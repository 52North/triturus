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
package org.n52.v3d.triturus.vgis;

/**
 * Abstract base class for geometric objects in 3D space.<p>
 * Note: Geo-objects (called &quot;features&quot; in ISO/OGC jargon) will be modelled by <tt>VgFeature</tt> objects.
 * Here, geo-objects consist of one or more <i>attributes</i>. The object's geometry is one of those attributes.<p>
 * <i>German:</i> Bem.: Geoobjekte sind durch <tt>VgFeature</tt>-Objekte modelliert. Die Geometrie ist ein Attribut
 * eines Geoobjekts.
 * @see org.n52.v3d.triturus.vgis.VgFeature
 * @author Benno Schmidt
 */
abstract public class VgGeomObject 
{
	private String mSRS = SRSNone;

	/**
     * CRS-value, if no coordinate reference system is assigned to the object.
     */
	public static final String SRSNone = "none";

    /**
     * CRS-constant for geographic coordinates, i.e. decimal longitude/latitude values referring to the WGS84-geoid
     * as coordinate refernce system (<tt>EPSG:4326</tt>).<br />
     * Note: In the scope of the Triturus framework, for geographic coordinates usually the rule holds that
     * x-coordinates refer to geographic longitude whereas y-coordinates refer to geographic latitude values.
     * <br /><br />
     * <i>German:</i> Bem.: Innerhalb des Triturus-Rahmenwerks gilt f&uunmLr geografische Koordinaten die Konvention,
     * dass die x-Koordinate die geografische L&auml;nge und die y-Koordinate die geografische Breite bezeichnet.
     */
    public static final String SRSLatLonWgs84 = "EPSG:4326";

    /**
     * @deprecated
     * CRS-constant for geographic coordinates, i.e. decimal longitude/latitude values referring to the Bessel-geoid
     * as coordinate reference system.
     */
    public static final String SRSLatLonBessel = "latLonBessel";

    /**
     * @deprecated
     * @see this#SRSLatLonWgs84
     */
    public static final String SRSLatLon = "EPSG:4326";

	/**
     * CRS-constant for Gauss-Kruger zone 2 system.<br /><br />
     * <i>German:</i> SRS-Konstante f&uml;r 2. Meridianstreifen des Gau&szlig;-Kr&uuml;ger-Systems (Bessel-Ellipsoid).
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
	public static final String SRSGkk2 = "EPSG:31466";

    /**
     * CRS-constant for Gauss-Kruger zone 3 system.<br /><br />
     * <i>German:</i> SRS-Konstante f&uml;r 3. Meridianstreifen des Gau&szlig;-Kr&uuml;ger-Systems (Bessel-Ellipsoid).
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
	public static final String SRSGkk3 = "EPSG:31467";

    /**
     * CRS-constant for Gauss-Kruger zone 4 system.<br /><br />
     * <i>German:</i> SRS-Konstante f&uml;r 4. Meridianstreifen des Gau&szlig;-Kr&uuml;ger-Systems (Bessel-Ellipsoid).
     * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
     */
    public static final String SRSGkk4 = "EPSG:31468";

    /**
     * CRS-constant for UTM ETRS89 Zone 32 North (WGS84-geoid).
     */
    public static final String SRSUtmZ32N = "EPSG:25832";

	/**
	 * assigns the given coordinate reference system the coordinates of the geometric object refer to. The reference
     * system shall be given EPSG-code, e.g. <tt></tt>&quot;EPSG:31492&quot;</tt>.
	 * @param pEPSGString EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt> or <tt>SRSNone</tt>
	 */
	public void setSRS(String pEPSGString) {
		mSRS = pEPSGString;
	}

	/** 
	 * returns the coordinate reference sytem (CRS) as EPSG-code. If not kein CRS is set, the mothod will return
     * <tt>SRSNone</tt> as result.
	 * @return EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt> or <tt>SRSNone</tt>
     */
	public String getSRS() {
		return mSRS;
	}

    /**
     * returns the information whether the coordinate reference system that has been assigned to the geometric object
     * is metric (i.e., if x- and y-values refer to meters as distance measure).
     * @return <i>true</i> for metric coordinate reference systems, else <i>false</i>
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
     * returns the information whether the coordinate reference system that has been assigned to the geometric object
     * is given in geographic coordinates (i.e., longitute/latitide values).
     * @return <i>true</i> for geographic coordinates, else <i>false</i>
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
	 * checks the compatibility of the coordinate reference systems referring to the current geometric object and
     * another given geometric object. If the coordinate reference systems are incompatible, an <tt>T3dSRSException</tt>
     * will be thrown.<br />
	 * If no coordinate reference system has been assigned to the current object <tt>this</tt>, this will be done
     * according to the CRS-setting of the object <tt>pObj</tt>.<br /><br />
	 * <i>German:</i> Der Aufruf dieser Methode empfiehlt sich bei der Implementierung Geometrie-manipulierender
     * Methoden in den Realisierungen der abstrakten Klassen des Pakets <tt>org.n52.v3d.triturus.vgis</tt>.
     * @param pObj
	 * @throws T3dSRSException in the case of incompatible coordinate reference systems
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
     * translates deprecated EPSG-codes into equivalent codes.<br /><br />
	 * <i>German:</i> &uuml;bersetzt (innerhalb des Triturus-Frameworks) un&uuml;bliche EPSG-Codes in praktisch
     * &auml;quivalente EPSG-Codes.
     * @param pEPSGString EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt>
	 * @return EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt>
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
	 * returns the geometric object's bounding-box.<br />
     * Note: For some geometric objects the bounding-box might have infinite extent. In this case the method will return
     * <i>null</i>.<br /><br />
	 * <i>German:</i> Bem.: F&uuml;r einige im Rahmenwerk modellierte Geometrien nimmt die Bounding-Box eine unendliche
     * Ausdehnung an. In diesem Fall gibt die Methode den Wert <i>null</i> zur&uuml;ck.
     * @return Bounding-box, or <i>null</i>
	 */
	abstract public VgEnvelope envelope();

	/**
     * returns the object's footprint geometry (projection to the x-y-plane).<br /><br />
	 * <i>German:</i> liefert das Objekt, das sich durch Projektion der Geometrie auf die xy-Ebene ergibt.<p>
	 * F&uuml;r Objekte mit einer r&auml;umlichen Dimension bis 2 ist die Dimension dieses &quot;Footprints&quot; gleich
     * der des Objekts, f&uuml;r das die Methode aufgerufen wird. F&uuml;r Objekte mit dreidimensionalen Geometrien
     * ergeben sich zweidimensionale &quot;Footprints&quot;.<br />
	 * Beispiel: Die Projektion eines im 3D-Raum liegenden Linienzugs (Klasse <tt>VgLineString</tt>) ergibt einen
     * planaren, in der xy-Ebene liegenden Linienzug. Die Projektion einer Kugel erg&auml;be einen Kreis.<p>
	 * Bem.: F&uuml;r einige im Rahmenwerk modellierte Geometrien nimmt die Bounding-Box eine unendliche Ausdehnung an.
     * In diesem Fall gibt die Methode den Wert <i>null</i> zur&uuml;ck.
	 * @return Footprint object (or <i>null</i>)
  	 */
	abstract public VgGeomObject footprint();
	
	/**
     * returns human-readable information about the geometric object.
     */
	abstract public String toString();
}
