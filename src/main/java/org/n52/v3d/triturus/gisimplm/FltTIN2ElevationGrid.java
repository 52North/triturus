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
package org.n52.v3d.triturus.gisimplm;

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgEquidistGrid;
import org.n52.v3d.triturus.vgis.VgIndexedTIN;
import org.n52.v3d.triturus.vgis.VgLineSegment;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTriangle;

/** 
 * Filter to transform a <tt>GmSimpleTINFeature</tt>-object to a 
 * <tt>GmSimpleElevationGrid</tt>. Basically, this implementation provides a 
 * "rasterizer" to transform a given TIN (e.g. a terrain surface) to a lattice 
 * of elevation values.
 * 
 * @author Benno Schmidt
 */
public class FltTIN2ElevationGrid extends T3dProcFilter
{
    private String logString = "";
    private VgEquidistGrid grdGeom;
    
    private List<VgLineSegment> conflicts = null;
    
    /**
     * Identifier for z-conflict handler which take the highest z-value if the 
     * source TIN gives more than one z-value (default value).
     */
    public static final int CONFLICT_TAKE_MAX_Z = 1;
    /**
     * Identifier for z-conflict handler which take the lowest z-value if the 
     * source TIN gives more than one z-value.
     */
    public static final int CONFLICT_TAKE_MIN_Z = 2;
    /**
     * Identifier for z-conflict handler which take the average z-value if the 
     * source TIN gives more than one z-value. (Note that this method has not 
     * been implemented yet.)
     */
    public static final int CONFLICT_TAKE_AVG_Z = 3;
    
    private int zConflictHandler = CONFLICT_TAKE_MAX_Z;
    
    /**
     * Constructor.
     */
    public FltTIN2ElevationGrid() {
        logString = this.getClass().getName();
    }

    public String log() {
        return logString;
    }

    /**
     * sets the grid geometry as spatial "filter" (geometric raster target).
     * 
     * @param grdGeom Grid geometry
     */
    public void setGridGeometry(VgEquidistGrid grdGeom) {
    	this.grdGeom = grdGeom;
    }

    /**
     * 
     * @param method
     */
    public void setZConflictHandler(int method) {
        this.zConflictHandler = method;
    }

    /**
     * performs the described filter operation.
     * 
     * @param tin Input TIN
     * @return Resulting elevation grid 
     */
    public VgElevationGrid transform(GmSimpleTINFeature tin) throws T3dException
    {
    	GmSimpleElevationGrid target = prepareTargetGrid();
    	
    	VgEnvelope envGeom = grdGeom.envelope(); 
        VgIndexedTIN geom = (VgIndexedTIN) tin.getGeometry();

        double 
    		xMin = envGeom.getXMin(),
	   		xMax = envGeom.getXMax(),
    		yMin = envGeom.getYMin(),
       		yMax = envGeom.getYMax();
    	long 
    		nx = grdGeom.numberOfColumns(),
    		ny = grdGeom.numberOfRows();    			
    	double
    		fx = (((double) nx) - 1.) / (xMax - xMin),
    		fy = (((double) ny) - 1.) / (yMax - yMin);
    	int iFrom, iTo, jFrom, jTo, ii, jj;
    	double z;
    	VgPoint p = new GmPoint();
    	conflicts = new ArrayList<VgLineSegment>();

        for (int i = 0; i < geom.numberOfTriangles(); i++) {
        	VgTriangle tri = geom.getTriangle(i);
        	VgEnvelope envTri = tri.envelope();
   
        	jFrom = (int)(fx * (envTri.getXMin() - xMin));
        	jTo = (int)(fx * (envTri.getXMax() - xMin)) + 1;
        	iFrom = (int)(fy * (envTri.getYMin() - yMin));
        	iTo = (int)(fy * (envTri.getYMax() - yMin)) + 1;
        	
        	for (jj = jFrom; jj <= jTo; jj++) {
            	for (ii = iFrom; ii <= iTo; ii++) {
            		if (ii >= 0 && ii < ny && jj >= 0 && jj < nx) {
            			p.setX(xMin + (xMax - xMin) * (((double) jj) / (double)(nx - 1)));
            			p.setY(yMin + (yMax - yMin) * (((double) ii) / (double)(ny - 1)));
            			
            			try {
	            			if (tri.isInsideXY(p, true)) {
	            				z = tri.interpolateZ(p);
	            				if (target.isSet(ii, jj)) {
	            					// non "2.5-D" case detected
	            					z = handleConflict(target, ii, jj, z, p);
	            				}
	            				target.setValue(ii, jj, z);
	            			}
            			} catch (Exception e) {
            				e.printStackTrace();
            			}
            		}
            	}
        	}
        }
    	return target;
    }

	private GmSimpleElevationGrid prepareTargetGrid() 
	{
		if (grdGeom == null) 
    		throw new T3dException("No grid geometry is given!");
    	if (!(grdGeom instanceof GmSimple2dGridGeometry)) 
    		throw new T3dException("Unexpected grid geometry class type!");  
    	    	
    	GmSimpleElevationGrid target = 
    		new GmSimpleElevationGrid((GmSimple2dGridGeometry) grdGeom);
    	((GmSimpleElevationGrid) target).setLatticeInterpretation();
    	
    	for (int ii = 0; ii < target.numberOfRows(); ii++) {
        	for (int jj = 0; jj < target.numberOfColumns(); jj++) {
        		target.unset(ii, jj);
        		// This is redundant, since the GmSimpleElevationGrid 
        		// constructor should initialize all grid cells as unset.
        	}
    	}
        	
		return target;
	}

	private double handleConflict(
		GmSimpleElevationGrid target, int ii, int jj, double z, VgPoint p) 
	{
		VgPoint 
			p1 = target.getPoint(ii, jj),
			p2 = new GmPoint(p.getX(), p.getY(), z);
		p2.setSRS(p1.getSRS());
				
		VgLineSegment line = new GmLineSegment(p1, p2);
		line.setSRS(p1.getSRS());
		conflicts.add(line);
		
		double z1 = p1.getZ(), z2 = z, res = z2;
		switch (zConflictHandler) {
			case CONFLICT_TAKE_MIN_Z:
				res = Math.min(z1, z2);
				break;
			case CONFLICT_TAKE_MAX_Z:
				res = Math.max(z1, z2);
				break;
			case CONFLICT_TAKE_AVG_Z:
				// Note for implementers: The implementation would require to 
				// store the number of candidate elevations, which might be 
				// > 2!
				throw new T3dNotYetImplException();
		}
		return res;
	}

    /**
     * returns the 2-D locations of detected conflicts. The z-values give the 
     * conflicting z-values at these locations.
     * 
     * @return List of vertically oriented line segments
     */
    public List<VgLineSegment> conflicts() {
    	return conflicts;
    }
}
