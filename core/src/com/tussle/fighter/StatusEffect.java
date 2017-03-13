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

import com.tussle.main.PhysicalBody;

public abstract class StatusEffect extends Terminable
{
	public abstract void onStart(); //After construction and initialization

	public boolean act(float delta)
	{
		StatusEffect newEffect = eachFrame();
		if (newEffect == this)
		{
			return false;
		}
		else
		{
			onEnd(newEffect);
			if (newEffect != null)
				((Fighter)actor).addStatusEffect(newEffect);
			return true;
		}
	}

	public abstract StatusEffect eachFrame(); //Each frame

	public abstract void onEnd(StatusEffect nextEffect); //Before disposal

	public Fighter getOwner()
	{
		//TODO: Insert more behavior for articles
		if (getActor() instanceof Fighter)
			return (Fighter)getActor();
		else
			return null;
	}

	public PhysicalBody getBody()
	{
		return (PhysicalBody)getActor();
	}
}
