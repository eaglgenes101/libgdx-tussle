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

import com.badlogic.gdx.utils.JsonValue;
import com.tussle.main.JsonSource;
import com.tussle.main.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class JsonCollectingWriter implements JsonSource
{
	Map<String, BufferedReader> readers;
	Reader errors;
	Deque<JsonValue> jsonValues;

	public JsonCollectingWriter(Reader err)
	{
		readers = new HashMap<>();
		addEntity("Error", err);
		jsonValues = new LinkedList<>();
	}

	public void addEntity(String name, Reader reader)
	{
		readers.put(name, new BufferedReader(reader));
	}

	public JsonValue read()
	{
		JsonValue toReturn = new JsonValue(JsonValue.ValueType.object);
		for (Map.Entry<String, BufferedReader> entry : readers.entrySet())
		{
			try
			{
				if (entry.getValue().ready())
				{
					String str = Utility.readAll(entry.getValue())
					toReturn.addChild(entry.getKey(), new JsonValue(str));
				}
			}
			catch (IOException ex)
			{
				toReturn.addChild(entry.getKey(), Utility.exceptionToJson(ex));
			}
		}
		return toReturn;
	}
}
