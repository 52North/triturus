/**
 * Copyright (C) 2007-2015 52North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * license version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.t3dutil;

import java.util.List;

import org.n52.v3d.triturus.vgis.VgPoint;

/** 
 * Simple Delaunay triangulation implementation. This simple incremental 
 * triangulation algorithm follows the description given by de Berg, van 
 * Krefeld, Overmars & Schwarzkopf (2000): Computational geometry, 
 * Berlin/Heidelberg: Springer and J. Ruppert's implementation.
 * <br/>  
 * The algorithm can construct a Delaunay triangulation for a given set of N 
 * points. Further, boundaries can be subscribed. <b>Important note:</b> This 
 * is a non-efficient O(N^2) implementation. Thus, it will be suitable for for
 * simple geometric structures only.
 * <br/>
 * Also note that z-coordinates will be ignored (this is a so-called 
 * &quot;2.5-D&quot; triangulation).
 * <br/>
 * If no boundary is given explicitly, the algorithm will add one and finally
 * remove it again. Algorithm sketch:
 * <br/>
 * If a new point <i>P</i> is inside the triangle <i>ABC</i>, new edges 
 * <i>AP</i>, <i>BP</i>, and <i>CP</i> will be inserted into the TIN structure.
 * Now all the new triangle's edges will be inspected and classified: An edge 
 * is either <i>legal</i> or <i>illegal</i>. Illegal edges will be 
 * <i>flipped</i>: Let </i>XY</i> be illegal, and let </i>XYZ</i> and 
 * <i>XWY</i> be its adjacent triangles. Then </i>XYZ</i> and </i>XWY</i> will 
 * be replaced by </i>XWZ</i> and </i>WYZ</i>. A <i>flip</i> will result in a 
 * recursion as the resulting edges are subject to inspection.
 */
public class SimpleDelaunay 
{
	private class Vec2 { 
		public double x, y;		
		public Vec2(double x, double y) { this.x = x; this.y = y; }
		public void set(double x, double y) { this.x = x; this.y = y; }
	}

	// TIN data storage:
	private int 
		// Initial sizes of temporary arrays (will be doubled when exceeded):
		maxNumberOfPoints = 1000,
		maxNumberOfFaces = 1000,
		// Current array sizes:
		numberOfPoints, 
		numberOfFaces;
	private Vec2[] 
		point = new Vec2[maxNumberOfPoints];
	private int[]
		face = new int[3 * maxNumberOfFaces],
		neigh = new int[3 * maxNumberOfFaces];
		// Here, face-based information is mapped to array positions.  
		// E.g., index 7 points to the 2nd vertex 7 % 3 = 1 of the 3rd face
		// (7 - 1) / 2 = 2.
	private boolean[] 
		segment = new boolean[3 * maxNumberOfFaces], // = edges on boundary
		obsolete;
	public double mXyBound; 
		// All points lie within a square of side length 2*mXyBound (origin as
		// center point).
	private boolean hasExteriorPoints = true;
	
	public SimpleDelaunay(List <VgPoint> points) {
		init(points);
		eatExterior();	
	}
	
	/**
	 * returns an index set containing a Delaunay triangulation for the given 
	 * set of points. The result set is organized as follows: For the input 
	 * points <i>p<sub>0</sub> .. p<sub>N-1</sub></i> the indices (point 
	 * numbers) of the i-th triangle of the result set are stored at the output
	 * array's positions <i>3i, 3i+1</i> and <i>3i+2</i>. Thus, the output 
	 * array will consist of 3 * M elements, where M gives the number of 
	 * triangles.
	 * 
	 * @param points Point list
	 * @return Triangle index set as described above 
	 */
	public static int[] triangulate(List<VgPoint> points) {
    	return new SimpleDelaunay(points).getIndices();
	}

	public int[] getIndices() {
		int[] r = new int[3 * numberOfFaces];
		for (int ii = 0; ii < 3 * numberOfFaces; ii++) r[ii] = face[ii];
		return r;
	}

	/** 
	 * gets the number of faces (triangles) inside the triangulation.
	 */
	public int numberOfFaces() {
		return numberOfFaces;
	}

	/** 
	 * gets the number of points inside the triangulation.
	 */
	public int numberOfPoints() {
		return numberOfPoints;
	}

