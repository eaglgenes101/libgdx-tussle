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
 * Created by eaglgenes101 on 5/17/17.
 */
public strictfp class StageCorner extends StageElement
{
	public static final double HALF_WHOLE = 180;
	private double localx, localy, localMinAngle, localMaxAngle;
	private double afterx, aftery, afterMinAngle, afterMaxAngle;
	private double beforex, beforey, beforeMinAngle, beforeMaxAngle;
	
	public StageCorner(double x, double y, double minAngle, double maxAngle,
	                   double globalX, double globalY, double globalRad, double globalScale,
	                   boolean globalFlipped)
	{
		localx = x;
		localy = y;
		localMinAngle = minAngle;
		localMaxAngle = maxAngle;
		formFor(globalX, globalY, globalRad, globalScale, globalFlipped);
	}
	
	protected void formFor(double globalX, double globalY, double globalRot,
	                       double globalScale, boolean globalFlipped)
	{
		double cos = FastMath.cos(FastMath.toRadians(globalRot));
		double sin = FastMath.sin(FastMath.toRadians(globalRot));
		double locx = localx*(globalFlipped?-globalScale:globalScale);
		double locy = localy*globalScale;
		afterx = locx * cos - locy * sin + globalX;
		aftery = locx * sin + locy * cos + globalY;
		afterMinAngle = globalFlipped?HALF_WHOLE-localMinAngle:localMinAngle + globalRot;
		afterMaxAngle = globalFlipped?HALF_WHOLE-localMaxAngle:localMaxAngle + globalRot;
	}
	
	public CollisionCorner getBefore()
	{
		if (hasBefore)
			return new CollisionCorner(beforex, beforey, beforeMinAngle, beforeMaxAngle);
		else
			return new CollisionCorner(afterx, aftery, afterMinAngle, afterMaxAngle);
	}
	
	public CollisionCorner getAfter()
	{
		return new CollisionCorner(afterx, aftery, afterMinAngle, afterMaxAngle);
	}
	
	public void step(double dx, double dy, double xpos, double ypos,
	                 double rot, double scale, boolean flipped)
	{
		beforex = afterx + dx;
		beforey = aftery + dy;
		beforeMinAngle = afterMinAngle;
		beforeMaxAngle = afterMaxAngle;
		formFor(xpos, ypos, rot, scale, flipped);
		hasBefore = true;
	}

}
