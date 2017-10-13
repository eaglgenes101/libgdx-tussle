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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.tussle.main.JsonParsingWriter;
import com.tussle.main.PipeBufferReader;
import com.tussle.main.PipeBufferWriter;

import java.io.IOException;

public class EntityStreamMaintainer implements EntityListener
{
	JsonDistributingWriter stdIn;
	JsonCollectingWriter stdOut;
	JsonCollectingWriter stdErr;
	PipeBufferWriter errStream;

	JsonParsingWriter stdinInterpreter;

	ComponentMapper<StreamComponent> streamMapper =
			ComponentMapper.getFor(StreamComponent.class);

	public EntityStreamMaintainer()
	{
		PipeBufferWriter warnStream = new PipeBufferWriter();
		errStream = new PipeBufferWriter();
		stdIn = new JsonDistributingWriter(warnStream);
		stdOut = new JsonCollectingWriter(warnStream.getNewReader());
		stdErr = new JsonCollectingWriter(errStream.getNewReader());

		stdinInterpreter = new JsonParsingWriter(errStream);
	}

	public void entityAdded(Entity entity)
	{
		//Attach stdin
		PipeBufferReader readIn = stdIn.openReaderFor(entity);
		streamMapper.get(entity).setStdin(readIn);
		//Attach stdout
		PipeBufferWriter writeOut = stdOut.openWriterFor(entity);
		streamMapper.get(entity).setStdout(writeOut);
		//Attach stderr
		PipeBufferWriter writeErr = stdErr.openWriterFor(entity);
		streamMapper.get(entity).setStderr(writeErr);
	}

	public void entityRemoved(Entity entity)
	{
		stdIn.removeEntity(entity);
		stdOut.removeEntity(entity);
		stdErr.removeEntity(entity);
	}

	public void writeStdIn(String in) throws IOException
	{
		stdinInterpreter.write(in);
		if (stdinInterpreter.ready())
		{
			stdIn.write(stdinInterpreter.read());
		}
	}

	public String readStdOut()
	{
		StringBuilder buffer = new StringBuilder();
		while (stdOut.ready())
			buffer.append(stdOut.read().toString());
		return buffer.toString();
	}

	public String readStdErr()
	{
		StringBuilder buffer = new StringBuilder();
		while (stdErr.ready())
			buffer.append(stdErr.read().toString());
		return buffer.toString();
	}
}
