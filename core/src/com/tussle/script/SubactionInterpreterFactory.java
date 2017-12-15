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

import javax.script.Bindings;
import javax.script.ScriptEngineFactory;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;

public class SubactionInterpreterFactory implements ScriptEngineFactory
{
	private List<String> mimeTypes;
	private List<String> names;
	private List<String> extensions;

	Bindings globalBindings;

	public SubactionInterpreterFactory()
	{
		globalBindings = new SimpleBindings();
		mimeTypes = new ArrayList<>();
		mimeTypes.add("application/json");
		names = new ArrayList<>();
		names.add("TussleJSON");
		names.add("Subaction");
		names.add("Tussle Script");
		extensions = new ArrayList<>();
		extensions.add(".json");
	}

	public String getEngineName()
	{
		return "Tussle Subaction Interpreter";
	}

	public String getEngineVersion()
	{
		return "0.0.0"; //...
	}

	public String getLanguageName()
	{
		return "Tussle Subaction Script";
	}

	public String getLanguageVersion()
	{
		return "0.0.0";
	}

	public List<String> getMimeTypes()
	{
		return mimeTypes;
	}

	public List<String> getNames()
	{
		return names;
	}

	public List<String> getExtensions()
	{
		return extensions;
	}

	public Object getParameter(String key)
	{
		switch (key)
		{
			case "ScriptEngine.ENGINE":
				return getEngineName();
			case "ScriptEngine.ENGINE_VERSION":
				return getEngineVersion();
			case "ScriptEngine.LANGUAGE":
				return getLanguageName();
			case "ScriptEngine.LANGUAGE_VERSION":
				return getLanguageVersion();
			case "ScriptEngine.NAMES":
				return "Subaction";
			default: //Might add other keys later
				return null;
		}
	}

	public String getProgram(String... statements)
	{
		return null; //TODO: Remove this stub as soon as I know more about the syntax
	}

	public String getOutputStatement(String toDisplay)
	{
		return null; //TODO: Remove this stub as soon as I know more about the syntax
	}

	public String getMethodCallSyntax(String obj, String m, String... args)
	{
		return null; //TODO: Remove this stub as soon as I know more about the syntax
	}

	public Bindings getGlobalBindings()
	{
		return globalBindings;
	}

	public SubactionInterpreter getScriptEngine()
	{
		return new SubactionInterpreter(this);
	}

}
