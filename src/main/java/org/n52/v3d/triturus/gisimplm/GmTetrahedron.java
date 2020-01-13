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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt, 52North Initiative for Geospatial Open Source 
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, 
 * b.schmidt@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTetrahedron;

/**
 * Tetrahedronal geometry implementation.
 * 
 * @author Benno Schmidt
 */
public class GmTetrahedron extends VgTetrahedron
{
    GmPoint p1, p2, p3, p4;
    private GmEnvelope env = null;
    private boolean envHasBeenCalculated = false;

    /* 
     * Constructor.
     * 
     * @param p1 First corner point
     * @param p2 Second corner point
     * @param p3 Third corner point
     * @param p4 Fourth corner point
     */
    public GmTetrahedron(VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4) 
    {
        this.setCornerPoints(p1, p2, p3, p4); 
    }

    public void setCornerPoints(VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4)
    {
        this.p1 = new GmPoint(p1);
        this.assertSRS(p2); 
        this.p2 = new GmPoint(p2);
        this.assertSRS(p3); 
        this.p3 = new GmPoint(p3);
        this.assertSRS(p4); 
        this.p4 = new GmPoint(p4);
        envHasBeenCalculated = false;
    }

    /**
     * returns the tetrahedron's corner-points.
     * 
     * @return Array consisting of four <tt>GmPoint</tt>-objects holding the corner-points
     */
    public VgPoint[] getCornerPoints() {
    	GmPoint[] res = new GmPoint[4];
        res[0] = p1; res[1] = p2; res[2] = p3; res[3] = p4;
        return res;
    }

    /**
     * returns the tetrahedron's center point.
     * 
     * @return Center point
     */
	public VgPoint getCenterPoint() {		
		GmPoint res = new GmPoint(
			(p1.getX() + p2.getX() + p3.getX() + p4.getX()) / 4.,
			(p1.getY() + p2.getY() + p3.getY() + p4.getY()) / 4.,
			(p1.getZ() + p2.getZ() + p3.getZ() + p4.getZ()) / 4.);
		res.setSRS(p1.getSRS());
		return res;
	}

    /** 
     * returns the geometry's bounding-box.
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
             env.letContainPoint(p4);
        }
        return env;
    }
}