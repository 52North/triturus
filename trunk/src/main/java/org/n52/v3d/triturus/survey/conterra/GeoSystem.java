package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jul 1, 2003
 * Time     :   3:27:47 PM
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
