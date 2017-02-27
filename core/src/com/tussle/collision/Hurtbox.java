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

package com.tussle.collision;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.tussle.Subaction;
import com.tussle.fighter.Fighter;
import com.tussle.main.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class Hurtbox extends Actor
{
	Vector2 start;
	Vector2 end;
	float radius;
	ShapeRenderer debugDrawer;
	ArrayList<Armor> armors;

	public Hurtbox(Vector2 s, Vector2 e, float rad)
	{
		start = s;
		end = e;
		radius = rad;
		setBounds(start.x-radius, start.y-radius,
				radius*2+end.x-start.x, radius*2+end.y-start.y);
		setOrigin(Align.center);
		debugDrawer = new ShapeRenderer();
		debugDrawer.setAutoShapeType(true);
	}

	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		batch.end();
		debugDrawer.begin();
		debugDrawer.setProjectionMatrix(this.getStage().getCamera().combined);
		debugDrawer.setColor(1, 0, 0, 1);
		debugDrawer.circle(start.x, start.y, radius);
		debugDrawer.circle(end.x, end.y, radius);
		debugDrawer.rectLine(start, end, radius);
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}

	public Vector2 getStart()
	{
		return start.cpy();
	}

	public Vector2 getEnd()
	{
		return end.cpy();
	}

	public float getRadius()
	{
		return radius;
	}

	public List<Armor> getArmors()
	{
		return (List)armors.clone();
	}

	public void onHit(Hitbox hbox, List<Subaction> subactions)
	{
		Fighter fighter = (Fighter)(this.getParent());
		BiPredicate<Hitbox, List<Subaction>> aggregateFilter =
				(Hitbox h, List<Subaction> subacts) -> true; //Yay lambdas
		for (Armor armor : fighter.getArmors())
			aggregateFilter = armor.and(aggregateFilter);
		for (Armor armor : getArmors())
			aggregateFilter = armor.and(aggregateFilter);
		if(aggregateFilter.test(hbox, subactions))
		{
			for (Subaction subaction : subactions)
				subaction.apply(fighter);
		}
	}
}
