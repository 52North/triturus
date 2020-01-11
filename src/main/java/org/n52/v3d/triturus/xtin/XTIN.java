/**
 * Copyright (C) 2019 52North Initiative for Geospatial Open Source
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
package org.n52.v3d.triturus.xtin;

import java.util.ArrayList;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.gisimplm.GmSimpleTINFeature;
import org.n52.v3d.triturus.gisimplm.GmSimpleTINGeometry;
import org.n52.v3d.triturus.gisimplm.GmTriangle;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTIN;
import org.n52.v3d.triturus.vgis.VgTriangle;

/**
 * Triangular irregular network (TIN) implementation as dynamic, topological 
 * structure.
 * 
 * @author Benno Schmidt
 */
public class XTIN extends VgTIN 
{
	protected class Vtx3 {
		protected int v1, v2, v3;
		public Vtx3(int v1, int v2, int v3) {
			this.v1 = v1; this.v2 = v2; this.v3 = v3; 
		}
	}

	protected class VtxTri {
		protected int v, tri;
		public VtxTri(int v, int tri) {
			this.v = v;
			this.tri = tri;
		}
	}
	
	protected class EdgL {
		protected ArrayList<VtxTri> vOut; // edges starting from the i-th 
			// vertex directing to edg.get(i). As second pair component, the
			// incident triangle's index will be stored.
			// Note that some list elements might be empty, if the TIN consists
			// of unreferenced vertices.
		protected ArrayList<VtxTri> vIn; // edges directing to the i-th vertex
		public EdgL() {
			vOut = new ArrayList<VtxTri>();
			vIn = new ArrayList<VtxTri>();
		}
	}

	// Lists of vertices, edges, and triangles, 
	// all implicitly numbered by the ArrayList's indices:
	protected ArrayList<Vtx3> tri_vtx; // triangle -> 3 vertices
	protected ArrayList<EdgL> vtx_edg; // vertex -> 0..n outgoing edges 
	// .incTri: edge -> 1..n incident triangles (with respect to the edge's 
	// direction)
			
	private ArrayList<VgPoint> vtx_loc; // vertex positions
	
	
	/**
	 * construct an empty XTIN object.
	 */
	public XTIN() {
		tri_vtx = new ArrayList<Vtx3>();
		vtx_edg = new ArrayList<EdgL>();

		vtx_loc = new ArrayList<VgPoint>();
	}

	public void addTriangle(int v1, int v2, int v3) {
		assureVtxListSize(v1 + 1);
		assureVtxListSize(v2 + 1);
		assureVtxListSize(v3 + 1);		
//		System.out.println("add <" + v1 + " " + v2 + " " + v3 + ">");
		tri_vtx.add(new Vtx3(v1, v2, v3));
		int idxTri = tri_vtx.size() - 1; // index of added triangle

		insertOutEdg(v1, v2, idxTri);
		insertInEdg(v2, v1, idxTri);

		insertOutEdg(v2, v3, idxTri);
		insertInEdg(v3, v2, idxTri);

		insertOutEdg(v3, v1, idxTri);
		insertInEdg(v1, v3, idxTri);
	}

	private void assureVtxListSize(int size) {
		if (vtx_edg.size() < size) {
			for (int i = vtx_edg.size(); i < size; i++) {
				vtx_edg.add(null/*new EdgL()*/);
				// Note: vtx_edg might contain indices which are not referenced
				// inside the source structure. To query whether a vertex index
				// is referenced, the vtx_loc can be used.
			}
		}
	} 
	
	private void insertOutEdg(int v1, int v2, int tri) {
//		System.out.println("add [" + v1 + " " + v2 + "]");
		if (vtx_edg.get(v1) == null) {
			vtx_edg.set(v1, new EdgL());
		}
		vtx_edg.get(v1).vOut.add(new VtxTri(v2, tri));
	}

	private void insertInEdg(int v1, int v2, int tri) {
		if (vtx_edg.get(v1) == null) {
			vtx_edg.set(v1, new EdgL());
		}
		vtx_edg.get(v1).vIn.add(new VtxTri(v2, tri));
	}

	public void addLocation(int i, VgPoint pos) {
		assureLocListSize(i + 1);
		vtx_loc.set(i, pos);
	}

	private void assureLocListSize(int size) {
		if (vtx_loc.size() < size) {
			for (int i = vtx_loc.size(); i < size; i++) {
				vtx_loc.add(null);
				// Note: vtx_edg might contain indices which are not referenced
				// inside the source structure. To query whether a vertex index
				// is referenced, the vtx_loc can be used.
			}
		}
	} 
	
