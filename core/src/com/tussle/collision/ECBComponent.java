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

import com.badlogic.ashley.core.Component;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public class ECBComponent implements Component
{
	private CollisionBox ecb;

	public ECBComponent()
	{
		ecb = new CollisionBox();
	}

	public ECBComponent(Stadium start)
	{
		ecb = new CollisionBox(start);
	}

	public CollisionBox getEcb()
	{
		return ecb;
	}

	public void update(double x, double y, double angle, double scale, boolean flipped)
	{
		ecb.setPosition(x, y);
		ecb.setRotation(angle);
		ecb.setScale(scale);
		ecb.setFlipped(flipped);
		ecb.setAreas();
	}

	public void setStadium(Stadium newStart)
	{
		ecb.setStadium(newStart);
		ecb.setAreas();
	}
}
