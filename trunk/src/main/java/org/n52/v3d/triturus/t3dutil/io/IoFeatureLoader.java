/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
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
