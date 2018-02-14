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

public class CollisionCircle implements CollisionShape
{
	protected double x;
	protected double y;
	protected double radius;
	
	public CollisionCircle(double x, double y, double rad)
	{
		this.x = x;
		this.y = y;
		radius = rad;
	}
	
	public ProjectionVector depth(CollisionStadium stad)
	{
		
		ProjectionVector disp = Intersector.dispSegmentPoint(
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(), x, y);
		//disp.xnorm = -disp.xnorm;
		//disp.ynorm = -disp.ynorm;
		//disp.magnitude = stad.getRadius()+getRadius(time)-disp.magnitude;
		disp.magnitude -= stad.getRadius()+radius;
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
		return true;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(x-radius, y-radius, 2*radius, 2*radius);
	}
	
	public void draw(ShapeRenderer drawer)
	{
		drawer.circle((float)x, (float)y, (float)radius);
	}
	
	public CollisionCircle displacementBy(double dx, double dy)
	{
		return new CollisionCircle(x+dx, y+dy, radius);
	}
	
	public CollisionCircle interpolate(CollisionShape other)
	{
		if (!(other instanceof CollisionCircle))
			throw new IllegalArgumentException();
		CollisionCircle o = (CollisionCircle)other;
		return new CollisionCircle(
				(x+o.x)/2,
				(y+o.y)/2,
				(radius+o.radius)/2
		);
	}
	
	public CollisionCircle transformBy(double dx, double dy, double rot, double scale, boolean flip)
	{
		return new CollisionCircle(x+dx, y+dy, FastMath.abs(radius*scale));
	}
}
