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

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.tussle.main.Components;
import com.tussle.stream.EntityStreamMaintainer;
import com.tussle.stream.InputOutputComponent;

import javax.script.SimpleBindings;
import java.io.*;

public class SubactionScriptSystem extends IteratingSystem
{
	Family streamFamily = Family.all(InputOutputComponent.class).get();
	Signal<ScriptIterator> destructionSignaller;
	
	EntityStreamMaintainer streamMaintainer;
	SubactionInterpreter subactionInterpreter;
	SubactionInterpreterFactory factory = new SubactionInterpreterFactory();
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(System.out));

	public SubactionScriptSystem(int i)
	{
		super(Family.all(ScriptContextComponent.class).get(), i);
		streamMaintainer = new EntityStreamMaintainer();
		destructionSignaller = new Signal<>();
	}
	
	public void addedToEngine(Engine engine)
	{
		subactionInterpreter = factory.getScriptEngine();
		engine.addEntityListener(streamFamily, streamMaintainer);
	}
	
	public void removedFromEngine(Engine engine)
	{
		engine.removeEntityListener(streamMaintainer);
	}
	
	public void update(float deltaTime)
	{
		//Fetch input, push it into the stream
		//Using stdin and stdout for now, will probably change later
		try
		{
			streamMaintainer.writeStdIn(stdIn.readLine());
		}
		catch (IOException ex)
		{
			System.err.println(ex.toString());
		}
		super.update(deltaTime);
		try
		{
			String writeString = streamMaintainer.readStdOut();
			String errorString = streamMaintainer.readStdErr();
			stdOut.write(writeString);
			System.err.println(errorString);
		}
		catch (IOException ex)
		{
			System.err.println(ex.toString());
		}
		//Fetch output, pull it up to the screen
	}
	
	public EntityActionContext createContextFor(Entity entity)
	{
		return new EntityActionContext(
				subactionInterpreter.getContext(),
				new SimpleBindings(), //TODO: Insert API bindings
				new SimpleBindings(),
				new SimpleBindings(),
				Components.inputOutputMapper.get(entity).getStdin(),
				Components.inputOutputMapper.get(entity).getStdout(),
				Components.inputOutputMapper.get(entity).getStderr()
		);
	}
	
	public void processEntity(Entity entity, float deltaTime)
	{
		ScriptContextComponent scriptContextComponent = Components.scriptContextMapper.get(entity);
		scriptContextComponent.exec();
	}
	
	public void subscribeDestruction(Listener<ScriptIterator> listener)
	{
		destructionSignaller.add(listener);
	}
	
	public void unsubscribeDestruction(Listener<ScriptIterator> listener)
	{
		destructionSignaller.remove(listener);
	}
	
	
}
