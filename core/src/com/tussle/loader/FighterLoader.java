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

package com.tussle.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class FighterLoader
{
	public static final Logger loadLogger = Logger.getLogger(FighterLoader.class.getName());
	
	HashMap<String, FileHandle> fighterPaths;
	
	public FighterLoader(FileHandle r)
	{
		if (!r.isDirectory())
		{
			//We have a pretty big problem here
			loadLogger.severe(r.path() + " was expected to be a directory. ");
			throw new GdxRuntimeException("Invalid tussle root");
		}
		for (FileHandle fighterDir : r.list())
		{
			if (fighterDir.isDirectory() && fighterDir.child("fighter_info.json") != null)
			{
				//Todo: validate json and check all paths
				fighterPaths.put(fighterDir.name(), fighterDir);
			}
		}
	}
	
	public Set<String> knownFighters()
	{
		return fighterPaths.keySet();
	}
	
	
	
}
