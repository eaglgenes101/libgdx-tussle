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

public class HurtboxComponent implements Component
{
	protected LinkedHashMap<ScriptIterator, LinkedHashSet<Hurtbox>> hurtboxes;
	
	public HurtboxComponent()
	{
		hurtboxes = new LinkedHashMap<>();
	}
	
	public void put(ScriptIterator iterator, Hurtbox hurtbox)
	{
		if (!hurtboxes.containsKey(iterator))
			hurtboxes.put(iterator, new LinkedHashSet<>());
		hurtboxes.get(iterator).add(hurtbox);
	}
	
	public void remove(ScriptIterator iterator, Hurtbox hurtbox)
	{
		hurtboxes.get(iterator).remove(hurtbox);
	}
	
	public void remove(ScriptIterator iterator)
	{
		hurtboxes.remove(iterator);
	}
	
	private transient Collection<Hurtbox> hurtboxValues;
	
	public Collection<Hurtbox> getHurtboxes()
	{
		if (hurtboxValues == null)
		{
			hurtboxValues = new AbstractCollection<Hurtbox>()
			{
				public Iterator<Hurtbox> iterator()
				{
					return IteratorUtils.chainedIterator(
							CollectionUtils.collect(
									hurtboxes.values(),
									HashSet::iterator
							)
					);
				}
				
				public int size()
				{
					//WHEE JAVA FUNCTIONAL STREAMS
					return hurtboxes.values().stream().mapToInt(HashSet::size).sum();
				}
			};
		}
		return hurtboxValues;
	}
}
