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
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.util.ArrayList;

/** 
 * Filter to perform triangulations of sets of 3-D points.
 * @author Benno Schmidt
 */
public class FltPointSet2TIN extends T3dProcFilter
{
    private String mLogString = "";

    /**
     * Constructor.
     */
    public FltPointSet2TIN() {
        mLogString = this.getClass().getName();
    }

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
