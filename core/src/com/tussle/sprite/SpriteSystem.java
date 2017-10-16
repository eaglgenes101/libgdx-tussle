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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tussle.motion.PositionComponent;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class SpriteSystem extends IteratingSystem
{
	private AssetManager assetManager; // Points to the communal asset manager
	private Camera camera; //The camera we use
	private SpriteBatch batch; // Where we draw everything

	ComponentMapper<SpriteComponent> spriteMapper =
			ComponentMapper.getFor(SpriteComponent.class);
	ComponentMapper<PositionComponent> positionMapper =
			ComponentMapper.getFor(PositionComponent.class);

	public SpriteSystem(AssetManager manager, Camera c, int p)
	{
		super(Family.all(SpriteComponent.class, PositionComponent.class).get(), p);
		assetManager = manager;
		batch = new SpriteBatch();
		camera = c;
	}

	public void update(float delta)
	{
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		super.update(delta);
		batch.end();
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
				sprite.setCenter((float) positionComponent.x, (float) positionComponent.y);
				sprite.draw(batch);
			}
		}
	}
}
