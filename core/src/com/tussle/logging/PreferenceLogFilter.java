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

package com.tussle.logging;

import com.badlogic.gdx.Preferences;
import com.tussle.main.Utility;

import java.util.HashMap;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PreferenceLogFilter implements Filter
{
	int topLevel;
	HashMap<Class<?>, Integer> systemLevels;
	
	public PreferenceLogFilter()
	{
		Preferences logPrefs = Utility.getPreferencesFor("log");
		topLevel = logPrefs.getInteger("level", 1000);
		for (String entry : logPrefs.get().keySet())
		{
			if (entry.endsWith("/level"))
			{
				try
				{
					Class<?> czz = Class.forName(entry.substring(0, entry.length()-6));
					systemLevels.put(czz, logPrefs.getInteger(entry));
				}
				catch (ClassNotFoundException ex)
				{
					Logger.getLogger(this.getClass().toString()).warning(
							entry.substring(0, entry.length()-6) +
							" is not a valid class name. Skipping. "
					);
				}
			}
		}
	}
	
	public boolean isLoggable(LogRecord record)
	{
		return (record.getLevel().intValue() >= topLevel) ||
		       (systemLevels.containsKey(record.getClass()) &&
		       record.getLevel().intValue() >= systemLevels.get(record.getClass()));
	}
}
