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

package com.tussle.script;

import com.badlogic.ashley.core.Component;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
// Holds and creates script iterators and holds onto a common bindings,
//
public class ScriptContextComponent implements Component
{
	ScriptIterator currentAction;
	LinkedHashSet<ScriptIterator> statusEffects;
	
	public ScriptContextComponent()
	{
		currentAction = null;
		statusEffects = new LinkedHashSet<>();
	}
	
	public ScriptIterator getCurrentAction()
	{
		return currentAction;
	}
	
	public ScriptIterator setCurrentAction(ScriptIterator newAction)
	{
		ScriptIterator toReturnAction = currentAction;
		if (currentAction != null)
			currentAction.drop();
		currentAction = newAction;
		if (newAction != null)
			newAction.init();
		return toReturnAction;
	}
	
	public void addStatusEffect(ScriptIterator newEffect)
	{
		if (newEffect != null)
		{
			statusEffects.add(newEffect);
			newEffect.init();
		}
	}
	
	public Collection<ScriptIterator> clearEffects()
	{
		Collection<ScriptIterator> toReturnCollection = new HashSet<>();
		for (Iterator<ScriptIterator> i = statusEffects.iterator(); i.hasNext();)
		{
			ScriptIterator script = i.next();
			script.drop();
			toReturnCollection.add(script);
			i.remove();
		}
		return toReturnCollection;
	}
	
	public Collection<ScriptIterator> clearEffects(Predicate<ScriptIterator> filter)
	{
		Collection<ScriptIterator> toReturnCollection = new HashSet<>();
		for (Iterator<ScriptIterator> i = statusEffects.iterator(); i.hasNext();)
		{
			ScriptIterator script = i.next();
			if (!filter.test(script))
			{
				script.drop();
				toReturnCollection.add(script);
				i.remove();
			}
		}
		return toReturnCollection;
	}
	
	public ScriptIterator removeEffect(ScriptIterator effect)
	{
		if (statusEffects.contains(effect))
		{
			effect.drop();
			statusEffects.remove(effect);
			return effect;
		}
		else
			return null;
	}
	
	public Collection<ScriptIterator> exec()
	{
		Collection<ScriptIterator> toReturn = new HashSet<>();
		Object replacingCurrentAction = currentAction.exec();
		if (replacingCurrentAction != null)
		{
			if (replacingCurrentAction != currentAction &&
		            replacingCurrentAction instanceof ScriptIterator)
				toReturn.add(setCurrentAction((ScriptIterator)replacingCurrentAction));
		}
		Set<ScriptIterator> toAdd = new HashSet<>();
		for (Iterator<ScriptIterator> i = statusEffects.iterator(); i.hasNext();)
		{
			ScriptIterator script = i.next();
			Object newStatus = script.exec();
			if (newStatus == null)
			{
				script.drop();
				toReturn.add(script);
				i.remove();
			}
			else if (newStatus != script && newStatus instanceof ScriptIterator)
			{
				script.drop();
				toReturn.add(script);
				i.remove();
				toAdd.add((ScriptIterator)newStatus);
				((ScriptIterator)newStatus).init();
			}
		}
		statusEffects.addAll(toAdd);
		return toReturn;
	}
	
}
