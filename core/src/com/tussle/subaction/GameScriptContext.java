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

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScriptContext implements ScriptContext
{
	// To prevent scripts from messing with the game, scopes > 0 are readonly
	public static final int API_SCOPE = 50; //Scope of API provided
	public static final int ENTITY_SCOPE = -10; //Scope of persistent entity bindingsMap
	public static final int LIFE_SCOPE = -20; //Scope of per-life entity bindingsMap
	public static final int ACTION_SCOPE = -30; //Scope of action bindingsMap

	Map<Integer, Bindings> bindingsMap;

	public ArrayList<Integer> scopeList;

	Reader stdinReader;
	Writer stdoutWriter;
	Writer stderrWriter;

	public GameScriptContext(Map<Integer, Bindings> init, Reader stdin,
							 Writer stdout, Writer stderr)
	{
		bindingsMap = new HashMap<>();
		for (Map.Entry<Integer, Bindings> entry : init.entrySet())
			bindingsMap.put(entry.getKey(), entry.getValue());
		scopeList = new ArrayList<>();
		scopeList.add(ACTION_SCOPE);
		scopeList.add(LIFE_SCOPE);
		scopeList.add(ENTITY_SCOPE);
		scopeList.add(API_SCOPE);
		scopeList.add(ENGINE_SCOPE);
		scopeList.add(GLOBAL_SCOPE);
		stdinReader = stdin;
		stdoutWriter = stdout;
		stderrWriter = stderr;
	}

	public List<Integer> getScopes()
	{
		return scopeList;
	}

	public Bindings getBindings(int scope)
	{
		return bindingsMap.get(scope);
	}

	public void setBindings(Bindings bindings, int scope)
	{
		if (bindings == null)
			throw new NullPointerException();
		else if (!scopeList.contains(scope))
			throw new IllegalArgumentException();
		else
			bindingsMap.put(scope, bindings);
	}

	public Object getAttribute(String name)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty())
			throw new IllegalArgumentException();
		for (Integer i : scopeList)
		{
			if (bindingsMap.containsKey(i) && bindingsMap.get(i).containsKey(name))
				return bindingsMap.get(i).get(name);
		}
		return null;
	}

	public Object getAttribute(String name, int scope)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty() || !scopeList.contains(scope))
			throw new IllegalArgumentException();
		if (bindingsMap.containsKey(scope) && bindingsMap.get(scope).containsKey(name))
			return bindingsMap.get(scope).get(name);
		return null;
	}

	public void setAttribute(String name, Object value, int scope)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty() || !scopeList.contains(scope))
			throw new IllegalArgumentException();
		bindingsMap.get(scope).put(name, value);
	}

	public Object removeAttribute(String name, int scope)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty() || !scopeList.contains(scope))
			throw new IllegalArgumentException();
		if (bindingsMap.containsKey(scope) && bindingsMap.get(scope).containsKey(name))
		{
			Object toReturn = bindingsMap.get(scope).get(name);
			bindingsMap.get(scope).remove(name);
			return toReturn;
		}
		return null;
	}

	public int getAttributesScope(String name)
	{
		if (name == null)
			throw new NullPointerException();
		if (name.isEmpty())
			throw new IllegalArgumentException();
		for (Integer i : scopeList)
		{
			if (bindingsMap.containsKey(i) && bindingsMap.get(i).containsKey(name))
				return i;
		}
		return -1;
	}

	public Reader getReader()
	{
		return stdinReader;
	}

	public Writer getWriter()
	{
		return stdoutWriter;
	}

	public Writer getErrorWriter()
	{
		return stderrWriter;
	}

	public void setReader(Reader reader)
	{
		stdinReader = reader;
	}

	public void setWriter(Writer writer)
	{
		stdoutWriter = writer;
	}

	public void setErrorWriter(Writer writer)
	{
		stderrWriter = writer;
	}
}
