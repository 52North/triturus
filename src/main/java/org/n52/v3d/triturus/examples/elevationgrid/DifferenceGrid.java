/**
 * Copyright (C) 2016 52 North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.gisimplm.*;

/**
 * Triturus example application: Calculated the difference of two elevation grids.
 *
 * @author Benno Schmidt
 */
public class DifferenceGrid
{
	public static void main(String args[])
	{
        IoElevationGridReader reader = new IoElevationGridReader(IoElevationGridReader.ARCINFO_ASCII_GRID);

        try {
            // Read the source grids:
            GmSimpleElevationGrid
                    grid1 = reader.readFromFile("/data/grd1.grd"),
                    grid2 = reader.readFromFile("/data/grd2.grd");

            FltElevationGridDifference flt = new FltElevationGridDifference();
            GmSimpleElevationGrid res = flt.transform(grid2, grid1);

            System.out.println(res);
            System.out.print("The elevation grid's bounding-box: ");
            System.out.println(res.envelope().toString());

            // Write VRML output:
            IoElevationGridWriter writer = new IoElevationGridWriter(IoElevationGridWriter.VRML2);
            writer.writeToFile(grid1, "/data/grd1.wrl");
            writer.writeToFile(grid2, "/data/grd2.wrl");
            writer.writeToFile(res, "/data/grd_diff.wrl");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
    }
}





