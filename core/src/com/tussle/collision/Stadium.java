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
import com.tussle.main.Intersector;

/**
 * Created by eaglgenes101 on 3/8/17.
 */
public class Stadium
{
	public double startx, starty, endx, endy;
	public double radius;

	public Stadium()
	{
		//Nothing
	}

	public Stadium(double startx, double starty, double endx, double endy, double radius)
	{
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
		this.radius = radius;
	}

	public double getStartx()
	{
		return startx;
	}

	public double getStarty()
	{
		return starty;
	}

	public double getEndx()
	{
		return endx;
	}

	public double getEndy()
	{
		return endy;
	}

	public double getRadius()
	{
		return radius;
	}

	public Stadium setStartX(double startx)
	{
		this.startx = startx;
		return this;
	}

	public Stadium setStartY(double starty)
	{
		this.starty = starty;
		return this;
	}

	public Stadium setEndX(double endx)
	{
		this.endx = endx;
		return this;
	}

	public Stadium setEndY(double endy)
	{
		this.endy = endy;
		return this;
	}

	public Stadium setRadius(double radius)
	{
		this.radius = radius;
		return this;
	}

	public Stadium setStart(double startx, double starty)
	{
		this.startx = startx;
		this.starty = starty;
		return this;
	}

	public Stadium setEnd(double endx, double endy)
	{
		this.endx = endx;
		this.endy = endy;
		return this;
	}

	public Stadium set(Stadium stad)
	{
		this.startx = stad.startx;
		this.starty = stad.starty;
		this.endx = stad.endx;
		this.endy = stad.endy;
		this.radius = stad.radius;
		return this;
	}

	public double getCenterX()
	{
		return (this.startx+this.endx)/2;
	}

	public double getCenterY()
	{
		return (this.starty+this.endy)/2;
	}

	public boolean contains(double x, double y)
	{
		if (startx == endx && starty == endy)
			return (startx-x)*(startx-x)+(starty-y)*(starty-y) <= radius*radius;
		return Intersector.d2SegmentPoint(startx, starty, endx, endy, x, y)
				< radius*radius;
	}
}
