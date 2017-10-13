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

package com.tussle.main;

import com.badlogic.gdx.utils.JsonValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JsonParsingWriterTest
{
	public JsonParsingWriter writer;
	public PipeBufferWriter err;
	public PipeBufferReader read;

	@BeforeEach
	public void init()
	{
		err = new PipeBufferWriter();
		writer = new JsonParsingWriter(err);
		read = err.getNewReader();
	}

	@Test
	@DisplayName("See if ready() works as expected")
	void readyCheckTest() throws IOException
	{
		assertFalse(writer.ready());
		assertFalse(read.ready());
		writer.write("[1, \"B\", {\"C\":2}]");
		assertTrue(writer.ready());
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
				while (writer.ready()) writer.read();
			},
			() -> "Shouldn't take forever to read all json values"
		);
		assertFalse(writer.ready());
	}

	@Test
	@DisplayName("Parsing of a complete JSON stream")
	void parseCompleteTest() throws IOException
	{
		writer.write("[1, \"B\", {\"C\":2}]");
		JsonValue value = writer.read();
		assertTrue(value.isArray());
		assertTrue(value.child().isLong());
		assertTrue(value.child().next().isString());
		assertTrue(value.child().next().next().isObject());
	}

	@Test
	@DisplayName("Parsing of a JSON stream with extraneous tokens")
	void parseRedundantTest() throws IOException
	{
		writer.write("\n\"A\"/**/ //\n [1, \"B\", {\"C\":2}]");
		JsonValue value = writer.read();
		assertTrue(value.isArray());
		assertTrue(value.child().isLong());
		assertTrue(value.child().next().isString());
		assertTrue(value.child().next().next().isObject());
	}

	@Test
	@DisplayName("Parsing of multiple JSON objects in a stream")
	void parseMultipleTest() throws IOException
	{
		writer.write("[1, \"B\", {\"C\":2}] {\"2\": null, \"S\": 4}");
		JsonValue value1 = writer.read();
		assertTrue(value1.isArray());
		assertTrue(value1.child().isLong());
		assertTrue(value1.child().next().isString());
		assertTrue(value1.child().next().next().isObject());
		JsonValue value2 = writer.read();
		assertTrue(value2.isObject());
		assertTrue(value2.child().name().equals("2"));
		assertTrue(value2.child().isNull());
		assertTrue(value2.child().next().name().equals("S"));
		assertTrue(value2.child().next().isLong());
	}

	@Test
	@DisplayName("Parsing despite syntax errors out of the object")
	void parseRobustTest() throws IOException
	{
		writer.write("{ture, flase} [1, \"B\", {\"C\":2}]");
		JsonValue value;
		value = writer.read();
		assertTrue(read.ready());
		char[] buf = new char[14];
		assertTrue(read.read(buf) == 14);
		//System.out.println("\""+new String(buf)+"\"");
		assertTrue(new String(buf).equals("{ture, flase} "));
		assertTrue(value.isArray());
		assertTrue(value.child().isLong());
		assertTrue(value.child().next().isString());
		assertTrue(value.child().next().next().isObject());
	}

	@Test
	@DisplayName("Parsing on despite interruptions in stream")
	void parseReentrant() throws IOException
	{
		writer.write("[1, \"B\", {");
		assertFalse(writer.ready());
		assertFalse(read.ready());
		writer.write("\"C\":2}]");
		writer.flush();
		char[] buf = new char[999];
		int numChars = read.read(buf);
		String str = new String(buf, 0, numChars);
		System.out.println(str);
		assertFalse(read.ready());
		assertTrue(writer.ready());
		JsonValue value = writer.read();
		assertTrue(value.isArray());
		assertTrue(value.child().isLong());
		assertTrue(value.child().next().isString());
		assertTrue(value.child().next().next().isObject());
	}
}