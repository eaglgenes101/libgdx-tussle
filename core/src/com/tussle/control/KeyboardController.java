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

package com.tussle.control;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

public class KeyboardController implements Controller
{
	java.util.Map<Integer, InputToken> inputMap;
	java.util.Map<Integer, InputToken> releaseMap;
	java.util.ArrayDeque<InputToken> buffer;
	java.util.LinkedHashMap<InputState, InputToken> initials;
	java.util.HashMap<InputState, Integer> currents;
	int maxFrames;
	int currentFrames;

	public KeyboardController(java.util.Map<Integer, InputToken> inputDict,
							  java.util.Map<Integer, InputToken> releaseDict,
							  int len)
	{
		inputMap = inputDict;
		releaseMap = releaseDict;
		maxFrames = len;
		this.clearInputs();
	}

	public void setInitToken(InputToken token)
	{
		initials.put(token.state(), token);
	}

	public void clearInputs()
	{
		currentFrames = 0;
		buffer = new java.util.ArrayDeque<>();
		initials = new java.util.LinkedHashMap<>();
		currents = new java.util.HashMap<>();
		setInitToken(new InputToken(0, InputState.HOR_MOVEMENT));
		setInitToken(new InputToken(0, InputState.VERT_MOVEMENT));
		setInitToken(new InputToken(0, InputState.HOR_ACTION));
		setInitToken(new InputToken(0, InputState.VERT_ACTION));
		setInitToken(new InputToken(0, InputState.ATTACK));
		setInitToken(new InputToken(0, InputState.SHIELD));
		setInitToken(new InputToken(0, InputState.JUMP));
		setInitToken(new InputToken(0, InputState.SPECIAL));
		setInitToken(new InputToken(0, InputState.TAUNT));
		setInitToken(new InputToken(0, InputState.PAUSE));
	}

	public void pumpBuffer()
	{
		if (currentFrames > maxFrames)
		{
			for (;;)
			{
				InputToken i = buffer.removeFirst();
				if (i.state() == InputState.FRAME_DIVIDE)
					break; //Exit point
				else
					initials.put(i.state(), i);
			}
			currentFrames--;
		}
		buffer.addLast(new InputToken(0, InputState.FRAME_DIVIDE));
		currentFrames++;
	}

	public LinkedList<InputToken> getInputs()
	{
		LinkedList<InputToken> returnList = new LinkedList<>();
		for (Map.Entry<InputState, InputToken> pair : initials.entrySet())
		{
			returnList.addFirst(pair.getValue());
		}
		returnList.addAll(buffer);
		return returnList;
	}

	public int matchInput(BufferScanner[] candidates)
	{
		int maxPos = 0;
		int maxMatch = -1;
		for (int i = 0; i < candidates.length; i++)
		{
			int frameCount = candidates[i].apply(buffer);
			if (frameCount > maxPos)
			{
				maxMatch = i;
				maxPos = frameCount;
			}
		}
		if (maxMatch == -1) return -1;
		for (int i = 0; i < maxPos; i++)
		{
			if (buffer.size() == 0) break;
			InputToken match = buffer.removeFirst();
			if (match.state() == InputState.FRAME_DIVIDE) currentFrames--;
			initials.put(match.state(), match);
		}
		return maxMatch;
	}

	public int getState(InputState state)
	{
		return currents.getOrDefault(state, 0);
	}

	public boolean keyDown(int keycode)
	{
		if (inputMap.containsKey(keycode))
		{
			InputToken i = inputMap.get(keycode);
			buffer.addLast(i);
			currents.put(i.state(), i.intensity());
			return true; //OM NOM NOM
		}
		else return false; //Pass!
	}

	public boolean keyTyped(char character)
	{
		return false; //Pass!
	}

	public boolean keyUp(int keycode)
	{
		if (releaseMap.containsKey(keycode))
		{
			InputToken i = releaseMap.get(keycode);
			buffer.addLast(i);
			currents.put(i.state(), i.intensity());
			return true; //OM NOM NOM
		}
		else return false; //Pass!
	}

	public boolean mouseMoved(int screenX, int screenY)
	{
		return false; //Pass!
	}

	public boolean scrolled(int amount)
	{
		return false; //Pass!
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false; //Pass!
	}

	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false; //Pass!
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false; //Pass!
	}
}
