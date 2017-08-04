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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tussle.motion.PositionComponent;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class SpriteSystem extends IteratingSystem
{
	public static final double ZOOM_FACTOR = .02;
	public static final int MARGIN = 40;

	private AssetManager assetManager; // Points to the communal asset manager
	private SpriteBatch batch; // Where we draw everything
	private ExtendViewport viewport;

	ComponentMapper<SpriteComponent> spriteMapper =
			ComponentMapper.getFor(SpriteComponent.class);
	ComponentMapper<PositionComponent> positionMapper =
			ComponentMapper.getFor(PositionComponent.class);
	private float currentx = 0;
	private float currenty = 0;
	private float minx = 0;
	private float maxx = 0;
	private float miny = 0;
	private float maxy = 0;

	public SpriteSystem(AssetManager manager, int p)
	{
		super(Family.all(SpriteComponent.class, PositionComponent.class).get(), p);
		assetManager = manager;
		batch = new SpriteBatch();
		viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport.apply();
	}

	public void update(float delta)
	{
		minx += (currentx-MARGIN-minx)*ZOOM_FACTOR;
		maxx += (currentx+MARGIN-maxx)*ZOOM_FACTOR;
		miny += (currenty-MARGIN-miny)*ZOOM_FACTOR;
		maxy += (currenty+MARGIN-maxy)*ZOOM_FACTOR;
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		batch.begin();
		super.update(delta);
		batch.end();
		currentx = (minx+maxx)/2;
		currenty = (miny+maxy)/2;
		float zoom = Math.max((maxx-minx)/Gdx.graphics.getWidth(),
				(maxy-miny)/Gdx.graphics.getHeight());
		viewport.getCamera().position.set(currentx, currenty,
				viewport.getCamera().position.z);
		viewport.setWorldSize(zoom*Gdx.graphics.getWidth(),
				zoom*Gdx.graphics.getHeight());
		viewport.apply();
	}

	public void processEntity(Entity entity, float delta)
	{
		String toGetPath = spriteMapper.get(entity).intendedSpritePath;
		if (toGetPath != null)
		{
			assetManager.load(toGetPath, Texture.class);
			assetManager.finishLoading();
			Sprite sprite = spriteMapper.get(entity).loadSprite(assetManager.get(toGetPath, Texture.class));
			PositionComponent positionComponent = positionMapper.get(entity);
			if (sprite != null)
			{
				sprite.setOriginCenter();
				sprite.setPosition((float) positionComponent.x, (float) positionComponent.y);
				sprite.draw(batch);
				Rectangle rect = sprite.getBoundingRectangle();
				if (rect.getX() < minx)
					minx = rect.getX();
				if (rect.getX() + rect.getWidth() > maxx)
					maxx = rect.getX() + rect.getWidth();
				if (rect.getY() < miny)
					miny = rect.getY();
				if (rect.getY() + rect.getHeight() > maxy)
					maxy = rect.getY() + rect.getHeight();
			}
		}
		//TODO: only track entities that are essential or have stage surfaces
	}
}
