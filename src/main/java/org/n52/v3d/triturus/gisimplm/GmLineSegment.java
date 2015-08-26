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
 * <tt>VgLineSegment</tt>-implementation. Object information will be kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system (SRS) that has been set for the
 * geometric object.<br /><br />
 * @author Benno Schmidt
 */
public class GmLineSegment extends VgLineSegment
{
    private GmPoint mPStart = new GmPoint(0., 0., 0.);
    private GmPoint mPEnd = new GmPoint(0., 0., 0.);

    /**
     * Constructor
     */
    public GmLineSegment(VgPoint pPStart, VgPoint pPEnd) {
        mPStart.set(pPStart);
        mPEnd.set(pPEnd);
    }

    public void setStartPoint(VgPoint pPStart) {
    	this.assertSRS(pPStart);
        mPStart.set(pPStart);
    }

    public VgPoint getStartPoint() {
        return mPStart;
    }

    public void setEndPoint( VgPoint pPEnd ) {
    	this.assertSRS(pPEnd);
        mPEnd.set( pPEnd );
    }

    public VgPoint getEndPoint() {
        return mPEnd;
    }

    public VgEnvelope envelope()
    {
        GmEnvelope mEnv = new GmEnvelope(mPStart);
        mEnv.letContainPoint(mPEnd);
        return mEnv;
    }

	/** 
	 * returns the object's footprint geometry (projection to the x-y-plane).<br /><br />
	 * <i>German:</i> liefert das zugeh�rige "Footprint"-Liniensegment.<p>
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