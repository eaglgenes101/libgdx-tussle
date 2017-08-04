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
import com.tussle.main.Utility;

/**
 * Created by eaglgenes101 on 4/18/17.
 */
public strictfp class StageEdge extends StageElement
{
	private double localx1 = 0, localy1 = 0, localx2 = 0, localy2 = 0;
	private double currentx1 = 0, currenty1 = 0, currentx2 = 0, currenty2 = 0;
	private double previousx1 = 0, previousy1 = 0, previousx2 = 0, previousy2 = 0;
	private double[] vanishPoint = {Double.NaN, Double.NaN};
	private double[] focusPoint = {Double.NaN, Double.NaN};

	public StageEdge()
	{
		localx1 = 0;
		localy1 = 0;
		localx2 = 0;
		localy2 = 0;
	}

	public StageEdge(double x1, double y1, double x2, double y2)
	{
		localx1 = x1;
		localy1 = y1;
		localx2 = x2;
		localy2 = y2;
	}

	public void computeNewPositions()
	{
		coordinatesDirty = false;
		final boolean doScale = flipped || scale != 1;
		final boolean doRotate = (rotation % 360 != 0);
		double cos = doRotate ? StrictMath.cos(StrictMath.toRadians(rotation)) : 1;
		double sin = doRotate ? StrictMath.sin(StrictMath.toRadians(rotation)) : 0;
		double sx = localx1 - originX;
		double sy = localy1 - originY;
		double ex = localx2 - originX;
		double ey = localy2 - originY;
		if (doScale)
		{
			sx *= flipped ? -scale : scale;
			sy *= scale;
			ex *= flipped ? -scale : scale;
			ey *= scale;
		}
		if (doRotate)
		{
			double oldSX = sx;
			double oldEX = ex;
			sx = sx * cos - sy * sin;
			sy = oldSX * sin + sy * cos;
			ex = ex * cos - ey * sin;
			ey = oldEX * sin + ey * cos;
		}
		currentx1 = sx + originX + x;
		currenty1 = sy + originY + y;
		currentx2 = ex + originX + x;
		currenty2 = ey + originY + y;
	}

	protected void setAreas()
	{
		previousx1 = currentx1;
		previousx2 = currentx2;
		previousy1 = currenty1;
		previousy2 = currenty2;
	}

	public void computeTransform()
	{
		vanishPoint = Utility.segmentsIntersectionPoint(getStartX(0), getStartY(0),
				getStartX(1), getStartY(1), getEndX(0), getEndY(0),
				getEndX(1), getEndY(1));
		focusPoint = Utility.segmentsIntersectionPoint(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartY(1),
				getEndX(1), getEndY(1));
	}

	public void setSegment(double x1, double y1, double x2, double y2)
	{
		this.localx1 = x1;
		this.localy1 = y1;
		this.localx2 = x2;
		this.localy2 = y2;
		coordinatesDirty = true;
	}

	public double getStartX(double time)
	{
		return (1-time)*previousx1 + time*currentx1;
	}

	public double getEndX(double time)
	{
		return (1-time)*previousx2 + time*currentx2;
	}

	public double getStartY(double time)
	{
		return (1-time)*previousy1 + time*currenty1;
	}

	public double getEndY(double time)
	{
		return (1-time)*previousy2 + time*currenty2;
	}

	public ProjectionVector depth(Stadium end, double xVel, double yVel)
	{
		double sumRad = end.getRadius();
		double time = 1;
		double time0 = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, currentx1, currenty1,
				xVel, yVel, 0, 0, sumRad);
		double time1 = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, currentx2, currenty2,
				xVel, yVel, 0, 0, sumRad);
		double time2 = Intersector.timeMovingSegmentCircle(currentx1, currenty1, currentx2, currenty2,
				end.getStartx() - xVel, end.getStarty() - yVel,0, 0, xVel, yVel, sumRad);
		double time3 = Intersector.timeMovingSegmentCircle(currentx1, currenty1, currentx2, currenty2,
				end.getEndx() - xVel, end.getEndy() - yVel, 0, 0, xVel, yVel, sumRad);
		if (Double.isFinite(time0) && time0 < time) time = time0;
		if (Double.isFinite(time1) && time1 < time) time = time1;
		if (Double.isFinite(time2) && time2 < time) time = time2;
		if (Double.isFinite(time3) && time3 < time) time = time3;
		if (time >= 1)
			return null;
		//Now we have the time, use this to determine facing
		double xDistRew = xVel * (1 - time);
		double yDistRew = yVel * (1 - time);
		double xStart = end.getStartx()-xDistRew;
		double yStart = end.getStarty()-yDistRew;
		double xEnd = end.getEndx()-xDistRew;
		double yEnd = end.getEndy()-yDistRew;
		if (Intersector.pointLineSide(currentx1, currenty1, currentx2, currenty2,
				end.getCenterX()-xDistRew,end.getCenterY()-yDistRew) <= 0)
			return null;
		if (Double.isNaN(Intersector.isPerpSegPoint(currentx1, currenty1, currentx2, currenty2,
				end.getCenterX()-xDistRew, end.getCenterY()-yDistRew)))
			return null;
		ProjectionVector v = Intersector.displacementSegments(xStart, yStart, xEnd, yEnd,
				currentx1, currenty1, currentx2, currenty2);
		v.magnitude += sumRad;
		double xDisp = v.xnorm*v.magnitude - xDistRew;
		double yDisp = v.ynorm*v.magnitude - yDistRew;
		double len = StrictMath.hypot(xDisp, yDisp);
		if (len == 0 || Double.isNaN(len))
			return null;
		else
			return new ProjectionVector(xDisp/len, yDisp/len, len);
	}

	public ProjectionVector instantVelocity(Stadium start)
	{
		//Find contact time
		double sumRad = start.getRadius();
		double dx = start.getEndx()-start.getStartx();
		double dy = start.getEndy()-start.getStarty();
		double len = StrictMath.hypot(dx, dy);
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double radx = start.getRadius()*dy/len;
		double rady = -start.getRadius()*dx/len;
		double time = Double.POSITIVE_INFINITY;
		//Start circle contact
		double time0 = Intersector.timeSegmentCircle(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], start.getStartx(), start.getStarty(),sumRad);
		//End circle contact
		double time1 = Intersector.timeSegmentCircle(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], start.getEndx(), start.getEndy(), sumRad);
		//Segment contact 1
		double time2 = Intersector.timeMovingSegments(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], sx+radx, sy+rady, ex+radx, ey+rady);
		//Segment contact 2
		double time3 = Intersector.timeMovingSegments(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], sx-radx, sy-rady, ex-radx, ey-rady);
		if (Double.isFinite(time0) && time0 < time) time = time0;
		if (Double.isFinite(time1) && time1 < time) time = time1;
		if (Double.isFinite(time2) && time2 < time) time = time2;
		if (Double.isFinite(time3) && time3 < time) time = time3;
		if (time >= 1)
			return null;
		if (Intersector.pointLineSide(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), start.getCenterX(), start.getCenterY()) <= 0)
			return null;
		if (Double.isNaN(Intersector.isPerpSegPoint(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), start.getCenterX(), start.getCenterY())))
			return null;
		//We got contact time, now find velocity of contact
		double part = Intersector.partSegments(
				getStartX(0) + time*(getStartX(1)-getStartX(0)),
				getStartY(0) + time*(getStartY(1)-getStartY(0)),
				getEndX(0) + time*(getEndX(1)-getEndX(0)),
				getEndY(0) + time*(getEndY(1)-getEndY(1)),
				sx, sy, ex, ey);
		double segPartCX = (1-part)*currentx1 + part*currentx2;
		double segPartCY = (1-part)*currenty1 + part*currenty2;
		double segPartPX = (1-part)*previousx1 + part*previousx2;
		double segPartPY = (1-part)*previousy1 + part*previousy2;
		double segDX = segPartCX-segPartPX;
		double segDY = segPartCY-segPartPY;
		double segSpd = StrictMath.hypot(segDX, segDY);
		if (segSpd == 0 || Double.isNaN(segSpd))
			return null;
		return new ProjectionVector(segDX/segSpd, segDY/segSpd, segSpd);
	}

	public ProjectionVector normal(Stadium start)
	{
		//Find contact point
		double sumRad = start.getRadius();
		double dx = start.getEndx()-start.getStartx();
		double dy = start.getEndy()-start.getStarty();
		double len = StrictMath.hypot(dx, dy);
		double sx = start.getStartx();
		double ex = start.getEndx();
		double sy = start.getStarty();
		double ey = start.getEndy();
		double radx = start.getRadius()*dy/len;
		double rady = -start.getRadius()*dx/len;
		double time = Double.POSITIVE_INFINITY;
		//Start circle contact
		double time0 = Intersector.timeSegmentCircle(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], start.getStartx(), start.getStarty(),sumRad);
		//End circle contact
		double time1 = Intersector.timeSegmentCircle(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], start.getEndx(), start.getEndy(), sumRad);
		//Segment contact 1
		double time2 = Intersector.timeMovingSegments(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], sx+radx, sy+rady, ex+radx, ey+rady);
		//Segment contact 2
		double time3 = Intersector.timeMovingSegments(getStartX(0), getStartY(0),
				getEndX(0), getEndY(0), getStartX(1), getStartX(1),
				getEndX(1), getEndY(1), focusPoint[0], focusPoint[1],
				vanishPoint[0], vanishPoint[1], sx-radx, sy-rady, ex-radx, ey-rady);
		if (Double.isFinite(time0) && time0 < time) time = time0;
		if (Double.isFinite(time1) && time1 < time) time = time1;
		if (Double.isFinite(time2) && time2 < time) time = time2;
		if (Double.isFinite(time3) && time3 < time) time = time3;
		if (time >= 1)
			return null;
		if (Intersector.pointLineSide(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), start.getCenterX(), start.getCenterY()) <= 0)
			return null;
		if (Double.isNaN(Intersector.isPerpSegPoint(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), start.getCenterX(), start.getCenterY())))
			return null;
		//We got contact time, now find normal of contact
		ProjectionVector v = Intersector.displacementSegments(start.getStartx(), start.getStarty(),
				start.getEndx(), start.getEndy(), getStartX(time), getStartY(time),
				getEndX(time), getEndY(time));
		v.magnitude += sumRad;
		return v;
	}

	public Rectangle getStartBounds()
	{
		double xMin = StrictMath.min(getStartX(0), getEndX(0));
		double xMax = StrictMath.max(getStartX(0), getEndX(0));
		double yMin = StrictMath.min(getStartY(0), getEndY(0));
		double yMax = StrictMath.max(getStartY(0), getEndY(0));
		return new Rectangle(xMin, yMin, xMax-xMin, yMax-yMin);
	}

	public Rectangle getTravelBounds()
	{
		double xMin = getStartX(0);
		double xMax = getStartX(0);
		double yMin = getStartY(0);
		double yMax = getStartY(0);
		if (getStartX(1) < xMin) xMin = getStartX(1);
		else if (getStartX(1) > xMax) xMax = getStartX(1);
		if (getEndX(0) < xMin) xMin = getEndX(0);
		else if (getEndX(0) > xMax) xMax = getEndX(0);
		if (getEndX(1) < xMin) xMin = getEndX(1);
		else if (getEndX(1) > xMax) xMax = getEndX(1);
		if (getStartY(1) < yMin) yMin = getStartY(1);
		else if (getStartY(1) > yMax) yMax = getStartY(1);
		if (getEndY(0) < yMin) yMin = getEndY(0);
		else if (getEndY(0) > yMax) yMax = getEndY(0);
		if (getEndY(1) < yMin) yMin = getEndY(1);
		else if (getEndY(1) > yMax) yMax = getEndY(1);
		return new Rectangle(xMin, yMin, xMax-xMin, yMax-yMin);
	}
}
