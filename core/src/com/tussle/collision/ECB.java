package com.tussle.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.tussle.stage.StageElement;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
public class ECB extends Polygon
{
	public ECB(float[] vertices)
	{
		super(vertices);
	}

	// Returns the factor by which the specified movement can be done
	public float checkMovement(Vector2 Velocity, StageElement[] surfaces)
	{
		float minDist = 1.0f;
		for (StageElement surface : surfaces)
		{
			float val = surface.checkMovement(Velocity, this);
			if (val == 0.0f)
				return 0.0f;
			else if (val < 1.0f)
				minDist = Math.min(val, minDist);
		}
		return minDist;
	}

	//Checks the polygon shape, and changes the polygon shape
	//Returns the deviation from the requested position
	//Might glitch out if multiple stage surfaces intersect in different directions
	public Vector2 checkShape(float[] newVertices, StageElement[] surfaces)
	{
		Polygon newShape = new Polygon(newVertices);
		Vector2 displacement = new Vector2(); //The current displacement from the expected position
		int maxIterations = 10;
		for (int i = 0; i < maxIterations; i++)
		{
			float len2 = 0.0f;
			Vector2 maxDisplacement = new Vector2(); //Current direction to displace
			for (StageElement surface : surfaces)
			{
				Vector2 checkDisp = surface.checkShape(this, newShape);
				if (checkDisp.len2() > len2)
				{
					len2 = checkDisp.len2();
					maxDisplacement = checkDisp;
				}
			}
			if (maxDisplacement.equals(Vector2.Zero))
				return displacement; //We can stop
			else
			{
				displacement = displacement.add(maxDisplacement); //Add displacement
				newShape.setPosition(newShape.getX()+maxDisplacement.x,
						newShape.getY()+maxDisplacement.y); //Move the prospective polygon
			}
		}
		return null; //We give up, initiate crushing routine
	}

	public Intersector.MinimumTranslationVector[] getNormals(StageElement[] surfaces)
	{
		Intersector.MinimumTranslationVector[] vectors =
				new Intersector.MinimumTranslationVector[surfaces.length];
		for (int i = 0; i < surfaces.length; i++)
			vectors[i] = surfaces[i].getNormal(this);
		return vectors;
	}

}
