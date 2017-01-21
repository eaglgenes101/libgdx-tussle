package com.tussle.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tussle.input.*;
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
		Map<Integer, InputToken> inputMap = new HashMap<>();
		inputMap.put(RIGHT, new InputToken(1, InputState.HOR_MOVEMENT));
		inputMap.put(LEFT, new InputToken(-1, InputState.HOR_MOVEMENT));
		inputMap.put(UP, new InputToken(1, InputState.VERT_MOVEMENT));
		inputMap.put(DOWN, new InputToken(-1, InputState.VERT_MOVEMENT));

		KeyboardController g = new KeyboardController(inputMap, 12);
		LwjglApplication l = new LwjglApplication(new LibgdxTussleMain(new KeyboardController[]{g}), config);
	}
}
