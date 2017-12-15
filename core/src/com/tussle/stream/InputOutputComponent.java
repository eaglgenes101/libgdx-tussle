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

import com.badlogic.ashley.core.Component;

import java.io.Reader;
import java.io.Writer;

public class InputOutputComponent implements Component
{
	Reader stdin;
	Writer stdout;
	Writer stderr;

	public InputOutputComponent()
	{
	}

	public void setStdin(Reader in)
	{
		stdin = in;
	}

	public void setStdout(Writer out)
	{
		stdout = out;
	}

	public void setStderr(Writer err)
	{
		stderr = err;
	}

	public Reader getStdin()
	{
		return stdin;
	}

	public Writer getStdout()
	{
		return stdout;
	}

	public Writer getStderr()
	{
		return stderr;
	}
}
