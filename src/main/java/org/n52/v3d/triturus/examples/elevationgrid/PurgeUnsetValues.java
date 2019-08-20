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
 * format and assigns a fixed value <tt>this.z0</tt> to all unset cells.
 * 
 * @author Benno Schmidt
 */
public class PurgeUnsetValues
{
	private String 
		inputFile = "../data/test.grd",
		outputFile = "../data/test.grd";
	private double z0 = 42.0;
	
	public static void main(String args[]) {
		new PurgeUnsetValues().run();
	}
	
	public void run() 
	{
		IoElevationGridReader reader = new IoElevationGridReader(
			IoElevationGridReader.ARCINFO_ASCII_GRID);
		IoElevationGridWriter writer = new IoElevationGridWriter(
			IoElevationGridWriter.ARCINFO_ASCII_GRID);
		
		GmSimpleElevationGrid grd;
		
		try {
			// Read the elevation grid from file:
			grd = reader.readFromFile(inputFile);
			
			// This is just some control output:
			System.out.println(grd);
			System.out.print("The input elevation grid's bounding-box: ");
			System.out.println(grd.envelope().toString());

			for (int i = 0; i < grd.numberOfRows(); i++) {
				for (int j = 0; j < grd.numberOfColumns(); j++) {
					if (!grd.isSet(i, j))
						grd.setValue(i, j, z0);
				}
			}
			
			System.out.print("The output elevation grid's bounding-box: ");
			System.out.println(grd.envelope().toString());

			// Write result:
			writer.writeToFile(grd, outputFile);
			System.out.println("Success!");
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
	}
}
