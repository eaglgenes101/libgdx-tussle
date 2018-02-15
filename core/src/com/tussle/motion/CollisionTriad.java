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

import com.tussle.collision.ProjectionVector;

public class CollisionTriad
{
	double cumulativeX;
	double cumulativeY;
	ProjectionVector mostRecent;
	
	public CollisionTriad(ProjectionVector start)
	{
		cumulativeX = start.xComp();
		cumulativeY = start.yComp();
		mostRecent = start;
	}
	
	public CollisionTriad(CollisionTriad previous, ProjectionVector newDisp)
	{
		cumulativeX = previous.cumulativeX + newDisp.xComp();
		cumulativeY = previous.cumulativeY + newDisp.yComp();
		mostRecent = newDisp;
	}
	
	public CollisionTriad(CollisionTriad previous, CollisionTriad next)
	{
		cumulativeX = previous.cumulativeX + next.cumulativeX;
		cumulativeY = previous.cumulativeY + next.cumulativeY;
		mostRecent = next.mostRecent;
	}
	
	public double getCumulativeX()
	{
		return cumulativeX;
	}
	
	public double getCumulativeY()
	{
		return cumulativeY;
	}
	
	public ProjectionVector getMostRecent()
	{
		return mostRecent;
	}
}
