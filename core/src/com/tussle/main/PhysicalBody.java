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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.tussle.collision.*;
import com.tussle.fighter.Terminable;

import java.util.*;

/**
 * Created by eaglgenes101 on 3/6/17.
 */
public abstract class PhysicalBody extends Group
{
	String baseDir;
	Texture texture;
	protected Sprite sprite;

	Vector2 velocity;
	float preferredXVelocity;
	float preferredYVelocity;

	List<Armor> armors;
	Set<HitboxLock> hitboxLocks;

	protected ShapeRenderer debugDrawer;

	public PhysicalBody(String path, Vector2 center)
	{
		armors = new LinkedList<>();
		hitboxLocks = Collections.newSetFromMap(new WeakHashMap<HitboxLock, Boolean>());
		if (path != null)
		{
			texture = new Texture(path);
			sprite = new Sprite(texture);
			baseDir = path;
			setSize(sprite.getWidth(), sprite.getHeight());
		}
		else
		{
			texture = null;
			sprite = null;
			baseDir = null;
		}
		velocity = new Vector2();
		setOrigin(Align.center);
		setPosition(center.x, center.y, Align.center);
		debugDrawer = new ShapeRenderer();
		debugDrawer.setAutoShapeType(true);
	}

	public void act(float delta)
	{
		super.act(delta);
		for (Action action : getActions())
		{
			if (action instanceof Terminable)
			{
				for (HitboxLock hitboxLocks : ((Terminable) action).getHitboxLocks())
				{
					for (Hitbox hitbox : hitboxLocks.getHitboxes())
					{
						hitbox.setPosition(getX(Align.center), getY(Align.center));
						hitbox.setRotation(getRotation());
						hitbox.setScale(getScaleY());
						hitbox.setFlipped(getScaleX() < 0);
					}
				}

				for (Hurtbox hurtbox : ((Terminable) action).getHurtboxes())
				{
					hurtbox.setPosition(getX(Align.center), getY(Align.center));
					hurtbox.setRotation(getRotation());
					hurtbox.setScale(getScaleY());
					hurtbox.setFlipped(getScaleX() < 0);
				}
			}
		}
	}

	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		if (sprite != null)
		{
			sprite.setOriginCenter();
			sprite.setFlip(getScaleX() < 0, false);
			sprite.setRotation(getRotation());
			sprite.setPosition(getX(), getY());
			sprite.draw(batch, parentAlpha);
		}
		batch.end();
		debugDrawer.begin();
		debugDrawer.setProjectionMatrix(this.getStage().getCamera().combined);
		for (Action action : getActions())
		{
			if (action instanceof Terminable)
			{
				debugDrawer.setColor(0, 0, 1, 1);
				for (HitboxLock hitboxLocks : ((Terminable) action).getHitboxLocks())
				{
					for (Hitbox hitbox : hitboxLocks.getHitboxes())
					{
						debugDrawer.circle(hitbox.getTransformedStart().x, hitbox.getTransformedStart().y,
								hitbox.getTransformedRadius());
						debugDrawer.circle(hitbox.getTransformedEnd().x, hitbox.getTransformedEnd().y,
								hitbox.getTransformedRadius());
						debugDrawer.rectLine(hitbox.getTransformedStart(), hitbox.getTransformedEnd(),
								hitbox.getTransformedRadius() * 2);
					}
				}

				debugDrawer.setColor(0, 1, 1, 1);
				for (Hurtbox hurtbox : ((Terminable) action).getHurtboxes())
				{
					debugDrawer.circle(hurtbox.getTransformedStart().x, hurtbox.getTransformedStart().y,
							hurtbox.getTransformedRadius());
					debugDrawer.circle(hurtbox.getTransformedEnd().x, hurtbox.getTransformedEnd().y,
							hurtbox.getTransformedRadius());
					debugDrawer.rectLine(hurtbox.getTransformedStart(), hurtbox.getTransformedEnd(),
							hurtbox.getTransformedRadius() * 2);
				}
			}
		}
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}

	public boolean hitboxLocked(HitboxLock lock)
	{
		return hitboxLocks.contains(lock);
	}

	public void addHitboxLock(HitboxLock lock)
	{
		hitboxLocks.add(lock);
	}

	public LinkedHashSet<HitboxLock> getHitboxGroups()
	{
		LinkedHashSet<HitboxLock> superset = new LinkedHashSet<>();
		for (Action action : getActions())
			if (action instanceof Terminable)
				superset.addAll(((Terminable)action).getHitboxLocks());
		return superset;
	}

	public LinkedHashSet<Hurtbox> getHurtboxes()
	{
		LinkedHashSet<Hurtbox> superset = new LinkedHashSet<>();
		for (Action action : getActions())
			if (action instanceof Terminable)
				superset.addAll(((Terminable)action).getHurtboxes());
		return superset;
	}

	public int getFacing()
	{
		return getScaleX()>0?1:-1;
	}

	public Vector2 getVelocity()
	{
		return velocity;
	}

	public float getXVelocity()
	{
		return velocity.x;
	}

	public float getYVelocity()
	{
		return velocity.y;
	}

	public void setVelocity(Vector2 newVelocity)
	{
		velocity = newVelocity.cpy();
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
		setScaleX(getScaleY()*newFacing);
	}

	public void setPreferredXVelocity(float newVelocity)
	{
		preferredXVelocity = newVelocity;
	}

	public void setPreferredYVelocity(float newVelocity)
	{
		preferredYVelocity = newVelocity;
	}

	public void xAccel(float factor)
	{
		velocity.x = Utility.addFrom(velocity.x, -factor, preferredXVelocity);
	}

	public void yAccel(float factor)
	{
		velocity.y = Utility.addTowards(velocity.y, -factor, preferredYVelocity);
	}

	public List<Armor> getArmors()
	{
		return armors;
	}

	//Return if the lock is in the set
	public boolean doesHit(HitboxLock lock)
	{
		return !hitboxLocks.contains(lock);
	}
}
