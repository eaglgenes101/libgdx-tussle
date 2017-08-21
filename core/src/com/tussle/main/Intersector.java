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

import static java.lang.Double.isFinite;

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

	public static double isPerpSegPoint(double startX, double startY, double endX,
										double endY, double pointX, double pointY)
	{
		double length2 = (endX-startX)*(endX-startX)+(endY-startY)*(endY-startY);
		if (length2 == 0)
			return HALF;
		else
		{
			double t = ((pointX - startX) * (endX - startX) +
					(pointY - startY) * (endY - startY)) / length2;
			if (t < 0 || t > 1)
				return Double.NaN;
			else
				return t;
		}
	}

	public static ProjectionVector dispSegmentPoint(double startX, double startY, double endX,
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
		if (x-pointX == 0 && y-pointY == 0)
		{
			double xNorm = endY - startY;
			double yNorm = startX - endX;
			double len = StrictMath.hypot(xNorm, yNorm);
			return new ProjectionVector(xNorm/len, yNorm/len, 0);
		}
		double dist = StrictMath.hypot(x-pointX, y-pointY);
		double xVec = (x-pointX)/dist;
		double yVec = (y-pointY)/dist;
		return new ProjectionVector(xVec, yVec, dist);
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
		if ( (a<b) && ((a<c && a<=d) || (a==c && a==d)))
		{
			ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x1, y1);
			v.xnorm *= -1;
			v.ynorm *= -1;
			return v;
		}
		else if ( (b<c) && ((b<d && b<=a) || (b==d && b==a)))
		{
			ProjectionVector v = dispSegmentPoint(x3, y3, x4, y4, x2, y2);
			v.xnorm *= -1;
			v.ynorm *= -1;
			return v;
		}
		else if ( (c<d) && ((c<a && c<=b) || (c==a && c==b)))
			return dispSegmentPoint(x1, y1, x2, y2, x3, y3);
		else
			return dispSegmentPoint(x1, y1, x2, y2, x4, y4);
	}


	public static double pointLineSide (double linePoint1X, double linePoint1Y, double linePoint2X,
									 double linePoint2Y, double pointX, double pointY)
	{
		return StrictMath.signum((linePoint2X - linePoint1X) * (pointY - linePoint1Y)
				- (linePoint2Y - linePoint1Y) * (pointX - linePoint1X));
	}

	public static double timeMovingSegmentCircle(double sx, double sy, double ex, double ey,
												 double dx, double dy, double rad)
	{
		// Return 0 if already intersecting at start
		if (d2SegmentPoint(sx, sy, ex, ey, 0, 0) <= rad*rad)
			return 0;

		// Determine time when segment is at radius
		double segLen = StrictMath.hypot(sx-ex, sy-ey);
		double segPerpX = (ey-sy)/segLen;
		double segPerpY = (sx-ex)/segLen;
		double project = segPerpX*sx + segPerpY*sy;
		double changeProject = segPerpX*dx + segPerpY*dy;
		//If it makes sense to divide over change projection
		if (changeProject != 0)
		{
			double intersectTime0 = (-project - rad) / changeProject;
			double intersectTime1 = (-project + rad) / changeProject;
			if (intersectTime0 < 0 && intersectTime1 < 0)
				return Double.NaN;
			else if (intersectTime0 > 1 && intersectTime1 > 1)
				return Double.NaN;
			double segmentTime = StrictMath.min(intersectTime0, intersectTime1);
			// Check if parallel axis lines up
			double segParX = (ex - sx) / segLen;
			double segParY = (ey - sy) / segLen;
			double segU0 = segParX * (sx + segmentTime * dx) + segParY * (sy + segmentTime * dy);
			double segU1 = segParX * (ex + segmentTime * dx) + segParY * (ey + segmentTime * dy);
			if ((segU0 >= 0 && segU1 <= 0) || (segU0 <= 0 && segU1 >= 0))
				return segmentTime; //Success!
		}
		//Nope. Find time of collision with the bounding movement segments
		double start0 = Utility.lowestPositiveRoot(dx*dx+dy*dy,2*sx*dx+2*sy*dy, sx*sx+sy*sy-rad*rad);
		double start1 = Utility.lowestPositiveRoot(dx*dx+dy*dy,2*ex*dx+2*ey*dy, ex*ex+ey*ey-rad*rad);
		boolean rootFound0 = isFinite(start0) && start0 <= 1;
		boolean rootFound1 = isFinite(start1) && start1 <= 1;
		if (!rootFound0 && !rootFound1)
			return Double.NaN;
		else if (!rootFound0)
			return start1;
		else if (!rootFound1)
			return start0;
		else
			return StrictMath.min(start0, start1);
	}

	public static double partCircleSegment(double sx, double sy, double ex, double ey,
										   double px, double py, double rad)
	{
		double dx = ex-sx;
		double dy = ey-sy;
		double xdist = sx-px;
		double ydist = sy-py;
		double a = dx*dx+dy*dy;
		double b = 2*(dx*xdist+dy*ydist);
		double c = xdist*xdist+ydist*ydist-rad*rad;
		double root = Utility.lowestPositiveRoot(a, b, c);
		if (!isFinite(root) || root >= 1)
			return Double.NaN;
		return root;
	}

	public static double timeMovingSegmentCircle(double sx, double sy, double ex, double ey,
												 double px, double py, double svx, double svy,
												 double pvx, double pvy, double rad)
	{
		return timeMovingSegmentCircle(sx-px, sy-py, ex-px, ey-py, svx-pvx, svy-pvy, rad);
	}


	public static double timeSegmentCircle(double sx0, double sy0, double ex0, double ey0,
										   double sx1, double sy1, double ex1, double ey1,
										   double fx, double fy, double vx, double vy,
										   double x, double y, double rad)
	{
		if (d2SegmentPoint(sx0, sy0, ex0, ey0, x, y) <= rad * rad)
			return 0;
		double x0, y0, x1, y1;
		double t0, t1, t2, t3;
		if (isFinite(fx) && isFinite(fy)) //There is a focus point
		{
			double unitX = x - fx;
			double unitY = y - fy;
			double len2 = unitX * unitX + unitY * unitY;
			double projPart = rad * rad;
			double rejPart = rad * StrictMath.sqrt(len2 - rad*rad);
			x0 = x + (-unitX * projPart - unitY * rejPart) / len2;
			y0 = y + (-unitY * projPart + unitX * rejPart) / len2;
			x1 = x + (-unitX * projPart + unitY * rejPart) / len2;
			y1 = y + (-unitY * projPart - unitX * rejPart) / len2;
		}
		else //All relevant lines are parallel
		{
			boolean flipSecond = (ex1-sx1 > 0) ^ (ex0-sx0 > 0);
			double dx = ex0 - sx0 + (flipSecond ? sx1 - ex1 : ex1 - sx1);
			double dy = ey0 - sy0 + (flipSecond ? sy1 - ey1 : ey1 - sy1);
			double len = StrictMath.hypot(dx, dy);
			x0 = x - dy * rad / len;
			y0 = y + dx * rad / len;
			x1 = x + dy * rad / len;
			y1 = y - dx * rad / len;
		}
		//Now check possible collisions
		t0 = partCircleSegment(sx0, sy0, sx1, sy1, x, y, rad);
		t1 = partCircleSegment(ex0, ey0, ex1, ey1, x, y, rad);
		//Find where the line between the segment homographs and the line
		//between the focus points and the vanishing point intersect
		double u0, u1;
		if (isFinite(vx) && isFinite(vy))
		{
			//Bog-standard linear equation solving.
			u0 = Utility.partSegmentsIntersecting(sx0, sy0, ex0, ey0, vx, vy, x0, y0);
			u1 = Utility.partSegmentsIntersecting(sx0, sy0, ex0, ey0, vx, vy, x1, y1);
		}
		else
		{
			//The relevant lines are parallel.
			boolean flipSecond = (ex1-ex0 > 0) ^ (sx1-sx0 > 0);
			double dx = sx1-sx0 + (flipSecond?ex0-ex1:ex1-ex0);
			double dy = sy1-sy0 + (flipSecond?ey0-ey1:ey1-ey0);
			double segdot = -dy * (ex0 - sx0) + dx * (ey0 - sy0);
			double pt0dot = -dy * (x0 - sx0) + dx * (y0 - sy0);
			double pt1dot = -dy * (x1 - sx0) + dx * (y1 - sy0);
			u0 = pt0dot / segdot;
			u1 = pt1dot / segdot;
		}
		if (isFinite(u0) && u0 >= 0 && u0 <= 1)
		{
			double sx = sx0 + u0*(ex0-sx0);
			double sy = sy0 + u0*(ey0-sy0);
			double ex, ey;
			if (isFinite(vx) && isFinite(vy))
			{
				double[] pt = Utility.segmentsIntersectionPoint(vx, vy, sx, sy, sx1, sy1, ex1, ey1);
				ex = pt[0];
				ey = pt[1];
			}
			else
			{
				ex = sx1 + u0*(ex1-sx1);
				ey = sy1 + u0*(ey1-sy1);
			}
			t2 = Utility.pointSegmentPosition(sx, sy, ex, ey, x0, y0);
		}
		else
			t2 = Double.NaN;
		if (isFinite(u1) && u1 >= 0 && u1 <= 1)
		{
			double sx = sx0 + u1*(ex0-sx0);
			double sy = sy0 + u1*(ey0-sy0);
			double ex, ey;
			if (isFinite(vx) && isFinite(vy))
			{
				double[] pt = Utility.segmentsIntersectionPoint(vx, vy, sx, sy, sx1, sy1, ex1, ey1);
				ex = pt[0];
				ey = pt[1];
			}
			else
			{
				ex = sx1 + u1*(ex1-sx1);
				ey = sy1 + u1*(ey1-sy1);
			}
			t3 = Utility.pointSegmentPosition(sx, sy, ex, ey, x1, y1);
		}
		else
			t3 = Double.NaN;
		double t = Double.POSITIVE_INFINITY;
		if (Double.isFinite(t0) && t0 >= 0 && t0 <= t) t = t0;
		if (Double.isFinite(t1) && t1 >= 0 && t1 <= t) t = t1;
		if (Double.isFinite(t2) && t2 >= 0 && t2 <= t) t = t2;
		if (Double.isFinite(t3) && t3 >= 0 && t3 <= t) t = t3;
		if (t > 1)
			return Double.NaN;
		return t;
	}

	public static double timeMovingSegments(double sx0, double sy0, double ex0, double ey0,
											double sx1, double sy1, double ex1, double ey1,
											double fx, double fy, double vx, double vy,
											double sx, double sy, double ex, double ey)
	{
		//Find where the line between the segment homographs and the line
		//between the focus points and the vanishing point intersect

		//If it's already intersecting, return 0 right here
		if (Intersector.segmentsIntersect(sx0, sy0, ex0, ey0, sx, sy, ex, ey))
			return 0;
		double su, eu;
		if (isFinite(vx) && isFinite(vy))
		{
			//Bog-standard linear equation solving.
			su = Utility.partSegmentsIntersecting(sx0, sy0, ex0, ey0, vx, vy, sx, sy);
			eu = Utility.partSegmentsIntersecting(sx0, sy0, ex0, ey0, vx, vy, ex, ey);
		}
		else
		{
			//The relevant lines are parallel.
			boolean flipSecond = (ex1 - ex0 > 0) ^ (sx1 - sx0 > 0);
			double dx = sx1 - sx0 + (flipSecond ? ex0 - ex1 : ex1 - ex0);
			double dy = sy1 - sy0 + (flipSecond ? ey0 - ey1 : ey1 - ey0);
			double segdot = -dy * (ex0 - sx0) + dx * (ey0 - sy0);
			double pt0dot = -dy * (sx - sx0) + dx * (sy - sy0);
			double pt1dot = -dy * (ex - sx0) + dx * (ey - sy0);
			su = pt0dot / segdot;
			eu = pt1dot / segdot;
		}
		double t0, t1, t2, t3;
		if (segmentsIntersect(sx0, sy0, sx1, sy1, sx, sy, ex, ey))
			t0 = partIntersectingSegments(sx0, sy0, sx1, sy1, sx, sy, ex, ey);
		else t0 = Double.NaN;
		if (segmentsIntersect(ex0, ey0, ex1, ey1, sx, sy, ex, ey))
			t1 = partIntersectingSegments(ex0, ey0, ex1, ey1, sx, sy, ex, ey);
		else t1 = Double.NaN;
		if (isFinite(fx) && isFinite(fy))
		{
			//Bog-standard linear equation solving.
			t2 = Utility.partSegmentsIntersecting(sx0, sy0, sx1, sy1, fx, fy, sx, sy);
			t3 = Utility.partSegmentsIntersecting(sx0, sy0, sx1, sy1, fx, fy, ex, ey);
		}
		else
		{
			//The relevant lines are parallel.
			boolean flipSecond = (ex1 - sx1 > 0) ^ (ex0 - sx0 > 0);
			double dx = ex0 - sx0 + (flipSecond ? sx1 - ex1 : ex1 - sx1);
			double dy = ey0 - sy0 + (flipSecond ? sy1 - ey1 : ey1 - sy1);
			double segdot = -dy * (sx1 - sx0) + dx * (sy1 - sy0);
			double pt0dot = -dy * (sx - sx0) + dx * (sy - sy0);
			double pt1dot = -dy * (ex - sx0) + dx * (ey - sy0);
			t2 = pt0dot / segdot;
			t3 = pt1dot / segdot;
		}
		double min = Double.POSITIVE_INFINITY;
		if (isFinite(su) && isFinite(eu))
		{
			if ((su >= 0 && 0 >= eu) || (su <= 0 && 0 <= eu))
				if (isFinite(t0) && t0 >= 0 && t0 <= 1)
					min = StrictMath.min(min, t0);
			if ((su >= 1 && 1 >= eu) || (su <= 1 && 1 <= eu))
				if (isFinite(t1) && t1 >= 0 && t1 <= 1)
					min = StrictMath.min(min, t1);
		}
		if (isFinite(su) && su >= 0 && su <= 1)
			if (isFinite(t2) && t2 >= 0 && t2 <= 1)
				min = StrictMath.min(min, t2);
		if (isFinite(eu) && eu >= 0 && eu <= 1)
			if (isFinite(t3) && t3 >= 0 && t3 <= 1)
				min = StrictMath.min(min, t3);
		return Double.isFinite(min) ? min : Double.NaN;
	}

}
