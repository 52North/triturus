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