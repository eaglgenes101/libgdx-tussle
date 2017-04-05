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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.tussle.collision.Hitbox;
import com.tussle.collision.HitboxLock;
import com.tussle.collision.Hurtbox;
import com.tussle.main.PhysicalBody;

import java.util.LinkedList;
import java.util.List;

public abstract class Terminable extends Action
{
	public abstract void onStart(); //After construction and initialization
	public abstract Terminable eachFrame(); //Each frame
	public void onEnd(Terminable nextState) //Before disposal
	{
		//Nothing by default
	}

	public abstract void onClank(Hitbox ourBox, Hitbox otherBox);
	public abstract Fighter getOwner(); //Who ultimately owns this action
	public abstract PhysicalBody getBody(); //Who to pass callbacks to
	public abstract List<HitboxLock> getHitboxLocks();
	public abstract List<Hurtbox> getHurtboxes();

	public Rectangle getHitboxBounds()
	{
		LinkedList<Rectangle> bounds = new LinkedList<>();
		for (HitboxLock hitboxLock : getHitboxLocks())
		{
			bounds.add(hitboxLock.getBoundingBox());
		}
		if (bounds.size() == 0)
			return null;
		Rectangle returnRect = bounds.getFirst();
		for (Rectangle rectangle : bounds)
		{
			returnRect.merge(rectangle);
		}
		return returnRect;
	}

	public Rectangle getHurtboxBounds()
	{
		List<Hurtbox> hurtboxes = getHurtboxes();
		if (hurtboxes.size() == 0)
			return null;
		Rectangle returnRect = hurtboxes.get(0).getBoundingRectangle();
		for (Hurtbox hurtbox : hurtboxes)
		{
			returnRect.merge(hurtbox.getBoundingRectangle());
		}
		return returnRect;
	}
}
