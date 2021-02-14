/**
 * Copyright (C) 2007-2016 52North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

/**
 * Class to manage elevation grids. The grid's elevation values might refer to 
 * the grid geometry's vertices (so-called &quot;lattices&quot;, <i>vertex-based 
 * grid</i>) or to the grid-cells (<i>cell-based grid</i>, often designed as 
 * &quot;grids&quot; in contrast to &quot;lattices&quot;). Since for all grid 
 * cells a &quot;no-data&quot;-flag can be set, not for all grid elements 
 * elevation values must be given.
 *
 * @author Benno Schmidt
 */
abstract public class VgElevationGrid extends VgFeature 
{
    /**  
     * returns the number of grid elements in <i>x</i>-direction (columns).
     *
     * @return Number of the grid's columns
     */
    abstract public int numberOfColumns();

    /** 
     * returns the number of grid elements in <i>y</i>-direction (rows).
     *
     * @return Number of the grid's rows
     */
    abstract public int numberOfRows();
    
    /**
     * sets the elevation value <tt>pZ</tt> for the row index <tt>pRow</tt> 
     * and the column index <tt>pCol</tt>.
     *
     * @param pRow Row index
     * @param pCol Column index
     * @param pZ Elevation value
     */
    abstract public void setValue(int pRow, int pCol, double pZ);

    /** 
     * returns the elevation value for the row index <tt>pRow</tt> and the 
     * column index <tt>pCol</tt>.
     *
     * @return Elevation value
     */
    abstract public double getValue(int pRow, int pCol);

    /** 
     * returns the elevation grid's minimum value.
     *
     * @return Minimal elevation
     */
    abstract public double minimalElevation();

    /** 
     * returns the elevation grid's maximum value.
     *
     * @return Maximal elevation
     */
    abstract public double maximalElevation();

    /**
     * returns the difference of the maximum and minimum elevation value.
     *
     * @return Elevation range
     */
    public double elevationDifference() {
        return this.maximalElevation() - this.minimalElevation();
    }
}
