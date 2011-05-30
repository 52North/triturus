package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/**
 * Filter zur Transformation eines <tt>GmSimpleElevationGrid</tt>-Objekts in eine Liste von <tt>VgPoint</tt>-Objekten.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2003-2006, con terra GmbH & Institute for Geoinformatics<br>
 */
public class FltElevationGrid2PointSet extends T3dProcFilter
{
    private String mLogString = "";
    private VgEnvelope mEnv = null;

    /** Konstruktor. */
    public FltElevationGrid2PointSet() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /**
     * setzt einen Envelope als räumlichen Filter. Falls der Filter ungleich <i>null</i> ist, werden innerhalb der
     * <tt>transform</tt>-Operation nur diejenigen Punkte berücksichtigt, die innerhalb der gegebenen Bounding-Box
     * liegen. Hierbei werden nur die x- und y-Koordinaten des Envelopes berücksichtigt!<p>
     * Bem.: Das gesetzte Raumbezugssystem wird in der aktuellen Implementierung nicht überprüft!<p>
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
