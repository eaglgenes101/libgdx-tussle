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

package com.tussle.postprocess;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.tussle.main.Components;

import java.util.Map;

// To avoid subtle bugs resulting from system evaluation order, all state changes
// are deferred until this last system, which changes all of them at once
public class PostprocessSystem extends EntitySystem
{
	public PostprocessSystem(int p)
	{
		super(p);
	}
	
	public void processEntity(Entity entity, float deltaTime)
	{
		Map<Class<? extends Component>, PostprocessStep<Component>> steps =
				Components.postprocessMapper.get(entity).getSteps();
		for (Component comp : entity.getComponents())
		{
			//Take the class of the component, get a lookup, then
			//apply that class's value changes to the class itself
			steps.get(comp.getClass()).accept(comp);
		}
	}
	
	
}
