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

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgMultiPolygon;
import org.n52.v3d.triturus.vgis.VgPolygon;

/**
 * Implementation of {@link VgMultiPolygon} that provides the method
 * {@link GmMultiPolygon#addPolygon(VgPolygon)} to add a new {@link VgPolygon}
 * to the collection.<br />
 * Note that the relation of the polygons is not checked, thus overlapping
 * polygons are not identified.
 * 
 * @author Christian Danowski
 * 
 */
public class GmMultiPolygon extends VgMultiPolygon {

	/**
	 * Constructor<br />
	 * <b>Note: The relative position of the polygons to each other is not
	 * checked! Thus overlapping polygons are not detected!</b>
	 * 
	 * @param polygons
	 *            a list of {@link VgPolygon} objects.
	 */
	public GmMultiPolygon(List<VgPolygon> polygons) {
		if (polygons == null)
			this.polygons = new ArrayList<VgPolygon>();

		else if (containsNullElements(polygons))
			throw new T3dException(
					"The array of polygons contains 'NULL elements'! This is not allowed!");

		else
			this.polygons = polygons;
	}

	/**
	 * Constructor that creates an empty list of {@link VgPolygon} with no
	 * geometries.
	 */
	public GmMultiPolygon() {
		this.polygons = new ArrayList<VgPolygon>();
	}

	/**
	 * Adds a new {@link VgPolygon}, which may not be <code>null</code> to the
	 * collection. This implementation does not check, if this polygon overlaps
	 * or equals any existing polygons of the collection!
	 * 
	 * @param polygon
	 *            the polygon
	 */
	public void addPolygon(VgPolygon polygon) {

		this.assertSRS(polygon);

		if (polygon == null)
			throw new T3dException("The polygon is NULL!");

		polygons.add(polygon);

	}

	/**
	 * Checks if the list of polygons contains any <code>null</code> elements.
	 * 
	 * @param polygons
	 *            the list of {@link VgPolygon}
	 * @return <b>true</b>, if any <code>null</code> elements has been detected.
	 */
	private boolean containsNullElements(List<VgPolygon> polygons) {
		for (VgPolygon vgPolygon : polygons) {
			if (vgPolygon == null)
				return true;
		}

		return false;
	}

}
