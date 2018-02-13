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

import org.apache.commons.math3.util.FastMath;

public class StageCircle extends StageElement
{
	private double localx, localy, localrad;
	private double afterx, aftery, afterrad;
	private double beforex, beforey, beforerad;

	public StageCircle(double x, double y, double r, double globalX, double globalY,
	                   double scale)
	{
		localx = x;
		localy = y;
		localrad = r;
		formFor(globalX, globalY, scale);
	}
	
	protected void formFor(double globalX, double globalY, double scale)
	{
		afterx = localx + globalX;
		aftery = localy + globalY;
		afterrad = FastMath.abs(localrad * scale);
	}
	
	public CollisionCircle getBefore()
	{
		if (hasBefore)
			return new CollisionCircle(beforex, beforey, beforerad);
		else
			return new CollisionCircle(afterx, aftery, afterrad);
	}
	
	public CollisionCircle getAfter()
	{
		return new CollisionCircle(afterx, aftery, afterrad);
	}
	
	public void step(double dx, double dy, double xpos, double ypos,
	                 double rot, double scale, boolean flipped)
	{
		beforex = afterx + dx;
		beforey = aftery + dy;
		beforerad = afterrad;
		formFor(xpos, ypos, scale);
		hasBefore = true;
	}

}
