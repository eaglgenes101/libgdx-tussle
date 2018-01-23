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

package com.tussle.ownership;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by eaglgenes101 on 4/21/17.
 */
//A team collective
public class Team
{
	int score;
	int lives;
	Set<Player> members;
	//TODO: Add script hooks for custom scoring or modes
	
	public Team()
	{
		score = 0;
		lives = 0;
		members = new HashSet<>();
	}
	
	public void setLives(int l)
	{
		lives = l;
	}
	
	public void addMember(Player newMember)
	{
		members.add(newMember);
	}
	
	
	
	
}
