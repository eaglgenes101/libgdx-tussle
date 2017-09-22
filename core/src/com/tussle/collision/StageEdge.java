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
		double cos = StrictMath.cos(StrictMath.toRadians(rotation));
		double sin = StrictMath.sin(StrictMath.toRadians(rotation));
		double sx = localx1 - originX;
		double sy = localy1 - originY;
		double ex = localx2 - originX;
		double ey = localy2 - originY;
		sx *= flipped ? -scale : scale;
		sy *= scale;
		ex *= flipped ? -scale : scale;
		ey *= scale;
		double oldSX = sx;
		double oldEX = ex;
		sx = sx * cos - sy * sin;
		sy = oldSX * sin + sy * cos;
		ex = ex * cos - ey * sin;
		ey = oldEX * sin + ey * cos;
		currentx1 = sx + originX + x;
		currenty1 = sy + originY + y;
		currentx2 = ex + originX + x;
		currenty2 = ey + originY + y;
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
		previousx1 = currentx1;
		previousx2 = currentx2;
		previousy1 = currenty1;
		previousy2 = currenty2;
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
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousx1 + time*currentx1;
	}

	public double getEndX(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousx2 + time*currentx2;
	}

	public double getStartY(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousy1 + time*currenty1;
	}

	public double getEndY(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousy2 + time*currenty2;
	}

	public ProjectionVector depth(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		ProjectionVector disp = Intersector.displacementSegments(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), startX, startY, endX, endY);
		disp.magnitude = stad.getRadius() - disp.magnitude;
		return disp;
	}

	public ProjectionVector instantVelocity(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		double startDX = getStartX(1)-getStartX(0);
		double startDY = getStartY(1)-getStartY(1);
		double endDX = getEndX(1)-getEndX(0);
		double endDY = getEndY(1)-getEndY(0);
		double section = Intersector.partSegments(startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		double secDX = (1-section)*startDX + section*endDX;
		double secDY = (1-section)*startDY + section*endDY;
		if (secDX == 0 && secDY == 0)
			return new ProjectionVector(0, 0, 0);
		else
		{
			double len = StrictMath.hypot(secDX, secDY);
			return new ProjectionVector(secDX/len, secDY/len, len);
		}
	}

	public boolean collides(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		ProjectionVector disp = Intersector.displacementSegments(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), startX, startY, endX, endY);
		//Tolerance of 16 pixels
		double section = Intersector.partSegments(startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		if (stad.getRadius()-disp.magnitude < 0) return false;
		if (stad.getRadius()-disp.magnitude > 16) return false;
		if (disp.xnorm*(startY-endY) + disp.ynorm*(endX-startX) <= 0) return false;
		return section > 0 && section < 1;

	}

	public double stadiumPortion(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		return Intersector.partSegments(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), startX, startY, endX, endY);
	}

	public Rectangle getBounds(double start, double end)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startXMin = StrictMath.min(getStartX(start), getStartX(end));
		double startYMin = StrictMath.min(getStartY(start), getStartY(end));
		double startXMax = StrictMath.max(getStartX(start), getStartX(end));
		double startYMax = StrictMath.max(getStartY(start), getStartY(end));
		double endXMin = StrictMath.min(getEndX(start), getEndX(end));
		double endYMin = StrictMath.min(getEndY(start), getEndY(end));
		double endXMax = StrictMath.max(getEndX(start), getEndX(end));
		double endYMax = StrictMath.max(getEndY(start), getEndY(end));
		double minX = StrictMath.min(startXMin, endXMin);
		double maxX = StrictMath.max(startXMax, endXMax);
		double minY = StrictMath.min(startYMin, endYMin);
		double maxY = StrictMath.max(startYMax, endYMax);
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void draw(ShapeRenderer drawer)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double len = StrictMath.hypot(getEndX(0)-getStartX(0),
				getEndY(0)-getStartY(0));
		if (len > 0)
		{
			double dx = (getStartY(0) - getEndY(0)) * 10 / len;
			double dy = (getEndX(0) - getStartX(0)) * 10 / len;
			drawer.line((float)(getStartX(0)), (float)(getStartY(0)),
					(float)(getEndX(0)), (float)(getEndY(0)));
			drawer.line((float)(getStartX(0)), (float)(getStartY(0)),
					(float)(getStartX(0) - dx), (float)(getStartY(0) - dy));
			drawer.line((float)(getEndX(0)), (float)(getEndY(0)),
					(float)(getEndX(0) - dx), (float)(getEndY(0) - dy));
		}
		else
			drawer.point((float)getStartX(0), (float)getStartY(0), 0);

		len = StrictMath.hypot(getEndX(1)-getStartX(1),
				getEndY(1)-getStartY(1));
		if (len > 0)
		{
			double dx = (getStartY(1) - getEndY(1)) * 10 / len;
			double dy = (getEndX(1) - getStartX(1)) * 10 / len;
			drawer.line((float)(getStartX(1)), (float)(getStartY(1)),
					(float)(getEndX(1)), (float)(getEndY(1)));
			drawer.line((float)(getStartX(1)), (float)(getStartY(1)),
					(float)(getStartX(1) - dx), (float)(getStartY(1) - dy));
			drawer.line((float)(getEndX(1)), (float)(getEndY(1)),
					(float)(getEndX(1) - dx), (float)(getEndY(1) - dy));
		}
		else
			drawer.point((float)getStartX(1), (float)getStartY(1), 0);

	}
}
