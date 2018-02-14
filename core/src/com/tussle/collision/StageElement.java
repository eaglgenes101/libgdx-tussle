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

import com.tussle.motion.PositionComponent;
import com.tussle.motion.TransformComponent;
import com.tussle.motion.VelocityComponent;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public class StageElement<T extends CollisionShape>
{
	T localShape;
	T beforeShape;
	T afterShape;
	
	public StageElement(T local, PositionComponent position, TransformComponent transform)
	{
		localShape = local;
		beforeShape = (T)localShape.transformBy(position.x, position.y, 0, 1, false);
		afterShape = beforeShape;
	}
	
	public CollisionShape getBefore()
	{
		return beforeShape;
	}
	
	public CollisionShape getAfter()
	{
		return afterShape;
	}
	
	public void step(PositionComponent position, TransformComponent transform,
	                 VelocityComponent velocity, double dx, double dy)
	{
		beforeShape = (T) afterShape.displacementBy(dx, dy);
		afterShape = (T)localShape.transformBy(
				position.x+dx+(velocity==null?0:velocity.xVel),
				position.y+dy+(velocity==null?0:velocity.yVel),
				0,
				1,
				false
		);
	}
}
