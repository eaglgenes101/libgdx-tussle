package com.tussle.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tussle.main.Utility;

/**
 * Created by eaglgenes101 on 2/13/17.
 */
public class Platform extends StageElement
{
	Polygon hitSurface;
	Vector2 normal;
	float tolerance;

	public Platform(Vector2 start, Vector2 end, float tolerance, String path)
	{
		super(path, start.cpy().add(end.cpy()).scl(0.5f));
		float[] constructedSurface = new float[8];
		Vector2 difference = end.cpy().sub(start);
		normal = new Vector2(-difference.y, difference.x).setLength2(1.0f);
		constructedSurface[0] = start.x;
		constructedSurface[1] = start.y;
		constructedSurface[2] = start.x-normal.x*tolerance*2;
		constructedSurface[3] = start.y-normal.y*tolerance*2;
		constructedSurface[4] = end.x-normal.x*tolerance*2;
		constructedSurface[5] = end.y-normal.y*tolerance*2;
		constructedSurface[6] = end.x;
		constructedSurface[7] = end.y;
		hitSurface = new Polygon(constructedSurface);
	}

	public Intersector.MinimumTranslationVector getNormal(Polygon ecb)
	{
		Intersector.MinimumTranslationVector nudge = new Intersector.MinimumTranslationVector();
		if (Intersector.overlapConvexPolygons(ecb.getTransformedVertices(),
				hitSurface.getTransformedVertices(), nudge))
		{
			if (nudge.normal.dot(normal) > 0 && nudge.normal.dot(normal)*nudge.depth <= tolerance)
				return nudge;
			else return null;
		}
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
				hitSurface.getTransformedVertices(), nudge) &&
				nudge.normal.dot(normal) > 0)
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
		if (velocity.dot(normal) < 0)
		{
			return Utility.pathPolygonIntersects(velocity, ecb.getTransformedVertices(),
					hitSurface.getTransformedVertices());
		}
		else
			return 1.0f;
	}

	public void draw(Batch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);
		batch.end();
		debugDrawer.begin();
		debugDrawer.setColor(0, 0, 1, 1);
		debugDrawer.polygon(hitSurface.getTransformedVertices());
		drawDebug(debugDrawer);
		debugDrawer.end();
		batch.begin();
	}
}
