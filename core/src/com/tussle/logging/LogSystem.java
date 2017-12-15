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

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Date;
import java.util.logging.*;

public class LogSystem extends EntitySystem
{
	public static final Logger metaLogger = Logger.getLogger(LogSystem.class.getName());
	public Handler consoleHandler;
	public Handler fileHandler;
	
	public LogSystem(int p)
	{
		super(p);
		consoleHandler = new LibgdxConsoleHandler();
		consoleHandler.setFormatter(new SimpleFormatter());
	}
	
	public void addedToEngine(Engine engine)
	{
		Logger.getGlobal().addHandler(consoleHandler);
		Logger.getGlobal().setFilter(new PreferenceLogFilter());
		if (Gdx.files.isExternalStorageAvailable())
		{
			Date date = new Date();
			date.setTime(System.currentTimeMillis());
			String dateString = date.toString().replace(" ", "_");
			try
			{
				FileHandle handle = Gdx.files.external("logs/" + dateString);
				fileHandler = new LibgdxLogHandler(handle);
				fileHandler.setFormatter(new XMLFormatter());
				Logger.getGlobal().addHandler(fileHandler);
				metaLogger.info("Started logging to logs/" + dateString);
			}
			catch (RuntimeException ex)
			{
				metaLogger.log(Level.WARNING, "Unable to write to logs/"+dateString, ex);
			}
		}
		else
		{
			metaLogger.warning("External storage unavailable for logging. " +
			                   "Log output will be available from console only. ");
		}
	}
	
	public void removedFromEngine(Engine engine)
	{
		metaLogger.info("Log system removed from engine");
		Logger.getGlobal().removeHandler(consoleHandler);
		Logger.getGlobal().removeHandler(fileHandler);
	}
}
