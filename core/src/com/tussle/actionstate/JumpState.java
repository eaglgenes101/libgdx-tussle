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
import com.tussle.input.InputState;

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
			return new AirState();
		}
		else
		{
			if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) > 0)
				((Fighter) actor).setFacing(1);
			else if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) < 0)
				((Fighter) actor).setFacing(-1);
			if (!((Fighter) actor).isGrounded())
				return new AirState();
			else
				return this;
		}
	}

	public void onEnd(Terminable nextAction)
	{
	}
}
