package com.tussle.input;

import com.badlogic.gdx.InputProcessor;
import com.tussle.input.*;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by eaglgenes101 on 1/18/17.
 */
public class KeyboardController implements InputProcessor, Controller
{
	java.util.Map<Integer, InputToken> inputMap;
	java.util.Map<Integer, InputToken> releaseMap;
	java.util.LinkedList<InputToken> buffer;
	java.util.LinkedHashMap<InputState, InputToken> initials;
	java.util.HashMap<InputState, Integer> currents;
	int maxFrames;
	int currentFrames;

	public KeyboardController(java.util.Map<Integer, InputToken> inputDict,
							  java.util.Map<Integer, InputToken> releaseDict, int len)
	{
		inputMap = inputDict;
		releaseMap = releaseDict;
		maxFrames = len;
		this.flushInputs();
	}

	public void setInitToken(InputToken token)
	{
		initials.put(token.state(), token);
	}

	public void flushInputs()
	{
		currentFrames = 0;
		buffer = new java.util.LinkedList<>();
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

	public int matchInput(Function<LinkedList<InputToken>, Integer>[] candidates)
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
		if (currents.containsKey(state))
		{
			return currents.get(state);
		}
		else
			return 0;
	}

	public boolean keyDown(int keycode)
	{
		if (inputMap.containsKey(keycode))
		{
			buffer.addLast(inputMap.get(keycode));
			currents.put(inputMap.get(keycode).state(), inputMap.get(keycode).intensity());
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
			buffer.addLast(releaseMap.get(keycode));
			currents.put(releaseMap.get(keycode).state(), releaseMap.get(keycode).intensity());
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
