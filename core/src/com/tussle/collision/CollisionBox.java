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
import com.tussle.main.Utility;

/**
 * Created by eaglgenes101 on 4/15/17.
 */
public strictfp class CollisionBox extends StageElement
{
	private Stadium localArea;
	private Stadium currentArea;
	private Stadium previousArea;
	private double[] vanishPoint = {Double.NaN, Double.NaN};
	private double[] focusPoint = {Double.NaN, Double.NaN};

	public CollisionBox()
	{
		localArea = new Stadium();
		currentArea = new Stadium();
		previousArea = new Stadium();
	}

	public CollisionBox(Stadium base)
	{
		localArea = base;
		currentArea = new Stadium();
		previousArea = new Stadium();
	}

	public Stadium getCurrentStadium()
	{
		return currentArea;
	}

	public Stadium getPreviousStadium()
	{
		return previousArea;
	}

	protected void setAreas()
	{
		previousArea.set(currentArea);
	}

	public void computeNewPositions()
	{
		double sx = localArea.startx - originX;
		double sy = localArea.starty - originY;
		double ex = localArea.endx - originX;
		double ey = localArea.endy - originY;
		double rad = StrictMath.abs(localArea.radius*scale);
		sx *= flipped?-scale:scale;
		sy *= scale;
		ex *= flipped?-scale:scale;
		ey *= scale;
		double cos = StrictMath.cos(StrictMath.toRadians(rotation));
		double sin = StrictMath.sin(StrictMath.toRadians(rotation));
		double oldSX = sx;
		double oldEX = ex;
		sx = sx*cos - sy*sin;
		sy = oldSX*sin + sy*cos;
		ex = ex*cos - ey*sin;
		ey = oldEX*sin + ey*cos;
		currentArea.setStart(sx+originX+x, sy+originY+y)
				.setEnd(ex+originX+x, ey+originY+y).setRadius(rad);
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

	public void setStadium(Stadium newStadium)
	{
		currentArea.set(newStadium);
		coordinatesDirty = true;
	}

	public double getStartX(double time)
	{
		return time*getPreviousStadium().getStartx() +
				(1-time)*getCurrentStadium().getStartx();
	}

	public double getEndX(double time)
	{
		return time*getPreviousStadium().getEndx() +
				(1-time)*getCurrentStadium().getEndx();
	}

	public double getStartY(double time)
	{
		return time*getPreviousStadium().getStarty() +
				(1-time)*getCurrentStadium().getStarty();
	}

	public double getEndY(double time)
	{
		return time*getPreviousStadium().getEndy() +
				(1-time)*getCurrentStadium().getEndy();
	}

	public double getRadius(double time)
	{
		return time*getPreviousStadium().getRadius() +
				(1-time)*getCurrentStadium().getRadius();
	}

	public double[] getFocus()
	{
		return focusPoint;
	}

	public double[] getVanish()
	{
		return vanishPoint;
	}

	public ProjectionVector depth(Stadium end, double xVel, double yVel)
	{
		double sumRad = end.getRadius() + this.getRadius(1);
		double time = 1;
		double time0 = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, getStartX(1), getStartY(1),
				xVel, yVel, 0, 0, sumRad);
		double time1 = Intersector.timeMovingSegmentCircle(end.getStartx() - xVel, end.getStarty() - yVel,
				end.getEndx() - xVel, end.getEndy() - yVel, getEndX(1), getEndY(1),
				xVel, yVel, 0, 0, sumRad);
		double time2 = Intersector.timeMovingSegmentCircle(getStartX(1), getStartY(1),
				getEndX(1), getEndY(1), end.getStartx() - xVel, end.getStarty() - yVel,
					0, 0, xVel, yVel, sumRad);
		double time3 = Intersector.timeMovingSegmentCircle(getStartX(1), getStartY(1),
				getEndX(1), getEndY(1), end.getEndx() - xVel, end.getEndy() - yVel,
				0, 0, xVel, yVel, sumRad);
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
		ProjectionVector v = Intersector.displacementSegments(xStart, yStart, xEnd, yEnd,
				getStartX(1), getStartY(1), getEndX(1), getEndY(1));
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
		double sumRad = start.getRadius() + this.getRadius(1);
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
		//We got contact time, now find velocity of contact
		double part = Intersector.partSegments(
				getStartX(0) + time*(getStartX(1)-getStartX(0)),
				getStartY(0) + time*(getStartY(1)-getStartY(0)),
				getEndX(0) + time*(getEndX(1)-getEndX(0)),
				getEndY(0) + time*(getEndY(1)-getEndY(1)),
				sx, sy, ex, ey);
		double segPartCX = (1-part)*getStartX(1) + part*getEndX(1);
		double segPartCY = (1-part)*getStartY(1) + part*getEndY(1);
		double segPartPX = (1-part)*getStartX(0) + part*getEndX(0);
		double segPartPY = (1-part)*getStartY(0) + part*getEndY(0);
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
		double sumRad = start.getRadius() + this.getRadius(1);
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
		//We got contact time, now find normal of contact
		ProjectionVector v = Intersector.displacementSegments(start.getStartx(), start.getStarty(),
				start.getEndx(), start.getEndy(), getStartX(time), getStartY(time),
				getEndX(time), getEndY(time));		v.magnitude += sumRad;
		return v;
	}

	public Rectangle getStartBounds()
	{
		double radius = getRadius(0);
		double xMin = StrictMath.min(getStartX(0), getEndX(0));
		double xMax = StrictMath.max(getStartX(0), getEndX(0));
		double yMin = StrictMath.min(getStartY(0), getEndY(0));
		double yMax = StrictMath.max(getStartY(0), getEndY(0));
		return new Rectangle(xMin-radius, yMin-radius,
				xMax-xMin+2*radius, yMax-yMin+2*radius);
	}

	public Rectangle getTravelBounds()
	{
		double radius = getRadius(1);
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
		return new Rectangle(xMin-radius, yMin-radius,
				xMax-xMin+2*radius, yMax-yMin+2*radius);
	}

	public void draw(ShapeRenderer drawer)
	{
		drawer.circle((float)getStartX(0), (float)getStartY(0), (float)getRadius(0));
		drawer.circle((float)getEndX(0), (float)getEndX(0), (float)getRadius(0));
		double len = StrictMath.hypot(getEndX(0)-getStartX(0),
				getEndY(0)-getStartY(0));
		double dx = (getStartY(0)-getEndY(0))*getRadius(0)/len;
		double dy = (getEndX(0)-getStartX(0))*getRadius(0)/len;
		drawer.line((float)(getStartX(0)+dx), (float)(getStartY(0)+dy),
				(float)(getEndX(0)+dx), (float)(getEndY(0)+dy));
		drawer.line((float)(getStartX(0)-dx), (float)(getStartY(0)-dy),
				(float)(getEndX(0)-dx), (float)(getEndY(0)-dy));
	}
}
