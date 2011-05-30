package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   2:01:14 PM
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
 *
 */
public interface Datum {
    // static attributes...

    public final static Datum WGS84_DATUM_SHIFT = new WGS84Datum();

    double[] fromWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException;
    double[] toWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException;
    String getName();
}
