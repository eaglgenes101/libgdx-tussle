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
 * Created by eaglgenes101 on 4/15/17.
 */
public strictfp class CollisionBox extends StageElement
{
	private Stadium localArea;
	private Stadium currentArea;
	private Stadium previousArea;

	public CollisionBox(Stadium base)
	{
		localArea = base;
		currentArea = base;
		previousArea = base;
	}
	
	public CollisionBox(double startx, double starty, double endx, double endy, double radius)
	{
		localArea = new Stadium(startx, starty, endx, endy, radius);
		currentArea = new Stadium(localArea);
		previousArea = new Stadium(localArea);
	}
	
	public CollisionBox(CollisionBox base)
	{
		this.localArea = new Stadium(base.localArea);
		this.currentArea = new Stadium(base.currentArea);
		this.previousArea = new Stadium(base.previousArea);
	}

	public Stadium getCurrentStadium()
	{
		if (coordinatesDirty)
			computeNewPositions();
		return currentArea;
	}

	public Stadium getPreviousStadium()
	{
		if (coordinatesDirty)
			computeNewPositions();
		return previousArea;
	}

	public void setAreas()
	{
		previousArea.set(currentArea);
	}

	public void computeNewPositions()
	{
		double sx = localArea.startx - originX;
		double sy = localArea.starty - originY;
		double ex = localArea.endx - originX;
		double ey = localArea.endy - originY;
		double rad = FastMath.abs(localArea.radius*scale);
		sx *= flipped?-scale:scale;
		sy *= scale;
		ex *= flipped?-scale:scale;
		ey *= scale;
		double cos = FastMath.cos(FastMath.toRadians(rotation));
		double sin = FastMath.sin(FastMath.toRadians(rotation));
		double oldSX = sx;
		double oldEX = ex;
		sx = sx*cos - sy*sin;
		sy = oldSX*sin + sy*cos;
		ex = ex*cos - ey*sin;
		ey = oldEX*sin + ey*cos;
		currentArea.setStart(sx+originX+x, sy+originY+y)
				.setEnd(ex+originX+x, ey+originY+y).setRadius(rad);
		coordinatesDirty = false;
		if (start)
		{
			start = false;
			setAreas();
		}
	}

	public void setStadium(Stadium newStadium)
	{
		if (coordinatesDirty)
			computeNewPositions();
		localArea.set(newStadium);
		coordinatesDirty = true;
	}

	public double getStartX(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*getPreviousStadium().getStartx() +
				(time)*getCurrentStadium().getStartx();
	}

	public double getEndX(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*getPreviousStadium().getEndx() +
				(time)*getCurrentStadium().getEndx();
	}

	public double getStartY(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*getPreviousStadium().getStarty() +
				(time)*getCurrentStadium().getStarty();
	}

	public double getEndY(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*getPreviousStadium().getEndy() +
				(time)*getCurrentStadium().getEndy();
	}

	public double getRadius(double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		return (1-time)*getPreviousStadium().getRadius() +
				(time)*getCurrentStadium().getRadius();
	}

	public Stadium getStadiumAt(double time)
	{
		return new Stadium(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), getRadius(time));
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
		disp.xnorm = -disp.xnorm;
		disp.ynorm = -disp.ynorm;
		disp.magnitude = stad.getRadius()+getRadius(time)-disp.magnitude;
		return disp;
	}

	public double[] instantVelocity(Stadium stad, double time)
	{
		if (coordinatesDirty)
			computeNewPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		double startDX = getCurrentStadium().getStartx() - getPreviousStadium().getStartx();
		double startDY = getCurrentStadium().getStarty() - getPreviousStadium().getStarty();
		double endDX = getCurrentStadium().getEndx() - getPreviousStadium().getEndx();
		double endDY = getCurrentStadium().getEndy() - getPreviousStadium().getEndy();
		double section = Intersector.partSegments(startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		double secDX = (1-section)*startDX + section*endDX;
		double secDY = (1-section)*startDY + section*endDY;
		return new double[]{secDX, secDY};
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
		return disp.magnitude <= getRadius(time) + stad.getRadius();
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
		double startXMin = FastMath.min(getStartX(start)-getRadius(start),
				getStartX(end)-getRadius(end));
		double startYMin = FastMath.min(getStartY(start)-getRadius(start),
				getStartY(end)-getRadius(end));
		double startXMax = FastMath.max(getStartX(start)+getRadius(start),
				getStartX(end)+getRadius(end));
		double startYMax = FastMath.max(getStartY(start)+getRadius(start),
				getStartY(end)+getRadius(end));
		double endXMin = FastMath.min(getEndX(start)-getRadius(start),
				getEndX(end)-getRadius(end));
		double endYMin = FastMath.min(getEndY(start)-getRadius(start),
				getEndY(end)-getRadius(end));
		double endXMax = FastMath.max(getEndX(start)+getRadius(start),
				getEndX(end)+getRadius(end));
		double endYMax = FastMath.max(getEndY(start)+getRadius(start),
				getEndY(end)+getRadius(end));
		double minX = FastMath.min(startXMin, endXMin);
		double maxX = FastMath.max(startXMax, endXMax);
		double minY = FastMath.min(startYMin, endYMin);
		double maxY = FastMath.max(startYMax, endYMax);
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void draw(ShapeRenderer drawer)
	{
		if (coordinatesDirty)
			computeNewPositions();
		drawer.circle((float)getStartX(0), (float)getStartY(0), (float)getRadius(0));
		drawer.circle((float)getEndX(0), (float)getEndY(0), (float)getRadius(0));
		double len = FastMath.hypot(getEndX(0)-getStartX(0),
				getEndY(0)-getStartY(0));
		if (len > 0)
		{
			double dx = (getStartY(0) - getEndY(0)) * getRadius(0) / len;
			double dy = (getEndX(0) - getStartX(0)) * getRadius(0) / len;
			drawer.line((float)(getStartX(0) + dx), (float)(getStartY(0) + dy),
					(float)(getEndX(0) + dx), (float)(getEndY(0) + dy));
			drawer.line((float)(getStartX(0) - dx), (float)(getStartY(0) - dy),
					(float)(getEndX(0) - dx), (float)(getEndY(0) - dy));
		}

		drawer.circle((float)getStartX(1), (float)getStartY(1), (float)getRadius(1));
		drawer.circle((float)getEndX(1), (float)getEndY(1), (float)getRadius(1));
		len = FastMath.hypot(getEndX(1)-getStartX(1),
				getEndY(1)-getStartY(1));
		if (len > 0)
		{
			double dx = (getStartY(1) - getEndY(1)) * getRadius(1) / len;
			double dy = (getEndX(1) - getStartX(1)) * getRadius(1) / len;
			drawer.line((float)(getStartX(1) + dx), (float)(getStartY(1) + dy),
					(float)(getEndX(1) + dx), (float)(getEndY(1) + dy));
			drawer.line((float)(getStartX(1) - dx), (float)(getStartY(1) - dy),
					(float)(getEndX(1) - dx), (float)(getEndY(1) - dy));
		}
	}
}
