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
public class TransverseMercator implements Projection {
    // static attributes...


    // public attributes


    // private attributes


    // static methods

    protected double centralmeridian = 0.0d;	//Bezugsmeridian
    protected double nulllat = 0.0d;				//Bezugsbreite (in England nicht �quator)
    protected double factor = 0.0d;				//Ma�stabsfaktor (Schnittzylinder)
    protected	double northing = 0.0d;			//Nordverschiebung
    protected	double easting = 0.0d;       	//Ostverschiebung

    protected Ellipsoid ellipsoid;

    protected String name = "Transverse Mercator";

    // constructors

    protected TransverseMercator() {
    }

    public TransverseMercator(double centralmeridian, double nulllat,
                              double factor, double northing, double easting) {
        this.centralmeridian = centralmeridian;
        this.nulllat = nulllat;
        this.factor = factor;
        this.northing = northing;
        this.easting = easting;
    }

    public TransverseMercator(double centralmeridian, double nulllat,
                              double factor, double northing, double easting,
                              String name) {
        this(centralmeridian, nulllat, factor, northing, easting);
        setName(name);
    }

    // public methods

    public void initEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
        if (nulllat != 0.0d) {
            double lon = centralmeridian * GeoSysUtil.DEG2RAD;
            double lat = nulllat * GeoSysUtil.DEG2RAD;
            double[] out = null;
            try {
                out = ellToCart(lon, lat, out);
            } catch (GeographicTransformException e) {
                throw new IllegalArgumentException("Invalid ellipsoid: " + ellipsoid + "- " + e.getMessage());
            }
            northing -= (out[1] - northing);
        }
    }

    //konvertiert ellipsoidische Koordinaten in kartesische Ebenenkoordinaten
    public double[] ellToCart(double x, double y, double[] out) throws GeographicTransformException {
        if (ellipsoid == null) {
            throw new GeographicTransformException("Invalid ellipsoid: " + ellipsoid);
        }
        double lon = x;
        double lat = y;
        lon = lon - (centralmeridian * GeoSysUtil.DEG2RAD);

        double N2 = ellipsoid.getQuerkruemmungshalbmesser(lat);
        double t = Math.tan (lat);
        double t2 = Math.pow (t, 2.0d);
        double l1 = lon * Math.cos(lat);
        double ETA2 = ellipsoid.getPowE2() * Math.pow (Math.cos (lat),2.0d);
//        double ETA2 = pEll->Get_e2_2() * Math.pow (Math.cos (lat),2.0d);
        double ETA1 = Math.sqrt(ETA2);          //Wurzel aus ETA2

        //Rechtswert berechnen
        x = N2 * ((l1 + (Math.pow(l1,3.0d)/6.0d) * (1.0d - t2 + ETA2)
                        + ((Math.pow(l1,5.0d)/120.0d) * ((5.0d - 18.0d * t2) +
                                Math.pow(t,4.0d) + 14.0d * ETA2 - 58.0d * t2 * ETA2))
                        + (Math.pow(l1,7.0d)/5040.0d) *
                                (61.0d - 479.0d * t2 + 179.0d * Math.pow(t,4.0d) - Math.pow(t,6.0d))));

        //Hochwert berechnen
        double term1 = ellipsoid.getF1() *
                (lat +
                ellipsoid.getF2() * Math.sin(2.0d*lat) +
                ellipsoid.getF3() * Math.sin(4.0d*lat) +
                ellipsoid.getF4() * Math.sin(6.0d*lat) +
                ellipsoid.getF5() * Math.sin(8.0d*lat));
/*        double term1 = pEll->Get_f1() *
                            (	lat +
                                pEll->Get_f2() * Math.sin(2.0d*lat) +
                                pEll->Get_f3() * Math.sin(4.0d*lat) +
                                pEll->Get_f4() * Math.sin(6.0d*lat) +
                                pEll->Get_f5() * Math.sin(8.0d*lat));
*/

        double term2 = (N2*t) * ((Math.pow(l1,2.0d)/2.0d) +
            Math.pow(l1,4.0d)/24.0d * (5.0d - t2 + 9.0d*ETA2 + 4.0d*Math.pow(ETA1,4.0d)));

        double term3 = (N2*t) * ((Math.pow(l1,6.0d)/720.0d) *
            (61.0d - 58.0d*t2 + Math.pow(t,4.0d) + 270.0d*ETA2 - 330.0d*t2*ETA2));

        double term4 = (N2*t) * ((Math.pow(l1,8.0d)/40320.0d) *
            (1385.0d - 3111.0d*t2 + 543.0*Math.pow(t,4.0d) - Math.pow(t,6.0d)));

        //Hochwert
        y = (term1 + term2 + term3 + term4);

        return addOffset(x, y, out);
    }

    //konvertiert kartesische Ebenenkoordinaten in ellipsoidische Koordinaten
    public double[] cartToEll(double x, double y, double[] out) {
        //Formelsammlung LVermA NRW
        double rechts = (x - easting) / factor;
        double hoch = (y - northing) / factor;

        double c = ellipsoid.getPolkruemmungshalbmesser();
        double B0 = hoch / (c * ellipsoid.getGE0()); // pEll->Get_E0());
        double Bf = B0 + (ellipsoid.getGF2() * Math.sin(2.0 * B0)) +
                         (ellipsoid.getGF4() * Math.sin(4.0 * B0)) +
                         (ellipsoid.getGF6() * Math.sin(6.0 * B0));
/*        double Bf = B0 +	(pEll->Get_F2() * sin(2.0 * B0)) +
                                (pEll->Get_F4() * sin(4.0 * B0)) +
                                (pEll->Get_F6() * sin(6.0 * B0));
*/
        double cos_Bf 		= Math.cos(Bf);
        double tan_Bf 		= Math.tan(Bf);
        double tan_Bf_2 	= tan_Bf * tan_Bf;
        double tan_Bf_4 	= tan_Bf_2 * tan_Bf_2;


        double ETA = ellipsoid.getPowE2() * Math.pow(cos_Bf, 2.0d);
        double N = c / Math.sqrt(1.0d + ETA);    		//Querkr�mmungshalbmesser
        //OutputDebugString("Querkr�mmungshalbmesser ok\n");

        double y_N 				= rechts / N;

        double s1 = Math.pow(y_N, 2.0d) * (1.0d + ETA) / 2.0d;
        double s2 = Math.pow(y_N, 4.0d) * ((5.0d + 3.0d * tan_Bf_2) + 6.0d * ETA * (1.0d - tan_Bf_2)) / 24.0;
        double s3 = Math.pow(y_N, 6.0d) * (61.0d + 90.0d * tan_Bf_2 + 45.0d * tan_Bf_4) / 720.0d;
        double DB = - s1 + s2 - s3;
        double B = Bf + DB * tan_Bf;         //geogr. Breite in Radiant

        //L�nge  aus GK-Koord.
        double DL = y_N -
                        (Math.pow(y_N, 3.0d) * (1.0d + 2.0d * tan_Bf_2 + ETA) / 6.0d)	+
                         Math.pow(y_N, 5.0d) * (5.0d + 28.0d * tan_Bf_2 + 24.0d * tan_Bf_4) / 120.0d;
        double L = (centralmeridian * GeoSysUtil.DEG2RAD) + (DL / cos_Bf); 	//geogr. L�nge in Rad

        //L�nge/Breite -> Rechts/Hoch
//        x = L;        //Grad->Rad       //???
//        y = B;        //Breite in Rad   //???
/*
        B *= GeoSysUtil.RAD2DEG;
        L *= GeoSysUtil.RAD2DEG;
*/
        return GeoSysUtil.return2DCoord(L, B, out);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Geografische Koordinaten (Radiant) normalisieren
    protected double[] normalizeEll(double lon, double lat, double[] out) {
        lon -= (centralmeridian * GeoSysUtil.DEG2RAD);
        lat -= (nulllat * GeoSysUtil.DEG2RAD);
        return GeoSysUtil.return2DCoord(lon, lat, out);
    }

    //Skalierung und Nord- und Ostverschiebung durchf�hren
    protected double[] addOffset(double x, double y, double[] out) {
        x *= factor;
        y *= factor;
        x += easting;
        y += northing;
        return GeoSysUtil.return2DCoord(x, y, out);
    }
}
