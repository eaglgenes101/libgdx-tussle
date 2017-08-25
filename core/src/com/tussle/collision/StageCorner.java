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

/**
 * Created by eaglgenes101 on 5/17/17.
 */
public class StageCorner extends StageElement
{
	public static final double HALF_WHOLE = 180;
	private double localx, localy, localMinAngle, localMaxAngle;
	private double currentx, currenty;
	private double currentRightCos, currentRightSin, currentLeftCos, currentLeftSin;
	private double previousx, previousy;
	private double previousRightCos, previousRightSin, previousLeftCos, previousLeftSin;

	public StageCorner(double x, double y, double minAngle, double maxAngle)
	{
		localx = x;
		localy = y;
		localMinAngle = minAngle;
		localMaxAngle = maxAngle;
	}

	public void computeNewPositions()
	{
		coordinatesDirty = false;
		double cos = StrictMath.cos(StrictMath.toRadians(rotation));
		double sin = StrictMath.sin(StrictMath.toRadians(rotation));
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
		currentRightCos = StrictMath.cos(StrictMath.toRadians(minAngle));
		currentRightSin = StrictMath.sin(StrictMath.toRadians(minAngle));
		currentLeftCos = StrictMath.cos(StrictMath.toRadians(maxAngle));
		currentLeftSin = StrictMath.sin(StrictMath.toRadians(maxAngle));
	}

	protected void setAreas()
	{
		previousx = currentx;
		previousy = currenty;
		previousRightCos = currentRightCos;
		previousRightSin = currentRightSin;
		previousLeftCos = currentLeftCos;
		previousLeftSin = currentLeftSin;
	}

	public void computeTransform()
	{
		//double rFocusX = (previousx-currentx)/(-currentRightSin+previousRightSin);
		//double rFocusY = (previousy-currenty)/(currentRightCos-previousRightCos);
		//double lFocusX = (previousx-currentx)/(-currentLeftSin+previousLeftSin);
		//double lFocusY = (previousy-currenty)/(currentLeftCos-previousLeftCos);
		//No need for homographs so we don't calculate them
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
		return (1-time)*previousx + time*currentx;
	}

	public double getY(double time)
	{
		return (1-time)*previousy + time*currenty;
	}

	//Can the stage corner push out at the specified angle at the specified time?
	public boolean doesCollide(double time, double cos, double sin)
	{
		double atTimeX = getX(time);
		double atTimeY = getY(time);
		double rightFocusX = (previousx-currentx)/(-currentRightSin+previousRightSin);
		double rightFocusY = (previousy-currenty)/(currentRightCos-previousRightCos);
		double leftFocusX = (previousx-currentx)/(-currentLeftSin+previousLeftSin);
		double leftFocusY = (previousy-currenty)/(currentLeftCos-previousLeftCos);
		double side = Intersector.pointLineSide(leftFocusX, leftFocusY,
				rightFocusX, rightFocusY, atTimeX, atTimeY);
		if (side < 0)
			return false;
		else if (side == 0)
		{
			return (rightFocusX-leftFocusX)*cos + (rightFocusY-leftFocusY)*sin == 0 &&
					(rightFocusX-leftFocusX)*sin - (rightFocusY-leftFocusY)*cos > 0;
			//TODO: Add a small amount of tolerance
		}
		else
		{
			double leftDX = atTimeX-leftFocusX;
			double leftDY = atTimeY-leftFocusY;
			double rightDX = rightFocusX-atTimeX;
			double rightDY = rightFocusY-atTimeY;
			return leftDX*cos + leftDY*sin > 0 &&
					rightDX*cos + rightDY*sin > 0 &&
					leftDX*sin - leftDY*cos < 0 &&
					rightDX*sin - rightDY*cos > 0;
		}
	}

	public ProjectionVector depth(Stadium end, double xVel, double yVel)
	{
		double sumRad = end.getRadius();
		double time = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, currentx, currenty,
				xVel, yVel, 0, 0, sumRad);
		if (!Double.isFinite(time))
			return null;
		//Now we have the time, use this to determine facing
		double xDistRew = xVel * (1 - time);
		double yDistRew = yVel * (1 - time);
		double xStart = end.getStartx()-xDistRew;
		double yStart = end.getStarty()-yDistRew;
		double xEnd = end.getEndx()-xDistRew;
		double yEnd = end.getEndy()-yDistRew;
		//Get angle of pt
		ProjectionVector v = Intersector.dispSegmentPoint(xStart, yStart, xEnd, yEnd, currentx, currenty);
		if (!doesCollide(time, v.xnorm, v.ynorm))
			return null;
		v.magnitude += sumRad;
		double xDisp = v.xnorm * v.magnitude - xDistRew;
		double yDisp = v.ynorm * v.magnitude - yDistRew;
		double len = StrictMath.hypot(xDisp, yDisp);
		if (len == 0 || Double.isNaN(len))
			return null;
		return new ProjectionVector(xDisp / len, yDisp / len, len);
	}

	public ProjectionVector instantVelocity(Stadium start)
	{
		//Find contact time
		double sumRad = start.getRadius();
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double time = Intersector.timeMovingSegmentCircle(sx, sy, ex, ey, previousx, previousy,
				0, 0,currentx-previousx, currenty-previousy, sumRad);
		if (!Double.isFinite(time))
			return null;
		//We got contact time, now find velocity of contact
		double atTimeX = (1-time)*previousx + time*currentx;
		double atTimeY = (1-time)*previousy + time*currenty;
		ProjectionVector v = Intersector.dispSegmentPoint(sx, sy, ex, ey, atTimeX, atTimeY);
		if (!doesCollide(time, v.xnorm, v.ynorm))
			return null;
		double segDX = currentx-previousx;
		double segDY = currenty-previousy;
		double segSpd = StrictMath.hypot(segDX, segDY);
		if (segSpd == 0 || Double.isNaN(segSpd))
			return null;
		return new ProjectionVector(segDX/segSpd, segDY/segSpd, segSpd);
	}

	public ProjectionVector normal(Stadium start)
	{
		//Find contact time
		double sumRad = start.getRadius();
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double time = Intersector.timeMovingSegmentCircle(sx, sy, ex, ey, previousx, previousy,
				0, 0,currentx-previousx, currenty-previousy, sumRad);
		if (!Double.isFinite(time))
			return null;
		//We got contact time, now find velocity of contact
		double atTimeX = (1-time)*previousx + time*currentx;
		double atTimeY = (1-time)*previousy + time*currenty;
		ProjectionVector v = Intersector.dispSegmentPoint(sx, sy, ex, ey, atTimeX, atTimeY);
		if (!doesCollide(time, v.xnorm, v.ynorm))
			return null;
		v.magnitude += sumRad;
		return v;
	}

	public Rectangle getStartBounds()
	{
		return new Rectangle(currentx, currenty, 0, 0);
	}

	public Rectangle getTravelBounds()
	{
		double xMin = StrictMath.min(currentx, previousx);
		double xMax = StrictMath.max(currentx, previousx);
		double yMin = StrictMath.min(currenty, previousy);
		double yMax = StrictMath.max(currenty, previousy);
		return new Rectangle(xMin, yMin, xMax-xMin, yMax-yMin);
	}

	public void draw(ShapeRenderer drawer)
	{
		drawer.circle((float)getX(0), (float)getY(0), 2);
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)-currentLeftCos*4), (float)(getY(0)-currentLeftSin*4));
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)-currentRightCos*4), (float)(getY(0)-currentRightSin*4));
	}
}
