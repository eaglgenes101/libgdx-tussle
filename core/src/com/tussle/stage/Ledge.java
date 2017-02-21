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

package com.tussle.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tussle.fighter.Fighter;

/**
 * Created by eaglgenes101 on 2/21/17.
 */
public class Ledge extends StageElement
{
	Rectangle hitSurface;
	int side;
	Fighter currentFighter;

	public Ledge(Rectangle sweetspot, int direction)
	{
		super(null, sweetspot.getCenter(new Vector2()));
		hitSurface = sweetspot;
		side = direction;
	}

	public void draw(Batch batch, float parentAlpha)
	{
		batch.end();
		debugDrawer.begin();
		debugDrawer.setProjectionMatrix(this.getStage().getCamera().combined);
		debugDrawer.setColor(0, 0, 1, 1);
		debugDrawer.rect(hitSurface.getX(), hitSurface.getY(), hitSurface.getWidth(), hitSurface.getHeight());
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}

	public int getFacing()
	{
		return side;
	}

	public Rectangle getRect()
	{
		return hitSurface;
	}

	public float getClingX()
	{
		if (side == 1)
			return hitSurface.getX()+hitSurface.getWidth();
		else
			return hitSurface.getX();
	}

	public float getClingY()
	{
		return hitSurface.getY()+hitSurface.getHeight();
	}

	public Intersector.MinimumTranslationVector getNormal(Polygon ecb)
	{
		return null;
	}

	public Vector2 checkShape(Polygon oldECB, Polygon newECB)
	{
		return Vector2.Zero;
	}

	public float checkMovement(Vector2 velocity, Polygon ECB)
	{
		return 1.0f;
	}

	public boolean isGrounded(Vector2 leg, Vector2 foot, float yVelocity)
	{
		return false; //Can't stand on a ledge
	}
}
