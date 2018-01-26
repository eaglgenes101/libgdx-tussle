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

package com.tussle.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.tussle.collision.StageElement;
import com.tussle.main.Components;
import com.tussle.motion.PositionComponent;

public class CameraSystem extends IteratingSystem
{
	public static final double ZOOM_FACTOR = .02;
	public static final int MARGIN = 80;
	private OrthographicCamera camera;

	private float minx = 0;
	private float maxx = 0;
	private float miny = 0;
	private float maxy = 0;

	public CameraSystem(int p)
	{
		super(Family.all(PositionComponent.class).get(), p);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void update(float delta)
	{
		minx = (float)(1-ZOOM_FACTOR)*(minx);
		miny = (float)(1-ZOOM_FACTOR)*(miny);
		maxx = (float)(1-ZOOM_FACTOR)*(maxx);
		maxy = (float)(1-ZOOM_FACTOR)*(maxy);
		super.update(delta);
		camera.zoom = Math.max((maxx-minx)/Gdx.graphics.getWidth(),
				(maxy-miny)/Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set((minx+maxx)/2, (miny+maxy)/2, 0);
		camera.update();
	}

	public void processEntity(Entity entity, float delta)
	{
		float entityMinX = (float)Components.positionMapper.get(entity).x;
		float entityMinY = (float)Components.positionMapper.get(entity).y;
		float entityMaxX = (float)Components.positionMapper.get(entity).x;
		float entityMaxY = (float)Components.positionMapper.get(entity).y;
		if (Components.spriteMapper.has(entity))
		{
			if (Components.spriteMapper.get(entity).currentSprite != null)
			{
				if (Components.spriteMapper.get(entity).currentSprite.getBoundingRectangle() != null)
				{
					Rectangle rect = Components.spriteMapper.get(entity).currentSprite.getBoundingRectangle();
					if (entityMinX > rect.x) entityMinX = rect.x;
					if (entityMinY > rect.y) entityMinY = rect.y;
					if (entityMaxX < rect.x + rect.width) entityMaxX = rect.x+rect.width;
					if (entityMaxY < rect.y + rect.height) entityMaxY = rect.y+rect.height;
				}
			}
		}
		if (Components.ecbMapper.has(entity))
		{
			for (StageElement s : Components.ecbMapper.get(entity).getCollisionBoxes())
			{
				com.tussle.collision.Rectangle rect = s.getBounds(1, 1);
				if (s.getBounds(0, 0) != null)
				{
					if (entityMinX > rect.x) entityMinX = (float)rect.x;
					if (entityMinY > rect.y) entityMinY = (float)rect.y;
					if (entityMaxX < rect.x + rect.width) entityMaxX = (float)(rect.x+rect.width);
					if (entityMaxY < rect.y + rect.height) entityMaxY = (float)(rect.y+rect.height);
				}
			}
		}
		if (Components.stageElementMapper.has(entity))
		{
			for (StageElement s : Components.stageElementMapper.get(entity).getStageElements())
			{
				com.tussle.collision.Rectangle rect = s.getBounds(1, 1);
				if (s.getBounds(0, 0) != null)
				{
					if (entityMinX > rect.x) entityMinX = (float)rect.x;
					if (entityMinY > rect.y) entityMinY = (float)rect.y;
					if (entityMaxX < rect.x + rect.width) entityMaxX = (float)(rect.x+rect.width);
					if (entityMaxY < rect.y + rect.height) entityMaxY = (float)(rect.y+rect.height);
				}
			}
		}
		if (minx > entityMinX-MARGIN) minx = entityMinX-MARGIN;
		if (miny > entityMinY-MARGIN) miny = entityMinY-MARGIN;
		if (maxx < entityMaxX+MARGIN) maxx = entityMaxX+MARGIN;
		if (maxy < entityMaxY+MARGIN) maxy = entityMaxY+MARGIN;
		//TODO: only track entities that are essential or have stage surfaces
	}
}
