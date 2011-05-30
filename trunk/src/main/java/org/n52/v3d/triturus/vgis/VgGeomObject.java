package org.n52.v3d.triturus.vgis;

/**
 * Abstrakte Basisklasse f�r geometrische Objekte im 3D-Raum.<p>
 * Bem.: Geoobjekte sind durch <tt>VgFeature</tt>-Objekte modelliert. Die Geometrie ist ein Attribut eines Geoobjekts.
 * <p>
 * @see org.n52.v3d.triturus.vgis.VgFeature
 * @author Benno Schmidt<br>
 * (c) 2003-2005, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgGeomObject 
{
	private String mSRS = SRSNone;

	/**
     * SRS-Wert f�r nicht-gesetztes r�umliches Referenzsystem.<p>
     */
	public static final String SRSNone = "none";

    /**
     * SRS-Konstante f�r geografische Koordinaten, d. h. Angaben durch L�ngen- und Breitengrade bezogen auf den
     * WGS-84-Geoid, als r�umliches Referenzsystem (EPSG:4326). Dieses Referenzsystem soll innerhalb der GDI-NRW
     * weitgehend unterst�tzt werden (optional).<p>
     * Bem.: Innerhalb des Triturus-Rahmenwerks gilt f�r geografische Koordinaten die Konvention, dass die x-Koordinate
     * die geografische L�nge und die y-Koordinate die geografische Breite bezeichnet.<p>
     */
    public static final String SRSLatLonWgs84 = "EPSG:4326";

    /**
     * SRS-Konstante f�r geografische Koordinaten, d. h. Angaben durch L�ngen- und Breitengrade bezogen auf den
     * Bessel-Geoid, als r�umliches Referenzsystem. Dieses Referenzsystem muss innerhalb der GDI-NRW nicht
     * notwendigerweise unterst�tzt werden.<p>
     * Bem.: Innerhalb des Triturus-Rahmenwerks gilt f�r geografische Koordinaten die Konvention, dass die x-Koordinate
     * die geografische L�nge und die y-Koordinate die geografische Breite bezeichnet.<p>
     */
    public static final String SRSLatLonBessel = "latLonBessel";

    /**
     * @deprecated
     * @see org.n52.v3d.triturus.vgis.VgGeomObject.SRSLatLonWgs84
     */
    public static final String SRSLatLon = "EPSG:4326";

	/**
     * SRS-Konstante f�r 2. Meridianstreifen des Gau�-Kr�ger-Systems (Bessel-Ellipsoid). Dieses Referenzsystem ist
     * innerhalb der GDI-NRW durchg�ngig zu unterst�tzen (mandatorisch).<p>
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
	public static final String SRSGkk2 = "EPSG:31466";

	/**
     * SRS-Konstante f�r 3. Meridianstreifen des Gau�-Kr�ger-Systems (Bessel-Ellipsoid). Dieses Referenzsystem ist
     * innerhalb der GDI-NRW durchg�ngig zu unterst�tzen (mandatorisch).<p>
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
	public static final String SRSGkk3 = "EPSG:31467";

    /**
     * SRS-Konstante f�r 4. Meridianstreifen des Gau�-Kr�ger-Systems (Bessel-Ellipsoid).<p>
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
    public static final String SRSGkk4 = "EPSG:31468";

    /**
     * SRS-Konstante f�r UTM ETRS89 Zone 32 Nord (WGS-84-Geoid). Die Unterst�tzung dieses Referenzsystems wird
     * innerhalb der GDI-NRW mit Blick grenz�berschreitende Aktivit�ten empfohlen.<p>
     */
    public static final String SRSUtmZ32N = "EPSG:25832";

	/**
	 * setzt das r�umliche Bezugssystem, auf das sich die Geometrie-Koordinaten beziehen. Das Bezugssystem ist als
     * EPSG-Code anzugeben, z. B. "EPSG:31492".<p>
	 * @param pEPSGString EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
	 */
	public void setSRS(String pEPSGString) {
		mSRS = pEPSGString;
	}

	/** 
	 * liefert das r�umliche Bezugssystem als EPSG-Coorg.n52.v3d. Falls kein Bezugssystem gesetzt ist, wird der Wert
     * <tt>this.SRSNone</tt> zur�ckgegeben.<p>
	 * @return EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
     */
	public String getSRS() {
		return mSRS;
	}

    /**
     * liefert die Information, ob das r�umliche Bezugssystem, auf das sich die Geometrie-Koordinaten beziehen,
     * metrisch ist (d. h., ob sich x- und y-Werte auf Meter als L�ngeneinheit beziehen).<p>
     * @return <i>true</i>, falls gesetztes r�umliches Bezugssystem metrisch, sonst <i>false</i>
     */
    public boolean hasMetricSRS() {
        if (mSRS.equalsIgnoreCase(VgGeomObject.SRSNone) ||
            mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLon) ||
            mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonBessel) ||
            mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84))
        {
            return false;
        }
        return true;
    }

    /**
     * liefert die Information, ob die Geometrie-Koordinaten in geografischen Koordinaten angegeben sind (d. h. L�ngen-
     * und Breitengrade als x- und y-Werte).<p
     * @return <i>true</i>, falls gesetztes r�umliches Bezugssystem geografisch, sonst <i>false</i>
     */
    public boolean hasGeographicSRS() {
        if (mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLon) ||
            mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonBessel) ||
            mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84))
        {
            return true;
        }
        return false;
    }

	/**
	 * pr�ft die Kompatibilit�t des r�umlichen Bezugssystems zu einem Geometrie-Objekt. Falls eine Inkompatibilit�t
     * vorliegt, wird eine <tt>T3dSRSException</tt> geworfen.<p>
	 * Ist f�r das Objekt <tt>this</tt> zum Zeitpunkt des Methodenaufrufs noch kein r�umliches Bezugssystem gesetzt, so
     * wird dies entsprechend der Einstellung f�r <tt>pObj</tt> gesetzt.<p>
	 * Hinweis: Der Aufruf dieser Methode empfiehlt sich bei der Implentierung Geometrie-manipulierender Methoden in
     * den Realisierungen der abstrakten Klassen des Pakets <tt>org.n52.v3d.triturus.vgis</tt>.<p>
	 * @throws T3dSRSException
	 */
	protected void assertSRS(VgGeomObject pObj) throws T3dSRSException 
	{
		if (mSRS.equalsIgnoreCase(SRSNone)) {
			mSRS = pObj.getSRS();
			return;
		}
		
		if (!mSRS.equalsIgnoreCase(pObj.getSRS())) 
			throw new T3dSRSException("Incompatibility in spatial reference systems.");
	}

    /**
	 * �bersetzt (innerhalb des Triturus-Frameworks) un�bliche EPSG-Codes in praktisch �quivalente EPSG-Codes.<p>
	 * @return EPSG-Code in der Form "EPSG:<EPSG-Nr.>"
     */
    static public String mapDeprecatedSRSCodes(String pEPSGString) {
        if (pEPSGString.equalsIgnoreCase("EPSG:25832") || pEPSGString.equalsIgnoreCase("EPSG:32632"))
            return VgGeomObject.SRSUtmZ32N;
        if (pEPSGString.equalsIgnoreCase("EPSG:31466") || pEPSGString.equalsIgnoreCase("EPSG:31492"))
            return VgGeomObject.SRSGkk2;
        if (pEPSGString.equalsIgnoreCase("EPSG:31467") || pEPSGString.equalsIgnoreCase("EPSG:31493"))
            return VgGeomObject.SRSGkk3;
        if (pEPSGString.equalsIgnoreCase("EPSG:31468") || pEPSGString.equalsIgnoreCase("EPSG:31494"))
            return VgGeomObject.SRSGkk4;
        return pEPSGString;
    }

	/**
	 * liefert die Bounding-Box der Geometrie.<p>
	 * Bem.: F�r einige im Rahmenwerk modellierte Geometrien nimmt die Bounding-Box eine unendliche Ausdehnung an. In
     * diesem Fall gibt die Methode den Wert <i>null</i> zur�ck.<p>
	 */
	abstract public VgEnvelope envelope();

	/** 
	 * liefert das Objekt, das sich durch Projektion der Geometrie auf die xy-Ebene ergibt.<p>
	 * F�r Objekte mit einer r�umlichen Dimension bis 2 ist die Dimension dieses "Footprints" gleich der des Objekts,
     * f�r das die Methode aufgerufen wird. F�r Objekte mit dreidimensionalen Geometrien ergeben sich zweidimensionale
     * "Footprints".<p>
	 * Beispiel: Die Projektion eines im 3D-Raum liegenden Linienzugs (Klasse <tt>VgLineString</tt>) ergibt einen
     * planaren, in der xy-Ebene liegenden Linienzug. Die Projektion einer Kugel erg�be einen Kreis.<p>
	 * Bem.: F�r einige im Rahmenwerk modellierte Geometrien nimmt die Bounding-Box eine unendliche Ausdehnung an. In
     * diesem Fall gibt die Methode den Wert <i>null</i> zur�ck.<p>
	 * @return "Footprint"-Objekt (oder <i>null</i>)
  	 */
	abstract public VgGeomObject footprint();
	
	/** liefert Information zu dem Geometrie-Objekt in textueller Form. */
	abstract public String toString();
}
