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

import com.badlogic.gdx.math.Rectangle;
import com.tussle.main.Utility;

import java.util.*;
import java.util.List;

/**
 * Created by eaglgenes101 on 3/8/17.
 */
public class HitboxLock
{
	LinkedList<Hitbox> hitboxes;

	public HitboxLock()
	{
		hitboxes = new LinkedList<>();
	}

	public HitboxLock(List<Hitbox> hitboxList)
	{
		hitboxes = new LinkedList<>(hitboxList);
	}

	public void addHitbox(Hitbox hitbox)
	{
		if (hitboxes.contains(hitbox))
			hitboxes.remove(hitbox);
		hitboxes.add(hitbox);
	}

	public void removeHitbox(Hitbox hitbox)
	{
		if (hitboxes.contains(hitbox))
			hitboxes.remove(hitbox);
	}

	public List<Hitbox> getHitboxes()
	{
		return hitboxes;
	}

	public Map<Hitbox, Hitbox> getClanks(HitboxLock other)
	{
		Map<Hitbox, Hitbox> returnMap = new HashMap<>();
		for (Hitbox hitbox : hitboxes)
			for (Hitbox target : other.getHitboxes())
				if (Utility.intersectStadia(hitbox.getTransformedStart(),
						hitbox.getTransformedEnd(), target.getTransformedStart(),
						target.getTransformedEnd(),
						hitbox.getTransformedRadius()+target.getTransformedRadius()))
					if (hitbox.doesHit(target))
					{
						returnMap.put(hitbox, target);
						break;
					}
		return returnMap;
	}

	public Rectangle getBoundingBox()
	{
		if (hitboxes.size() == 0)
		{
			return null;
		}
		Rectangle returnRect = new Rectangle(hitboxes.getFirst().getBoundingRectangle());
		for (Hitbox hitbox : hitboxes)
		{
			returnRect = returnRect.merge(hitbox.getBoundingRectangle());
		}
		return returnRect;
	}

	public Map<Hitbox, Hurtbox> getHits(List<Hurtbox> other)
	{
		Map<Hitbox, Hurtbox> returnMap = new HashMap<>();
		for (Hitbox hitbox : hitboxes)
			for (Hurtbox target : other)
				if (Utility.intersectStadia(hitbox.getTransformedStart(),
						hitbox.getTransformedEnd(), target.getTransformedStart(),
						target.getTransformedEnd(),
						hitbox.getTransformedRadius()+target.getTransformedRadius()))
					if (hitbox.doesHit(target))
					{
						returnMap.put(hitbox, target);
						break;
					}
		return returnMap;
	}
}
