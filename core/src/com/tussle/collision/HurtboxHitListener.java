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

package com.tussle.collision;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.tussle.Subaction;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by eaglgenes101 on 2/27/17.
 */
public class HurtboxHitListener implements EventListener
{
	public HurtboxHitListener()
	{
		super();
	}

	public boolean handle(Event event)
	{
		if (!(event instanceof HitEvent))
		{
			return false; //PASS
		}
		else
		{
			HitEvent hitEvent = (HitEvent)event;
			Hurtbox hbox = (Hurtbox)hitEvent.getListenerActor();
			BiPredicate<Hitbox, List<Subaction>> aggregateFilter =
					(Hitbox h, List<Subaction> subacts) -> true; //Yay lambdas
			for (Armor armor : hbox.getArmors())
				aggregateFilter = armor.and(aggregateFilter);
			return !aggregateFilter.test(hitEvent.getHitbox(), hitEvent.getEffects());
		}
	}
}
