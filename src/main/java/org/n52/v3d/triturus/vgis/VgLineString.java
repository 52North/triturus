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

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold line-strings (polylines) in 3-D space.
 *
 * @author Benno Schmidt
 */
abstract public class VgLineString extends VgGeomObject1d 
{
   /**
    * returns the number of points that are part of the polyline (<i>vertices</i>).
    */
	abstract public int numberOfVertices();

	/**
	 * returns the i-th point (vertex) of the polyline structure.<br /><br />
     * <i>German:</i> Es ist stets die Bedingung 0 &lt;<= i &lt; <tt>this.numberOfVertices()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.
     *
     * @param i Point index
     * @return Vertex object
	 */
	abstract public VgPoint getVertex(int i) throws T3dException;
	
	/**
	 * returns the polyline's length referring to the assigned coordinate reference system.
     *
	 * @see VgGeomObject#getSRS
     * @return Distance value
	 */
    public double length()
	{
		double sum = 0.;
		for (int i = 0; i < this.numberOfVertices() - 1; i++)
			sum += this.getVertex(i + 1).distance( this.getVertex(i) );
		return sum;
	}

	public String toString() 
	{
		String str = "[";
		if (this.numberOfVertices() > 0) {
			for (int i = 0; i < this.numberOfVertices() - 1; i++) {
				str = str + this.getVertex(i).toString() + ", ";
			}
			str = str + this.getVertex(this.numberOfVertices() - 1).toString();
		}
		return str + "]";
	}
}
