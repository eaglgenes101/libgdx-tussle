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

import com.tussle.collision.HitboxLock;
import com.tussle.collision.Hurtbox;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.InputState;
import com.tussle.main.Utility;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by eaglgenes101 on 2/16/17.
 */
public class AirJumpState extends ActionState
{
	int frame;

	public AirJumpState()
	{
		frame = 0;
	}

	public void onStart()
	{
		frame = 0;
	}

	public Terminable eachFrame()
	{
		frame += 1;
		if (frame > 5)
		{
			((Fighter) actor).setYVelocity(10);
			return new AirState();
		}
		else
		{
			if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) > 0)
			{
				((Fighter) actor).setFacing(1);
				((Fighter) actor).setPreferredXVelocity(5);
			}
			else if (((Fighter) actor).getController().getState(InputState.HOR_MOVEMENT) < 0)
			{
				((Fighter) actor).setFacing(-1);
				((Fighter) actor).setPreferredXVelocity(-5);
			}
			if (((Fighter) actor).isGrounded())
				return new LandState();
			else
				return this;
		}
	}

	public void onEnd(Terminable nextAction)
	{
		((Fighter) actor).setPreferredXVelocity(0);
	}
}
