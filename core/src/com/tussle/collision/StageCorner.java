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
import com.tussle.main.Intersector;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by eaglgenes101 on 5/17/17.
 */
public strictfp class StageCorner extends StageElement
{
	public static final double HALF_WHOLE = 180;
	private double localx, localy, localMinAngle, localMaxAngle;
	private double currentx, currenty;
	private double currentMinAngle, currentMaxAngle;
	private double currentRightCos, currentRightSin, currentLeftCos, currentLeftSin;
	private double previousx, previousy;
	private double previousRightCos, previousRightSin, previousLeftCos, previousLeftSin;
	private double previousMinAngle, previousMaxAngle;

	public StageCorner(double x, double y, double minAngle, double maxAngle)
	{
		localx = x;
		localy = y;
		localMinAngle = minAngle;
		localMaxAngle = maxAngle;
	}

	public void computeNewPositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(rotation));
		double sin = FastMath.sin(FastMath.toRadians(rotation));
		double locx = localx - originX;
		double locy = localy - originY;
		double minAngle = localMinAngle;
		double maxAngle = localMaxAngle;
		if (flipped)
		{
			minAngle = HALF_WHOLE - minAngle;
			maxAngle = HALF_WHOLE - maxAngle;
		}
		locx *= flipped ? -scale : scale;
		locy *= scale;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		minAngle += rotation;
		maxAngle += rotation;
		currentx = locx + originX + x;
		currenty = locy + originY + y;
		currentMinAngle = minAngle;
		currentMaxAngle = maxAngle;
		currentRightCos = FastMath.cos(FastMath.toRadians(minAngle));
		currentRightSin = FastMath.sin(FastMath.toRadians(minAngle));
		currentLeftCos = FastMath.cos(FastMath.toRadians(maxAngle));
		currentLeftSin = FastMath.sin(FastMath.toRadians(maxAngle));
		coordinatesDirty = false;
		if (start)
		{
			start = false;
			setAreas();
		}
	}

	public void setAreas()
	{
		if (coordinatesDirty)
			computeNewPositions();
		previousx = currentx;
		previousy = currenty;
		previousRightCos = currentRightCos;
		previousRightSin = currentRightSin;
		previousLeftCos = currentLeftCos;
		previousLeftSin = currentLeftSin;
		previousMinAngle = currentMinAngle;
		previousMaxAngle = currentMaxAngle;
	}

	public void setPoint(double x, double y)
	{
		localx = x;
		localy = y;
		coordinatesDirty = true;
	}

	public void setAngles(double min, double max)
	{
		localMinAngle = min;
		localMaxAngle = max;
		coordinatesDirty = true;
	}

	public double getX(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousx + time*currentx;
	}

	public double getY(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousy + time*currenty;
	}

	public ProjectionVector getRightNormal(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		//Not as simple as interpolating angle unfortunately
		double interpCos = (1-time)*previousRightCos + time*currentRightCos;
		double interpSin = (1-time)*previousRightSin + time*currentRightSin;
		if (interpCos == 0 && interpSin == 0)
		{
			if (previousRightCos*currentRightCos + previousRightSin*currentRightSin > 0)
			{
				return new ProjectionVector(currentRightCos, currentRightSin, 1);
			}
			else
			{
				return new ProjectionVector(currentRightSin, -currentRightCos, 1);
			}
		}
		double magn = FastMath.hypot(interpSin, interpCos);
		return new ProjectionVector(interpCos/magn, interpSin/magn, 1);
	}

	public ProjectionVector getLeftNormal(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		//Not as simple as interpolating angle unfortunately
		double interpCos = (1-time)*previousLeftCos + time*currentLeftCos;
		double interpSin = (1-time)*previousLeftSin + time*currentLeftSin;
		if (interpCos == 0 && interpSin == 0)
		{
			if (previousLeftCos*currentLeftCos + previousLeftSin*currentLeftSin > 0)
			{
				return new ProjectionVector(currentLeftCos, currentLeftSin, 1);
			}
			else
			{
				return new ProjectionVector(currentLeftSin, -currentLeftCos, 1);
			}
		}
		double magn = FastMath.hypot(interpSin, interpCos);
		return new ProjectionVector(interpCos/magn, interpSin/magn, 1);
	}

	public ProjectionVector depth(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double xPos = getX(time);
		double yPos = getY(time);
		ProjectionVector disp = Intersector.dispSegmentPoint(stad.getStartx(),
				stad.getStarty(), stad.getEndx(), stad.getEndy(), xPos, yPos);
		//disp.xnorm = -disp.xnorm;
		//disp.ynorm = -disp.ynorm;
		//disp.magnitude = stad.getRadius()-disp.magnitude;
		disp.magnitude -= stad.getRadius();
		return disp;
	}

	public double[] instantVelocity(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double secDX = getX(1)-getX(0);
		double secDY = getY(1)-getY(0);
		return new double[]{secDX, secDY};
	}

	public boolean collides(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double xPos = getX(time);
		double yPos = getY(time);
		ProjectionVector disp = Intersector.dispSegmentPoint(stad.getStartx(),
				stad.getStarty(), stad.getEndx(), stad.getEndy(), xPos, yPos);
		ProjectionVector rightNormal = getRightNormal(time);
		ProjectionVector leftNormal = getLeftNormal(time);
		//if (stad.getRadius()-disp.magnitude < 0) return false;
		//if (stad.getRadius()-disp.magnitude > 16) return false;
		if (disp.magnitude - stad.getRadius() < 0) return false;
		if (disp.magnitude - stad.getRadius() > 16) return false;
		//Thanks stack overflow!
		return (rightNormal.ynorm*disp.xnorm-rightNormal.xnorm*disp.ynorm) *
			   (rightNormal.ynorm*leftNormal.xnorm-rightNormal.xnorm*leftNormal.ynorm) < 0;

	}

	public double stadiumPortion(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return Intersector.partSegmentPoint(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), getX(time), getY(time));
	}

	public Rectangle getBounds(double start, double end)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double minX = FastMath.min(getX(start), getX(end));
		double maxX = FastMath.max(getX(start), getX(end));
		double minY = FastMath.min(getY(start), getY(end));
		double maxY = FastMath.max(getY(start), getY(end));
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void draw(ShapeRenderer drawer)
	{
		ProjectionVector startRight = getRightNormal(0);
		ProjectionVector startLeft = getLeftNormal(0);
		ProjectionVector endRight = getRightNormal(1);
		ProjectionVector endLeft = getLeftNormal(1);

		if (coordinatesDirty)
			computeNewPositions();
		drawer.arc((float)getX(0), (float)getY(0), 12,
				(float)previousMinAngle, (float)(previousMaxAngle-previousMinAngle));
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)+startRight.xnorm*20), (float)(getY(0)+startRight.ynorm*20));
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)+startLeft.xnorm*20), (float)(getY(0)+startLeft.ynorm*20));

		drawer.arc((float)getX(1), (float)getY(1), 16,
				(float)currentMinAngle, (float)(currentMaxAngle-currentMinAngle));
		drawer.line((float)getX(1), (float)getY(1),
				(float)(getX(1)+endRight.xnorm*20), (float)(getY(1)+endRight.ynorm*20));
		drawer.line((float)getX(1), (float)getY(1),
				(float)(getX(1)+endLeft.xnorm*20), (float)(getY(1)+endLeft.ynorm*20));
	}
}
