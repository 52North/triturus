package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 30, 2003
 * Time     :   11:02:14 AM
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
public class Ellipsoid {

    // static attributes...

    // constants for calculation of double[] en
    private final static double C00 = 1.0d;
    private final static double C02 = 0.25d;
    private final static double C04 = 0.046875d;
    private final static double C06 = 0.01953125d;
    private final static double C08 = 0.01068115234375d;
    private final static double C22 = 0.75d;
    private final static double C44 = 0.46875d;
    private final static double C46 = 0.01302083333333333333d;
    private final static double C48 = 0.00712076822916666666d;
    private final static double C66 = 0.36458333333333333333d;
    private final static double C68 = 0.00569661458333333333d;
    private final static double C88 = 0.3076171875d;
    private final static double EPS = 1E-11;
    private final static int MAX_ITER = 10;

    // public attributes


    // private attributes

    private double a; // semiMajorAxis

    private double b; // semiMinorAxis

    private double f; // inverseFlattening

    private String name;

    // cached constants for calculation

    private double powA;
    private double powB;

    private double e1; // 1. num. excentricity
    private double powE1; // power of 1. num. excentricity
    private double powE2; // power of 2. num. excentricity

    // Meridianbogenlänge
    private double f1;
    private double f2;
    private double f3;
    private double f4;
    private double f5;

    // geogr. Breite Lotfußpunkt
    private double gE0;
    private double gF2;
    private double gF4;
    private double gF6;

    private double[] en = new double[5]; // Meridianbogenabstände

    // static methods

    // constructors

    public Ellipsoid(double semiMajorAxis, double semiMinorAxis) {
        this(semiMajorAxis, semiMinorAxis, null);
    }

    public Ellipsoid(double semiMajorAxis, double semiMinorAxis, String name) {
        this.a = semiMajorAxis;
        this.b = semiMinorAxis;
        this.name = name;
        init();
    }

    // public methods

    //
    // Read accessor for name attribute
    // @return	the attribute value
    //
    public String getName() {
        return name;
    }

