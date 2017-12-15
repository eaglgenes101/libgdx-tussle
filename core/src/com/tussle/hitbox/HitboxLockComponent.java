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
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.tussle.script.ScriptIterator;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceIdentityMap;
import org.apache.commons.collections4.multimap.AbstractMultiValuedMap;

import java.util.HashSet;

//Holds a weak map of hitbox locks used against its owning entity
//To help avoid cluttering the runtime with garbage, this component
//contains some of its own collection logic
public class HitboxLockComponent implements Component, Listener<ScriptIterator>
{
	MultiValuedMap<ScriptIterator, HitboxLock> lockMap;
	
	public HitboxLockComponent()
	{
		//There ought to be a more pleasant factory method to do this
		lockMap = new AbstractMultiValuedMap<ScriptIterator, HitboxLock>(
				new ReferenceIdentityMap<>(AbstractReferenceMap.ReferenceStrength.SOFT,
				                           AbstractReferenceMap.ReferenceStrength.WEAK))
		{
			protected HashSet<HitboxLock> createCollection()
			{
				return new HashSet<>();
			}
		};
		
	}
	
	public void put(ScriptIterator iter, HitboxLock lock)
	{
		lockMap.put(iter, lock);
	}
	
	public boolean containsLock(ScriptIterator iter, HitboxLock lock)
	{
		return lockMap.get(iter).contains(lock);
	}
	
	public void remove(ScriptIterator iter)
	{
		lockMap.remove(iter);
	}
	
	public void receive(Signal<ScriptIterator> signaller, ScriptIterator signal)
	{
		remove(signal);
	}
	
	
}
