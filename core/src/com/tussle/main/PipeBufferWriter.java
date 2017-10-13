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

import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class PipeBufferWriter extends Writer
{
	Set<PipeBufferReader> outputs;

	public PipeBufferWriter()
	{
		outputs = new HashSet<>();
	}

	public void open(PipeBufferReader reader)
	{
		if (!outputs.contains(reader))
			outputs.add(reader);
	}

	//Factory method for getting readers to this writer
	public PipeBufferReader getNewReader()
	{
		return new PipeBufferReader(this);
	}

	public void close()
	{
		for (PipeBufferReader reader : outputs)
		{
			outputs.remove(reader);
		}
	}

	public boolean close(PipeBufferReader reader)
	{
		return outputs.remove(reader);
	}

	public boolean has(PipeBufferReader reader)
	{
		return outputs.contains(reader);
	}

	public Set<PipeBufferReader> getOutputs()
	{
		return outputs;
	}

	public void flush()
	{
		//Nothing, as this writer writes everything instantly
	}

	public void write(char[] cbuf, int off, int len)
	{
		for (PipeBufferReader reader : outputs)
		{
			reader.append(cbuf, off, len);
		}
	}
}
