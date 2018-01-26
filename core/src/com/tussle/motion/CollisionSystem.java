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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public strictfp class CollisionSystem extends IteratingSystem
{
	public static final double COLLISION_TOLERANCE = .000000001;
	
	//Entity families
	Family surfaceFamily = Family.all(PositionComponent.class,
	                                  StageElementComponent.class).get();
	
	public CollisionSystem(int p)
	{
		super(Family.all(VelocityComponent.class, ECBComponent.class).get(), p);
	}
	
	public void processEntity(Entity entity, float delta)
	{
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
				LazyMap.lazyMap(new LinkedHashMap<>(), () -> (new LinkedHashMap<>()));
		Map<CollisionBox, Map<StageElement, ProjectionVector>> maxVectors =
				LazyMap.lazyMap(new LinkedHashMap<>(), () -> (new LinkedHashMap<>()));
		Map<CollisionBox, Stadium> beforeStads = new LinkedHashMap<>();
		Map<CollisionBox, Stadium> afterStads = new LinkedHashMap<>();
		
		//Move a box copy first
		for (CollisionBox box : Components.ecbMapper.get(entity).getCollisionBoxes())
		{
			CollisionBox ourBox = new CollisionBox(box);
			ourBox.setPosition(Components.positionMapper.get(entity).x,
			                   Components.positionMapper.get(entity).y);
			beforeStads.put(box, ourBox.getStadiumAt(0));
			afterStads.put(box, ourBox.getStadiumAt(1));
			//First, populate the highest-level hash maps
			for (Entity ent : getEngine().getEntitiesFor(surfaceFamily))
				if (ent != entity)
				{
					for (StageElement se : Components.stageElementMapper.get(ent).getStageElements())
					{
						if (se.getBounds(0, 1).overlaps(ourBox.getBounds(0, 1)))
						{
							minVectors.get(box).put(se, se.depth(ourBox.getStadiumAt(0), 0));
							maxVectors.get(box).put(se, se.depth(ourBox.getStadiumAt(1), 1));
						}
					}
				}
		}
		//Now find the hit stage element corresponding to the largest disp
		CollisionTriad hit = ecbHit(0, 1, beforeStads, afterStads,
		                            minVectors, maxVectors);
		
		//After all this, operate collision effects
		if (hit != null)
		{
			//Reflect off of the hit surface
			if (Components.velocityMapper.has(entity))
			{
				Stadium finalStad = new Stadium(hit.getBox().getCurrentStadium());
				finalStad.displace(hit.getVector().xnorm * hit.getVector().magnitude,
				                    hit.getVector().ynorm * hit.getVector().magnitude);
				ProjectionVector surfNorm = hit.getSurface().depth(finalStad, 1);
				double[] surfVel = hit.getSurface().instantVelocity(finalStad, 1);
				
				double diffX = Components.velocityMapper.get(entity).xVel - surfVel[0];
				double diffY = Components.velocityMapper.get(entity).yVel - surfVel[1];
				if (diffX * surfNorm.xnorm + diffY * surfNorm.ynorm <= 0)
				{
					getEngine().getSystem(PostprocessSystem.class).add(
							entity,
							PositionComponent.class,
							(PositionComponent comp) -> {
								comp.displace(hit.getVector().xnorm * hit.getVector().magnitude,
								              hit.getVector().ynorm * hit.getVector().magnitude);
							}
					);
				}
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
					final double[] projection = Utility.projection(diffX, diffY, surfNorm.xnorm, surfNorm.ynorm);
					
					getEngine().getSystem(PostprocessSystem.class).add(
							entity,
							VelocityComponent.class,
							(comp) -> {
								comp.accelerate((-1-elasticity)*projection[0],
								                (-1-elasticity)*projection[1]);
							}
					);
				}
			}
			else
			{
				getEngine().getSystem(PostprocessSystem.class).add(
						entity,
						PositionComponent.class,
						(PositionComponent comp) -> {
							comp.displace(hit.getVector().xnorm * hit.getVector().magnitude,
							              hit.getVector().ynorm * hit.getVector().magnitude);
						}
				);
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
	
	public CollisionTriad ecbHit(double start, double end,
	                             Map<CollisionBox, Stadium> beforeBoxes,
	                             Map<CollisionBox, Stadium> afterBoxes,
	                             Map<CollisionBox, Map<StageElement, ProjectionVector>> fores,
	                             Map<CollisionBox, Map<StageElement, ProjectionVector>> afts)
	{
		//TODO: Optimize aggressively while keeping reasonable numerical accuracy
		//TODO: Also, double check
		//Eject early if the time interval is too small
		/*
		if (end - start < COLLISION_TOLERANCE)
		{
			//System.out.print("a");
			System.out.println("Out!");
			return null;
		}
		*/
		if (fores.isEmpty() || afts.isEmpty())
		{
			//System.out.print("b");
			return null;
		}
		//If stage surface normal change too little, we can take them out of consideration
		MultiValuedMap<CollisionBox, StageElement> earlySurfaces = new HashSetValuedHashMap<>();
		for (Map.Entry<CollisionBox, Stadium> boxEnt : beforeBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> foreEnt : fores.get(boxEnt.getKey()).entrySet())
			{
				if (foreEnt.getKey().collides(boxEnt.getValue(), start))
					earlySurfaces.put(boxEnt.getKey(), foreEnt.getKey());
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
						tentativeDisp = foreVec;
					}
				}
			}
			if (returnElement != null)
			{
				//System.out.print("c");
				return new CollisionTriad(tentativeBox, returnElement, tentativeDisp);
			}
		}
		MultiValuedMap<CollisionBox, StageElement> acceptedSurfaces = new HashSetValuedHashMap<>();
		for (Map.Entry<CollisionBox, Stadium> boxEnt : afterBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> aftEnt : afts.get(boxEnt.getKey()).entrySet())
			{
				if (aftEnt.getKey().collides(boxEnt.getValue(), end))
					acceptedSurfaces.put(boxEnt.getKey(), aftEnt.getKey());
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
						tentativeDisp = aftVec;
					}
				}
			}
			if (returnElement != null)
			{
				//System.out.print("d");
				return new CollisionTriad(tentativeBox, returnElement, tentativeDisp);
			}
		}
		//First move
		Map<CollisionBox, Map<StageElement, ProjectionVector>> firstFores =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		Map<CollisionBox, Map<StageElement, ProjectionVector>> firstAfts =
				LazyMap.lazyMap(new HashMap<>(), () -> (new HashMap<>()));
		
		double avg = (start + end) / 2;
		Map<CollisionBox, Stadium> middleBoxes = LazyMap.lazyMap(
				new HashMap<>(),
				(CollisionBox c) -> Utility.middleStad(beforeBoxes.get(c), afterBoxes.get(c))
		);
		
		for (Map.Entry<CollisionBox, Stadium> boxEnt : beforeBoxes.entrySet())
		{
			for (Map.Entry<StageElement, ProjectionVector> foreEnt : fores.get(boxEnt.getKey()).entrySet())
			{
				// If the step is lower than 8 pixels or the start and end projections are close in direction,
				// it is unlikely that even finer stepping will yield significant results
				double spd = Utility.speedDifference(foreEnt.getKey(), boxEnt.getValue(),
				                                     afterBoxes.get(boxEnt.getKey()), start, end);
				
				//Pruning heuristics
				//If any of these conditions is true, then subdivision of the path through these surfaces
				//is unlikely to be worth the extra steps
				if (spd*(end-start) < 8) continue;
				if (Utility.projectionsClose(foreEnt.getValue(),
				        afts.get(boxEnt.getKey()).get(foreEnt.getKey()))) continue;
				
				firstFores.get(boxEnt.getKey()).put(foreEnt.getKey(),
				                                    fores.get(boxEnt.getKey()).get(foreEnt.getKey()));
				firstAfts.get(boxEnt.getKey()).put(foreEnt.getKey(),
				                                   foreEnt.getKey().depth(middleBoxes.get(boxEnt.getKey()), avg));
			}
		}
		if (firstFores.isEmpty())
		{
			//System.out.print("e");
			return null;
		}
		
		CollisionTriad firstHit = ecbHit(start, avg, beforeBoxes, middleBoxes,
		                                 firstFores, firstAfts);
		
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
		if (firstHit != null)
		{
			for (Map.Entry<CollisionBox, Stadium> entry : middleBoxes.entrySet())
			{
				entry.getValue().displace(firstHit.getVector().xnorm * firstHit.getVector().magnitude,
				                          firstHit.getVector().ynorm * firstHit.getVector().magnitude);
				endingBoxes.get(entry.getKey()).displace(firstHit.getVector().xnorm * firstHit.getVector().magnitude,
				                                         firstHit.getVector().ynorm * firstHit.getVector().magnitude);
			}
		}
		for (Map.Entry<CollisionBox, Stadium> entry : middleBoxes.entrySet())
		{
			for (StageElement se : firstFores.get(entry.getKey()).keySet())
			{
				secondFores.get(entry.getKey()).put(se, se.depth(entry.getValue(), avg));
				secondAfts.get(entry.getKey()).put(se, se.depth(endingBoxes.get(entry.getKey()), avg));
			}
		}
		CollisionTriad secondHit = ecbHit(avg, end, middleBoxes, endingBoxes, secondFores, secondAfts);
		//Now interpret the two halves
		if (firstHit == null && secondHit == null) return null;
		else if (firstHit == null)
		{
			return secondHit;
		}
		else if (secondHit == null)
		{
			return firstHit;
		}
		else
		{
			double xSum = firstHit.getVector().xnorm * firstHit.getVector().magnitude +
			              secondHit.getVector().xnorm * secondHit.getVector().magnitude;
			double ySum = firstHit.getVector().ynorm * firstHit.getVector().magnitude +
			              secondHit.getVector().ynorm * secondHit.getVector().magnitude;
			double lSum = StrictMath.hypot(xSum, ySum);
			ProjectionVector disp;
			if (lSum == 0)
			{
				disp = new ProjectionVector(secondHit.getVector().xnorm, secondHit.getVector().ynorm, 0);
			}
			else
			{
				disp = new ProjectionVector(xSum/lSum, ySum/lSum, lSum);
			}
			return new CollisionTriad(secondHit.getBox(), secondHit.getSurface(), disp);
		}
	}
}
