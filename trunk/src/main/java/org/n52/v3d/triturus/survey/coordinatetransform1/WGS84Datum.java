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
