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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

/**
 * Created by eaglgenes101 on 3/6/17.
 */
public class BaseBody extends Group
{
	String baseDir;
	Texture texture;
	protected Sprite sprite;

	Vector2 velocity;
	int facing;
	float angle;
	float preferredXVelocity;
	float preferredYVelocity;

	protected ShapeRenderer debugDrawer;

	public BaseBody(String path, Vector2 center)
	{
		if (path != null)
		{
			texture = new Texture(path);
			sprite = new Sprite(texture);
			baseDir = path;
			setSize(sprite.getWidth(), sprite.getHeight());
		}
		else
		{
			texture = null;
			sprite = null;
			baseDir = null;
		}
		velocity = new Vector2();
		setOrigin(Align.center);
		setPosition(center.x, center.y, Align.center);
		debugDrawer = new ShapeRenderer();
		debugDrawer.setAutoShapeType(true);
	}

	public void draw(Batch batch, float parentAlpha)
	{
		if (sprite != null)
		{
			sprite.setOriginCenter();
			sprite.setFlip(facing < 0, false);
			sprite.setRotation(angle);
			sprite.setPosition(getX(), getY());
			sprite.draw(batch, parentAlpha);
		}
	}

	public int getFacing()
	{
		return facing;
	}

	public Vector2 getVelocity()
	{
		return velocity;
	}

	public float getXVelocity()
	{
		return velocity.x;
	}

	public float getYVelocity()
	{
		return velocity.y;
	}

	public void setVelocity(Vector2 newVelocity)
	{
		velocity = newVelocity.cpy();
	}

	public void setXVelocity(float x)
	{
		velocity.x = x;
	}

	public void setYVelocity(float y)
	{
		velocity.y = y;
	}

	public void setFacing(int newFacing)
	{
		facing = newFacing;
	}

	public void setPreferredXVelocity(float newVelocity)
	{
		preferredXVelocity = newVelocity;
	}

	public void setPreferredYVelocity(float newVelocity)
	{
		preferredYVelocity = newVelocity;
	}

	public void xAccel(float factor)
	{
		velocity.x = Utility.addFrom(velocity.x, -factor, preferredXVelocity);
	}

	public void yAccel(float factor)
	{
		velocity.y = Utility.addTowards(velocity.y, -factor, preferredYVelocity);
	}
}
