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

import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgGeomObject2d;

/**
 * Class to manage polygonal geometries (&quot;closed polylines&quot; containing
 * an area). Inner holes are supported.<br />
 * <br />
 * <i>German:</i> Klasse zur Verwaltung polygonaler Geometrien
 * ("geschlossene Polylinien" mit Fl&auml;cheneigenschaft). Es k&ouml;nnen auch
 * innere L&ouml;cher definiert werden. In der aktuellen Version wird jedoch
 * nicht &uuml;berpr&uuml;ft, ob die L&ouml;cher g&uuml;ltig sind, sprich ob sie
 * den Simple-Feature-Bedingungen des OGC entsprechen. Ein inneres Loch darf den
 * äu&szlig;eren Rand (outer boundary) des Polygons maximal in einem Punkt
 * ber&uuml;hren. Dies wird jedoch ebenfalls aktuell nicht gepr&uuml;ft.<br/>
 * Die Polygone m&uuml;ssen <i>planar</i> sein.<br />
 * <i>Planarit&auml;t wird z. Zt. in der Implementierung <tt>GmPlane</tt> noch
 * nicht &uuml;berp&uuml;ft. Die Fl&auml;chenberechnung ist nur f&uuml;r den
 * 2D-Fall (xy-Ebene) Ebene implementiert. L&ouml;sungsansatz: Bestimmung der
 * zugeh&ouml;rigen Ebene (Klasse <tt>VgPlane</tt>) durch Minimierung der
 * Abweichung der Polygon-Vertizes von der Ebene; einfache Rechen&uuml;bung! ->
 * todo</i>
 * 
 * @author Christian Danowski, Benno Schmidt
 * 
 */
public abstract class VgPolygon extends VgGeomObject2d {


	/**
	 * Gets the outer/exterior boundary of the polygon.
	 * 
	 * @return
	 */
	public abstract VgLinearRing getOuterBoundary();

	/**
	 * Gets the number of interior holes (interior boundaries) of the polygon.
	 * 
	 * @return
	 */
	public abstract int getNumberOfHoles();

	/**
	 * Gets the i-th hole / interior boundary.
	 * 
	 * Note that the parameter i must conform to the following rule: <br/>
	 * <code>0 &lt;= i &lt; {@link #getNumberOfHoles()}</code>.
	 * 
	 * @param i
	 * @return
	 */
	public abstract VgLinearRing getHole(int i);

	/**
	 * returns the polygon area referring to the assigned coordinate reference
	 * system.<br />
	 * <br />
	 * 
	 * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	public double area() {

		double areaWithHoles = this.getOuterBoundary().area();

		double areaOfAllHoles = 0;

		for (int i = 0; i < this.getNumberOfHoles(); i++) {
			VgLinearRing hole = this.getHole(i);

			areaOfAllHoles += hole.area();
		}

		return areaWithHoles - areaOfAllHoles;
	}

	/**
	 * returns the circumference of the polygon referring to the assigned
	 * coordinate reference system.<br />
	 * <br />
	 * 
	 * @return Circumference value
	 * @see VgGeomObject#getSRS
	 */
	public double circumference() {
		return this.getOuterBoundary().circumference();
	}

	public String toString() {
		String str = "Polygon: [OuterBoundary: [";
		VgLinearRing outerBoundary = this.getOuterBoundary();
		if (outerBoundary.getNumberOfVertices() > 0) {
			for (int i = 0; i < outerBoundary.getNumberOfVertices() - 1; i++) {
				str = str + outerBoundary.getVertex(i).toString() + ", ";
			}
			str = str
					+ outerBoundary.getVertex(
							outerBoundary.getNumberOfVertices() - 1).toString();
		}
		str = str + "], Inner boundaries: [";

		if (this.getNumberOfHoles() > 0) {

			for (int j = 0; j < this.getNumberOfHoles(); j++) {
				str = str + "[";
				VgLinearRing innerBoundary = this.getHole(j);
				if (innerBoundary.getNumberOfVertices() > 0) {
					for (int i = 0; i < innerBoundary.getNumberOfVertices() - 1; i++) {
						str = str + innerBoundary.getVertex(i).toString()
								+ ", ";
					}
					str = str
							+ innerBoundary.getVertex(
									innerBoundary.getNumberOfVertices() - 1)
									.toString();
					str = str + "] ";
				}
			}
		}

		return str + "]";
	}

}