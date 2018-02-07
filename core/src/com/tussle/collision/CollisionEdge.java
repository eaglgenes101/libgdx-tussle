/*
 * Copyright (c) 2018 eaglgenes101
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

public class CollisionEdge implements CollisionShape
{
	protected double startx;
	protected double starty;
	protected double endx;
	protected double endy;
	
	public CollisionEdge(double sx, double sy, double ex, double ey)
	{
		startx = sx;
		starty = sy;
		endx = ex;
		endy = ey;
	}
	
	public ProjectionVector depth(CollisionStadium stad)
	{
		ProjectionVector disp = Intersector.displacementSegments(
				startx, starty, endx, endy,
				stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		disp.xnorm = -disp.xnorm;
		disp.ynorm = -disp.ynorm;
		disp.magnitude = stad.getRadius() - disp.magnitude;
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
		double section = Intersector.partSegments(startx, starty, endx, endy,
		       stad.getStartx(), stad.getStarty(), stad.getEndx(), stad.getEndy());
		ProjectionVector disp = depth(stad);
		return section > 0 && section < 1 &&
				disp.xNorm()*(starty-endy)+disp.yNorm()*(endx-startx) > 0 &&
				disp.magnitude <= 16;
	}
	
	public Rectangle getBounds()
	{
		double minX = FastMath.min(startx, endx);
		double maxX = FastMath.max(startx, endx);
		double minY = FastMath.min(starty, endy);
		double maxY = FastMath.max(starty, endy);
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}
	
	public void draw(ShapeRenderer drawer)
	{
		
		double len = FastMath.hypot(endx-startx, endy-starty);
		if (len > 0)
		{
			double dx = (starty - endy) * 10 / len;
			double dy = (endx - startx) * 10 / len;
			drawer.line((float)startx, (float)starty, (float)endx, (float)endy);
			drawer.line((float)startx, (float)starty,
			            (float)(startx - dx), (float)(starty - dy));
			drawer.line((float)endx, (float)endy,
			            (float)(endx - dx), (float)(endy - dy));
		}
		else
			drawer.point((float)startx, (float)starty, 0);
	}
	
	public CollisionEdge displacement(double dx, double dy)
	{
		return new CollisionEdge(startx+dx, starty+dy, endx+dx, endy+dy);
	}
	
	public CollisionEdge interpolate(CollisionShape other)
	{
		if (!(other instanceof CollisionEdge))
			throw new IllegalArgumentException();
		CollisionEdge o = (CollisionEdge)other;
		return new CollisionEdge(
				(startx+o.startx)/2, (starty+o.starty)/2,
				(endx+o.endx)/2, (endy+o.endy)/2
		);
	}
}
