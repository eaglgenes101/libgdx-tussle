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

import com.badlogic.ashley.core.Entity;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by eaglgenes101 on 2/21/17.
 */
public class Ledge
{
	protected double x, y;
	protected double originX, originY;
	protected double rotation;
	protected double scale = 1;
	protected boolean flipped = false;
	protected boolean coordinatesDirty = true;
	double localX, localY, localRadius, localDirection;
	double currentX, currentY, currentRadius, currentDirection;
	Entity currentEntity;

	public Ledge(double x, double y, double radius, double direction)
	{
		localX = x;
		localY = y;
		localRadius = radius;
		localDirection = direction;
		currentEntity = null;
	}

	public void step()
	{
		if (coordinatesDirty)
		{
			coordinatesDirty = false;
			computeNewPositions();
		}
		setAreas();
	}

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

	public void computeNewPositions()
	{
		coordinatesDirty = false;
		final boolean doScale = flipped || scale != 1;
		final boolean doRotate = (rotation % 360 != 0);
		double cos = doRotate ? FastMath.cos(FastMath.toDegrees(rotation)) : 1;
		double sin = doRotate ? FastMath.sin(FastMath.toDegrees(rotation)) : 0;
		double posx = localX - originX;
		double posy = localY - originY;
		double rad = localRadius;
		double direction = localDirection;
		if (doScale)
		{
			posx *= flipped ? -scale : scale;
			posy *= scale;
			rad *= scale;
			if (flipped) direction = 180-direction;
		}
		if (doRotate)
		{
			double oldX = posx;
			posx = posx * cos - posy * sin;
			posy = oldX * sin + posy * cos;
			direction += rotation;
		}
		currentX = posx + originX + x;
		currentY = posy + originY + y;
		currentRadius = rad;
		currentDirection = direction;
	}

	protected void setAreas()
	{
		//Nothing...
	}

	public void setPoint(double x, double y)
	{
		localX = x;
		localY = y;
	}

	public void setRadius(double radius)
	{
		localRadius = radius;
	}

	public void setDirection(double direction)
	{
		localDirection = direction;
	}

	public boolean isOccupied()
	{
		return currentEntity != null;
	}

	public boolean checkCling(Entity fighter)
	{
		if (isOccupied()) //Edgehogging
			return false;
		else
		{
			currentEntity = fighter;
			return true;
		}
	}

	public boolean canCling(double x, double y)
	{
		double dx = x-currentX;
		double dy = y-currentY;
		double sin = FastMath.sin(FastMath.toRadians(currentDirection));
		double cos = FastMath.cos(FastMath.toRadians(currentDirection));
		return dx*cos + dy*sin > 0 && dx*dx+dy*dy > currentRadius*currentRadius;
	}

	public void release()
	{
		currentEntity = null;
	}
}
