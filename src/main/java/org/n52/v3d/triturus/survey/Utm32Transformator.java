package org.n52.v3d.triturus.survey;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.survey.conterra.CoordinateTransformFactory;
import org.n52.v3d.triturus.survey.conterra.CoordinateTransform;
import org.n52.v3d.triturus.survey.conterra.GeographicTransformException;

import java.lang.Double;

/**
 * Klasse zur Transformation von Punkten in das/aus dem UTM32-System.<p>
 * Geografische Koordinaten sind stets dezimal in Altgrad anzugeben. Rechts- und Hochwerte sind in m anzugeben (d. h.,
 * unter Verwendung von 7 Vorkommastellen).<p>
 * @author Benno Schmidt<br>
 * (c) 2005 con terra GmbH<br>
 */
public class Utm32Transformator
{
	private double mRechts = 0.;
	private double mHoch = 0.;
	private double mLaenge = 0.;
	private double mBreite = 0.;

	/**
	 * transformiert eine eine auf den WGS-84-Ellipsoid bezogene geografische Koordinate in das UTM32-System.<p>
	 * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).<p>
	 * @see VgPoint
	 * @param pIn zu transformierender Punkt in geografischen Koordinaten (EPSG:4326)
	 * @param pOut Punkt mit berechneten Koordinaten im UTM32-System
	 * @throws T3dSRSException
	 * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
	 */
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
     * transformiert eine auf den WGS-84-Ellipsoid bezogene geografische Koordinate in das UTM32-System.<p>
     * @param pLat geografische Breite des zu transformierenden Punktes
     * @param pLon geografische Länge des zu transformierenden Punktes
     * @param pRechts berechneter Rechtswert (UTM32-System)
     * @param pHoch berechneter Hochwert (UTM32-System)
     */
    public void latLon2Gkk(double pLat, double pLon, Double pRechts, Double pHoch)
    {
        this.geoWgs84InUtm(pLat, pLon);

        pRechts = new Double(mRechts);
        pHoch = new Double(mHoch);
    }

    /**
     * transformiert eine UTM32-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.<p>
     * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).<p>
     * @param pIn zu transformierender Punkt im UTM32-System
     * @param pOut Punkt mit berechneten geografischen Koordinaten
     * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
     */
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
     * transformiert eine UTM32-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.<p>
     * @param pRechts zu transformierender Rechtswert (UTM32-System)
     * @param pHoch zu transformierender Hochwert (UTM32-System)
     * @param pLat berechnete geografische Länge
     * @param pLon berechnete geografische Breite
     */
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
