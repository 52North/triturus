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

import java.util.List;

import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgPoint;

/** 
 * Triturus example application: Reads an elevation grid from a file, finds
 * local and global minima and extrema in it, and writes the results in CSV
 * format to the console.<br/>
 * <br/>
 * To visualize the identified data in ParaView, you might want to follow
 * these steps:<br/>
 * 1. For the input grid, generate a file that can be read by ParaView, e.g.
 * as shown in the {@link SlopeAnalysisApp}.<br/>
 * 2. Copy the console output of this application to an ASCII file (with 
 * file extension <i>.csv</i>, open this file in ParaView, add a 
 * "TableToPoints" filter there, assign the columns "X", "Y", and "Z" to 
 * be used as point geometry, and map the column "CATEGORY" to proper
 * colors using the "Points" representation.<br/>
 * 3. Optionally, choose a proper scaling for all visualized data (e.g.
 * (1, 1, 10)) to exaggerate the displayed terrain situation.
 * 
 * @author Benno Schmidt
 */
public class TerrainAnalysisApp
{
    static private String 
        input = "data/test.asc"; // input as ArcInfo ASCII grid
    
    static public void main(String args[])
    {
        // Read elevation grid:
        GmSimpleElevationGrid grid =
            new IoElevationGridReader("ArcIGrd")
            .read(input);

        List<VgAttrFeature> res;
        for (int k = 0; k < 3; k++) {
            switch (k) {
            case 0: 
                // Find local minima:
                res =
                    new FltElevationGridFindExtremePoints
                    (FltElevationGridFindExtremePoints.AnalysisMode.LOC_MIN)
                    .transform(grid);
                writeCSV(res, "CATEGORY");
                break;
            case 1: 
                // Find local maxima:
                res =
                    new FltElevationGridFindExtremePoints
                    (FltElevationGridFindExtremePoints.AnalysisMode.LOC_MAX)
                    .transform(grid);
                writeCSV(res, "CATEGORY");
                break;
            case 2: 
                // Find global extrema:
                res =
                    new FltElevationGridFindExtremePoints
                    (FltElevationGridFindExtremePoints.AnalysisMode.GLOBAL_EXTR)
                    .transform(grid);
                writeCSV(res, "EXTR_TYPE");
                break;
            }
        }
    }
    
    static public void writeCSV(List<VgAttrFeature> res, String attrName) 
    {
        System.out.println("X,Y,Z," + attrName);
        for (VgAttrFeature p : res) {
            System.out.print(((VgPoint) p.getGeometry()).getX());
            System.out.print("," + ((VgPoint) p.getGeometry()).getY());
            System.out.print("," + ((VgPoint) p.getGeometry()).getZ());
            System.out.println("," + p.getAttributeValue(attrName));
        }
        System.out.println();
    }
}
