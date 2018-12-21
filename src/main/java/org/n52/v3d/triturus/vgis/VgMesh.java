/**
 * Copyright (C) 2007-2018 52North Initiative for Geospatial Open Source
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
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * @deprecated
 * 
 * Class to hold meshes.
 * TODO: check is class can be replaced by VgLineSegmentTopology
 * 
 * @author
 * @see org.n52.v3d.triturus.vgis.VgLineSegmentTopology
 */
abstract public class VgMesh extends VgGeomObject 
{
	/** 
	 * gets the number of points (vertices) of the mesh.
	 */
	abstract public int getNumberOfPoints();

	/**
	 * gets the number of LineSegments (edges) of the mesh. 
	 */
	abstract public int getNumberOfLineSegments();

	/**
	 * gets the i-th point (vertex) of the mesh. The assertion 
	 * <i>0 &lt;= i &lt; this.getNumberOfPoints()</i> must hold, otherwise
	 * a <i>T3dException</i> will be thrown.
	 * 
	 * @throws T3dException
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 * gets the i-th LineSegment (edge) of the mesh. The assertion
	 * <i>0 &lt;= i &lt;  this.getNumberOfLineSegments()</i> must hold;
	 * otherwise a <i>T3dException</i> will be thrown.
	 * 
	 * @throws T3dException
	 */
	abstract public VgLineSegment getLineSegment(int i) throws T3dException;

	/**
	 * gets the vertex-indices of the i-th LineSegment (edge) of the mesh.
	 * The Assertion <i>0 &lt;= i &lt; this.getNumberOfLineSegments()</i> must hold;
	 * otherwise a <i>T3dException</i> will be thrown.
	 * 
	 * @throws T3DException
	 */
	abstract public int[] getLineSegmentVertexIndices(int i) throws T3dException;

	/**
	 * gets ann array with indices of all vertices. Per LineSegment a vertex pair
	 * will be given.
	 * 
	 * @return Array of size 2 * this.getNumberOfLineSegments
	 */
	abstract public int[] getLineIndexArray();

	/**
	 * provides the information whether two vertices are connected by a LineSegment 
	 * inside the mesh. The assertions <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i>
	 * and <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i> must hold; otherwise
	 * a <i>T3dException</i> will be thrown.
	 * 
	 * @param vertex1 Index of first vertex
	 * @param vertex2 Index of second vertex
	 * @return <i>true</i> if the LineSegment is part of the mesh, else <i>false</i>
	 * @throws T3DException
	 *
	 * todo 1. better int[] as parameter
	 * todo 2. rename method??
	 */
	abstract public boolean areConnected(int vertex1, int vertex2) throws T3dException;

	/**
	 * add a LineSegment connecting two vertices. The assertions 
	 * <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i> 
	 * and <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i> must hold, otherwise a 
	 * <i>T3dException</i> will be thrown.
	 * 
	 * @param vertex1 Index of first vertex
	 * @param vertex2 Index of second vertex
	 * @throws T3DException
	 */
	abstract public void addLineSegment(int vertex1, int vertex2) throws T3dException;
	
	public String toString() {
		return "[" +
			"(# " + this.getNumberOfPoints() + " vertices), " +
			"(# " + this.getNumberOfLineSegments() + " triangles)]";
	}
}