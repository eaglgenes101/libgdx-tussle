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

/**
 * Created by eaglgenes101 on 6/7/17.
 */
public class CollisionSystem extends IteratingSystem
{
	//Entity families
	Family surfaceFamily = Family.all(PositionComponent.class,
			StageElementComponent.class).get();

	//Component mappers
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
		CollisionBox ours = ecbMapper.get(entity).getEcb();
		ProjectionVector deepNorm = null;
		ProjectionVector deepVel = null;
		for (Entity surfaceHolder : getEngine().getEntitiesFor(surfaceFamily))
		{
			for (StageElement element : surfaceMapper.get(surfaceHolder).get())
			{
				ProjectionVector disp = element.normal(ours.getCurrentStadium());
				if (disp != null && (deepNorm == null || disp.magnitude > deepNorm.magnitude))
				{
					deepNorm = disp;
					deepVel = element.instantVelocity(ours.getCurrentStadium());
				}
			}
		}
		if (deepNorm != null && deepVel != null)
		{
			//Now, given these projections, find the reflection angle
			double relativeX = velocityMapper.get(entity).xVel - deepVel.magnitude * deepVel.xnorm;
			double relativeY = velocityMapper.get(entity).yVel - deepVel.magnitude * deepVel.ynorm;

			double[] proj = Utility.projection(relativeX, relativeY, deepNorm.xnorm, deepNorm.ynorm);
			ElasticityComponent ec = elasticityMapper.get(entity);
			double subVal;
			if (deepNorm.ynorm > 0)
				subVal = -1 - ec.getGroundElasticity();
			else
				subVal = -1 - ec.getWallElasticity();
			velocityMapper.get(entity).xVel += subVal*proj[0];
			velocityMapper.get(entity).yVel += subVal*proj[1];
		}
	}
}
