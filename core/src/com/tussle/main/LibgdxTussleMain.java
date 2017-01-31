package com.tussle.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tussle.fighter.Fighter;
import com.tussle.input.BufferChecker;
import com.tussle.input.Controller;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;
import com.tussle.input.KeyboardController;

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
		stage = new Stage(new ScreenViewport(new OrthographicCamera(640, 480)));
		stage.addActor(new Fighter(controllers[0], "core/assets/sprites/default_franchise_icon.png"));
		Gdx.input.setInputProcessor(inputs);
	}

	@Override
	public void render () {
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
}
