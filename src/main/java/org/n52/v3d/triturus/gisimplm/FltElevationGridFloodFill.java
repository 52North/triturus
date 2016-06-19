/**
 * Copyright (C) 2007-2016 52 North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;

/** 
 * This &quot;filter&quot;-class allows to compute simple flooding situations 
 * for elevation grids. As input objects, an {@link VgElevationGrid} and a
 * {@link VgPoint} which defines the flooding situation have to be given. 
 * Taking terrain barriers into account, starting from this seed-point, the 
 * elevation-grid cells in the neighborhood will be set to the water-level 
 * z' that is given by the seed-point's z-value. As a result, a new 
 * elevation-grid will be generated with the elevations set to specified
 * water-level z'. For those areas that are not flooded, since they are
 * situated above the level z', or since their is a terrain barrier in between
 * so that the water will not run to there, the resulting grid's elevation
 * values are not set (no-data value).
 *  
 * @author Benno Schmidt
 */
public class FltElevationGridFloodFill extends T3dProcFilter
{
    private String mLogString = "";

    public String log() {
        return mLogString;
    }

    /** 
     * performs the flood fill. The Seed-point gives the water-level z' for 
     * the position (x, y).
     * 
     * @param pElevationGrid Elevation-grid
     * @param pSeedPoint Seed-point 
     * @throws T3dException
     */
    public VgElevationGrid transform(VgElevationGrid pElevationGrid, VgPoint pSeedPoint) 
    	throws T3dException
    {   	
        if (pElevationGrid == null) {
            throw new T3dException("Source grid is missing.");
        }
        
        VgGeomObject lGeom = pElevationGrid.getGeometry();
        if (!(lGeom instanceof GmSimple2dGridGeometry)) {
            throw new T3dException("Unexpected grid geometry.");        	
        }
        
        GmSimpleElevationGrid lResultGrid = 
        	this.setUpResultGrid((GmSimple2dGridGeometry) lGeom);
        
        try {
        	int[] indices = 
        		((GmSimple2dGridGeometry) lGeom).getIndices(pSeedPoint);
        	if (indices == null) {
                throw new T3dException("Seed point outside elevation grid.");
        	}
        	int row = indices[0], col = indices[1];
        	
        	// Start recursion:
        	this.fillElement(
        			lResultGrid, pElevationGrid, 
        			row, col, 
        			pSeedPoint.getZ());
        }
        catch (T3dException e) {
            throw e;
        }

        return lResultGrid;
    }    

    private GmSimpleElevationGrid setUpResultGrid(GmSimple2dGridGeometry pGeom)
    {
    	GmSimpleElevationGrid lResultGrid = new GmSimpleElevationGrid(pGeom);

    	// Usually, the following lines should not be necessary:
    	for (int i = 0; i < lResultGrid.numberOfRows(); i++) {
    		for (int j = 0; j < lResultGrid.numberOfColumns(); j++) {
    			lResultGrid.unset(i, j);
    		}
    	}
    	
    	return lResultGrid;
    }

	private void fillElement(
			GmSimpleElevationGrid targetGrd, 
			VgElevationGrid srcGrd,
			int i, 
			int j,
			double zFlood) 
	{
		if (
			i < 0 || i >= srcGrd.numberOfRows() || 
			j < 0 || j >= srcGrd.numberOfColumns()) 
		{
			return;
		}
		if (targetGrd.isSet(i, j)) {
			return;
		}
		// else:
		double z = srcGrd.getValue(i, j);
		if (z < zFlood) {
			targetGrd.setValue(i, j, zFlood);
			fillElement(targetGrd, srcGrd, i - 1, j, zFlood);
			fillElement(targetGrd, srcGrd, i + 1, j, zFlood);
			fillElement(targetGrd, srcGrd, i, j - 1, zFlood);
			fillElement(targetGrd, srcGrd, i, j + 1, zFlood);
		}
	}
}