    //
    // Write accessor for name attribute
    // @param	value	the attribute value
    //
    public void setName(String value) {
        this.name = value;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getF() {
        return f;
    }


    public double getF1() {
        return f1;
    }

    public double getF2() {
        return f2;
    }

    public double getF3() {
        return f3;
    }

    public double getF4() {
        return f4;
    }

    public double getF5() {
        return f5;
    }

    public double getGE0() {
        return gE0;
    }

    public double getGF2() {
        return gF2;
    }

    public double getGF4() {
        return gF4;
    }

    public double getGF6() {
        return gF6;
    }

    public double getE1() {
        return e1;
    }

    public double getPowE1() {
        return powE1;
    }

    public double getPowE2() {
        return powE2;
    }

    public double pjMlfn(double phi, double sphi, double cphi) {
        cphi *= sphi;
        sphi *= sphi;
        return(en[0] * phi - cphi * (en[1] + sphi*(en[2] + sphi*(en[3] + sphi*en[4]))));
    }

    public double pjInvMlfn(double arg/*phi*/) {
        double es = powE1;
        double s, t, phi, k = 1./(1.-es);
        int i;

        phi = arg;
        for (i = MAX_ITER; i > 0 ; --i) { // i > 0 ??? /* rarely goes over 2 iterations */
            s = Math.sin(phi);
            t = 1. - es * s * s;
            phi -= t = (pjMlfn(phi, s, Math.cos(phi)) - arg) * (t * Math.sqrt(t)) * k;
            if (Math.abs(t) < EPS) // ??? if (fabs(t) < EPS)
                return phi;
        }
//	pj_errno = -17;
        return phi;
    }

    public double flattening() { // Abplattung
        return f;
    }

    public double getQuerkruemmungshalbmesser(double lat) {
        return powA / Math.sqrt(
            powA * Math.pow(Math.cos (lat), 2.0d)  + // ??? 2.0l
            powB * Math.pow (Math.sin (lat), 2.0d)); //Querkrümmungshalbmesser // ??? 2.0l
    }

    public double getPolkruemmungshalbmesser() {
        //Krümmungshalbmesser am Pol (lat = 90°):
        return powA / b;
    }

    //Krümmungshalbmesser der Hauptvertikalen (für Molodensky)
    public double getRN(double lat) {
        return a / Math.sqrt(1.0d/*l*/ - powE1 * Math.pow(Math.sin(lat), 2.0d/*l*/));
    }

    //Krümmungshalbmesser im Hauptmeridian (für Molodensky)
    public double getRM(double lat) {
        return a * (1.0d/*l*/ - powE1) / Math.pow(
            1.0d/*l*/ - (powE1 * Math.pow(Math.sin(lat), 2.0d/*l*/)), 1.5d/*l*/);
    }

    //konvertiert ellipsoidische Koordinaten in geozentrische Koordinaten
    //LON, LAT werden in Radiant angegeben, H in Metern
    //x, y, z in Metern
    public double[] ellToGeocentric(double lon, double lat, double H, double[] out) {
        double N = getQuerkruemmungshalbmesser(lat);

        return GeoSysUtil.return3DCoord(
                (N + H) * Math.cos(lat) * Math.cos(lon),
                (N + H) * Math.cos(lat) * Math.sin(lon),
                (N * (1.0d/*l*/ - powE1) + H) * (Math.sin(lat)), //(1-e1_2) = b2/a2
                out);
    }

    //konvertiert geozentrische Koordinaten in ellipsoidische Koordinaten
    //x, y, z in Metern
    //lon, lat werden in Radiant ausgegeben, h in Metern
    public double[] geocentricToEll(double x, double y, double z, double[] out) throws GeographicTransformException {
        if (a* b * x * y == 0.0)
            throw new GeographicTransformException("atan2(*, 0) not defined");	//Fehler abfangen: atan2(*, 0) nicht definiert

        //Winkel zwischen Punkt-Nullpunkt-Gerade und x-y-Ebene berechnen
        double pp = Math.sqrt (x * x + y * y);
        double phi = Math.atan2 (z * a, pp * b);

        double var1 = powE2 * b * Math.pow(Math.sin (phi), 3.0);
        double var2 = powE1 * a * Math.pow(Math.cos (phi), 3.0);

        if (pp - var2 == 0.0)
            throw new GeographicTransformException("atan2(*, 0) not defined");	//Fehler abfangen: atan2(*, 0) nicht definiert

        double lat = Math.atan2 ((z + var1), (pp - var2));
        double lon = Math.atan2 (y, x);

        if (lon < 0.0)    //Winkel positiv machen (0 - 2pi)
            lon += GeoSysUtil.PI2;

        //Höhe berechnen:
        //
        var1 = Math.atan2 (b * Math.tan(lat), a);
        var2 = pp - a * Math.cos (var1);
        double h = Math.sqrt (Math.pow (var2, 2.0) +
                     Math.pow (z - b * Math.sin (var1), 2.0));
        if (var2 < 0.0)
            h = - h;

        //alternative Funktion für die Höhe (Transformation of GPS Results)
        //double h = pp / cos(lat) - Querkruemmungshalbmesser(lat);

        return GeoSysUtil.return3DCoord(lon, lat, h, out);
    }

    private void init() {
        powA = a * a;
        powB = b * b;
        f = (a - b) / a;

        powE1 = (powA - powB) / powA;
        powE2 = (powA - powB) / powB;
        e1 = Math.sqrt(powE1);

        //Konstanten für Meridianbogenlänge
        // Formeln nach Hofmann-Wellenhof GSP in der Praxis S. 93
        //

        double n3 	= (a-b)/(a+b);

        f1 = ((a + b)/2.0) * ( 1.0 + Math.pow(n3,2.0)/4.0 + Math.pow(n3,4.0)/64.0);
        f2 =	- 3.0/2.0 * n3 + 9.0/16.0 * Math.pow(n3,3.0) - 3.0/32.0 * Math.pow(n3,5.0);
        f3 =  15.0/16.0 * Math.pow(n3,2.0) - 15.0/32.0 * Math.pow(n3,4.0);
        f4 =  - 35.0/48.0 * Math.pow(n3,3.0) + 105.0/256.0 * Math.pow(n3,5.0);
        f5 =  315.0/512.0 * Math.pow(n3,4.0);

        //Konstanten zur Berechnung der geographischen Breite des
        //Lotfußpunktes

        double e2_4 = powE2 * powE2;
        double e2_6 = e2_4 * powE2;
        double e2_8 = e2_6 * powE2;

        gE0 = (1.0) - (powE2 * 3.0/4.0) + (e2_4 * 45.0/64.0) - (e2_6 * 175.0/256)
            + (e2_8 * 11025.0/16384);

        gF2 = (powE2 * 3.0/8.0) - (e2_4 * 3.0/16.0) + (e2_6 * 213.0/2048.0)
            - (e2_8 * 255.0/4096.0);

        gF4 = (e2_4 * 21.0/256.0) 	- (e2_6 * 21.0/256.0) + (e2_8 * 533.0/8192.0);
        gF6 = (e2_6 * 151.0/6144.0) - (e2_8 * 453.0/12288.0);

        //Hilfsvariablen für proj
        double t;
        double es = powE1;
        en[0] = C00 - es * (C02 + es * (C04 + es * (C06 + es * C08)));
        en[1] = es * (C22 - es * (C04 + es * (C06 + es * C08)));
        en[2] = (t = es * es) * (C44 - es * (C46 + es * C48));
        en[3] = (t *= es) * (C66 - es * C68);
        en[4] = t * es * C88;
    }
}
