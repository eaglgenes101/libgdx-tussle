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

import java.util.ArrayList;
import java.util.Collection;

import static com.tussle.main.Utility.combineProjections;
import static com.tussle.main.Utility.prunedProjections;

/**
 * Created by eaglgenes101 on 5/25/17.
 */
public class MotionSystem extends IteratingSystem
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

	public MotionSystem(int p)
	{
		super(Family.all(PositionComponent.class, VelocityComponent.class).get(), p);
	}

	public void processEntity(Entity entity, float delta)
	{
		positionMapper.get(entity).x += velocityMapper.get(entity).xVel;
		positionMapper.get(entity).y += velocityMapper.get(entity).yVel;
		if (ecbMapper.has(entity))
		{
			CollisionBox ours = ecbMapper.get(entity).getEcb();
			double xVel = (ours.getEndX(1) + ours.getStartX(1)
					- ours.getEndX(0) - ours.getStartX(0)) / 2;
			double yVel = (ours.getEndY(1) + ours.getStartY(1)
					- ours.getEndY(0) - ours.getStartY(0)) / 2;
			Collection<ProjectionVector> vectorCollection = new ArrayList<>();
			for (Entity surfaceHolder : getEngine().getEntitiesFor(surfaceFamily))
			{
				for (StageElement element : surfaceMapper.get(surfaceHolder).get())
				{
					ProjectionVector disp = element.depth(ours.getCurrentStadium(), xVel, yVel);
					if (disp != null)
						vectorCollection.add(disp);
				}
			}
			ProjectionVector ourDisplace = combineProjections(prunedProjections(vectorCollection));
			if (ourDisplace != null)
			{
				positionMapper.get(entity).x += ourDisplace.xnorm * ourDisplace.magnitude;
				positionMapper.get(entity).y += ourDisplace.xnorm * ourDisplace.magnitude;
			}
		}
		//Collision system will handle normals and such
	}
}
