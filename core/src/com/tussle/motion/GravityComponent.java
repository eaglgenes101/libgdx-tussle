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

package com.tussle.motion;

import com.tussle.main.Utility;

//Handles gravity falling
public class GravityComponent
{
	double fallSpeed;
	double fallAcceleration;
	double fastFallAddition;
	
	public GravityComponent(double speed, double accel, double plusAccel)
	{
		fallSpeed = speed;
		fallAcceleration = accel;
		fastFallAddition = plusAccel;
	}
	
	public double getAccel(double currentSpeed, boolean isFastFalling)
	{
		double provisionalAccel = fallAcceleration+(isFastFalling?fastFallAddition:0);
		return Utility.addFrom(currentSpeed, -provisionalAccel, -fallSpeed)
				- currentSpeed;
	}
}
