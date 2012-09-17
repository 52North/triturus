/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold indexed TIN-structures (<tt>VgTIN</tt> specialization).<p>
 * The vertices that are part of the indexed TIN are numbered, the triangles (faces) can be referenced by these index
 * numbers.
 * @author Benno Schmidt
 */
abstract public class VgIndexedTIN extends VgTIN
{
	/**
	 * returns the vertex-indices if the i-th triangle (face) inside the TIN.<br /><br />
	 * <i>German:</i> Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.
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