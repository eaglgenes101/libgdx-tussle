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

package com.tussle.script;

import com.tussle.subaction.RemoteJump;
import com.tussle.subaction.Subaction;

//Indicates that a jump is invalid because it specifies a jump that cannot
//be made (IE an unnamed break statement outside of a loop)
//This almost certainly indicates a syntax error in the script
public class IllegalJumpException extends IllegalStateException
{
	Subaction thrower;
	
	public IllegalJumpException(RemoteJump cause)
	{
		super(cause.getSource().toString() + " made an invalid jump", cause);
		thrower = cause.getSource();
	}
	
	public Subaction getThrower()
	{
		return thrower;
	}
}
