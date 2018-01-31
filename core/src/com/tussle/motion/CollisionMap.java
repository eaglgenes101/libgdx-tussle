/*
 * Copyright (c) 2018 eaglgenes101
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
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;

public class CollisionMap extends LinkedHashMap<Pair<CollisionBox, StageElement>,
		                                            ProjectionVector>
{
	public ProjectionVector get(CollisionBox c, StageElement s)
	{
		//Ah well, looks like I'm going to have to use a transient pair
		return get(Pair.of(c, s));
	}
	
	public ProjectionVector put(CollisionBox c, StageElement s, ProjectionVector p)
	{
		return put(Pair.of(c, s), p);
	}
	
	
	
}
