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

package com.tussle.subaction;

//Break throwable to get out of loops early
//Optionally allows naming of a loop to break out of
public class Break extends RemoteJump
{
	String target;
	
	public Break(Subaction source)
	{
		super(source, "Loop break from ");
		target = null;
	}
	
	public Break(Subaction source, String targ)
	{
		super(source, "Loop break to "+targ.toString()+" from ");
		target = targ;
	}
	
	public boolean hasTarget()
	{
		return target != null;
	}
	
	public boolean isTarget(String str)
	{
		return target == null || target.equals(str);
	}
}
