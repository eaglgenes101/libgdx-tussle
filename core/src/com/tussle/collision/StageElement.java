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
	protected double befX = 0, befY = 0, aftX = 0, aftY = 0;
	protected double befOriginX = 0, befOriginY = 0, aftOriginX = 0, aftOriginY = 0;
	protected double befRot = 0, aftRot = 0;
	protected double befScale = 1, aftScale = 1;
	protected boolean befFlip = false, aftFlip = false;
	protected boolean befDirty = true, aftDirty = true;
	
	public StageElement()
	{
		//Nothing, mostly exists so we don't need to do hoop jumping
	}
	
	//Copy constructors!
	public StageElement(StageElement other)
	{
		this.befX = other.befX;this.befY = other.befY;
		this.aftX = other.aftX;this.aftY = other.aftY;
		this.befOriginX = other.befOriginX;this.befOriginY = other.befOriginY;
		this.aftOriginX = other.aftOriginX;this.aftOriginY = other.aftOriginY;
		this.befRot = other.befRot;this.aftRot = other.aftRot;
		this.befFlip = other.befFlip;this.aftFlip = other.aftFlip;
	}

	protected abstract void computeNewBeforePositions();
	
	protected abstract void computeNewAfterPositions();
	
	protected void cleanForTime(double time)
	{
		if (time != 0) computeNewAfterPositions();
		if (time != 1) computeNewBeforePositions();
	}
	
	public void setBeforeOrigin(double originX, double originY)
	{
		befOriginX = originX;
		befOriginY = originY;
		befDirty = true;
	}
	
	public void setAfterOrigin(double originX, double originY)
	{
		aftOriginX = originX;
		aftOriginY = originY;
		aftDirty = true;
	}

	public void setBeforePos(double x, double y)
	{
		this.befX = x;
		this.befY = y;
		befDirty = true;
	}
	
	public void setAfterPos(double x, double y)
	{
		this.aftX = x;
		this.aftY = y;
		aftDirty = true;
	}
	
	public void setBeforeRot(double degrees)
	{
		this.befRot = degrees;
		befDirty = true;
	}
	
	public void setAfterRot(double degrees)
	{
		this.aftRot = degrees;
		aftDirty = true;
	}
	
	public void setBeforeScale(double scale)
	{
		this.befScale = scale;
		befDirty = true;
	}
	
	public void setAfterScale(double scale)
	{
		this.aftScale = scale;
		aftDirty = true;
	}
	
	public void setBeforeFlipped(boolean flipped)
	{
		this.befFlip = flipped;
		befDirty = true;
	}
	
	public void setAfterFlipped(boolean flipped)
	{
		this.aftFlip = flipped;
		aftDirty = true;
	}

	//Minimal displacement needed to prevent intersection given the time and stadium
	public abstract ProjectionVector depth(CollisionStadium end, double time);

	//Instantaneous velocity at point closest to the given stadium
	public abstract double[] instantVelocity(CollisionStadium start, double time);

	//Portion of the stadium that the stage element is closest to
	public abstract double stadiumPortion(CollisionStadium start, double time);

	//Boolean indicating if the ECB interacts with the stage surface at this time
	public abstract boolean collides(CollisionStadium end, double time);

	public abstract Rectangle getBounds(double start, double end);

	public abstract void draw(ShapeRenderer drawer);
}
