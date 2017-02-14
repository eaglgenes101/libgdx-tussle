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

package com.tussle.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tussle.main.Utility;

public class SolidSurface extends StageElement
{
	Polygon hitSurface;

	public SolidSurface(Polygon surface, String path)
	{
		super(path, surface.getBoundingRectangle().getCenter(new Vector2()));
		hitSurface = surface;
	}

	public Intersector.MinimumTranslationVector getNormal(Polygon ecb)
	{
		Intersector.MinimumTranslationVector nudge = new Intersector.MinimumTranslationVector();
		if (Intersector.overlapConvexPolygons(ecb.getTransformedVertices(),
				hitSurface.getTransformedVertices(), nudge))
			return nudge;
		else return null;
	}

	//Returns the deviation between what is expected and the position to take
	public Vector2 checkShape(Polygon oldECB, Polygon newECB)
	{
		Intersector.MinimumTranslationVector nudge = new Intersector.MinimumTranslationVector();
		Vector2 displacement = Utility.medianDifference(oldECB, newECB);
		Polygon interECB = new Polygon(newECB.getTransformedVertices());
		interECB.translate(-displacement.x, -displacement.y);
		if (Intersector.overlapConvexPolygons(interECB.getTransformedVertices(),
				hitSurface.getTransformedVertices(), nudge))
		{
			interECB.translate(nudge.normal.x*nudge.depth, nudge.normal.y*nudge.depth);
			Intersector.MinimumTranslationVector contact = getNormal(interECB);
			if (contact != null && contact.normal.dot(displacement) < 0)
				return displacement.sub(contact.normal.x*contact.depth, contact.normal.y*contact.depth);
			else return displacement;
		}
		else
		{
			float travelPortion = checkMovement(displacement, interECB);
			if (travelPortion < 1.0f)
				return displacement.scl(travelPortion-1);
			else return Vector2.Zero;
		}
	}

	public float checkMovement(Vector2 velocity, Polygon ecb)
	{
		return Utility.pathPolygonIntersects(velocity, ecb.getTransformedVertices(),
				hitSurface.getTransformedVertices());
	}

	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		batch.end();
		debugDrawer.begin();
		debugDrawer.setProjectionMatrix(this.getStage().getCamera().combined);
		debugDrawer.setColor(0, 0, 1, 1);
		debugDrawer.polygon(hitSurface.getTransformedVertices());
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}
}
