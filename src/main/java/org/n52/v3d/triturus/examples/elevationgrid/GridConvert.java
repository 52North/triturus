/**
 * Copyright (C) 2007-2016 52North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;

/** 
 * Triturus example application: Reads an elevation grid in ArcInfo ASCII grid 
 * format and writes it to a X3D file.
 * 
 * @author Benno Schmidt
 * @see GridConvertApp
 */
public class GridConvert
{
    public static void main(String args[])
    {
        IoElevationGridReader reader = 
            new IoElevationGridReader(IoFormatType.ARCINFO_ASCII_GRID);

        try {
            // Read the elevation grid from file:
            GmSimpleElevationGrid grid = 
                reader.read("data/test.asc");

            // This is just some control output:
            System.out.println(grid);
            System.out.print("The elevation grid's bounding-box: ");
            System.out.println(grid.envelope().toString());

            // If some grid cell's have NODATA values, assign a value...
            for (int j = 0; j < grid.numberOfColumns(); j++) {
                for (int i = 0; i < grid.numberOfRows(); i++) {
                    if (!grid.isSet(i, j))
                        grid.setValue(i, j, 0.0);
                }
            }

            // Write X3DOM output:
            IoElevationGridWriter writer = 
                new IoElevationGridWriter(IoFormatType.X3D);
            writer.writeToFile(grid, "data/test.x3d");
            System.out.println("Success!");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
    }
}