	/**
	 * returns the number of points (<i>vertices</i>) that are part of the TIN.
	 * Note that a call to this method is computing intensive; the runtime is
	 * O(N) with N = #vertices. 
	 * 
	 * @return Number of vertices
	 */
	@Override
	public int numberOfPoints() {
		return pointsStatistics()[1];
	}

	/**
	 * computes the number of vertices held in the XTIN structure. As a result,
	 * four numbers will be given in the resulting array <tt>res</tt>:
	 * <table>
	 *   <tr>
	 *     <td>res[0]</td><td># used index numbers</td>
	 *     <td>res[1]</td><td># referenced vertices</td>
	 *     <td>res[2]</td><td># vertices which are start-vertices of at least 1 edge</td>
	 *     <td>res[3]</td><td># georeferenced vertices (locations)</td>
	 *   </tr>
	 * </table>
	 * 
	 * @return Integer array holding the four numbers described above
	 * @throws T3dException
	 */
	public int[] pointsStatistics() throws T3dException
	{
		int ctUsedIndices = 0, ctReferenced = 0, ctTopologyParts = 0, ctLocations = 0; 
		for (int i = 0; i < vtx_edg.size(); i++) {
			EdgL x = vtx_edg.get(i);
			ctUsedIndices++;
			if (x != null) {
				ctReferenced++;
				if (x.vOut.size() > 0)
					ctTopologyParts++;
			}
		}
		if (vtx_edg.size() != ctUsedIndices) 
			throw new T3dException("Assertion error");
		for (int i = 0; i < vtx_loc.size(); i++) {
			if (vtx_loc.get(i) != null)
				ctLocations++;
		}
		//System.out.println(">> " + ctUsedIndices + " " + ctReferenced + " " + ctTopologyParts + " " + ctLocations);	
		return new int[]{ctUsedIndices, ctReferenced, ctTopologyParts, ctLocations};
	}

	@Override
	public int numberOfTriangles() {
		return tri_vtx.size();
	}

	/**
	 * returns the number of edges that are part of the TIN.
	 * 
	 * @return Number of edges
	 */
	public int numberOfEdges() {
		int ct = 0;
		for (int i = 0; i < vtx_edg.size(); i++) {
			ct += vtx_edg.get(i).vOut.size();
		}
		return ct;
	}

	@Override
	public VgPoint getPoint(int i) throws T3dException {
		if (i < 0 || i >= vtx_loc.size())
			throw new T3dException("Point index " + i + " out of bounds (0 ... " + (vtx_loc.size() - 1));
		return vtx_loc.get(i);
	}

	@Override
	public VgTriangle getTriangle(int i) throws T3dException {
		if (i < 0 || i >= vtx_loc.size())
			throw new T3dException("Point index " + i + " out of bounds (0 ... " + (vtx_loc.size() - 1));
		return new GmTriangle(
			vtx_loc.get(tri_vtx.get(i).v1), 
			vtx_loc.get(tri_vtx.get(i).v2), 
			vtx_loc.get(tri_vtx.get(i).v3));
	}

	public GmSimpleTINFeature asSimpleTINFeature() {
		int 
			N = this.numberOfPoints(),
			M = this.numberOfTriangles();
		GmSimpleTINGeometry geom = new GmSimpleTINGeometry(N, M);
		for (int i = 0; i < N; i++) {
			geom.setPoint(i, vtx_loc.get(i));
		}
		for (int i = 0; i < M; i++) {
			if (tri_vtx.get(i) == null) System.out.println(">>>" + i);
			geom.setTriangle(i, tri_vtx.get(i).v1, tri_vtx.get(i).v2, tri_vtx.get(i).v3);
		}
		GmSimpleTINFeature f = new GmSimpleTINFeature();
		f.setGeometry(geom);
		return f;
	}

	@Override
	public VgEnvelope envelope() {
		throw new T3dNotYetImplException(); // TODO (see GmSimpleTINGeometry)
	}

	@Override
	public VgGeomObject footprint() {
		throw new T3dNotYetImplException(); // TODO
	}
	
	public String toString() {
		return "[" +
			"(" + this.numberOfPoints() + " vertices), " +
			"(" + this.numberOfEdges() + " edges), " +
			"(" + this.numberOfTriangles() + " triangles)]";
	}
}
