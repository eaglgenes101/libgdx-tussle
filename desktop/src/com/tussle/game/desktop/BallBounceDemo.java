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

package com.tussle.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tussle.control.InputToken;
import com.tussle.control.KeyboardController;
import com.tussle.main.BallBounceDemoMain;

import java.util.HashMap;
import java.util.Map;

public class BallBounceDemo
{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Ball Bounce Demo";
		config.width = 640;
		config.height = 480;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		Map<Integer, InputToken> inputMap = new HashMap<>();
		Map<Integer, InputToken> releaseMap = new HashMap<>();
		KeyboardController g = new KeyboardController(inputMap, releaseMap, 12);
		new LwjglApplication(new BallBounceDemoMain(new KeyboardController[]{g}), config);
	}
}
