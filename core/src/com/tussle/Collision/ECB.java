package com.tussle.Collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

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
	public float checkMovement(float changeX, float changeY, StageSurface[] surfaces)
	{
		return -1; //Stubbed for now
	}

	//Returns the necessary displacement for the new ECB shape to not intersect with stage surfaces
	public Intersector.MinimumTranslationVector checkShape(float[] newVertices, StageSurface[] surfaces)
	{
		return null; //Stubbed for now
	}

	//Repositions the ECB according to its parent
	public void reposition()
	{

	}

}
