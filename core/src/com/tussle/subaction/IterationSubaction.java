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

public class IterationSubaction implements Subaction
{
	String name = null;
	Subaction condition;
	final Subaction[] whileTrue;
	
	public IterationSubaction(Subaction cond, Subaction[] doing)
	{
		condition = cond;
		whileTrue = doing;
	}
	
	public IterationSubaction(String n, Subaction cond, Subaction[] doing)
	{
		condition = cond;
		whileTrue = doing;
		name = n;
	}
	
	public Object eval(ScriptContext globals, StackedBindings locals) throws RemoteJump
	{
		Object pred;
		Object ret = null;
		for (;;)
		{
			try
			{
				pred = condition.eval(globals, locals);
			}
			catch (RemoteJump jump)
			{
				throw new IllegalJumpException(jump);
			}
			if (pred == null || pred.equals(Boolean.FALSE)) break;
			locals.push();
			try
			{
				for (Subaction s : whileTrue)
					ret = s.eval(globals, locals);
			}
			catch (Break br)
			{
				if (br.isTarget(name))
					break;
				else
					throw br;
			}
			catch (Continue cont)
			{
				if (cont.isTarget(name))
					continue;
				else
					throw cont;
			}
			finally
			{
				locals.pop();
			}
		}
		return ret;
	}
}
