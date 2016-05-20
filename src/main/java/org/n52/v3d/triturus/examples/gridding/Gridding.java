/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.examples.gridding;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.*;

import java.util.ArrayList;

/**
 * Triturus example application: Reads point coordinates from an ASCII file and constructs an elevation-grid (lattice
 * model).
 * 
 * @author Benno Schmidt
 * @see org.n52.v3d.triturus.examples.gridding.GriddingApp
 */
public class Gridding
{
    public static void main(String args[]) 
    {
        IoPointListReader lReader = new IoPointListReader("Plain");

        ArrayList lPointList;

        try {
            lPointList = lReader.readFromFile("/triturus/data/test.xyz");

            int N = lPointList.size();
            System.out.println("Number of read points: " + N);
            
            if (N > 0) {
                GmEnvelope lEnv = new GmEnvelope((GmPoint) lPointList.get(0));
                for (int i = 1; i < N; i++)
                    lEnv.letContainPoint((GmPoint) lPointList.get(i));
                System.out.println("Bounding-box: " + lEnv.toString());

                double cellSize = 50.0;
                int nx = (int) Math.ceil(lEnv.getExtentX() / cellSize) + 1;
                int ny = (int) Math.ceil(lEnv.getExtentY() / cellSize) + 1;
                System.out.println("A lattice consisting of " + nx + " x " + ny + " elements will be set-up...");
                GmSimple2dGridGeometry lGeom = new GmSimple2dGridGeometry( 
                    nx, ny, 
                    new GmPoint(lEnv.getXMin(), lEnv.getYMin(), 0.), // lower left corner
                    cellSize, cellSize); // Cell-sizes in x- and y-direction
                                            
                double searchRadius = 0.9 * cellSize; // well...
                System.out.println("Search radius: " + searchRadius);

                FltPointSet2ElevationGrid lGridder = new FltPointSet2ElevationGrid(lGeom, searchRadius);
                System.out.println("Amount of heap-space required: " +
                    (lGridder.estimateMemoryConsumption() / 1000) + " KBytes");
                System.out.println("# points inside search-circle: " + lGridder.numberOfPointsInSearchCircle());
                
                System.out.println("Starting gridding for " + lPointList.size() + " input points...");
                GmSimpleElevationGrid lResGrid = lGridder.transform(lPointList);
 
                if (!lResGrid.isSet())
                    System.out.println("Could not assign values to all lattice points!");
                
                System.out.println("Writing result file...");
                IoElevationGridWriter lGridWriter = new IoElevationGridWriter(IoElevationGridWriter.ACGEO);
                lGridWriter.writeToFile(lResGrid, "/triturus/data/test.grd");
            } 
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
