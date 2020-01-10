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
 * Class to hold indexed tetrahedronal structures (<tt>VgTetrMesh</tt> 
 * specialization). The vertices that are part of the indexed mesh are 
 * numbered, the tetrahedron elements can be referenced by these index numbers.
 *
 * @author Benno Schmidt
 */
abstract public class VgIndexedTetrMesh extends VgTetrMesh
{
	/**
	 * returns the vertex-indices if the i-th tetrahedron inside the mesh.
	 * The assertion 0 &lt;= i &lt; <tt>this.numberOfTetrahdrons()</tt> must 
	 * hold; otherwise a <tt>T3dException</tt> will be thrown.
	 *
	 * @param i Tetrahedron index
	 * @return Array consisting of four indices
	 * @throws T3dException
	 */
	abstract public int[] getTetrahedronVertexIndices(int i) throws T3dException;
	
	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " indexed vertices), " +
			"(# " + this.numberOfTetrahedrons() + " tetrahedrons)]";
	}
}