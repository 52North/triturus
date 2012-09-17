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
