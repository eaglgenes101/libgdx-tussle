package com.tussle.input;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
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
