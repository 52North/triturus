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
 * Calculation of the difference of two equidistant elevation-grids.
 *
 * @author Benno Schmidt
 */
public class FltElevationGridDifference extends T3dProcFilter
{
    private String mLogString = "";

    public FltElevationGridDifference() {
        mLogString = this.getClass().getName();
    }
                                                                                     
    public String log() {
        return mLogString;
    }

    /** 
     * calculates the difference of two elevation-grids.
     *
     * @param pGrid1 Minuend
     * @param pGrid2 Subtrahend
     * @return Difference grid
     * @throws T3dException
     */
    public GmSimpleElevationGrid transform(GmSimpleElevationGrid pGrid1, GmSimpleElevationGrid pGrid2) throws T3dException
    {
        boolean sameGeometry = this.checkGeometry(pGrid1, pGrid2);
        if (!sameGeometry) throw new T3dException("Elevation grids do no refer to the same area!");

        GmSimpleElevationGrid lGridRes = new GmSimpleElevationGrid(
             pGrid1.numberOfColumns(), pGrid1.numberOfRows(),
             ((GmSimple2dGridGeometry) pGrid1.getGeometry()).getOrigin(),
             ((GmSimple2dGridGeometry) pGrid1.getGeometry()).getDeltaX(),
             ((GmSimple2dGridGeometry) pGrid1.getGeometry()).getDeltaY());

        for (int i = 0; i < lGridRes.numberOfRows(); i++) {
            for (int j = 0; j < lGridRes.numberOfColumns(); j++) {
                if (pGrid1.isSet(i, j) && pGrid2.isSet(i, j)) {
                    lGridRes.setValue(i, j, pGrid2.getValue(i, j) - pGrid1.getValue(i, j));
                } else {
                    lGridRes.unset(i, j);
                }
            }
        }

        return lGridRes;
    }
    
    private boolean checkGeometry(GmSimpleElevationGrid pGrid1, GmSimpleElevationGrid pGrid2) throws T3dException
    {
        int
                nx_1 = ((GmSimple2dGridGeometry) pGrid1.getGeometry()).numberOfColumns(),
                ny_1 = ((GmSimple2dGridGeometry) pGrid1.getGeometry()).numberOfRows(),
                nx_2 = ((GmSimple2dGridGeometry) pGrid2.getGeometry()).numberOfColumns(),
                ny_2 = ((GmSimple2dGridGeometry) pGrid2.getGeometry()).numberOfRows();
        double
                xll_1 = pGrid1.getGeometry().envelope().getXMin(),
                yll_1 = pGrid1.getGeometry().envelope().getYMin(),
                xur_1 = pGrid1.getGeometry().envelope().getXMax(),
                yur_1 = pGrid1.getGeometry().envelope().getYMax(),
                xll_2 = pGrid2.getGeometry().envelope().getXMin(),
                yll_2 = pGrid2.getGeometry().envelope().getYMin(),
                xur_2 = pGrid2.getGeometry().envelope().getXMax(),
                yur_2 = pGrid2.getGeometry().envelope().getYMax();

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
