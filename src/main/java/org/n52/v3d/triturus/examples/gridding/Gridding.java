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
import org.n52.v3d.triturus.gisimplm.FltPointSet2ElevationGrid;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridPNGWriter;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;
import org.n52.v3d.triturus.gisimplm.IoPointListReader;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Triturus example application: Reads point coordinates from an ASCII file, 
 * constructs an elevation-grid (lattice model), and writes the result to an 
 * ArcInfo ASCII grid file and additionally to a PNG file.
 *
 * @author Benno Schmidt
 */
public class Gridding 
{
    /**
     * Input filename
     */
    public String inputFile = "data/test.xyz";

    /**
     * Output filename ASCII grid
     */
    public String outputFile = "data/test.asc";

    /**
     * Output filename PNG image
     */
    public String outputFilePNG = "data/test.png";

    /**
     * Cell size of target grid
     */
    public double cellSize = 50.;
    
    /**
     * Search radius 
     */
    public double searchRadius = 50.;
    
    /**
     * Sampling method
     */
    private short samplingMethod = FltPointSet2ElevationGrid.cInverseDist;
    
    
    public static void main(String args[])
    {
        Gridding app = new Gridding();
        List<VgPoint> points = app.readPointCloud();

        GmSimpleElevationGrid elevGrid = app.performGridding(points);
        // Control output:
        System.out.println(elevGrid);
        System.out.println(elevGrid.envelope());
        
        app.writeOutputFiles(elevGrid);
        System.out.println("Success!");
    }

    /**
     * reads point data from the specified input file.
     *  
     * @return List of {@link VgPoint}-objects 
     */
    public List<VgPoint> readPointCloud() 
    {
        IoPointListReader reader = new IoPointListReader("Plain");
        
        List<VgPoint> pointList = null;

        try {
            pointList = reader.readFromFile(inputFile);
            int N = pointList.size();
            System.out.println("Number of read points: " + N);
        }
        catch (Exception e) {
            e.printStackTrace();
        }    
        
        return pointList;
    }
       
    /**
     * performs gridding.  
     * 
     * @param pointList List of {@link VgPoint}s
     * @return Elevation grid
     */
    public GmSimpleElevationGrid performGridding(List<VgPoint> pointList) 
    {
        GmSimpleElevationGrid resGrid = null;
        
        try {
            int N = pointList.size();
            if (N > 0) {
                VgEnvelope env = new GmEnvelope(pointList.get(0));
                for (int i = 1; i < N; i++) {
                    env.letContainPoint(pointList.get(i));
                }
                System.out.println("Bounding-box: " + env.toString());
                
                int nx = (int) Math.ceil(env.getExtentX() / cellSize) + 1;
                int ny = (int) Math.ceil(env.getExtentY() / cellSize) + 1;
                System.out.println("A lattice consisting of " + 
                    nx + " x " + ny + " elements will be set-up...");
                GmSimple2dGridGeometry geom = new GmSimple2dGridGeometry(
                    nx, ny,
                    new GmPoint(env.getXMin(), env.getYMin(), 0.), // lower left corner
                    cellSize, cellSize); // Cell-sizes in x- and y-direction
                System.out.println("Search radius: " + searchRadius);

                FltPointSet2ElevationGrid gridder = 
                    new FltPointSet2ElevationGrid(geom, samplingMethod, searchRadius);

                System.out.println("Amount of heap space required: " +
                    (gridder.estimateMemoryConsumption() / 1000) + " KBytes");
                System.out.println("# grid points inside search circle: " + 
                    gridder.numberOfPointsInSearchCircle());
                System.out.println("Starting gridding for " + N + " input points...");
                
                resGrid = gridder.transform(pointList);

                if (!resGrid.isSet()) {
                    System.out.println("Could not assign values to all lattice points!");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return resGrid;
    }
    
    /**
     * sets the output file name.
     * 
     * @param outputFile File name (optionally including file path)
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    /**
     * writes the elevation grid to the specified outputs.
     * 
     * @param elevGrid Elevation grid
     */
    public void writeOutputFiles(GmSimpleElevationGrid elevGrid) {
        try {
            System.out.println("Writing resulting ASCII file...");
            IoElevationGridWriter gridWriter 
                = new IoElevationGridWriter(IoFormatType.ARCINFO_ASCII_GRID);
            gridWriter.writeToFile(elevGrid, outputFile);

            System.out.println("Writing resulting PNG file...");
            IoElevationGridPNGWriter imageWriter
                = new IoElevationGridPNGWriter("TYPE_USHORT_GRAY");
            imageWriter.writeToFile(elevGrid, outputFilePNG);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
