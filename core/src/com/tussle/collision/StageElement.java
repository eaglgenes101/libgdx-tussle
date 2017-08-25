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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public abstract class StageElement
{
	protected double x = 0, y = 0;
	protected double originX = 0, originY = 0;
	protected double rotation = 0;
	protected double scale = 1;
	protected boolean flipped = false;
	protected boolean coordinatesDirty = true;
	protected boolean start = true;

	public void step()
	{
		if (start)
			computeNewPositions();
		setAreas();
		if (coordinatesDirty)
		{
			coordinatesDirty = false;
			computeNewPositions();
		}
		computeTransform();
	}

	protected abstract void setAreas();

	protected abstract void computeNewPositions();

	protected abstract void computeTransform();

	public void setOrigin(double originX, double originY)
	{
		this.originX = originX;
		this.originY = originY;
		coordinatesDirty = true;
	}

	public void setPosition(double x, double y)
	{
		this.x = x;
		this.y = y;
		coordinatesDirty = true;
	}

	public void setRotation(double degrees)
	{
		this.rotation = degrees;
		coordinatesDirty = true;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
		coordinatesDirty = true;
	}

	public void setFlipped(boolean flipped)
	{
		this.flipped = flipped;
		coordinatesDirty = true;
	}

	public double getRotation()
	{
		double returnVal = rotation;
		if (flipped) returnVal = 180-returnVal;
		if (scale<0) returnVal = 180+returnVal;
		return returnVal;
	}

	public void cutTrail()
	{
		start = true;
	}

	//Minimal displacement needed to prevent intersection
	public abstract ProjectionVector depth(Stadium end, double xVel, double yVel);

	//Instantaneous velocity at point closest to the given stadium
	public abstract ProjectionVector instantVelocity(Stadium start);

	//Ejection normal of the stadium
	public abstract ProjectionVector normal(Stadium start);

	public abstract Rectangle getStartBounds();

	public abstract Rectangle getTravelBounds();

	public abstract void draw(ShapeRenderer drawer);
}
