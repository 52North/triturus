package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   3:52:33 PM
 * Copyright:   Copyright (c) con terra GmbH
 * @link    :   www.conterra.de
 * @version :   0.1
 *
 * Revision :
 * @author  :   spanier
 * Date     :
 *
 */


//import

/**
 */
public class WGS84Datum implements Datum {

    // static attributes

    public final static String WGS84_DATUM_NAME = "WGS 84 (Nullpunkt)";

    public final static Datum WGS84_DATUM = new WGS84Datum();

    // public attributes

    // private attributes


    // static methods


    // constructors


    // public methods
    /**
     * Geozentrisch => Ellipsoidisch
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     */
    public double[] fromWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException {
        // nothing to do
//        return GeoSysUtil.return3DCoord(x, y, z, out);
        return ellipsoid.geocentricToEll(x, y, z, out);
    }

    /**
     * Ellipsoidisch => Geozentrisch bezogen auf WGS84-Ellipsoid
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     */
    public double[] toWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) {
        // nothing to do
        //return GeoSysUtil.return3DCoord(x, y, z, out);
        return ellipsoid.ellToGeocentric(x, y, z, out);
    }

    public String getName() {
        return WGS84_DATUM_NAME;
    }
}
