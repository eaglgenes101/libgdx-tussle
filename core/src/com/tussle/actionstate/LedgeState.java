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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.BufferChecker;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;
import com.tussle.stage.Ledge;

/**
 * Created by eaglgenes101 on 2/21/17.
 */
public class LedgeState extends ActionState
{
	Ledge holding;
	int frame;
	int side;

	public LedgeState(Ledge ledge)
	{
		holding = ledge;
	}

	public void onStart()
	{
		frame = 0;
		side = holding.getFacing();
		if (side == 1)
			actor.setPosition(holding.getClingX(), holding.getClingY(), Align.topRight);
		else if (side == -1)
			actor.setPosition(holding.getClingX(), holding.getClingY(), Align.topLeft);
		((Fighter) actor).setVelocity(Vector2.Zero);
		((Fighter) actor).setPreferredXVelocity(0);
		((Fighter) actor).setPreferredYVelocity(0);
	}

	public ActionState eachFrame()
	{
		if (side == 1)
			actor.setPosition(holding.getClingX(), holding.getClingY(), Align.topRight);
		else if (side == -1)
			actor.setPosition(holding.getClingX(), holding.getClingY(), Align.topLeft);
		((Fighter) actor).setVelocity(Vector2.Zero);
		((Fighter) actor).setPreferredXVelocity(0);
		((Fighter) actor).setPreferredYVelocity(0);
		if (frame > 240)
			return new AirState();
		BufferChecker[] b = {
				new BufferChecker(12, new InputToken(1, InputState.JUMP)),
				new BufferChecker(12, new InputToken(-holding.getFacing(),
						InputState.HOR_MOVEMENT))
		};
		int choice = ((Fighter)actor).getController().matchInput(b);
		switch (choice)
		{
			case -1:
				return this;
			case 0:
				return new AirJumpState();
			case 1:
				return new AirState();
			default:
				return null; //Something went wrong
		}
	}

	public void onEnd(Terminable nextAction)
	{
		((Fighter) actor).setPreferredYVelocity(-30);
	}
}
