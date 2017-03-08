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

package com.tussle.actionstate;

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.BufferChecker;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;

public class IdleState extends ActionState
{
	public void onStart()
	{
		((Fighter)actor).setPreferredXVelocity(0);
	}

	public Terminable eachFrame()
	{
		if (!((Fighter) actor).isGrounded())
			return new AirState();
		if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) > 0)
		{
			((Fighter) actor).setFacing(1);
			return new WalkState();
		}
		else if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) < 0)
		{
			((Fighter) actor).setFacing(-1);
			return new WalkState();
		}
		BufferChecker[] b = {
				new BufferChecker(12, new InputToken(1, InputState.JUMP))
		};
		int choice = ((Fighter)actor).getController().matchInput(b);
		switch (choice)
		{
			case -1:
				return this;
			case 0:
				return new JumpState();
			default:
				return null; //Something went wrong
		}
	}

	public void onEnd(Terminable nextAction)
	{
	}
}
