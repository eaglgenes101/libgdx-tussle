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
	private Vector2 localStart, localEnd, worldStart, worldEnd;

	private float radius;

	private float x, y;
	private float originX, originY;
	private float rotation;
	private float scale = 1;
	private boolean flipped = false;
	private boolean startDirty = true;
	private boolean endDirty = true;
	private Rectangle bounds;

	public Stadium(float startx, float starty, float endx, float endy, float radius)
	{
		localStart = new Vector2(startx, starty);
		localEnd = new Vector2(endx, endy);
		worldStart = new Vector2();
		worldEnd = new Vector2();
		this.radius = radius;
	}

	public Stadium(Vector2 start, Vector2 end, float radius)
	{
		localStart = start.cpy();
		localEnd = end.cpy();
		worldStart = new Vector2();
		worldEnd = new Vector2();
		this.radius = radius;
	}

	public Vector2 getStart()
	{
		return localStart;
	}

	public Vector2 getEnd()
	{
		return localEnd;
	}

	public float getRadius()
	{
		return radius;
	}

	public Vector2 getTransformedStart()
	{
		if (!startDirty) return worldStart;
		startDirty = false;

		final float positionX = x;
		final float positionY = y;
		final float originX = this.originX;
		final float originY = this.originY;
		final float scaleFactor = this.scale;
		final boolean scale = scaleFactor != 1;
		final float rotation = this.rotation;
		final float cos = MathUtils.cosDeg(rotation);
		final float sin = MathUtils.sinDeg(rotation);

		float x = localStart.x - originX;
		float y = localStart.y - originY;

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

		worldStart.set(x+positionX+originX, y+positionY+originY);

		return worldStart;
	}


	public Vector2 getTransformedEnd()
	{
		if (!endDirty) return worldEnd;
		endDirty = false;

		final float positionX = x;
		final float positionY = y;
		final float originX = this.originX;
		final float originY = this.originY;
		final float scaleFactor = this.scale;
		final boolean scale = scaleFactor != 1;
		final float rotation = this.rotation;
		final float cos = MathUtils.cosDeg(rotation);
		final float sin = MathUtils.sinDeg(rotation);

		float x = localEnd.x - originX;
		float y = localEnd.y - originY;

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

		worldEnd.set(x+positionX+originX, y+positionY+originY);

		return worldEnd;
	}

	public float getTransformedRadius()
	{
		return radius*scale;
	}

	public void setOrigin(float originX, float originY)
	{
		this.originX = originX;
		this.originY = originY;
		startDirty = true;
		endDirty = true;
	}

	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		startDirty = true;
		endDirty = true;
	}

	public void setStart(float x, float y)
	{
		localStart.set(x, y);
		startDirty = true;
	}

	public void setEnd(float x, float y)
	{
		localEnd.set(x, y);
		endDirty = true;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public void translate(float x, float y)
	{
		this.x += x;
		this.y += y;
		startDirty = true;
		endDirty = true;
	}

	public void setRotation(float degrees)
	{
		this.rotation = degrees;
		startDirty = true;
		endDirty = true;
	}

	public void rotate(float degrees)
	{
		this.rotation += degrees;
		startDirty = true;
		endDirty = true;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		startDirty = true;
		endDirty = true;
	}

	public void scale(float scale)
	{
		this.scale *= scale;
		startDirty = true;
		endDirty = true;
	}

	public void setFlipped(boolean flipped)
	{
		this.flipped = flipped;
		startDirty = true;
		endDirty = true;
	}

	public void flip()
	{
		flipped = !flipped;
		startDirty = true;
		endDirty = true;
	}

	public void dirty()
	{
		startDirty = true;
		endDirty = true;
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
