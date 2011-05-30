package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * Filter zur Transformation eines <tt>GmSimpleTINFeature</tt>-Objekts in ein <tt>GmSimpleElevationGrid</tt>.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class FltTIN2ElevationGrid extends T3dProcFilter
{
    private String mLogString = "";

    /** Konstruktor. */
    public FltTIN2ElevationGrid() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /** <b>TODO</b> */
    public GmSimpleElevationGrid transform(GmSimpleTINFeature pTIN) throws T3dException
    {
    	throw new T3dNotYetImplException();
    }
}
