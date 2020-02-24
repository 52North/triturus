/**
 * Copyright (C) 2016 52 North Initiative for Geospatial Open Source
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
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster,
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dProcFilter;

/**
 * Computation of the difference of two elevation-grids. Both input grids must 
 * refer to the same geometry, otherwise the computation will not be done.
 *
 * @author Benno Schmidt
 */
public class FltElevationGridDifference extends T3dProcFilter
{
    private String logString = "";

    public FltElevationGridDifference() {
        logString = this.getClass().getName();
    }
                                                                                     
    public String log() {
        return logString;
    }

    /** 
     * calculates the difference of two elevation-grids.
     *
     * @param grid1 Minuend
     * @param grid2 Subtrahend
     * @return Difference grid
     * @throws T3dException
     */
    public GmSimpleElevationGrid transform(
    	GmSimpleElevationGrid grid1, GmSimpleElevationGrid grid2) 
    	throws T3dException
    {
        boolean sameGeometry = this.checkGeometry(grid1, grid2);
        if (!sameGeometry) 
        	throw new T3dException("Elevation grids differ in geometry!");

        GmSimpleElevationGrid gridRes = new GmSimpleElevationGrid(
             grid1.numberOfColumns(), grid1.numberOfRows(),
             ((GmSimple2dGridGeometry) grid1.getGeometry()).getOrigin(),
             ((GmSimple2dGridGeometry) grid1.getGeometry()).getDeltaX(),
             ((GmSimple2dGridGeometry) grid1.getGeometry()).getDeltaY());

        for (int i = 0; i < gridRes.numberOfRows(); i++) {
            for (int j = 0; j < gridRes.numberOfColumns(); j++) {
                if (grid1.isSet(i, j) && grid2.isSet(i, j)) {
                    gridRes.setValue(i, j, grid2.getValue(i, j) - grid1.getValue(i, j));
                } else {
                    gridRes.unset(i, j);
                }
            }
        }

        return gridRes;
    }
    
    private boolean checkGeometry(
    	GmSimpleElevationGrid grid1, GmSimpleElevationGrid grid2) 
    	throws T3dException
    {
        int
                nx_1 = ((GmSimple2dGridGeometry) grid1.getGeometry()).numberOfColumns(),
                ny_1 = ((GmSimple2dGridGeometry) grid1.getGeometry()).numberOfRows(),
                nx_2 = ((GmSimple2dGridGeometry) grid2.getGeometry()).numberOfColumns(),
                ny_2 = ((GmSimple2dGridGeometry) grid2.getGeometry()).numberOfRows();
        double
                xll_1 = grid1.getGeometry().envelope().getXMin(),
                yll_1 = grid1.getGeometry().envelope().getYMin(),
                xur_1 = grid1.getGeometry().envelope().getXMax(),
                yur_1 = grid1.getGeometry().envelope().getYMax(),
                xll_2 = grid2.getGeometry().envelope().getXMin(),
                yll_2 = grid2.getGeometry().envelope().getYMin(),
                xur_2 = grid2.getGeometry().envelope().getXMax(),
                yur_2 = grid2.getGeometry().envelope().getYMax();

        if (nx_1 != nx_2) return false;
        if (ny_1 != ny_2) return false;

        double eps = 0.001;
        if (Math.abs((xll_1 - xll_2) / xll_1) > eps) return false;
        if (Math.abs((yll_1 - yll_2) / yll_1) > eps) return false;
        if (Math.abs((xur_1 - xur_2) / xur_1) > eps) return false;
        if (Math.abs((yur_1 - yur_2) / yur_1) > eps) return false;

        return true;
    }
}
