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

package com.tussle.input;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

public class BufferChecker implements Function<LinkedList<InputToken>, Integer>
{
	int bufferLength;
	InputToken compareToken;

	public BufferChecker(int bufferLen, InputToken matchToken)
	{
		bufferLength = bufferLen;
		compareToken = matchToken;
	}

	public Integer apply(LinkedList<InputToken> list)
	{
		int frameCount = 0;
		int listPos = list.size();
		for (Iterator<InputToken> i = list.descendingIterator(); i.hasNext();)
		{
			InputToken checkToken = i.next();
			listPos--;
			if (checkToken.equals(compareToken))
			{
				return listPos;
			}
			else if (checkToken.state() == InputState.FRAME_DIVIDE)
			{
				frameCount++;
				if (frameCount > bufferLength)
					return -1;
			}
		}
		return -1;
	}
}
