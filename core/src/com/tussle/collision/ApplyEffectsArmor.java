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

import com.tussle.main.PhysicalBody;

/**
 * Created by eaglgenes101 on 4/5/17.
 */
public class ApplyEffectsArmor extends Armor
{
	PhysicalBody owner;

	public ApplyEffectsArmor(PhysicalBody owner)
	{
		this.owner = owner;
	}

	public boolean test(Hitbox hitbox, EffectList effectList)
	{
		effectList.apply(hitbox.getAssociated(), hitbox.getAssociated().getBody());
		return true;
	}
}
