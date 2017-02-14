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

package com.tussle.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tussle.fighter.Fighter;
import com.tussle.input.Controller;
import com.tussle.input.KeyboardController;
import com.tussle.stage.Platform;
import com.tussle.stage.SolidSurface;
import com.tussle.stage.StageElement;

public class LibgdxTussleMain extends ApplicationAdapter {

	Stage stage;
	InputMultiplexer inputs;
	Controller[] controllers;

	public LibgdxTussleMain(KeyboardController[] ctrl)
	{
		controllers = ctrl;
		inputs = new InputMultiplexer();
		for (KeyboardController g : ctrl)
		{
			inputs.addProcessor(g);
		}
	}
	
	@Override
	public void create () {
		stage = new Stage(new ExtendViewport(640, 480));
		Fighter fighter = new Fighter(controllers[0], "core/assets/sprites/default_franchise_icon.png",
				new Vector2(300, 300));
		float[] testVertices = {0, 10, 250, 0, 500, 10, 250, 20};
		StageElement surface = new SolidSurface(new Polygon(testVertices),
				"core/assets/sprites/default_franchise_icon.png");
		StageElement platform = new Platform(new Vector2(100, 200), new Vector2(400, 200),
				4.0f, "core/assets/sprites/default_franchise_icon.png");
		stage.addActor(fighter);
		stage.addActor(surface);
		stage.addActor(platform);
		stage.setDebugAll(true);
		fighter.onSpawn();
		Gdx.input.setInputProcessor(inputs);
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		for (Controller controller : controllers)
		{
			controller.pumpBuffer();
		}
	}
	
	@Override
	public void dispose () {
	}

	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}
}
