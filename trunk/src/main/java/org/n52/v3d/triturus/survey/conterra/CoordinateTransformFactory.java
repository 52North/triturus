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

import java.util.Map;
import java.util.TreeMap;

/**
 * Creates CoordinateTransforms based on identifiers like EPSG codes.
 * @author Udo Einspanier
 */
public class CoordinateTransformFactory {
    /*
    * Datum
    */
    public static Datum datumNrw = new HelmertDiffDatum(
            /*Translations Parameter DX*/ -567.7820,
            /*Translations Parameter DY*/ -106.9170,
            /*Translations Parameter DZ*/ -389.3200,
            /*Rotations Parameter ex*/     0.000004195141264009,
            /*Rotations Parameter ey*/     0.0000008771733932315,
            /*Rotations Parameter ez*/    -0.00001735458445447,
            /*Ma�stabsfaktor m*/          -0.000012687,
            "Potsdam (NRW)");

    public static Datum datumEd1950 = new HelmertDiffDatum(
            /*Translations Parameter DX*/ -102,
            /*Translations Parameter DY*/ -102,
            /*Translations Parameter DZ*/ -129,
            /*Rotations Parameter ex*/     2.002280502982e-6,
            /*Rotations Parameter ey*/     -8.920571732415e-7,
            /*Rotations Parameter ez*/     -1.866532672271e-6,
            /*Ma�stabsfaktor m*/          0.000002,
            "Europ�isches Datum 1950");


    /*
    * Ellipsoids
    */
    public static Ellipsoid besselBrd = new Ellipsoid(6377397.155, 6356078.9628, "Bessel");

    public static Ellipsoid hayford = new Ellipsoid(6378388.0, 6356911.94613, "Hayford (INT 1924)");


    /*
     * projections
     */
    public static Projection projUtm32 = new UTM(32);

    public static Projection transverseMercator = new TransverseMercator(
            /*centralmeridian*/ 0.0,
            /*nullat*/ 0.0,
            /*factor*/ 1.0,
            /*northing*/ 0.0,
            /*easting*/ 0.0);


    /*
    * Projections
    */
    public static Projection projectionGk1Nrw = new GaussKrueger(1, 3, "Gau�-Kr�ger 1-er Streifen");

    public static Projection projectionGk2Nrw = new GaussKrueger(2, 3, "Gau�-Kr�ger 2-er Streifen");

    public static Projection projectionGk3Nrw = new GaussKrueger(3, 3, "Gau�-Kr�ger 3-er Streifen");

    public static Projection projectionGk4Nrw = new GaussKrueger(4, 3, "Gau�-Kr�ger 4-er Streifen");

    public static Projection projectionGk5Nrw = new GaussKrueger(5, 3, "Gau�-Kr�ger 5-er Streifen");


    /*
    * GCS
    */
    public static GeoSystem geoSystemUtm32 = new GeoSystem(hayford, datumEd1950, projUtm32,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);

    public static GeoSystem geoSystemGk1Nrw = new GeoSystem(besselBrd, datumNrw, projectionGk1Nrw,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);

    public static GeoSystem geoSystemGk2Nrw = new GeoSystem(besselBrd, datumNrw, projectionGk2Nrw,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);

    public static GeoSystem geoSystemGk3Nrw = new GeoSystem(besselBrd, datumNrw, projectionGk3Nrw,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);

    public static GeoSystem geoSystemGk4Nrw = new GeoSystem(besselBrd, datumNrw, projectionGk4Nrw,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);

    public static GeoSystem geoSystemGk5Nrw = new GeoSystem(besselBrd, datumNrw, projectionGk5Nrw,
            GeoSystem.PROJECTIONTYPE_CARTESIAN);


    private GeoSystem[] knownSystems;
    private int[] epsgCodes = {CsCodes.EPSG_GCS_WGS84,
                               CsCodes.EPSG_PCS_UTM32,
                               CsCodes.EPSG_PCS_DHDN_GK1,
                               CsCodes.EPSG_PCS_DHDN_GK2,
                               CsCodes.EPSG_PCS_DHDN_GK3,
                               CsCodes.EPSG_PCS_DHDN_GK4,
                               CsCodes.EPSG_PCS_DHDN_GK5};

    private static CoordinateTransformFactory instance = new CoordinateTransformFactory();

    /**
     * gets the default factory
     * @return default factory
     */
    public static CoordinateTransformFactory getDefault() {
        return instance;
    }

    private Map transformations = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    private CoordinateTransformFactory() {
        init();
    }

    /**
     * Creates a CoordinateTransform based on source and destination identifier. Identifiers may have the form
     * <authority>:<code>, e.g. epsg:31465.
     * @param srcName source cs identifier
     * @param dstName destination cs identifier
     * @return a coordinate transformation based on identifiers
     * @throws GeographicTransformException
     */
    public CoordinateTransform createCoordinateTransform(String srcName, String dstName)
            throws GeographicTransformException {
        String key = createKey(srcName, dstName);
        return (CoordinateTransform) transformations.get(key);
    }

    /**
     * Creates a CoordinateTransform based on source and destination EPSG codes, i.e. <authority>="epsg".
     *
     * @param srcEpsgCode EPSG code of source cs
     * @param dstEpsgCode EPSG code of destination cs
     * @return a coordinate transformation based on codes
     * @throws GeographicTransformException
     */
    public CoordinateTransform createCoordinateTransform(int srcEpsgCode, int dstEpsgCode)
            throws GeographicTransformException {
        return createCoordinateTransform(CsCodes.createId(srcEpsgCode), CsCodes.createId(dstEpsgCode));
    }

    private void init() {
        knownSystems = new GeoSystem[] {GeoSystem.GEOSYSTEM_WGS84,
                                        geoSystemUtm32,
                                        geoSystemGk1Nrw,
                                        geoSystemGk2Nrw,
                                        geoSystemGk3Nrw,
                                        geoSystemGk4Nrw,
                                        geoSystemGk5Nrw};

        for (int i = 0; i < knownSystems.length; i++) {
            for (int j = 0; j < knownSystems.length; j++) {
                if (i == j) {
                    continue;
                }
                GeographicTransform geoTransform = new GeographicTransform(knownSystems[i], knownSystems[j]);
                CoordinateTransform coordTransform = new CoordinateTransformImpl(geoTransform);
                String key = createKey(CsCodes.createId(epsgCodes[i]), CsCodes.createId(epsgCodes[j]));
                transformations.put(key, coordTransform);
            }
        }
    }

    private String createKey(String srcId, String dstId) {
        return srcId + "-" + dstId;
    }

}
