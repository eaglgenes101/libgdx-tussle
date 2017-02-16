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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

public abstract class StageElement extends Actor
{
	String baseDir;
	Texture texture;
	Sprite sprite;

	float xVelocity;
	float yVelocity;
	int facing;
	float angle;

	ShapeRenderer debugDrawer;

	public StageElement(String path, Vector2 center)
	{
		texture = new Texture(path);
		sprite = new Sprite(texture);
		baseDir = path;
		setSize(sprite.getWidth(), sprite.getHeight());
		setOrigin(Align.center);
		setPosition(center.x, center.y, Align.center);
		debugDrawer = new ShapeRenderer();
		debugDrawer.setAutoShapeType(true);
	}

	public void draw(Batch batch, float parentAlpha)
	{
		sprite.setOriginCenter();
		sprite.setFlip(facing < 0, false);
		sprite.setRotation(angle);
		sprite.setPosition(getX(), getY());
		sprite.draw(batch, parentAlpha);
	}

	public void act(float delta)
	{
		super.act(delta);
		//Move self
		setX(getX()+xVelocity);
		setY(getY()+yVelocity);
	}

	public void setVelocity(Vector2 newVelocity)
	{
		xVelocity = newVelocity.x;
		yVelocity = newVelocity.y;
	}

	//Determine the normal to the ECB if it exists
	public abstract Intersector.MinimumTranslationVector getNormal(Polygon ecb);

	//Determine the displacement needed to keep the ECB from intersecting
	public abstract Vector2 checkShape(Polygon oldECB, Polygon newECB);

	//Determine if there is a point where the StageElement would intercept the movement
	public abstract float checkMovement(Vector2 velocity, Polygon ecb);

	public abstract boolean isGrounded(Vector2 leg, Vector2 foot, float yVelocity);
}
