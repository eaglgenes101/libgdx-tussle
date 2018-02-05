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
	private double localx0, localy0, localx1, localy1, localrad;
	private double beforex0, beforey0, beforex1, beforey1, beforerad;
	private double afterx0, aftery0, afterx1, aftery1, afterrad;
	
	public CollisionBox(double startx, double starty, double endx, double endy, double radius)
	{
		super();
		localx0 = startx; localy0 = starty; localx1 = endx; localy1 = endy; localrad = radius;
	}
	
	public CollisionBox(CollisionBox base)
	{
		super(base);
		localx0 = base.localx0; localy0 = base.localy0;
		localx1 = base.localx1; localy1 = base.localy1; localrad = base.localrad;
	}

	public Stadium getAfterStadium()
	{
		if (aftDirty) computeNewAfterPositions();
		return new Stadium(afterx0, aftery0, afterx1, aftery1, afterrad);
	}

	public Stadium getBeforeStadium()
	{
		if (befDirty) computeNewBeforePositions();
		return new Stadium(beforex0, beforey0, beforex1, beforey1, beforerad);
	}
	
	public void computeNewBeforePositions()
	{
		double sx = (localx0 - befOriginX) * (befFlip ? -befScale : befScale);
		double sy = (localy0 - befOriginY) * befScale;
		double ex = (localx1 - befOriginX) * (befFlip ? -befScale : befScale);
		double ey = (localy1 - befOriginY) * befScale;
		double rad = FastMath.abs(localrad * befScale);
		double cos = FastMath.cos(FastMath.toRadians(befRot));
		double sin = FastMath.sin(FastMath.toRadians(befRot));
		double oldSX = sx;
		double oldEX = ex;
		sx = sx*cos - sy*sin;
		sy = oldSX*sin + sy*cos;
		ex = ex*cos - ey*sin;
		ey = oldEX*sin + ey*cos;
		beforex0 = sx + befOriginX + befX;
		beforey0 = sy + befOriginY + befY;
		beforex1 = ex + befOriginX + befX;
		beforey1 = ey + befOriginY + befY;
		beforerad = rad;
		befDirty = false;
	}
	
	public void computeNewAfterPositions()
	{
		double sx = (localx0 - aftOriginX) * (aftFlip ? -aftScale : aftScale);
		double sy = (localy0 - aftOriginY) * aftScale;
		double ex = (localx1 - aftOriginX) * (aftFlip ? -aftScale : aftScale);
		double ey = (localy1 - aftOriginY) * aftScale;
		double rad = FastMath.abs(localrad * aftScale);
		double cos = FastMath.cos(FastMath.toRadians(aftRot));
		double sin = FastMath.sin(FastMath.toRadians(aftRot));
		double oldSX = sx;
		double oldEX = ex;
		sx = sx*cos - sy*sin;
		sy = oldSX*sin + sy*cos;
		ex = ex*cos - ey*sin;
		ey = oldEX*sin + ey*cos;
		afterx0 = sx + aftOriginX + aftX;
		aftery0 = sy + aftOriginY + aftY;
		afterx1 = ex + aftOriginX + aftX;
		aftery1 = ey + aftOriginY + aftY;
		afterrad = rad;
		aftDirty = false;
	}

	public double getStartX(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforex0;
		if (time == 1) return afterx0;
		return (1-time)*beforex0 + time*afterx0;
	}

	public double getEndX(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforex1;
		if (time == 1) return afterx1;
		return (1-time)*beforex1 + time*afterx1;
	}

	public double getStartY(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforey0;
		if (time == 1) return aftery0;
		return (1-time)*beforey0 + time*aftery0;
	}

	public double getEndY(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforey1;
		if (time == 1) return aftery1;
		return (1-time)*beforey1 + time*aftery1;
	}

	public double getRadius(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforerad;
		if (time == 1) return afterrad;
		return (1-time)*beforerad + time*afterrad;
	}

	public Stadium getStadiumAt(double time)
	{
		return new Stadium(getStartX(time), getStartY(time),
				getEndX(time), getEndY(time), getRadius(time));
	}

	public ProjectionVector depth(Stadium stad, double time)
	{
		cleanForTime(time);
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
		computeNewBeforePositions();
		computeNewAfterPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		double startDX = getAfterStadium().getStartx() - getBeforeStadium().getStartx();
		double startDY = getAfterStadium().getStarty() - getBeforeStadium().getStarty();
		double endDX = getAfterStadium().getEndx() - getBeforeStadium().getEndx();
		double endDY = getAfterStadium().getEndy() - getBeforeStadium().getEndy();
		double section = Intersector.partSegments(startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		double secDX = (1-section)*startDX + section*endDX;
		double secDY = (1-section)*startDY + section*endDY;
		return new double[]{secDX, secDY};
	}

	public boolean collides(Stadium stad, double time)
	{
		cleanForTime(time);
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
		cleanForTime(time);
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		return Intersector.partSegments(stad.getStartx(), stad.getStarty(),
				stad.getEndx(), stad.getEndy(), startX, startY, endX, endY);
	}

	public Rectangle getBounds(double start, double end)
	{
		computeNewBeforePositions();
		computeNewAfterPositions();
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
		computeNewBeforePositions();
		computeNewAfterPositions();
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
