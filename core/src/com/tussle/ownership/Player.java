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

import com.badlogic.ashley.core.Entity;
import com.tussle.control.Controller;

//Class representing the interface to a single player,
//wielding a single controller and controlling a single fighter
public class Player
{
	Entity fighter;
	Controller controller;
	Team team;
	
	public Player(Controller ctrl)
	{
		fighter = null;
		controller = ctrl;
		team = new Team(); //By default, players are alone in a single team
	}
	
	public boolean isInTeam(Team t)
	{
		return team.equals(t);
	}
	
	//Receives a heap of scripts which the player object uses to construct a
	//new fighter entity
	public Entity setFighter(Entity f)
	{
		//Scrap the previous fighter if we had a previous one
		if (fighter != null)
		{
			//TODO: Add code
		}
		
		//if (Components.playerOwnershipMapper)
		return null; //stub
	}
}
