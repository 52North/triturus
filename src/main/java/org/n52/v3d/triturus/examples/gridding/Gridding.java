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
package org.n52.v3d.triturus.examples.gridding;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Triturus example application: Reads point coordinates from an ASCII file, 
 * constructs an elevation-grid (lattice model), and writes the result to a 
 * X3DOM scene.
 *
 * @author Benno Schmidt, Adhitya Kamakshidasan
 */
public class Gridding 
{
    private String inputFile = "data/test.xyz";
    private String outputFile = "data/test.html";
    private double cellSize = 50.;
    private short samplingMethod = 1;
    private String outputFormat = IoElevationGridWriter.X3DOM;

    
    public static void main(String args[]){
        Gridding app = new Gridding();

        List<VgPoint> points = app.readPointCloud();
        GmSimpleElevationGrid elevGrid = app.performGridding(points);
        app.writeOutputFile(elevGrid);
    }

    /**
     * sets the input file.
     * 
     * @param inputFile File name (optionally including file path)
     */
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFile() {
        return this.inputFile;
    }

    /**
     * reads point data from the specified input file.
     *  
     * @return List of {@link VgPoint}-objects 
     * @see {@link this#setInputFile(String)}
     */
    public List<VgPoint> readPointCloud() 
    {
        IoPointListReader reader = new IoPointListReader("Plain");

        ArrayList<VgPoint> pointList = null;

        try {
            pointList = reader.readFromFile(inputFile);
            int N = pointList.size();
            System.out.println("Number of read points: " + N);
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }    
        
        return pointList;
    }
       
    /**
     * sets the cell size of the elevation grid to be set up.
     * 
     * @param cellSize Grid cell size
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }
    
    public double getCellSize(){
        return cellSize;
    }
    
    /**
     * sets the sampling method to be used. Default-value is 
     * {@link FltPointSet2ElevationGrid.cNearestNeighbor}.
     * 
     * @param samplingMethod Sampling method identifier
     */
    public void setSamplingMethod(short samplingMethod) {
        this.samplingMethod = samplingMethod;
    }
    
    public short getSamplingMethod(){
        return samplingMethod;
    }
    
    /**
     * sets the format to be used for file output.  
     * 
     * @param outputFormat File format type identifier
     * @see IoElevationGridWriter
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
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
                GmEnvelope env = new GmEnvelope((GmPoint) pointList.get(0));
                for (int i = 1; i < N; i++) {
                    env.letContainPoint((GmPoint) pointList.get(i));
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

                double searchRadius = 0.9 * cellSize; // well...
                System.out.println("Search radius: " + searchRadius);

                FltPointSet2ElevationGrid gridder = 
                	new FltPointSet2ElevationGrid(geom, samplingMethod, searchRadius);

                System.out.println("Amount of heap-space required: " +
                	(gridder.estimateMemoryConsumption() / 1000) + " KBytes");
                System.out.println("# points inside search-circle: " + 
                	gridder.numberOfPointsInSearchCircle());
                System.out.println("Starting gridding for " + N + " input points...");
                
                resGrid = gridder.transform(pointList);

                if (!resGrid.isSet()) {
                    System.out.println("Could not assign values to all lattice points!");
                }
            }
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return resGrid;
    }
    
    /**
     * sets the output file name.
     * 
     * @param outputPath File name (optionally including file path)
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    /**
     * writes elevation grid to the specified output.
     * 
     * @param elevGrid Elevation grid
     * @see {@link this#setOutputFile(String)}
     * @see {@link this#setOutputFormat(String)}
     */
    public void writeOutputFile(GmSimpleElevationGrid elevGrid) {
        try {
            System.out.println("Writing result file...");

            IoElevationGridWriter gridWriter 
            	= new IoElevationGridWriter(outputFormat);
            gridWriter.writeToFile(elevGrid, outputFile);
            
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
