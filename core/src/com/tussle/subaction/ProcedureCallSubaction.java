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
import java.util.Map;

public class ProcedureCallSubaction implements Subaction
{
	Subaction toCallSource;
	Map<String, Subaction> argSources;
	
	public ProcedureCallSubaction(Subaction calleeSource, Map<String, Subaction> args)
	{
		toCallSource = calleeSource;
		argSources = args;
	}
	
	public Object eval(ScriptContext globals, StackedBindings locals)
	{
		StackedBindings locs = new StackedBindings();
		for (Map.Entry<String, Subaction> entry : argSources.entrySet())
		{
			try
			{
				locs.put(entry.getKey(), entry.getValue().eval(globals, locals));
			}
			catch (RemoteJump j)
			{
				throw new IllegalJumpException(j);
			}
		}
		try
		{
			return toCallSource.eval(globals, locs);
		}
		catch (RemoteJump j)
		{
			throw new IllegalJumpException(j);
		}
	}
}
