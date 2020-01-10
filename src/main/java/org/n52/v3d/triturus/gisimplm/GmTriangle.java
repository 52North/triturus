/**
 * Copyright (C) 2007-2019 52 North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt, 52 North Initiative for Geospatial Open Source 
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, 
 * b.schmidt@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;

/**
 * Implementing class to manage triangles with arbitrary orientation in 3D 
 * space.
 * 
 * @author Benno Schmidt
 */
public class GmTriangle extends VgTriangle 
{
    GmPoint p1, p2, p3;
    private GmEnvelope env = null;
    private boolean envHasBeenCalculated = false;

    /* 
     * Constructor.
     * 
     * @param p1 First corner point
     * @param p2 Second corner point
     * @param p3 Third corner point
     */
    public GmTriangle(VgPoint p1, VgPoint p2, VgPoint p3) 
    {
        this.setCornerPoints(p1, p2, p3); 
    }

    public void setCornerPoints(VgPoint p1, VgPoint p2, VgPoint p3)
    {
        this.p1 = new GmPoint(p1);
        this.assertSRS(p2); 
        this.p2 = new GmPoint(p2);
        this.assertSRS(p3); 
        this.p3 = new GmPoint(p3);
        envHasBeenCalculated = false;
    }

    /**
     * returns the triangle's corner-points.
     * 
     * @return Array consisting of three <tt>GmPoint</tt>-objects holding the corner-points
     */
    public VgPoint[] getCornerPoints() {
    	GmPoint[] res = new GmPoint[3];
        res[0] = p1; res[1] = p2; res[2] = p3;
        return res;
    }

    /**
     * returns the triangle's center point.
     * 
     * @return Center point
     */
	public VgPoint getCenterPoint() {		
		GmPoint res = new GmPoint(
			(p1.getX() + p2.getX() + p3.getX()) / 3.,
			(p1.getY() + p2.getY() + p3.getY()) / 3.,
			(p1.getZ() + p2.getZ() + p3.getZ()) / 3.);
		res.setSRS(p1.getSRS());
		return res;
	}

    /** 
     * returns the TIN-geometry's bounding-box.
     * 
     * @return Bounding-box
     */
    public VgEnvelope envelope() 
    {
        if (!envHasBeenCalculated) {
             env = new GmEnvelope( 
                 p1.getX(), p1.getX(),
                 p1.getY(), p1.getY(),
                 p1.getZ(), p1.getZ() );
             env.letContainPoint(p2);
             env.letContainPoint(p3);
        }
        return env;
    }
    
	/**
     * returns the TIN's footprint.
     * 
     * @return Footprint as <tt>GmTriangle</tt>-object
  	 */
	public VgGeomObject footprint() {
		return new GmTriangle(
			(VgPoint) p1.footprint(), 
			(VgPoint) p2.footprint(), 
			(VgPoint) p3.footprint());
	}
}