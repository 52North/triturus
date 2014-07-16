/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * If the program is linked with libraries which are licensed under one of the         *
 * following licenses, the combination of the program with the linked library is not   *
 * considered a "derivative work" of the program:                                      *
 *                                                                                     *
 *   - Apache License, version 2.0                                                     *
 *   - Apache Software License, version 1.0                                            *
 *   - GNU Lesser General Public License, version 3                                    *
 *   - Mozilla Public License, versions 1.0, 1.1 and 2.0                               *
 *   - Common Development and Distribution License (CDDL), version 1.0                 *
 *                                                                                     *
 * Therefore the distribution of the program linked with libraries licensed under      *
 * the aforementioned licenses, is permitted by the copyright holders if the           *
 * distribution is compliant with both the GNU General Public License version 2 and    *
 * the aforementioned licenses.                                                        *
 *                                                                                     *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY     *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.            *
 *                                                                                     *
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
     *
     * @param pStart Point object
     */
	abstract public void setStartPoint(VgPoint pStart) ;

    /**
     * returns the line segment's start-point.
     *
     * @return Point object
     */
	abstract public VgPoint getStartPoint();

    /**
     * sets the line segment's end-point.
     *
     * @param pEnd Point object
     */
	abstract public void setEndPoint(VgPoint pEnd);

    /**
     * returns the line segment's end-point.
     *
     * @return Point object
     */
	abstract public VgPoint getEndPoint();

	/**
     * returns the line segment's length referring to the assigned coordinate reference system with respect to
     * the x-y plane (&quot;footprint length&quot;).
     *
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