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
import com.tussle.main.Utility;
import com.tussle.stream.*;
import org.apache.commons.collections4.map.LazyMap;

import javax.script.SimpleBindings;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SubactionScriptSystem extends IteratingSystem
{
	Signal<ScriptIterator> destructionSignaller;
	Map<Entity, EntityActionContext> entityContexts;
	
	SubactionInterpreter subactionInterpreter;
	SubactionInterpreterFactory factory = new SubactionInterpreterFactory();
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
	BufferedWriter stdOut = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
	
	JsonDistributingWriter stdInProcessor;
	JsonCollectingWriter stdOutProcessor;
	JsonCollectingWriter stdErrProcessor;
	PipeBufferWriter processingErrStream;
	
	JsonParsingWriter stdinInterpreter;

	public SubactionScriptSystem(int i)
	{
		super(Family.all(ScriptContextComponent.class).get(), i);
		PipeBufferWriter warnStream = new PipeBufferWriter();
		processingErrStream = new PipeBufferWriter();
		stdInProcessor = new JsonDistributingWriter(warnStream);
		stdOutProcessor = new JsonCollectingWriter(warnStream.getNewReader());
		stdErrProcessor = new JsonCollectingWriter(processingErrStream.getNewReader());
		
		stdinInterpreter = new JsonParsingWriter(processingErrStream);
		destructionSignaller = new Signal<>();
		entityContexts = LazyMap.lazyMap(
				new HashMap<>(),
				(Entity ent) -> new EntityActionContext(
						subactionInterpreter.getContext(),
						new SimpleBindings(), //TODO: Insert API bindings
						new SimpleBindings(),
						new SimpleBindings(),
						stdInProcessor.openReaderFor(ent),
						stdOutProcessor.openWriterFor(ent),
						stdErrProcessor.openWriterFor(ent)
				)
		);
	}
	
	public EntityActionContext getContextFor(Entity entity)
	{
		return entityContexts.get(entity);
	}
	
	public void addedToEngine(Engine engine)
	{
		subactionInterpreter = factory.getScriptEngine();
	}
	
	public void update(float deltaTime)
	{
		//Fetch input, push it into the stream
		//Using stdin and stdout for now, will probably change later
		try
		{
			
			stdinInterpreter.write(Utility.readAll(stdIn));
			if (stdinInterpreter.ready())
			{
				stdInProcessor.write(stdinInterpreter.read());
			}
		}
		catch (IOException ex)
		{
			System.err.println(ex.toString());
		}
		super.update(deltaTime); //FIXME: Remember to move back out of statement
		try
		{
			StringBuilder outBuffer = new StringBuilder();
			while (stdOutProcessor.ready())
				outBuffer.append(stdOutProcessor.read().toString());
			String writeString = outBuffer.toString();
			
			StringBuilder errBuffer = new StringBuilder();
			while (stdErrProcessor.ready())
				errBuffer.append(stdErrProcessor.read().toString());
			String errorString = errBuffer.toString();
			stdOut.write(writeString);
			System.err.println(errorString);
		}
		catch (IOException ex)
		{
			System.err.println(ex.toString());
		}
		//Fetch output, pull it up to the screen
	}
	
	public void processEntity(Entity entity, float deltaTime)
	{
		ScriptContextComponent scriptContextComponent = Components.scriptContextMapper.get(entity);
		scriptContextComponent.exec();
	}
	
	public Signal<ScriptIterator> getDestructionSignaller()
	{
		return destructionSignaller;
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
