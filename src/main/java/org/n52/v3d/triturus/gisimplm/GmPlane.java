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
     * Constructor. The plane will be constructed by three given points. For collinear points, a <tt>T3dException</tt>
     * will be thrown.
     * @param pt1 Point that is lying on the plane
     * @param pt2 Another point that is lying on the plane
     * @param pt3 And another point that is lying on the plane
     *
     */
    public GmPlane(VgPoint pt1, VgPoint pt2, VgPoint pt3)
    {
    	GmTriangle triangle = new GmTriangle(pt1, pt2, pt3);
    	this.init(triangle);
    }

    /** 
     * Constructor. The plane will be constructed by a triangle's corner-points. If the triangle's surface area is 0,
     * a <tt>T3dException</tt> will be thrown.
     * @param pTriangle Triangle that is lying on the plane
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

		VgPoint ret;
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
		VgPoint ret;
		ret = new GmPoint(pt);
		
		if (mC != 0.) {
			ret.setZ( (-mD - mA*pt.getX() - mB*pt.getY()) / mC); 
			return ret;
		}
		// sonst: C = 0:
		throw new T3dException("Numerical error. Can't get unique z-value for vertical plane.");
	}
}
