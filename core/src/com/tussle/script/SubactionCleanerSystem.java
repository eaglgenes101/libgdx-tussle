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

package com.tussle.script;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.tussle.collision.ECBComponent;
import com.tussle.collision.StageElementComponent;
import com.tussle.hitbox.HitboxComponent;
import com.tussle.hitbox.HitboxLockComponent;
import com.tussle.hitbox.HurtboxComponent;
import com.tussle.main.Components;

//Provides logic for efficiently dealing with scriptiterator-associated disposal
public class SubactionCleanerSystem extends EntitySystem
{
	public Listener<ScriptIterator> destructionListener;
	
	public SubactionCleanerSystem(int priority)
	{
		super(priority);
	}
	
	public void addedToEngine(Engine engine)
	{
		destructionListener = (signal, object) ->
		{
			for (Entity e : engine.getEntitiesFor(Family.all(ECBComponent.class).get()))
				Components.ecbMapper.get(e).remove(object);
			for (Entity e : engine.getEntitiesFor(Family.all(StageElementComponent.class).get()))
				Components.stageElementMapper.get(e).remove(object);
			for (Entity e : engine.getEntitiesFor(Family.all(HitboxComponent.class).get()))
				Components.hitboxMapper.get(e).remove(object);
			for (Entity e : engine.getEntitiesFor(Family.all(HitboxLockComponent.class).get()))
				Components.hitboxLockMapper.get(e).remove(object);
			for (Entity e : engine.getEntitiesFor(Family.all(HurtboxComponent.class).get()))
				Components.hurtboxMapper.get(e).remove(object);
		};
		engine.getSystem(SubactionScriptSystem.class).subscribeDestruction(this.destructionListener);
	}
	
	public void removedFromEngine(Engine engine)
	{
		engine.getSystem(SubactionScriptSystem.class).unsubscribeDestruction(this.destructionListener);
	}
}
