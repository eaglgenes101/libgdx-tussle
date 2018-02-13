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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tussle.collision.StageStadium;
import com.tussle.collision.StageElement;
import com.tussle.main.Components;
import com.tussle.motion.PositionComponent;

public class HitboxDrawingSystem extends IteratingSystem
{
	private Camera camera; //The camera we use
	ShapeRenderer drawer;

	public HitboxDrawingSystem(Camera c, int p)
	{
		super(Family.all(PositionComponent.class).get(), p);
		drawer = new ShapeRenderer();
		camera = c;
	}

	public void update(float delta)
	{
		drawer.setProjectionMatrix(camera.combined);
		drawer.begin(ShapeRenderer.ShapeType.Line);
		super.update(delta);
		drawer.end();
	}

	public void processEntity(Entity entity, float delta)
	{
		if (Components.ecbMapper.has(entity))
		{
			//Draw ECB
			drawer.setColor(Color.BLUE);
			for (StageStadium s : Components.ecbMapper.get(entity).getCollisionBoxes())
			{
				s.getBefore().draw(drawer);
				s.getAfter().draw(drawer);
			}
		}
		if (Components.stageElementMapper.has(entity))
		{
			drawer.setColor(Color.GREEN);
			for (StageElement s : Components.stageElementMapper.get(entity).getStageElements())
			{
				s.getBefore().draw(drawer);
				s.getAfter().draw(drawer);
			}
		}
	}
}
