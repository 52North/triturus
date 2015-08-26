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
public class GeoSystem {

    // static attributes...

    public static final int PROJECTIONTYPE_GEOCENTRIC = 0;

    public static final int PROJECTIONTYPE_CARTESIAN = 1;

    public static final int PROJECTIONTYPE_ELLIPSIODAL = 2;

    public final static GeoSystem GEOSYSTEM_WGS84 = new GeoSystem(new Ellipsoid(6378137.0d, 6356752.3142d, "WGS 84"),
            WGS84Datum.WGS84_DATUM, null /*new Projection()*/, GeoSystem.PROJECTIONTYPE_ELLIPSIODAL);

    // public attributes


    // private attributes

    private Ellipsoid ellipsoid;

    private Datum datum;

    private Projection projection;

    private int projectionType;

    // static methods


    // constructors

    public GeoSystem() {
        this (null, null, null, PROJECTIONTYPE_ELLIPSIODAL);
    }

    public GeoSystem(Ellipsoid ellipsoid, Datum datum, Projection projection, int projectionType) {
        setEllipsoid(ellipsoid);
        setDatum(datum);
        setProjection(projection);
        this.projectionType = projectionType;
    }

    // public methods
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
        if (projection != null) {
            projection.initEllipsoid(ellipsoid);
        }
    }

    public Datum getDatum() {
        return datum;
    }

    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
        if (projection != null) {
            projection.initEllipsoid(ellipsoid);
        }
    }

    public int getProjectionType() {
        return projectionType;
    }

}
