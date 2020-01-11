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
package org.n52.v3d.triturus.examples.xtin;

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.IoTINWriter;
import org.n52.v3d.triturus.t3dutil.SimpleDelaunay;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.xtin.XTIN;

/**
 * Triturus example application: Constructs an TIN from a set of randomly 
 * distributed points.
 *
 * @author Benno Schmidt
 */
public class RandomTINApp 
{
	private String outFilename = "/projects/Triturus/data/randomtin.obj"; 
	private String outputFormat = IoFormatType.OBJ; 

    private int N = 3000; 
    private double 
    	XMIN = 3500000., XMAX = 3600000., YMIN = 5700000., YMAX = 5800000.;
    
    private String crs = GmPoint.SRSGkk3;
    
    public static void main(String args[]) {	
    	new RandomTINApp().run();
    }
    
    private void run() 
    {
    	// Generate N points randomly distributed in the range [XMIN .. XMAX],
    	// [YMIN .. YMAX]:
    	List<VgPoint> points = randomPoints();  
		// Perform triangulation:
        int[] res = SimpleDelaunay.triangulate(points);

        XTIN tin = new XTIN();
        VgPoint p;
        for (int i = 0; i < N; i++) {
        	p = points.get(i);
        	p.setSRS(crs);
        	tin.addLocation(i, points.get(i));
        }
        for (int i = 0; i < res.length / 3; i++) {
        	tin.addTriangle(res[3*i], res[3*i+1], res[3*i+2]);
        }

		// Generate result file:
		IoTINWriter writer = new IoTINWriter(outputFormat);
		try {
			writer.writeToFile(tin.asSimpleTINFeature(), outFilename);
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
		System.out.println("Wrote the file \"" + outFilename + "\".");
    }

	private List<VgPoint> randomPoints() {
		double x, y, z;
		VgPoint p;
		ArrayList<VgPoint> res = new ArrayList<VgPoint>();
		for (int i = 0; i < N; i++) {
			x = (XMAX - XMIN) * Math.random() + XMIN;
			y = (YMAX - YMIN) * Math.random() + YMIN;
			z = 0.; 
			p = new GmPoint(x, y, z); 
			p.setSRS(crs); 
			res.add(p);
		}
		return res;
	}
}
