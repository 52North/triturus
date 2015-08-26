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
 * @author Udo Einspanier
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
