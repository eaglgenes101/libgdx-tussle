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

import com.badlogic.gdx.math.*;

/**
 * Created by eaglgenes101 on 3/8/17.
 */
public class Stadium implements Shape2D
{
	private float localStartx, localStarty, localEndx, localEndy;
	private float worldStartx, worldStarty, worldEndx, worldEndy;

	private float radius;

	private float x, y;
	private float originX, originY;
	private float rotation;
	private float scale = 1;
	private boolean flipped = false;
	private boolean dirty = true;
	private Rectangle bounds;

	public Stadium(float startx, float starty, float endx, float endy, float radius)
	{
		localStartx = startx;
		localStarty = starty;
		localEndx = endx;
		localEndy = endy;
		this.radius = radius;
	}

	public Vector2 getStart()
	{
		return new Vector2(localStartx, localStarty);
	}

	public Vector2 getEnd()
	{
		return new Vector2(localEndx, localEndy);
	}

	public float getRadius()
	{
		return radius;
	}

	public Vector2 getTransformedStart()
	{
		if (!dirty) return new Vector2(worldStartx, worldStarty);
		dirty = false;

		final float positionX = x;
		final float positionY = y;
		final float originX = this.originX;
		final float originY = this.originY;
		final float scaleFactor = this.scale;
		final boolean scale = scaleFactor != 1;
		final float rotation = this.rotation;
		final float cos = MathUtils.cosDeg(rotation);
		final float sin = MathUtils.sinDeg(rotation);

		float x = localStartx - originX;
		float y = localStarty - originY;

		if (scale)
		{
			x *= scaleFactor;
			y *= scaleFactor;
		}

		if (flipped)
			x *= -1;

		if (rotation != 0)
		{
			float oldX = x;
			x = cos*x - sin*y;
			y = sin*oldX + cos*y;
		}
		worldStartx = x+positionX+originX;
		worldStarty = y+positionY+originY;

		return new Vector2(worldStartx, worldStarty);
	}


	public Vector2 getTransformedEnd()
	{
		if (!dirty) return new Vector2(worldStartx, worldStarty);
		dirty = false;

		final float positionX = x;
		final float positionY = y;
		final float originX = this.originX;
		final float originY = this.originY;
		final float scaleFactor = this.scale;
		final boolean scale = scaleFactor != 1;
		final float rotation = this.rotation;
		final float cos = MathUtils.cosDeg(rotation);
		final float sin = MathUtils.sinDeg(rotation);

		float x = localEndx - originX;
		float y = localEndy - originY;

		if (scale)
		{
			x *= scaleFactor;
			y *= scaleFactor;
		}

		if (rotation != 0)
		{
			float oldX = x;
			x = cos*x - sin*y;
			y = sin*oldX + cos*y;
		}

		if (flipped)
			x *= -1;

		worldEndx = x+positionX+originX;
		worldEndy = y+positionY+originY;

		return new Vector2(worldEndx, worldEndy);
	}

	public float getTransformedRadius()
	{
		return radius*scale;
	}

	public void setOrigin(float originX, float originY)
	{
		this.originX = originX;
		this.originY = originY;
		dirty = true;
	}

	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		dirty = true;
	}

	public void setStart(float x, float y)
	{
		localStartx = x;
		localStarty = y;
		dirty = true;
	}

	public void setEnd(float x, float y)
	{
		localEndx = x;
		localEndy = y;
		dirty = true;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
		dirty = true;
	}

	public void translate(float x, float y)
	{
		this.x += x;
		this.y += y;
		dirty = true;
	}

	public void setRotation(float degrees)
	{
		this.rotation = degrees;
		dirty = true;
	}

	public void rotate(float degrees)
	{
		this.rotation += degrees;
		dirty = true;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		dirty = true;
	}

	public void scale(float scale)
	{
		this.scale *= scale;
		dirty = true;
	}

	public void setFlipped(boolean flipped)
	{
		this.flipped = flipped;
		dirty = true;
	}

	public void flip()
	{
		flipped = !flipped;
		dirty = true;
	}

	public void dirty()
	{
		dirty = true;
	}

	public Rectangle getBoundingRectangle ()
	{
		float minX = Math.min(getTransformedStart().x, getTransformedEnd().x)-getTransformedRadius();
		float maxX = Math.max(getTransformedStart().x, getTransformedEnd().x)+getTransformedRadius();
		float minY = Math.min(getTransformedStart().y, getTransformedEnd().y)-getTransformedRadius();
		float maxY = Math.max(getTransformedStart().y, getTransformedEnd().y)+getTransformedRadius();

		if (bounds == null) bounds = new Rectangle();
		bounds.x = minX;
		bounds.y = minY;
		bounds.width = maxX - minX;
		bounds.height = maxY - minY;

		return bounds;
	}

	public boolean contains(Vector2 point)
	{
		return Intersector.distanceSegmentPoint(getTransformedStart(), getTransformedEnd(),
				point) <= getTransformedRadius();
	}

	public boolean contains(float x, float y)
	{
		return contains(new Vector2(x, y));
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getOriginX ()
	{
		return originX;
	}

	public float getOriginY ()
	{
		return originY;
	}

	public float getRotation ()
	{
		return rotation;
	}

	public float getScale ()
	{
		return scale;
	}

	public boolean getFlipped()
	{
		return flipped;
	}
}
