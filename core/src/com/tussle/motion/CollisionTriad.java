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

import com.tussle.collision.CollisionBox;
import com.tussle.collision.ProjectionVector;
import com.tussle.collision.StageElement;

//A tuple of collision box, stage element, and projection vector
//used for exactly one method in CollisionSystem.
//Kinda heavyweight for a tuple, but it works
public class CollisionTriad
{
	CollisionBox box;
	StageElement surface;
	ProjectionVector vector;
	
	public CollisionTriad(CollisionBox b, StageElement s, ProjectionVector v)
	{
		box = b;
		surface = s;
		vector = v;
	}
	
	public CollisionBox getBox()
	{
		return box;
	}
	
	public StageElement getSurface()
	{
		return surface;
	}
	
	public ProjectionVector getVector()
	{
		return vector;
	}
}
