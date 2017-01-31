package com.tussle.actionstate;

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;

/**
 * Created by eaglgenes101 on 1/25/17.
 */
public class WalkState extends ActionState
{
	int side;
	int frame;

	public WalkState()
	{
		side = 0;
		frame = 0;
	}

	public void onStart()
	{
		side = ((Fighter)actor).getFacing();
		frame = 0;
		((Fighter)actor).setPreferredXVelocity(5*side); //Replace this constant at some point
	}

	public ActionState eachFrame()
	{
		frame += 1;
		if (frame > 10)
			return new IdleState();
		else
			return this;
	}

	public void onEnd(Terminable nextAction)
	{
		((Fighter)actor).setPreferredXVelocity(0);
	}
}
