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

import com.badlogic.gdx.Gdx;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

//Provides a shim between Java's logging API and Libgdx's logging API
public class LibgdxConsoleHandler extends Handler
{
	public void flush()
	{
		//Nothing to flush
	}
	
	public void close()
	{
		//Nothing to close
	}
	
	public void publish(LogRecord record)
	{
		try
		{
			if (getFilter().isLoggable(record))
			{
				if (record.getLevel().intValue() >= Level.SEVERE.intValue())
					Gdx.app.error(record.getLoggerName(), getFormatter().formatMessage(record));
				else if (record.getLevel().intValue() >= Level.INFO.intValue())
					Gdx.app.log(record.getLoggerName(), getFormatter().formatMessage(record));
				else
					Gdx.app.debug(record.getLoggerName(), getFormatter().formatMessage(record));
			}
		}
		catch (Exception e)
		{
			getErrorManager().error(record.getLoggerName(), e, ErrorManager.WRITE_FAILURE);
		}
	}
	
	
}
