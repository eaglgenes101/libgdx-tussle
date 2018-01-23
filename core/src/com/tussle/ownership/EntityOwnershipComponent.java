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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class EntityOwnershipComponent implements Component
{
	Set<Entity> parentEntities;

	public EntityOwnershipComponent()
	{
		parentEntities = new HashSet<>();
	}

	public Set<Entity> getDependents()
	{
		return parentEntities;
	}

	public void addDependent(Entity entity)
	{
		parentEntities.add(entity);
	}

	public void removeDependent(Entity entity)
	{
		parentEntities.remove(entity);
	}
}
