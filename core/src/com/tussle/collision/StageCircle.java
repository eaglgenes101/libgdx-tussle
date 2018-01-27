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
		double cos = FastMath.cos(FastMath.toRadians(rotation));
		double sin = FastMath.sin(FastMath.toRadians(rotation));
		double locx = localx - originX;
		double locy = localy - originY;
		locx *= flipped ? -scale : scale;
		locy *= scale;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		currentx = locx + originX + x;
		currenty = locy + originY + y;
		currentr = FastMath.abs(localr*scale);
		coordinatesDirty = false;
		if (start)
		{
			start = false;
			setAreas();
		}
	}

	public void setAreas()
	{
		previousx = currentx;
		previousy = currenty;
		previousr = currentr;
	}

	public void setPoint(double x, double y)
	{
		localx = x;
		localy = y;
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

	public double getRadius(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*previousr + time*currentr;
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
		//disp.magnitude = stad.getRadius()+getRadius(time)-disp.magnitude;
		disp.magnitude -= stad.getRadius()+getRadius(time);
		return disp;
	}

	public double[] instantVelocity(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double secDX = currentx - previousx;
		double secDY = currenty - previousy;
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
		return stad.getRadius() - disp.magnitude + getRadius(time) <= 0;
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
		double minX = FastMath.min(getX(start)-getRadius(start),
				getX(end)-getRadius(end));
		double maxX = FastMath.max(getX(start)+getRadius(start),
				getX(end)+getRadius(end));
		double minY = FastMath.min(getY(start)-getRadius(start),
				getY(end)+getRadius(end));
		double maxY = FastMath.max(getY(start)+getRadius(start),
				getY(end)+getRadius(end));
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void draw(ShapeRenderer drawer)
	{
		if (coordinatesDirty)
			computeNewPositions();
		drawer.circle((float)getX(0), (float)getY(0), (float)getRadius(0));
		drawer.circle((float)getX(1), (float)getY(1), (float)getRadius(1));
	}

}
