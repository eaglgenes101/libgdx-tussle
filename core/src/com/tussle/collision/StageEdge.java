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
 * Created by eaglgenes101 on 4/18/17.
 */
public strictfp class StageEdge extends StageElement
{
	private double localx0, localy0, localx1, localy1;
	private double afterx0, aftery0, afterx1, aftery1;
	private double beforex0, beforey0, beforex1, beforey1;

	public StageEdge(double x0, double y0, double x1, double y1)
	{
		super();
		localx0 = x0;
		localy0 = y0;
		localx1 = x1;
		localy1 = y1;
	}

	public void computeNewBeforePositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(befRot));
		double sin = FastMath.sin(FastMath.toRadians(befRot));
		double sx = (localx0 - befOriginX)*(befFlip?-befScale:befScale);
		double sy = (localy0 - befOriginY)*befScale;
		double ex = (localx1 - befOriginX)*(befFlip?-befScale:befScale);
		double ey = (localy1 - befOriginY)*befScale;
		double oldSX = sx;
		double oldEX = ex;
		sx = sx * cos - sy * sin;
		sy = oldSX * sin + sy * cos;
		ex = ex * cos - ey * sin;
		ey = oldEX * sin + ey * cos;
		beforex0 = sx + befOriginX + befX;
		beforey0 = sy + befOriginY + befY;
		beforex1 = ex + befOriginX + befX;
		beforey1 = ey + befOriginY + befY;
		befDirty = false;
	}
	
	public void computeNewAfterPositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(aftRot));
		double sin = FastMath.sin(FastMath.toRadians(aftRot));
		double sx = (localx0 - aftOriginX)*(aftFlip?-aftScale:aftScale);
		double sy = (localy0 - aftOriginY)*aftScale;
		double ex = (localx1 - aftOriginX)*(aftFlip?-aftScale:aftScale);
		double ey = (localy1 - aftOriginY)*aftScale;
		double oldSX = sx;
		double oldEX = ex;
		sx = sx * cos - sy * sin;
		sy = oldSX * sin + sy * cos;
		ex = ex * cos - ey * sin;
		ey = oldEX * sin + ey * cos;
		afterx0 = sx + aftOriginX + aftX;
		aftery0 = sy + aftOriginY + aftY;
		afterx1 = ex + aftOriginX + aftX;
		aftery1 = ey + aftOriginY + aftY;
		aftDirty = false;
	}

	public double getStartX(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforex0;
		if (time == 1) return afterx0;
		return (1-time) * beforex0 + time * afterx0;
	}

	public double getEndX(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforex1;
		if (time == 1) return afterx1;
		return (1-time) * beforex1 + time * afterx1;
	}

	public double getStartY(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforey0;
		if (time == 1) return aftery0;
		return (1-time) * beforey0 + time * aftery0;
	}

	public double getEndY(double time)
	{
		cleanForTime(time);
		if (time == 0) return beforey1;
		if (time == 1) return aftery1;
		return (1-time) * beforey1 + time * aftery1;
	}

	public ProjectionVector depth(CollisionStadium stad, double time)
	{
		cleanForTime(time);
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		ProjectionVector disp = Intersector.displacementSegments(
		        startX, startY, endX, endY,
		        stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		disp.xnorm = -disp.xnorm;
		disp.ynorm = -disp.ynorm;
		disp.magnitude = stad.getRadius() - disp.magnitude;
		return disp;
	}

	public double[] instantVelocity(CollisionStadium stad, double time)
	{
		computeNewBeforePositions();
		computeNewAfterPositions();
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		double startDX = afterx0 - beforex0;
		double startDY = aftery0 - beforey0;
		double endDX = afterx1 - beforex1;
		double endDY = aftery1 - beforey1;
		double section = Intersector.partSegments(startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		double secDX = (1-section)*startDX + section*endDX;
		double secDY = (1-section)*startDY + section*endDY;
		return new double[]{secDX, secDY};
	}

	public boolean collides(CollisionStadium stad, double time)
	{
		cleanForTime(time);
		double startX = getStartX(time);
		double startY = getStartY(time);
		double endX = getEndX(time);
		double endY = getEndY(time);
		double section = Intersector.partSegments(startX, startY, endX, endY,
		        stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		
		if (section <= 0 || section >= 1) return false;
		ProjectionVector disp = Intersector.displacementSegments(
				startX, startY, endX, endY,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		//Tolerance of 16 pixels
		//First, transform to the format that depth would give
		disp.xnorm = -disp.xnorm;
		disp.ynorm = -disp.ynorm;
		disp.magnitude = stad.getRadius() - disp.magnitude;
		if (disp.xnorm*(startY-endY) + disp.ynorm*(endX-startX) <= 0) return false;
		return disp.magnitude >= 0 && disp.magnitude <= 16;

	}

	public double stadiumPortion(CollisionStadium stad, double time)
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
		double startXMin = FastMath.min(getStartX(start), getStartX(end));
		double startYMin = FastMath.min(getStartY(start), getStartY(end));
		double startXMax = FastMath.max(getStartX(start), getStartX(end));
		double startYMax = FastMath.max(getStartY(start), getStartY(end));
		double endXMin = FastMath.min(getEndX(start), getEndX(end));
		double endYMin = FastMath.min(getEndY(start), getEndY(end));
		double endXMax = FastMath.max(getEndX(start), getEndX(end));
		double endYMax = FastMath.max(getEndY(start), getEndY(end));
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
		double len = FastMath.hypot(getEndX(0)-getStartX(0),
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

		len = FastMath.hypot(getEndX(1)-getStartX(1),
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
