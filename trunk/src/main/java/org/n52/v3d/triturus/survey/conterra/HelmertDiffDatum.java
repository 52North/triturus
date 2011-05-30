package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   2:15:07 PM
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
public class HelmertDiffDatum implements Datum {

    // static attributes...


    // public attributes


    // private attributes

    protected String name;

    protected double dx, dy, dz, mx, my, mz, ex, ey, ez, m;  		//Parameter für Datumsübergang
    protected double ex2, ey2, ez2, det;

    public static boolean forward = true; //??????

    // static methods


    // constructors

    public HelmertDiffDatum(double dx, double dy, double dz,
                                 double ex, double ey, double ez, double m) {
        this(dx, dy, dz, ex, ey, ez, m, null);
    }

    public HelmertDiffDatum(double dx, double dy, double dz,
                                 double ex, double ey, double ez, double m,
                                 String name) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
        this.m = m;

        setName(name);

        init();
    }

    // public methods

    public boolean equals(Object o) {
        if (! (o instanceof HelmertDiffDatum)) {
            return false;
        }
        HelmertDiffDatum other = (HelmertDiffDatum)o;
        return dx == other.dx &&
                dy == other.dy &&
                dz == other.dz &&
                ex == other.ex &&
                ey == other.ey &&
                ez == other.ez &&
                m == other.m &&
                forward == other.forward;
    }

    /**
     * Input immer geozentrisch, Output immer ellipsoidisch bezogen auf ellipdoid-Paramaeter
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     * @throws GeographicTransformException
     */
    public double[] fromWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException {
        if (ellipsoid == null)
            throw new GeographicTransformException("Invalid ellipsoid: " + ellipsoid);
/*
        double wgsx = x;
        double wgsy = y;
        double wgsz = z;

        if (!TGeoSystem::pWGS84->Ell()->EllToGeozentr(wgsx, wgsy, wgsz))
            return out; //???
*/
//        out = GeoSystem.GEOSYSTEM_WGS84.getEllipsoid().ellToGeocentric(x, y, z, out); // war unnötig!!!!!
        out = GeoSysUtil.return3DCoord(x, y, z, out);
        if (forward)
            out = forward(out[0], out[1], out[2], out);
        else
            out = inverse(out[0], out[1], out[2], out);

//        if (!pEll->GeozentrToEll(wgsx, wgsy, wgsz))
//            return FALSE;
//        x = wgsx;
//        y = wgsy;
//        z = wgsz;
//        return TRUE;
        return ellipsoid.geocentricToEll(out[0], out[1], out[2], out);
    }

    /**
     * Input immer ellipsoidisch  bezogen auf ellipdoid-Paramaeter, Output immer geozentrisch bezogen auf WGS84
     * @param ellipsoid
     * @param x
     * @param y
     * @param z
     * @param out
     * @return
     * @throws GeographicTransformException
     */
    public double[] toWGS84(Ellipsoid ellipsoid, double x, double y, double z, double[] out) throws GeographicTransformException {
        if (ellipsoid == null)
            throw new GeographicTransformException("Invalid ellipsoid: " + ellipsoid);

        out = ellipsoid.ellToGeocentric(x, y, z, out);
 /*       x = out[0];
        y = out[1];
        z = out[2];
*/
//        if (!pEll->EllToGeozentr(x, y, z))
//            return FALSE;

        if (forward)
            out = inverse(out[0], out[1], out[2], out); //Datumsangabe: WGS84->dest
        else
            out = forward(out[0], out[1], out[2], out);
/*
        if (!TGeoSystem::pWGS84->Ell()->GeozentrToEll(gx, gy, gz))
            return FALSE;
*/
        return out;
//        return GeoSystem.GEOSYSTEM_WGS84.getEllipsoid().geocentricToEll(out[0], out[1], out[2], out); // war unnötig!!!
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //initialisiert häufig benötigte Konstanten
    private void init() {
        //Quadrate der Rotationsparameter
        ex2 = ex * ex;
        ey2 = ey * ey;
        ez2 = ez * ez;

        //Determinante der Rotationsmatrix des Datumsuebergangs
        det = 1.0d + ex2 + ey2 + ez2;
    }

    protected double[] forward(double x, double y, double z, double[] out) {
        double wgsx = x - mx;
        double wgsy = y - my;
        double wgsz = z - mz;

        x =  dx + ((1.0d + m) * (			wgsx + ez * wgsy - ey * wgsz));
        y =  dy + ((1.0d + m) * (	-ez * wgsx + 		wgsy + ex * wgsz));
        z =  dz + ((1.0d + m) * (	ey *	wgsx - ex * wgsy + 		wgsz));

        x += mx;
        y += my;
        z += mz;

        return GeoSysUtil.return3DCoord(x, y, z, out);
    }

    protected double[] inverse(double x, double y, double z, double[] out) {
        double gx = x;
        double gy = y;
        double gz = z;

        gx = (gx - dx) / (1.0d + m);
        gy = (gy - dy) / (1.0d + m);
        gz = (gz - dz) / (1.0d + m);

        //inverse Rotationsmatrix (nach Cramerscher Regel berechnet)
        //
        x = gx * (1.0d + ex2) + gy * (ex*ey - ez) + gz * (ex*ez + ey);
        y = gx * (ex*ey + ez) + gy * (1.0d + ey2) + gz * (ey*ez - ex);
        z = gx * (ex*ez - ey) + gy * (ey*ez + ex) + gz * (1.0d + ez2);
        x /= det;
        y /= det;
        z /= det;

        return GeoSysUtil.return3DCoord(x, y, z, out);
    }
}
