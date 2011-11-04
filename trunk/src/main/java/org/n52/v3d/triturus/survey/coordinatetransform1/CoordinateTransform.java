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
package org.n52.v3d.triturus.survey.coordinatetransform1;

/**
 * Transforms coordinates from one Coordinate Reference System to another.
 * @author Udo Einspanier
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
