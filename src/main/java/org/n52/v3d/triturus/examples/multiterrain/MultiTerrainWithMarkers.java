/**
 * Copyright (C) 2012-2016 52 North Initiative for Geospatial Open Source
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
package org.n52.v3d.triturus.examples.multiterrain;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.t3dutil.*;
import org.n52.v3d.triturus.t3dutil.symboldefs.T3dSphere;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.visx3d.VrmlX3dSceneGenerator;
import org.n52.v3d.triturus.vscene.MultiTerrainScene;

/** 
 * Triturus example application: Visualizes two elevation grids with markers added
 *
 * @author Benno Schmidt
 */
public class MultiTerrainWithMarkers                
{
	public static void main(String args[]) {
        MultiTerrainWithMarkers app = new MultiTerrainWithMarkers();
        app.run();
    }

    private void run()
    {
        IoElevationGridReader reader = new IoElevationGridReader("ArcIGrd");

		try {
            // Read elevation grids:
			GmSimpleElevationGrid
                grid1 = reader.readFromFile("data/test.asc"),
                grid2 = reader.readFromFile("data/test_flood.asc");

            // Define some points:
            VgPoint
                pt1 = new GmPoint(2592193.5, 5707993.5, 37.0),
                pt2 = new GmPoint(2615692.25, 5730641.5, 161.6999969482422);

            // Define marker symbol:
            T3dSymbolDef sym = new T3dSphere(100.);
            T3dColor col = new T3dColor(0,1,1);

            // Set markers:
            T3dSymbolInstance
                m1 = new T3dSymbolInstance(sym, pt1),
                m2 = new T3dSymbolInstance(sym, pt2);
                m1.setColor(col);
                m2.setColor(col);

            // Construct 3D scene:
            MultiTerrainScene s = new MultiTerrainScene();
            s.addTerrain(grid1);
            s.addTerrain(grid2);
            s.addMarker(m1);
            s.addMarker(m2);
            s.setDefaultExaggeration(8.0);

            System.out.println("Bounding-box s: " + s.envelope());

            for (int i = 0; i < s.getTerrains().size(); i++) {
                this.prepare((GmSimpleElevationGrid) s.getTerrains().get(i)); // just replaces NODATA values...
            }

            MpHypsometricColor colMapper = this.defineReliefColoring();
            s.setHypsometricColorMapper(colMapper);

            // Export scene as VRML file:
            VrmlX3dSceneGenerator res = new VrmlX3dSceneGenerator(s);
            String file1 = "data/result.x3d";
            res.writeToX3dFile(file1);
            System.out.println("Finished writing " + file1 + "...");
            String file2 = "data/result.html";
            res.writeToX3domFile(file2);
            System.out.println("Finished writing " + file2 + "...");
            System.out.println("Success!");
        }
		catch (T3dException e) {
			e.printStackTrace();
		}
    }

    private void prepare(GmSimpleElevationGrid grid)
    {
        // This is just some control output:
		System.out.println(grid);
        System.out.println("Bounding-box: " + grid.envelope());

        // If some grid cell's have NODATA values, assign values...
        for (int j = 0; j < grid.numberOfColumns(); j++) {
            for (int i = 0; i < grid.numberOfRows(); i++) {
                if (!grid.isSet(i, j)) {
                    grid.setValue(i, j, grid.minimalElevation());
                }
            }
        }
    }

    private MpHypsometricColor defineReliefColoring()
    {
        MpHypsometricColor colMapper = new MpSimpleHypsometricColor();
        double elev[] = {30., 80, 130, 180.};
        T3dColor cols[] = {
            new T3dColor(0.0f, 0.8f, 0.0f), // green
            new T3dColor(1.0f, 1.0f, 0.5f), // pale yellow
            new T3dColor(0.78f, 0.27f, 0.0f), // brown
            new T3dColor(0.82f, 0.2f, 0.0f)}; // red/brown
        ((MpSimpleHypsometricColor) colMapper).setPalette(elev, cols, true);
        return colMapper;
    }
}
