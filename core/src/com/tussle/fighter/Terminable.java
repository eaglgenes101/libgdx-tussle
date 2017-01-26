package com.tussle.fighter;

/**
 * Created by eaglgenes101 on 1/26/17.
 */
public interface Terminable
{
	void onStart(); //After construction and initialization
	Terminable eachFrame(); //Each frame
	void onEnd(Terminable nextState); //Before disposal
}
