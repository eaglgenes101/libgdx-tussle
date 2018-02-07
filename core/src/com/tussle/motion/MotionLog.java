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

import com.tussle.collision.CollisionStadium;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class MotionLog
{
	ConcurrentSkipListMap<Double, CollisionStadium> base;
	
	public MotionLog(CollisionStadium start, CollisionStadium end)
	{
		base = new ConcurrentSkipListMap<>();
		base.put(0.0, start);
		base.put(1.0, end);
	}
	
	public CollisionStadium interpolate(double time)
	{
		if (time < 0.0 || time > 1.0)
			throw new IllegalArgumentException();
		Map.Entry<Double, CollisionStadium> lowerEntry = base.floorEntry(time);
		Map.Entry<Double, CollisionStadium> upperEntry = base.ceilingEntry(time);
		if (lowerEntry.getKey().equals(upperEntry.getKey()))
		{
			return new CollisionStadium(lowerEntry.getValue());
		}
		else
		{
			double t = (time-lowerEntry.getKey())/(upperEntry.getKey()-lowerEntry.getKey());
			return new CollisionStadium(
					(1-t)*lowerEntry.getValue().getStartx() +
					t*upperEntry.getValue().getStartx(),
					(1-t)*lowerEntry.getValue().getStarty() +
					t*upperEntry.getValue().getStarty(),
					(1-t)*lowerEntry.getValue().getEndx() +
					t*upperEntry.getValue().getEndx(),
					(1-t)*lowerEntry.getValue().getEndy() +
					t*upperEntry.getValue().getEndy(),
					(1-t)*lowerEntry.getValue().getRadius() +
					t*upperEntry.getValue().getEndy()
			);
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
	}
}
