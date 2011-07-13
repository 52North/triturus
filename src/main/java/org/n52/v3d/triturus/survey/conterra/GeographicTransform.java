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
public class GeographicTransform {

    // static attributes...


    // public attributes


    // private attributes

    private GeoSystem sourceGcs;

    private GeoSystem targetGcs;

    // static methods


    // constructors

    public GeographicTransform(GeoSystem sourceGcs) {
        this(sourceGcs, GeoSystem.GEOSYSTEM_WGS84);
    }

    public GeographicTransform(GeoSystem sourceGcs, GeoSystem targetGcs) {
        setSourceGcs(sourceGcs);
        setTargetGcs(targetGcs);
    }

    // public methods

    public GeoSystem getSourceGcs() {
        return sourceGcs;
    }

    public void setSourceGcs(GeoSystem sourceGcs) {
        this.sourceGcs = sourceGcs;
    }

    public GeoSystem getTargetGcs() {
        return targetGcs;
    }

    public void setTargetGcs(GeoSystem targetGcs) {
        this.targetGcs = targetGcs;
    }

    public double[] forward(double x, double y, double z, double[] out) throws GeographicTransformException {
        //sp�ter auch src �berpr�fen, jetzt ist es immer WGS84
        //
//        data.xout = data.xin; //* DEG2RAD;	//nur bei ellipsoidischen RBZ
//        data.yout = data.yin; //* DEG2RAD;
//        data.zout = data.zin;
        out = GeoSysUtil.return3DCoord(x, y, z, out);

        if (sourceGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_GEOCENTRIC) {
            throw new GeographicTransformException("Invalid source GeoSystem.projectionType: PROJECTIONTYPE_GEOCENTRIC"); //????
        }

        if (sourceGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_CARTESIAN) {
            out = sourceGcs.getProjection().cartToEll(out[0], out[1], out); //???
        }
        else {	//RBZ ist Ellipsoidisch (z.B. WGS84)=> in Radiant umrechnen
            out[0] *= GeoSysUtil.DEG2RAD;
            out[1] *= GeoSysUtil.DEG2RAD;
        }

        //Datums�bergang
        //
        Datum sourceDatum = sourceGcs.getDatum();
        Datum targetDatum = targetGcs.getDatum();
        if (sourceDatum == null) {
            throw new GeographicTransformException("source datum is null"); //????
        }
        if (targetDatum == null) {
            throw new GeographicTransformException("target datum is null"); //????
        }
        if (!sourceDatum.equals(targetDatum)) {
//            out = sourceDatum.toWGS84(sourceGcs.getEllipsoid(), out[0], out[1], out[2], out);
            out = sourceDatum.toWGS84(sourceGcs.getEllipsoid(), out[0], out[1], z, out);
            out = targetDatum.fromWGS84(targetGcs.getEllipsoid(), out[0], out[1], out[2], out);
        }

        if (targetGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_GEOCENTRIC)
            throw new GeographicTransformException("Invalid target GeoSystem.projectionType: PROJECTIONTYPE_GEOCENTRIC"); //????

        if (targetGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_ELLIPSIODAL) {
            //in Dezimalgrad ausgeben
            out[0] *= GeoSysUtil.RAD2DEG;
            out[1] *= GeoSysUtil.RAD2DEG;
            return out;
        }

        return targetGcs.getProjection().ellToCart(out[0], out[1], out);
    }

    public double[] reverse(double x, double y, double z, double[] out) throws GeographicTransformException {
        //sp�ter auch target �berpr�fen, jetzt ist es immer WGS84
        //
//        data.xout = data.xin; //* DEG2RAD;	//nur bei ellipsoidischen RBZ
//        data.yout = data.yin; //* DEG2RAD;
//        data.zout = data.zin;
        out = GeoSysUtil.return3DCoord(x, y, z, out);

        if (targetGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_GEOCENTRIC) {
            throw new GeographicTransformException("Invalid target GeoSystem.projectionType: PROJECTIONTYPE_GEOCENTRIC"); //????
        }

        if (targetGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_CARTESIAN) {
            out = targetGcs.getProjection().cartToEll(out[0], out[1], out); //???
        }
        else {	//RBZ ist Ellipsoidisch (z.B. WGS84)=> in Radiant umrechnen
            out[0] *= GeoSysUtil.DEG2RAD;
            out[1] *= GeoSysUtil.DEG2RAD;
        }

        //Datums�bergang
        //
        Datum targetDatum = targetGcs.getDatum();
        Datum sourceDatum = sourceGcs.getDatum();
        if (targetDatum == null) {
            throw new GeographicTransformException("target datum is null"); //????
        }
        if (sourceDatum == null) {
            throw new GeographicTransformException("source datum is null"); //????
        }
        if (!targetDatum.equals(sourceDatum)) {
            out = targetDatum.toWGS84(targetGcs.getEllipsoid(), out[0], out[1], out[2], out);
            out = sourceDatum.fromWGS84(sourceGcs.getEllipsoid(), out[0], out[1], out[2], out);
        }

        if (sourceGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_GEOCENTRIC)
            throw new GeographicTransformException("Invalid source GeoSystem.projectionType: PROJECTIONTYPE_GEOCENTRIC"); //????

        if (sourceGcs.getProjectionType() == GeoSystem.PROJECTIONTYPE_ELLIPSIODAL) {
            //in Dezimalgrad ausgeben
            out[0] *= GeoSysUtil.RAD2DEG;
            out[1] *= GeoSysUtil.RAD2DEG;
            return out;
        }

        return sourceGcs.getProjection().ellToCart(out[0], out[1], out);
    }
}
