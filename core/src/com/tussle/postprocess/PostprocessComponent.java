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
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

// This component holds a list of changes to apply to its owner
// during the post-process step
public class PostprocessComponent implements Component
{
	Map<Class<? extends Component>, PostprocessStep<Component>> componentListMap;
	
	public PostprocessComponent()
	{
		componentListMap = LazyMap.lazyMap(
				new HashMap<>(),
				(Factory<PostprocessStep>)()->(Component c)->{} //Lazily generate null consumers
		);
	}
	
	public <E extends Component> void add(Class<E> comp, PostprocessStep<E> step)
	{
		componentListMap.put(comp, componentListMap.get(comp).andThen(step));
	}
	
	public void addAll(Map<Class<Component>, PostprocessStep<Component>> steps)
	{
		for (Map.Entry<Class<Component>, PostprocessStep<Component>> step : steps.entrySet())
		{
			add(step.getKey(), step.getValue());
		}
	}
	
	//This obtains and clears the map in one step
	public Map<Class<? extends Component>, PostprocessStep<Component>> getSteps()
	{
		Map<Class<? extends Component>, PostprocessStep<Component>> toReturn =
				new HashMap<>(componentListMap);
		componentListMap.clear();
		return toReturn;
	}
}
