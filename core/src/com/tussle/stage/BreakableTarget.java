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
import com.badlogic.gdx.math.Vector2;
import com.tussle.collision.Hurtbox;

/**
 * Created by eaglgenes101 on 2/22/17.
 */
public class BreakableTarget extends StageElement
{
	public BreakableTarget(Vector2 center, String path)
	{
		super(path, center);
	}

	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
	}

	//Determine the normal to the ECB if it exists
	public Intersector.MinimumTranslationVector getNormal(Polygon ecb)
	{
		return null;
	}

	//Determine the displacement needed to keep the ECB from intersecting
	public Vector2 checkShape(Polygon oldECB, Polygon newECB)
	{
		return Vector2.Zero.cpy();
	}

	//Determine if there is a point where the StageElement would intercept the movement
	public float checkMovement(Vector2 velocity, Polygon ecb)
	{
		return 1.0f;
	}

	public boolean isGrounded(Vector2 leg, Vector2 foot, float yVelocity)
	{
		return false;
	}
}
