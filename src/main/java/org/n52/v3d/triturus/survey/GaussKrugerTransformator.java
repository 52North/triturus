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
 * Klasse zur Transformation von Punkten in das/aus dem Gauß-Krüger-System.<p>
 * Geografische Koordinaten sind stets dezimal in Altgrad anzugeben. Rechts- und Hochwerte sind in m anzugeben (d. h.,
 * unter Verwendung von 7 Vorkommastellen).<p>
 * Die Umrechnungsvorschrift entspricht der des nordrhein-westfälischen Landesvermessungsamtes (d. h., die Ergebnisse
 * besitzen prinzipiell Gültigkeit für alle alten Bundesländer). Die Gauß-Krüger-Koordinaten sind auf den
 * Bessel-Ellipsoid bezogen. Die Reichweite der Formeln liegt bei 3 Längengraden.<p>
 * Die Berechnungsreihenfolgen wurden abgeändert, so dass die Routinen möglichst schnell abgearbeitet werden können.<p>
 * <b>Bem.: Die Transformationen für den WGS-84-Ellipsoid sind (im Gegensatz zum Bessel-Ellipoid) leider noch nicht
 * getestet.</b><p>
 * @author Benno Schmidt<br>
 * (c) 1993-1996, Geopro GmbH, 2004-2005 con terra GmbH<br>
 */
public class GaussKrugerTransformator
{
	private double mRechts = 0.;
	private double mHoch = 0.;
	private double mLaenge = 0.;
	private double mBreite = 0.;

	/**
	 * transformiert eine geografische Koordinate in das Gauß-Krüger-System.<p>
	 * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).<p>
	 * @see VgPoint
	 * @param pIn zu transformierender Punkt in geografischen Koordinaten (EPSG:4326)
	 * @param pMerid Nummer des Gauß-Krüger-Meridianstreifens (z. B. 2, 3 oder 4)
	 * @param pOut Punkt mit berechneten Koordinaten im Gauß-Krüger-System
	 * @throws T3dSRSException
	 * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
	 */
	public void latLon2Gkk(VgPoint pIn, int pMerid, VgPoint pOut) throws T3dSRSException
	{
        short lCase = 0;
        if (pIn.getSRS().equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84)) lCase = 1; // WGS-84
        if (pIn.getSRS().equalsIgnoreCase(VgGeomObject.SRSLatLonBessel)) lCase = 2; // Bessel

        switch (lCase) {
            case 1:
                this.geoWgs84InGkk(pIn.getY() /*lat*/, pIn.getX() /*lon*/, pMerid);
                break;
            case 2:
                this.geoBesselInGkk(pIn.getY() /*lat*/, pIn.getX() /*lon*/, pMerid);
                break;
            default:
                throw new T3dSRSException("Tried to process illegal point coordinate.");
        }

		pOut.setX(mRechts);
		pOut.setY(mHoch);
		pOut.setZ(pIn.getZ());

