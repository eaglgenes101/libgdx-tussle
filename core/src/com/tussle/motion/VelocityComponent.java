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

package com.tussle.motion;

import com.badlogic.ashley.core.Component;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class VelocityComponent implements Component
{
	public double xVel;
	public double yVel;
	
	public void setVelocity(double x, double y)
	{
		xVel = x;
		yVel = y;
	}
	
	public void accelerate(double dx, double dy)
	{
		xVel += dx;
		yVel += dy;
	}
	
	public String toString()
	{
		return "(" + xVel + ", " + yVel + ")";
	}
}
