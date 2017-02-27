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

package com.tussle.input;

public class InputToken
{
	int intensity;
	InputState state;

	public InputToken(int i, InputState s)
	{
		intensity = i;
		state = s;
	}

	public int intensity()
	{
		return intensity;
	}

	public InputState state()
	{
		return state;
	}

	public boolean equals(Object o)
	{
		return o instanceof InputToken && ((InputToken) o).intensity() == intensity
				&& ((InputToken) o).state().equals(state);
	}
}
