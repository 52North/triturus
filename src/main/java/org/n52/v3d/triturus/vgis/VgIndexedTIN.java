/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold indexed TIN-structures (<tt>VgTIN</tt> specialization).<p>
 * The vertices that are part of the indexed TIN are numbered, the triangles (faces) can be referenced by these index
 * numbers.
 *
 * @author Benno Schmidt
 */
abstract public class VgIndexedTIN extends VgTIN
{
	/**
	 * returns the vertex-indices if the i-th triangle (face) inside the TIN.<br /><br />
	 * <i>German:</i> Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.
     *
     * @param i Triangle index
     * @return Array consisting of three indices
     * @throws T3dException
	 */
	abstract public int[] getTriangleVertexIndices(int i) throws T3dException;

	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " indexed vertices), " +
			"(# " + this.numberOfTriangles() + " triangles)]";
	}
}