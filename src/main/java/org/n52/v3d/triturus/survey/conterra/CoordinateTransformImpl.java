package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   19.08.2004
 * Time     :   16:21:12
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
public class CoordinateTransformImpl implements CoordinateTransform {

    // static attributes...


    // public attributes


    // private attributes

    private GeographicTransform geographicTransform;

    // static methods


    // constructors

    public CoordinateTransformImpl(GeographicTransform geographicTransform) {
        this.geographicTransform = geographicTransform;
    }

    // public methods

    public double[] transformCoord(double[] srcPt, double[] dstPt) throws GeographicTransformException {
        if (srcPt.length < 2) {
            throw new GeographicTransformException("invalif number of coordinates < 2");
        }
        double x = srcPt[0];
        double y = srcPt[1];
        double z =  0.0d;
        if (srcPt.length > 2) {
            srcPt[2] = z;
        }
        return geographicTransform.forward(x, y, z, dstPt);
    }

    public double[] transformCoords(double[] srcPt, double[] dstPt) throws GeographicTransformException {
        if (dstPt==null || dstPt.length<srcPt.length) dstPt=new double[srcPt.length];
        double[] out = new double[3];
        double x = 0d;
        double y = 0d;
        final double z = 0d;
        for (int i = 0; i < srcPt.length;) {
            x = srcPt[i];
            y = srcPt[i+1];
            out = geographicTransform.forward(x,y,z,out);
            dstPt[i++]=out[0];
            dstPt[i++]=out[1];
        }
        return dstPt;
    }

    public double[] transformCoords(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws GeographicTransformException {
        double[] out = new double[3];
        double x = 0d;
        double y = 0d;
        final double z = 0d;

        for (int i = srcOff,j=0; i < srcPts.length && j<numPts;++j) {
            x = srcPts[i++];
            y = srcPts[i++];
            out = geographicTransform.forward(x,y,z,out);
            dstPts[dstOff++]=out[0];
            dstPts[dstOff++]=out[1];
        }
        return dstPts;
    }

    public CoordinateTransform inverse() {
        GeographicTransform invGeoTrans = new GeographicTransform(geographicTransform.getTargetGcs(),
                geographicTransform.getSourceGcs());
        CoordinateTransformImpl inv = new CoordinateTransformImpl(invGeoTrans);
        return inv;
    }
}
