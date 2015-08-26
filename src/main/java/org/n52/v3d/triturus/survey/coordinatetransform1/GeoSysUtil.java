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
