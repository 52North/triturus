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

    /** <b>TODO</b> */
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
