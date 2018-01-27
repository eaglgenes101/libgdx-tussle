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

package com.tussle.main;

import com.tussle.collision.ProjectionVector;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by eaglgenes101 on 4/15/17.
 * Based on the Intersector class in libgdx, whose license is below:
 ******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
public strictfp class Intersector
{
	public static final double HALF = 0.5;

	public static double d2SegmentPoint(double startX, double startY, double endX,
										double endY, double pointX, double pointY)
	{
		double x;
		double y;
		double length2 = (endX-startX)*(endX-startX)+(endY-startY)*(endY-startY);
		if (length2 == 0)
		{
			x = startX;
			y = startY;
		}
		else
		{
			double t = ((pointX - startX) * (endX - startX) +
					(pointY - startY) * (endY - startY)) / length2;
			if (t < 0)
			{
				x = startX;
				y = startY;
			}
			else if (t > 1)
			{
				x = endX;
				y = endY;
			}
			else
			{
				x = startX + t * (endX - startX);
				y = startY + t * (endY - startY);
			}
		}
		return (x-pointX)*(x-pointX) + (y-pointY)*(y-pointY);
	}

	public static double partSegmentPoint(double startX, double startY, double endX,
										  double endY, double pointX, double pointY)
	{
		double length2 = (endX-startX)*(endX-startX)+(endY-startY)*(endY-startY);
		if (length2 == 0)
			return HALF;
		else
		{
			double t = ((pointX - startX) * (endX - startX) +
					(pointY - startY) * (endY - startY)) / length2;
			if (t < 0)
				return 0;
			else if (t > 1)
				return 1;
			else
				return t;
		}
	}

	public static ProjectionVector dispSegmentPoint(double startX, double startY, double endX,
													double endY, double pointX, double pointY)
	{
		double x;
		double y;
		if (endX-startX == 0 && endY-startY == 0)
		{
			x = startX;
			y = startY;
		}
		else
		{
			double length2 = (endX-startX)*(endX-startX)+(endY-startY)*(endY-startY);
			double tprime = (pointX - startX) * (endX - startX) +
			                (pointY - startY) * (endY - startY);
			if (tprime < 0)
			{
				x = startX;
				y = startY;
			}
			else if (tprime > length2)
			{
				x = endX;
				y = endY;
			}
			else
			{
				x = startX + tprime * (endX - startX) / length2;
				y = startY + tprime * (endY - startY) / length2;
			}
		}
		if (x-pointX == 0 && y-pointY == 0)
		{
			double len = FastMath.hypot(endY - startY, startX - endX);
			return new ProjectionVector((endY - startY)/len,
			                            (startX - endX)/len, 0);
		}
		double dist = FastMath.hypot(x-pointX, y-pointY);
		return new ProjectionVector((x-pointX)/dist, (y-pointY)/dist, dist);
	}


	public static boolean segmentsIntersect(double x1, double y1, double x2, double y2,
											double x3, double y3, double x4, double y4)
	{
		double d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (d == 0) return false;

		double yd = y1 - y3;
		double xd = x1 - x3;
		double ua = ((x4 - x3) * yd - (y4 - y3) * xd)/d;
		if (ua < 0 || ua > 1) return false;

		double ub = ((x2 - x1) * yd - (y2 - y1) * xd)/d;
		return !(ub < 0) && !(ub > 1);
	}

	public static double partIntersectingSegments(double x1, double y1, double x2, double y2,
												  double x3, double y3, double x4, double y4)
	{
		double d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (d == 0) return HALF;

		double yd = y1 - y3;
		double xd = x1 - x3;
		double ua = ((x4 - x3) * yd - (y4 - y3) * xd)/d;
		if (ua < 0) return 0;
		else if (ua > 1) return 1;
		else return ua;
	}

	public static double partSegments(double x1, double y1, double x2, double y2,
									  double x3, double y3, double x4, double y4)
	{
		if (segmentsIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
			return partIntersectingSegments(x1, y1, x2, y2, x3, y3, x4, y4);
		double a = d2SegmentPoint(x3, y3, x4, y4, x1, y1);
		double b = d2SegmentPoint(x3, y3, x4, y4, x2, y2);
		double c = d2SegmentPoint(x1, y1, x2, y2, x3, y3);
		double d = d2SegmentPoint(x1, y1, x2, y2, x4, y4);
		if ( (a<b) && ((a<c && a<=d) || (a==c && a==d)))
			return 0;
		else if ( (b<c) && ((b<d && b<=a) || (b==d && b==a)))
			return 1;
		else if ( (c<d) && ((c<a && c<=b) || (c==a && c==b)))
			return partSegmentPoint(x1, y1, x2, y2, x3, y3);
		else
			return partSegmentPoint(x1, y1, x2, y2, x4, y4);
	}

	public static ProjectionVector displacementSegments(double x1, double y1, double x2, double y2,
														double x3, double y3, double x4, double y4)
	{
		double a = d2SegmentPoint(x3, y3, x4, y4, x1, y1);
		double b = d2SegmentPoint(x3, y3, x4, y4, x2, y2);
		double c = d2SegmentPoint(x1, y1, x2, y2, x3, y3);
		double d = d2SegmentPoint(x1, y1, x2, y2, x4, y4);
		if (segmentsIntersect(x1, y1, x2, y2, x3, y3, x4, y4))
		{
			//Displacement points inward
			if ((a < b) && ((a < c && a <= d) || (a == c && a == d)))
			{
				ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x1, y1);
				v.xnorm *= -1;
				v.ynorm *= -1;
				v.magnitude *= -1;
				return v;
			}
			else if ((b < c) && ((b < d && b <= a) || (b == d && b == a)))
			{
				ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x2, y2);
				v.xnorm *= -1;
				v.ynorm *= -1;
				v.magnitude *= -1;
				return v;
			}
			else if ((c < d) && ((c < a && c <= b) || (c == a && c == b)))
			{
				ProjectionVector v = dispSegmentPoint(x1, y1, x2, y2, x3, y3);
				v.magnitude *= -1;
				return v;
			}
			else
			{
				ProjectionVector v = dispSegmentPoint(x1, y1, x2, y2, x4, y4);
				v.magnitude *= -1;
				return v;
			}
		}
		else
		{
			//Displacement points outward
			if ((a < b) && ((a < c && a <= d) || (a == c && a == d)))
			{
				ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x1, y1);
				v.xnorm *= -1;
				v.ynorm *= -1;
				return v;
			}
			else if ((b < c) && ((b < d && b <= a) || (b == d && b == a)))
			{
				ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x2, y2);
				v.xnorm *= -1;
				v.ynorm *= -1;
				return v;
			}
			else if ((c < d) && ((c < a && c <= b) || (c == a && c == b)))
				return dispSegmentPoint(x1, y1, x2, y2, x3, y3);
			else
				return dispSegmentPoint(x1, y1, x2, y2, x4, y4);
		}
	}

}
