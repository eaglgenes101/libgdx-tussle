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

package com.tussle.subaction;

import com.badlogic.ashley.core.Entity;
import com.tussle.control.SingleTokenScanner;
import com.tussle.main.Components;
import com.tussle.script.StackedBindings;

import javax.script.ScriptContext;

public class TestInputSubaction implements Subaction
{
	SingleTokenScanner[] checkers;
	Entity owner;
	
	public TestInputSubaction(SingleTokenScanner[] bufchecks, Entity e)
	{
		checkers = bufchecks;
		owner = e;
	}
	
	public Object eval(ScriptContext globals, StackedBindings locals) throws RemoteJump
	{
		return Components.controlMapper.get(owner).getController().matchInput(checkers);
	}
}
