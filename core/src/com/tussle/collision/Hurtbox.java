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

import com.tussle.fighter.Terminable;
import com.tussle.fighter.Fighter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class Hurtbox extends Stadium
{
	ArrayList<Armor> armors;
	Terminable associated;

	public Hurtbox(float startx, float starty, float endx, float endy, float rad, Terminable associated)
	{
		super(startx, starty, endx, endy, rad);
		armors = new ArrayList<>();
		this.associated = associated;
	}

	public List<Armor> getArmors()
	{
		return (List)armors.clone();
	}

	public void onHit(Hitbox hbox, EffectList subactions)
	{
		BiPredicate<Hitbox, EffectList> aggregateFilter =
				new ApplyEffectsArmor(getAssociated().getBody());
		if (associated.getBody() instanceof Fighter)
		{
			Fighter fighter = (Fighter)associated.getBody();
			for (Armor armor : fighter.getArmors())
				aggregateFilter = armor.and(aggregateFilter);
		}
		for (Armor armor : getArmors())
			aggregateFilter = armor.and(aggregateFilter);

		if (aggregateFilter.test(hbox, subactions))
		{
			hbox.getOwnerOnHitSubactions(this.getAssociated().getBody())
					.apply(hbox.getAssociated(), hbox.getAssociated().getBody());
		}
	}

	public Terminable getAssociated()
	{
		return associated;
	}
}
