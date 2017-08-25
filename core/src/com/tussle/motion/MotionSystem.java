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
import com.tussle.collision.StageElement;
import com.tussle.collision.StageElementComponent;

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
		if (surfaceMapper.has(entity))
		{
			for (StageElement se : surfaceMapper.get(entity).get())
			{
				se.step();
				se.setPosition(positionMapper.get(entity).x, positionMapper.get(entity).y);
			}
		}
		//Collision system will handle normals and such
	}
}
