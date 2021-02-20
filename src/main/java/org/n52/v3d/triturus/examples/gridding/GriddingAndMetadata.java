/**
 * Copyright (C) 2021 52North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.gisimplm.FltPointSet2InterpolationQualityIndicators;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;
import org.n52.v3d.triturus.gisimplm.IoFloatGridWriter;
import org.n52.v3d.triturus.gisimplm.IoPointListReader;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Triturus example application: Reads point coordinates from an ASCII file, 
 * constructs an elevation-grid (lattice model), and writes the result to an 
 * ASCII file in VTK format. Additionally, meta-data files giving information  
 * about the gridding process is provided.<br/>
 * <br/>
 * Note: To run this program for large data sets, the JVM might need more 
 * heap space than usual. To increase the default value, you have to set
 * proper VM argument, e.g. to assign 1024 MB:<br/>
 * <br/>
 * <tt>java -Xmx1024m GriddingAndMetadata</tt>
 *
 * @author Benno Schmidt
 */
public class GriddingAndMetadata 
{
    /**
     * File name body. Thus, the input file will be <tt>fileNameBody + ".xyz</tt>,
     * the output files will be named <tt>fileNameBody + &lt;type&gt; + ".vtk"</tt>
     * with the appendix <tt>&lt;type&gt;</tt> giving the meta-data identifier 
     * ("MIN_Z", "MAX_Z", "DELTA_Z"; etc. or an empty appendix string for the target 
     * elevation grid, i.e. <tt>fileNameBody + ".vtk"</tt>
     */
    public String fileNameBody = "data/test"; 

    private String inputFile = fileNameBody + ".xyz"; 
    /**
     * Delimiter used to separate <i>x</i>, <i>y</i>, and <i>z</i>-values in 
     * the input file.
     */
    public String delimiter = " ";
    
    /**
     * Output filename (elevation grid)
     */
    public String outputFile = fileNameBody + ".vtk";

    private FltPointSet2InterpolationQualityIndicators gridder;

    /**
     * Cell size of target grid
     */
    public double cellSize = 200.; 
    
    /**
     * Search radius 
     */
    public double searchRadius = 50.; 
    
    /**
     * Sampling method
     */
    private short samplingMethod = FltPointSet2ElevationGrid.cInverseDist;

    /**
     * Output filename (meta-data)
     */
    public String 
        outputMIN_Z = fileNameBody + FltPointSet2InterpolationQualityIndicators.MIN_Z + ".vtk",
        outputMAX_Z = fileNameBody + FltPointSet2InterpolationQualityIndicators.MAX_Z + ".vtk",
        outputDELTA_Z= fileNameBody + FltPointSet2InterpolationQualityIndicators.DELTA_Z + ".vtk",
        outputHIT_COUNT = fileNameBody + FltPointSet2InterpolationQualityIndicators.HIT_COUNT + ".vtk",
        outputCLUSTERS = fileNameBody + FltPointSet2InterpolationQualityIndicators.CLUSTERS + ".vtk",
        outputNOISE = fileNameBody + FltPointSet2InterpolationQualityIndicators.NOISE + ".vtk";
    
    
    public static void main(String args[])
    {
        GriddingAndMetadata app = new GriddingAndMetadata();
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
        System.out.println("Reading input file...");
        List<VgPoint> pointList = null;
        IoPointListReader reader = new IoPointListReader("Plain");
        reader.setDelimiter(delimiter); 
        try {
            pointList = reader.readFromFile(inputFile);
            System.out.println("Number of read points: " + pointList.size());
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
        if (pointList != null && pointList.size() > 0) { 
            try {
                VgEnvelope env = new GmEnvelope(pointList.get(0));
                for (int i = 1; i < pointList.size(); i++) {
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

                gridder = new FltPointSet2InterpolationQualityIndicators(
                    geom, 
                    samplingMethod, 
                    searchRadius);

                System.out.print("Amount of heap space required: ");
                int memory = 
                        gridder.estimateMemoryConsumption() + // Gridding
                        nx * ny * (3 * 8 + 4) + // ArrayList holding source points 
                        nx * ny * (8 + 4); // ArrayList holding elevations 
                System.out.println((memory / 1000) + " KBytes");

                System.out.println("Starting gridding and computing meta-data...");
                resGrid = gridder.transform(pointList);

                if (!resGrid.isSet()) {
                    System.out.println("Could not assign values to all lattice points!");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resGrid;
    }
    
    /**
     * writes the elevation grid and additional data files to the specified outputs.
     * 
     * @param elevGrid Elevation grid
     */
    public void writeOutputFiles(GmSimpleElevationGrid elevGrid) {
        try {
            System.out.println("Writing elevation grid to file...");
            new IoElevationGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(elevGrid, outputFile);
                
            System.out.println("Writing additional files giving meta-information");
            
            System.out.println(FltPointSet2InterpolationQualityIndicators.MIN_Z + "...");
            new IoElevationGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaMIN_Z(), outputMIN_Z);

            System.out.println(FltPointSet2InterpolationQualityIndicators.MAX_Z + "...");
            new IoElevationGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaMAX_Z(), outputMAX_Z);
            
            System.out.println(FltPointSet2InterpolationQualityIndicators.DELTA_Z + "...");
            new IoElevationGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaDELTA_Z(), outputDELTA_Z);
            
            System.out.println(FltPointSet2InterpolationQualityIndicators.HIT_COUNT + "...");
            new IoFloatGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaHIT_COUNT(), outputHIT_COUNT);
            
            System.out.println(FltPointSet2InterpolationQualityIndicators.CLUSTERS + "...");
            new IoFloatGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaCLUSTERS(), outputCLUSTERS);

            System.out.println(FltPointSet2InterpolationQualityIndicators.NOISE + "...");
            new IoFloatGridWriter(IoFormatType.VTK_DATASET)
                .writeToFile(gridder.metaNOISE(), outputNOISE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
