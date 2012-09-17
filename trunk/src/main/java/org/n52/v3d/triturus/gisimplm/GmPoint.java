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
