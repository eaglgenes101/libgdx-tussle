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

package com.tussle.hitbox;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.tussle.collision.StageElement;
import com.tussle.postprocess.PostprocessSystem;
import com.tussle.script.ScriptContextComponent;
import com.tussle.script.ScriptIterator;
import com.tussle.subaction.Subaction;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Hitbox
{
	//Collision Box
	StageElement box;
	Entity owner;
	ScriptIterator controller;
	
	//Hitbox lock
	HitboxLock lock;
	
	Subaction chargeSupplier;
	
	GeneralizedInflictionSupplier hitInflicts;
	Supplier<ScriptIterator> onHit;
	Supplier<ScriptIterator> onOutprioritized;
	
	BiPredicate<Hurtbox, Entity> doesHitSupplier;
	Predicate<Hitbox> doesClankSupplier;
	
	public Hitbox(Entity e, ScriptIterator s)
	{
		//Squeezing every single field into the constructor would be extremely unweildy,
		//even if I've managed to reduce it to a bunch of functions
		//so a blank constructor and numerous set methods are provided for
		//factory methods to use
		owner = e;
		controller = s;
	}
	
	public StageElement getBox()
	{
		return box;
	}
	
	public Entity getOwner()
	{
		return owner;
	}
	
	public ScriptIterator getController()
	{
		return controller;
	}
	
	//Called when attempting to clank against another hitbox
	//Put post-processing callbacks into ourselves, then
	//Return true if we were outclanked, false otherwise
	public boolean tryClank(Hitbox other, Engine engine)
	{
		//We assume the engine already checked whether we actually collide
		//We handle our callbacks, the other will handle theirs if applicable
		if (doesClankSupplier.test(other))
		{
			engine.getSystem(PostprocessSystem.class).add(
					owner,
					ScriptContextComponent.class,
					(comp) -> {
						comp.addStatusEffect(onOutprioritized.get());
					}
			);
			engine.getSystem(PostprocessSystem.class).add(
					other.getOwner(),
					HitboxLockComponent.class,
					(comp) -> {
						comp.put(controller, lock);
					}
			);
			return true;
		}
		return false;
	}
	
	
	public boolean tryHit(Hurtbox other, Entity otherOwner, Engine engine)
	{
		if (doesHitSupplier.test(other, otherOwner))
		{
			engine.getSystem(PostprocessSystem.class).add(
					owner,
					ScriptContextComponent.class,
					(comp) -> {
						comp.addStatusEffect(onHit.get());
					}
			);
			engine.getSystem(PostprocessSystem.class).add(
					otherOwner,
					ScriptContextComponent.class,
					(comp) -> {
						comp.addStatusEffect(hitInflicts.getEffect(
								owner, otherOwner,
								this, other,
								chargeSupplier
						));
					}
			);
			return true;
		}
		return false;
	}
}
