package com.tussle.fighter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
public class Fighter extends Group
{
	Controller controller;
	String baseDir;
	Texture texture;
	Sprite sprite;

	float xVelocity;
	float yVelocity;
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

	public Fighter(Controller ctrl, String path, Vector2 center)
	{
		texture = new Texture(path);
		sprite = new Sprite(texture);
		controller = ctrl;
		baseDir = path;
		setSize(sprite.getWidth(), sprite.getHeight());
		setOrigin(Align.center);
		setPosition(center.x, center.y, Align.center);
	}

	public void draw(Batch batch, float parentAlpha)
	{
		sprite.setOriginCenter();
		sprite.setFlip(facing < 0, false);
		sprite.setRotation(angle);
		sprite.setPosition(getX(), getY());
		sprite.draw(batch, parentAlpha);
	}

	public void onSpawn()
	{
		setActionState(new IdleState());
		setCollisionBox();
		//Currently (mostly ) stubbed
	}

	public void onDeath()
	{
		//Currently stubbed
	}

	public void act(float delta)
	{
		super.act(delta);
		//Move self
		List<StageElement> stageElements = new LinkedList<>();
		for (Actor act : getStage().getActors())
		{
			if (act instanceof StageElement)
				stageElements.add((StageElement)act);
		}
		StageElement[] elementArray = stageElements.toArray(new StageElement[0]);
		float limit = 1.0f;
		Vector2 velocity = getVelocity();
		limit = collisionBox.checkMovement(velocity, elementArray);
		setX(getX()+xVelocity*limit);
		setY(getY()+yVelocity*limit);
		xVelocity = Utility.addFrom(xVelocity, -0.5f, preferredXVelocity);
		yVelocity = Utility.addFrom(yVelocity, -0.5f, preferredYVelocity);
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
		float[] vertices = {rect.getX(), rect.getY(), rect.getX()+rect.getWidth(), rect.getY(),
				rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight(),
				rect.getX(), rect.getY()+rect.getHeight()};
		collisionBox = new ECB(vertices);
	}

	public int getFacing()
	{
		return facing;
	}

	public Vector2 getVelocity()
	{
		return new Vector2(xVelocity, yVelocity);
	}

	public void setVelocity(Vector2 newVelocity)
	{
		xVelocity = newVelocity.x;
		yVelocity = newVelocity.y;
	}

	public void setFacing(int newFacing)
	{
		facing = newFacing;
	}

	public void setPreferredXVelocity(float newVelocity)
	{
		preferredXVelocity = newVelocity;
	}

	public Controller getController()
	{
		return controller;
	}
}
