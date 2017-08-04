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

import com.badlogic.ashley.core.Component;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by eaglgenes101 on 4/24/17.
 */
public class StageElementComponent implements Component
{
	protected Collection<StageElement> surfaces;

	public StageElementComponent()
	{
		surfaces = new LinkedList<>();
	}

	public void put(StageElement surface)
	{
		surfaces.add(surface);
	}

	public Collection<StageElement> get()
	{
		return surfaces;
	}
}