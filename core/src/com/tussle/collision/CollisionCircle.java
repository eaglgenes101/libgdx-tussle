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

import com.tussle.main.Intersector;

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
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(),
				x, y);
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
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(),
				x, y);
	}
	
	public boolean collidesWith(CollisionStadium stad)
	{
	
	}
}
