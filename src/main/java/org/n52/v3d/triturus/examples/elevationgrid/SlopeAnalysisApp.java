/**
 * Copyright (C) 2021 52 North Initiative for Geospatial Open Source 
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

import org.n52.v3d.triturus.gisimplm.*;

/** 
 * Triturus example application: Reads an elevation grid from a file, computes 
 * terrain slopes (also known as &quot;dip&quot; or (&quot;inclination&quot;) 
 * and generates a corresponding output grid. 
 * 
 * @author Benno Schmidt
 */
public class SlopeAnalysisApp
{
    static private String 
        input = "data/test.asc", // input as ArcInfo ASCII grid
        inputAsVtk = "data/test.vtk", // input converted to VTK format
        output = "data/test_slope.vtk"; // slope grid in VTK format
    
    static public void main(String args[])
    {
        GmSimpleElevationGrid grid =
            new IoElevationGridReader("ArcIGrd")
            .read(input);
        // Control output:
        System.out.println(grid);
        System.out.print("The elevation grid's bounding-box: ");
        System.out.println(grid.envelope().toString());
        
        GmSimpleFloatGrid res =
            new FltElevationGridGradientOperators
            (FltElevationGridGradientOperators.AnalysisMode.SLOPE)
            .transform(grid);
        // Note: You may want to change the analysis mode parameter to perform 
        // other computations, e.g. ASPECT_CATEGORIES_4 or PROFILE_CURVATURE.
        // Control output:
        System.out.println(res);
        System.out.print("The data grid's bounding-box: ");
        System.out.println(res.envelope().toString());

        new IoElevationGridWriter("VTKDataset")
            .writeToFile(grid, inputAsVtk);
        new IoFloatGridWriter("VTKDataset")
            .writeToFile(res, output);
        // Control output:
        System.out.println("Wrote result file \"" + output + "\".");
        System.out.println("Success!");
    }
}
