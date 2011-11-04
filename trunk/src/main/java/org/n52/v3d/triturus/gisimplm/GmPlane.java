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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgPlane;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTriangle;
import org.n52.v3d.triturus.core.T3dException;

/** 
 * <tt>VgPlane</tt>-implementation to hold planes in 3-D space.<br />
 * <i>todo: Klasse ist noch nicht getestet.</i>
 * @author Benno Schmidt
 */
public class GmPlane extends VgPlane
{
    private double mA, mB, mC, mD; // Ebenengleichung A*x + B*y + C*z + D = 0

    /** 
     * Construktor. The plane will be constructed by three given points. For collinear points, a <tt>T3dException</tt>
     * will be thrown.
     */
    public GmPlane(VgPoint pt1, VgPoint pt2, VgPoint pt3)
    {
    	GmTriangle triangle = new GmTriangle(pt1, pt2, pt3);
    	this.init(triangle);
    }

    /** 
     * Construktor. The plane will be constructed by a triangle's corner-points. If the triangle's surface area is 0,
     * a <tt>T3dException</tt> will be thrown.
     */
	public GmPlane(VgTriangle pTriangle) {
		this.init(pTriangle);
	}
	
	private void init(VgTriangle pTriangle) throws T3dException
	{
		if (pTriangle.area() < .000001)
			throw new T3dException("Plane instantiation failed. Maybe the definition points are collinear.");
			
		VgPoint pt1 = null, pt2 = null, pt3 = null;
		pTriangle.getCornerPoints(pt1, pt2, pt3);		
		
		T3dVector v1 = new T3dVector();
		v1.assignDiff(pt2, pt1);
		T3dVector v2 = new T3dVector();
		v1.assignDiff(pt3, pt1);

		T3dVector normal = new T3dVector();
		normal.assignCrossProd(v1, v2);

		mA = normal.getX();
		mB = normal.getY();
		mC = normal.getZ();
		mD = -1. * (mA * pt1.getX() + mB * pt1.getY() + mC * pt1.getZ());		
	}

	public VgPoint getNormal()
	{
		T3dVector hlp = new T3dVector(mA, mB, mC); 
		hlp.doNorm();

		VgPoint ret = null;
		ret = new GmPoint(hlp.getX(), hlp.getY(), hlp.getZ()); 
		ret.setSRS(this.getSRS());
		return ret;
	}

	public VgPoint getAnchor()
	{
		VgPoint ret;
		ret = new GmPoint(0., 0., 0.);
		ret.setSRS(this.getSRS());
		if (mC != 0.) {
			ret.setZ(-mD/mC); 
			return ret;
		}
		if (mB != 0.) {
			ret.setY(-mD/mB);
			return ret;
		}
		if (mA != 0.) {
			ret.setX(-mD/mA);
			return ret;
		}
		// sonst: A = B = C = 0:
		throw new T3dException("Numerical error.");
	}
	
	/**
	 * projects a point <tt>pt</tt> in z-direction to the x-y-plane. If the plane is parallel to the z-axis, a
     * <tt>T3dException</tt> will be thrown.
	 * @return <tt>GmPoint</tt> with same x- and y-coordinate as <tt>pt</tt>
	 * @throws T3dException
	 */
	public VgPoint projectPointZ(VgPoint pt)
	{
		VgPoint ret = null;
		ret = new GmPoint(pt);
		
		if (mC != 0.) {
			ret.setZ( (-mD - mA*pt.getX() - mB*pt.getY()) / mC); 
			return ret;
		}
		// sonst: C = 0:
		throw new T3dException("Numerical error. Can't get unique z-value for vertical plane.");
	}
}
