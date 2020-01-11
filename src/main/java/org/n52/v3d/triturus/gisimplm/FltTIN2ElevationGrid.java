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
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import java.util.ArrayList;
import java.util.List;

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
 * <br/><b>TODO: This implementation is still experimental!</b>
 * 
 * @author Benno Schmidt
 */
public class FltTIN2ElevationGrid extends T3dProcFilter
{
    private String logString = "";
    private VgEquidistGrid gridGeom;
    
    private List<VgLineSegment> conflicts = null;
    
    /**
     * @deprecated
     * Constructor.
     * <br/><b>TODO: This implementation is still experimental!</b>
     */
    public FltTIN2ElevationGrid() {
        logString = this.getClass().getName();
    }

    public String log() {
        return logString;
    }

    /**
     * sets the grid geometry as spatial "filter".
     * 
     * @param grdGeom Grid geometry
     */
    public void setGridGeometry(VgEquidistGrid gridGeom) {
    	this.gridGeom = gridGeom;
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
    	
    	VgEnvelope envGeom = gridGeom.envelope(); 
        VgIndexedTIN geom = (VgIndexedTIN) tin.getGeometry();

        double 
    		xMin = envGeom.getXMin(),
	   		xMax = envGeom.getXMax(),
    		yMin = envGeom.getYMin(),
       		yMax = envGeom.getYMax();
    	long 
    		nx = gridGeom.numberOfColumns(),
    		ny = gridGeom.numberOfRows();    			
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
	            					VgPoint 
	            						conflictP1 = target.getPoint(ii, jj),
	            						conflictP2 = new GmPoint(p.getX(), p.getY(), z);
	            					conflictP2.setSRS(conflictP1.getSRS());
	            					VgLineSegment line = 
	            						new GmLineSegment(conflictP1, conflictP2);
System.out.println(ii + " " + jj + " §" + line.length() + " (" + line.lengthXY() + ")" + " -> " 
	    + i + " vs. ");        						
		
	            					line.setSRS(conflictP1.getSRS());
	            					conflicts.add(line);
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
		if (gridGeom == null) 
    		throw new T3dException("No grid geometry is given!");
    	if (!(gridGeom instanceof GmSimple2dGridGeometry)) 
    		throw new T3dException("Unexpected grid geometry class type!");  
    	    	
    	GmSimpleElevationGrid target = 
    		new GmSimpleElevationGrid((GmSimple2dGridGeometry) gridGeom);
    	((GmSimpleElevationGrid) target).setLatticeInterpretation(); // TODO
    	
    	for (int ii = 0; ii < target.numberOfRows(); ii++) {
        	for (int jj = 0; jj < target.numberOfColumns(); jj++) {
        		target.unset(ii, jj);
        		// This is redundant, since the GmSimpleElevationGrid 
        		// constructor should initialize all grid cells as unset.
        	}
    	}
        	
		return target;
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
