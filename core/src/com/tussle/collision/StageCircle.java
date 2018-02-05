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
	private double localx, localy, localrad;
	private double afterx, aftery, afterrad;
	private double beforex, beforey, beforerad;

	public StageCircle(double x, double y, double r)
	{
		super();
		localx = x;
		localy = y;
		localrad = r;
	}
	
	public void computeNewBeforePositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(befRot));
		double sin = FastMath.sin(FastMath.toRadians(befRot));
		double locx = (localx - befOriginX)*(befFlip?-befScale:befScale);
		double locy = (localy - befOriginY)*befScale;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		beforex = locx + befOriginX + befX;
		beforey = locy + befOriginY + befY;
		beforerad = FastMath.abs(localrad * befScale);
		befDirty = false;
	}
	
	public void computeNewAfterPositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(befRot));
		double sin = FastMath.sin(FastMath.toRadians(befRot));
		double locx = (localx - befOriginX)*(befFlip?-befScale:befScale);
		double locy = (localy - befOriginY)*befScale;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		afterx = locx + befOriginX + befX;
		aftery = locy + befOriginY + befY;
		afterrad = FastMath.abs(localrad * befScale);
		aftDirty = false;
	}

	public double getX(double time)
	{
		cleanForTime(time);
		return (1-time) * beforex + time * afterx;
	}

	public double getY(double time)
	{
		cleanForTime(time);
		return (1-time) * beforey + time * aftery;
	}

	public double getRadius(double time)
	{
		cleanForTime(time);
		return (1-time) * beforerad + time * afterrad;
	}

	public ProjectionVector depth(Stadium stad, double time)
	{
		cleanForTime(time);
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
		computeNewBeforePositions();
		computeNewAfterPositions();
		double secDX = afterx - beforex;
		double secDY = aftery - beforey;
		return new double[]{secDX, secDY};
	}

	public boolean collides(Stadium stad, double time)
	{
		cleanForTime(time);
		double xPos = getX(time);
		double yPos = getY(time);
		ProjectionVector disp = Intersector.dispSegmentPoint(stad.getStartx(),
				stad.getStarty(), stad.getEndx(), stad.getEndy(), xPos, yPos);
		return stad.getRadius() - disp.magnitude + getRadius(time) <= 0;
	}

	public double stadiumPortion(Stadium stad, double time)
	{
		cleanForTime(time);
		return Intersector.partSegmentPoint(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), getX(time), getY(time));
	}

	public Rectangle getBounds(double start, double end)
	{
		computeNewBeforePositions();
		computeNewAfterPositions();
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
		computeNewBeforePositions();
		computeNewAfterPositions();
		drawer.circle((float)getX(0), (float)getY(0), (float)getRadius(0));
		drawer.circle((float)getX(1), (float)getY(1), (float)getRadius(1));
	}

}
