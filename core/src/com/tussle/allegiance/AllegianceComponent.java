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

package com.tussle.allegiance;

import com.badlogic.ashley.core.Component;

/**
 * Created by eaglgenes101 on 4/21/17.
 */
public class AllegianceComponent implements Component
{
	private Team team;

	public AllegianceComponent(Team startTeam, boolean isEssential)
	{
		team = startTeam;
		//TODO: Add essential team checking
	}

	public void changeTeam(Team newTeam)
	{
		team = newTeam;
	}

	public boolean isAllied(Team checkTeam)
	{
		return team.equals(checkTeam);
	}
}
