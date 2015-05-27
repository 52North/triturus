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
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmLinearRing;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgGeomObject1d;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Class to manage linearRing geometries (&quot;closed polylines /
 * lineStrings&quot; containing an area).
 * 
 * @author Christian Danowski
 *
 */
public abstract class VgLinearRing extends VgGeomObject1d {

	/**
	 * returns the polygon's i-th vertex. The constraint 0 &lt;= i &lt;
	 * <tt>this.numberOfCorners()</tt> must always hold; otherwise a
	 * <tt>T3dException</tt> will be thrown.
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
	abstract public int getNumberOfVertices();

	/**
	 * returns the circumference of the polygon referring to the assigned
	 * coordinate reference system.<br />
	 * <br />
	 * 
	 * @return Circumference value
	 * @see VgGeomObject#getSRS
	 */
	public double circumference() {
		return this.sumUpArea()[1];
	}
	
	/**
	 * returns the polygon area referring to the assigned coordinate reference
	 * system.<br />
	 * <br />
	 * 
	 * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	public double area() {

		return this.sumUpArea()[0];
	}
	
	private double[] sumUpArea() {
		int N = this.getNumberOfVertices();
		double A = 0., C = 0., C3d = 0.;

		if (N >= 3) {
			double dx, dy, dz, sx;
			VgPoint pt1, pt2;

			for (int i = 0; i < N - 1; i++) {
				pt1 = this.getVertex(i);
				pt2 = this.getVertex(i + 1);
				dx = pt2.getX() - pt1.getX();
				dy = pt2.getY() - pt1.getY();
				dz = pt2.getZ() - pt1.getZ();
				sx = pt1.getX() + pt2.getX();
				A += sx * dy;
				C += Math.sqrt(dx * dx + dy * dy);
				C3d += Math.sqrt(dx * dx + dy * dy + dz * dz); 
			}

			pt1 = this.getVertex(N - 1);
			pt2 = this.getVertex(0);
			dx = pt2.getX() - pt1.getX();
			dy = pt2.getY() - pt1.getY();
			dz = pt2.getZ() - pt1.getZ();
			sx = pt1.getX() + pt2.getX();
			A += sx * dy;
			C += Math.sqrt(dx * dx + dy * dy);
			C3d += Math.sqrt(dx * dx + dy * dy + dz * dz); 
		} else {
			if (N == 2){
				C = this.getVertex(0).distanceXY(this.getVertex(1));
				C3d = this.getVertex(0).distance(this.getVertex(1));
			}
		}

		double[] ret = new double[3];
		ret[0] = A;
		ret[1] = C;
		ret[2] = C3d;
		return ret;
	}
	
	@Override
	public double length() {
		return this.sumUpArea()[2];
	}

	@Override
	public double lengthXY() {
		return this.circumference();
	}

	public String toString() {
		String str = "[";
		if (this.getNumberOfVertices() > 0) {
			for (int i = 0; i < this.getNumberOfVertices() - 1; i++) {
				str = str + this.getVertex(i).toString() + ", ";
			}
			str = str
					+ this.getVertex(this.getNumberOfVertices() - 1).toString();
		}
		return str + "]";
	}

}
