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
import com.tussle.collision.StageElement;
import com.tussle.collision.StageElementComponent;
import com.tussle.main.Components;
import com.tussle.postprocess.PostprocessSystem;

/**
 * Created by eaglgenes101 on 5/25/17.
 */
public class MotionSystem extends IteratingSystem
{
	public MotionSystem(int p)
	{
		super(Family.all(PositionComponent.class, VelocityComponent.class).get(), p);
	}

	public void processEntity(Entity entity, float delta)
	{
		final double xVel = Components.velocityMapper.get(entity).xVel;
		final double yVel = Components.velocityMapper.get(entity).yVel;
		final double xPos = Components.positionMapper.get(entity).x;
		final double yPos = Components.positionMapper.get(entity).y;
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				PositionComponent.class,
				cl -> cl.displace(xVel, yVel)
		);
		getEngine().getSystem(PostprocessSystem.class).add(
				entity,
				StageElementComponent.class,
				cl -> {
					for (StageElement se : cl.getStageElements())
					{
						se.setBeforePos(xPos, yPos);
						se.setAfterPos(xPos+xVel, yPos+yVel);
					}
				}
		);
		//Collision system will handle normals and such
	}
}
