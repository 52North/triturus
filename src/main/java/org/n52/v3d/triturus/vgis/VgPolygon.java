/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * If the program is linked with libraries which are licensed under one of the         *
 * following licenses, the combination of the program with the linked library is not   *
 * considered a "derivative work" of the program:                                      *
 *                                                                                     *
 *   - Apache License, version 2.0                                                     *
 *   - Apache Software License, version 1.0                                            *
 *   - GNU Lesser General Public License, version 3                                    *
 *   - Mozilla Public License, versions 1.0, 1.1 and 2.0                               *
 *   - Common Development and Distribution License (CDDL), version 1.0                 *
 *                                                                                     *
 * Therefore the distribution of the program linked with libraries licensed under      *
 * the aforementioned licenses, is permitted by the copyright holders if the           *
 * distribution is compliant with both the GNU General Public License version 2 and    *
 * the aforementioned licenses.                                                        *
 *                                                                                     *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY     *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.            *
 *                                                                                     *
 **************************************************************************************/
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to manage polygonal geometries &quot;closed polylines&quot; containing an area).<br /><br />
 * <i>German:</i> Klasse zur Verwaltung polygonaler Geometrien ("geschlossene Polylinien" mit Fl&auml;cheneigenschaft).
 * Die Polygone m&uuml;ssen <i>planar</i> sein.<br />
 * <i>Planarit&auml;t wird z. Zt. in der Implementierung <tt>GmPlane</tt> noch nicht &uuml;berp&uuml;ft. Die
 * Fl&auml;chenberechnung ist nur f&uuml;r den 2D-Fall (xy-Ebene) Ebene implementiert. L&ouml;sungsansatz: Bestimmung
 * der zugeh&ouml;rigen Ebene (Klasse <tt>VgPlane</tt>) durch Minimierung der Abweichung der Polygon-Vertizes von der
 * Ebene; einfache Rechen&uuml;bung! -> todo</i>
 *
 * @author Benno Schmidt
 */
abstract public class VgPolygon extends VgGeomObject2d 
{
	/**
     * returns the polygon's i-th vertex. The constraint 0 &lt;= i &lt; <tt>this.numberOfCorners()</tt> must always
     * hold; otherwise a <tt>T3dException</tt> will be thrown.
     *
     * @return vertex
	 * @throws T3dException
	 */
	abstract public VgPoint getVertex(int i) throws T3dException;
	
	/**
     * returns the number of vertices.
     *
     * @return Number of vertices
     */
	abstract public int numberOfVertices();

    /**
     * returns the polygon area referring to the assigned coordinate reference system.<br /><br />
     * TODO: Die Methode ist noch nicht getestet!
     *
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	public double area() {

		return this.sumUpAreaXY()[0]; 
	}

 	/**
	 * returns the circumference of the polygon referring to the assigned coordinate reference system.<br /><br />
     * TODO: Die Methode ist noch nicht getestet!
      *
     * @return Circumference value
	 * @see VgGeomObject#getSRS
	 */
	public double circumference()
	{
		return this.sumUpAreaXY()[1]; 
	}

	private double[] sumUpAreaXY() 
	{
		int N = this.numberOfVertices();
		double A = 0., C = 0.;
			
		if (N >= 3) 
		{
			double dx, dy, dz, sx;
			VgPoint pt1, pt2;
		
			for (int i = 0; i < N - 1; i++) 
			{
				pt1 = this.getVertex(i);
				pt2 = this.getVertex(i + 1);
				dx = pt2.getX() - pt1.getX();
				dy = pt2.getY() - pt1.getY();
				dz = pt2.getZ() - pt1.getZ();
   				sx = pt1.getX() + pt2.getX();
   				A += sx * dy;
   				C += Math.sqrt(dx*dx + dy*dy);  
   			}
		
			pt1 = this.getVertex(N - 1);
			pt2 = this.getVertex(0);
			dx = pt2.getX() - pt1.getX();
			dy = pt2.getY() - pt1.getY();
   			sx = pt1.getX() + pt2.getX();
   			A += sx * dy;
   			C += Math.sqrt(dx*dx + dy*dy);  
   		}
   		else {
   			if (N == 2) 
   				C = this.getVertex(0).distanceXY(this.getVertex(1));
   		}
 
 		double[] ret = new double[2];
 		ret[0] = A;
 		ret[1] = C;
 		return ret;
	}

	public String toString() {
		String str = "[";
		if (this.numberOfVertices() > 0) {
			for (int i = 0; i < this.numberOfVertices() - 1; i++) {
				str = str + this.getVertex(i).toString() + ", ";
			}
			str = str + this.getVertex(this.numberOfVertices() - 1).toString();
		}
		return str + "]";
	}
}
