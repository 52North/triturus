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
package org.n52.v3d.triturus.survey.conterra;

/**
 * @author Udo Einspanier
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
