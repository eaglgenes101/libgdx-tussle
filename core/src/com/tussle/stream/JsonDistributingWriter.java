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

package com.tussle.stream;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.tussle.main.Utility;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class JsonDistributingWriter implements JsonSink
{
	Map<Entity, Writer> writers;
	Writer errorWriter;

	public JsonDistributingWriter(Writer err)
	{
		writers = new HashMap<>();
		errorWriter = err;
	}

	public PipeBufferReader openReaderFor(Entity entity)
	{
		PipeBufferWriter writer = new PipeBufferWriter();
		writers.put(entity, writer);
		return writer.getNewReader();
	}

	public void removeEntity(Entity entity)
	{
		try
		{
			writers.get(entity).close();
		}
		catch (IOException ex)
		{
			//We're closing down anyway
		}
		finally
		{
			writers.remove(entity);
		}
	}

	public void write(JsonValue jsonVal)
	{
		try
		{
			for (JsonValue i : jsonVal)
			{ //Not a typo, we're iterating over the jsonValue we were handed
				if (writers.containsKey(i.name()))
				{
					try
					{
						writers.get(i.name()).write(i.prettyPrint(
								JsonWriter.OutputType.json, Integer.MAX_VALUE) + "\n");
					}
					catch (IOException ex)
					{
						JsonValue value = Utility.exceptionToJson(ex);
						value.addChild("Invalid String", i);
						errorWriter.write(value.toString());
					}
				}
				else
				{
					JsonValue value = new JsonValue(JsonValue.ValueType.object);
					value.addChild("Invalid String", i);
					errorWriter.write(value.toString());
				}
			}
		}
		catch (IOException ex)
		{
			throw new SerializationException(ex);
		}
	}


}
