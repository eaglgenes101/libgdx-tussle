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

import com.tussle.collision.ProjectionVector;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public strictfp class Utility
{

	public static double[] getXYfromDM(double direction, double magnitude)
	{
		double[] returnVect = new double[2];
		returnVect[0] = magnitude*StrictMath.cos(StrictMath.toRadians(direction));
		returnVect[1] = magnitude*StrictMath.sin(StrictMath.toDegrees(direction));
		return returnVect;
	}

	public static double addTowards(double value, double addend, double base)
	{
		if (addend == 0) return value;
		if (addend*(base-value-addend) > 0) return value+addend;
		else return addend>0?StrictMath.max(base, value):StrictMath.min(base, value);
	}

	public static double addAway(double value, double addend, double base)
	{
		if (addend*(base-value) <= 0) return value+addend;
		else return value;
	}

	public static double addFrom(double value, double amount, double base)
	{
		if (amount < 0 && StrictMath.min(value-base,base-value) > amount) return base;
		else return amount * StrictMath.copySign(1, value-base) + value;
	}

	public static double[] projection(double x1, double y1, double x2, double y2)
	{
		double[] returnVec = new double[2];
		double scalarProj = (x1*x2+y1*y2)/(x2*x2+y2*y2);
		returnVec[0] = x2*scalarProj;
		returnVec[1] = y2*scalarProj;
		return returnVec;
	}

	public static double[] rejection(double x1, double y1, double x2, double y2)
	{
		double[] returnVec = new double[2];
		double scalarProj = (x1*x2+y1*y2)/(x2*x2+y2*y2);
		returnVec[0] = x1 - x2*scalarProj;
		returnVec[1] = y1 - y2*scalarProj;
		return returnVec;
	}

	public static double angle(double x, double y)
	{
		return StrictMath.toDegrees(StrictMath.atan2(y, x));
	}

	public static double[][] outsidePoints(double[] xpoints, double[] ypoints)
	{
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		int leftmostIndex = 0;
		double leftmostCoord = xpoints[0];
		for (int i = 0; i < xpoints.length; i++)
		{
			if (xpoints[i] < leftmostCoord)
			{
				leftmostIndex = i;
				leftmostCoord = xpoints[i];
			}
		}
		double xvec = 0;
		double yvec = -1;
		int currentIndex = leftmostIndex;
		do
		{
			vertexIndices.add(currentIndex);
			int endpointIndex = 0;
			for (int i = 0; i < xpoints.length; i++)
			{
				if (endpointIndex == currentIndex)
				{
					endpointIndex = i;
					continue;
				}
				double dx = xpoints[endpointIndex]-xpoints[currentIndex];
				double dy = ypoints[endpointIndex]-ypoints[currentIndex];
				double crossProduct = dx*yvec - dy*xvec;
				if (crossProduct > 0)
					endpointIndex = i;
			}
			xvec = xpoints[endpointIndex] - xpoints[currentIndex];
			yvec = ypoints[endpointIndex] - ypoints[currentIndex];
			currentIndex = endpointIndex;
		} while (currentIndex != leftmostIndex);
		double[][] returnArray = new double[2][vertexIndices.size()];
		for (int i = 0; i < vertexIndices.size(); i++)
		{
			returnArray[0][i] = xpoints[vertexIndices.get(i)];
			returnArray[1][i] = ypoints[vertexIndices.get(i)];
		}
		return returnArray;
	}

	public static double lowestPositiveRoot (double a, double b, double c)
	{
		double det = b * b - 4 * a * c;
		if (det < 0) return Double.NaN;

		double sqrtD = StrictMath.sqrt(det);
		double invA = .5 / a;
		double r1 = (-b - sqrtD) * invA;
		double r2 = (-b + sqrtD) * invA;

		if (r1 > r2)
		{
			double tmp = r2;
			r2 = r1;
			r1 = tmp;
		}

		if (r1 >= 0) return r1;
		if (r2 >= 0) return r2;
		return Double.NaN;
	}

	public static boolean isPruned(Collection<ProjectionVector> vectors)
	{
		if (vectors.size() <= 2)
			return true;
		Iterator<ProjectionVector> i = vectors.iterator();
		ProjectionVector p0 = i.next();
		ProjectionVector p1 = i.next();
		double cos0 = p0.xnorm;
		double sin0 = p0.ynorm;
		double cos1 = p1.xnorm;
		double sin1 = p1.ynorm;
		//zeroth on the right, first on the left
		if (cos0 * sin1 < cos1 * sin0)
		{
			double tmpcos = cos1;
			double tmpsin = sin1;
			cos1 = cos0;
			sin1 = sin0;
			cos0 = tmpcos;
			sin0 = tmpsin;
		}
		while (i.hasNext())
		{
			ProjectionVector next = i.next();
			double nextcos = next.xnorm;
			double nextsin = next.ynorm;
			if (nextcos*sin0 >= cos0*nextsin && cos1*nextsin >= nextcos*sin1)
			{
				//Case 0: Within cross product bounds
			}
			else if (nextcos*sin0 >= cos0*nextsin)
			{
				//Case 1: Over the left, extend those bounds
				cos1 = nextcos;
				sin1 = nextsin;
			}
			else if (cos1*nextsin >= nextcos*sin1)
			{
				//Case 2: Over the right, extend those bounds
				cos0 = nextcos;
				sin0 = nextsin;
			}
			else
			{
				//Case 3: Opposite side, immediately return false
				return false;
			}
		}
		return true;
	}

	public static Collection<ProjectionVector> prunedProjections(Collection<ProjectionVector> vectors)
	{
		SortedSet<ProjectionVector> sortedVectors = new ConcurrentSkipListSet<>(
		        Comparator.comparingDouble((ProjectionVector p)-> -p.magnitude));
		sortedVectors.addAll(vectors);
		if (isPruned(sortedVectors))
			return sortedVectors;
		double reduceMagnitude = 0;
		Iterator<ProjectionVector> i = sortedVectors.iterator();
		ProjectionVector p0 = i.next();
		ProjectionVector p1 = i.next();
		double cos0 = p0.xnorm;
		double sin0 = p0.ynorm;
		double cos1 = p1.xnorm;
		double sin1 = p1.ynorm;
		//zeroth on the right, first on the left
		if (cos0 * sin1 < cos1 * sin0)
		{
			double tmpcos = cos1;
			double tmpsin = sin1;
			cos1 = cos0;
			sin1 = sin0;
			cos0 = tmpcos;
			sin0 = tmpsin;
		}
		while (i.hasNext())
		{
			ProjectionVector next = i.next();
			double nextcos = next.xnorm;
			double nextsin = next.ynorm;
			if (nextcos*sin0 >= cos0*nextsin && cos1*nextsin >= nextcos*sin1)
			{
				//Case 0: Within cross product bounds
			}
			else if (nextcos*sin0 >= cos0*nextsin)
			{
				//Case 1: Over the left, extend those bounds
				cos1 = nextcos;
				sin1 = nextsin;
			}
			else if (cos1*nextsin >= nextcos*sin1)
			{
				//Case 2: Over the right, extend those bounds
				cos0 = nextcos;
				sin0 = nextsin;
			}
			else
			{
				//Case 3: Opposite side, immediately return false
				reduceMagnitude = next.magnitude;
				break;
			}
		}
		//Now given reduceMagnitude, remove elements with lesser magnitude and
		//reduce the magnitude of remaining elements
		if (Double.isFinite(reduceMagnitude))
		{
			for (Iterator<ProjectionVector> j = sortedVectors.iterator(); j.hasNext();)
			{
				ProjectionVector vec = j.next();
				if (vec.magnitude <= reduceMagnitude)
					j.remove();
				else
					vec.magnitude -= reduceMagnitude;
			}
		}
		return sortedVectors;
	}

	public static ProjectionVector combineProjections(Collection<ProjectionVector> vectors)
	{
		Iterator<ProjectionVector> i = vectors.iterator();
		if (vectors.size() == 0)
			return null;
		if (vectors.size() == 1)
			return i.next();
		ProjectionVector p0 = i.next();
		ProjectionVector p1 = i.next();
		//Get bordering unit vectors
		double cos0 = p0.xnorm;
		double sin0 = p0.ynorm;
		double cos1 = p1.xnorm;
		double sin1 = p1.ynorm;
		//zeroth on the right, first on the left
		if (cos0 * sin1 < cos1 * sin0)
		{
			double tmpcos = cos1;
			double tmpsin = sin1;
			cos1 = cos0;
			sin1 = sin0;
			cos0 = tmpcos;
			sin0 = tmpsin;
		}
		while (i.hasNext())
		{
			ProjectionVector next = i.next();
			double nextcos = next.xnorm;
			double nextsin = next.ynorm;
			if (nextcos*sin0 >= cos0*nextsin && cos1*nextsin >= nextcos*sin1)
			{
				//Case 0: Within cross product bounds
			}
			else if (nextcos*sin0 >= cos0*nextsin)
			{
				//Case 1: Over the left, extend those bounds
				cos1 = nextcos;
				sin1 = nextsin;
			}
			else if (cos1*nextsin >= nextcos*sin1)
			{
				//Case 2: Over the right, extend those bounds
				cos0 = nextcos;
				sin0 = nextsin;
			}
			else
			{
				//Case 3: something went horribly wrong
				return null;
			}
		}
		//Now... project all vectors onto the sum of the borders.
		double sumcos = cos0+cos1;
		double sumsin = sin0+sin1;
		double len = StrictMath.hypot(sumcos, sumsin);
		if (len == 0) return null;
		sumcos /= len;
		sumsin /= len;
		double maxlen = Double.NEGATIVE_INFINITY;
		for (ProjectionVector v : vectors)
		{
			double scalarProj = (v.xnorm*v.magnitude*sumcos+v.ynorm*v.magnitude*sumsin)
					/(sumcos*sumcos+sumsin*sumsin);
			if (scalarProj > maxlen)
				maxlen = scalarProj;
		}
		return new ProjectionVector(sumcos, sumsin, maxlen);
	}

	//Find the point that 1-2 and 3-4 both point towards
	public static double[] segmentsIntersectionPoint(double x1, double y1, double x2, double y2,
													 double x3, double y3, double x4, double y4)
	{
		// This problem can be reduced to the following linear system:
		// (y2-y1)x + (x1-x2)y = x1*y2 - x2*y1
		// (y4-y3)x + (x3-x4)y = x3*y4 - x4*y3

		//Yay for Julia's sympy package for doing work for me
		double numX = x1*x3*y2 - x1*x3*y4 - x1*x4*y2 + x1*x4*y3
				- x2*x3*y1 + x2*x3*y4 + x2*x4*y1 - x2*x4*y3;
		double numY = x1*y2*y3 - x1*y2*y4 - x2*y1*y3 + x2*y1*y4
				- x3*y1*y4 + x3*y2*y4 + x4*y1*y3 - x4*y2*y3;
		double den = x1*y3 - x1*y4 - x2*y3 + x2*y4 - x3*y1 + x3*y2 + x4*y1 - x4*y2;
		return new double[]{numX/den, numY/den};
	}

	public static double pointSegmentPosition(double sx, double sy, double ex, double ey,
											  double x, double y)
	{
		double dx = x-sx;
		double dy = y-sy;
		double lx = ex-sx;
		double ly = ey-sy;
		return (dx*lx+dy*ly)/(lx*lx+ly*ly);
	}

	public static double partSegmentsIntersecting(double x1, double y1, double x2, double y2,
												  double x3, double y3, double x4, double y4)
	{
		double den = x1*y3 - x1*y4 - x2*y3 + x2*y4 - x3*y1 + x3*y2 + x4*y1 - x4*y2;
		double x = (x1*x3*y2 - x1*x3*y4 - x1*x4*y2 + x1*x4*y3
				- x2*x3*y1 + x2*x3*y4 + x2*x4*y1 - x2*x4*y3)/den;
		double y = (x1*y2*y3 - x1*y2*y4 - x2*y1*y3 + x2*y1*y4
				- x3*y1*y4 + x3*y2*y4 + x4*y1*y3 - x4*y2*y3)/den;
		double dx = x-x1;
		double dy = y-y1;
		double lx = x2-x1;
		double ly = y2-y1;
		return (dx*lx+dy*ly)/(lx*lx+ly*ly);
	}

}






