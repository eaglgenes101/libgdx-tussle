package com.tussle.fighter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tussle.collision.ECB;
import com.tussle.collision.Hitbox;
import com.tussle.collision.Hurtbox;
import com.tussle.input.Controller;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
public class Fighter extends Group
{
	Controller controller;
	String baseDir;

	float xVelocity;
	float yVelocity;
	float preferredXVelocity;
	float preferredYVelocity;
	int facing;

	ActionState currentState;

	HashMap<String, Double> stats;
	HashMap<String, Integer> counts;

	ECB collisionBox;
	Set<Hitbox> hitboxes;
	Set<Hurtbox> hurtboxes;
	//Array<Armor> armors;
	Set<Hitbox> hitboxLocks;

	//Ledge currentLedge;

	int jumps;
	int airdodges;
	double damage;

	int hitlag_frames;

	public Fighter(Controller ctrl, String path)
	{
		controller = ctrl;
		baseDir = path;
	}

	public void onSpawn()
	{
		//Currently stubbed
	}

	public void onDeath()
	{
		//Currently stubbed
	}

	public void act(float delta)
	{
		super.act(delta);
		//Move self
		setX(getX()+xVelocity);
		setY(getY()+yVelocity);

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

	public int getFacing()
	{
		return facing;
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
