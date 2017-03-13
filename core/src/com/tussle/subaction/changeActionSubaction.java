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

package com.tussle.subaction;

import com.tussle.actionstate.ActionState;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.main.PhysicalBody;

/**
 * Created by eaglgenes101 on 2/28/17.
 */
public class changeActionSubaction extends Subaction
{
	ActionState state;

	public changeActionSubaction(ActionState newState)
	{
		state = newState;
	}

	public void apply(Terminable action, PhysicalBody actor)
	{
		if (actor instanceof Fighter)
		{
			((Fighter)actor).setActionState(state);
		}
	}
}
