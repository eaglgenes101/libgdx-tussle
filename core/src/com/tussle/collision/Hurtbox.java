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

import com.tussle.main.BaseBody;
import com.tussle.fighter.Fighter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class Hurtbox extends Stadium
{
	ArrayList<Armor> armors;
	BaseBody owner;

	public Hurtbox(float startx, float starty, float endx, float endy, float rad, ArrayList<Armor> armorList, BaseBody owner)
	{
		super(startx, starty, endx, endy, rad);
		armors = armorList;
		this.owner = owner;
	}

	public List<Armor> getArmors()
	{
		return (List)armors.clone();
	}

	public void onHit(Hitbox hbox, EffectList subactions)
	{
		BiPredicate<Hitbox, EffectList> aggregateFilter =
				(Hitbox h, EffectList subacts) -> true; //Yay lambdas
		if (owner instanceof Fighter)
		{
			Fighter fighter = (Fighter) owner;
			for (Armor armor : fighter.getArmors())
				aggregateFilter = armor.and(aggregateFilter);
		}
		for (Armor armor : getArmors())
			aggregateFilter = armor.and(aggregateFilter);
		if(aggregateFilter.test(hbox, subactions))
			subactions.onStart();
	}
}
