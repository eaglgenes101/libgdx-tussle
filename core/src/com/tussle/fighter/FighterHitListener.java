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

package com.tussle.fighter;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.tussle.Subaction;
import com.tussle.collision.Armor;
import com.tussle.collision.HitEvent;
import com.tussle.collision.Hitbox;
import com.tussle.collision.Hurtbox;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by eaglgenes101 on 2/27/17.
 */
public class FighterHitListener implements EventListener
{
	public FighterHitListener()
	{
		super();
	}

	public boolean handle(Event event)
	{
		if (!(event instanceof HitEvent))
		{
			return false;
		}
		else
		{
			HitEvent hitEvent = (HitEvent)event;
			Fighter fighter = (Fighter)hitEvent.getListenerActor();
			BiPredicate<Hitbox, List<Subaction>> aggregateFilter =
					(Hitbox h, List<Subaction> subacts) -> true; //Yay lambdas
			for (Armor armor : fighter.getArmors())
				aggregateFilter = armor.and(aggregateFilter);
			if(aggregateFilter.test(hitEvent.getHitbox(), hitEvent.getEffects()))
			{
				List<Subaction> subactions = hitEvent.getEffects();
				for (Subaction subaction : subactions)
					subaction.apply(fighter);
			}
			return true; //Always handled
		}
	}
}
