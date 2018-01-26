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

import com.tussle.script.StackedBindings;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

//It appears that tussle subaction files format its subactions so that each
//subaction is a lexable token
//Then I can run a context-free parser over the result and emit
//a parse tree
@FunctionalInterface
public interface Subaction
{
	Bindings BLANK_BINDINGS = new SimpleBindings();
	
	/*
	 * Upon serialization, a subaction object is constructed so that it is pre-linked to any
	 * prerequisite subactions as defined by the subaction script, and has no need to reach
	 * into its bindings (though some subactions do so).
	 */
	Object eval(ScriptContext globals, StackedBindings locals) throws RemoteJump;
}
