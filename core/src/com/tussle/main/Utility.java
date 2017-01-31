package com.tussle.main;

import com.badlogic.gdx.math.Vector2;

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

	public static float bounded(float value, float min, float max)
	{
		return Math.max(Math.min(value, max), min);
	}

}
