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
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.FastMath;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public strictfp class CollisionSystem extends IteratingSystem
{
	public static final double PIXEL_STEP = 1;
	
	//Entity families
	Family surfaceFamily = Family.all(PositionComponent.class,
	                                  StageElementComponent.class).get();
	
	public CollisionSystem(int p)
	{
		super(Family.all(VelocityComponent.class, ECBComponent.class).get(), p);
	}
	
	public void processEntity(Entity entity, float delta)
	{
		CollisionMap minVectors = new CollisionMap();
		Map<StageStadium, CollisionStadium> beforeStads = new HashMap<>();
		Map<StageStadium, CollisionStadium> afterStads = new HashMap<>();
		
		//Move a box copy first
		for (StageStadium box : Components.ecbMapper.get(entity).getCollisionBoxes())
		{
			StageStadium ourBox = new StageStadium(box);
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
		
		//Position to move to
		final double finalX;
		final double finalY;
		//After all this, operate collision effects
		if (hit != null)
		{
			//Reflect off of the hit surface
			if (Components.velocityMapper.has(entity))
			{
				CollisionStadium finalStad = new CollisionStadium(
						hit.getBox().getAfterStadium(),
						hit.getVector().xComp(), hit.getVector().yComp()
				);
				ProjectionVector surfNorm = hit.getSurface().depth(finalStad, 1);
				double[] surfVel = hit.getSurface().instantVelocity(finalStad, 1);
				
				double diffX = Components.velocityMapper.get(entity).xVel - surfVel[0];
				double diffY = Components.velocityMapper.get(entity).yVel - surfVel[1];
				if (diffX * surfNorm.xNorm() + diffY * surfNorm.yNorm() <= 0)
				{
					getEngine().getSystem(PostprocessSystem.class).add(
							entity,
							PositionComponent.class,
							(PositionComponent comp) -> {
								comp.displace(hit.getVector().xComp(),
								              hit.getVector().yComp());
							}
					);
					finalX = Components.positionMapper.get(entity).x+hit.getVector().xComp();
					finalY = Components.positionMapper.get(entity).y+hit.getVector().yComp();
				}
				else
				{
					finalX = Components.positionMapper.get(entity).x;
					finalY = Components.positionMapper.get(entity).y;
				}
				if (diffX * surfNorm.xNorm() + diffY * surfNorm.yNorm() < 0)
				{
					final double elasticity;
					if (Components.elasticityMapper.has(entity))
					{
						if (surfNorm.yNorm() > FastMath.abs(surfNorm.xNorm()))
							elasticity = Components.elasticityMapper.get(entity).getGroundElasticity();
						else elasticity = Components.elasticityMapper.get(entity).getWallElasticity();
					}
					else
						elasticity = 0;
					//Get vector projection and rejection
					final double[] projection = Utility.projection(diffX, diffY, surfNorm.xNorm(), surfNorm.yNorm());
					
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
				finalX = Components.positionMapper.get(entity).x+hit.getVector().xComp();
				finalY = Components.positionMapper.get(entity).y+hit.getVector().yComp();
			}
		}
		else
		{
			finalX = Components.positionMapper.get(entity).x;
			finalY = Components.positionMapper.get(entity).y;
		}
		
		final double xVel = Components.velocityMapper.has(entity)?
		                    Components.velocityMapper.get(entity).xVel:0;
		final double yVel = Components.velocityMapper.has(entity)?
		                    Components.velocityMapper.get(entity).yVel:0;
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				ECBComponent.class,
				(comp) -> {
					for (StageStadium c : comp.getCollisionBoxes())
					{
						c.setBeforePos(finalX, finalY);
						c.setAfterPos(finalX+xVel, finalY+yVel);
					}
				}
		);
	}
	
	//TODO: Instead of "strongest wins", combine the different displacement
	//vectors intelligently
	public CollisionTriad ecbHit(double start, double end,
	                             Map<StageStadium, CollisionStadium> beforeBoxes,
	                             Map<StageStadium, CollisionStadium> afterBoxes,
	                             CollisionMap fores)
	{
		if (fores.isEmpty()) return null;
		
		//Split the given surfaces into two groups: those which are not worth timestep subdividing,
		//and those which are
		Predicate<Pair<StageStadium, StageElement>> splitHeuristic =
				(Pair<StageStadium, StageElement> m) ->
				{
					StageStadium c = m.getLeft();
					StageElement s = m.getRight();
					double spd = Utility.displacementDifference(s, beforeBoxes.get(c), afterBoxes.get(c), start, end);
					return spd >= PIXEL_STEP; //Add optimization heuristics when I finally get correct results
				};
		
		if (fores.keySet().stream().anyMatch(splitHeuristic))
		{
			//Split in half, and run
			double avg = (start+end)/2;
			Map<StageStadium, CollisionStadium> middleBoxes = LazyMap.lazyMap(
					new HashMap<>(),
					(StageStadium c) -> Utility.middleStad(beforeBoxes.get(c), afterBoxes.get(c))
			);
			CollisionTriad latestHit = ecbHit(start, avg, beforeBoxes, middleBoxes, fores);
			
			Map<StageStadium, CollisionStadium> postMiddleBoxes;
			Map<StageStadium, CollisionStadium> postAfterBoxes;
			if (latestHit != null)
			{
				double xDisp = latestHit.getVector().xComp();
				double yDisp = latestHit.getVector().yComp();
				postMiddleBoxes = LazyMap.lazyMap(
						new HashMap<>(),
						(StageStadium c) -> new CollisionStadium(middleBoxes.get(c), xDisp, yDisp)
				);
				postAfterBoxes = LazyMap.lazyMap(
						new HashMap<>(),
						(StageStadium c) -> new CollisionStadium(afterBoxes.get(c), xDisp, yDisp)
				);
			}
			else
			{
				postMiddleBoxes = middleBoxes;
				postAfterBoxes = afterBoxes;
			}
			
			//Populate a new collision map for the second half
			CollisionMap mids = new CollisionMap();
			for (Map.Entry<Pair<StageStadium, StageElement>, ProjectionVector> foreEntry : fores.entrySet())
			{
				StageStadium c = foreEntry.getKey().getLeft();
				StageElement s = foreEntry.getKey().getRight();
				mids.put(c, s, s.depth(postMiddleBoxes.get(c), avg));
			}
			CollisionTriad secondHalfHit = ecbHit(avg, end, postMiddleBoxes, postAfterBoxes, mids);
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
					latestHit.box = secondHalfHit.getBox();
					latestHit.surface = secondHalfHit.getSurface();
					if (xSum == 0 && ySum == 0)
					{
						latestHit.vector = new ProjectionVector(
								secondHalfHit.vector.xNorm(),
								secondHalfHit.vector.yNorm(),
								0
						);
					}
					else
					{
						double magSum = FastMath.hypot(xSum, ySum);
						latestHit.vector = new ProjectionVector(
								xSum/magSum,
								ySum/magSum,
								magSum
						);
					}
					
				}
			}
			return latestHit;
		}
		else
		{
			//Take the whole step at once
			CollisionMap afts = new CollisionMap();
			for (Pair<StageStadium, StageElement> foreKey : fores.keySet())
			{
				if (foreKey.getRight().collides(beforeBoxes.get(foreKey.getLeft()), start) ||
					foreKey.getRight().collides(afterBoxes.get(foreKey.getLeft()), end))
				{
					afts.put(foreKey, foreKey.getRight().depth(afterBoxes.get(foreKey.getLeft()), end));
				}
			}
			ProjectionVector combinedVectors = Utility.combineProjections(
					Utility.prunedProjections(afts.values())
			);
			if (combinedVectors != null)
			{
				double dotScore = Double.NEGATIVE_INFINITY;
				StageStadium highScoreBox = null;
				StageElement highScoreElement = null;
				for (Map.Entry<Pair<StageStadium, StageElement>, ProjectionVector> viewEntry : afts.entrySet())
				{
					double candidateDotScore = viewEntry.getValue().xComp() * combinedVectors.xNorm() +
					                           viewEntry.getValue().yComp() * combinedVectors.yNorm();
					if (candidateDotScore > dotScore)
					{
						dotScore = candidateDotScore;
						highScoreBox = viewEntry.getKey().getLeft();
						highScoreElement = viewEntry.getKey().getRight();
					}
				}
				if (dotScore > 0)
					return new CollisionTriad(highScoreBox, highScoreElement, combinedVectors);
				else
					return null;
			}
			else return null;
		}
	}
}
