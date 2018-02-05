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
 * Created by eaglgenes101 on 5/17/17.
 */
public strictfp class StageCorner extends StageElement
{
	public static final double HALF_WHOLE = 180;
	private double localx, localy, localMinAngle, localMaxAngle;
	private double afterx, aftery, afterMinAngle, afterMaxAngle;
	private double afterRightCos, afterRightSin, afterLeftCos, afterLeftSin;
	private double beforex, beforey, beforeMinAngle, beforeMaxAngle;
	private double beforeRightCos, beforeRightSin, beforeLeftCos, beforeLeftSin;

	public StageCorner(double x, double y, double minAngle, double maxAngle)
	{
		super();
		localx = x;
		localy = y;
		localMinAngle = minAngle;
		localMaxAngle = maxAngle;
	}

	public void computeNewBeforePositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(befRot));
		double sin = FastMath.sin(FastMath.toRadians(befRot));
		double locx = (localx - befOriginX)*(befFlip?-befScale:befScale);
		double locy = (localy - befOriginY)*befScale;
		double minAngle = befFlip?HALF_WHOLE-localMinAngle:localMinAngle + befRot;
		double maxAngle = befFlip?HALF_WHOLE-localMaxAngle:localMaxAngle + befRot;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		beforex = locx + befOriginX + befX;
		beforey = locy + befOriginY + befY;
		beforeMinAngle = minAngle;
		beforeMaxAngle = maxAngle;
		beforeRightCos = FastMath.cos(FastMath.toRadians(minAngle));
		beforeRightSin = FastMath.sin(FastMath.toRadians(minAngle));
		beforeLeftCos = FastMath.cos(FastMath.toRadians(maxAngle));
		beforeLeftSin = FastMath.sin(FastMath.toRadians(maxAngle));
		befDirty = false;
	}
	
	public void computeNewAfterPositions()
	{
		double cos = FastMath.cos(FastMath.toRadians(aftRot));
		double sin = FastMath.sin(FastMath.toRadians(aftRot));
		double locx = (localx - aftOriginX)*(aftFlip?-aftScale:aftScale);
		double locy = (localy - aftOriginY)*aftScale;
		double minAngle = aftFlip?HALF_WHOLE-localMinAngle:localMinAngle + aftRot;
		double maxAngle = aftFlip?HALF_WHOLE-localMaxAngle:localMaxAngle + aftRot;
		double oldX = locx;
		locx = locx * cos - locy * sin;
		locy = oldX * sin + locy * cos;
		afterx = locx + aftOriginX + aftX;
		aftery = locy + aftOriginY + aftY;
		afterMinAngle = minAngle;
		afterMaxAngle = maxAngle;
		afterRightCos = FastMath.cos(FastMath.toRadians(minAngle));
		afterRightSin = FastMath.sin(FastMath.toRadians(minAngle));
		afterLeftCos = FastMath.cos(FastMath.toRadians(maxAngle));
		afterLeftSin = FastMath.sin(FastMath.toRadians(maxAngle));
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

	ProjectionVector getRightNormal(double time)
	{
		cleanForTime(time);
		//Not as simple as interpolating angle unfortunately
		double interpCos = (1-time) * beforeRightCos + time * afterRightCos;
		double interpSin = (1-time) * beforeRightSin + time * afterRightSin;
		if (interpCos == 0 && interpSin == 0)
		{
			if (beforeRightCos * afterRightCos + beforeRightSin * afterRightSin > 0)
			{
				return new ProjectionVector(afterRightCos, afterRightSin, 1);
			}
			else
			{
				return new ProjectionVector(afterRightSin, -afterRightCos, 1);
			}
		}
		double magn = FastMath.hypot(interpSin, interpCos);
		return new ProjectionVector(interpCos/magn, interpSin/magn, 1);
	}

	ProjectionVector getLeftNormal(double time)
	{
		cleanForTime(time);
		//Not as simple as interpolating angle unfortunately
		double interpCos = (1-time) * beforeLeftCos + time * afterLeftCos;
		double interpSin = (1-time) * beforeLeftSin + time * afterLeftSin;
		if (interpCos == 0 && interpSin == 0)
		{
			if (beforeLeftCos * afterLeftCos + beforeLeftSin * afterLeftSin > 0)
			{
				return new ProjectionVector(afterLeftCos, afterLeftSin, 1);
			}
			else
			{
				return new ProjectionVector(afterLeftSin, -afterLeftCos, 1);
			}
		}
		double magn = FastMath.hypot(interpSin, interpCos);
		return new ProjectionVector(interpCos/magn, interpSin/magn, 1);
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
		//disp.magnitude = stad.getRadius()-disp.magnitude;
		disp.magnitude -= stad.getRadius();
		return disp;
	}

	public double[] instantVelocity(Stadium stad, double time)
	{
		computeNewBeforePositions();
		computeNewAfterPositions();
		double secDX = getX(1)-getX(0);
		double secDY = getY(1)-getY(0);
		return new double[]{secDX, secDY};
	}

	public boolean collides(Stadium stad, double time)
	{
		cleanForTime(time);
		double xPos = getX(time);
		double yPos = getY(time);
		ProjectionVector disp = Intersector.dispSegmentPoint(stad.getStartx(),
				stad.getStarty(), stad.getEndx(), stad.getEndy(), xPos, yPos);
		ProjectionVector rightNormal = getRightNormal(time);
		ProjectionVector leftNormal = getLeftNormal(time);
		//if (stad.getRadius()-disp.magnitude < 0) return false;
		//if (stad.getRadius()-disp.magnitude > 16) return false;
		if (disp.magnitude - stad.getRadius() < 0) return false;
		if (disp.magnitude - stad.getRadius() > 16) return false;
		//Thanks stack overflow!
		return (rightNormal.ynorm*disp.xnorm-rightNormal.xnorm*disp.ynorm) *
			   (rightNormal.ynorm*leftNormal.xnorm-rightNormal.xnorm*leftNormal.ynorm) < 0;

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
		double minX = FastMath.min(getX(start), getX(end));
		double maxX = FastMath.max(getX(start), getX(end));
		double minY = FastMath.min(getY(start), getY(end));
		double maxY = FastMath.max(getY(start), getY(end));
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}

	public void draw(ShapeRenderer drawer)
	{
		computeNewBeforePositions();
		computeNewAfterPositions();
		
		ProjectionVector startRight = getRightNormal(0);
		ProjectionVector startLeft = getLeftNormal(0);
		ProjectionVector endRight = getRightNormal(1);
		ProjectionVector endLeft = getLeftNormal(1);
		
		drawer.arc((float)getX(0), (float)getY(0), 12,
		           (float)beforeMinAngle, (float)(beforeMaxAngle - beforeMinAngle));
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)+startRight.xnorm*20), (float)(getY(0)+startRight.ynorm*20));
		drawer.line((float)getX(0), (float)getY(0),
				(float)(getX(0)+startLeft.xnorm*20), (float)(getY(0)+startLeft.ynorm*20));

		drawer.arc((float)getX(1), (float)getY(1), 16,
		           (float)afterMinAngle, (float)(afterMaxAngle - afterMinAngle));
		drawer.line((float)getX(1), (float)getY(1),
				(float)(getX(1)+endRight.xnorm*20), (float)(getY(1)+endRight.ynorm*20));
		drawer.line((float)getX(1), (float)getY(1),
				(float)(getX(1)+endLeft.xnorm*20), (float)(getY(1)+endLeft.ynorm*20));
	}
}
