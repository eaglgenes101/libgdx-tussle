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

package com.tussle.ownership;

import com.badlogic.ashley.core.*;
import com.tussle.main.Components;

import java.util.Set;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class OwnershipSystem extends EntitySystem implements EntityListener
{
	public void addedToEngine(Engine engine)
	{
		engine.addEntityListener(this);
	}

	public void removedFromEngine(Engine engine)
	{
		engine.removeEntityListener(this);
	}

	public void entityAdded(Entity entity)
	{
		//Don't care
	}

	public void entityRemoved(Entity entity)
	{
		if (Components.dependencyMapper.has(entity))
		{
			Set<Entity> toRemoveEntities = Components.dependencyMapper.get(entity).getDependents();
			for (Entity e : toRemoveEntities)
			{
				getEngine().removeEntity(e);
			}
		}
	}
}
