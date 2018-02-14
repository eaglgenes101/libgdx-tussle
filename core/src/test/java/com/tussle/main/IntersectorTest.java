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

	@Test
	@DisplayName("Squared distance between a line segment and a point")
	void d2SegmentPointTest()
	{
		assertEquals(d2SegmentPoint(-1, 0, 1, 0, 0, 0), 0, FP_TOLERANCE);
		assertEquals(d2SegmentPoint(-1, 1, 1, -1, 1, 1), 2, FP_TOLERANCE);
		assertEquals(d2SegmentPoint(-1, -1, 1, 1, 2, 0), 2, FP_TOLERANCE);
		assertEquals(d2SegmentPoint(0, 0, 0, 0, 1, 1), 2, FP_TOLERANCE);
	}

	@Test
	@DisplayName("Portion of a segment's length which a point is closest to")
	void partSegmentPointTest()
	{
		assertEquals(partSegmentPoint(-1, 0, 1, 0, 0, 0), .5, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, 1, 1, -1, 1, 1), .5, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, -1, 1, 1, 2, 0), 1, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, -1, 1, 1, -2, -2), 0, FP_TOLERANCE);
		assertEquals(partSegmentPoint(-1, -1, 1, 1, 2, 2), 1, FP_TOLERANCE);
		assertEquals(partSegmentPoint(0, 0, 0, 0, 1, 1), .5, FP_TOLERANCE);
	}

	@Test
	@DisplayName("The displacementBy between a point and a line segment")
	void dispSegmentPointTest()
	{
		ProjectionVector p1 = dispSegmentPoint(-1, -1, 1, 1, 0, 0);
		ProjectionVector p2 = dispSegmentPoint(-1, -1, 1, 1, -1, 1);
		ProjectionVector p3 = dispSegmentPoint(-1, -1, 1, 1, 0, 3);
		assertEquals(p1.xNorm(), .7071, FP_TOLERANCE);
		assertEquals(p1.yNorm(), -.7071, FP_TOLERANCE);
		assertEquals(p1.magnitude(), 0, FP_TOLERANCE);
		assertEquals(p2.xNorm(), .7071, FP_TOLERANCE);
		assertEquals(p2.yNorm(), -.7071, FP_TOLERANCE);
		assertEquals(p2.magnitude(), 1.4142, FP_TOLERANCE);
		assertEquals(p3.xNorm(), .4472, FP_TOLERANCE);
		assertEquals(p3.yNorm(), -.8944, FP_TOLERANCE);
		assertEquals(p3.magnitude(), 2.2361, FP_TOLERANCE);
	}

	@Test
	@DisplayName("Whether two different segments intersect")
	void segmentsIntersectTest()
	{
		assertTrue(segmentsIntersect(-1, -1,1, 1, 1, -1, -1, 1));
		assertTrue(segmentsIntersect(-1, 1, 1, -1, 1, 1, -1, -1));
		assertTrue(segmentsIntersect(-1, -1, 1, 1, -1, -1, -1, 1));
		assertFalse(segmentsIntersect(-1, -1, -1, 1, 0, 0, 2, 0));
	}

	@Test
	@DisplayName("The point along a segment where it intersects with another segment")
	void partIntersectingSegmentsTest()
	{
		assertEquals(partIntersectingSegments(-1, -1, 1, 1, 0, 1, 1, 0),
				.75, FP_TOLERANCE);
	}


	@Test
	@DisplayName("The point along a segment closest to another segment")
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
	@DisplayName("The displacementBy needed to have two segments just touching")
	void displacementSegmentsTest()
	{
		ProjectionVector p1 = displacementSegments(-1, -1, 1, 1, -1, 0, 0, 1);
		ProjectionVector p2 = displacementSegments(-1, -1, 1, 1, 2, 2, 3, 3);
		ProjectionVector p3 = displacementSegments(-1, -1, 1, 1, -2, -1, -1, -2);
		assertEquals(p1.xNorm(), .7071, FP_TOLERANCE);
		assertEquals(p1.yNorm(), -.7071, FP_TOLERANCE);
		assertEquals(p1.magnitude(), .7071, FP_TOLERANCE);
		assertEquals(p2.xNorm(), -.7071, FP_TOLERANCE);
		assertEquals(p2.yNorm(), -.7071, FP_TOLERANCE);
		assertEquals(p2.magnitude(), 1.4142, FP_TOLERANCE);
		assertEquals(p3.xNorm(), .7071, FP_TOLERANCE);
		assertEquals(p3.yNorm(), .7071, FP_TOLERANCE);
		assertEquals(p3.magnitude(), .7071, FP_TOLERANCE);
	}

}
