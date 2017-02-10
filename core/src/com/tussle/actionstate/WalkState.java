package com.tussle.actionstate;

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.BufferChecker;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;

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
		{
			if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT)*side <= 0)
				return new IdleState();
		}
		BufferChecker[] b = {
				new BufferChecker(12, new InputToken(-side, InputState.HOR_MOVEMENT)),
				new BufferChecker(12, new InputToken(1, InputState.JUMP))
		};
		int choice = ((Fighter) actor).getController().matchInput(b);
		switch (choice)
		{
			case -1:
				return this;
			case 0:
				((Fighter) actor).setFacing(-side);
				return new WalkState();
			case 1:
				return new JumpState();
			default:
				return null;
		}

	}

	public void onEnd(Terminable nextAction)
	{
	}
}
