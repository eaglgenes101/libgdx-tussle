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

package com.tussle.subaction;

import com.tussle.script.EntityActionContext;
import com.tussle.script.StackedBindings;

import javax.script.ScriptContext;

public class GetSubaction implements Subaction
{
	String toGet;
	int scope;
	
	public GetSubaction(String str)
	{
		toGet = str;
		scope = Integer.MIN_VALUE;
	}
	
	public GetSubaction(String str, int s)
	{
		toGet = str;
		scope = s >= EntityActionContext.GLOBAL_SCOPE ? EntityActionContext.GLOBAL_SCOPE :
		        s >= EntityActionContext.ENGINE_SCOPE ? EntityActionContext.ENGINE_SCOPE :
		        s >= EntityActionContext.API_SCOPE ? EntityActionContext.API_SCOPE :
		        s >= EntityActionContext.ENTITY_SCOPE ? EntityActionContext.ENTITY_SCOPE :
		        s >= EntityActionContext.ACTION_SCOPE ? EntityActionContext.ACTION_SCOPE :
		        EntityActionContext.SUBACTION_SCOPE;
	}
	
	public Object eval(ScriptContext globals, StackedBindings locals)
	{
		if (scope <= EntityActionContext.SUBACTION_SCOPE)
		{
			if (locals.containsKey(toGet))
				return locals.get(toGet);
			else if (scope < EntityActionContext.SUBACTION_SCOPE)
				return globals.getAttribute(toGet);
			else
				return null;
		}
		else
			return globals.getAttribute(toGet, scope);
	}
}