		pOut.setSRS("EPSG:3146" + (4 + pMerid)); // also z. B. "EPSG:31467" für Gauß-Krüger Streifen 3
	}

	/**
	 * transformiert eine auf den Bessel-Ellipsoid bezogene geografische Koordinate in das Gauß-Krüger-System.<p>
	 * @param pLat geografische Breite des zu transformierenden Punktes
	 * @param pLon geografische Länge des zu transformierenden Punktes
	 * @param pMerid Nummer des Gauß-Krüger-Meridianstreifens (z. B. 2, 3 oder 4)
	 * @param pRechts berechneter Rechtswert (Gauß-Krüger-System)
	 * @param pHoch berechneter Hochwert (Gauß-Krüger-System)
	 */
	public void latLonBessel2Gkk(double pLat, double pLon, int pMerid, Double pRechts, Double pHoch)
	{
		this.geoBesselInGkk(pLat, pLon, pMerid);

		pRechts = new Double(mRechts);
		pHoch = new Double(mHoch);
	}

	/**
	 * transformiert eine Gauß-Krüger-Koordinate in auf den Bessel-Ellipsoid bezogene geografische Koordinaten.<p>
	 * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).<p>
	 * @param pIn zu transformierender Punkt im Gauß-Krüger-System
	 * @param pOut Punkt mit berechneten geografischen Koordinaten
	 * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
	 */
	public void gkk2LatLonBessel(VgPoint pIn, VgPoint pOut)
	{
		String srs = pIn.getSRS();
		if ( !(
			srs.equalsIgnoreCase("EPSG:31492") ||
			srs.equalsIgnoreCase("EPSG:31493") ||
			srs.equalsIgnoreCase("EPSG:31494") ||
            srs.equalsIgnoreCase("EPSG:31495") ||
            srs.equalsIgnoreCase("EPSG:31496") ||
            srs.equalsIgnoreCase("EPSG:31466") ||
            srs.equalsIgnoreCase("EPSG:31467") ||
            srs.equalsIgnoreCase("EPSG:31468") ))
		{
			throw new T3dSRSException("Tried to process illegal point coordinate (" + srs + ").");
		}

		this.gkkInGeoBessel(pIn.getX(), pIn.getY());

        pOut.setX(mLaenge);
		pOut.setY(mBreite);
		pOut.setZ(pIn.getZ());

		pOut.setSRS(VgGeomObject.SRSLatLonBessel);
	}

	/**
     * transformiert eine Gauß-Krüger-Koordinate in auf den Bessel-Ellipsoid bezogene geografische Koordinaten.<p>
	 * @param pRechts zu transformierender Rechtswert (Gauß-Krüger-System)
	 * @param pHoch zu transformierender Hochwert (Gauß-Krüger-System)
	 * @param pLat berechnete geografische Länge
	 * @param pLon berechnete geografische Breite
	 */
	public void gkk2LatLonBessel(double pRechts, double pHoch, Double pLat, Double pLon) throws T3dSRSException
	{
		this.gkkInGeoBessel(pRechts, pHoch);

		pLat = new Double(mBreite);
		pLon = new Double(mLaenge);
	}

    /**
     * transformiert eine auf den WGS-84-Ellipsoid bezogene geografische Koordinate in das Gauß-Krüger-System.<p>
     * @param pLat geografische Breite des zu transformierenden Punktes
     * @param pLon geografische Länge des zu transformierenden Punktes
     * @param pMerid Nummer des Gauß-Krüger-Meridianstreifens (z. B. 2, 3 oder 4)
     * @param pRechts berechneter Rechtswert (Gauß-Krüger-System)
     * @param pHoch berechneter Hochwert (Gauß-Krüger-System)
     */
    public void latLon2Gkk(double pLat, double pLon, int pMerid, Double pRechts, Double pHoch)
    {
        this.geoWgs84InGkk(pLat, pLon, pMerid);

        pRechts = new Double(mRechts);
        pHoch = new Double(mHoch);
    }

    /**
     * transformiert eine Gauß-Krüger-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.<p>
     * Vor dem Methodenaufruf muss das Ergebnisobjekt bereits instanziiert sein (z. B. als <tt>GmPoint</tt>).<p>
     * @param pIn zu transformierender Punkt im Gauß-Krüger-System
     * @param pOut Punkt mit berechneten geografischen Koordinaten
     * @throws java.lang.NullPointerException falls <tt>pOut</tt> nicht intanziiert
     */
    public void gkk2LatLon(VgPoint pIn, VgPoint pOut)
    {
        String srs = pIn.getSRS();
        if ( !(
            srs.equalsIgnoreCase("EPSG:31492") ||
            srs.equalsIgnoreCase("EPSG:31493") ||
            srs.equalsIgnoreCase("EPSG:31494") ||
            srs.equalsIgnoreCase("EPSG:31495") ||
            srs.equalsIgnoreCase("EPSG:31496") ||
            srs.equalsIgnoreCase("EPSG:31466") ||
            srs.equalsIgnoreCase("EPSG:31467") ||
            srs.equalsIgnoreCase("EPSG:31468") ))
        {
            throw new T3dSRSException("Tried to process illegal point coordinate (" + srs + ").");
        }

        this.gkkInGeoWgs84(pIn.getX(), pIn.getY());

        pOut.setX(mLaenge);
        pOut.setY(mBreite);
        pOut.setZ(pIn.getZ());

        pOut.setSRS(VgGeomObject.SRSLatLonWgs84);
    }

    /**
     * transformiert eine Gauß-Krüger-Koordinate in auf den WGS-84-Ellipsoid bezogene geografische Koordinaten.<p>
     * @param pRechts zu transformierender Rechtswert (Gauß-Krüger-System)
     * @param pHoch zu transformierender Hochwert (Gauß-Krüger-System)
     * @param pLat berechnete geografische Länge
     * @param pLon berechnete geografische Breite
     */
    public void gkk2LatLon(double pRechts, double pHoch, Double pLat, Double pLon) throws T3dSRSException
    {
        this.gkkInGeoWgs84(pRechts, pHoch);
        
        pLat = new Double(mBreite);
        pLon = new Double(mLaenge);
    }

	// private Helfer:

	private void geoBesselInGkk(double breite, double laenge, int meridian)
	{
		double[] _cosB = new double[10];
		double[] _sinB = new double[10];
		double[] _l0 = new double[6];
		double k, l0;
		double r1, r2, r3, h0, h1, h2;
		int i;
		final double rho = 57.2957795131;

   		laenge /= rho;
   		breite /= rho;

   		for (i = 1; i < 10; i++) {
      		_cosB[i] = Math.cos( (double)i * breite );
      		_sinB[i] = Math.sin( (double)i * breite );
   		}

   		k = (double) meridian * 1.e6 + 500000.;

		l0 = rho * laenge - (double)(meridian * 3);
   		_l0[2] = l0 * l0;
   		for (i = 3; i < 6; i++)
      		_l0[i] = _l0[i - 1] * l0;

   		r1 =  111399.6739914 * _cosB[1]
        	-     93.2127903 * _cosB[3]
        	+      0.1170252 * _cosB[5]
        	-      0.0001633 * _cosB[7]
        	+      0.0000002 * _cosB[9];

   		r2 =       2.8492319 * _cosB[1]
        	+      2.8397234 * _cosB[3]
        	+      0.0000010 * _cosB[5]
        	+      0.0000010 * _cosB[7];

   		r3 =       0.0000878 * _cosB[1]
        	+      0.0002186 * _cosB[3]
        	+      0.0001316 * _cosB[5]
        	+      0.0000005 * _cosB[7];

   		h0 =      6366742.52 * breite
        	-  15988.6385316 * _sinB[2]
        	+     16.7299538 * _sinB[4]
        	-      0.0217848 * _sinB[6]
        	+      0.0000308 * _sinB[8];

   		h1 =     486.4794917 * _sinB[2]
        	-      0.4072281 * _sinB[4]
        	+      0.0005113 * _sinB[6]
        	-      0.0000007 * _sinB[8];

   		h2 =       0.0249166 * _sinB[2]
        	+      0.0186901 * _sinB[4]
        	+      0.0000312 * _sinB[6];

   		mRechts = r1 * l0 + r2 * _l0[3] + r3 * _l0[5] + k;
   		mHoch = h0 + h1 * _l0[2] + h2 * _l0[4];
   	}

	private void gkkInGeoBessel(double rechts, double hoch)
	{
		double[] _rechts = new double[6];
		// Konstanten des Bessel-Ellipsoids:
		final double c0 =  1.;
        final double c1 = -0.00831729565;
        final double c2 =  0.00424914906;
        final double c3 = -0.00113566119;
        final double c4 =  0.00022976983;
        final double c5 = -0.00004363980;
        final double c6 =  0.00000562025;
        final double eStrich2 = 0.006719218798;
        final double x90 = 10000855.7646; // Bogenlänge Meridianquadrant
        final double k2 = 325632.08677;
        final double c = 6398786.8481; // große Erdhalbachse
		double phi, phi2;
		double eta_f2, t_f, N_f, B_f;
		double cosB_f, cos2B_f;
		double[] _t_f = new double[5];
		double[] _N_f = new double[6];
	    final double rho = 57.2957795131; // 180./PI
		int i;

        int meridian = (int)(rechts / 1.e6);
   		rechts -= (double) meridian * 1.e6;
   		rechts -= 500000.0;

		phi = hoch / x90;

	    _rechts[2] = rechts * rechts;
   		for (i = 3; i < 6; i++)
      		_rechts[i] = _rechts[i - 1] * rechts;

   		phi2 = phi * phi;

   		// Berechnung von B_f nach der Formel von Mittermayer:
   		B_f = k2 * phi / 3600. * ((((((c6
   			* phi2 + c5)
            * phi2 + c4)
            * phi2 + c3)
            * phi2 + c2)
            * phi2 + c1)
            * phi2 + c0);

   		cosB_f = Math.cos(B_f/rho);
   		cos2B_f = cosB_f * cosB_f;
   		eta_f2 = eStrich2 * cos2B_f;
		t_f = Math.tan(B_f / rho);
   		N_f = c / Math.sqrt(1. + eStrich2 * cos2B_f);

	   	_t_f[2] = t_f * t_f;
   		_N_f[2] = N_f * N_f;

   		for (i = 3; i < 5; i++) {
      		_t_f[i] = _t_f[i - 1] * t_f;
      		_N_f[i] = _N_f[i - 1] * N_f;
   		}

   		_N_f[5] = _N_f[4] * N_f;

   		mBreite = B_f - rho * t_f * _rechts[2] * (1. + eta_f2) / (2. * _N_f[2])
			+ rho * t_f * _rechts[4]
			* (5. + 3.*_t_f[2] + 6.*eta_f2 - 6.*eta_f2*_t_f[2])
			/ (24. * _N_f[4]);

 		mLaenge = rho * rechts / (N_f*cosB_f) - rho * (1. + 2. * _t_f[2] + eta_f2)
 			* _rechts[3] / (6. * _N_f[3] * cosB_f)
            + rho * (1. + 28. * _t_f[2] + 24. * _t_f[4]) * _rechts[ 5 ]
            / (120. * _N_f[5] * cosB_f)
            + (double)(3 * meridian);
	}

    private void geoWgs84InGkk(double breite, double laenge, int meridian)
    {
        CoordinateTransformFactory f = CoordinateTransformFactory.getDefault();
        CoordinateTransform t = null;
        double[] src = new double[2];
        double[] dest = new double[2];
        src[0] = breite; src[1] = laenge;

        String srs = "";
        switch (meridian) {
            case 2: srs = VgGeomObject.SRSGkk2; break;
            case 3: srs = VgGeomObject.SRSGkk3; break;
            case 4: srs = VgGeomObject.SRSGkk4; break;
            default: throw new T3dException("Illegal GKK meridian (" + meridian + ").");
        }
        try {
            t = f.createCoordinateTransform(VgGeomObject.SRSLatLonWgs84, srs);
            dest = t.transformCoord(src, dest);
        }
        catch (GeographicTransformException e) {
            throw new T3dException("Coordinate transformation failed: " + e.getMessage());
        }

        mRechts = dest[0];
        mHoch = dest[1];
    }

    private void gkkInGeoWgs84(double rechts, double hoch)
    {
        CoordinateTransformFactory f = CoordinateTransformFactory.getDefault();
        CoordinateTransform t = null;
        double[] src = new double[2];
        double[] dest = new double[2];
        src[0] = rechts; src[1] = hoch;

        int meridian = (int)(rechts / 1.e6);
        String srs = "";
        switch (meridian) {
            case 2: srs = VgGeomObject.SRSGkk2; break;
            case 3: srs = VgGeomObject.SRSGkk3; break;
            case 4: srs = VgGeomObject.SRSGkk4; break;
            default: throw new T3dException("Illegal GKK meridian (" + meridian + ").");
        }
        try {
            t = f.createCoordinateTransform(srs, VgGeomObject.SRSLatLonWgs84);
            dest = t.transformCoord(src, dest);
        }
        catch (GeographicTransformException e) {
            throw new T3dException("GeographicTransformException: " + e.getMessage());
        }

        mBreite = dest[1];
        mLaenge = dest[0];
    }
}
