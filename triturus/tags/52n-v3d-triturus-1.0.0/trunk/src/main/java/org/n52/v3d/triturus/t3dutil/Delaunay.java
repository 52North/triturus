package org.n52.v3d.triturus.t3dutil;

/** The algorithm implemented here follows De Berg, van Krefeld, Overmars, and Schwarzkopf's
  * <q>Computational geometry: Algorithms and Applications</q> (Springer Verlag, 2000). See
  * <a href="http://www.cs.ubc.ca/~szwang/Research/516Project/delaunay.htm">Wang's paper</a>
  * for further developments.
  * <p>
  * This algorithm can construct a Delaunay triangulation for a given set of points.
  * Further, boundaries can be subscribed. In that case, the triangulation will be
  * Delaunay up to the bounday triangles.
  * <p>
  * If no boundary ist subscribed, the algorithm will add one and remove it in the end.
  * That way, the following algorithm can be applied:
  * <p>
  * A new point <code>P</code> is implemented in a naive way: If <code>P</code> is in
  * triangle <code>ABC</code>, then new edges <code>AP</code>, <code>BP</code>,
  * and <code>CP</code> are inserted.
  * <ul>
  * <li> All three new edges are inspected and classified: An edge is either
  * <q>legal</q> or <q>illegal</q>.
  * <li> <q>Illegal</q> edges are <q>flipped</q>: Let </code>XY</code> be illegal, and let
  * </code>XYZ</code> and </code>XWY</code> be its adjacent triangles. Then </code>XYZ</code>
  * and </code>XWY</code> are replaced by </code>XWZ</code> and </code>WYZ</code>.
  * <li> A <q>flip</q> leads to a recursion as the resulting edges are subject
  * to inspection.
  * </ul>
  *
  * @author Ruppert
  */
