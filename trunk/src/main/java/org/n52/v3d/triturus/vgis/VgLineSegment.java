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
package org.n52.v3d.triturus.vgis;

/**
 * Class to hold line-segments in 3-D space.
 * @author Benno Schmidt
 */
abstract public class VgLineSegment extends VgGeomObject1d 
{
	/**
     * sets the line segment's start-point.
     * @param pStart Point object
     */
	abstract public void setStartPoint(VgPoint pStart) ;

    /**
     * returns the line segment's start-point.
     * @return Point object
     */
	abstract public VgPoint getStartPoint();

    /**
     * sets the line segment's end-point.
     * @param pEnd Point object
     */
	abstract public void setEndPoint(VgPoint pEnd);

    /**
     * returns the line segment's end-point.
     * @return Point object
     */
	abstract public VgPoint getEndPoint();

	/**
     * returns the line segment's length referring to the assigned coordinate reference system with respect to
     * the x-y plane (&quot;footprint length&quot;).
     * @return Distance value
	 * @see VgGeomObject#getSRS
	 */
    public double length()
	{
		return this.getEndPoint().distance(this.getStartPoint());
	}

	public String toString() {
		return "[" + this.getStartPoint().toString() + ", " +
			this.getEndPoint().toString() + "]";
	}
}