	private void init(List<VgPoint> points) 
	{
		for (int ii = 0; ii < this.maxNumberOfPoints; ii++) point[ii] = new Vec2(42., 42.);
		
		int N = points.size();
		double r;
		mXyBound = Math.abs(points.get(0).getX());
		for (int i = 0; i < N; i++) {
			if ((r = Math.abs(points.get(i).getX())) > mXyBound) mXyBound = r;
			if ((r = Math.abs(points.get(i).getY())) > mXyBound) mXyBound = r;
		}
		if (mXyBound == 0) {
			mXyBound = 1.; // prevents algorithm from crashing for all points = (0, 0)
		}
		
		numberOfPoints = 3;
		double c = 3. * mXyBound;
		point[0].set(c, 0.);
		point[1].set(0., c);
		point[2].set(-c, -c);

		numberOfFaces = 1;
		face[0] = 0; 
		face[1] = 1;
		face[2] = 2;
		neigh[0] = -1;
		neigh[1] = -1;
		neigh[2] = -1;

		for (int i = 0; i < N; i++) {
			Vec2 p = new Vec2(points.get(i).getX(), points.get(i).getY());
			this.addPoint(p);
		}
	}

    /**
     * adds a point to the triangulation (core method of this implementation). 
     * Note that the point must lie inside (!) the given triangulation.
     * 
     * @param p Point object
     */
	public void addPoint(Vec2 p) {
		int f = searchTriangle(p); // f = face index
		if (f < 0)
			throw new RuntimeException("point in no face");
		
		if (pointOnEdge < 0)
			addPoint(p, f);
		else
			addPoint(p, f, pointOnEdge);
	}

	private void addPoint(Vec2 p, int f) {
		checkPointArr();
		point[numberOfPoints] = p;
		numberOfPoints++;

		splitTriangle(f);
		legalizeNewFaces();
	}

	private void addPoint(Vec2 p, int f, int e) {
		checkPointArr();
		point[numberOfPoints] = p;
		numberOfPoints++;

		splitEdge(f, e);
		legalizeNewFaces();
	}

	private int[] 
		faceNew = new int[4],
		edgeNew = new int[4];

	private void legalizeNewFaces() {
		// face indices:
		int f0 = faceNew[0], f1 = faceNew[1], f2 = faceNew[2], f3 = faceNew[3];
		// edge indices, where edge i (i = 0, 1, 2) lies opposite to vertex i.
		int e0 = edgeNew[0], e1 = edgeNew[1], e2 = edgeNew[2], e3 = edgeNew[3];
		// trigger recursion:
		legalizeEdge(f0, e0);
		legalizeEdge(f1, e1);
		if (f2 >= 0)
			legalizeEdge(f2, e2);
		if (f3 >= 0)
			legalizeEdge(f3, e3);
	}

	private int flipEdge(int f, int e) { 
		// returns the neighbor's edge that has to be checked next
		int 
			fp = 3 * f, // positions in face-based arrays (index mapping)
			f2 = fp + (e + 2) % 3, 
			n = neigh[fp + e];
		int 
			np = 3 * n, 
			ne = getNeighbor(n, f), 
			n2 = np + (ne + 2) % 3;

		face[fp + (e + 1) % 3] = face[np + ne];
		face[np + (ne + 1) % 3] = face[fp + e];

		int dummy;
		dummy = neigh[n2]; if (dummy >= 0) replaceNeighbor(dummy, n, f);
		neigh[fp + e] = dummy;
		dummy = neigh[f2]; if (dummy >= 0) replaceNeighbor(dummy, f, n);
		neigh[np + ne] = dummy;

		neigh[f2] = n;
		neigh[n2] = f;

		return (ne + 1) % 3;
	}

	private int searchTriangle(Vec2 p) {
		for (int i = 0; i < numberOfFaces; i++) {
			if (checkTriangle(i, p))
				return i;
		}
		return -1;
	}

	private int pointOnEdge; // = -1, if the point is _inside_ triangle	
	// If a point lies within a 3-degree-angle of an edge, it is considered to 
	// be _on_ the edge:
	static double MIN_LINE_COS_ANGLE = 0.99862953;

