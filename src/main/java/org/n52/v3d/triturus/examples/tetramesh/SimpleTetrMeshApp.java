/**
 * Copyright (C) 2020 52North Initiative for Geospatial Open Source 
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
package org.n52.v3d.triturus.examples.tetramesh;

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimpleTetrMesh;
import org.n52.v3d.triturus.gisimplm.IoTetrMeshWriter;

/**
 * Triturus example application: Constructs a simple tetrahedronal mesh. 
 *
 * @author Benno Schmidt
 */
public class SimpleTetrMeshApp 
{
    private String outputFile = "/projects/Triturus/data/tetramesh.vtk";
    private String outputFormat = IoFormatType.VTK_DATASET;

    
    public static void main(String args[]) 
    {
        new SimpleTetrMeshApp().run();;
    }
    
    private void run() {
        GmSimpleTetrMesh mesh = new GmSimpleTetrMesh();
     
        mesh.addPoint(new GmPoint(0., 0., 0.)); // vertex 0
        mesh.addPoint(new GmPoint(10., 0., 0.)); // 1
        mesh.addPoint(new GmPoint(10., 10., 0.)); // 2
        mesh.addPoint(new GmPoint(0., 10., 0.)); // 3
        mesh.addPoint(new GmPoint(0., 0., 10.)); // 4
        mesh.addPoint(new GmPoint(10., 0., 10.)); // 5
        mesh.addPoint(new GmPoint(10., 10., 10.)); // 6
        mesh.addPoint(new GmPoint(0., 10., 10.)); // 7
        mesh.addTetrahedron(0, 1, 3, 4); // tetrahedron 0
        mesh.addTetrahedron(5, 6, 7, 2); // 1
        mesh.addTetrahedron(1, 4, 3, 7); // 2
        
        System.out.println(mesh);
        //System.out.println(mesh.envelope());

        try {
            System.out.println("Writing result file \"" + outputFile + "\"...");
            new IoTetrMeshWriter(outputFormat).writeToFile(mesh, outputFile);
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