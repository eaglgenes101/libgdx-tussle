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

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.tussle.logging.LogSystem;
import com.tussle.subaction.ProcedureDefinitionSubaction;
import com.tussle.subaction.RemoteJump;
import com.tussle.subaction.Subaction;

import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

//Packages a procedure in a action-local context that lets it be run repeatedly
//as an action or status effect
public class ScriptIterator implements Listener<String>
{
	public static final Logger logger = Logger.getLogger(LogSystem.class.getName());
	
	Subaction wrappedConstructor;
	Subaction wrappedProcedure;
	Subaction wrappedDestructor;
	ScriptContext scriptContext;
	Map<String, Subaction> eventCallbacks;
	Signal<ScriptIterator> destructionSignal;
	boolean destructorFired;
	
	public ScriptIterator(Subaction constructor, Subaction procedure,
	                      Subaction destructor, ScriptContext context,
	                      Signal<ScriptIterator> destructSig)
	{
		wrappedConstructor = constructor;
		wrappedProcedure = procedure;
		wrappedDestructor = destructor;
		scriptContext = context;
		eventCallbacks = new HashMap<>();
		destructionSignal = destructSig;
	}
	
	public ProcedureDefinitionSubaction putCallback(String str, Subaction call)
	{
		return (ProcedureDefinitionSubaction)eventCallbacks.put(str, call);
	}
	
	public ProcedureDefinitionSubaction removeCallback(String str)
	{
		return (ProcedureDefinitionSubaction)eventCallbacks.remove(str);
	}
	
	//Targeted callback
	public boolean callback(String str)
	{
		if (eventCallbacks.containsKey(str))
		{
			try
			{
				eventCallbacks.get(str).eval(scriptContext, new StackedBindings());
			}
			catch (RemoteJump j)
			{
				throw new IllegalJumpException(j);
			}
			return true;
		}
		else
			return false;
	}
	
	public void receive(Signal<String> signaller, String str)
	{
		callback(str);
	}
	
	public void init()
	{
		try
		{
			wrappedConstructor.eval(scriptContext, new StackedBindings());
		}
		catch (RemoteJump j)
		{
			throw new IllegalJumpException(j);
		}
	}
	
	public Object exec()
	{
		try
		{
			return wrappedProcedure.eval(scriptContext, new StackedBindings());
		}
		catch (RemoteJump j)
		{
			throw new IllegalJumpException(j);
		}
	}
	
	public void drop()
	{
		if (!destructorFired)
		{
			try
			{
				wrappedDestructor.eval(scriptContext, new StackedBindings());
			}
			catch (RemoteJump j)
			{
				throw new IllegalJumpException(j);
			}
			finally
			{
				destructionSignal.dispatch(this);
				destructorFired = true;
			}
		}
		else
		{
			logger.warning("Disposal method called on script iterator called more than" +
			               "once. Please contact the developer(s) so they can fix this.");
		}
	}
	
	public void finalize()
	{
		if (!destructorFired)
		{
			try
			{
				wrappedDestructor.eval(scriptContext, new StackedBindings());
			}
			catch (RemoteJump j)
			{
				throw new IllegalJumpException(j);
			}
			finally
			{
				destructionSignal.dispatch(this);
				logger.warning("Disposal method not called on script iterator. Please " +
				               "contact the developer(s) so they can fix this.");
			}
		}
	}
	
}
