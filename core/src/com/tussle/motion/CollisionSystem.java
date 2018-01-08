/*
 * Copyright (c) 2017 eaglgenes101
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tussle.motion;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tussle.collision.*;
import com.tussle.main.Components;
import com.tussle.main.Utility;
import com.tussle.postprocess.PostprocessSystem;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public strictfp class CollisionSystem extends IteratingSystem
{
	public static final double COLLISION_TOLERANCE = .00001;
	
	//Entity families
	Family surfaceFamily = Family.all(PositionComponent.class,
	                                  StageElementComponent.class).get();
	
	public CollisionSystem(int p)
	{
		super(Family.all(VelocityComponent.class, ECBComponent.class).get(), p);
	}
	
	public void processEntity(Entity entity, float delta)
	{
		//TODO: Filter out collision boxes that obviously won't hit
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				ECBComponent.class,
				(ECBComponent comp) -> {
					for (CollisionBox c : comp.getCollisionBoxes())
						c.setPosition(Components.positionMapper.get(entity).x,
						              Components.positionMapper.get(entity).y);
				}
		);
		Map<CollisionBox, Map<StageElement, ProjectionVector>> minVectors =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		Map<CollisionBox, Map<StageElement, ProjectionVector>> maxVectors =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		Map<CollisionBox, Stadium> beforeStads = new HashMap<>();
		Map<CollisionBox, Stadium> afterStads = new HashMap<>();
		
		//Move a box copy first
		for (CollisionBox box : Components.ecbMapper.get(entity).getCollisionBoxes())
		{
			CollisionBox ourBox = new CollisionBox(box);
			ourBox.setPosition(Components.positionMapper.get(entity).x,
			                   Components.positionMapper.get(entity).y);
			beforeStads.put(box, box.getStadiumAt(0));
			afterStads.put(box, box.getStadiumAt(1));
			//First, populate the highest-level hash maps
			for (Entity ent : getEngine().getEntitiesFor(surfaceFamily))
				if (ent != entity)
					for (StageElement se : Components.stageElementMapper.get(ent).getStageElements())
					{
						minVectors.get(box).put(se, se.depth(ourBox.getStadiumAt(0), 0));
						maxVectors.get(box).put(se, se.depth(ourBox.getStadiumAt(1), 1));
					}
		}
		//Now find the hit stage element corresponding to the largest disp
		ProjectionVector disp = new ProjectionVector(0, 0, 0);
		CollisionPair hit = ecbHit(0, 1, beforeStads, afterStads,
		                           minVectors, maxVectors, disp);
		//After all this, operate collision effects
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				PositionComponent.class,
				(PositionComponent comp) -> comp.displace(disp.xnorm * disp.magnitude,
				                                          disp.ynorm * disp.magnitude)
		);
		if (hit != null)
		{
			Stadium finalStad = new Stadium(hit.getBox().getCurrentStadium());
			finalStad.displace(Components.positionMapper.get(entity).x + disp.xnorm * disp.magnitude,
			                   Components.positionMapper.get(entity).y + disp.ynorm * disp.magnitude);
			//Reflect off of the hit surface
			if (Components.velocityMapper.has(entity))
			{
				ProjectionVector surfNorm = hit.getSurface().depth(finalStad, 1);
				ProjectionVector surfVel = hit.getSurface().instantVelocity(finalStad, 1);
				double diffX = Components.velocityMapper.get(entity).xVel - surfVel.xnorm * surfVel.magnitude;
				double diffY = Components.velocityMapper.get(entity).yVel - surfVel.ynorm * surfVel.magnitude;
				if (diffX * surfNorm.xnorm + diffY * surfNorm.ynorm < 0)
				{
					final double elasticity;
					if (Components.elasticityMapper.has(entity))
					{
						if (surfNorm.ynorm > StrictMath.abs(surfNorm.xnorm))
							elasticity = Components.elasticityMapper.get(entity).getGroundElasticity();
						else elasticity = Components.elasticityMapper.get(entity).getWallElasticity();
					}
					else
						elasticity = 0;
					//Get vector projection and rejection
					double[] projection = Utility.projection(diffX, diffY, surfNorm.xnorm, surfNorm.ynorm);
					
					getEngine().getSystem(PostprocessSystem.class).add(
							entity,
							VelocityComponent.class,
							(comp) -> {
								comp.xVel += (-1 - elasticity) * projection[0];
								comp.yVel += (-1 - elasticity) * projection[1];
							}
					);
				}
			}
		}
		
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				ECBComponent.class,
				(comp) -> {
					for (CollisionBox c : comp.getCollisionBoxes())
					{
						c.setPosition(Components.positionMapper.get(entity).x,
						              Components.positionMapper.get(entity).y);
						c.setAreas();
					}
				}
		);
	}
	
	public CollisionPair ecbHit(double start, double end,
	                            Map<CollisionBox, Stadium> beforeBoxes,
	                            Map<CollisionBox, Stadium> afterBoxes,
	                            Map<CollisionBox, Map<StageElement, ProjectionVector>> fores,
	                            Map<CollisionBox, Map<StageElement, ProjectionVector>> afts,
	                            ProjectionVector disp)
	{
		//Eject early if the time interval is too small
		if (end - start < COLLISION_TOLERANCE) return null;
		if (fores.isEmpty() || afts.isEmpty()) return null;
		//If stage surface normal change too little, we can take them out of consideration
		MultiValuedMap<CollisionBox, StageElement> earlySurfaces = new HashSetValuedHashMap<>();
		for (Map.Entry<CollisionBox, Stadium> boxEnt : beforeBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> aftEnt : afts.get(boxEnt.getKey()).entrySet())
			{
				if (Utility.velocityDifference(aftEnt.getKey(), boxEnt.getValue(),
				                               afterBoxes.get(boxEnt.getKey()),
				                               start, end).magnitude > 0)
				{
					if (aftEnt.getKey().collides(boxEnt.getValue(), start))
						earlySurfaces.put(boxEnt.getKey(), aftEnt.getKey());
				}
			}
		}
		if (!earlySurfaces.isEmpty())
		{
			ProjectionVector tentativeDisp = new ProjectionVector(0, 0, 0);
			CollisionBox tentativeBox = null;
			StageElement returnElement = null;
			for (CollisionBox box : beforeBoxes.keySet())
			{
				for (StageElement se : earlySurfaces.get(box))
				{
					ProjectionVector foreVec = fores.get(box).get(se);
					if (foreVec.magnitude > tentativeDisp.magnitude)
					{
						returnElement = se;
						tentativeBox = box;
						tentativeDisp.xnorm = foreVec.xnorm;
						tentativeDisp.ynorm = foreVec.ynorm;
						tentativeDisp.magnitude = foreVec.magnitude;
					}
				}
			}
			if (returnElement != null)
			{
				disp.xnorm = tentativeDisp.xnorm;
				disp.ynorm = tentativeDisp.ynorm;
				disp.magnitude = tentativeDisp.magnitude;
				return new CollisionPair(tentativeBox, returnElement);
			}
		}
		MultiValuedMap<CollisionBox, StageElement> acceptedSurfaces = new HashSetValuedHashMap<>();
		for (Map.Entry<CollisionBox, Stadium> boxEnt : afterBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> aftEnt : afts.get(boxEnt.getKey()).entrySet())
			{
				if (Utility.velocityDifference(aftEnt.getKey(), beforeBoxes.get(boxEnt.getKey()),
				                               boxEnt.getValue(),
				                               start, end).magnitude > 0)
				{
					if (aftEnt.getKey().collides(boxEnt.getValue(), end))
						acceptedSurfaces.put(boxEnt.getKey(), aftEnt.getKey());
				}
			}
		}
		if (!acceptedSurfaces.isEmpty())
		{
			ProjectionVector tentativeDisp = new ProjectionVector(0, 0, 0);
			CollisionBox tentativeBox = null;
			StageElement returnElement = null;
			for (CollisionBox box : beforeBoxes.keySet())
			{
				for (StageElement se : acceptedSurfaces.get(box))
				{
					ProjectionVector aftVec = afts.get(box).get(se);
					if (aftVec.magnitude > tentativeDisp.magnitude)
					{
						returnElement = se;
						tentativeBox = box;
						tentativeDisp.xnorm = aftVec.xnorm;
						tentativeDisp.ynorm = aftVec.ynorm;
						tentativeDisp.magnitude = aftVec.magnitude;
					}
				}
			}
			if (returnElement != null)
			{
				disp.xnorm = tentativeDisp.xnorm;
				disp.ynorm = tentativeDisp.ynorm;
				disp.magnitude = tentativeDisp.magnitude;
				return new CollisionPair(tentativeBox, returnElement);
			}
		}
		MultiValuedMap<CollisionBox, StageElement> rejectedSurfaces = new HashSetValuedHashMap<>();
		for (Map.Entry<CollisionBox, Stadium> boxEnt : beforeBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> aftEnt : afts.get(boxEnt.getKey()).entrySet())
			{
				if (Utility.velocityDifference(aftEnt.getKey(), boxEnt.getValue(),
				                               afterBoxes.get(boxEnt.getKey()),
				                               start, end).magnitude > 0)
				{
					if (Utility.projectionsClose(fores.get(boxEnt.getKey()).get(aftEnt.getKey()),
					                             aftEnt.getValue()))
						rejectedSurfaces.put(boxEnt.getKey(), aftEnt.getKey());
				}
			}
		}
		if (rejectedSurfaces.isEmpty()) return null;
		//Else, iterate deeper
		double avg = (start + end) / 2;
		Map<CollisionBox, Stadium> middleBoxes = new HashMap<>();
		for (Map.Entry<CollisionBox, Stadium> ents : beforeBoxes.entrySet())
		{
			middleBoxes.put(ents.getKey(), Utility.middleStad(ents.getValue(),
			                                                  afterBoxes.get(ents.getKey())));
		}
		//First move
		Map<CollisionBox, Map<StageElement, ProjectionVector>> firstFores =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		Map<CollisionBox, Map<StageElement, ProjectionVector>> firstAfts =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		for (CollisionBox c : beforeBoxes.keySet())
		{
			for (StageElement se : rejectedSurfaces.get(c))
			{
				firstFores.get(c).put(se, fores.get(c).get(se));
				firstAfts.get(c).put(se, se.depth(middleBoxes.get(c), avg));
			}
		}
		ProjectionVector disp1 = new ProjectionVector(0, 0, 0);
		CollisionPair firstHit = ecbHit(start, avg, beforeBoxes, middleBoxes,
		                                firstFores, firstAfts, disp1);
		//Now do the second move
		Map<CollisionBox, Stadium> endingBoxes = new HashMap<>();
		for (Map.Entry<CollisionBox, Stadium> entry : afterBoxes.entrySet())
		{
			endingBoxes.put(entry.getKey(), new Stadium(entry.getValue()));
		}
		Map<CollisionBox, Map<StageElement, ProjectionVector>> secondFores =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		Map<CollisionBox, Map<StageElement, ProjectionVector>> secondAfts =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		if (disp1.magnitude != 0)
		{
			for (Map.Entry<CollisionBox, Stadium> entry : middleBoxes.entrySet())
			{
				entry.getValue().displace(disp1.xnorm * disp1.magnitude, disp1.ynorm * disp1.magnitude);
				endingBoxes.get(entry.getKey()).displace(disp1.xnorm * disp1.magnitude,
				                                         disp1.ynorm * disp1.magnitude);
			}
		}
		for (Map.Entry<CollisionBox, Stadium> entry : middleBoxes.entrySet())
		{
			for (StageElement se : rejectedSurfaces.get(entry.getKey()))
			{
				secondFores.get(entry.getKey()).put(se, se.depth(entry.getValue(), avg));
				secondAfts.get(entry.getKey()).put(se, se.depth(endingBoxes.get(entry.getKey()), avg));
			}
		}
		ProjectionVector disp2 = new ProjectionVector(0, 0, 0);
		CollisionPair secondHit = ecbHit(avg, end, middleBoxes, endingBoxes, secondFores, secondAfts, disp2);
		//Now interpret the two halves
		double xSum = disp1.xnorm * disp1.magnitude + disp2.xnorm * disp2.magnitude;
		double ySum = disp1.ynorm * disp1.magnitude + disp2.ynorm * disp2.magnitude;
		double lSum = StrictMath.hypot(xSum, ySum);
		if (lSum != 0)
		{
			disp.xnorm = xSum / lSum;
			disp.ynorm = ySum / lSum;
			disp.magnitude = lSum;
		}
		return (secondHit == null) ? firstHit : secondHit;
	}
}
