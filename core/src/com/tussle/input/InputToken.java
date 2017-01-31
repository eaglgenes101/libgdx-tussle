package com.tussle.input;

/**
 * Created by eaglgenes101 on 1/18/17.
 */
public class InputToken
{
	int intensity;
	InputState state;

	public InputToken(int i, InputState s)
	{
		intensity = i;
		state = s;
	}

	public int intensity()
	{
		return intensity;
	}

	public InputState state()
	{
		return state;
	}

	public boolean equals(Object o)
	{
		return o instanceof InputToken && ((InputToken) o).intensity() == intensity && ((InputToken) o).state().equals(state);
	}
}
