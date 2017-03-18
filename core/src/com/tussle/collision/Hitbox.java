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

import com.tussle.fighter.Terminable;
import com.tussle.main.PhysicalBody;
import com.tussle.main.Utility;

public class Hitbox extends Stadium
{
	Terminable associated;

	public Hitbox(float startx, float starty, float endx, float endy, float rad,
				  Terminable associated)
	{
		super(startx, starty, endx, endy, rad);
		this.associated = associated;
	}

	public boolean doesHit(Hitbox other)
	{
		return Utility.intersectStadia(getTransformedStart(), getTransformedEnd(),
				other.getTransformedStart(), other.getTransformedEnd(),
				getTransformedRadius()+other.getTransformedRadius());
	}

	public boolean doesHit(Hurtbox other)
	{
		return Utility.intersectStadia(getTransformedStart(), getTransformedEnd(),
				other.getTransformedStart(), other.getTransformedEnd(),
				getTransformedRadius()+other.getTransformedRadius());
	}

	public Terminable getAssociated()
	{
		return associated;
	}

	public EffectList getOwnerOnHitSubactions(PhysicalBody victim)
	{
		return new EffectList();
	}

	public EffectList getOtherOnHitSubactions(PhysicalBody victim)
	{
		return new EffectList();
	}

	public boolean doesClank(Hitbox other)
	{
		return false; //Default
	}
}
