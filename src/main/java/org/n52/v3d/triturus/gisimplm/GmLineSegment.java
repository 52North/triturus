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
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;

/**
 * <tt>VgLineSegment</tt>-implementation. Note that x- and y-values have to be
 * given with respect to the spatial reference system (SRS) that has been set 
 * for the geometric object.
 * 
 * @author Benno Schmidt
 */
public class GmLineSegment extends VgLineSegment
{
    private GmPoint mStart = new GmPoint(0., 0., 0.);
    private GmPoint mEnd = new GmPoint(0., 0., 0.);

    /**
     * Constructor
     * 
     * @param pStart Start point
     * @param pEnd End point
     */
    public GmLineSegment(VgPoint pStart, VgPoint pEnd) {
        mStart.set(pStart);
        mEnd.set(pEnd);
    }

    public void setStartPoint(VgPoint pStart) {
    	this.assertSRS(pStart);
        mStart.set(pStart);
    }

    public VgPoint getStartPoint() {
        return mStart;
    }

    public void setEndPoint(VgPoint pEnd) {
    	this.assertSRS(pEnd);
        mEnd.set(pEnd);
    }

    public VgPoint getEndPoint() {
        return mEnd;
    }

    public VgEnvelope envelope()
    {
        GmEnvelope env = new GmEnvelope(mStart);
        env.letContainPoint(mEnd);
        return env;
    }

	/** 
	 * returns the object's footprint geometry (projection to the x-y plane).
	 * 
	 * @return "Footprint" as <tt>GmLineSegment</tt>-object
  	 */
	public VgGeomObject footprint() {
		return new GmLineSegment(
			(VgPoint) this.getStartPoint().footprint(),
			(VgPoint) this.getEndPoint().footprint());
	}

    /**
     * provides the direction vector defined by the line-segment.
     */
    public VgPoint getDirection() {
        double dx = this.getEndPoint().getX() - this.getStartPoint().getX();
        double dy = this.getEndPoint().getY() - this.getStartPoint().getY();
        double dz = this.getEndPoint().getZ() - this.getStartPoint().getZ();
        return new GmPoint(dx, dy, dz);
    }
}