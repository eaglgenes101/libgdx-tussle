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

package com.tussle.fighter;

import com.tussle.collision.Hitbox;
import com.tussle.collision.HitboxLock;
import com.tussle.collision.Hurtbox;
import com.tussle.main.Utility;

import java.util.*;

/**
 * Created by eaglgenes101 on 3/10/17.
 */
public class PersistentHitboxStatusEffect extends StatusEffect
{
	LinkedList<HitboxLock> associatedLocks;
	Hitbox associatedHitbox;

	public PersistentHitboxStatusEffect(float startx, float starty, float endx, float endy, float radius)
	{
		associatedHitbox = new Hitbox(startx, starty, endx, endy, radius, this);
		associatedLocks = new LinkedList<>();
		HitboxLock associatedLock = new HitboxLock();
		associatedLock.addHitbox(associatedHitbox);
		associatedLocks.add(associatedLock);
	}

	public void onStart()
	{
		//Nothing...
	}

	public StatusEffect eachFrame()
	{
		return this;
	}

	public void onEnd(StatusEffect nextEffect)
	{
		//Nothing...
	}

	public List<HitboxLock> getHitboxLocks()
	{
		return associatedLocks;
	}

	public List<Hurtbox> getHurtboxes()
	{
		return Utility.emptyHurtboxList;
	}
}
