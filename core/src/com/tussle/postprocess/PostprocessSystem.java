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
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.map.LazyMap;

import java.util.LinkedHashMap;
import java.util.Map;

// To avoid subtle bugs resulting from system evaluation order, all state changes
// are deferred until this last system, which changes all of them at once
public class PostprocessSystem extends EntitySystem
{
	Map<Class<Component>, Map<Entity, PostprocessStep>>
			componentListMap;
	
	public PostprocessSystem(int p)
	{
		super(p);
		componentListMap = LazyMap.lazyMap(
				new LinkedHashMap<Class<Component>, Map<Entity, PostprocessStep>>(),
				() -> LazyMap.lazyMap(
						new LinkedHashMap<>(),
						FactoryUtils.constantFactory(
								(PostprocessStep)((Component c)->{})
						)
				)
		);
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		for (Map.Entry<Class<Component>, Map<Entity, PostprocessStep>> entry
				: componentListMap.entrySet())
		{
			ComponentMapper<Component> mapper = ComponentMapper.getFor(entry.getKey());
			for (Map.Entry<Entity, PostprocessStep> subEntry
					: entry.getValue().entrySet())
			{
				subEntry.getValue().apply(mapper.get(subEntry.getKey()));
			}
		}
			componentListMap.clear();
	}
	public <E extends Component> void add(Entity e,
	                                      Class<E> comp,
	                                      PostprocessStep<? super E> step)
	{
		componentListMap.get(comp).put(
				e,
				componentListMap.get(comp).get(e).andThen(step)
		);
	}
	
	
}
