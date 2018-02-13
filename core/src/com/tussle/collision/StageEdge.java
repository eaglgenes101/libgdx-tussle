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

/**
 * Created by eaglgenes101 on 4/18/17.
 */
public strictfp class StageEdge extends StageElement
{
	private double localx0, localy0, localx1, localy1;
	private double afterx0, aftery0, afterx1, aftery1;
	private double beforex0, beforey0, beforex1, beforey1;
	
	public StageEdge(double startx, double starty, double endx, double endy,
	                 double globalX, double globalY, double globalRot, double globalScale,
	                 boolean globalFlipped)
	{
		localx0 = startx; localy0 = starty;
		localx1 = endx; localy1 = endy;
		formFor(globalX, globalY, globalRot, globalScale, globalFlipped);
	}
	
	protected void formFor(double globalX, double globalY, double globalRot, double globalScale,
	                       boolean globalFlipped)
	{
		double sx = localx0 * (globalFlipped ? -globalScale : globalScale);
		double sy = localy0 * globalScale;
		double ex = localx1 * (globalFlipped ? -globalScale : globalScale);
		double ey = localy1 * globalScale;
		double cos = FastMath.cos(FastMath.toRadians(globalRot));
		double sin = FastMath.sin(FastMath.toRadians(globalRot));
		afterx0 = sx*cos - sy*sin + globalX;
		aftery0 = sx*sin + sy*cos + globalY;
		afterx1 = ex*cos - ey*sin + globalX;
		aftery1 = ex*sin + ey*cos + globalY;
	}
	
	public CollisionEdge getBefore()
	{
		if (hasBefore)
			return new CollisionEdge(beforex0, beforey0, beforex1, beforey1);
		else
			return new CollisionEdge(afterx0, aftery0, afterx1, aftery1);
	}
	
	public CollisionEdge getAfter()
	{
		return new CollisionEdge(afterx0, aftery0, afterx1, aftery1);
	}
	
	public void step(double dx, double dy, double xpos, double ypos,
	                 double rot, double scale, boolean flipped)
	{
		beforex0 = afterx0 + dx; beforey0 = aftery0 + dy;
		beforex1 = afterx1 + dx; beforey1 = aftery1 + dy;
		formFor(xpos, ypos, rot, scale, flipped);
		hasBefore = true;
	}
}
