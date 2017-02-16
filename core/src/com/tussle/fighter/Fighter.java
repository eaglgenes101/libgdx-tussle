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

package com.tussle.fighter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.tussle.actionstate.ActionState;
import com.tussle.actionstate.IdleState;
import com.tussle.collision.ECB;
import com.tussle.collision.Hitbox;
import com.tussle.collision.Hurtbox;
import com.tussle.input.Controller;
import com.tussle.main.Utility;
import com.tussle.stage.StageElement;

import java.util.*;

public class Fighter extends Group
{
	Controller controller;
	String baseDir;
	Texture texture;
	Sprite sprite;

	Vector2 startCenter;
	Vector2 leg;
	Vector2 foot;
	Vector2 velocity;
	float preferredXVelocity;
	float preferredYVelocity;
	int facing;
	float angle;

	ActionState currentState;

	HashMap<String, Double> stats;
	HashMap<String, Integer> counts;

	ECB collisionBox;
	Set<Hitbox> hitboxes;
	Set<Hurtbox> hurtboxes;
	List<Armor> armors;
	Set<Hitbox> hitboxLocks;

	//Ledge currentLedge;

	int jumps;
	int airdodges;
	double damage;

	int hitlag_frames;

	ShapeRenderer debugDrawer;
	float elasticity = 0.0f;
	ArrayList<Vector2> currentNormals;

	public Fighter(Controller ctrl, String path, Vector2 center)
	{
		velocity = new Vector2();
		texture = new Texture(path);
		sprite = new Sprite(texture);
		controller = ctrl;
		baseDir = path;
		startCenter = center;
		debugDrawer = new ShapeRenderer();
		debugDrawer.setAutoShapeType(true);
		currentNormals = new ArrayList<>();
		leg = new Vector2();
		foot = new Vector2();
	}

	public void draw(Batch batch, float parentAlpha)
	{
		sprite.setOriginCenter();
		sprite.setFlip(facing < 0, false);
		sprite.setRotation(angle);
		sprite.setPosition(getX(), getY());
		sprite.draw(batch, parentAlpha);
		batch.end();
		debugDrawer.begin();
		debugDrawer.setProjectionMatrix(this.getStage().getCamera().combined);
		debugDrawer.setColor(0, 0, 1, 1);
		debugDrawer.polygon(collisionBox.getTransformedVertices());
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}

	public void onSpawn()
	{
		currentNormals.clear();
		setSize(sprite.getWidth(), sprite.getHeight());
		setOrigin(Align.center);
		setPosition(startCenter.x, startCenter.y, Align.center);
		leg.set(getX(Align.center), getY(Align.bottom)+4);
		foot.set(getX(Align.center), getY(Align.bottom)-4);
		interruptActionState(new IdleState());
		setCollisionBox();
		setVelocity(new Vector2());
		setPreferredXVelocity(0.0f);
		preferredYVelocity = -30;
	}

	public void onDeath()
	{
		onSpawn();
	}

	public void act(float delta)
	{
		super.act(delta);
		//Move self
		currentNormals.clear();
		List<StageElement> stageElements = new LinkedList<>();
		for (Actor act : getStage().getActors())
			if (act instanceof StageElement)
				stageElements.add((StageElement)act);
		collisionBox.setCenter(getX(Align.center), getY(Align.center));
		StageElement[] elementArray = stageElements.toArray(new StageElement[0]);
		velocity.set(Utility.addFrom(velocity.x, -0.5f, preferredXVelocity),
				Utility.addFrom(velocity.y, -0.5f, preferredYVelocity));
		collisionBox.checkMovement(velocity, elementArray);
		collisionBox.eject(velocity, elementArray, elasticity);
		Intersector.MinimumTranslationVector[] normals = collisionBox.getNormals(elementArray);
		for (Intersector.MinimumTranslationVector normal : normals)
			if (normal != null)
				currentNormals.add(normal.normal);
		setPosition(collisionBox.getX(), collisionBox.getY(), Align.center);
		leg.set(getX(Align.center), getY(Align.bottom)+4);
		foot.set(getX(Align.center), getY(Align.bottom)-4);
		System.out.println(isGrounded());
	}

	public void setActionState(ActionState newState)
	{
		if (currentState != null) {
			currentState.onEnd(newState);
			removeAction(currentState);
		}
		currentState = newState;
		addAction(newState);
		newState.onStart();
	}

	public void interruptActionState(ActionState newState)
	{
		if (currentState != null) {
			removeAction(currentState);
		}
		currentState = newState;
		addAction(newState);
		newState.onStart();
	}

	public void addStatusEffect(StatusEffect newEffect)
	{
		newEffect.onStart();
		this.addAction(newEffect);
	}

	public void setCollisionBox(ECB ecb)
	{
		collisionBox = ecb;
	}

	public void setCollisionBox()
	{
		Rectangle rect = this.sprite.getBoundingRectangle();
		float[] vertices = {0, -rect.getHeight()/2, rect.getWidth()/2, 0,
				0, rect.getHeight()/2, -rect.getWidth()/2, 0};
		collisionBox = new ECB(vertices);
	}

	public int getFacing()
	{
		return facing;
	}

	public Vector2 getVelocity()
	{
		return velocity;
	}

	public void setVelocity(Vector2 newVelocity)
	{
		velocity = newVelocity;
	}

	public void setXVelocity(float x)
	{
		velocity.x = x;
	}

	public void setYVelocity(float y)
	{
		velocity.y = y;
	}

	public void setFacing(int newFacing)
	{
		facing = newFacing;
	}

	public void setPreferredXVelocity(float newVelocity)
	{
		preferredXVelocity = newVelocity;
	}

	public boolean isGrounded()
	{
		List<StageElement> stageElements = new LinkedList<>();
		for (Actor act : getStage().getActors())
			if (act instanceof StageElement)
				stageElements.add((StageElement)act);
		StageElement[] elementArray = stageElements.toArray(new StageElement[0]);
		for (StageElement surface : elementArray)
			if (surface.isGrounded(leg, foot, velocity.y))
				return true;
		return false;
	}

	public Controller getController()
	{
		return controller;
	}
}
