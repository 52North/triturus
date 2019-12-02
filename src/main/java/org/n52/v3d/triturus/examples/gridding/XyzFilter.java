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
package org.n52.v3d.triturus.examples.gridding;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Triturus example application: Reads point coordinates from an ASCII file 
 * (x, y, z and optionally additional data columns) and writes a filtered 
 * subset back to an ASCII file.
 *
 * @author Benno Schmidt
 */
public class XyzFilter 
{
    private String inputFile = "data/example42.asc";
    private String outputFile = "data/example42.xyz";
    private String outputFormat = IoPointListWriter.PLAIN;

    
    public static void main(String args[]) {
        new XyzFilter().run();
    }
    
    public void run() {
        List<VgPoint> points = readPointCloud();
        writeOutputFile(points);
        System.out.println("Bounding-box: " + envelope(points));
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
     * writes elevation grid to the specified output.
     * 
     * @param elevGrid Elevation grid
     * @see {@link this#setOutputFile(String)}
     * @see {@link this#setOutputFormat(String)}
     */
    public void writeOutputFile(List<VgPoint> points) {
        try {
            System.out.println("Writing result file...");

            IoPointListWriter writer = new IoPointListWriter(outputFormat);
            writer.writeToFile(points, outputFile);
          
            System.out.println("Success!");
        }
        catch (T3dException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private VgEnvelope envelope(List<VgPoint> points) {
    	GmEnvelope env = null;
        int N = points.size();
        if (N > 0) {
            env = new GmEnvelope((GmPoint) points.get(0));
            for (int i = 1; i < N; i++) {
                env.letContainPoint((GmPoint) points.get(i));
            }
        }  
    	return env;
    }
}
