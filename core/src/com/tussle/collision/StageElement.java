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

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public abstract class StageElement
{
	//Whether the StageElement has a before presence, allowing
	//interpolation to make sense
	protected boolean hasBefore = false;
	
	public abstract CollisionShape getBefore();
	public abstract CollisionShape getAfter();
	
	public abstract void step(double dx, double dy, double xpos, double ypos,
	                          double rot, double scale, boolean flipped);
}
