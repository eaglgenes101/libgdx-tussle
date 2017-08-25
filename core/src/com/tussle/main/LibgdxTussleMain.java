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

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.tussle.collision.*;
import com.tussle.control.Controller;
import com.tussle.motion.PositionComponent;
import com.tussle.motion.VelocityComponent;
import com.tussle.sprite.SpriteComponent;

public class LibgdxTussleMain extends ApplicationAdapter
{
	TussleEngine engine;
	Entity ball;
	Entity cage;

	public LibgdxTussleMain(Controller[] ctrl)
	{
		super();
	}
	
	@Override
	public void create ()
	{
		engine = new TussleEngine();

		ball = engine.createEntity();
		ball.add(engine.createComponent(PositionComponent.class));
		ball.add(engine.createComponent(VelocityComponent.class));
		ball.add(engine.createComponent(ECBComponent.class));
		ball.add(engine.createComponent(ElasticityComponent.class));
		ball.add(engine.createComponent(SpriteComponent.class));
		ball.getComponent(PositionComponent.class).setPosition(320, 240);
		ball.getComponent(VelocityComponent.class).xVel = 1;
		ball.getComponent(VelocityComponent.class).yVel = 1;
		ball.getComponent(ECBComponent.class).setStadium(new Stadium(0, 0, 0, 0, 16));
		ball.getComponent(ElasticityComponent.class).wallElasticity = 0.9;
		ball.getComponent(ElasticityComponent.class).groundElasticity = 0.9;
		ball.getComponent(SpriteComponent.class).setPath("core/assets/sprites/shield_bubble.png");

		cage = engine.createEntity();
		cage.add(engine.createComponent(PositionComponent.class));
		cage.add(engine.createComponent(StageElementComponent.class));
		cage.getComponent(StageElementComponent.class).put(new StageCircle(-320, -320, 160));
		cage.getComponent(StageElementComponent.class).put(new StageCircle(320, -320, 160));
		cage.getComponent(StageElementComponent.class).put(new StageCircle(320, 320, 160));
		cage.getComponent(StageElementComponent.class).put(new StageCircle(-320, 320, 160));

		/*
		cage.getComponent(StageElementComponent.class).put(new StageEdge(0, 0, 0, 480));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(0, 480, 640, 480));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(640, 480, 640, 0));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(640, 0, 0, 0));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(0, 480, 0, 0));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(640, 480, 0, 480));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(640, 0, 640, 480));
		cage.getComponent(StageElementComponent.class).put(new StageEdge(0, 0, 640, 0));
		*/

		engine.addEntity(ball);
		engine.addEntity(cage);
	}

	@Override
	public void render ()
	{
		engine.update(1);
	}
	
	@Override
	public void dispose ()
	{
	}

	public void resize(int width, int height)
	{
	}
}
