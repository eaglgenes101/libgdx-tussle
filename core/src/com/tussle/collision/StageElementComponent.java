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

import com.badlogic.ashley.core.Component;
import com.tussle.script.ScriptIterator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;

import java.util.*;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public class StageElementComponent implements Component
{
	LinkedHashMap<ScriptIterator, LinkedHashSet<StageElement>> surfaces;
	
	public StageElementComponent()
	{
		surfaces = new LinkedHashMap<>();
	}
	
	public void put(ScriptIterator iterator, StageElement surface)
	{
		if (!surfaces.containsKey(iterator))
			surfaces.put(iterator, new LinkedHashSet<>());
		surfaces.get(iterator).add(surface);
	}
	
	public void remove(ScriptIterator iterator, StageElement surface)
	{
		surfaces.get(iterator).remove(surface);
	}
	
	public void remove(ScriptIterator iterator)
	{
		surfaces.remove(iterator);
	}
	
	private transient Collection<StageElement> hitboxValues;
	
	public Collection<StageElement> getStageElements()
	{
		if (hitboxValues == null)
		{
			hitboxValues = new AbstractCollection<StageElement>()
			{
				public Iterator<StageElement> iterator()
				{
					return IteratorUtils.chainedIterator(
							CollectionUtils.collect(
									surfaces.values(),
									HashSet::iterator
							)
					);
				}
				
				public int size()
				{
					//WHEE JAVA FUNCTIONAL STREAMS
					return surfaces.values().stream().mapToInt(HashSet::size).sum();
				}
			};
		}
		return hitboxValues;
	}
}
