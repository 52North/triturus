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
import org.n52.v3d.triturus.vgis.VgWedge;

/**
 * 3-D wedge geometry implementation. As often used in the field of solid 
 * geometry, here a <i>wedge<i> describes a polyhedron defined by two triangles 
 * and three trapezoid faces. Thus, a wedge consists of 5 faces (2 triangles 
 * and 3 trapeziod faces with 4 corners), 9 edges, and 6 vertices).
 * 
 * @author Benno Schmidt
 */
public class GmWedge extends VgWedge
{
    GmPoint p0, p1, p2, p3, p4, p5;
    private GmEnvelope env = null;
    private boolean envHasBeenCalculated = false;

    /* 
     * Constructor.
     * 
     * @param p0 1st corner point (base triangle)
     * @param p1 2nd corner point (base triangle)
     * @param p2 3rd corner point (base triangle)
     * @param p3 4th corner point (connected with 1st point)
     * @param p4 5th corner point (connected with 2nd point)
     * @param p5 6th corner point (connected with 3rd point)
     */
    public GmWedge(VgPoint p0, VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4, VgPoint p5) 
    {
        this.setCornerPoints(p0, p1, p2, p3, p4, p5); 
    }

    public void setCornerPoints(
        VgPoint p0, VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4, VgPoint p5)
    {
        this.p0 = new GmPoint(p0);
        this.assertSRS(p1); 
        this.p1 = new GmPoint(p1);
        this.assertSRS(p2); 
        this.p2 = new GmPoint(p2);
        this.assertSRS(p3); 
        this.p3 = new GmPoint(p3);
        this.assertSRS(p4); 
        this.p4 = new GmPoint(p4);
        this.assertSRS(p5); 
        this.p5 = new GmPoint(p5);
        envHasBeenCalculated = false;
    }

    /**
     * returns the wedges's corner-points.
     * 
     * @return Array consisting of six <tt>GmPoint</tt>-objects holding the corner-points
     */
    public VgPoint[] getCornerPoints() {
        GmPoint[] res = new GmPoint[6];
        res[0] = p0; 
        res[1] = p1; 
        res[2] = p2; 
        res[3] = p3; 
        res[4] = p4; 
        res[5] = p5;
        return res;
    }

    /**
     * returns the wedges's center point.
     * 
     * @return Center point
     */
    public VgPoint getCenterPoint() {       
        GmPoint res = new GmPoint(
            (p0.getX() + p1.getX() + p2.getX() + p3.getX() + p4.getX() + p5.getX()) / 6.,
            (p0.getY() + p1.getY() + p2.getY() + p3.getY() + p4.getY() + p5.getY()) / 6.,
            (p0.getZ() + p1.getZ() + p2.getZ() + p3.getZ() + p4.getZ() + p5.getZ()) / 6.);
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
                 p0.getX(), p0.getX(),
                 p0.getY(), p0.getY(),
                 p0.getZ(), p0.getZ() );
             env.letContainPoint(p1);
             env.letContainPoint(p2);
             env.letContainPoint(p3);
             env.letContainPoint(p4);
             env.letContainPoint(p5);
        }
        return env;
    }
}