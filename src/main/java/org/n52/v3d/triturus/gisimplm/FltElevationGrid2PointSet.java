/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/**
 * Filter to transform <tt>GmSimpleElevationGrid</tt>-objects into a list of <tt>VgPoint</tt>-objects.
 * @author Benno Schmidt
 */
public class FltElevationGrid2PointSet extends T3dProcFilter
{
    private String mLogString = "";
    private VgEnvelope mEnv = null;

    public FltElevationGrid2PointSet() {
        mLogString = this.getClass().getName();
    }

    public String log() {
        return mLogString;
    }

    /**
     * sets an envelope as spatial filter.<br /><br />
     * <i>German:</i> setzt einen Envelope als r�umlichen Filter. Falls der Filter ungleich <i>null</i> ist, werden
     * innerhalb der <tt>transform</tt>-Operation nur diejenigen Punkte ber�cksichtigt, die innerhalb der gegebenen
     * Bounding-Box liegen. Hierbei werden nur die x- und y-Koordinaten des Envelopes ber&uuml;cksichtigt!<br />
     * Bem.: Das gesetzte Raumbezugssystem wird in der aktuellen Implementierung nicht &uuml;berpr&uuml;ft!
     * @param pEnv Envelope mit x- und y-Begrenzung
     */
    public void setSpatialFilter(VgEnvelope pEnv) {
       mEnv = pEnv;
    }

    /**
     * performs the described filter operation.
     */
    public ArrayList transform(GmSimpleElevationGrid pGrid) throws T3dException
    {
        if (pGrid == null)
            return null;
        ArrayList res = new ArrayList();
        for (int i = 0; i < pGrid.numberOfRows(); i++) {
            for (int j = 0; j < pGrid.numberOfColumns(); j++) {
                try {
                    VgPoint pt = pGrid.getPoint(i, j);
                    if (pt == null)
                        continue;
                    if (mEnv != null) {
                        if (pt.getX() < mEnv.getXMin())
                            continue;
                        if (pt.getX() > mEnv.getXMax())
                            continue;
                        if (pt.getY() < mEnv.getYMin())
                            continue;
                        if (pt.getY() > mEnv.getYMax())
                            continue;
                    }
                    res.add(pt);
                }
                catch (T3dException e) {
                    //System.out.println("Ignoring unset grid cell (" + i + ", " + j + ")...");   
                }
            }
        }
        return res;
    }
}
