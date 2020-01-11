/**
 * Copyright (C) 2007-2019 52North Initiative for Geospatial Open Source 
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
package org.n52.v3d.triturus.examples.gridding;

import java.util.List;

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgEquidistGrid;
import org.n52.v3d.triturus.vgis.VgLineSegment;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Triturus example application: Reads a simple triangle mesh ("triangulated 
 * irregular network", TIN) from an ASCII file and "rasterizes" the given 
 * surface using an elevation-grid (lattice model in x-y plane).
 *
 * @author Benno Schmidt
 */
public class TIN2Grid 
{
	private String inputFile = "/projects/Triturus/data/s_geologie_Rotliegend_ts.tin";
	private String inputFormat = IoFormatType.ACGEO; 
	private String outputFile = "/projects/Triturus/data/s_geologie_Rotliegend_ts.obj";
	private String outputFormat = IoFormatType.OBJ;
	private double cellSize = 200.;
	private String conflictFile = "/projects/Triturus/data/s_geologie_Rotliegend_ts_conflicts.vtk";
    
    public static void main(String args[]) 
    {
        TIN2Grid app = new TIN2Grid();
        
        GmSimpleTINFeature tin = app.readInputFile();
        System.out.println(tin);
        
        VgEnvelope bbox = tin.envelope();
        VgEquidistGrid grdGeom = app.setUpGeometry(bbox);
        
        FltTIN2ElevationGrid trans = new FltTIN2ElevationGrid();
        trans.setGridGeometry(grdGeom);
        trans.setZConflictHandler(
        	FltTIN2ElevationGrid.CONFLICT_TAKE_MAX_Z);

        VgElevationGrid grd = trans.transform(tin);
  
        app.writeOutputFile(grd);
        System.out.println("z_min = " + grd.minimalElevation());
        System.out.println("z_max = " + grd.maximalElevation());
        
        if (trans.conflicts().size() > 0) {
        	System.out.println("Detected " + trans.conflicts().size() + " non-unique z-values.");
        	app.writeConflictOutputFile(trans.conflicts());
        }
    }

	private VgEquidistGrid setUpGeometry(VgEnvelope bbox) 
    {
        System.out.println(bbox);

		VgPoint origin = new GmPoint(bbox.getXMin(), bbox.getYMin(), 0.0);
		// TODO: origin ist noch ein schraeger Wert -> ist zu runden gemaess cellSize!
		
		int nrows = (int)(Math.floor(bbox.getExtentY() / cellSize)) + 1;
		int ncols = (int)(Math.floor(bbox.getExtentX() / cellSize)) + 1;
		        
		GmSimple2dGridGeometry res = new GmSimple2dGridGeometry(
			ncols, nrows, origin, cellSize, cellSize);
        System.out.println(res);
        return res;
	}

	// read the input TIN:
    private GmSimpleTINFeature readInputFile() {
		GmSimpleTINFeature tin = null; 
		try {
		    System.out.println("Reading input file...");
		
		    IoTINReader reader = new IoTINReader(inputFormat);
		    tin = reader.read(inputFile);
		    
		    System.out.println("Success!");
		}
		catch (T3dException e) {
		    e.printStackTrace();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		return tin;
    }

    // write the resulting elevation grid:
    private void writeOutputFile(VgElevationGrid grd) 
    {
        try {
            System.out.println("Writing result file \"" + outputFile + "\"...");

            IoElevationGridWriter writer = new IoElevationGridWriter(outputFormat);
            writer.writeToFile((GmSimpleElevationGrid) grd, outputFile);
            
            System.out.println("Success!");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // write conflict locations to VTK line dataset:
    private void writeConflictOutputFile(List<VgLineSegment> conflicts) {
        try {
            System.out.println("Writing conflicts to file \"" + conflictFile + "\"...");

        	IoLineSegmentWriter writer = new IoLineSegmentWriter(IoFormatType.VTK_DATASET);
            writer.writeToFile(conflicts, conflictFile);
            
            System.out.println("Success!");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }		 
	}
}
