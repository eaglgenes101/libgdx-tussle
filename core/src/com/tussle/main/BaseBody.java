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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.tussle.collision.*;
import com.tussle.fighter.Terminable;

import java.util.*;

/**
 * Created by eaglgenes101 on 3/6/17.
 */
public abstract class BaseBody extends Group
{
	String baseDir;
	Texture texture;
	protected Sprite sprite;

	Vector2 velocity;
	float preferredXVelocity;
	float preferredYVelocity;

	List<Armor> armors;
	WeakHashMap<HitboxLock, HitboxLock> hitboxLocks;
	HashMap<Terminable, HashSet<Hitbox>> hitboxes;
	HashMap<Terminable, HashSet<Hurtbox>> hurtboxes;

	protected ShapeRenderer debugDrawer;

	public BaseBody(String path, Vector2 center)
	{
		armors = new LinkedList<>();
		hitboxLocks = new WeakHashMap<>();
		hitboxes = new HashMap<>();
		hurtboxes = new HashMap<>();
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
		for (Set<Hitbox> hitboxSet : hitboxes.values())
		{
			for (Hitbox hitbox : hitboxSet)
			{
				hitbox.setPosition(getX(Align.center), getY(Align.center));
				hitbox.setRotation(getRotation());
				hitbox.setScale(getScaleY());
				hitbox.setFlipped(getScaleX() < 0);
			}
		}
		for (Set<Hurtbox> hurtboxSet : hurtboxes.values())
		{
			for (Hurtbox hurtbox :hurtboxSet)
			{
				hurtbox.setPosition(getX(Align.center), getY(Align.center));
				hurtbox.setRotation(getRotation());
				hurtbox.setScale(getScaleY());
				hurtbox.setFlipped(getScaleX() < 0);
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
		debugDrawer.setColor(0, 0, 1, 1);
		for (Set<Hitbox> hitboxSet : hitboxes.values())
		{
			for (Hitbox hitbox : hitboxSet)
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
		for (Set<Hurtbox> hurtboxSet : hurtboxes.values())
		{
			for (Hurtbox hurtbox : hurtboxSet)
			{
				debugDrawer.circle(hurtbox.getTransformedStart().x, hurtbox.getTransformedStart().y,
						hurtbox.getTransformedRadius());
				debugDrawer.circle(hurtbox.getTransformedEnd().x, hurtbox.getTransformedEnd().y,
					hurtbox.getTransformedRadius());
				debugDrawer.rectLine(hurtbox.getTransformedStart(), hurtbox.getTransformedEnd(),
						hurtbox.getTransformedRadius() * 2);
			}
		}
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}

	public void addHitbox(Hitbox hitbox, Terminable lifetime)
	{
		if (!hitboxes.containsKey(lifetime))
		{
			hitboxes.put(lifetime, new HashSet<>());
		}
		hitboxes.get(lifetime).add(hitbox);
	}

	public void addHurtbox(Hurtbox hurtbox, Terminable lifetime)
	{
		if (!hurtboxes.containsKey(lifetime))
		{
			hurtboxes.put(lifetime, new HashSet<>());
		}
		hurtboxes.get(lifetime).add(hurtbox);
	}

	public void removeHitbox(Hitbox hitbox)
	{
		for (Terminable terminable : hitboxes.keySet())
		{
			if (hitboxes.get(terminable).remove(hitbox))
				break;
		}
	}

	public void removeHurtbox(Hurtbox hurtbox)
	{
		for (Terminable terminable : hurtboxes.keySet())
		{
			if (hurtboxes.get(terminable).remove(hurtbox))
				break;
		}
	}

	public void removeTerminable(Terminable terminable)
	{
		hitboxes.remove(terminable);
		hurtboxes.remove(terminable);
	}

	public Set<Hitbox> getHitboxes()
	{
		Set<Hitbox> superset = new HashSet<>();
		for (Set<Hitbox> set : hitboxes.values())
		{
			superset.addAll(set);
		}
		return superset;
	}

	public Set<Hurtbox> getHurtboxes()
	{
		Set<Hurtbox> superset = new HashSet<>();
		for (Set<Hurtbox> set : hurtboxes.values())
		{
			superset.addAll(set);
		}
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
}
