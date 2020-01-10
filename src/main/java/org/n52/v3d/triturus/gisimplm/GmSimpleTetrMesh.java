/**
 * Copyright (C) 2020 52North Initiative for Geospatial Open Source 
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.*;

/**
 * Class to hold a simple tetrehedronal mesh.
 * 
 * @author Benno Schmidt
 */
public class GmSimpleTetrMesh extends VgIndexedTetrMesh
{
	private List<VgPoint> point;
	private List<Int4> tetra;

	private class Int4 {
		public int v0, v1, v2, v3;
		public Int4(int v0, int v1, int v2, int v3) { 
			this.v0 = v0; this.v1 = v1; this.v2 = v2; this.v3 = v3;
		}
	}
	
	/**
     * Constructor.
	 */
	public GmSimpleTetrMesh()
    {
		point = new ArrayList<VgPoint>();
		tetra = new ArrayList<Int4>();
	}

	public int numberOfPoints() {
		return point == null ? 0 : point.size();
	}

	public int numberOfTetrahedrons() {
		return tetra == null ? 0 : tetra.size();
	}

	public VgPoint getPoint(int i) throws T3dException {
		try {
			return point.get(i);
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

    /**
	 * adds a point (vertex) to the mesh structure. The condition 
	 * 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> will hold. Note that the
	 * calling application has to keep control over the point indices which 
	 * will be generated automatically; here the given point <tt>p</tt> will 
	 * get the index <tt>this.numberOfPoints</tt>.
	 * 
     * @param Vertex point 
	 */
	public void addPoint(VgPoint p) throws T3dException {
		try {
			point.add(p);
		}
		catch (T3dException e) {
			throw e;
		}
	}

	public VgTetrahedron getTetrahedron(int i) throws T3dException {
		try {
			Int4 pos= tetra.get(i);
			return pos == null ? null : 
				new GmTetrahedron(
						point.get(tetra.get(i).v0),
						point.get(tetra.get(i).v1),
						point.get(tetra.get(i).v2),
						point.get(tetra.get(i).v3));
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * adds a tetrahedron to the mesh structure. <tt>idx1</tt>, <tt>idx2</tt>, 
     * <tt>idx3</tt>, and <tt>idx4</tt> give the indices of the tetrahedron's 
     * corner points. The condition 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> 
     * will hold. 
	 */
    public void addTetrahedron(int idx1, int idx2, int idx3, int idx4)
		throws T3dException 
    {
		try {
			tetra.add(new Int4(idx1, idx2, idx3, idx4));
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	public int[] getTetrahedronVertexIndices(int i) throws T3dException {
		try {
			Int4 pos = tetra.get(i);
			return pos == null ? null : new int[] { pos.v0, pos.v1, pos.v2, pos.v3 };
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	@Override
	public double area() {
		throw new T3dNotYetImplException(); // TODO
	}

	@Override
	public VgEnvelope envelope() {
		throw new T3dNotYetImplException(); // TODO
	}

	@Override
	public VgGeomObject footprint() {
		throw new T3dNotYetImplException(); // TODO
	}
}