public class Delaunay implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	// If a point lies within a 3-degree-angle of an edge, it is considered to be ON the edge
	static double MIN_LINE_COS_ANGLE = 0.99862953;
	// cos(3)=0.99862953   cos(1)=0.9998477

	// The following are initial values
	// They are doubled each time they are exceeded
	int maximalNumberOfPoints = 1000;
	int maximalNumberOfFaces = 1000;

	double[] point = new double[3 * maximalNumberOfPoints];
	int[] face = new int[3 * maximalNumberOfFaces];
	int[] neighbor = new int[3 * maximalNumberOfFaces];
	// A segment is an edge on the boundary - an (almost) untouchable edge
	boolean[] segment = new boolean[3 * maximalNumberOfFaces];

	int numberOfPoints;
	int numberOfFaces;

	// Every Point lies within a square of side length 2*xyBound which is centered about the origin
	double xyBound;

	boolean[] obsolete;
	boolean hasExteriorPoints = true;

	// further, we use   f      index for face
	//                           n     index for face`s neighbor
	//                           e     edge (e.g. 0 is edge from vertex 1 to 2)
	//                           ne   neighbor`s edge corresponding to its adjacent edge
	//                           fp    pointer to face[]-index (e.g. 4 points to vertex 0 of face 1)

	/**
		 * A Delaunay triangulation for the given points is created.
		 * @param p the points given as <code>x<sub>1</sub>,y<sub>1</sub>,
		 * x<sub>2</sub>,y<sub>2</sub>,...</code>
		 */
	public Delaunay(final double[] p) {
		init(p);
		eatExterior();
	}

	/**
		 * A Delaunay triangulation for the given boundaries is created.
		 * <p>
		 * The array <code>p</code> is given as <code>x<sub>1,1</sub>,y<sub>1,1</sub>,
		 * x<sub>1,2</sub>,y<sub>1,2</sub>,...</code>
		 * <p>
		 * <b>Note:</b>
		 * <ul>
		 * <li> All boundaries are connected, e.g. <code>x<sub>1,n</sub>,y<sub>1,n</sub>
		 * with <code>x<sub>1,1</sub>,y<sub>1,1</sub>
		 * <li> The first array <code>p[0]</code> must contain the exterior boundary
		 * </ul>
		 * <p>
		 * @param p the array given as <code>x<sub>1,1</sub>,y<sub>1,1</sub>,
		 * x<sub>1,2</sub>,y<sub>1,2</sub>,...</code> containing the boundary points
		 */
	public Delaunay(final double[][] p) {
		init(makePointArray(p));
		checkBoundary(p);
		makeSegments(p);
		eatExterior(p);
	}

	/**
	 * This static methods returns an index set
	 * containing a Delaunay triangulation for a given set of points.
	 * @param p the points given as <code>x<sub>1</sub>,y<sub>1</sub>,
	 * x<sub>2</sub>,y<sub>2</sub>,...</code>
	 * @return the index set: <code>p<sub>1</sub> .... p<sub>3n</sub></code>.
	 * Triangle <code>i</code> consists of points number
	 *  <code>p<sub>3i</sub>, p<sub>3i+1</sub>, p<sub>3i+2</sub></code>
	 */
	public static int[] triangulate(final double[] p) {
		return new Delaunay(p).getIndices();
	}

	/** Every Point lies within a square of side length <code>2*getBound()</code>
	 *  which is centered about the origin.
	 */
	public double getBound() {
		return xyBound;
	}

	/**
	 * The index set of the Delaunay triangulation.
	 * @return The index set: <code>p<sub>1</sub> .... p<sub>3n</sub></code>.
	 * Triangle <code>i</code> consists of points number
	 *  <code>p<sub>3i</sub>, p<sub>3i+1</sub>, p<sub>3i+2</sub></code> 
	 */
	public int[] getIndices() {
		int[] r = new int[3 * numberOfFaces];
		System.arraycopy(face, 0, r, 0, 3 * numberOfFaces);
		return r;
	}

	/** Stores the coordinates of point <code>i</code> in the submitted array
	 * @param index the index of the point
	 * @param point the double array of length 2 submitted
	 */
	public void getPoint(int index, double[] point) {
		if (point == null)
			throw new RuntimeException("no array submitted");
		if (point.length < 2)
			throw new RuntimeException("submitted array is to small");
		point[0] = this.point[2 * index];
		point[1] = this.point[2 * index + 1];
	}

	/** Stores the coordinates of all points  in the submitted array <code>points</code>
	 * beginning at the specified position <code>offset</code>.
	 */
	public void getPoints(double[] points, int offset) {
		if (points == null)
			throw new RuntimeException("no array submitted");
		if (point.length < 2 * numberOfPoints + offset)
			throw new RuntimeException("submitted array is to small");
		System.arraycopy(point, 0, points, offset, 2 * numberOfPoints);
	}

	/** 
	   * @return An array containing the coordinates of the points given as 
	   * <code>x<sub>1</sub>,y<sub>1</sub>, x<sub>2</sub>,y<sub>2</sub>,...</code>
		 */
	public double[] getPoints() {
		double[] r = new double[2 * numberOfPoints];
		getPoints(r, 0);
		return r;
	}

	/** 
	 * @return The number of faces generated by the triangulation.
	 */
	public int getNumFaces() {
		return numberOfFaces;
	}

	/** 
	 * @return The number of points.  
	 */
	public int getNumPoints() {
		return numberOfPoints;
	}

	void init(final double[] p) {
		int nip = p.length / 2;
		double r;
		xyBound = Math.abs(p[0]);
		for (int i = 1; i < 2 * nip; i++)
			if ((r = Math.abs(p[i])) > xyBound)
				xyBound = r;

		if (xyBound == 0)
			xyBound = 1; // to omit crash if ALL points are (0,0)

		numberOfPoints = 3;
		point[0] = 3 * xyBound;
		point[1] = 0;
		point[2] = 0;
		point[3] = 3 * xyBound;
		point[4] = -3 * xyBound;
		point[5] = -3 * xyBound;

		numberOfFaces = 1;
		face[0] = 0;
		face[1] = 1;
		face[2] = 2;
		neighbor[0] = -1;
		neighbor[1] = -1;
		neighbor[2] = -1;

		for (int i = 0, c = 0; i < nip; i++, c += 2)
			addPoint(p[c], p[c + 1]);
	}

    /**
     * The core method of this class: A point <code>x,y</code> is inserted.
     * The point <b>must lie inside</b> the given triangulation.
     * @param x,y are the point's coordinates
     */
	public void addPoint(final double x, final double y) {
		final int f = findTriangle(x, y);

		if (f < 0)
			throw new RuntimeException("point in no face");

		if (pointOnEdge < 0)
			addPoint(x, y, f);
		else
			addPoint(x, y, f, pointOnEdge);
	}

	void addPoint(final double x, final double y, final int f) {
		checkPointArray();
		point[2 * numberOfPoints] = x;
		point[2 * numberOfPoints + 1] = y;
		numberOfPoints++;

		splitTriangle(f);
		legalizeNewFaces();
	}

	void addPoint(final double x, final double y, final int f, final int e) {
		checkPointArray();
		point[2 * numberOfPoints] = x;
		point[2 * numberOfPoints + 1] = y;
		numberOfPoints++;

		splitEdge(f, e);
		legalizeNewFaces();
	}

	int[] newFace = new int[4];
	int[] newEdge = new int[4];

	// calls recursion
	void legalizeNewFaces() {
		int f0 = newFace[0], f1 = newFace[1], f2 = newFace[2], f3 = newFace[3];
		int e0 = newEdge[0], e1 = newEdge[1], e2 = newEdge[2], e3 = newEdge[3];
		legalizeEdge(f0, e0);
		legalizeEdge(f1, e1);
		if (f2 >= 0)
			legalizeEdge(f2, e2);
		if (f3 >= 0)
			legalizeEdge(f3, e3);
	}

	// returns neighbor`s edge to check next
	int flipEdge(final int f, final int e) {
		final int fp = 3 * f, f2 = fp + (e + 2) % 3, n = neighbor[fp + e];
		final int np = 3 * n, ne = getNeighbor(n, f), n2 = np + (ne + 2) % 3;

		face[fp + (e + 1) % 3] = face[np + ne];
		face[np + (ne + 1) % 3] = face[fp + e];

		int dummy = neighbor[n2];
		if (dummy >= 0)
			replaceNeighbor(dummy, n, f);
		neighbor[fp + e] = dummy;

		dummy = neighbor[f2];
		if (dummy >= 0)
			replaceNeighbor(dummy, f, n);
		neighbor[np + ne] = dummy;

		neighbor[f2] = n;
		neighbor[n2] = f;

		return (ne + 1) % 3;
	}

	int findTriangle(final double x, final double y) {
		for (int i = 0, fp = 0; i < numberOfFaces; i++, fp += 3)
			if (checkTriangle(i, x, y))
				return i;
		return -1;
	}

	int pointOnEdge; // Is set to  -1  if the point is INSIDE triangle

	boolean checkTriangle(final int f, final double x, final double y) {
		int fp = 3 * f,
			p0 = 2 * face[fp++],
			p1 = 2 * face[fp++],
			p2 = 2 * face[fp];
		final double x0 = point[p0++],
			y0 = point[p0],
			x1 = point[p1++],
			y1 = point[p1],
			x2 = point[p2++],
			y2 = point[p2];
		final double e0x = x2 - x1,
			e0y = y2 - y1,
			e1x = x0 - x2,
			e1y = y0 - y2,
			e2x = x1 - x0,
			e2y = y1 - y0;
		final double v0x = x - x1,
			v0y = y - y1,
			v1x = x - x2,
			v1y = y - y2,
			v2x = x - x0,
			v2y = y - y0;
		final double n0 = e0x * v0y - e0y * v0x,
			n1 = e1x * v1y - e1y * v1x,
			n2 = e2x * v2y - e2y * v2x;

		if ((n0 < 0 || n1 < 0) || n2 < 0)
			return false;

		pointOnEdge = -1;

		final double v0 = v0x * v0x + v0y * v0y,
			v1 = v1x * v1x + v1y * v1y,
			v2 = v2x * v2x + v2y * v2y;
		final double e0 = e0x * e0x + e0y * e0y,
			e1 = e1x * e1x + e1y * e1y,
			e2 = e2x * e2x + e2y * e2y;
		final double s0 = e0x * v0x + e0y * v0y,
			s1 = e1x * v1x + e1y * v1y,
			s2 = e2x * v2x + e2y * v2y;
		final double cos0 = s0 / Math.sqrt(e0 * v0),
			cos1 = s1 / Math.sqrt(e1 * v1),
			cos2 = s2 / Math.sqrt(e2 * v2);

		if (cos0 > cos1) {
			if (cos0 > cos2) {
				if (cos0 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(x1, y1, x2, y2, x, y, f, 0);
			} else {
				if (cos2 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(x0, y0, x1, y1, x, y, f, 2);
			}
		} else {
			if (cos1 > cos2) {
				if (cos1 > MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(x2, y2, x0, y0, x, y, f, 1);
			} else {
				if (cos2 >= MIN_LINE_COS_ANGLE)
					pointOnEdge = checkForNewEdges(x0, y0, x1, y1, x, y, f, 2);
			}
		}

		return true;
	}

	// We must not spilt the edge if the resulting split of the neighbor creates disoriented triangles !
	int checkForNewEdges(
		final double x1,
		final double y1,
		final double x2,
		final double y2,
		final double x,
		final double y,
		final int f,
		final int e) {

		final int n = neighbor[3 * f + e];

		if (n < 0)
			return e;

		int p = 2 * face[getNeighborPtr(n, f)];
		final double x0 = point[p++],
			y0 = point[p],
			ex = x - x0,
			ey = y - y0,
			ex1 = x1 - x0,
			ey1 = y1 - y0,
			ex2 = x2 - x0,
			ey2 = y2 - y0;

		if (ex * ey1 - ey * ex1 > 0 && ex2 * ey - ey2 * ex > 0)
			return e;

		return -1;
	}

	void splitTriangle(final int f) {
		checkFaceArray();
		int fp = 3 * f, c = 3 * numberOfFaces;
		final int n0 = neighbor[fp],
			v0 = face[fp++],
			n1 = neighbor[fp],
			v1 = face[fp++];
		final int n2 = neighbor[fp], v2 = face[fp];

		if (n0 >= 0)
			replaceNeighbor(n0, f, numberOfFaces + 1);
		if (n2 >= 0)
			replaceNeighbor(n2, f, numberOfFaces);

		fp = 3 * f;
		neighbor[fp++] = numberOfFaces + 1;
		face[fp++] = numberOfPoints - 1;
		neighbor[fp] = numberOfFaces;

		neighbor[c] = numberOfFaces + 1;
		face[c++] = v0;
		neighbor[c] = f;
		face[c++] = v1;
		neighbor[c] = n2;
		face[c++] = numberOfPoints - 1;
		neighbor[c] = f;
		face[c++] = v1;
		neighbor[c] = numberOfFaces;
		face[c++] = v2;
		neighbor[c] = n0;
		face[c++] = numberOfPoints - 1;

		newFace[0] = f;
		newFace[1] = numberOfFaces;
		newFace[2] = numberOfFaces + 1;
		newFace[3] = -1;
		newEdge[0] = 1;
		newEdge[1] = 2;
		newEdge[2] = 2;

		numberOfFaces += 2;
	}

	void splitEdge(final int f, final int e) {
		checkFaceArray();
		int fp = 3 * f,
			c = 3 * numberOfFaces,
			f0 = fp + e,
			f1 = fp + (e + 1) % 3,
			f2 = fp + (e + 2) % 3,
			n = neighbor[f0],
			v0 = face[f0],
			v1 = face[f1],
			nf = neighbor[f2];

		if (nf >= 0)
			replaceNeighbor(nf, f, numberOfFaces);
		face[f1] = numberOfPoints - 1;
		neighbor[f2] = numberOfFaces;
		face[c++] = v0;
		neighbor[c] = f;
		face[c++] = v1;
		neighbor[c] = nf;
		face[c++] = numberOfPoints - 1;

		newFace[0] = f;
		newEdge[0] = (e + 1) % 3;
		newFace[1] = numberOfFaces;
		newEdge[1] = 2;

		if (n >= 0) {
			neighbor[3 * numberOfFaces] = numberOfFaces + 1;

			int np = 3 * n,
				ne = getNeighbor(n, f),
				n0 = np + ne,
				n1 = np + (ne + 1) % 3,
				n2 = np + (ne + 2) % 3,
				nn = neighbor[n1],
				v2 = face[n0];

			if (nn >= 0)
				replaceNeighbor(nn, n, numberOfFaces + 1);
			face[n2] = numberOfPoints - 1;
			neighbor[n1] = numberOfFaces + 1;
			neighbor[c] = n;
			face[c++] = v1;
			neighbor[c] = numberOfFaces;
			face[c++] = v2;
			neighbor[c] = nn;
			face[c] = numberOfPoints - 1;

			newFace[2] = n;
			newEdge[2] = (ne + 2) % 3;
			newFace[3] = numberOfFaces + 1;
			newEdge[3] = 2;

			numberOfFaces += 2;
		} else {
			neighbor[3 * numberOfFaces] = -1;
			newFace[2] = -1;
			newFace[3] = -1;

			numberOfFaces++;
		}
	}

	void replaceNeighbor(final int f, final int n, final int r) {
		neighbor[getNeighborPtr(f, n)] = r;
	}

	int getNeighborPtr(final int f, final int n) {
		for (int i = 0, fp = 3 * f - 1; i < 3; i++)
			if (neighbor[++fp] == n)
				return fp;
		throw new IllegalArgumentException(n + " is not a neighbor of " + f);
	}

	int getNeighbor(final int f, final int n) {
		for (int i = 0, fp = 3 * f; i < 3; i++)
			if (neighbor[fp++] == n)
				return i;
		throw new IllegalArgumentException(n + " is not a neighbor of " + f);
	}

	// check edge with respect to triangle and neighbor
	boolean edgeIllegal(final int f, final int e) {
		int np;
		if (hasExteriorPoints) {
			int fp = 3 * f, n = neighbor[fp + e];
			if (n < 0)
				return false;

			final int v0 = face[fp + e],
				v1 = face[fp + (e + 1) % 3],
				v2 = face[fp + (e + 2) % 3],
				v = face[getNeighborPtr(n, f)];

			if (v0 == 0 || v == 0)
				return false;
			if (v0 == 1 || v == 1)
				return false;
			if (v0 == 2 || v == 2)
				return false;

			np = 2 * v;

		} else {
			final int fp = 3 * f + 3;
			if (segment[fp])
				return false;
			final int n = neighbor[fp];
			if (n < 0)
				return false;

			np = 2 * face[getNeighborPtr(n, f)];
		}

		return pointInCircle(point[np++], point[np], f);
	}

	double circleX, circleY, circleR; // radius is squared !

	// set e=p1-p0  <n,(p2-p1)>=0  x=(p1+p2)/2  d=(p0-p2)/2  
	// then x -= n*<e,d>/<e,n> will be the CircumCenter
	void computeCircumCircle(
		final double x0,
		final double y0,
		final double x1,
		final double y1,
		final double x2,
		final double y2) {
		final double ex = x1 - x0, ey = y1 - y0, nx = y2 - y1, ny = x1 - x2;
		circleX = (x1 + x2) * 0.5;
		circleY = (y1 + y2) * 0.5;
		final double dx = (x0 - x2) * 0.5,
			dy = (y0 - y2) * 0.5,
			s = (ex * dx + ey * dy) / (ex * nx + ey * ny);
		circleX += s * nx;
		circleY += s * ny;
		final double rx = x0 - circleX, ry = y0 - circleY;
		circleR = rx * rx + ry * ry;
	}

	void computeCircumCircle(final int f) {
		int fp = 3 * f,
			p0 = 2 * face[fp++],
			p1 = 2 * face[fp++],
			p2 = 2 * face[fp];
		computeCircumCircle(
			point[p0++],
			point[p0],
			point[p1++],
			point[p1],
			point[p2++],
			point[p2]);
	}

	boolean pointInCircle(final double x, final double y, final int f) {
		computeCircumCircle(f);
		final double dx = x - circleX, dy = y - circleY;
		return (dx * dx + dy * dy < circleR);
	}

	// recursion !!!
	void legalizeEdge(final int f, int e) {
		if (edgeIllegal(f, e)) {
			int n = neighbor[3 * f + e], ne = flipEdge(f, e);
			legalizeEdge(f, e);
			legalizeEdge(n, ne);
		}
	}

	void checkPointArray() {
		if (numberOfPoints >= maximalNumberOfPoints - 1) {
			point = doubleSize(point);
			maximalNumberOfPoints *= 2;
		}
	}

	void checkFaceArray() {
		if (numberOfFaces >= maximalNumberOfFaces - 4) {
			face = doubleSize(face);
			neighbor = doubleSize(neighbor);
			segment = doubleSize(segment);
			maximalNumberOfFaces *= 2;
		}
	}

	double[] doubleSize(double[] p) {
		int i = p.length;
		double[] newP = new double[2 * i];
		System.arraycopy(p, 0, newP, 0, i);
		return newP;
	}

	int[] doubleSize(int[] p) {
		int i = p.length;
		int[] newP = new int[2 * i];
		System.arraycopy(p, 0, newP, 0, i);
		return newP;
	}

	boolean[] doubleSize(boolean[] p) {
		int i = p.length;
		boolean[] newP = new boolean[2 * i];
		System.arraycopy(p, 0, newP, 0, i);
		return newP;
	}

	// returns -1 if false
	int pointInFace(int f, int v) {
		int fp = 3 * f;
		for (int i = 0; i < 3; i++)
			if (face[fp++] == v)
				return i;
		return -1;
	}

	// returns -1 if false
	int edgeInFace(int f, int v1, int v2) {
		int i = pointInFace(f, v1), j = pointInFace(f, v2);
		if (i < 0 || j < 0)
			return -1;
		return 3 - i - j;
	}

	void eatExterior() {
		obsolete = new boolean[numberOfFaces];
		for (int i = 0; i < numberOfFaces; obsolete[i++] = false);

		for (int i = 0, fp = 0; i < numberOfFaces; i++)
			for (int j = 0; j < 3; j++, fp++) {
				final int v = face[fp];
				if (((v == 0) || (v == 1)) || (v == 2))
					obsolete[i] = true;
			}

		deleteObsoleteFaces();
		shiftPoints();
		hasExteriorPoints = false;
	}

	void deleteObsoleteFaces() {
		int[] faceIndex = new int[numberOfFaces];
		int newNof = 0;
		for (int i = 0; i < numberOfFaces; i++) {
			if (obsolete[i])
				faceIndex[i] = -1;
			else
				faceIndex[i] = newNof++;
		}

		for (int i = 0, p0 = 0, p1 = 0; i < numberOfFaces; i++) {
			if (obsolete[i])
				p0 += 3;
			else {
				System.arraycopy(face, p0, face, p1, 3);
				System.arraycopy(segment, p0, segment, p1, 3);
				for (int j = 0; j < 3; j++, p1++) {
					final int n = neighbor[p0++];
					if (n < 0)
						neighbor[p1] = -1;
					else
						neighbor[p1] = faceIndex[n];
				}
			}
		}

		numberOfFaces = newNof;
	}

	// get rid of the three extra points
	// (make sure that  eatExterior  has been called before !)
	void shiftPoints() {
		final int p0 = 3 * numberOfFaces;
		for (int i = 0; i < p0; i++)
			face[i] -= 3;
		final int p1 = 3 * numberOfPoints - 6;
		System.arraycopy(point, 6, point, 0, p1);
		numberOfPoints -= 3;
	}

	// all methods below deal with segments

	static double[] makePointArray(final double[][] p) {
		int pp = 0, nos = p.length;
		for (int i = 0; i < nos; i++)
			pp += p[i].length;
		double[] r = new double[pp];
		for (int i = 0, j = 0; i < nos; i++) {
			int l = p[i].length;
			if (l % 2 == 1)
				throw new RuntimeException("points need to be 2-dimensional");
			System.arraycopy(p[i], 0, r, j, l);
			j += l;
		}
		return r;
	}

	void makeSegments(final double[][] p) {
		final int nos = p.length;
		for (int i = 0, j = 3; i < nos; i++) {
			int l = p[i].length / 2;
			for (int k = 0; k < l - 1; k++)
				seekSegment(j + k, j + k + 1);
			if (l > 1)
				seekSegment(j, j + l - 1);
			j += l;
		}
	}

	boolean markSegment(final int f, final int e) {
		final int fp = 3 * f + e, n = neighbor[fp];
		if (n >= 0)
			segment[getNeighborPtr(n, f)] = true;
		return (segment[fp] = true);
	}

	boolean seekAndMarkSegment(final int v0, final int v1) {
		for (int i = 0, fp = 0; i < numberOfFaces; i++, fp += 3) {
			if (face[fp] == v0) {
				if (face[fp + 1] == v1)
					return markSegment(i, 2);
				if (face[fp + 2] == v1)
					return markSegment(i, 1);
			}
			if (face[fp + 1] == v0) {
				if (face[fp] == v1)
					return markSegment(i, 2);
				if (face[fp + 2] == v1)
					return markSegment(i, 0);
			}
			if (face[fp + 2] == v0) {
				if (face[fp] == v1)
					return markSegment(i, 1);
				if (face[fp + 1] == v1)
					return markSegment(i, 0);
			}
		}
		return false;
	}

	// recursion !!!
	void seekSegment(final int v0, final int v1) {
		if (!seekAndMarkSegment(v0, v1)) {
			final int v2 = numberOfPoints;
			int p0 = 2 * v0, p1 = 2 * v1;

			// addMidPoint
			addPoint(
				(0.5 * (point[p0++] + point[p1++])),
				(0.5 * (point[p0] + point[p1])));

			seekSegment(v0, v2);
			seekSegment(v1, v2);
		}
	}

	// calls recursion
	void eatExterior(final double[][] p) {
		obsolete = new boolean[numberOfFaces];
		for (int i = 0; i < numberOfFaces; obsolete[i++] = false);

		int f, fp;
		for (int i = 0; i < 3; i++) {
			f = 0;
			fp = 0;
			while ((face[fp++] != i && face[fp++] != i) && face[fp++] != i)
				f++;
			if (f < numberOfFaces)
				eatFace(f);
		}

		final int nos = p.length;
		for (int i = 1; i < nos; i++)
			if (computeInteriorPoint(p[i]))
				eatFace(findTriangle(circleX, circleY));

		deleteObsoleteFaces();
		shiftPoints();
		hasExteriorPoints = false;
	}

	boolean computeInteriorPoint(final double[] p) // circleX,circleY  will be inside p
	{
		double x0 = p[0], x, xc;
		int j = 0;
		final int l = p.length / 2;
		for (int i = 1, c = 2; i < l; i++, c += 2)
			if ((x = p[c]) > x0) {
				x0 = x;
				j = i;
			} // j = is right-most point
		final double y0 = p[2 * j + 1];

		int p1 = 2 * ((j + 1) % l), p2 = 2 * ((j - 1 + l) % l);
		final double ym1 = p[p1 + 1] - y0, ym2 = p[p2 + 1] - y0;
		if (ym1 * ym2 >= 0) { // j  is a peak (both neighbors above OR below)
			if (ym1 == 0 && ym2 == 0)
				return false;
			x = 1; // x  will be sqr of distance to closest point
			for (int i = 0, c = 0; i < l; i++) {
				if (i != j) {
					final double xm = p[c++] - x0, ym = p[c++] - y0;
					xc = xm * xm + ym * ym;
					if (xc < x)
						x = xc;
				} else
					c += 2;
			}
			double vx = p[p1++] + p[p2++] - 2 * x0, vy = p[p1] + p[p2] - 2 * y0;
			final double s = 0.5 * Math.sqrt(x / (vx * vx + vy * vy));
			circleX = x0 + s * vx;
			circleY = y0 + s * vy;
			return true; // end peak-case
		}

		x = x0 - 1;
		for (int i = 0; i < l; i++) // no peak: find closed point with y(j)
			if (j != i && j != (i + 1) % l) {
				p1 = 2 * i;
				p2 = 2 * ((i + 1) % l);
				final double x1 = p[p1++], y1 = p[p1], x2 = p[p2++], y2 = p[p2];
				if ((y2 - y0) * (y1 - y0) <= 0) {
					if (y1 == y2)
						xc = (x1 > x2 ? x1 : x2);
					else
						xc = x2 - (x2 - x1) * (y2 - y0) / (y2 - y1);
					if (xc > x)
						x = xc;
				}
			}

		if (x == x0)
			return false;
		circleX = 0.5 * (x + x0);
		circleY = y0;

		return true;
	}

	// recursion !!!
	void eatFace(final int f) {
		obsolete[f] = true;

		int fp = 3 * f;
		final int n0 = neighbor[fp],
			n1 = neighbor[fp + 1],
			n2 = neighbor[fp + 2];
		final boolean s0 = segment[fp++],
			s1 = segment[fp++],
			s2 = segment[fp++];
		if (!s0 && (n0 >= 0)) {
			if (!obsolete[n0])
				eatFace(n0);
		}
		if (!s1 && (n1 >= 0)) {
			if (!obsolete[n1])
				eatFace(n1);
		}
		if (!s2 && (n2 >= 0)) {
			if (!obsolete[n2])
				eatFace(n2);
		}
	}

	// check whether the boundary edges do not intersect
	void checkBoundary(final double[][] p) {
		final int numberOfBoundaryCurves = p.length;

		for (int i = 0; i < numberOfBoundaryCurves; i++) {
			final int currentCurveLength = p[i].length / 2;

			for (int j = 0; j < currentCurveLength; j++) {
				final double e0x0 = p[i][2 * j], e0y0 = p[i][2 * j + 1];
				final double e0x1 = p[i][2 * ((j + 1) % currentCurveLength)],
					e0y1 = p[i][2 * ((j + 1) % currentCurveLength) + 1];

				// Check whether any two segments of the same boundary curve intersect
				if (j < currentCurveLength - 2)
					for (int k = j + 2; k < currentCurveLength; k++) {
						if ((k == currentCurveLength) && (j == 0))
							break;
						if (edgesCross(e0x0,
							e0y0,
							e0x1,
							e0y1,
							p[i][2 * k],
							p[i][2 * k + 1],
							p[i][2 * (k + 1) % currentCurveLength],
							p[i][2 * (k + 1) % currentCurveLength + 1]))
							throw new RuntimeException("boundary semengts must not intersect !");
					}

				// Check whether any two segments of different boundary curves intersect
				if (i < numberOfBoundaryCurves - 1) {
					final int nextCurveLength = p[i + 1].length / 2;
					for (int k = 0; k < nextCurveLength; k++)
						if (edgesCross(e0x0,
							e0y0,
							e0x1,
							e0y1,
							p[i + 1][2 * k],
							p[i + 1][2 * k + 1],
							p[i + 1][2 * ((k + 1) % nextCurveLength)],
							p[i + 1][2 * ((k + 1) % nextCurveLength) + 1]))
							throw new RuntimeException("boundary semengts must not intersect !");
				}
			}
		}
	}

	// Two edges cross if either edge crosses the line through the other
	boolean edgesCross(
		final double e0x0,
		final double e0y0,
		final double e0x1,
		final double e0y1,
		final double e1x0,
		final double e1y0,
		final double e1x1,
		final double e1y1) {
		final double ax = e0x1 - e0x0,
			ay = e0y1 - e0y0,
			bx = e1x1 - e1x0,
			by = e1y1 - e1y0;
		// edges
		final double cx = e1x0 - e0x0,
			cy = e1y0 - e0y0,
			dx = e1x1 - e0x0,
			dy = e1y1 - e0y0;
		final double ex = e0x0 - e1x0,
			ey = e0y0 - e1y0,
			fx = e0x1 - e1x0,
			fy = e0y1 - e1y0;

		if ((ax * cy - ay * cx) * (ax * dy - ay * dx) >= 0)
			return false; // edge e1 does NOT cross line through e0
		if ((bx * ey - by * ex) * (bx * fy - by * fx) >= 0)
			return false; // edge e0 does NOT cross line through e1

		return true;
	}

}
