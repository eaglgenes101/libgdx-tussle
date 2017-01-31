package com.tussle.actionstate;

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.BufferChecker;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;

/**
 * Created by eaglgenes101 on 1/25/17.
 */
public class IdleState extends ActionState
{
	public void onStart()
	{
		((Fighter)actor).setPreferredXVelocity(0);
	}

	public ActionState eachFrame()
	{
		BufferChecker[] b = {
				new BufferChecker(12, new InputToken(-1, InputState.HOR_MOVEMENT)),
				new BufferChecker(12, new InputToken(1, InputState.HOR_MOVEMENT))
		};
		int choice = ((Fighter)actor).getController().matchInput(b);
		switch (choice)
		{
			case -1:
				return this;
			case 0:
				((Fighter)actor).setFacing(-1);
				return new WalkState();
			case 1:
				((Fighter)actor).setFacing(1);
				return new WalkState();
			default:
				return null; //Something went wrong
		}
	}

	public void onEnd(Terminable nextAction)
	{
		//Do nothing
	}
}
