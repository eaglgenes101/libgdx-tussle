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

import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.main.PhysicalBody;
import com.tussle.subaction.Subaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by eaglgenes101 on 3/1/17.
 */
public class EffectList extends Terminable
{
	LinkedList<Subaction> subactions;

	public EffectList(LinkedList<Subaction> subactionList)
	{
		subactions = subactionList;
	}

	public EffectList()
	{
		subactions = new LinkedList<>();
	}

	public void onStart()
	{
		for (Subaction subaction : subactions)
			subaction.apply(this, (PhysicalBody)getActor());
	}

	public boolean act(float delta)
	{
		return true;
	}

	public Terminable eachFrame()
	{
		return null; //That's all, folks
	}

	public void onEnd(Terminable terminable)
	{
		//Nothin'.
	}

	public Fighter getOwner()
	{
		return null; //If this gets called, something is wrong
	}

	public PhysicalBody getBody()
	{
		return null; //If this gets called, something is wrong
	}

	public Set<Hitbox> getHitboxes()
	{
		return Collections.emptySet();
	}

	public Set<Hurtbox> getHurtboxes()
	{
		return Collections.emptySet();
	}
}
