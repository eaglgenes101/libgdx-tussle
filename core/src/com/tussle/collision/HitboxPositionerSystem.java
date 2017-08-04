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

package com.tussle.collision;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tussle.motion.PositionComponent;

/**
 * Created by eaglgenes101 on 7/27/17.
 */
public class HitboxPositionerSystem extends IteratingSystem
{
	//Component mappers
	ComponentMapper<PositionComponent> positionMapper =
			ComponentMapper.getFor(PositionComponent.class);
	ComponentMapper<ECBComponent> ecbMapper =
			ComponentMapper.getFor(ECBComponent.class);
	ComponentMapper<StageElementComponent> surfaceMapper =
			ComponentMapper.getFor(StageElementComponent.class);

	public HitboxPositionerSystem(int p)
	{
		super(Family.all(PositionComponent.class).get(), p);
	}

	public void processEntity(Entity entity, float delta)
	{
		if (ecbMapper.has(entity))
		{
			CollisionBox ours = ecbMapper.get(entity).getEcb();
			ours.setPosition(positionMapper.get(entity).x, positionMapper.get(entity).y);
			ours.step();
		}
		if (surfaceMapper.has(entity))
		{
			for (StageElement se : surfaceMapper.get(entity).get())
			{
				se.setPosition(positionMapper.get(entity).x, positionMapper.get(entity).y);
				se.step();
			}
		}
	}
}
