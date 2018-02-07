/*
 * Copyright (c) 2018 eaglgenes101
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

public class CollisionCorner implements CollisionShape
{
	double x, y, minAngle, maxAngle;
	double minCos = Double.NaN, minSin = Double.NaN;
	double maxCos = Double.NaN, maxSin = Double.NaN;
	boolean trigInit = false;
	
	public CollisionCorner(double x, double y, double min, double max)
	{
		this.x = x;
		this.y = y;
		minAngle = min;
		maxAngle = max;
	}
	
	protected void initTrig()
	{
		if (!trigInit)
		{
			minCos = FastMath.cos(FastMath.toRadians(minAngle));
			minSin = FastMath.sin(FastMath.toRadians(minAngle));
			maxCos = FastMath.cos(FastMath.toRadians(maxAngle));
			maxSin = FastMath.sin(FastMath.toRadians(maxAngle));
			trigInit = true;
		}
	}
	
	public ProjectionVector depth(CollisionStadium stad)
	{
		ProjectionVector disp = Intersector.dispSegmentPoint(
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(), x, y);
		//disp.xnorm = -disp.xnorm;
		//disp.ynorm = -disp.ynorm;
		//disp.magnitude = stad.getRadius()-disp.magnitude;
		disp.magnitude -= stad.getRadius();
		return disp;
	}
	
	public double[] nearestPoint(CollisionStadium stad)
	{
		return new double[]{x, y};
	}
	
	public double stadiumPortion(CollisionStadium stad)
	{
		return Intersector.partSegmentPoint(
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(), x, y);
	}
	
	public boolean collidesWith(CollisionStadium stad)
	{
		initTrig();
		ProjectionVector disp = depth(stad);
		return disp.magnitude() <= 16 &&
		       (minSin*disp.xnorm-minCos*disp.ynorm) * (minSin*maxCos-minCos*maxSin) < 0;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, 0, 0);
	}
	
	public void draw(ShapeRenderer drawer)
	{
		initTrig();
		drawer.arc((float)x, (float)y, 12, (float)minAngle, (float)(maxAngle - minAngle));
		drawer.line((float)x, (float)y, (float)(x+minCos*20), (float)(y+minSin*20));
		drawer.line((float)x, (float)y, (float)(x+maxCos*20), (float)(y+maxSin*20));
	}
	
	public CollisionCorner displacement(double dx, double dy)
	{
		return new CollisionCorner(x+dx, y+dy, minAngle, maxAngle);
	}
}
