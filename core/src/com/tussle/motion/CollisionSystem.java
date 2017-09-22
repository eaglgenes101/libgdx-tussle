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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tussle.collision.*;
import com.tussle.main.Utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public strictfp class CollisionSystem extends IteratingSystem
{
	//Entity families
	Family surfaceFamily = Family.all(PositionComponent.class,
			StageElementComponent.class).get();

	//Component mappers
	ComponentMapper<PositionComponent> positionMapper =
			ComponentMapper.getFor(PositionComponent.class);
	ComponentMapper<VelocityComponent> velocityMapper =
			ComponentMapper.getFor(VelocityComponent.class);
	ComponentMapper<ECBComponent> ecbMapper =
			ComponentMapper.getFor(ECBComponent.class);
	ComponentMapper<StageElementComponent> surfaceMapper =
			ComponentMapper.getFor(StageElementComponent.class);
	ComponentMapper<ElasticityComponent> elasticityMapper =
			ComponentMapper.getFor(ElasticityComponent.class);

	public CollisionSystem(int p)
	{
		super(Family.all(VelocityComponent.class, ECBComponent.class).get(), p);
	}

	public void processEntity(Entity entity, float delta)
	{
		//Move the entity first
		CollisionBox ourBox = ecbMapper.get(entity).getEcb();
		ourBox.setPosition(positionMapper.get(entity).x, positionMapper.get(entity).y);
		Map<StageElement, ProjectionVector> minVectors = new HashMap<>();
		Map<StageElement, ProjectionVector> maxVectors = new HashMap<>();
		//First, populate the highest-level hash maps
		for (Entity ent : getEngine().getEntitiesFor(surfaceFamily))
			for (StageElement se : surfaceMapper.get(ent).get())
			{
				minVectors.put(se, se.depth(ourBox.getStadiumAt(0), 0));
				maxVectors.put(se, se.depth(ourBox.getStadiumAt(1), 1));
			}
		ProjectionVector disp = new ProjectionVector(0, 0, 0);
		StageElement hit = ecbHit(0, 1, ourBox.getStadiumAt(0),
				ourBox.getStadiumAt(1), minVectors, maxVectors, disp);
		//After all this, operate collision effects
		positionMapper.get(entity).x += disp.xnorm*disp.magnitude;
		positionMapper.get(entity).y += disp.ynorm*disp.magnitude;
		ourBox.setPosition(positionMapper.get(entity).x, positionMapper.get(entity).y);
		ourBox.setAreas();
		//Reflect off of the hit surface
		if (velocityMapper.has(entity) && hit != null)
		{
			ProjectionVector surfNorm = hit.depth(ourBox.getCurrentStadium(), 1);
			ProjectionVector surfVel = hit.instantVelocity(ourBox.getCurrentStadium(), 1);
			double diffX = velocityMapper.get(entity).xVel - surfVel.xnorm * surfVel.magnitude;
			double diffY = velocityMapper.get(entity).yVel - surfVel.ynorm * surfVel.magnitude;
			if (diffX*surfNorm.xnorm + diffY*surfNorm.ynorm < 0)
			{
				double elasticity = 0;
				if (elasticityMapper.has(entity))
				{
					if (surfNorm.ynorm > StrictMath.abs(surfNorm.xnorm))
						elasticity = elasticityMapper.get(entity).getGroundElasticity();
					else elasticity = elasticityMapper.get(entity).getWallElasticity();
				}
				//Get vector projection and rejection
				double[] projection = Utility.projection(diffX, diffY, surfNorm.xnorm, surfNorm.ynorm);
				velocityMapper.get(entity).xVel -= (1 + elasticity) * projection[0];
				velocityMapper.get(entity).yVel -= (1 + elasticity) * projection[1];
			}
		}
	}

	public StageElement ecbHit(double start, double end, Stadium before,
			Stadium after, Map<StageElement, ProjectionVector> fores,
			Map<StageElement, ProjectionVector> afts, ProjectionVector disp)
	{
		//If stage surface normal change too little, we can take them out of consideration
		if (fores.isEmpty() || afts.isEmpty()) return null;
		Set<StageElement> earlySurfaces = new HashSet<>();
		Set<StageElement> acceptedSurfaces = new HashSet<>();
		Set<StageElement> rejectedSurfaces = new HashSet<>();
		for (Map.Entry<StageElement, ProjectionVector> entry : afts.entrySet())
		{
			if (Utility.velocityDifference(entry.getKey(), before, after, start, end).magnitude > 0)
			{
				if (entry.getKey().collides(before, start))
					earlySurfaces.add(entry.getKey());
				if (entry.getKey().collides(after, end))
					acceptedSurfaces.add(entry.getKey());
				if (!(Utility.projectionsClose(fores.get(entry.getKey()), entry.getValue())))
					rejectedSurfaces.add(entry.getKey());
			}
		}
		if (!earlySurfaces.isEmpty())
		{
			ProjectionVector tentativeDisp = new ProjectionVector(0, 0, 0);
			StageElement returnElement = null;
			for (StageElement se : earlySurfaces)
			{
				ProjectionVector foreVec = fores.get(se);
				if (foreVec.magnitude >= tentativeDisp.magnitude)
				{
					returnElement = se;
					tentativeDisp.xnorm = foreVec.xnorm;
					tentativeDisp.ynorm = foreVec.ynorm;
					tentativeDisp.magnitude = foreVec.magnitude;
				}
			}
			if (returnElement != null)
			{
				disp.xnorm = tentativeDisp.xnorm;
				disp.ynorm = tentativeDisp.ynorm;
				disp.magnitude = tentativeDisp.magnitude;
				return returnElement;
			}
		}
		if (!acceptedSurfaces.isEmpty())
		{
			ProjectionVector tentativeDisp = new ProjectionVector(0, 0, 0);
			StageElement returnElement = null;
			for (StageElement se : acceptedSurfaces)
			{
				ProjectionVector aftVec = afts.get(se);
				if (aftVec.magnitude >= tentativeDisp.magnitude)
				{
					returnElement = se;
					tentativeDisp.xnorm = aftVec.xnorm;
					tentativeDisp.ynorm = aftVec.ynorm;
					tentativeDisp.magnitude = aftVec.magnitude;
				}
			}
			if (returnElement != null)
			{
				disp.xnorm = tentativeDisp.xnorm;
				disp.ynorm = tentativeDisp.ynorm;
				disp.magnitude = tentativeDisp.magnitude;
				return returnElement;
			}
		}
		if (rejectedSurfaces.isEmpty()) return null;
		if (end - start < .000000001)
		{
			System.out.println("Iterating interval too small");
			return null;
		}
		//Else, iterate deeper
		double avg = (start+end)/2;
		Stadium middle = Utility.middleStad(before, after);
		//First move
		Map<StageElement, ProjectionVector> firstFores = new HashMap<>();
		Map<StageElement, ProjectionVector> firstAfts = new HashMap<>();
		for (StageElement se : rejectedSurfaces)
		{
			firstFores.put(se, fores.get(se));
			firstAfts.put(se, se.depth(middle, avg));
		}
		ProjectionVector disp1 = new ProjectionVector(0, 0, 0);
		StageElement firstHit = ecbHit(start, avg, before, middle, firstFores, firstAfts, disp1);
		//Now do the second move
		Stadium ending = new Stadium().set(after);
		Map<StageElement, ProjectionVector> secondFores = new HashMap<>();
		Map<StageElement, ProjectionVector> secondAfts = new HashMap<>();
		if (disp1.magnitude != 0)
		{
			middle.displace(disp1.xnorm*disp1.magnitude, disp1.ynorm*disp1.magnitude);
			ending.displace(disp1.xnorm*disp1.magnitude, disp1.ynorm*disp1.magnitude);
		}
		for (StageElement se : rejectedSurfaces)
		{
			secondFores.put(se, se.depth(middle, avg));
			secondAfts.put(se, se.depth(ending, end));
		}
		ProjectionVector disp2 = new ProjectionVector(0, 0, 0);
		StageElement secondHit = ecbHit(avg, end, middle, ending, secondFores, secondAfts, disp2);
		//Now interpret the two halves
		double xSum = disp1.xnorm*disp1.magnitude + disp2.xnorm*disp2.magnitude;
		double ySum = disp1.ynorm*disp1.magnitude + disp2.ynorm*disp2.magnitude;
		double lSum = StrictMath.hypot(xSum, ySum);
		if (lSum != 0)
		{
			disp.xnorm = xSum / lSum;
			disp.ynorm = ySum / lSum;
			disp.magnitude = lSum;
		}
		return (secondHit==null)?firstHit:secondHit;
	}
}
