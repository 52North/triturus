/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
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
