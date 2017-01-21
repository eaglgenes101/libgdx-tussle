package com.tussle.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tussle.input.BufferChecker;
import com.tussle.input.Controller;
import com.tussle.input.InputState;
import com.tussle.input.InputToken;
import com.tussle.input.KeyboardController;

public class LibgdxTussleMain extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	InputMultiplexer inputs;
	Controller[] controllers;
	float x;
	float y;
	BufferChecker RightCheck = new BufferChecker(4, new InputToken(1, InputState.HOR_MOVEMENT));
	BufferChecker LeftCheck = new BufferChecker(4, new InputToken(-1, InputState.HOR_MOVEMENT));
	BufferChecker UpCheck = new BufferChecker(4, new InputToken(1, InputState.VERT_MOVEMENT));
	BufferChecker DownCheck = new BufferChecker(4, new InputToken(-1, InputState.VERT_MOVEMENT));
	BufferChecker[] checkers = {RightCheck, LeftCheck, UpCheck, DownCheck};

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
		batch = new SpriteBatch();
		img = new Texture("core/assets/sprites/default_franchise_icon.png");
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 640, 480);
		Gdx.input.setInputProcessor(inputs);
		x = 0;
		y = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.begin();
		batch.draw(img, x, y);
		batch.end();
		for (Controller controller : controllers)
		{
			controller.pumpBuffer();
			int lastValue = controller.matchInput(checkers);
			switch (lastValue)
			{
				case 0:
					x += 1;
					break;
				case 1:
					x -= 1;
					break;
				case 2:
					y += 1;
					break;
				case 3:
					y -= 1;
					break;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
