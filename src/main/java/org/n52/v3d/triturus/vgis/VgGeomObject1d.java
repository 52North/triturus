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
 * Abstract base class for 1-dimensional geometric objects. This class extends <tt>VgGeomObject</tt> by definitions
 * to access <i>metric</i> geometric properties
 * @author Benno Schmidt
 */
abstract public class VgGeomObject1d extends VgGeomObject 
{
	/**
	 * returns the geometric object's length referring to the assigned coordinate reference system.
	 * @see VgGeomObject#getSRS
     * @return Distance value
	 */
	abstract public double length();

	/**
     * returns the geometric object's length referring to the assigned coordinate reference system with respect to
     * the x-y plane (&quot;footprint length&quot;).
     * @return Distance velue
	 * @see VgGeomObject#getSRS
	 */
	public double lengthXY() {
		return ((VgGeomObject1d) this.footprint()).length();
	}
}
