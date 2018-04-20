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
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class EntityActionContext implements ScriptContext
{
	// To prevent scripts from messing with the game, certain scopes are read-only
	// and will throw an exception if access is attempted
	public static final int API_SCOPE = 70; //Scope of API provided
	public static final int ENTITY_SCOPE = 50; //Scope of persistent entity bindingsMap
	public static final int ACTION_SCOPE = 30; //Scope that each individual ScriptIterator holds
	public static final int SUBACTION_SCOPE = 0; //Scope for subactions to use for their locals

	ScriptContext baseContext;
	Bindings entityImmutables;
	Bindings entityMutables;
	Bindings actionMutables;
	
	static final List<Integer> scopeList = Arrays.asList(
			ACTION_SCOPE,
			ENTITY_SCOPE,
			API_SCOPE,
			ENGINE_SCOPE,
			GLOBAL_SCOPE
	);

	Reader stdinReader;
	Writer stdoutWriter;
	Writer stderrWriter;

	public EntityActionContext(ScriptContext base, Bindings immutables, Bindings mutables,
	                           Bindings action, Reader stdin, Writer stdout, Writer stderr)
	{
		baseContext = base;
		entityImmutables = immutables;
		entityMutables = mutables;
		actionMutables = action;
		stdinReader = stdin;
		stdoutWriter = stdout;
		stderrWriter = stderr;
	}
	
	public EntityActionContext(EntityActionContext base)
	{
		baseContext = base.baseContext;
		entityImmutables = base.entityImmutables;
		entityMutables = base.entityMutables;
		actionMutables = base.actionMutables;
		stdinReader = base.stdinReader;
		stdoutWriter = base.stdoutWriter;
		stderrWriter = base.stderrWriter;
	}

	public List<Integer> getScopes()
	{
		return scopeList;
	}

	public Bindings getBindings(int scope)
	{
		switch (scope)
		{
			case API_SCOPE:
				return entityImmutables;
			case ENTITY_SCOPE:
				return entityMutables;
			case ACTION_SCOPE:
				return actionMutables;
			default:
				return baseContext.getBindings(scope);
		}
	}

	public void setBindings(Bindings bindings, int scope)
	{
		switch (scope)
		{
			case ACTION_SCOPE:
				actionMutables = new SimpleBindings(bindings);
				break;
			case ENTITY_SCOPE:
				entityMutables = new SimpleBindings(bindings);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	public Object getAttribute(String name)
	{
		if (actionMutables.containsKey(name))
			return actionMutables.get(name);
		else if (entityMutables.containsKey(name))
			return entityMutables.get(name);
		else if (entityImmutables.containsKey(name))
			return entityImmutables.get(name);
		else return baseContext.getAttribute(name);
	}

	public Object getAttribute(String name, int scope)
	{
		if (scope == ACTION_SCOPE && actionMutables.containsKey(name))
			return actionMutables.get(name);
		else if (scope == ENTITY_SCOPE && entityMutables.containsKey(name))
			return entityMutables.get(name);
		else if (scope == API_SCOPE && entityImmutables.containsKey(name))
			return entityImmutables.get(name);
		else return baseContext.getAttribute(name, scope);
	}

	public void setAttribute(String name, Object value, int scope)
	{
		switch (scope)
		{
			case ACTION_SCOPE:
				actionMutables.put(name, value);
				break;
			case ENTITY_SCOPE:
				entityMutables.put(name, value);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	public Object removeAttribute(String name, int scope)
	{
		switch (scope)
		{
			case ACTION_SCOPE:
				return actionMutables.remove(name);
			case ENTITY_SCOPE:
				return entityMutables.remove(name);
			default:
				throw new IllegalArgumentException();
		}
	}

	public int getAttributesScope(String name)
	{
		if (actionMutables.containsKey(name))
			return ACTION_SCOPE;
		else if (entityMutables.containsKey(name))
			return ENTITY_SCOPE;
		else if (entityImmutables.containsKey(name))
			return API_SCOPE;
		else
			return baseContext.getAttributesScope(name);
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
