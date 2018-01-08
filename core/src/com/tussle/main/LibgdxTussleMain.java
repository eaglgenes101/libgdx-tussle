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
import com.tussle.script.ContainerStatusEffect;
import com.tussle.script.ScriptContextComponent;
import com.tussle.script.SubactionScriptSystem;
import com.tussle.sprite.SpriteComponent;

public class LibgdxTussleMain extends ApplicationAdapter
{
	TussleEngine engine;
	Entity ball0;
	Entity ball1;
	Entity cage;

	public LibgdxTussleMain(Controller[] ctrl)
	{
		super();
	}
	
	@Override
	public void create ()
	{
		engine = new TussleEngine();

		ball0 = engine.createEntity();
		ball0.add(engine.createComponent(PositionComponent.class));
		ball0.add(engine.createComponent(VelocityComponent.class));
		ball0.add(engine.createComponent(StageElementComponent.class));
		ball0.add(engine.createComponent(ECBComponent.class));
		ball0.add(engine.createComponent(ElasticityComponent.class));
		ball0.add(engine.createComponent(SpriteComponent.class));
		ball0.add(engine.createComponent(ScriptContextComponent.class));
		ContainerStatusEffect dummyStatus0 = new ContainerStatusEffect(
				engine.getSystem(SubactionScriptSystem.class).getContextFor(ball0),
				engine.getSystem(SubactionScriptSystem.class).getDestructionSignaller()
		);
		ball0.getComponent(ScriptContextComponent.class).addStatusEffect(dummyStatus0);
		ball0.getComponent(PositionComponent.class).setPosition(100, 0);
		ball0.getComponent(VelocityComponent.class).xVel = 1;
		ball0.getComponent(VelocityComponent.class).yVel = 4;
		ball0.getComponent(ECBComponent.class).put(dummyStatus0 , new CollisionBox(0, 0, 0, 0, 32));
		ball0.getComponent(ElasticityComponent.class).wallElasticity = .5;
		ball0.getComponent(ElasticityComponent.class).groundElasticity = .5;
		ball0.getComponent(SpriteComponent.class).setPath("core/assets/sprites/shield_bubble.png");
		ball0.getComponent(StageElementComponent.class).put(dummyStatus0, new StageCircle(0, 0, 32));


		ball1 = engine.createEntity();
		ball1.add(engine.createComponent(PositionComponent.class));
		ball1.add(engine.createComponent(VelocityComponent.class));
		ball1.add(engine.createComponent(StageElementComponent.class));
		ball1.add(engine.createComponent(ECBComponent.class));
		ball1.add(engine.createComponent(ElasticityComponent.class));
		ball1.add(engine.createComponent(SpriteComponent.class));
		ball1.add(engine.createComponent(ScriptContextComponent.class));
		ContainerStatusEffect dummyStatus1 = new ContainerStatusEffect(
				engine.getSystem(SubactionScriptSystem.class).getContextFor(ball1),
				engine.getSystem(SubactionScriptSystem.class).getDestructionSignaller()
		);
		ball1.getComponent(ScriptContextComponent.class).addStatusEffect(dummyStatus1);
		ball1.getComponent(PositionComponent.class).setPosition(0, 100);
		ball1.getComponent(VelocityComponent.class).xVel = 2;
		ball1.getComponent(VelocityComponent.class).yVel = 1;
		ball1.getComponent(ECBComponent.class).put(dummyStatus1, new CollisionBox(0, 0, 0, 0, 32));
		ball1.getComponent(ElasticityComponent.class).wallElasticity = .5;
		ball1.getComponent(ElasticityComponent.class).groundElasticity = .5;
		ball1.getComponent(SpriteComponent.class).setPath("core/assets/sprites/shield_bubble.png");
		ball1.getComponent(StageElementComponent.class).put(dummyStatus1, new StageCircle(0, 0, 32));

		cage = engine.createEntity();
		cage.add(engine.createComponent(PositionComponent.class));
		cage.add(engine.createComponent(StageElementComponent.class));
		cage.add(engine.createComponent(ScriptContextComponent.class));
		ContainerStatusEffect dummyCage = new ContainerStatusEffect(
				engine.getSystem(SubactionScriptSystem.class).getContextFor(cage),
				engine.getSystem(SubactionScriptSystem.class).getDestructionSignaller()
		);
		cage.getComponent(ScriptContextComponent.class).addStatusEffect(dummyCage);
		//cage.getComponent(StageElementComponent.class).put(new StageCircle(-320, -320, 160));
		//cage.getComponent(StageElementComponent.class).put(new StageCircle(320, -320, 160));
		//cage.getComponent(StageElementComponent.class).put(new StageCircle(320, 320, 160));
		//cage.getComponent(StageElementComponent.class).put(new StageCircle(-320, 320, 160));


		//cage.getComponent(StageElementComponent.class).put(new StageEdge(-640, -480, -640, 480));
		//cage.getComponent(StageElementComponent.class).put(new StageEdge(-640, 480, 640, 480));
		//cage.getComponent(StageElementComponent.class).put(new StageEdge(640, 480, 640, -480));
		//cage.getComponent(StageElementComponent.class).put(new StageEdge(640, -480, -640, -480));
		cage.getComponent(StageElementComponent.class).put(dummyCage, new StageEdge(-320, 240, -320, -240));
		cage.getComponent(StageElementComponent.class).put(dummyCage, new StageEdge(320, 240, -320, 240));
		cage.getComponent(StageElementComponent.class).put(dummyCage, new StageEdge(320, -240, 320, 240));
		cage.getComponent(StageElementComponent.class).put(dummyCage, new StageEdge(-320, -240, 320, -240));


		engine.addEntity(ball0);
		engine.addEntity(ball1);
		engine.addEntity(cage);
	}

	@Override
	public void render ()
	{
		if (engine != null)
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
