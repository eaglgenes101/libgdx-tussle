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
public class ECBComponent implements Component
{
	LinkedHashMap<ScriptIterator, LinkedHashSet<StageStadium>> ecbs;
	
	public ECBComponent()
	{
		ecbs = new LinkedHashMap<>();
	}
	
	public void put(ScriptIterator iterator, StageStadium box)
	{
		if (!ecbs.containsKey(iterator))
			ecbs.put(iterator, new LinkedHashSet<>());
		ecbs.get(iterator).add(box);
	}
	
	public void remove(ScriptIterator iterator, StageStadium box)
	{
		ecbs.get(iterator).remove(box);
	}
	
	public void remove(ScriptIterator iterator)
	{
		ecbs.remove(iterator);
	}
	
	private transient Collection<StageStadium> hitboxValues;
	
	public Collection<StageStadium> getCollisionBoxes()
	{
		if (hitboxValues == null)
		{
			hitboxValues = new AbstractCollection<StageStadium>()
			{
				public Iterator<StageStadium> iterator()
				{
					return IteratorUtils.chainedIterator(
							CollectionUtils.collect(
									ecbs.values(),
									(LinkedHashSet<StageStadium> coll) -> coll.iterator()
							)
					);
				}
				
				public int size()
				{
					//WHEE JAVA FUNCTIONAL STREAMS
					return ecbs.values().stream().mapToInt(coll->coll.size()).sum();
				}
			};
		}
		return hitboxValues;
	}
}
