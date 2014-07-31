/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
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
package org.n52.v3d.triturus.viskml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmMeasurementPath;
import org.n52.v3d.triturus.t3dutil.T3dSymbolInstance;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * KML scene description. This class allows to specify KML-based scene
 * descriptions, e.g. to enable geo-visualizations in GoogleEarth. Note that all
 * information that will be added to the scene must refer to WGS84 coordinates!
 * 
 * @author Benno Schmidt, Marius Klug, Christian Danowski
 */
public class KmlScene {
	private List<VgPoint> mPointGeometries;
	private List<VgAttrFeature> mPointFeatures;
	private List<T3dSymbolInstance> mSymbols;
	private List<GmMeasurementPath> mMeasurementPaths;

	private BufferedWriter mDoc;

	public KmlScene() {
		mPointGeometries = new ArrayList<VgPoint>();
		mPointFeatures = new ArrayList<VgAttrFeature>();
		mSymbols = new ArrayList<T3dSymbolInstance>();
		mMeasurementPaths = new ArrayList<GmMeasurementPath>();
	}

	private void wl(String pLine) {
		try {
			mDoc.write(pLine);
			mDoc.newLine();
		} catch (IOException e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
	 * adds a point of interest (POI) to the current scene. Here, these POIs are
	 * pure point geometries without any thematic attributes.
	 * 
	 * @param pPos
	 *            POI location
	 */
	public void add(VgPoint pPos) {
		checkCRS(pPos);

		mPointGeometries.add(pPos);
	}

	/**
	 * adds a point of interest (POI) to the current scene. Here, these POIs are
	 * point geometries and thematic attributes.
	 * 
	 * @param pPos
	 *            POI features
	 */
	public void add(VgAttrFeature pPOI) {
		if (!(pPOI.getGeometry() instanceof VgPoint)) {
			throw new T3dException("POIs must be defined by point geometries!");
		}

		checkCRS((VgPoint) pPOI.getGeometry());

		mPointFeatures.add(pPOI);
	}

	/**
	 * adds a cartographic symbol to the current scene.
	 * 
	 * @param pSymbol
	 */
	public void add(T3dSymbolInstance pSymbol) {
		// TODO
	}

	/**
	 * adds a measurement path to the current scene.
	 * 
	 * @param pMeasurementPath
	 *            Measurement path
	 */
	public void add(GmMeasurementPath pMeasurementPath) {
		// TODO auf WGS84 prüfen!!

		this.mMeasurementPaths.add(pMeasurementPath);
	}

	private void checkCRS(VgPoint pPos) {
		if (!VgGeomObject.SRSLatLonWgs84.equalsIgnoreCase(pPos.getSRS())) {
			throw new T3dSRSException(
					"KML objects must refer to WGS84 coordinate!");
		}
	}

	/**
	 * generates a KML document that contains the objects that have been added
	 * to the current scene.
	 * 
	 * @param pFilePath
	 *            File path, e.g. "/myfiles/example.kml"
	 */
	public void generateScene(String pFilePath) {

		int pointNumber = 0;
		try {
			mDoc = new BufferedWriter(new FileWriter(pFilePath));

			wl("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			wl("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
			wl("<Document>");

			
			if (!mPointFeatures.isEmpty()) {

				
				for (int i = 0; i < this.mPointFeatures.size(); i++) {

					Object geom = this.mPointFeatures.get(i).getGeometry();
					if (!(geom instanceof VgPoint)) {
						throw new T3dException("Point geometry expected ...");
					}
					VgPoint yourPoint = (VgPoint) geom;
					double x = yourPoint.getX();
					double y = yourPoint.getY();
					double z = yourPoint.getZ();
					wl("	<Placemark>");
					wl("		<name>" + pointNumber++ + "</name>");
					//wl("		<description> VgAttrFeature </description>");
					wl("		<Point>");
					wl("			<coordinates> " + x + " , " + y + " , " + z
							+ " </coordinates>");
					wl("		</Point>");

					
					
					wl("		<ExtendedData>");
					
					
					for( int j=0;j<this.mPointFeatures.get(i).getAttributeNames().length;j++){
						
						wl("		<Data name=\"" + this.mPointFeatures.get(i).getAttributeNames()[j] + "\">");
						wl("			<value>"+ this.mPointFeatures.get(i).getAttributeValue(this.mPointFeatures.get(i).getAttributeNames()[j]) + "</value>");

						wl("		</Data>");

					}
					wl("		</ExtendedData>");
					wl("	</Placemark>");

				}
			}

			
			if (!this.mMeasurementPaths.isEmpty()) {

		
				for (int i = 0; i < this.mMeasurementPaths.size(); i++) {
					wl("	<Placemark>");
					wl("		<name>" + pointNumber++ + "</name>");
					wl("		<Point>");

					wl("			<coordinates> "
							+ this.mMeasurementPaths.get(i).getLocation(0).getX()+ " , "
							+ this.mMeasurementPaths.get(i).getLocation(0).getY()+ " , "
							+ this.mMeasurementPaths.get(i).getLocation(0).getZ() 
							+							" </coordinates>");

					wl("		</Point>");

					if (this.mMeasurementPaths.get(i).getMeasurement(0).length >= 1) {
						wl("		<ExtendedData>");
						
						Date dateFromLong = new Date(this.mMeasurementPaths.get(0).getTimeStamp(0) * 10000);
						
						wl("		<Data name=\"TimeStamp\">");
						wl("			<value>" + dateFromLong + "</value>");
						wl("		</Data>");

						for (int j = 0; j < this.mMeasurementPaths.get(i).getMeasurement(0).length; j++) {
							wl("		<Data name=\"" + j + "\">");
							wl("			<value>"+ this.mMeasurementPaths.get(i).getMeasurement(0)[j] + "</value>");
							wl("		</Data>");
						}
						wl("		</ExtendedData>");
					}

					wl("	</Placemark>");

				}
			}

			
			if (!this.mPointGeometries.isEmpty()) {
				for (int i = 0; i < this.mPointGeometries.size(); i++) {
					wl("	<Placemark>");
					wl("		<name>" + pointNumber++ + "</name>");
					//wl("		<description> VgPoint </description>");
					wl("		<Point>");

					wl("			<coordinates> "
							+ this.mPointGeometries.get(i).getX() + " , "
							+ this.mPointGeometries.get(i).getY() + " , "
							+ this.mPointGeometries.get(i).getZ()
							+ " </coordinates>");

					wl("		</Point>");

					wl("	</Placemark>");

				}
			}
			wl("</Document>");
			wl("</kml>");
			mDoc.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
