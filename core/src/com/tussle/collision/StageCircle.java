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

import com.tussle.main.Intersector;

public class StageCircle extends StageElement
{
	private double localx = 0, localy = 0, localr = 1;
	private double currentx, currenty, currentr;
	private double previousx, previousy, previousr;

	public StageCircle(double x, double y, double r)
	{
		localx = x;
		localy = y;
		localr = r;
	}

	public void computeNewPositions()
	{
		coordinatesDirty = false;
		double cos = StrictMath.cos(StrictMath.toRadians(rotation));
		double sin = StrictMath.sin(StrictMath.toRadians(rotation));
		double locx = localx - originX;
		double locy = localy - originY;
		locx *= flipped ? -scale : scale;
		locy *= scale;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		currentx = locx + originX + x;
		currenty = locy + originY + y;
		currentr = StrictMath.abs(localr*scale);
	}

	protected void setAreas()
	{
		previousx = currentx;
		previousy = currenty;
		previousr = currentr;
	}

	public void computeTransform()
	{
		//Nothing...
	}

	public void setPoint(double x, double y)
	{
		localx = x;
		localy = y;
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

	public double getRadius(double time) { return (1-time)*previousr + time*currentr;}

	public ProjectionVector depth(Stadium end, double xVel, double yVel)
	{
		double sumRad = end.getRadius()+this.getRadius(1);
		double time = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, currentx, currenty,
				xVel, yVel, 0, 0, sumRad);
		if (Double.isInfinite(time))
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
		double sumRad = start.getRadius()+this.getRadius(1);
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double time = Intersector.timeMovingSegmentCircle(sx, sy, ex, ey, previousx, previousy,
				0, 0,currentx-previousx, currenty-previousy, sumRad);
		if (Double.isInfinite(time))
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
		double sumRad = start.getRadius()+this.getRadius(1);
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double time = Intersector.timeMovingSegmentCircle(sx, sy, ex, ey, previousx, previousy,
				0, 0,currentx-previousx, currenty-previousy, sumRad);
		if (Double.isInfinite(time))
			return null;
		//We got contact time, now find velocity of contact
		double atTimeX = (1-time)*previousx + time*currentx;
		double atTimeY = (1-time)*previousy + time*currenty;
		ProjectionVector v = Intersector.dispSegmentPoint(sx, sy, ex, ey, atTimeX, atTimeY);
		v.magnitude += sumRad;
		return v;
	}

	public Rectangle getStartBounds()
	{
		double radius = getRadius(0);
		return new Rectangle(getX(0)-radius, getY(0)-radius,
				2*radius, 2*radius);
	}

	public Rectangle getTravelBounds()
	{
		double radius = getRadius(1);
		double xMin = StrictMath.min(getX(0), getX(1));
		double xMax = StrictMath.max(getX(0), getX(1));
		double yMin = StrictMath.min(getY(0), getY(1));
		double yMax = StrictMath.max(getY(0), getY(1));
		return new Rectangle(xMin-radius, yMin-radius,
				xMax-xMin+2*radius, yMax-yMin+2*radius);
	}
}
