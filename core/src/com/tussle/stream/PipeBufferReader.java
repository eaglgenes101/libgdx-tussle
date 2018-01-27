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

import org.apache.commons.math3.util.FastMath;

import java.io.Reader;

//Please, use the factory method of PipeBufferWriter to get new PipeBufferReaders
//This class won't work if instantiated directly
public class PipeBufferReader extends Reader
{
	StringBuilder buffer;
	PipeBufferWriter writer;

	PipeBufferReader(PipeBufferWriter out)
	{
		buffer = new StringBuilder();
		out.open(this);
		writer = out;
	}

	public void append(char[] buf, int off, int len)
	{
		buffer.append(buf, off, len);
	}

	public void close()
	{
		writer.close(this);
	}

	public int read(char[] buf, int off, int len)
	{
		if (buffer.length() == 0)
			return -1;
		int tryLen = FastMath.min(len, buffer.length());
		buffer.getChars(0, tryLen, buf, off);
		buffer.delete(0, tryLen);
		return tryLen;
	}

	public boolean ready()
	{
		return buffer.length() > 0;
	}

	public PipeBufferWriter getWriter()
	{
		return writer;
	}
}
