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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.stage.StageElement;

import java.util.function.Function;

/**
 * Created by eaglgenes101 on 2/28/17.
 */

public class changeVelocitySubaction extends Subaction
{
	float xVel;
	float yVel;

	public changeVelocitySubaction(float dx, float dy)
	{
		xVel = dx;
		yVel = dy;
	}

	public void apply(Terminable action, Fighter actor)
	{
		actor.setXVelocity(xVel);
		actor.setYVelocity(yVel);
	}

	public void apply(Terminable action, StageElement actor)
	{
		actor.setXVelocity(xVel);
		actor.setYVelocity(yVel);
	}
}
