package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   19.08.2004
 * Time     :   15:28:23
 * Copyright:   Copyright (c) con terra GmbH
 * @link    :   www.conterra.de
 * @version :   0.1
 *
 * Revision :
 * @author  :   spanier
 * Date     :
 *
 */

// import ...

/**
 *  Transforms coordinates from one Coordinate Reference System to another.
 */
public interface CoordinateTransform {

    /**
     * Converts coordinates of a single point.
     *
     * @param srcPt source point with same dimensionality as src cs
     * @param dstPt destination coordinate array, null will create a new one
     * @return destination coordinate array
     * @throws GeographicTransformException
     */
    public double[] transformCoord(double[] srcPt, double[] dstPt) throws GeographicTransformException;

    /**
     * Converts coordinates of a multiple points. Coordinates are strored in the form {x1,y1,x2,y2,...}
     *
     * @param srcPt source point coordinates with same dimensionality as src cs
     * @param dstPt destination coordinate array, null will create a new one
     * @return destination coordinate array
     * @throws GeographicTransformException
     */
    public double[] transformCoords(double[] srcPt, double[] dstPt) throws GeographicTransformException;

    /**
     * Converts coordinates of a multiple points. Coordinates are strored in the form {x1,y1,x2,y2,...}
     *
     * @param srcPts source point coordinates with same dimensionality as src cs
     * @param srcOff point offset in source
     * @param dstPts estination coordinate array, null will create a new one
     * @param dstOff point offset in destination
     * @param numPts number of points to convert
     * @return destination coordinate array
     * @throws GeographicTransformException
     */
    public double[] transformCoords(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws GeographicTransformException;

    /**
     * Creates a transformation with interchanged source and destination cs
     * @return the inverse transformation
     */
    CoordinateTransform inverse();
}
