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

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to manage equidistant grid geometries that be oriented arbitrarily inside the x-y plane.<br /><br />
 * <i>German:</i> Klasse zur Verwaltung &auml;quidistanter, beliebig in der xy-Ebene orientierter Gittergeometrien.
 * <tt>VgEquidistGrid</tt>-Objekte sind keine <tt>VgGeomObject2d</tt>-Objekte, da die zugeh&ouml;rige Interpretation
 * fehlt. Eine interpretierbare, konkrete Realisierung ist z. B. die im Paket &quot;gisimplm&quot; implementierte Klasse
 * <tt>GmSimple2dGridGeometry</tt>.
 *
 * @author Benno Schmidt
 * @see org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry
 */
abstract public class VgEquidistGrid extends VgGeomObject
{
    /**
     * returns the grid's number of rows (first grid axis).
     *
     * @return Number of rows
     */
	abstract public int numberOfRows();

    /**
     * returns the grid's number of columns (second grid axis).
     *
     * @return Number of columns
     */
	abstract public int numberOfColumns();

    /**
     * returns the direction vector of the grid's first axis (row direction).
     *
     */
	abstract public T3dVector getDirectionColumns();

    /**
     * returns the direction vector of the grid's second axis (column direction).
     *
     * @return Direction vector
     */
	abstract public T3dVector getDirectionRows();

	/** 
	 * @deprecated
	 * @see VgEquidistGrid#getCellSizeRows
	 * @see VgEquidistGrid#getCellSizeColumns
	 * <i>German:</i> liefert die Gitterweiten f&uuml;r die Achsen in <tt>pDeltaRows</tt> und <tt>pDeltaColumns</tt>.
     *
     * @param pDeltaRows Cell-width in direction of the first axis (rows)
     * @param pDeltaColumns Cell-width in direction of the second axis (columns)
	 */
	abstract public void getDelta(Double pDeltaRows, Double pDeltaColumns);

	/** 
	 * returns the cell width of the grid's rows.
     *
	 * @return Cell-width with respect to the assigned coordinate reference system
     * @see VgGeomObject2d#setSRS(String)
	 */
	abstract public double getCellSizeRows();

	/** 
     * returns the cell width of the grid's columns.
     *
     * @return Cell-width with respect to the assigned coordinate reference system
     * @see VgGeomObject2d#setSRS(String)
	 */
	abstract public double getCellSizeColumns();

    /**
     * return the grid's origin point.
     */
	abstract public VgPoint getOrigin();

    /**
     * returns the coordinate of the grid-element with the given indices.<br />
     * The assertions <i>0 &lt;= i &lt; this.numberOfRows(), 0 &lt;= j &lt; this.numberOfColumns()</i> must hold,
     * otherwise a <i>T3dException</i> will be thrown.
     *
     * @param i Index of grid row
     * @param j Index of grid column
     * @return Vertex consisting of x- and y-coordinate (with z undefined)
     * @throws T3dException
     */
	abstract public VgPoint getVertexCoordinate(int i, int j) throws T3dException;

	public String toString() {
		return "[" + "(#" + this.numberOfRows() + " rows x #" +
			this.numberOfColumns() + " cols)" + "]";
	}
}