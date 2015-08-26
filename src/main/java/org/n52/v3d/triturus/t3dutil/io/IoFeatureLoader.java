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
package org.n52.v3d.triturus.t3dutil.io;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.gisimplm.IoTINReader;
import org.n52.v3d.triturus.vgis.VgFeature;

/**
 * Helper class to load VgFeature objects.
 * @author Torsten Heinen
 */
public class IoFeatureLoader extends IoObject {
	public static final int GRID_ACGEO = 0;
	public static final int TIN_ACGEO = 1;
	public static final int GRID_IMG = 2;
	
	public static String DEM_GRID_PATH;
	public static String DEM_TIN_PATH;
			
	/**
     * @deprecated
	 * Constructor.
	 */
	public IoFeatureLoader() {
		this("testdata/DEM-Grid/", "testdata/DEM-TIN/");
	}

	/**
	 * Constructor.
	 * @param demGridPath Path to folder holding elevation-grid data
	 * @param demTinPath Path to folder holding TIN data
	 */
	public IoFeatureLoader(String demGridPath, String demTinPath) {
		DEM_GRID_PATH = demGridPath;
		DEM_TIN_PATH = demTinPath;
	}
	
	
	/**
     * loads elevation-models in arbitrary formats.<br /><br />
	 * <i>German:</i> Methode zum Laden von H�henmodellen unbekannten Formates. Intern werden alle bekannten Formate
	 * ausprobiert.
	 * @param location Ort der Daten, kann eine URL (file oder http) oder ein realtiver Dateiname sein (wenn Pfad �ber Konstruktor gesetzt).
	 * @return das geladene H�henmodell-Feature 
	 * @throws T3dException
	 */
    // todo engl. JavaDoc für Parameter
	public VgFeature loadFeature(String location) throws T3dException {
		VgFeature feature = null;
		StringBuffer log = new StringBuffer("<IoFeatureLoader> Loading " + location + " ...");
		System.out.println(log.toString());	
		try {
			// alle formate ausprobieren...
			for (int i=0; i<2; i++) {
				//System.out.println("<IoFeatureLoader> ... " +i+ " at "+ location);
				feature = loadFeature(location, i);
				if ( feature != null)
					break;
			}
		} catch (T3dException t3de) {
			throw new T3dException("<IoFeatureLoader> Error loading "+location);
		}	
		
		return feature;
	}
	
	/**
	 * loads an elevation-model with a given format.<br /><br />
	 * <i>German:</i> Methode zu Laden eines H&ouml;henmodells (andere Features (Points,etc) m&uuuml;ssen noch
     * hinzugef&uuml;gt werden) mit bekanntem Format.
	 * @param location Ort der Daten, kann eine URL (file oder http) oder ein relativer Dateiname sein (wenn Pfad �ber Konstruktor gesetzt).
	 * @param type
	 * @return das geladene H�henmodell-Feature
	 * @throws T3dException
	 */
    // todo engl. JavaDoc für Parameter
	public VgFeature loadFeature(String location, int type) throws T3dException {
		StringBuffer log = new StringBuffer("<IoFeatureLoader> Loading ");
		long sTime = System.currentTimeMillis();		
		VgFeature feature = null;
		
		try {
			switch (type) {
				case (GRID_ACGEO): {
					IoElevationGridReader lReader = new IoElevationGridReader("AcGeo");

					if (!location.startsWith("http:") && !location.startsWith("file:"))
						location = DEM_GRID_PATH+location;

					log.append("ArcInfoAsciiGrid ");
					feature = lReader.read(location);
					break;
				}
				case (GRID_IMG): {
					IoElevationGridReader lReader = new IoElevationGridReader("IdrisiIMG");

					if (!location.startsWith("http:") && !location.startsWith("file:"))
						location = DEM_GRID_PATH+location;

					log.append("ArcInfoAsciiGrid ");
					feature = lReader.read(location);
					break;
				}
				case (TIN_ACGEO): {
					if (!location.startsWith("http:") && !location.startsWith("file:"))
						location = DEM_TIN_PATH+location;
					log.append("ArcInfoAsciiTIN ");
					
					IoTINReader lReader = new IoTINReader("AcGeo");
					feature = lReader.read(location);
					break;
				}
				default: 
					throw new T3dException("<IoFeatureLoader> Data Format "+type+" not supported yet.");					
			}
		} catch (T3dException t3de) {
			t3de.printStackTrace();
			//throw new T3dException("<IoFeatureLoader> Error loading "+location);
		}

		if (feature == null)
			log.append("failed! Location: " + location);
		else
			log.append("in "+(System.currentTimeMillis()-sTime)+"ms: "+location);
				
		System.out.println(log.toString());
		return feature;
	}

	public String log() {
		// TODO Auto-generated method stub
		return null;
	}
}
