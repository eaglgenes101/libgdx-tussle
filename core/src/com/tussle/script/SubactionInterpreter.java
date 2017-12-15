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

import javax.script.*;
import java.io.Reader;

public class SubactionInterpreter extends AbstractScriptEngine
{
	SubactionInterpreterFactory sourceFactory;

	public SubactionInterpreter(SubactionInterpreterFactory factory)
	{
		super(new SimpleBindings());
		sourceFactory = factory;
		setBindings(factory.getGlobalBindings(), ScriptContext.GLOBAL_SCOPE);
	}

	public Object eval(Reader reader, ScriptContext context)
	{
		return null; //TODO: Remove this stub as soon as I know more about the syntax
	}

	public Object eval(String string, ScriptContext context)
	{
		return null; //TODO: Remove this stub as soon as I know more about the syntax
	}

	public ScriptEngineFactory getFactory()
	{
		return sourceFactory;
	}

	public Bindings createBindings()
	{
		//Create a blank bindingsMap
		return new SimpleBindings();
	}
}
