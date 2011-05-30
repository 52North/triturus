package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.util.ArrayList;

/** 
 * Filter zur Triangulation einer Menge von 3D-Punkten<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class FltPointSet2TIN extends T3dProcFilter
{
    private String mLogString = "";

    /** Konstruktor. */
    public FltPointSet2TIN() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /** 
     * <b>TODO</b><p> 
     * <i>... Verfahren: Delaunay, ...</i><p>
     * <i>... Verfahrensparameter: ...</i><p>
     * @param pPointSet Liste von <tt>VgPoint</tt>-Objekten
     */
    public GmSimpleTINFeature transform(ArrayList pPointSet) throws T3dException
    {
    	throw new T3dNotYetImplException();
   }
}
