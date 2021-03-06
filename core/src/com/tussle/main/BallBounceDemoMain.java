/*
 * Copyright (c) 2018 eaglgenes101
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

public class BallBounceDemoMain extends ApplicationAdapter
{
	TussleEngine engine;
	Entity ball0;
	Entity cage;

	public BallBounceDemoMain(Controller[] ctrl)
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
		ball0.getComponent(VelocityComponent.class).setVelocity(4, -4);
		ball0.getComponent(ECBComponent.class).put(dummyStatus0,
		        Utility.stageElementFor(
		        		new CollisionStadium(0, 0, 0, 0, 32),
				        ball0
		));
		ball0.getComponent(ElasticityComponent.class).wallElasticity = 1.5;
		ball0.getComponent(ElasticityComponent.class).groundElasticity = 1.5;
		ball0.getComponent(SpriteComponent.class).setPath("core/assets/sprites/shield_bubble.png");
		ball0.getComponent(StageElementComponent.class).put(dummyStatus0,
		        Utility.stageElementFor(
		        		new CollisionCircle(0, 0, 32),
				        ball0
		));
		
		cage = engine.createEntity();
		cage.add(engine.createComponent(PositionComponent.class));
		cage.add(engine.createComponent(StageElementComponent.class));
		cage.add(engine.createComponent(ScriptContextComponent.class));
		ContainerStatusEffect dummyCage = new ContainerStatusEffect(
				engine.getSystem(SubactionScriptSystem.class).getContextFor(cage),
				engine.getSystem(SubactionScriptSystem.class).getDestructionSignaller()
		);
		cage.getComponent(ScriptContextComponent.class).addStatusEffect(dummyCage);
		cage.getComponent(StageElementComponent.class).put(dummyCage,
		        Utility.stageElementFor(
		        		new CollisionEdge(-320, 240, -320, -240),
		                cage
		));
		cage.getComponent(StageElementComponent.class).put(dummyCage,
		        Utility.stageElementFor(
		                new CollisionEdge(320, -240, 320, 240),
		                cage
		));
		cage.getComponent(StageElementComponent.class).put(dummyCage,
		        Utility.stageElementFor(
		                new CollisionEdge(-320, -240, 320, -240),
		                cage
		));
		cage.getComponent(StageElementComponent.class).put(dummyCage,
		        Utility.stageElementFor(
		                new CollisionEdge(320, 240, -320, 240),
		                cage
		));

		engine.addEntity(ball0);
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
