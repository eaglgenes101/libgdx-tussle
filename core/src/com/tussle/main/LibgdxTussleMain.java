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

package com.tussle.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.tussle.control.Controller;

public class LibgdxTussleMain extends ApplicationAdapter
{
	TussleEngine engine;

	public LibgdxTussleMain(Controller[] ctrl)
	{
		super();
	}
	
	@Override
	public void create ()
	{
		engine = new TussleEngine();
	}

	@Override
	public void render ()
	{
		if (engine != null)
			engine.update(1);
	}
	
	@Override
	public void dispose ()
	{
	}

	public void resize(int width, int height)
	{
	}
}
