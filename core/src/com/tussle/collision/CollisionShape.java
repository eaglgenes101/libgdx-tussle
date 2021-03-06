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

//Use StageElement to generate these loader shapes
public interface CollisionShape
{
	ProjectionVector depth(CollisionStadium stad);
	double[] nearestPoint(CollisionStadium stad);
	double stadiumPortion(CollisionStadium stad);
	
	//Defines additional predicates that must be satisfied in addition to
	//depth having positive magnitude before collision acts
	boolean collidesWith(CollisionStadium stad);
	
	Rectangle getBounds();
	
	void draw(ShapeRenderer drawer);
	
	CollisionShape displacementBy(double dx, double dy);
	
	CollisionShape transformBy(double dx, double dy, double rot, double scale, boolean flip);
	
	CollisionShape interpolate(CollisionShape other);
	
	
}
