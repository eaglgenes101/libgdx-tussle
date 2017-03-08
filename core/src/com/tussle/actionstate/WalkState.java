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
		((Fighter)actor).setPreferredXVelocity(7*side); //Replace this constant at some point
	}

	public Terminable eachFrame()
	{
		if (((Fighter) actor).getVelocity().x*side < 7)
			((Fighter) actor).xAccel(0.3f);
		frame += 1;
		if (frame > 0)
		{
			if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT)*side <= 0)
				return new IdleState();
			else if (!((Fighter)actor).isGrounded())
				return new AirState();
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
		((Fighter) actor).setPreferredXVelocity(0);
	}
}
