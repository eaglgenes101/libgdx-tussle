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

import com.tussle.script.IllegalJumpException;
import com.tussle.script.StackedBindings;

import javax.script.ScriptContext;

public class ConditionalSubaction implements Subaction
{
	String name = null;
	Subaction basedOn;
	Subaction[] ifTrue;
	Subaction[] ifFalse;
	
	public ConditionalSubaction(Subaction based, Subaction[] pos, Subaction[] neg)
	{
		basedOn = based;
		ifTrue = pos;
		ifFalse = neg;
	}
	
	public ConditionalSubaction(String n, Subaction based, Subaction[] pos, Subaction[] neg)
	{
		 name = n;
		 basedOn = based;
		 ifTrue = pos;
		 ifFalse = neg;
	}
	
	public Object eval(ScriptContext context, StackedBindings locals) throws RemoteJump
	{
		Object pred;
		Object ret = null;
		try
		{
			pred = basedOn.eval(context, locals);
		}
		catch (RemoteJump jump)
		{
			throw new IllegalJumpException(jump);
		}
		try
		{
			locals.push();
			if (pred == null || pred.equals(Boolean.FALSE))
			{
				for (Subaction s : ifFalse)
					ret = s.eval(context, locals);
			}
			else
			{
				for (Subaction s : ifTrue)
					ret = s.eval(context, locals);
			}
		}
		catch (Break br)
		{
			if (!(br.hasTarget() && br.target.equals(name)))
				throw br;
		}
		finally
		{
			locals.pop();
			return ret;
		}
	}
}
