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
 * Class to hold TINs (&quot;triangulated irregular networks&quot;).<br /><br />
 * <i>German:</i> Bem.: In <tt>VgTIN</tt>-Objekten ist die Verwendung indizierter Vertizes nicht notwendig (hierdurch
 * werden die Anforderungen an die nutzbaren Implementierungen kleinstm�glich gehalten!). Die spezialisierte
 * Schnittstelle <tt>VgIndexedTIN</tt> erm�glicht stattdessen den Zugriff auf indizierte Vertizes.
 * @see VgIndexedTIN
 * @author Benno Schmidt
 */
abstract public class VgTIN extends VgGeomObject2d 
{
	/**
     * returns the number of points that are part of the TIN (<i>vertices</i>).
     * @return Number of vertices
     */
	abstract public int numberOfPoints();

	/**
     * returns the number of triangles that are part of the TIN (<i>facets</i>).
     * @return Number of facets
     */
	abstract public int numberOfTriangles();

	/**
	 * returns the i-th point (vertex) of the TIN structure.<br /><br />
     * <i>German:</i> Es ist stets die Bedingung 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.
     * @param i Point index
     * @return Vertex object
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
     * returns the i-th triangle (facet) of the TIN structure.<br /><br />
     * <i>German:</i> Es ist stets die Bedingung 0 &lt;<= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten;
     * anderenfalls wird eine <tt>T3dException</tt> geworfen.
     * @return Triangle object
	 */
	abstract public VgTriangle getTriangle(int i) throws T3dException;

	/**
     * returns the TIN's surface area.
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
    public double area()
	{
		double sum = 0.;
		VgTriangle tri;
		for (int i = 0; i < this.numberOfTriangles(); i++) {
			tri = this.getTriangle(i);
			sum += tri.area();
		}
		return sum;
	}
	
	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " vertices), " +
			"(# " + this.numberOfTriangles() + " triangles)]";
	}
}