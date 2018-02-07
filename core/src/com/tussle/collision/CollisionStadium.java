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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tussle.main.Intersector;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by eaglgenes101 on 3/8/17.
 */
public class CollisionStadium implements CollisionShape
{
	protected double startx = 0;
	protected double starty = 0;
	protected double endx = 0;
	protected double endy = 0;
	protected double radius = 0;

	public CollisionStadium(double startx, double starty, double endx, double endy, double radius)
	{
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
		this.radius = radius;
	}
	
	public CollisionStadium(CollisionStadium other, double dx, double dy)
	{
		this(other.startx+dx, other.starty+dy,
		     other.endx+dx, other.endy+dy, other.radius);
	}
	
	public CollisionStadium(CollisionStadium other)
	{
		this(other.startx, other.starty, other.endx, other.endy, other.radius);
	}

	public double getStartx()
	{
		return startx;
	}

	public double getStarty()
	{
		return starty;
	}

	public double getEndx()
	{
		return endx;
	}

	public double getEndy()
	{
		return endy;
	}

	public double getRadius()
	{
		return radius;
	}
	
	public ProjectionVector depth(CollisionStadium stad)
	{
		ProjectionVector disp = Intersector.displacementSegments(
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(),
				startx, starty, endx, endy);
		disp.xnorm = -disp.xnorm;
		disp.ynorm = -disp.ynorm;
		disp.magnitude = stad.getRadius()+radius-disp.magnitude;
		return disp;
	}
	
	public double[] nearestPoint(CollisionStadium stad)
	{
		double section = Intersector.partSegments(startx, starty, endx, endy,
		        stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		return new double[]{
				(1-section)*startx + section*endx,
				(1-section)*starty + section*endy
		};
	}
	
	public double stadiumPortion(CollisionStadium stad)
	{
		return Intersector.partSegments(
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy(),
				startx, starty, endx, endy);
	}
	
	public boolean collidesWith(CollisionStadium stad)
	{
		return true;
	}
	
	public Rectangle getBounds()
	{
		double minX = FastMath.min(startx, endx)-radius;
		double maxX = FastMath.max(startx, endx)+radius;
		double minY = FastMath.min(starty, endy)-radius;
		double maxY = FastMath.max(starty, endy)+radius;
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}
	
	public void draw(ShapeRenderer drawer)
	{
		drawer.circle((float)startx, (float)starty, (float)radius);
		drawer.circle((float)endx, (float)endy, (float)radius);
		double len = FastMath.hypot(endx-startx, endy-starty);
		if (len > 0)
		{
			double dx = (starty - endy) * radius / len;
			double dy = (endx - startx) * radius / len;
			drawer.line((float)(startx + dx), (float)(starty + dy),
			            (float)(endx + dx), (float)(endy + dy));
			drawer.line((float)(startx - dx), (float)(starty - dy),
			            (float)(endx - dx), (float)(endy - dy));
		}
	}
	
	public CollisionStadium displacement(double dx, double dy)
	{
		return new CollisionStadium(
				startx+dx, starty+dy,
				endx+dx, endy+dy,
				radius
		);
	}
	
	public CollisionStadium interpolate(CollisionShape other)
	{
		if (!(other instanceof CollisionStadium))
			throw new IllegalArgumentException();
		CollisionStadium o = (CollisionStadium) other;
		return new CollisionStadium(
				(startx+o.startx)/2, (starty+o.starty)/2,
				(endx+o.endx)/2, (endy+o.endy)/2,
				(radius+o.radius)/2
		);
	}
}
