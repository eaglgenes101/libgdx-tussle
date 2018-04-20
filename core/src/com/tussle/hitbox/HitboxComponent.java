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

package com.tussle.hitbox;

import com.badlogic.ashley.core.Component;
import com.tussle.script.ScriptIterator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;

import java.util.*;

public class HitboxComponent implements Component
{
	protected LinkedHashMap<ScriptIterator, LinkedHashSet<Hitbox>> hitboxes;
	
	public HitboxComponent()
	{
		hitboxes = new LinkedHashMap<>();
	}
	
	public void put(ScriptIterator iterator, Hitbox hitbox)
	{
		if (!hitboxes.containsKey(iterator))
			hitboxes.put(iterator, new LinkedHashSet<>());
		hitboxes.get(iterator).add(hitbox);
	}
	
	public void remove(ScriptIterator iterator, Hitbox hitbox)
	{
		hitboxes.get(iterator).remove(hitbox);
	}
	
	public void remove(ScriptIterator iterator)
	{
		hitboxes.remove(iterator);
	}
	
	private transient Collection<Hitbox> hitboxValues;
	
	public Collection<Hitbox> getHitboxes()
	{
		if (hitboxValues == null)
		{
			hitboxValues = new AbstractCollection<Hitbox>()
			{
				public Iterator<Hitbox> iterator()
				{
					return IteratorUtils.chainedIterator(
							CollectionUtils.collect(
									hitboxes.values(),
									HashSet::iterator
							)
					);
				}
				
				public int size()
				{
					//WHEE JAVA FUNCTIONAL STREAMS
					return hitboxes.values().stream().mapToInt(HashSet::size).sum();
				}
			};
		}
		return hitboxValues;
	}
}
