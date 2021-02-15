/**
 * Copyright (C) 2016 52North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.gisimplm.*;

/**
 * Triturus example application: Calculates the difference of two elevation 
 * grids read from the files <tt>inFile1</tt> and <tt>inFile2</tt>. The result
 * will be written to <tt>outFile</tt>.
 *
 * @author Benno Schmidt
 */
public class DifferenceGrid
{
    static public String 
        inFile1 = "data/test.asc",
        inFile2 = "data/test.asc",
        outFile = "data/test_diff.asc";

    static public void main(String args[])
    {
        IoElevationGridReader reader = 
            new IoElevationGridReader(IoFormatType.ARCINFO_ASCII_GRID);
        FltElevationGridDifference flt = 
            new FltElevationGridDifference();
        IoElevationGridWriter writer = 
            new IoElevationGridWriter(IoFormatType.ARCINFO_ASCII_GRID);
        GmSimpleElevationGrid grid1, grid2, res;
        
        try {
            // Read the source grids:
            grid1 = reader.read(inFile1);
            grid2 = reader.read(inFile2);

            System.out.println(grid1);
            System.out.println(grid2);

            // Compute difference grid:
            res = flt.transform(grid2, grid1);

            System.out.println(res);

            // Write output:
            writer.writeToFile(res, outFile);

            System.out.println("Wrote result file \"" + outFile + "\".");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }

        System.out.println("Success!");
    }
}
