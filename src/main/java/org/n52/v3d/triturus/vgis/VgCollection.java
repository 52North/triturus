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
 * Interface for geometry collections
 * 
 * @author Christian Danowski
 *
 */
public interface VgCollection {

	/**
	 * Gets the i-th geometry of the collection.<br />
	 * Note: The following condition must always be ensured: <b>0 &lt;= i &lt;
	 * {@link VgCollection#getNumberOfGeometries()}</b>.
	 * 
	 * @param i
	 *            geometry index
	 * @return the i-th geometry of the collection
	 */
	public abstract VgGeomObject getGeometry(int i);

	/**
	 * Gets the number of geometries that are part of the collection.
	 * 
	 * @return the number of geometries
	 */
	public abstract int getNumberOfGeometries();

}
