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

import org.n52.v3d.triturus.vgis.*;

/**
 * Implementing class to manage triangles with arbitrary orientation in 3-D space.<br /><br />
 * <i>German:</i> Implementierende Klasse zur Verwaltung (beliebig im Raum orientierter) Dreiecke.
 * @author Benno Schmidt
 */
public class GmTriangle extends VgTriangle 
{
    GmPoint mP1, mP2, mP3;
    private GmEnvelope mEnv = null;
    private boolean mEnvIsCalculated = false;

    /* 
     * Constructor.
     */
    public GmTriangle(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3) 
    {
        this.setCornerPoints(pCorner1, pCorner2, pCorner3); 
    }

    public void setCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3)
    {
        mP1 = new GmPoint(pCorner1);
        this.assertSRS(pCorner2); 
        mP2 = new GmPoint(pCorner2);
        this.assertSRS(pCorner3); 
        mP3 = new GmPoint(pCorner3);
        mEnvIsCalculated = false;
    }
    
	public void getCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3) {
		pCorner1 = mP1;
		pCorner2 = mP2;
		pCorner3 = mP3;
	}

    /**
     * returns the triangle's corner-points.
     * @return Array consisting of three <tt>GmPoint</tt>-objects holding the corner-points
     */
    public VgPoint[] getCornerPoints() {
    	GmPoint[] res = new GmPoint[3];
        res[0] = mP1; res[1] = mP2; res[2] = mP3;
        return res;
    }

    /** 
     * returns the TIN-geometry's bounding-box.
     * @return Bounding-box
     */
    public VgEnvelope envelope() 
    {
        if (!mEnvIsCalculated) {
             mEnv = new GmEnvelope( 
                 mP1.getX(), mP1.getX(),
                 mP1.getY(), mP1.getY(),
                 mP1.getZ(), mP1.getZ() );
             mEnv.letContainPoint(mP2);
             mEnv.letContainPoint(mP3);
        }
        return mEnv;
    }
    
	/**
     * returns the TIN's footprint.
     * @return Footprint as <tt>GmTriangle</tt>-object
  	 */
	public VgGeomObject footprint() {
		return new GmTriangle(
			(VgPoint) mP1.footprint(), 
			(VgPoint) mP2.footprint(), 
			(VgPoint) mP3.footprint());
	}
}