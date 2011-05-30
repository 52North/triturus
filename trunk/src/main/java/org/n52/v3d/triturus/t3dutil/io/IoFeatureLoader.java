/*
 * Created on 12.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.n52.v3d.triturus.t3dutil.io;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.gisimplm.IoTINReader;
import org.n52.v3d.triturus.vgis.VgFeature;

/**
 * Hilfklasse zum Laden von VgFeatures. 
 *
 * @author Torsten Heinen
 * Copyright (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoFeatureLoader extends IoObject {
	public static final int GRID_ACGEO = 0;
	public static final int TIN_ACGEO = 1;
	public static final int GRID_IMG = 2;
	
	public static String DEM_GRID_PATH;
	public static String DEM_TIN_PATH;
			
	/**
	 * Default-Konstruktor. 
	 */
	public IoFeatureLoader() {
		this("testdata/DEM-Grid/", "testdata/DEM-TIN/");
	}

	/**
	 * Konstruktor mit Pfadangaben zu Grid und Tin Daten. 
	 * 
	 * @param demGridPath
	 * @param demTinPath
	 */
	public IoFeatureLoader(String demGridPath, String demTinPath) {
		DEM_GRID_PATH = demGridPath;
		DEM_TIN_PATH = demTinPath;
	}
	
	
	/**
	 * Methode zum Laden von Höhenmodellen unbekannten Formates. Intern werden alle bekannten Formate 
	 * ausprobiert.
	 *   
	 * @param location Ort der Daten, kann eine URL (file oder http) oder ein realtiver Dateiname sein (wenn Pfad über Konstruktor gesetzt).
	 * @return das geladene Höhenmodell-Feature 
	 * @throws T3dException
	 */
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
	 * Methode zu Laden eines Höhenmodells (andere Features (Points,etc) müssen noch hinzugefügt werden) mit bekanntem Format.
	 * 
	 * @param location Ort der Daten, kann eine URL (file oder http) oder ein relativer Dateiname sein (wenn Pfad über Konstruktor gesetzt).
	 * @param type
	 * @return das geladene Höhenmodell-Feature
	 * @throws T3dException
	 */
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
	
	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.core.IoObject#log()
	 */
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}
}
