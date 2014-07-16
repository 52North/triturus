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
 * Abstract base class for 0-dimensional geometric objects. This class extends <tt>VgGeomObject</tt> by definitions
 * to access <i>metric</i> geometric properties.
 *
 * @author Benno Schmidt
 */
abstract public class VgGeomObject0d extends VgGeomObject 
{
	/**
     * returns the distance between two point-like geometric objects.<br /><br />
	 * <i>German:</i> liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt>.<br />
	 * Der Abstand wird bezogen auf das der Geometrie zugrunde liegenden r&auml;umlichen Referenzsystem
     * (<tt>this.getSRS()</tt>) und die den z-Koordinaten zugrunde liegende Einheit berechnet.<br />
	 * Beispiel: F&uml;r siebenstellige Gau&szlig;-Kr&uuml;ger-Koordinaten in m und H&ouml;henangaben in m &uuml;. NN.
     * ergibt sich das Abstandsma&szlig; 1 m. Seitens der aufrufenden Anwendung ist sicherzustellen, dass in der
     * xy-Ebene und in z-Richtung vern&uuml;nftige Ma&szlig;e verwendet werden.<br />
	 * Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt> &uuml;bereinstimmt,
     * wird eine <tt>T3dException</tt> geworfen.
     *
     * @param pt
     * @return Distance value
	 * @see VgGeomObject#getSRS
	 */
	abstract public double distance(VgGeomObject0d pt) throws T3dException;
	
	/**
     * returns the distance between two point-like geometric objects with respect to the x-y plane (i.e., z-values will
     * be set to 0.<br /><br />
	 * <i>German:</i> liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt> in der xy-Ebene
     * (d. h., die z-Werte werden = 0 gesetzt).<br />
	 * Der Abstand wird in dem der Geometrie zugrunde liegenden r&auml;umlichen Referenzsystem (<tt>this.getSRS()</tt>)
     * berechnet. Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt>
     * &uml;bereinstimmt, wird eine <tt>T3dException</tt> geworfen.
     *
     * @param pt
     * @return Distance value
	 * @see VgGeomObject#getSRS
	 * @see VgGeomObject0d#distance
	 */
	abstract public double distanceXY(VgGeomObject0d pt) throws T3dException;
}
