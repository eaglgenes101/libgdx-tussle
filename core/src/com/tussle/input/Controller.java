package com.tussle.input;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by eaglgenes101 on 1/18/17.
 */
public interface Controller
{
	void flushInputs();
	void pumpBuffer();
	int matchInput(Function<LinkedList<InputToken>, Integer>[] funcs);
	int getState(InputState state);
}
