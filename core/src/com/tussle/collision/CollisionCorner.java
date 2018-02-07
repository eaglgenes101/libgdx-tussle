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
	double x, y;
	ProjectionVector minVec, maxVec;
	
	public CollisionCorner(double x, double y, double min, double max)
	{
		this.x = x;
		this.y = y;
		minVec = new ProjectionVector(
				FastMath.cos(FastMath.toRadians(min)),
				FastMath.sin(FastMath.toRadians(min)),
				1
		);
		maxVec = new ProjectionVector(
				FastMath.cos(FastMath.toRadians(max)),
				FastMath.sin(FastMath.toRadians(max)),
				1
		);
	}
	
	public CollisionCorner(double x, double y, ProjectionVector min, ProjectionVector max)
	{
		this.x = x;
		this.y = y;
		this.minVec = min;
		this.maxVec = max;
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
		ProjectionVector disp = depth(stad);
		return disp.magnitude() <= 16 &&
		       (minVec.yNorm()*disp.xnorm-minVec.xNorm()*disp.ynorm) *
		       (minVec.yNorm()*maxVec.xNorm()-minVec.xNorm()*maxVec.yNorm()) < 0;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, 0, 0);
	}
	
	public void draw(ShapeRenderer drawer)
	{
		double minAngle = FastMath.toDegrees(FastMath.atan2(minVec.yNorm(), minVec.xNorm()));
		double maxAngle = FastMath.toDegrees(FastMath.atan2(maxVec.yNorm(), maxVec.xNorm()));
		drawer.arc((float)x, (float)y, 12,
		           (float)minAngle, (float)(maxAngle - minAngle));
		drawer.line((float)x, (float)y,
		            (float)(x+minVec.xNorm()*20), (float)(y+minVec.yNorm()*20));
		drawer.line((float)x, (float)y,
		            (float)(x+maxVec.xNorm()*20), (float)(y+maxVec.yNorm()*20));
	}
	
	public CollisionCorner displacement(double dx, double dy)
	{
		return new CollisionCorner(
				x+dx, y+dy,
				minVec, maxVec
		);
	}
	
	public CollisionCorner interpolate(CollisionShape other)
	{
		if (!(other instanceof CollisionCorner))
			throw new IllegalArgumentException();
		CollisionCorner o = (CollisionCorner)other;
		double minXSum = minVec.xComp()+o.minVec.xComp();
		double minYSum = minVec.yComp()+o.minVec.yComp();
		double maxXSum = maxVec.xComp()+o.maxVec.xComp();
		double maxYSum = maxVec.yComp()+o.maxVec.yComp();
		ProjectionVector minProj;
		ProjectionVector maxProj;
		if (minXSum == 0 && minYSum == 0)
		{
			if (minVec.xComp() * o.minVec.xComp() + minVec.yComp() * o.minVec.yComp() > 0)
			{
				minProj = new ProjectionVector(minVec.xComp(), minVec.yComp(), 0);
			}
			else
			{
				minProj = new ProjectionVector(minVec.yComp(), -minVec.xComp(), 0);
			}
		}
		else
		{
			double magnitude = FastMath.hypot(minXSum, minYSum);
			minProj = new ProjectionVector(
					minXSum/magnitude, minYSum/magnitude, magnitude/2);
		}
		if (maxXSum == 0 && maxYSum == 0)
		{
			if (maxVec.xComp() * o.maxVec.xComp() + maxVec.yComp() * o.maxVec.yComp() > 0)
			{
				maxProj = new ProjectionVector(maxVec.xComp(), maxVec.yComp(), 0);
			}
			else
			{
				maxProj = new ProjectionVector(maxVec.yComp(), -maxVec.xComp(), 0);
			}
		}
		else
		{
			double magnitude = FastMath.hypot(maxXSum, maxYSum);
			maxProj = new ProjectionVector(
					maxXSum/magnitude, maxYSum/magnitude, magnitude/2);
		}
		return new CollisionCorner((x+o.x)/2, (y+o.y)/2, minProj, maxProj);
	}
}
