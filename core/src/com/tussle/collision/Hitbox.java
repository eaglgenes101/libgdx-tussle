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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.tussle.main.BaseBody;
import com.tussle.subaction.Subaction;
import com.tussle.main.Utility;

import java.util.LinkedList;
import java.util.List;

public class Hitbox extends Stadium
{
	BaseBody owner;

	public Hitbox(float startx, float starty, float endx, float endy, float rad, BaseBody owner)
	{
		super(startx, starty, endx, endy, rad);
		this.owner = owner;
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

	public EffectList getOwnerOnHitSubactions(BaseBody victim)
	{
		return new EffectList();
	}

	public EffectList getOtherOnHitSubactions(BaseBody victim)
	{
		return new EffectList();
	}

	public EffectList getOwnerOnClankSubactions(BaseBody victim)
	{
		return new EffectList();
	}

	public EffectList getOtherOnClankSubactions(BaseBody victim)
	{
		return new EffectList();
	}
}
