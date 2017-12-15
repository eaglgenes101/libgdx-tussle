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

import com.badlogic.ashley.signals.Signal;

import javax.script.ScriptContext;

//Script Iterator that does very little besides assume ownership
//of game objects stored to it
public class ContainerStatusEffect extends ScriptIterator
{
	public ContainerStatusEffect(ScriptContext context, Signal<ScriptIterator> destructSig)
	{
		//Contains entries that are then deallocated when this leaves scope
		super(
				(globals, locals) -> {
					return null;
				},
				(globals, locals) -> {
					return null;
				},
				(globals, locals) -> {
					return null;
				},
				context,
				destructSig
		);
	}
}
