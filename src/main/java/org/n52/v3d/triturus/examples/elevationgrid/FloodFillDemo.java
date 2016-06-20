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
package org.n52.v3d.triturus.examples.elevationgrid;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.FltElevationGridFloodFill;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;
import org.n52.v3d.triturus.vgis.VgPoint;

/** 
 * Triturus example application: Reads an elevation grid in ArcInfo ASCII grid 
 * format, computes a simple flooding-situation, and generates another
 * elevation grid which holds the information about the flooded area.
 * 
 * @author Benno Schmidt
 */
public class FloodFillDemo
{
	private String 
		inputFile = "/data/example_dem.asc",
		outputFile = "/data/example_dem_flood_2.asc";

	public static void main(String args[])
	{
		FloodFillDemo app = new FloodFillDemo();
		app.run();
	}
	
	public void run() 
	{
		IoElevationGridReader reader = new IoElevationGridReader(
			IoElevationGridReader.ARCINFO_ASCII_GRID);
		IoElevationGridWriter writer = new IoElevationGridWriter(
			IoElevationGridWriter.ARCINFO_ASCII_GRID);
		
		GmSimpleElevationGrid srcGrd, targetGrd;
		
		try {
			// Read the elevation grid from file:
			srcGrd = reader.readFromFile(inputFile);
			
			// This is just some control output:
			System.out.println(srcGrd);
			System.out.print("The elevation grid's bounding-box: ");
			System.out.println(srcGrd.envelope().toString());
			VgPoint seedPoint = new GmPoint(srcGrd.envelope().getCenterPoint());
			// Set water-level 2 meters above ground:
			seedPoint.setZ(
				srcGrd.getValue(
					srcGrd.numberOfRows() / 2, 
					srcGrd.numberOfColumns() / 2) 
				+ 2.); 
			System.out.println( "Seed: " + seedPoint);
			
			FltElevationGridFloodFill flt = new FltElevationGridFloodFill();
			targetGrd = (GmSimpleElevationGrid) flt.transform(srcGrd, seedPoint);
			
			// Write result:
			writer.writeToFile(targetGrd, outputFile);
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
	}
}
