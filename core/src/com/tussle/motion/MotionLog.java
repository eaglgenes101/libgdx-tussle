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

import com.tussle.collision.CollisionShape;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class MotionLog
{
	ConcurrentSkipListMap<Double, CollisionShape> base;
	
	public MotionLog(CollisionShape start, CollisionShape end)
	{
		base = new ConcurrentSkipListMap<>();
		base.put(0.0, start);
		base.put(1.0, end);
	}
	
	public CollisionShape interpolate(double time)
	{
		if (time < 0.0 || time > 1.0)
			throw new IllegalArgumentException();
		Map.Entry<Double, CollisionShape> lowerEntry = base.floorEntry(time);
		Map.Entry<Double, CollisionShape> upperEntry = base.ceilingEntry(time);
		if (lowerEntry.getKey().equals(upperEntry.getKey()))
		{
			return lowerEntry.getValue();
		}
		else
		{
			double t = (time-lowerEntry.getKey())/(upperEntry.getKey()-lowerEntry.getKey());
			return lowerEntry.getValue().interpolate(lowerEntry.getValue());
		}
	}
	
	public void displace(double timeFrom, double timeTo, double xDisp, double yDisp)
	{
		//Create entries for timeFrom and timeTo if they don't already exist
		if (timeFrom < 0 || timeFrom > 1 || timeTo < 0 || timeTo > 1)
			throw new IllegalArgumentException();
		if (!base.containsKey(timeFrom))
			base.put(timeFrom, interpolate(timeFrom));
		if (!base.containsKey(timeTo))
			base.put(timeTo, interpolate(timeTo));
		for (Map.Entry<Double, CollisionShape> entry : base.tailMap(timeTo).entrySet())
		{
			entry.setValue(entry.getValue().displacementBy(xDisp, yDisp));
		}
	}
}
