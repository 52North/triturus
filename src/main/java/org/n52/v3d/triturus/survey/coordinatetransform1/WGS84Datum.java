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
public class WGS84Datum implements Datum {

    // static attributes

    public final static String WGS84_DATUM_NAME = "WGS 84 (Nullpunkt)";

    public final static Datum WGS84_DATUM = new WGS84Datum();

    // public attributes

    // private attributes


    // static methods


    // constructors


    // public methods
    /**
     * Geozentrisch => Ellipsoidisch
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     */
    public double[] fromWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException {
        // nothing to do
//        return GeoSysUtil.return3DCoord(x, y, z, out);
        return ellipsoid.geocentricToEll(x, y, z, out);
    }

    /**
     * Ellipsoidisch => Geozentrisch bezogen auf WGS84-Ellipsoid
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     */
    public double[] toWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) {
        // nothing to do
        //return GeoSysUtil.return3DCoord(x, y, z, out);
        return ellipsoid.ellToGeocentric(x, y, z, out);
    }

    public String getName() {
        return WGS84_DATUM_NAME;
    }
}
