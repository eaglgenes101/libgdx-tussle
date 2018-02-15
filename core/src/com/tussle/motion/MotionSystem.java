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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by eaglgenes101 on 5/25/17.
 */
public class MotionSystem extends IteratingSystem
{
	public static final double PIXEL_STEP = 1;
	
	public Family surfaceFamily = Family.all(StageElementComponent.class).get();
	
	public MotionSystem(int p)
	{
		super(Family.all(PositionComponent.class).get(), p);
	}

	public void processEntity(Entity entity, float delta)
	{
		final double xpos = Components.positionMapper.get(entity).x;
		final double ypos = Components.positionMapper.get(entity).y;
		if (Components.ecbMapper.has(entity))
		{
			CollisionMap minVectors = new CollisionMap();
			Map<StageElement<CollisionStadium>, CollisionStadium> beforeStads = new LinkedHashMap<>();
			Map<StageElement<CollisionStadium>, CollisionStadium> afterStads = new LinkedHashMap<>();
			Map<StageElement, CollisionShape> beforeElements = new LinkedHashMap<>();
			Map<StageElement, CollisionShape> afterElements = new LinkedHashMap<>();
			
			//Move a box copy first
			for (StageElement<CollisionStadium> box : Components.ecbMapper.get(entity).getCollisionBoxes())
			{
				beforeStads.put(box, (CollisionStadium)box.getBefore());
				afterStads.put(box, (CollisionStadium)box.getAfter());
				//First, populate the highest-level hash maps
				for (Entity ent : getEngine().getEntitiesFor(surfaceFamily))
					if (ent != entity)
					{
						for (StageElement se : Components.stageElementMapper.get(ent).getStageElements())
						{
							Rectangle seBounds = se.getBefore().getBounds().merge(se.getAfter().getBounds());
							Rectangle boxBounds = box.getBefore().getBounds().merge(box.getAfter().getBounds());
							if (seBounds.overlaps(boxBounds))
							{
								minVectors.put(box, se, se.getBefore().depth((CollisionStadium)box.getBefore()));
								beforeElements.put(se, se.getBefore());
								afterElements.put(se, se.getAfter());
							}
						}
					}
			}
			//Now find the hit stage element corresponding to the largest disp
			CollisionTriad hit = ecbHit(beforeStads, afterStads, beforeElements, afterElements, minVectors);
			
			//Position to move to
			final double dx;
			final double dy;
			//After all this, operate collision effects
			if (hit != null)
			{
				//Reflect off of the hit surface
				if (Components.velocityMapper.has(entity))
				{
					//The hit.mostRecent ProjectionVector describes the collision displacement
					//from the collective of surfaces near the end
					//Use it for reflection if it runs against our current velocity
					
					double[] stageVelocity = new double[]{hit.mostRecent.xNorm(), hit.mostRecent.yNorm()};
					double diffX = Components.velocityMapper.get(entity).xVel;
					double diffY = Components.velocityMapper.get(entity).yVel;
					if (stageVelocity[0]*diffX + stageVelocity[1]*diffY < 0)
					{
						dx = hit.cumulativeX;
						dy = hit.cumulativeY;
					}
					else
					{
						dx = 0; dy = 0;
					}
					if (diffX * stageVelocity[0] + diffY * stageVelocity[1] < 0)
					{
						final double elasticity;
						if (Components.elasticityMapper.has(entity))
						{
							if (stageVelocity[1] > FastMath.abs(stageVelocity[0]))
								elasticity = Components.elasticityMapper.get(entity).getGroundElasticity();
							else elasticity = Components.elasticityMapper.get(entity).getWallElasticity();
						}
						else
							elasticity = 0;
						//Get vector projection and rejection
						final double[] projection = Utility.projection(diffX, diffY, stageVelocity[0], stageVelocity[1]);
						
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
					dx = hit.cumulativeX;
					dy = hit.cumulativeY;
				}
			}
			else
			{
				dx = 0;
				dy = 0;
			}
			
			final double xVel = Components.velocityMapper.has(entity)?
			                    Components.velocityMapper.get(entity).xVel:0;
			final double yVel = Components.velocityMapper.has(entity)?
			                    Components.velocityMapper.get(entity).yVel:0;
			getEngine().getSystem(PostprocessSystem.class).add(
					entity, ECBComponent.class,
					cl -> {
						for (StageElement se : cl.getCollisionBoxes())
						{
							se.step(dx, dy, xpos, ypos, xVel, yVel, 0, 1, false);
						}
					}
			);
			if (Components.velocityMapper.has(entity))
			{
				getEngine().getSystem(PostprocessSystem.class).add(
						entity, PositionComponent.class,
						cl -> cl.setPosition(xpos+dx+xVel, ypos+dy+yVel)
				);
			}
			if (Components.stageElementMapper.has(entity))
			{
				getEngine().getSystem(PostprocessSystem.class).add(
						entity, StageElementComponent.class,
						cl -> {
							for (StageElement se: cl.getStageElements())
							{
								se.step(dx, dy, xpos, ypos, xVel, yVel, 0, 1, false);
							}
						}
				);
			}
		}
		else
		{
			final double xVel = Components.velocityMapper.has(entity) ?
			                    Components.velocityMapper.get(entity).xVel : 0;
			final double yVel = Components.velocityMapper.has(entity) ?
			                    Components.velocityMapper.get(entity).yVel : 0;
			getEngine().getSystem(PostprocessSystem.class).add(
					entity, PositionComponent.class,
					cl -> cl.setPosition(xpos+xVel, ypos+yVel)
			);
			if (Components.stageElementMapper.has(entity))
			{
				getEngine().getSystem(PostprocessSystem.class).add(
						entity, StageElementComponent.class,
						cl -> {
							for (StageElement se: cl.getStageElements())
							{
								se.step(0, 0, xpos, ypos, xVel, yVel, 0, 1, false);
							}
						}
				);
			}
		}
	}
	
	
	public CollisionTriad ecbHit(Map<StageElement<CollisionStadium>, CollisionStadium> beforeBoxes,
	                             Map<StageElement<CollisionStadium>, CollisionStadium> afterBoxes,
	                             Map<StageElement, CollisionShape> beforeSurfaces,
	                             Map<StageElement, CollisionShape> afterSurfaces,
	                             CollisionMap fores)
	{
		if (fores.isEmpty()) return null;
		
		//Split the given surfaces into two groups: those which are not worth timestep subdividing,
		//and those which are
		Predicate<Pair<StageElement<CollisionStadium>, StageElement>> splitHeuristic =
				(Pair<StageElement<CollisionStadium>, StageElement> m) ->
				{
					StageElement<CollisionStadium> c = m.getLeft();
					StageElement s = m.getRight();
					double[] velocity = Utility.displacementDiff(beforeSurfaces.get(s), afterSurfaces.get(s),
					                                             beforeBoxes.get(c), afterBoxes.get(c));
					return FastMath.hypot(velocity[0], velocity[1]) >= PIXEL_STEP;
				};
		
		if (fores.keySet().stream().anyMatch(splitHeuristic))
		{
			//Split in half, and run
			Map<StageElement<CollisionStadium>, CollisionStadium> middleBoxes = LazyMap.lazyMap(
					new HashMap<>(),
					(StageElement<CollisionStadium> c) -> beforeBoxes.get(c).interpolate(afterBoxes.get(c))
			);
			Map<StageElement, CollisionShape> middleSurfaces = LazyMap.lazyMap(
					new HashMap<>(),
					(StageElement s) -> beforeSurfaces.get(s).interpolate(afterSurfaces.get(s))
			);
			CollisionTriad latestHit = ecbHit(beforeBoxes, middleBoxes, beforeSurfaces, middleSurfaces, fores);
			
			Map<StageElement<CollisionStadium>, CollisionStadium> postMiddleBoxes;
			Map<StageElement<CollisionStadium>, CollisionStadium> postAfterBoxes;
			Map<StageElement, CollisionShape> postMiddleSurfaces;
			Map<StageElement, CollisionShape> postAfterSurfaces;
			if (latestHit != null)
			{
				double xDisp = latestHit.cumulativeX;
				double yDisp = latestHit.cumulativeY;
				postMiddleBoxes = LazyMap.lazyMap(
						new HashMap<>(),
						(StageElement<CollisionStadium> c) -> middleBoxes.get(c).displacementBy(xDisp, yDisp));
				postAfterBoxes = LazyMap.lazyMap(new HashMap<>(),
						(StageElement<CollisionStadium> c) -> afterBoxes.get(c).displacementBy(xDisp, yDisp));
				postMiddleSurfaces = LazyMap.lazyMap(new HashMap<>(),
						(StageElement s) -> middleSurfaces.get(s).displacementBy(xDisp, yDisp));
				postAfterSurfaces = LazyMap.lazyMap(new HashMap<>(),
						(StageElement s) -> afterSurfaces.get(s).displacementBy(xDisp, yDisp));
			}
			else
			{
				postMiddleBoxes = middleBoxes;
				postAfterBoxes = afterBoxes;
				postMiddleSurfaces = middleSurfaces;
				postAfterSurfaces = afterSurfaces;
			}
			
			//Populate a new collision map for the second half
			CollisionMap mids = new CollisionMap();
			for (Map.Entry<Pair<StageElement<CollisionStadium>, StageElement>, ProjectionVector> foreEntry : fores.entrySet())
			{
				StageElement<CollisionStadium> c = foreEntry.getKey().getLeft();
				StageElement s = foreEntry.getKey().getRight();
				mids.put(c, s, s.getBefore().interpolate(s.getAfter()).depth(postMiddleBoxes.get(c)));
			}
			CollisionTriad secondHalfHit = ecbHit(postMiddleBoxes, postAfterBoxes,
			                                      postMiddleSurfaces, postAfterSurfaces, mids);
			if (secondHalfHit != null)
				latestHit = latestHit == null ? secondHalfHit : new CollisionTriad(latestHit, secondHalfHit);
			return latestHit;
		}
		else
		{
			//Take the whole step at once
			CollisionMap afts = new CollisionMap();
			for (Pair<StageElement<CollisionStadium>, StageElement> foreKey : fores.keySet())
			{
				if (foreKey.getRight().getBefore().collidesWith(beforeBoxes.get(foreKey.getLeft())) ||
				    foreKey.getRight().getAfter().collidesWith(afterBoxes.get(foreKey.getLeft())))
				{
					afts.put(foreKey, foreKey.getRight().getAfter().depth(afterBoxes.get(foreKey.getLeft())));
				}
			}
			ProjectionVector combinedVectors = Utility.combineProjections(
					Utility.prunedProjections(afts.values())
			);
			return combinedVectors == null ? null : new CollisionTriad(combinedVectors);
		}
	}
}
