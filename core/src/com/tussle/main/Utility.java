package com.tussle.main;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

/**
 * Created by eaglgenes101 on 1/31/17.
 */
public class Utility
{
	public static Vector2 getXYfromDM(double direction, double magnitude)
	{
		return new Vector2((float)(magnitude*Math.cos(direction*Math.PI/180)),
				(float)(magnitude*Math.sin(direction*Math.PI/180)));
	}

	public static float addTowards(float value, float addend, float base)
	{
		if (addend == 0) return value;
		if (addend*(base-value-addend) > 0) return value+addend;
		else return addend>0?Math.max(base, value):Math.min(base, value);
	}

	public static float addAway(float value, float addend, float base)
	{
		if (addend*(base-value) <= 0) return value+addend;
		else return value;
	}

	public static float addFrom(float value, float amount, float base)
	{
		if (amount < 0 && -Math.abs(value-base) > amount) return base;
		else return amount*Math.copySign(1, value-base)+value;
	}

	public static Vector2 medianDifference(Polygon p1, Polygon p2)
	{
		Vector2 center1 = p1.getBoundingRectangle().getCenter(new Vector2());
		Vector2 center2 = p2.getBoundingRectangle().getCenter(new Vector2());
		return center2.sub(center1);
	}

	public static Vector2 projection(Vector2 v1, Vector2 v2)
	{
		return v2.cpy().scl(v1.dot(v2)/v2.len2());
	}

	public static Vector2 rejection(Vector2 v1, Vector2 v2)
	{
		return v1.cpy().sub(projection(v1, v2));
	}

	//Returns the time when an intersection is introduced
	public static float[] intervalCollide(float min1, float max1, float slope, float min2, float max2)
	{
		float[] returnVals = new float[2];
		if (slope == 0.0f)
		{
			if (min1 >= max2 || max1 <= min2)
			{
				returnVals[0] = Float.POSITIVE_INFINITY;
				returnVals[1] = Float.NEGATIVE_INFINITY;
				return returnVals;
			}
			else
			{
				returnVals[0] = Float.NEGATIVE_INFINITY;
				returnVals[1] = Float.POSITIVE_INFINITY;
				return returnVals;
			}
		}
		else if (slope > 0)
		{
			returnVals[0] = (min2-max1)/slope;
			returnVals[1] = (max2-min1)/slope;
			return returnVals;
		}
		else
		{
			returnVals[0] = (max2-min1)/slope;
			returnVals[1] = (min2-max1)/slope;
			return returnVals;
		}
	}

	/*
		Proudly pulled from the Libgdx source code,
		licensed under the Apache License
	 */
	public static float pathPolygonIntersects(Vector2 movement, float[] start, float[] surface)
	{
		int startLen = start.length;
		int surfaceLen = surface.length;
		float minTime = 0.0f;
		float maxTime = 1.0f;

		// Get polygon1 axes
		for (int i = 0; i < startLen; i += 2) {
			float x1 = start[i];
			float y1 = start[i + 1];
			float x2 = start[(i + 2) % startLen];
			float y2 = start[(i + 3) % startLen];

			float axisX = y1 - y2;
			float axisY = -(x1 - x2);

			final float length = (float)Math.sqrt(axisX * axisX + axisY * axisY);
			axisX /= length;
			axisY /= length;

			// -- Begin check for separation on this axis --//

			// Project polygon1 onto this axis
			float min1 = axisX * start[0] + axisY * start[1];
			float max1 = min1;
			for (int j = 0; j < startLen; j += 2) {
				float p = axisX * start[j] + axisY * start[j + 1];
				if (p < min1) min1 = p;
				else if (p > max1) max1 = p;
			}

			// Project polygon2 onto this axis
			float min2 = axisX * surface[0] + axisY * surface[1];
			float max2 = min2;
			for (int j = 0; j < surfaceLen; j += 2)
			{
				float p = axisX * surface[j] + axisY * surface[j + 1];
				if (p < min2) min2 = p;
				else if (p > max2) max2 = p;
			}
			float[] times = intervalCollide(min1, max1, movement.dot(axisX, axisY), min2, max2);
			minTime = Math.max(minTime, times[0]);
			maxTime = Math.min(maxTime, times[1]);

			// -- End check for separation on this axis --//
		}

		// Get polygon2 axes
		for (int i = 0; i < surfaceLen; i += 2) {
			float x1 = surface[i];
			float y1 = surface[i + 1];
			float x2 = surface[(i + 2) % surfaceLen];
			float y2 = surface[(i + 3) % surfaceLen];

			float axisX = y1 - y2;
			float axisY = -(x1 - x2);

			final float length = (float)Math.sqrt(axisX * axisX + axisY * axisY);
			axisX /= length;
			axisY /= length;

			// -- Begin check for separation on this axis --//

			// Project polygon1 onto this axis
			float min1 = axisX * start[0] + axisY * start[1];
			float max1 = min1;
			for (int j = 0; j < startLen; j += 2) {
				float p = axisX * start[j] + axisY * start[j + 1];
				if (p < min1) min1 = p;
				else if (p > max1) max1 = p;
			}

			// Project polygon2 onto this axis
			float min2 = axisX * surface[0] + axisY * surface[1];
			float max2 = min2;
			for (int j = 0; j < surfaceLen; j += 2) {
				float p = axisX * surface[j] + axisY * surface[j + 1];
				if (p < min2) min2 = p;
				else if (p > max2) max2 = p;
			}
			float[] times = intervalCollide(min1, max1, movement.dot(axisX, axisY), min2, max2);
			minTime = Math.max(minTime, times[0]);
			maxTime = Math.min(maxTime, times[1]);
			// -- End check for separation on this axis --//
		}

		if (minTime >= maxTime)
			return Float.NaN;
		else
			return minTime;
	}
}






