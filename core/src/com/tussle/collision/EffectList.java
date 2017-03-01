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

package com.tussle.collision;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tussle.fighter.Terminable;
import com.tussle.subaction.Subaction;

import java.util.LinkedList;

/**
 * Created by eaglgenes101 on 3/1/17.
 */
public class EffectList extends LinkedList<Subaction> implements Terminable
{
	Actor target;

	public EffectList(LinkedList<Subaction> subactionList, Actor actor)
	{
		super((LinkedList<Subaction>)(subactionList.clone()));
		target = actor;
	}

	public EffectList(Actor actor)
	{
		super();
		target = actor;
	}

	public void onStart()
	{
		for (Subaction subaction : this)
			subaction.apply(this, target);
	}

	public Terminable eachFrame()
	{
		return null; //That's all, folks
	}

	public void onEnd(Terminable terminable)
	{
		//Nothin'.
	}
}
