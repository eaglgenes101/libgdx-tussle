package com.tussle.actionstate;

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.InputState;

/**
 * Created by eaglgenes101 on 2/9/17.
 */
public class JumpState extends ActionState
{
	int frame;

	public JumpState()
	{
		frame = 0;
	}

	public void onStart()
	{
		frame = 0;
	}

	public ActionState eachFrame()
	{
		frame += 1;
		if (frame > 5)
		{
			if (((Fighter) actor).getController().getState(InputState.JUMP) > 0)
				((Fighter) actor).setYVelocity(10);
			else
				((Fighter) actor).setYVelocity(7);
			return new IdleState();
		}
		else
			return this;
	}

	public void onEnd(Terminable nextAction)
	{
	}
}
