package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   2:41:48 PM
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
public class GeoSysUtil {

    // static attributes...

    public final static double DEG2RAD = Math.PI / 180.0d;

    public final static double RAD2DEG = 180.0d / Math.PI;

    public final static double PI2 = 2.0d * Math.PI;
    
    // public attributes


    // private attributes


    // static methods

    public final static double[] check3DCoord(double[] coord) {
        if ((coord == null) || (coord.length != 3)) {
            coord = new double[3];
        }
        return coord;
    }

    public final static double[] return3DCoord(double x, double y, double z, double[] out) {
        out = check3DCoord(out);
        out[0] = x;
        out[1] = y;
        out[2] = z;
        return out;
    }

    public final static double[] check2DCoord(double[] coord) {
        if ((coord == null) || (coord.length != 2)) {
            coord = new double[2];
        }
        return coord;
    }

    public final static double[] return2DCoord(double x, double y, double[] out) {
        out = check2DCoord(out);
        out[0] = x;
        out[1] = y;
        return out;
    }

    // constructors


    // public methods

}
