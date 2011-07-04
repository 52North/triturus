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

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstract base class for 0-dimensional geometric objects. This class extends <tt>VgGeomObject</tt> by definitions
 * to access <i>metric</i> geometric properties
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
     * @param pt
     * @return Distance value
	 * @see VgGeomObject#getSRS
	 * @see VgGeomObject0d#distance
	 */
	abstract public double distanceXY(VgGeomObject0d pt) throws T3dException;
}
