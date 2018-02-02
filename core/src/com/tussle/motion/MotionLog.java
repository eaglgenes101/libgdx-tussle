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

import com.tussle.collision.Stadium;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class MotionLog
{
	ConcurrentSkipListMap<Double, Stadium> base;
	
	public MotionLog(Stadium start, Stadium end)
	{
		base = new ConcurrentSkipListMap<>();
		base.put(0.0, start);
		base.put(1.0, end);
	}
	
	public Stadium interpolate(double time)
	{
		if (time < 0.0 || time > 1.0)
			throw new IllegalArgumentException();
		Map.Entry<Double, Stadium> lowerEntry = base.floorEntry(time);
		Map.Entry<Double, Stadium> upperEntry = base.ceilingEntry(time);
		if (lowerEntry.getKey().equals(upperEntry.getKey()))
		{
			return new Stadium(lowerEntry.getValue());
		}
		else
		{
			double t = (time-lowerEntry.getKey())/(upperEntry.getKey()-lowerEntry.getKey());
			return new Stadium(
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
	
	public void displace(double timeFrom, double xDisp, double yDisp)
	{
	
	}
}
