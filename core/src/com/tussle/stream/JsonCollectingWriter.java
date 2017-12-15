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
import com.tussle.main.Utility;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JsonCollectingWriter implements JsonSource
{
	Map<Entity, Reader> readers;
	Reader errorReader;

	public JsonCollectingWriter(Reader err)
	{
		readers = new HashMap<>();
		errorReader = err;
	}

	public void removeEntity(Entity entity)
	{
		try
		{
			readers.get(entity).close();
		}
		catch (IOException ex)
		{
			//Doesn't really matter
		}
		finally
		{
			readers.remove(entity);
		}
	}

	public PipeBufferWriter openWriterFor(Entity entity)
	{
		PipeBufferWriter writer = new PipeBufferWriter();
		readers.put(entity, writer.getNewReader());
		return writer;
	}

	public void remove(Entity entity)
	{
		try
		{
			readers.get(entity).close();
		}
		catch (IOException ex)
		{
			//We're closing down anyway
		}
		finally
		{
			readers.remove(entity);
		}
	}

	public JsonValue read()
	{
		JsonValue toReturn = new JsonValue(JsonValue.ValueType.object);
		for (Map.Entry<Entity, Reader> entry : readers.entrySet())
		{
			try
			{
				if (entry.getValue().ready())
				{
					String str = Utility.readAll(entry.getValue());
					toReturn.addChild(entry.getKey().toString(), new JsonValue(str));
				}
			}
			catch (IOException ex)
			{
				toReturn.addChild(entry.getKey().toString(), Utility.exceptionToJson(ex));
			}
		}
		//Now read the error stream
		try
		{
			if (errorReader.ready())
			{
				String str = Utility.readAll(errorReader);
				toReturn.addChild("Error", new JsonValue(str));
			}
		}
		catch (IOException ex)
		{
			toReturn.addChild("Error", Utility.exceptionToJson(ex));
		}
		return toReturn;
	}

	public boolean ready()
	{
		//Returns if any of the dependent readers are themselves ready
		for (Reader reader : readers.values())
		{
			try
			{
				if (reader.ready()) return true;
			}
			catch (IOException ex)
			{
				//I guess that's a no...
			}
		}
		return false;
	}
}
