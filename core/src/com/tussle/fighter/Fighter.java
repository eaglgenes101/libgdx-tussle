package com.tussle.fighter;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.tussle.Collision.ECB;
import com.tussle.Collision.Hitbox;
import com.tussle.Collision.Hurtbox;
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

	double xVelocity;
	double yVelocity;
	double preferredXVelocity;
	double preferredYVelocity;
	int facing;

	ActionState currentState;

	HashMap<String, Double> stats;
	HashMap<String, Integer> counts;

	ECB collisionBox;
	Set<Hitbox> hitboxes;
	Set<Hurtbox> hurtboxes;
	Array<Armor> armors;
	Set<Hitbox> hitboxLocks;

	Ledge currentLedge;

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

	}

	public void onDeath()
	{

	}

	public void act(float delta)
	{

	}
}
