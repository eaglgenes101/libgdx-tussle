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

/**
 * Created by eaglgenes101 on 4/27/17.
 */
public class ProjectionVector
{
	//TODO: Turn into a flyweight object
	protected double xnorm;
	protected double ynorm;
	protected double magnitude;

	public ProjectionVector(double x, double y, double dist)
	{
		xnorm = x;
		ynorm = y;
		magnitude = dist;
	}
	
	public ProjectionVector(ProjectionVector base)
	{
		xnorm = base.xnorm;
		ynorm = base.ynorm;
		magnitude = base.magnitude;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ProjectionVector)
		{
			ProjectionVector p = (ProjectionVector)o;
			return p.xnorm == xnorm && p.ynorm == ynorm && p.magnitude == magnitude;
		}
		else return false;
	}
	
	public double xComp()
	{
		return xnorm*magnitude;
	}
	
	public double yComp()
	{
		return ynorm*magnitude;
	}
	
	public double xNorm()
	{
		return xnorm;
	}
	
	public double yNorm()
	{
		return ynorm;
	}
	
	public double magnitude()
	{
		return magnitude;
	}

	@Override
	public int hashCode()
	{
		return Double.hashCode(xnorm) ^ Double.hashCode(ynorm) ^ Double.hashCode(magnitude);
	}

	@Override
	public String toString()
	{
		return String.format("{[%f, %f], %f}", xnorm, ynorm, magnitude);
	}
}
