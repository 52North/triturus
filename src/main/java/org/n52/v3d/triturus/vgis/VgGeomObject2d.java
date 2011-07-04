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
 * Abstract base class for 2-dimensional geometric objects. This class extends <tt>VgGeomObject</tt> by definitions
 * to access <i>metric</i> geometric properties
 * @author Benno Schmidt
 */
abstract public class VgGeomObject2d extends VgGeomObject 
{
	/**
     * returns the geometric object's area referring to the assigned coordinate reference system.
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	abstract public double area();

	/**
     * returns the geometric object's area referring to the assigned coordinate reference system with respect to
     * the x-y plane (&quot;footprint area&quot;).
     * @return Area value
	 * @see VgGeomObject#getSRS
	 */
	public double areaXY() {
		return ((VgGeomObject2d) this.footprint()).area();
	}
}
