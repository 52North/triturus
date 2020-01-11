/**
 * Copyright (C) 2007-2020 52North Initiative for Geospatial Open Source
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
 * license version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.t3dutil.DelaunayTutsNichtMehr;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTIN;
import org.n52.v3d.triturus.xtin.XTIN;

import java.util.List;

/** 
 * Filter to perform triangulations of sets of 3-D points.
 * 
 * @author Benno Schmidt
 */
public class FltPointSet2TIN extends T3dProcFilter
{
    private String mLogString = "";

    /**
     * Identifier for simple incremental Delaunay method. Note that this 
     * implementation is inefficient, since it requires O(n^2) runtime!
     */
    public final static short cSimpleDelaunay = 1;

    private short mAlgorithm = 1; // simple Delaunay as default

    /**
     * Constructor.
     */
    public FltPointSet2TIN(short pAlgorithm) {
        mLogString = this.getClass().getName();
        mAlgorithm = pAlgorithm;        
    }

    public String log() {
        return mLogString;
    }

    /** 
     * triangulates a set of points.
     * 
     * @param points Point set
     */
    public GmSimpleTINFeature transform(List<VgPoint> points) throws T3dException
    {
    	VgTIN tin = null;
    	
    	switch (mAlgorithm) {
    		case cSimpleDelaunay:
    	        int[] res = DelaunayTutsNichtMehr.triangulate(points);
    	        tin = new XTIN();
    	        for (int i = 0; i < points.size(); i++) {
    	        	((XTIN) tin).addLocation(i, points.get(i));
    	        }
    	        for (int i = 0; i < res.length / 3; i++) {
    	        	((XTIN) tin).addTriangle(res[3*i], res[3*i+1], res[3*i+2]);
    	        }
    			break;
    		default:
    	    	throw new T3dNotYetImplException();
    	}
    	
    	if (tin instanceof XTIN)
    		return ((XTIN) tin).asSimpleTINFeature();
    	else {
    		GmSimpleTINFeature f = new GmSimpleTINFeature();
    		f.setGeometry(tin);
    		return f;
    	}
   }
}