	private boolean checkTriangle(int f, Vec2 p) {
		int 
			fp = 3 * f,
			pi0 = face[fp++],
			pi1 = face[fp++],
			pi2 = face[fp];
		Vec2 
			p0 = point[pi0],
			p1 = point[pi1],
			p2 = point[pi2]; 
		Vec2 
			e0 = new Vec2(p2.x - p1.x, p2.y - p1.y),
			e1 = new Vec2(p0.x - p2.x, p0.y - p2.y),
			e2 = new Vec2(p1.x - p0.x, p1.y - p0.y);			
		Vec2 
			v0 = new Vec2(p.x - p1.x, p.y - p1.y),
			v1 = new Vec2(p.x - p2.x, p.y - p2.y),
			v2 = new Vec2(p.x - p0.x, p.y - p0.y);			
		double 
			n0 = e0.x * v0.y - e0.y * v0.x,
			n1 = e1.x * v1.y - e1.y * v1.x,
			n2 = e2.x * v2.y - e2.y * v2.x;

		if ((n0 < 0 || n1 < 0) || n2 < 0)
			return false;

		pointOnEdge = -1;

		double 
			v0d = v0.x * v0.x + v0.y * v0.y,
			v1d = v1.x * v1.x + v1.y * v1.y,
			v2d = v2.x * v2.x + v2.y * v2.y;
		double 
			e0d = e0.x * e0.x + e0.y * e0.y,
			e1d = e1.x * e1.x + e1.y * e1.y,
			e2d = e2.x * e2.x + e2.y * e2.y;
		double 
			s0 = e0.x * v0.x + e0.y * v0.y,
			s1 = e1.x * v1.x + e1.y * v1.y,
			s2 = e2.x * v2.x + e2.y * v2.y;
		double 
			cos0 = s0 / Math.sqrt(e0d * v0d),
			cos1 = s1 / Math.sqrt(e1d * v1d),
			cos2 = s2 / Math.sqrt(e2d * v2d);

		if (cos0 > cos1) {
			if (cos0 > cos2) {
				if (cos0 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(p1, p2, p, f, 0);
			} else {
				if (cos2 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(p0, p1, p, f, 2);
			}
		} else {
			if (cos1 > cos2) {
				if (cos1 > MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(p2, p0, p, f, 1);
			} else {
				if (cos2 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(p0, p1, p, f, 2);
			}
		}

		return true;
	}

	private int checkForNewEdges(Vec2 p1, Vec2 p2, Vec2 p, int f, int ex) 
	{
		// We must not split the edge if the resulting split of the neighbor
		// creates disoriented triangles!
		int n = neigh[3 * f + ex];
		if (n < 0)
			return ex;

		int pi = face[getNeighborPtr(n, f)];
		Vec2 p0 = point[pi]; 
		Vec2 
			e = new Vec2(p.x - p0.x, p.y - p0.y),
			e1 = new Vec2(p1.x - p0.x, p1.y - p0.y),
			e2 = new Vec2(p2.x - p0.x, p2.y - p0.y);

		if (e.x * e1.y - e.y * e1.x > 0. && e2.x * e.y - e2.y * e.x > 0.)
			return ex;
		else
			return -1;
	}

	private void splitTriangle(int f) {
		checkFaceArr();
		int fp = 3 * f, c = 3 * numberOfFaces;
		int 
			n0 = neigh[fp],
			v0 = face[fp++],
			//n1 = neigh[fp],
			v1 = face[fp++],
			n2 = neigh[fp],
			v2 = face[fp];

		if (n0 >= 0) replaceNeighbor(n0, f, numberOfFaces + 1);
		if (n2 >= 0) replaceNeighbor(n2, f, numberOfFaces);

		fp = 3 * f;
		neigh[fp++] = numberOfFaces + 1;
		face[fp++] = numberOfPoints - 1;
		neigh[fp] = numberOfFaces;

		neigh[c] = numberOfFaces + 1;
		face[c++] = v0;
		neigh[c] = f;
		face[c++] = v1;
		neigh[c] = n2;
		face[c++] = numberOfPoints - 1;
		neigh[c] = f;
		face[c++] = v1;
		neigh[c] = numberOfFaces;
		face[c++] = v2;
		neigh[c] = n0;
		face[c++] = numberOfPoints - 1;

		faceNew[0] = f;
		faceNew[1] = numberOfFaces;
		faceNew[2] = numberOfFaces + 1;
		faceNew[3] = -1;
		edgeNew[0] = 1;
		edgeNew[1] = 2;
		edgeNew[2] = 2;

		numberOfFaces += 2;
	}

	private void splitEdge(int f, int e) { // splits edge with index e  
		checkFaceArr();
		int 
			fp = 3 * f,
			c = 3 * numberOfFaces,
			f0 = fp + e,
			f1 = fp + (e + 1) % 3,
			f2 = fp + (e + 2) % 3,
			n = neigh[f0],
			v0 = face[f0],
			v1 = face[f1],
			nf = neigh[f2];

		if (nf >= 0) replaceNeighbor(nf, f, numberOfFaces);
		face[f1] = numberOfPoints - 1;
		neigh[f2] = numberOfFaces;
		face[c++] = v0;
		neigh[c] = f;
		face[c++] = v1;
		neigh[c] = nf;
		face[c++] = numberOfPoints - 1;

		faceNew[0] = f;
		edgeNew[0] = (e + 1) % 3;
		faceNew[1] = numberOfFaces;
		edgeNew[1] = 2;

		if (n >= 0) {
			neigh[3 * numberOfFaces] = numberOfFaces + 1;

			int 
				np = 3 * n,
				ne = getNeighbor(n, f), // neighbor's edge corresponding to its adjacent edge
				n0 = np + ne,
				n1 = np + (ne + 1) % 3,
				n2 = np + (ne + 2) % 3,
				nn = neigh[n1],
				v2 = face[n0];

			if (nn >= 0) replaceNeighbor(nn, n, numberOfFaces + 1);
			face[n2] = numberOfPoints - 1;
			neigh[n1] = numberOfFaces + 1;
			neigh[c] = n;
			face[c++] = v1;
			neigh[c] = numberOfFaces;
			face[c++] = v2;
			neigh[c] = nn;
			face[c] = numberOfPoints - 1;

			faceNew[2] = n;
			edgeNew[2] = (ne + 2) % 3;
			faceNew[3] = numberOfFaces + 1;
			edgeNew[3] = 2;

			numberOfFaces += 2;
		} 
		else {
			neigh[3 * numberOfFaces] = -1;
			faceNew[2] = -1;
			faceNew[3] = -1;

			numberOfFaces++;
		}
	}

	private void replaceNeighbor(int f, int n, int r) {
		neigh[getNeighborPtr(f, n)] = r;
	}

	private int getNeighborPtr(int f, int n) {
		for (int i = 0, fp = 3 * f; i < 3; i++)
			if (neigh[fp + i] == n)
				return fp + i;
		throw new IllegalArgumentException(n + " is not a neighbor of " + f);
	}

	private int getNeighbor(int f, int n) {
		for (int i = 0, fp = 3 * f; i < 3; i++)
			if (neigh[fp++] == n)
				return i;
		throw new IllegalArgumentException(n + " is not a neighbor of " + f);
	}

	private boolean edgeIllegal(int f, int e) {
		// check edge with respect to triangle and neighbor
		int np;
		if (hasExteriorPoints) {
			int fp = 3 * f, n = neigh[fp + e];
			if (n < 0)
				return false;
			int 
				v0 = face[fp + e],
				v = face[getNeighborPtr(n, f)];
			if (v0 == 0 || v == 0) return false;
			if (v0 == 1 || v == 1) return false;
			if (v0 == 2 || v == 2) return false;

			np = v;
		} 
		else {
			int fp = 3 * f + 3;
			if (segment[fp])
				return false;
			int n = neigh[fp];
			if (n < 0)
				return false;

			np = face[getNeighborPtr(n, f)];
		}

		return pointInCircle(point[np], f);
	}

	private void computeCircumcircle(int f) {
		int 
			fp = 3 * f,
			p0 = face[fp++],
			p1 = face[fp++],
			p2 = face[fp];
		computeCircumcircle(
			point[p0], point[p1], point[p2]);
	}

	private double 
		cirX, cirY, 
		cirR2; // squared radius 

	private void computeCircumcircle(Vec2 p0, Vec2 p1, Vec2 p2) 
	{
		Vec2 
			e = new Vec2(p1.x - p0.x, p1.y - p0.y), 
			n = new Vec2(p2.y - p1.y, p1.x - p2.x);
		double 
			dx = (p0.x - p2.x) / 2.,
			dy = (p0.y - p2.y) / 2.,
			s = (e.x * dx + e.y * dy) / (e.x * n.x + e.y * n.y);
		cirX = (p1.x + p2.x) / 2. + s * n.x;
		cirY = (p1.y + p2.y) / 2. + s * n.y;
		Vec2 
			r = new Vec2(p0.x - cirX, p0.y - cirY);
		cirR2 = r.x * r.x + r.y * r.y;
	}

	private boolean pointInCircle(Vec2 p, int f) {
		computeCircumcircle(f);
		double 
			dx = p.x - cirX, 
			dy = p.y - cirY;
		return dx * dx + dy * dy < cirR2;
	}

	private void legalizeEdge(int f, int e) {
		if (edgeIllegal(f, e)) {
			int n = neigh[3 * f + e], ne = flipEdge(f, e);
			// recursive calls:
			legalizeEdge(f, e);
			legalizeEdge(n, ne);
		}
	}

	private void checkPointArr() {
		if (numberOfPoints >= maxNumberOfPoints - 1) {
			point = this.doubleSize(point);
			maxNumberOfPoints *= 2;
		}
	}

	private void checkFaceArr() {
		if (numberOfFaces >= maxNumberOfFaces - 4) {
			face = this.doubleSize(face);
			neigh = this.doubleSize(neigh);
			segment = this.doubleSize(segment);
			maxNumberOfFaces *= 2;
		}
	}

	private Vec2[] doubleSize(Vec2[] arr) {
		int N = arr.length;
		Vec2[] arrNew = new Vec2[2 * N];
		for (int ii = 0; ii < N; ii++) arrNew[ii] = arr[ii];
		for (int ii = N; ii < 2 * N; ii++) arrNew[ii] = new Vec2(42., 42.);
		return arrNew;
	}

	private int[] doubleSize(int[] arr) {
		int N = arr.length;
		int[] arrNew = new int[2 * N];
		for (int ii = 0; ii < N; ii++) arrNew[ii] = arr[ii];
		return arrNew;
	}

	private boolean[] doubleSize(boolean[] arr) {
		int N = arr.length;
		boolean[] arrNew = new boolean[2 * N];
		for (int ii = 0; ii < N; ii++) arrNew[ii] = arr[ii];
		return arrNew;
	}

	private void eatExterior() {
		obsolete = new boolean[numberOfFaces];
		for (int i = 0; i < numberOfFaces; obsolete[i++] = false);

		for (int i = 0, fp = 0; i < numberOfFaces; i++)
			for (int j = 0; j < 3; j++, fp++) {
				int v = face[fp];
				if (v == 0 || v == 1 || v == 2)
					obsolete[i] = true;
			}

		deleteObsoleteFaces();
		shiftPointsInArr();
		hasExteriorPoints = false;
	}

	private void deleteObsoleteFaces() {
		int[] faceIndex = new int[numberOfFaces];
		int nofNew = 0;
		for (int i = 0; i < numberOfFaces; i++) {
			faceIndex[i] = obsolete[i] ? -1 : nofNew++; 
		}
		for (int i = 0, p0 = 0, p1 = 0; i < numberOfFaces; i++) {
			if (obsolete[i])
				p0 += 3;
			else {
				for (int ii = 0; ii < 3; ii++) face[p1 + ii] = face[p0 + ii];
				for (int ii = 0; ii < 3; ii++) segment[p1 + ii] = segment[p0 + ii];
				for (int j = 0; j < 3; j++, p1++) {
					int n = neigh[p0++];
					neigh[p1] = n < 0 ? -1 : faceIndex[n];
				}
			}
		}
		numberOfFaces = nofNew;
	}

	private void shiftPointsInArr() {
		// get rid of the three extra points, to be called after eatExterior()!
		int p0 = 3 * numberOfFaces;
		for (int ii = 0; ii < p0; ii++) face[ii] -= 3;
		for (int ii = 0; ii < numberOfPoints - 3; ii++) point[ii] = point[ii + 3];
		numberOfPoints -= 3;
	}
}
