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
	
	public StageElement()
	{
		//Nothing, mostly exists so we don't need to do hoop jumping
	}
	
	//Copy constructors!
	public StageElement(StageElement other)
	{
		this.x = other.x;
		this.y = other.y;
		this.originX = other.originX;
		this.originY = other.originY;
		this.rotation = other.rotation;
		this.scale = other.scale;
		this.flipped = other.flipped;
		this.coordinatesDirty = other.coordinatesDirty;
		this.start = other.start;
	}

	public abstract void setAreas();

	protected abstract void computeNewPositions();

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
		coordinatesDirty = true;
	}

	//Minimal displacement needed to prevent intersection given the time and stadium
	public abstract ProjectionVector depth(Stadium end, double time);

	//Instantaneous velocity at point closest to the given stadium
	public abstract double[] instantVelocity(Stadium start, double time);

	//Portion of the stadium that the stage element is closest to
	public abstract double stadiumPortion(Stadium start, double time);

	//Boolean indicating if the ECB interacts with the stage surface at this time
	public abstract boolean collides(Stadium end, double time);

	public abstract Rectangle getBounds(double start, double end);

	public abstract void draw(ShapeRenderer drawer);
}
