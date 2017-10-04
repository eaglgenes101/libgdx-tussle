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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class EntityStreamMaintainer implements EntityListener
{
	Map<Entity, Reader> inputReaders;
	Map<Entity, Writer> outputWriters;
	Map<Entity, Writer> errorWriters;
	Writer collectedOutput;
	Writer collectedErrors;

	public EntityStreamMaintainer(Reader inputReader, Writer outputWriter, Writer errorWriter)
	{
		toDistributeReader = inputReader;
	}
}
