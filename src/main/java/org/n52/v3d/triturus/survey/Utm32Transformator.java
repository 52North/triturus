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
package org.n52.v3d.triturus.survey;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.survey.coordinatetransform1.CoordinateTransformFactory;
import org.n52.v3d.triturus.survey.coordinatetransform1.CoordinateTransform;
import org.n52.v3d.triturus.survey.coordinatetransform1.GeographicTransformException;

import java.lang.Double;

/**
 * Transformation of positions given in UTM 32.<br /><br />
 * <i>German:</i> Klasse zur Transformation von Punkten in das/aus dem UTM32-System.<br />
 * Geografische Koordinaten sind stets dezimal in Altgrad anzugeben. Rechts- und Hochwerte sind in m anzugeben (d. h.,
 * unter Verwendung von 7 Vorkommastellen).
 * @author Benno Schmidt
 */
public class Utm32Transformator
{
	private double mRechts = 0.;
	private double mHoch = 0.;
	private double mLaenge = 0.;
	private double mBreite = 0.;

	/**
     * transforms coordinates referring to the WGS-84-ellipsoid to UTM32.<br /><br />
	 * <i>German:</i> transformiert auf den WGS-84-Ellipsoid bezogene geografische Koordinaten in das UTM32-System.
     * <br />
	 * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).
	 * @see VgPoint
	 * @param pIn zu transformierender Punkt in geografischen Koordinaten (EPSG:4326)
	 * @param pOut Punkt mit berechneten Koordinaten im UTM32-System
	 * @throws T3dSRSException
	 * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
	 */
    // todo engl. javadoc der Parameter
	public void latLon2Utm(VgPoint pIn, VgPoint pOut) throws T3dSRSException
	{
        if (! pIn.getSRS().equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84))
            throw new T3dSRSException("Tried to process illegal point coordinate.");

        this.geoWgs84InUtm(pIn.getY() /*lat*/, pIn.getX() /*lon*/);

		pOut.setX(mRechts);
		pOut.setY(mHoch);
		pOut.setZ(pIn.getZ());

		pOut.setSRS(VgGeomObject.SRSUtmZ32N);
	}

    /**
     * transforms coordinates referring to the WGS-84-ellipsoid to UTM32.<br /><br />
	 * <i>German:</i> transformiert eine auf den WGS-84-Ellipsoid bezogene geografische Koordinate in das UTM32-System.
     * <br />
     * @param pLat geografische Breite des zu transformierenden Punktes
     * @param pLon geografische L&auml;nge des zu transformierenden Punktes
     * @param pRechts berechneter Rechtswert (UTM32-System)
     * @param pHoch berechneter Hochwert (UTM32-System)
     */
    // todo engl. javadoc der Parameter
    public void latLon2Gkk(double pLat, double pLon, Double pRechts, Double pHoch)
    {
        this.geoWgs84InUtm(pLat, pLon);

        pRechts = new Double(mRechts);
        pHoch = new Double(mHoch);
    }

    /**
     * transforms UTM32 coordinates to geographic coordinates referring to the WGS-84-ellipsoid.<br /><br />
	 * <i>German:</i> transformiert eine UTM32-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.
     * <br />
     * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).
     * @param pIn zu transformierender Punkt im UTM32-System
     * @param pOut Punkt mit berechneten geografischen Koordinaten
     * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
     */
    // todo engl. javadoc der Parameter
    public void utm2LatLon(VgPoint pIn, VgPoint pOut)
    {
        String srs = pIn.getSRS();
        if (! srs.equalsIgnoreCase(VgGeomObject.SRSUtmZ32N))
            throw new T3dSRSException("Tried to process illegal point coordinate (" + srs + ").");

        this.utmInGeoWgs84(pIn.getX(), pIn.getY());

        pOut.setX(mLaenge);
        pOut.setY(mBreite);
        pOut.setZ(pIn.getZ());

        pOut.setSRS(VgGeomObject.SRSLatLonWgs84);
    }

    /**
     * transforms UTM32 coordinates to geographic coordinates referring to the WGS-84-ellipsoid.<br /><br />
	 * <i>German:</i> transformiert eine UTM32-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.
     * <br />
     * @param pRechts zu transformierender Rechtswert (UTM32-System)
     * @param pHoch zu transformierender Hochwert (UTM32-System)
     * @param pLat berechnete geografische L�nge
     * @param pLon berechnete geografische Breite
     */
     // todo engl. javadoc der Parameter
    public void utm2LatLon(double pRechts, double pHoch, Double pLat, Double pLon) throws T3dSRSException
    {
        this.utmInGeoWgs84(pRechts, pHoch);

        pLat = new Double(mBreite);
        pLon = new Double(mLaenge);
    }

	// private Helfer:

    private void geoWgs84InUtm(double breite, double laenge)
    {
        CoordinateTransformFactory f = CoordinateTransformFactory.getDefault();
        CoordinateTransform t = null;
        double[] src = new double[2];
        double[] dest = new double[2];
        src[0] = breite; src[1] = laenge;

        try {
            t = f.createCoordinateTransform(VgGeomObject.SRSLatLonWgs84, /*VgGeomObject.SRSUtmZ32N -> TODO! */ "EPSG:32632");
            dest = t.transformCoord(src, dest);
        }
        catch (GeographicTransformException e) {
            throw new T3dException("Coordinate transformation failed: " + e.getMessage());
        }

        mRechts = dest[0];
        if (mRechts >= 1.e7 /*32000000.*/)
            mRechts -= mRechts;
        mHoch = dest[1];
    }

    private void utmInGeoWgs84(double rechts, double hoch)
    {
        CoordinateTransformFactory f = CoordinateTransformFactory.getDefault();
        CoordinateTransform t = null;
        double[] src = new double[2];
        double[] dest = new double[2];
        src[0] = rechts;
        if (src[0] < 1.e6 /*32000000.*/)
            src[0] += 32000000.;
        src[1] = hoch;

        try {
            t = f.createCoordinateTransform(/*VgGeomObject.SRSUtmZ32N -> TODO! */ "EPSG:32632", VgGeomObject.SRSLatLonWgs84);
            dest = t.transformCoord(src, dest);
        }
        catch (GeographicTransformException e) {
            throw new T3dException("GeographicTransformException: " + e.getMessage());
        }

        mBreite = dest[1];
        mLaenge = dest[0];
    }
}
