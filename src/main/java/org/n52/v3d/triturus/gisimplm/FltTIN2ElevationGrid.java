/**
 * Copyright (C) 2007-2019 52 North Initiative for Geospatial Open Source 
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

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgEquidistGrid;
import org.n52.v3d.triturus.vgis.VgIndexedTIN;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTriangle;

/** 
 * Filter to transform a <tt>GmSimpleTINFeature</tt>-object to a 
 * <tt>GmSimpleElevationGrid</tt>. Basically, this implementation provides a 
 * "rasterizer" to transform a given TIN (e.g. a terrain surface) to a lattice 
 * of elevation values.
 * <br/><b>TODO: This implenentation is still experimental!</b>
 * 
 * @author Benno Schmidt
 */
public class FltTIN2ElevationGrid extends T3dProcFilter
{
    private String logString = "";
    private VgEquidistGrid gridGeom;
    
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
    	if (gridGeom == null) 
    		throw new T3dException("No grid geometry is given!");
    	if (!(gridGeom instanceof GmSimple2dGridGeometry)) 
    		throw new T3dException("Unexpected grid geometry class type!");    	
    	VgElevationGrid target = new GmSimpleElevationGrid((GmSimple2dGridGeometry) gridGeom);
    	((GmSimpleElevationGrid) target).setLatticeInterpretation(); // todo
System.out.println(((GmSimple2dGridGeometry) gridGeom).envelope());
// todo: folgenden code in eigene methode der ElevationGrid-Klasse oder besser mit Unset-Value arbeiten in Writer:
for (int jj = 0; jj < gridGeom.numberOfColumns(); jj++) {
	for (int ii = 0; ii < gridGeom.numberOfRows(); ii++) {
			target.setValue((int) ii, (int) jj, 0.0); // TODO int?
	}
}
System.out.println(">"+((GmSimpleElevationGrid) target).envelope());
    	
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
    		fx = (((double) nx) - 1.0) / (xMax - xMin),
    		fy = (((double) ny) - 1.0) / (yMax - yMin);
    	long iFrom, iTo, jFrom, jTo, ii, jj;
    	double z;
    	VgPoint p = new GmPoint();

    	
        for (int i = 0; i < geom.numberOfTriangles(); i++) {
        	VgTriangle tri = geom.getTriangle(i);
//System.out.println(tri); // TODO
        	VgEnvelope envTri = tri.envelope();
   
        	jFrom = (long)(fx * (envTri.getXMin() - xMin));
        	jTo = (long)(fx * (envTri.getXMax() - xMin)) + 1;
        	iFrom = (long)(fy * (envTri.getYMin() - yMin));
        	iTo = (long)(fy * (envTri.getYMax() - yMin)) + 1;
        	
        	for (jj = jFrom; jj <= jTo; jj++) {
            	for (ii = iFrom; ii <= iTo; ii++) {
            		if (ii >= 0 && ii < ny && jj >= 0 && jj < nx) {
            			p.setX(xMin + (xMax - xMin) * (((double) jj) / (nx - 1)));
            			p.setY(yMin + (yMax - yMin) * (((double) ii) / (ny - 1)));
            			
            			try {
            			if (tri.isInsideXY(p, true)) {
            				z = tri.interpolateZ(p);
            				target.setValue((int) ii, (int) jj, z); // TODO int?
            			}
            			} catch (Exception e) {
            				// Todo
            			}
            		}
            	}
        	}
        }
    	return target;
    }
}
