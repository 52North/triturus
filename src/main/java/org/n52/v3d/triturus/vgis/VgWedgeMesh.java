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
 * Class to hold wedge meshes. Note that <tt>VgWedgeMesh</tt> objects 
 * do not use indexed vertices. (This way, the requirements for concrete 
 * implementations are kept low.) Instead, the specialized interface 
 * <tt>VgIndexedWedgeMesh</tt> allows to access structures consisting of 
 * indexed vertices.
 * 
 * @see VgIndexedWedgeMesh
 * @see VgWedge
 * @author Benno Schmidt
 */
abstract public class VgWedgeMesh extends VgGeomObject3d 
{
	/**
	 * returns the number of points ('vertices') that are part of the mesh.
	 * 
	 * @return Number of vertices
	 */
	abstract public int numberOfPoints();
	
	/**
	 * returns the number of wedges that are part of the mesh.
	 * 
	 * @return Number of wedges
	 */
	abstract public int numberOfWedges();
	
	/**
	 * returns the i-th point (vertex) of the mesh structure. The assertion
	 * 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> must hold; otherwise
	 * a <tt>T3dException</tt> will be thrown.
	 * 
	 * @param i Point index
	 * @return Vertex object
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 * returns the i-th wedge of the mesh structure. The assertion
	 * 0 &lt;<= i &lt; <tt>this.numberOfWedges()</tt> must hold; 
	 * otherwise a <tt>T3dException</tt> will be thrown.
	 * 
	 * @return Wedge object
	 */
	abstract public VgWedge getWedge(int i) throws T3dException;
	
	public String toString() {
		return "[" +
			"(" + this.numberOfPoints() + " vertices), " +
			"(" + this.numberOfWedges() + " wedges)]";
	}
}