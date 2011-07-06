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