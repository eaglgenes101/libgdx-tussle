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

package com.tussle.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tussle.control.*;
import com.tussle.main.LibgdxTussleMain;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Libgdx-Tussle";
		config.width = 640;
		config.height = 480;
		config.foregroundFPS = 10;
		config.backgroundFPS = 10;
		Map<Integer, InputToken> inputMap = new HashMap<>();
		Map<Integer, InputToken> releaseMap = new HashMap<>();
		inputMap.put(RIGHT, new InputToken(1, InputState.HOR_MOVEMENT));
		inputMap.put(LEFT, new InputToken(-1, InputState.HOR_MOVEMENT));
		inputMap.put(UP, new InputToken(1, InputState.JUMP));
		releaseMap.put(RIGHT, new InputToken(0, InputState.HOR_MOVEMENT));
		releaseMap.put(LEFT, new InputToken(0, InputState.HOR_MOVEMENT));
		releaseMap.put(UP, new InputToken(0, InputState.JUMP));
		KeyboardController g = new KeyboardController(inputMap, releaseMap, 12);
		new LwjglApplication(new LibgdxTussleMain(new KeyboardController[]{g}), config);
	}
}
