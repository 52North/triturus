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

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.core.T3dException;

/**
 * <tt>VgPoint</tt>-implementation. Object information will be kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system (SRS) that has been set for the
 * geometric object.
 * @author Benno Schmidt
 */
public class GmPoint extends VgPoint
{
    private double mX, mY, mZ;

    /**
     * Constructor.
     */
    public GmPoint(double pX, double pY, double pZ) {
        mX = pX;
        mY = pY;
        mZ = pZ;
    }

    /**
     * Constructor. x, y and z will be set to 0.
     */
    public GmPoint() {
        mX = 0.;
        mY = 0.;
        mZ = 0.;
    }

    /**
     * Constructor. x, y and z will be set to the coordinates of the given point.
     */
    public GmPoint(VgPoint pt) {
    	this.set(pt);
    }

    /**
     * Constructor. x, y and z will be set according the values given in a comma-separated coordinate list. If no
     * z-value is specified, z will be set to 0.<br />
     * Examples: <tt>&quot;3500000,5800000&quot;, &quot;3500000,5800000,50.5&quot;</tt><br />
     * If the given string can not be interpreted, a <tt>T3dException</tt> will be thrown.
     * @param pCommaSeparatedList List consisting of 2 or 3 coordinate-values
     */
    public GmPoint(String pCommaSeparatedList) {
        String[] coords = pCommaSeparatedList.split(",");
        if (coords.length < 2 || coords.length > 3)
            throw new T3dException("Cannot parse geo-coordinates from \"" + pCommaSeparatedList + "\".");
        mX = Double.parseDouble(coords[0]);
        mY = Double.parseDouble(coords[1]);
        if (coords.length >= 3)
            mZ = Double.parseDouble(coords[2]);
        else
            mZ = 0.;
    }

	/**
     * setzt den x-Wert der Punktgeometrie.<p>
	 * Bem.: F�r <i>geografische Koordinaten</i> (EPSG:4326) ist als x-Wert die geografische Breite anzugeben, f�r
     * <i>Gau�-Kr�ger-Koordinaten</i> der Rechtswert.<p>
	 */
    public void setX(double pX) { 
        mX = pX; 
    }

    public double getX() {
        return mX;
    }

    public void setY(double pY) {
        mY = pY; 
    }

    public double getY() {
        return mY;
    }

    public void setZ(double pZ) {
        mZ = pZ;
    }

    public double getZ() {
        return mZ;
    }

    public VgEnvelope envelope() {
        VgEnvelope lEnv = new GmEnvelope(mX, mX, mY, mY, mZ, mZ);
        return lEnv;
    }

	/** 
     * returns the object's footprint geometry (projection to the x-y-plane).
	 * @return "Footprint" als <tt>GmPoint</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return new GmPoint(mX, mY, 0.);
	}

    /**
     * @deprecated
     * returns the distance between two points with respect to the x-y-plane. If the points refer to different
     * coordinate reference systems, a <tt>T3dSRSException</tt> will be thrown.
     * @throws org.n52.v3d.triturus.vgis.T3dSRSException
     * @see VgPoint#distanceXY
     */
    public double distance2d(GmPoint pPnt) throws T3dException
    {
       if (!this.getSRS().equalsIgnoreCase(pPnt.getSRS()))
          throw new T3dException("Spatial reference systems are incompatible.");

       if (this.getSRS().equalsIgnoreCase("EPSG:31411") // GKK3
           || this.getSRS().equalsIgnoreCase("EPSG:31412") // GKK4
           || this.getSRS().equalsIgnoreCase("EPSG:31413")) // GKK5
       {
           double dx = pPnt.getX() - this.getX();
           double dy = pPnt.getY() - this.getY();
           return Math.sqrt(dx * dx + dy * dy);
       }
       // else:
       throw new T3dSRSException("Spatial reference systems is not supported (yet).");
    }

    /** 
     * @deprecated
     * returns the distance between two points with respect in 3-D space. If the points refer to different
     * coordinate reference systems, a <tt>T3dSRSException</tt> will be thrown.
     * @throws T3dSRSException
     * @see VgPoint#distance
     */
    public double distance3d(GmPoint pPnt) throws T3dSRSException
    {
       if (!this.getSRS().equalsIgnoreCase(pPnt.getSRS()))
          throw new T3dSRSException("Spatial reference systems are incompatible.");
       if (this.getSRS().equalsIgnoreCase("EPSG:31411") // GKK3
           || this.getSRS().equalsIgnoreCase("EPSG:31412") // GKK4
           || this.getSRS().equalsIgnoreCase("EPSG:31413")) // GKK5
       {
           double dx = pPnt.getX() - this.getX();
           double dy = pPnt.getY() - this.getY();
           double dz = pPnt.getZ() - this.getZ();
           return Math.sqrt(dx * dx + dy * dy + dz * dz);
       }
       // else:
       throw new T3dSRSException("Spatial reference systems is not supported (yet).");
    }
}
