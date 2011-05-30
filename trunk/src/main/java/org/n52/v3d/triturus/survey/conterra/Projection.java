package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   4:11:20 PM
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
public interface Projection {

    public void initEllipsoid(Ellipsoid ellipsoid);

    //konvertiert ellipsoidische Koordinaten in kartesische Ebenenkoordinaten
    double[] ellToCart(double x, double y, double[] out) throws GeographicTransformException;

    //konvertiert kartesische Ebenenkoordinaten in ellipsoidische Koordinaten
    double[] cartToEll(double x, double y, double[] out);

    String getName();
}
