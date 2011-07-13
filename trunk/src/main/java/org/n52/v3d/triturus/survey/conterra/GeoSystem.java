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
