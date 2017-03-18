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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tussle.collision.*;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.input.Controller;
import com.tussle.input.KeyboardController;
import com.tussle.stage.*;

import java.util.*;

public class LibgdxTussleMain extends ApplicationAdapter {

	Stage stage;
	InputMultiplexer inputs;
	Controller[] controllers;
	int screenWidth = 640;
	int screenHeight = 480;
	float zoomScale = 1.0f;
	float leftBound = -1000f;
	float rightBound = 1000f;
	float bottomBound = -1000f;
	float topBound = 1000f;
	int frameCount = 0;

	public LibgdxTussleMain(KeyboardController[] ctrl)
	{
		controllers = ctrl.clone();
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
				new Vector2(0, 300));
		float[] testVertices = {-300, 0, -100, -10, 100, -10, 300, 0, 100, 10, -100, 10};
		StageElement surface = new SolidSurface(new Polygon(testVertices),
				"core/assets/sprites/default_franchise_icon.png");
		StageElement platform1 = new Platform(new Vector2(-300, 200), new Vector2(-200, 200),
				4.0f, "core/assets/sprites/default_franchise_icon.png");
		StageElement platform2 = new Platform(new Vector2(200, 200), new Vector2(300, 200),
				4.0f, "core/assets/sprites/default_franchise_icon.png");
		StageElement leftLedge = new Ledge(new Rectangle(-320, -20, 20, 20), 1);
		StageElement rightLedge = new Ledge(new Rectangle(300, -20, 20, 20), -1);
		StageElement target = new BreakableTarget(new Vector2(), new Vector2(0, 100), 40,
				"core/assets/sprites/default_franchise_icon.png");
		stage.addActor(fighter);
		stage.addActor(surface);
		stage.addActor(platform1);
		stage.addActor(platform2);
		stage.addActor(leftLedge);
		stage.addActor(rightLedge);
		stage.addActor(target);
		stage.setDebugAll(true);
		fighter.onSpawn();
		Gdx.input.setInputProcessor(inputs);
	}

	@Override
	public void render ()
	{
		focusCamera();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		for (Controller controller : controllers)
		{
			controller.pumpBuffer();
		}
		frameCount++;
	}

	//Do a first pass for collisions, eliminating unlikely clanks
	private Map<Terminable, LinkedHashSet<Terminable>> getProbableClanks()
	{
		LinkedHashMap<Terminable, Rectangle> boundingBoxes = new LinkedHashMap<>();
		for (Actor actor : stage.getActors())
		{
			if (actor instanceof PhysicalBody)
			{
				for (Action action : actor.getActions())
				{
					if (action instanceof Terminable)
					{
						boundingBoxes.put((Terminable)action, ((Terminable)action).getHitboxBounds());
					}
				}
			}
		}
		HashMap<Terminable, LinkedHashSet<Terminable>> returnMap = new HashMap<>();
		for (Terminable first : boundingBoxes.keySet())
			for (Terminable second : boundingBoxes.keySet())
				if (boundingBoxes.get(first) != null && boundingBoxes.get(second) != null)
					if (boundingBoxes.get(first).overlaps(boundingBoxes.get(second)))
					{
						if (!returnMap.containsKey(first))
							returnMap.put(first, new LinkedHashSet<>());
						returnMap.get(first).add(second);
					}
		return returnMap;
	}

	//Do a first pass for collisions, eliminating unlikely hits
	private Map<Terminable, LinkedHashSet<Terminable>> getProbableHits()
	{
		HashMap<Terminable, Rectangle> boundingBoxes = new HashMap<>();
		LinkedHashMap<Terminable, Rectangle> hurtBounds = new LinkedHashMap<>();
		for (Actor actor : stage.getActors())
		{
			if (actor instanceof PhysicalBody)
			{
				for (Action action : actor.getActions())
				{
					if (action instanceof Terminable)
					{
						boundingBoxes.put((Terminable)action, ((Terminable)action).getHitboxBounds());
						hurtBounds.put((Terminable)action, ((Terminable)action).getHurtboxBounds());
					}
				}
			}
		}
		HashMap<Terminable, LinkedHashSet<Terminable>> returnMap = new HashMap<>();
		for (Terminable first : boundingBoxes.keySet())
			for (Terminable second : hurtBounds.keySet())
				if (boundingBoxes.get(first) != null && hurtBounds.get(second) != null)
					if (boundingBoxes.get(first).overlaps(hurtBounds.get(second)))
					{
						if (!returnMap.containsKey(first))
							returnMap.put(first, new LinkedHashSet<>());
						returnMap.get(first).add(second);
					}
		return returnMap;
	}

	private void handleClanks()
	{
		Map<Terminable, LinkedHashSet<Terminable>> probableClanks = getProbableClanks();
		Map<PhysicalBody, LinkedHashSet<HitboxLock>> lockPairs = new HashMap<>();
		Map<Hitbox, LinkedList<Hitbox>> doClankMap = new LinkedHashMap<>();
		for (Terminable ourAction : probableClanks.keySet())
		{
			for (Terminable otherAction : probableClanks.get(ourAction))
			{
				for (HitboxLock otherLock : otherAction.getHitboxLocks())
				{
					if (!ourAction.getBody().hitboxLocked(otherLock))
					{
						for (HitboxLock ourLock : ourAction.getHitboxLocks())
						{
							ClankPair pair = ourLock.getClanks(otherLock);
							if (pair != null && pair.first.doesClank(pair.second))
							{
								if (!lockPairs.containsKey(ourAction.getBody()))
									lockPairs.put(ourAction.getBody(), new LinkedHashSet<>());
								if (!doClankMap.containsKey(pair.first))
									doClankMap.put(pair.first, new LinkedList<>());
								lockPairs.get(ourAction.getBody()).add(otherLock);
								doClankMap.get(pair.first).add(pair.second);
							}
						}
					}
				}
			}
		}
		//TODO: Add callback code
	}

	private void focusCamera()
	{
		float xMin = Float.POSITIVE_INFINITY;
		float xMax = Float.NEGATIVE_INFINITY;
		float yMin = Float.POSITIVE_INFINITY;
		float yMax = Float.NEGATIVE_INFINITY;
		for (Actor actor : stage.getActors())
		{
			boolean doDie = false;
			if (actor.getX(Align.left) < xMin && xMin >= leftBound)
			{
				xMin = Math.max(leftBound, actor.getX(Align.left));
				if (actor instanceof Fighter && actor.getX(Align.right) < leftBound)
					doDie = true;
			}
			if (actor.getX(Align.right) > xMax && xMax <= rightBound)
			{
				xMax = Math.min(rightBound, actor.getX(Align.right));
				if (actor instanceof Fighter && actor.getX(Align.left) > rightBound)
					doDie = true;
			}
			if (actor.getY(Align.bottom) < yMin && yMin >= bottomBound)
			{
				yMin = Math.max(bottomBound, actor.getY(Align.bottom));
				if (actor instanceof Fighter && actor.getY(Align.bottom) < bottomBound)
					doDie = true;
			}
			if (actor.getY(Align.top) > yMax && yMax <= topBound)
			{
				yMax = Math.min(topBound, actor.getY(Align.top));
				if (actor instanceof Fighter && actor.getY(Align.top) > topBound)
					doDie = true;
			}
			if (doDie/* || (actor instanceof Fighter && frameCount % 360 == 0)*/)
				((Fighter)actor).onDeath();
		}
		float centerx = (xMin+xMax)/2;
		float centery = (yMin+yMax)/2;
		float width = xMax-xMin+80;
		float height = yMax-yMin+80;
		zoomScale = Math.max(Math.max(width/screenWidth, height/screenHeight), zoomScale*.98f);
		stage.getCamera().position.set(centerx, centery, stage.getCamera().position.z);
		stage.getViewport().setWorldSize(zoomScale*screenWidth, zoomScale*screenHeight);
		stage.getViewport().apply();
	}
	
	@Override
	public void dispose () {
		stage.dispose();
	}

	public void resize(int width, int height)
	{
		screenWidth = width;
		screenHeight = height;
		stage.getViewport().update(width, height, true);
	}
}
