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

package com.tussle.ownership;

import com.badlogic.ashley.core.Component;

public class PlayerOwnershipComponent implements Component
{
	Player owner;
	
	public PlayerOwnershipComponent()
	{
		owner = null;
	}
	
	public void setOwner(Player player)
	{
		owner = player;
	}
	
	public boolean isOwner(Player player)
	{
		return owner != null && owner.equals(player);
	}
	
	public boolean isInTeam(Team team)
	{
		return owner != null && owner.isInTeam(team);
	}
}
