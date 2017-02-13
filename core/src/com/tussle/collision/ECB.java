package com.tussle.collision;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tussle.main.Utility;
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

	// Displaces self, and returns actual displacement done
	public Vector2 checkMovement(Vector2 velocity, StageElement[] surfaces)
	{
		Vector2 velocityDone = new Vector2();
		Vector2 velocityLeft = velocity.cpy();
		StageElement lastContact = null;
		int maxIterations = 4;
		for (int i = 0; i < maxIterations; i++)
		{
			float minDist = 1.0f;
			StageElement interceptSurface = null;
			Vector2 finalNormal = new Vector2();
			for (StageElement surface : surfaces)
			{
				if (surface != lastContact)
				{
					float val = surface.checkMovement(velocityLeft, this);
					if (!Float.isNaN(val) && val < minDist)
					{
						Polygon poly = new Polygon(this.getTransformedVertices());
						poly.translate(val * velocityLeft.x, val * velocityLeft.y);
						Intersector.MinimumTranslationVector mtv = surface.getNormal(poly);
						if (mtv != null && mtv.normal.dot(velocityLeft) < 0)
						{
							minDist = val;
							interceptSurface = surface;
							finalNormal = mtv.normal;
						}
					}
				}
			}
			if (minDist == 1.0f || interceptSurface == null)
			{
				this.translate(velocityLeft.x, velocityLeft.y);
				return velocityDone.add(velocityLeft);
			}
			else
			{
				Vector2 truncatedMovement = velocityLeft.cpy().scl(minDist);
				velocityDone.add(truncatedMovement);
				this.translate(truncatedMovement.x, truncatedMovement.y);
				velocityLeft.scl(1-minDist);
				reflect(velocityLeft, finalNormal, 0);
				lastContact = interceptSurface;
			}
		}
		return velocityDone;
	}

	public Vector2 reflect(Vector2 velocity, Vector2 normal, float elasticity)
	{
		if (normal.dot(velocity) < 0)
		{
			Vector2 rej = Utility.rejection(velocity, normal);
			Vector2 proj = Utility.projection(velocity, normal);
			velocity.set(rej.sub(proj.scl(elasticity)));
		}
		return velocity;
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

	public void eject(Vector2 velocity, StageElement[] surfaces, float elasticity)
	{
		Intersector.MinimumTranslationVector[] normals = getNormals(surfaces);
		for (Intersector.MinimumTranslationVector normal : normals)
		{
			if (normal != null)
			{
				Vector2 toDisplace = normal.normal.cpy().scl(normal.depth);
				translate(toDisplace.x, toDisplace.y);
				reflect(velocity, normal.normal, elasticity);
			}
		}
	}

	public Intersector.MinimumTranslationVector[] getNormals(StageElement[] surfaces)
	{
		Intersector.MinimumTranslationVector[] vectors =
				new Intersector.MinimumTranslationVector[surfaces.length];
		for (int i = 0; i < surfaces.length; i++)
			vectors[i] = surfaces[i].getNormal(this);
		return vectors;
	}

	public void setCenter(float centerx, float centery)
	{
		Rectangle rect = this.getBoundingRectangle();
		setPosition(centerx-rect.getWidth()/2, centery-rect.getHeight()/2);
	}

}
