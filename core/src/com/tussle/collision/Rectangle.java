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

import org.apache.commons.math3.util.FastMath;

/**
 * Shamelessly copied by eaglgenes101 on 4/13/17.
 * Below is the license for the file this was derived from.
 *******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/

public strictfp class Rectangle
{
	public double x, y;
	public double width, height;

	public Rectangle (double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle (Rectangle rect)
	{
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}

	public Rectangle set (double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}

	public double getX ()
	{
		return x;
	}

	public Rectangle setX (double x)
	{
		this.x = x;
		return this;
	}

	public double getY ()
	{
		return y;
	}

	public Rectangle setY (double y)
	{
		this.y = y;
		return this;
	}

	public double getWidth ()
	{
		return width;
	}

	public Rectangle setWidth (double width)
	{
		this.width = width;
		return this;
	}

	public double getHeight ()
	{
		return height;
	}

	public Rectangle setHeight (double height)
	{
		this.height = height;
		return this;
	}

	public Rectangle setPosition (double x, double y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public Rectangle setSize (double width, double height)
	{
		this.width = width;
		this.height = height;
		return this;
	}

	public Rectangle setSize (double sizeXY)
	{
		this.width = sizeXY;
		this.height = sizeXY;
		return this;
	}

	public boolean contains (double x, double y)
	{
		return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
	}

	public boolean overlaps (Rectangle r)
	{
		return x <= r.x + r.width && x + width >= r.x && y <= r.y + r.height && y + height >= r.y;
	}

	public Rectangle set (Rectangle rect)
	{
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;

		return this;
	}

	public Rectangle merge (Rectangle rect)
	{
		double minX = FastMath.min(x, rect.x);
		double maxX = FastMath.max(x + width, rect.x + rect.width);
		x = minX;
		width = maxX - minX;

		double minY = FastMath.min(y, rect.y);
		double maxY = FastMath.max(y + height, rect.y + rect.height);
		y = minY;
		height = maxY - minY;

		return this;
	}

	public Rectangle merge (double x, double y)
	{
		double minX = FastMath.min(this.x, x);
		double maxX = FastMath.max(this.x + width, x);
		this.x = minX;
		this.width = maxX - minX;

		double minY = FastMath.min(this.y, y);
		double maxY = FastMath.max(this.y + height, y);
		this.y = minY;
		this.height = maxY - minY;

		return this;
	}

	public double getAspectRatio ()
	{
		return (height == 0) ? Float.NaN : width / height;
	}

	public Rectangle setCenter (double x, double y)
	{
		setPosition(x - width / 2, y - height / 2);
		return this;
	}

	public Rectangle fitOutside (Rectangle rect)
	{
		double ratio = getAspectRatio();

		if (ratio > rect.getAspectRatio()) {
			setSize(rect.height * ratio, rect.height);
		} else {
			setSize(rect.width, rect.width / ratio);
		}

		setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
		return this;
	}

	public Rectangle fitInside (Rectangle rect)
	{
		double ratio = getAspectRatio();

		if (ratio < rect.getAspectRatio()) {
			setSize(rect.height * ratio, rect.height);
		} else {
			setSize(rect.width, rect.width / ratio);
		}

		setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
		return this;
	}

	public String toString ()
	{
		return "[" + x + "," + y + "," + width + "," + height + "]";
	}

	public double area ()
	{
		return this.width * this.height;
	}

	public double perimeter ()
	{
		return 2 * (this.width + this.height);
	}

}
