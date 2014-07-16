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
 * Abstract base class for 3-dimensional geometric objects. This class extends <tt>VgGeomObject</tt> by definitions
 * to access <i>metric</i> geometric properties.
 *
 * @author Benno Schmidt
 */
abstract public class VgGeomObject3d extends VgGeomObject 
{
	/**
     * returns the geometric object's volume referring to the assigned coordinate reference system and to the measure
     * used for z-coordinates.<br /><br />
	 * <i>German:</i> Bem.: Ggf. ist sicherzustellen, dass dem Fl&auml;chenma&szlig; eine vern&uuml;nftige Einheit
     * zugrunde liegt.
     *
     * @return Volume value
	 * @see VgGeomObject#getSRS
	 */
	abstract public double volume();

	/**
     * returns the geometric object's surface area referring to the assigned coordinate reference system and to the
     * measure used for z-coordinates.<br /><br />
	 * <i>German:</i> Bem.: Ggf. ist sicherzustellen, dass dem Fl&auml;chenma&szlig; eine vern&uuml;nftige Einheit
     * zugrunde liegt.
     *
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	abstract public double surface();

	/**
     * returns the geometric object's area referring to the assigned coordinate reference system with respect to
     * the x-y plane (&quot;footprint area&quot;).
     *
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	public double areaXY() {
		return ((VgGeomObject2d) this.footprint()).area();
	}
}
