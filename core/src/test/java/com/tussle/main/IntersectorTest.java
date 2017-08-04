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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tussle.main.Intersector.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by eaglgenes101 on 7/6/17.
 */
public class IntersectorTest
{
	public static final double FP_TOLERANCE = .0001;

	double ap = Double.NaN;
	double aq = Double.NaN;
	double ax = -1;
	double ay = 3;

	double bp = -1;
	double bq = 0;
	double bx = Double.NaN;
	double by = Double.NaN;

	@Test
	@DisplayName("Squared distance between a line segment and a point")
	void d2SegmentPointTest()
	{
		assertEquals(d2SegmentPoint(-1, 0, 1, 0, 0, 0), 0, FP_TOLERANCE);
		assertEquals(d2SegmentPoint(-1, 1, 1, -1, 1, 1), 2, FP_TOLERANCE);
		assertEquals(d2SegmentPoint(-1, -1, 1, 1, 2, 0), 2, FP_TOLERANCE);
	}

	@Test
	@DisplayName("Portion of a segment's length which a point is closest to")
	void partSegmentPointTest()
	{
		assertEquals(partSegmentPoint(-1, 0, 1, 0, 0, 0), .5, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, 1, 1, -1, 1, 1), .5, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, -1, 1, 1, 2, 0), 1, FP_TOLERANCE);
	}

	@Test
	@DisplayName("The position of a point along a line segment")
	void isPerpSegPointTest()
	{
		assertEquals(isPerpSegPoint(-1, 0, 1, 0, 0, 0), .5, FP_TOLERANCE);
		assertEquals(isPerpSegPoint(-1, 1, 1, -1, 1, 1), .5, FP_TOLERANCE);
		assertEquals(isPerpSegPoint(-1, -1, 1, 1, 3, 0), Double.NaN);
	}

	@Test
	@DisplayName("The displacement between a point and a line segment")
	void dispSegmentPointTest()
	{
		ProjectionVector p1 = dispSegmentPoint(-1, -1, 1, 1, 0, 0);
		ProjectionVector p2 = dispSegmentPoint(-1, -1, 1, 1, -1, 1);
		ProjectionVector p3 = dispSegmentPoint(-1, -1, 1, 1, 0, 3);
		assertEquals(p1.xnorm, .7071, FP_TOLERANCE);
		assertEquals(p1.ynorm, -.7071, FP_TOLERANCE);
		assertEquals(p1.magnitude, 0, FP_TOLERANCE);
		assertEquals(p2.xnorm, .7071, FP_TOLERANCE);
		assertEquals(p2.ynorm, -.7071, FP_TOLERANCE);
		assertEquals(p2.magnitude, 1.4142, FP_TOLERANCE);
		assertEquals(p3.xnorm, .4472, FP_TOLERANCE);
		assertEquals(p3.ynorm, -.8944, FP_TOLERANCE);
		assertEquals(p3.magnitude, 2.2361, FP_TOLERANCE);
	}

	@Test
	void segmentsIntersectTest()
	{
		assertTrue(segmentsIntersect(-1, -1,1, 1, 1, -1, -1, 1));
		assertTrue(segmentsIntersect(-1, 1, 1, -1, 1, 1, -1, -1));
		assertTrue(segmentsIntersect(-1, -1, 1, 1, -1, -1, -1, 1));
		assertFalse(segmentsIntersect(-1, -1, -1, 1, 0, 0, 2, 0));
	}

	@Test
	void partIntersectingSegmentsTest()
	{
		assertEquals(partIntersectingSegments(-1, -1, 1, 1, 0, 1, 1, 0),
				.75, FP_TOLERANCE);
	}


	@Test
	void partSegmentsTest()
	{
		assertEquals(partSegments(-1, -1, 1, 1, 0, 1, 1, 0),
				.75, FP_TOLERANCE);
		assertEquals(partSegments(-1, -1, 1, 1, -2, -1, -1, -2),
				0, FP_TOLERANCE);
		assertEquals(partSegments(-1, -1, 1, 1, 0, 1, -1, 2),
				.75, FP_TOLERANCE);
	}

	@Test
	void displacementSegmentsTest()
	{
		ProjectionVector p1 = displacementSegments(-1, -1, 1, 1, -1, 0, 0, 1);
		ProjectionVector p2 = displacementSegments(-1, -1, 1, 1, 2, 2, 3, 3);
		ProjectionVector p3 = displacementSegments(-1, -1, 1, 1, -2, -1, -1, -2);
		assertEquals(p1.xnorm, .7071, FP_TOLERANCE);
		assertEquals(p1.ynorm, -.7071, FP_TOLERANCE);
		assertEquals(p1.magnitude, .7071, FP_TOLERANCE);
		assertEquals(p2.xnorm, -.7071, FP_TOLERANCE);
		assertEquals(p2.ynorm, -.7071, FP_TOLERANCE);
		assertEquals(p2.magnitude, 1.4142, FP_TOLERANCE);
		assertEquals(p3.xnorm, .7071, FP_TOLERANCE);
		assertEquals(p3.ynorm, .7071, FP_TOLERANCE);
		assertEquals(p3.magnitude, .7071, FP_TOLERANCE);
	}

	@Test
	void pointLineSideTest()
	{
		assertEquals(pointLineSide(-1, -1, 1, 1, -1, 1),
				1);
		assertEquals(pointLineSide(-1, -1, 1, 1, 1, -1),
				-1);
		assertEquals(pointLineSide(-1, -1, 1, 1, -1, -1),
				0);
	}

	@Test
	void timeMovingSegmentCircleTest()
	{
		assertEquals(timeMovingSegmentCircle(-1, -1, 1, -1, 0, 2, .5),
				.25, FP_TOLERANCE);
		assertEquals(timeMovingSegmentCircle(-1, 0, 1, 0, 0, 1, .5),
				0, FP_TOLERANCE);
		assertEquals(timeMovingSegmentCircle(-3, 0, -1, 0, 1, 0, .5),
				.5, FP_TOLERANCE);
	}

	@Test
	void partCircleSegmentTest()
	{
		assertEquals(partCircleSegment(-2, 0, 2, 0, 0, 0, 1), .25, FP_TOLERANCE);
		assertEquals(partCircleSegment(0, 0, 2, 0, 0, 0, 1), .5, FP_TOLERANCE);
	}

	@Test
	void timeSegmentCircleTest1()
	{
		assertEquals(timeSegmentCircle(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, -1, 1, 1), .5, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, .5, 1, 1), .5, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, 0, 1, 1), .5, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, 1, 1, .5), Double.NaN);
		assertEquals(timeSegmentCircle(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, -1, 2, .5), Double.NaN);
	}

	@Test
	void timeSegmentCircleTest2()
	{
		assertEquals(timeSegmentCircle(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, -1, 1, 1), 0, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 0, 1, 1), .5, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 1, 1, 1), .5, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 2, .5, 1), .75, FP_TOLERANCE);
		assertEquals(timeSegmentCircle(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 0, 2, 1), Double.NaN);
	}

	@Test
	void timeMovingSegmentsTest1()
	{
		assertEquals(timeMovingSegments(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, -2, 0, 2, 0), .5, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, -2, -1, 0, 1), .5, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, 0, .5, 1, -.5), .5, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, 0, 0, 0, -2), 0, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, -1, 1, -1, -1, 1, 0, 1,
				ap, aq, ax, ay, 0, 0, 0, 2), .5, FP_TOLERANCE);
	}

	@Test
	void timeMovingSegmentsTest2()
	{
		assertEquals(timeMovingSegments(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, -2, 0, 2, 0), 0, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 0, 0, 2, 0), .5, FP_TOLERANCE);
		assertEquals(timeMovingSegments(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, -1, 1, 1, 2), Double.NaN);
		assertEquals(timeMovingSegments(-1, 0, 1, -1, -1, 0, 1, 1,
				bp, bq, bx, by, 0, 1, 2, -1), .5, FP_TOLERANCE);
	}
}