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
import com.tussle.collision.Hurtbox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eaglgenes101 on 3/10/17.
 */
public class PersistentHurtboxStatusEffect extends StatusEffect
{
	HashSet<Hurtbox> hurtboxes;

	public PersistentHurtboxStatusEffect(float startx, float starty, float endx, float endy, float radius)
	{
		Hurtbox associatedHurtbox = new Hurtbox(startx, starty, endx, endy, radius, this);
		hurtboxes.add(associatedHurtbox);
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

	public Set<Hitbox> getHitboxes()
	{
		return Collections.emptySet();
	}

	public Set<Hurtbox> getHurtboxes()
	{
		return hurtboxes;
	}

}
