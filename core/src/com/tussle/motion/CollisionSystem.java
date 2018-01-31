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
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public strictfp class CollisionSystem extends IteratingSystem
{
	public static final double COLLISION_PIXEL_STEP = 8;
	
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
		CollisionMap minVectors = new CollisionMap();
		Map<CollisionBox, Stadium> beforeStads = new HashMap<>();
		Map<CollisionBox, Stadium> afterStads = new HashMap<>();
		
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
							minVectors.put(box, se, se.depth(box.getStadiumAt(0), 0));
						}
					}
				}
		}
		//Now find the hit stage element corresponding to the largest disp
		CollisionTriad hit = ecbHit(0, 1, beforeStads, afterStads, minVectors);
		
		//After all this, operate collision effects
		if (hit != null)
		{
			//Reflect off of the hit surface
			if (Components.velocityMapper.has(entity))
			{
				Stadium finalStad = new Stadium(hit.getBox().getCurrentStadium());
				finalStad.displace(hit.getVector().xComp(),
				                    hit.getVector().yComp());
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
								comp.displace(hit.getVector().xComp(),
								              hit.getVector().yComp());
							}
					);
				}
				if (diffX * surfNorm.xnorm + diffY * surfNorm.ynorm < 0)
				{
					final double elasticity;
					if (Components.elasticityMapper.has(entity))
					{
						if (surfNorm.ynorm > FastMath.abs(surfNorm.xnorm))
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
							comp.displace(hit.getVector().xComp(),
							              hit.getVector().yComp());
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
	                             CollisionMap fores)
	{
		if (fores.isEmpty()) return null;
		
		//Compute afts
		CollisionMap afts = new CollisionMap();
		for (Map.Entry<Pair<CollisionBox, StageElement>, ProjectionVector> foreEntry : fores.entrySet())
		{
			CollisionBox c = foreEntry.getKey().getLeft();
			StageElement s = foreEntry.getKey().getRight();
			afts.put(c, s, s.depth(afterBoxes.get(c), end));
		}
		
		//Split the given surfaces into two groups: those which are not worth timestep subdividing,
		//and those which are
		Predicate<Pair<CollisionBox, StageElement>> splitHeuristic =
				(Pair<CollisionBox, StageElement> m) ->
		{
			CollisionBox c = m.getLeft();
			StageElement s = m.getRight();
			double spd = Utility.speedDifference(s, beforeBoxes.get(c), afterBoxes.get(c), start, end);
			return !Utility.projectionsClose(fores.get(c, s), afts.get(c, s)) &&
			       spd*(end-start) >= COLLISION_PIXEL_STEP;
		};
		
		//Populate firstHalves, for suitable entries
		//TODO: Figure out how to use a predicated map view instead of copying entries
		CollisionMap firstHalves = new CollisionMap();
		CollisionMap wholeAfts = new CollisionMap();
		for (Map.Entry<Pair<CollisionBox, StageElement>, ProjectionVector> foreEntry : fores.entrySet())
		{
			if (splitHeuristic.evaluate(foreEntry.getKey()))
				firstHalves.put(foreEntry.getKey(), foreEntry.getValue());
			else
				wholeAfts.put(foreEntry.getKey(), afts.get(foreEntry.getKey()));
		}
		
		//Set these right up
		CollisionTriad latestHit = null;
		
		//First, run everything over the firstHalves
		if (!firstHalves.isEmpty())
		{
			double avg = (start + end) / 2;
			Map<CollisionBox, Stadium> middleBoxes = LazyMap.lazyMap(
					new HashMap<>(),
					(CollisionBox c) -> Utility.middleStad(beforeBoxes.get(c), afterBoxes.get(c))
			);
			CollisionTriad firstHit = ecbHit(start, avg, beforeBoxes, middleBoxes, firstHalves);
			Map<CollisionBox, Stadium> postMiddleBoxes;
			Map<CollisionBox, Stadium> postAfterBoxes;
			if (firstHit != null)
			{
				latestHit = firstHit;
				double xDisp = firstHit.getVector().xComp();
				double yDisp = firstHit.getVector().yComp();
				postMiddleBoxes = LazyMap.lazyMap(
						new HashMap<>(),
						(CollisionBox c) -> middleBoxes.get(c).displace(xDisp, yDisp)
				);
				postAfterBoxes = LazyMap.lazyMap(
						new HashMap<>(),
						(CollisionBox c) -> new Stadium(afterBoxes.get(c)).displace(xDisp, yDisp)
				);
			}
			else
			{
				postMiddleBoxes = middleBoxes;
				postAfterBoxes = afterBoxes;
			}
			
			//Populate a new collision map for the second half
			CollisionMap secondHalves = new CollisionMap();
			for (Map.Entry<Pair<CollisionBox, StageElement>, ProjectionVector> halfEntry : firstHalves.entrySet())
			{
				CollisionBox c = halfEntry.getKey().getLeft();
				StageElement s = halfEntry.getKey().getRight();
				secondHalves.put(c, s, s.depth(postAfterBoxes.get(c), end));
			}
			CollisionTriad secondHalfHit = ecbHit(avg, end, postMiddleBoxes, postAfterBoxes, secondHalves);
			if (secondHalfHit != null)
			{
				if (latestHit == null)
				{
					latestHit = secondHalfHit;
				}
				else
				{
					double xSum = latestHit.vector.xComp() + secondHalfHit.vector.xComp();
					double ySum = latestHit.vector.yComp() + secondHalfHit.vector.yComp();
					latestHit = secondHalfHit;
					if (xSum == 0 && ySum == 0)
					{
						latestHit.vector.magnitude = 0;
					}
					else
					{
						double magSum = FastMath.hypot(xSum, ySum);
						latestHit.vector.xnorm = xSum/magSum;
						latestHit.vector.ynorm = ySum/magSum;
						latestHit.vector.magnitude = magSum;
					}
					
				}
			}
		}
		
		//Then join back with the wholeSteps to get the second half entries
		for (Map.Entry<Pair<CollisionBox, StageElement>, ProjectionVector> wholeEntry : wholeAfts.entrySet())
		{
			CollisionBox c = wholeEntry.getKey().getLeft();
			StageElement s = wholeEntry.getKey().getRight();
			if (s.collides(afterBoxes.get(c), end))
			{
				if (latestHit == null)
				{
					latestHit = new CollisionTriad(c, s, wholeEntry.getValue());
				}
				else if (latestHit.getVector().magnitude < wholeEntry.getValue().magnitude)
				{
					latestHit.box = c;
					latestHit.surface = s;
					latestHit.vector = wholeEntry.getValue();
				}
			}
		}
		
		//Combine our three subresults to get our final result
		return latestHit;
	}
}
