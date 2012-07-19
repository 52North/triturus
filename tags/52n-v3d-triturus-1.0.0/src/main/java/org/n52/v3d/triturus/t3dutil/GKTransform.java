package org.n52.v3d.triturus.t3dutil;

import java.util.*;

/**
 * @deprecated -> Klassen aus org.n52.v3d.triturus.survey nutzen!
 */
public class GKTransform {


    //******Konstanten******//
    
    //Ellipsoidparameter für BESSEL
    static final double    a  = 6377397.155;
    static final double    b  = 6356078.96282;
    static final double    a2 = 4.06711944726E13;
    static final double    b2 = 4.039973978389E13;
    static final double    e_2 = 6.674372174975E-3;    //e_2=e2  Quadr. der 
    //1.numerische Exzentrizität
    static final double    e2_2 =   0.006719218741582;   //e2_=e'2 Quadr. der 
    //2.numerische Exzentrizität
    static final double    e = 0.08169683087473;    // e2 = (a2-b2)/a2
    static final double    N3 = 0.001674184800972;   // n = (a-b)/(a+b)
    
    static final double    B0 =  0.8194703840921;
    static final double    L0 =  0.129845224085;
    
    static final double    PI       =      3.14159265358979323846;
    static final double    DEG2RAD =       PI / 180.0;
    static final double    RAD2DEG = 180.0 / PI;
    //Lotfußpunkte
    static final double    E0 = 0.994992124558914; //Konstanten zur Berechnung
    static final double   F2 = 0.00251127322033324;    // der geographischen
    static final double    F4 = 3.67878644696096E-6;   // Breite des
    static final double    F6 = 7.38044744263472E-9;   // Lotfußpunktes

    //*******ENDE Konstanten********//


		public static double[] ellToGauss(double lon, double lat, int streifen, double[] out) {
			//ellip. --> Gauss-Krüger
			//Quelle: Formelsammlung LVA - NRW, Blatt 1-9,1-10

			double rechts, hoch;

			if ((out == null) || (out.length < 2))
	    	out = new double[2];

			double N2 = a2 / Math.sqrt((a2 * Math.pow (Math.cos (lat), 2.0)  + (b2 * Math.pow (Math.sin (lat), 2.0))));
			//Querkrümmungshalbmesser

			double t = Math.tan (lat);
			double t2 = Math.pow (t,2.0);
			double l = lon - (streifen * 3.0 * DEG2RAD);
		
			double l1 = l * Math.cos(lat);
			double ETA2 = e2_2 * Math.pow (Math.cos (lat),2.0);
			double ETA1 = Math.sqrt(ETA2);  //Wurzel aus ETA2
		
			// Formeln nach Hofmann-Wellenhof GPS in der Praxis S. 93

			double f1 = ((a + b)/2.0) * ( 1.0 + Math.pow(N3,2.0)/4.0 + Math.pow(N3,4.0)/64.0);
			double f2 =  - 3.0/2.0 * N3 + 9.0/16.0 * Math.pow(N3,3.0) - 3.0/32.0 * Math.pow(N3,5.0);
			double f3 =  15.0/16.0 * Math.pow(N3,2.0) - 15.0/32.0 * Math.pow(N3,4.0);
			double f4 =  - 35.0/48.0 * Math.pow(N3,3.0) + 105.0/256.0 * Math.pow(N3,5.0);
			double f5 =  315.0/512.0 * Math.pow(N3,4.0);

			// term1..4 nicht definiert!
			double term1, term2, term3, term4;

			term1 = f1 * (lat + f2 * Math.sin(2*lat) +  f3 * Math.sin(4*lat) + f4 * Math.sin(6*lat) + f5 * Math.sin(8*lat));

			term2 =  (N2*t) * ((Math.pow(l1,2.0)/2.0) + Math.pow(l1,4.0)/24.0 * (5.0 - t2 + 9.0*ETA2 + 4.0*Math.pow(ETA1,4.0)));

			term3 =  (N2*t) * ((Math.pow(l1,6.0)/720.0) * (61.0 - 58.0*t2 + Math.pow(t,4.0) + 270.0*ETA2 - 330.0*t2*ETA2));

			term4 =  (N2*t) * ((Math.pow(l1,8.0)/40320.0) * (1385.0 - 3111.0*t2 + 543.0*Math.pow(t,4.0) - Math.pow(t,6.0)));

			hoch = term1 + term2 + term3 + term4;

			rechts = N2 * ((l1 + (Math.pow(l1,3.0)/6.0) * (1 - t2 + ETA2) + ((Math.pow(l1,5.0)/120.0) * ((5.0 - 18.0 * t2) + Math.pow(t,4.0) + 14.0 * ETA2 - 58.0 * t2 * ETA2)) + (Math.pow(l1,7.0)/5040.0) * (61.0 - 479.0 * t2 + 179.0 * Math.pow(t,4.0) - Math.pow(t,6.0))));

			switch (streifen) {
				case 2: rechts += 2500000L; break;
				case 3: rechts += 3500000L; break;
				case 4: rechts += 4500000L; break;
			}
			
			out[0] = rechts;
			out[1] = hoch;
			return out;
    }


		public static double[] gaussToEll(double rechts, double hoch, int streifen, double[] out) {
			double lon, lat;

			if ((out == null) || (out.length < 2))
	    	out = new double[2];

			switch (streifen) {
				case 2: rechts -= 2500000L; break;
				case 3: rechts -= 3500000L; break;
				case 4: rechts -= 4500000L; break;
			}
	
			double c = a2 / b;
	
			double B0 = hoch / (c * E0);
			double Bf = B0 + (F2 * Math.sin(2.0 * B0)) + (F4 * Math.sin(4.0 * B0)) + (F6 * Math.sin(6.0 * B0));

			double cos_Bf 		= Math.cos(Bf);
			double tan_Bf 		= Math.tan(Bf);
			double tan_Bf_2 	= tan_Bf * tan_Bf;
			double tan_Bf_4 	= tan_Bf_2 * tan_Bf_2;


			double ETA = e2_2 * Math.pow(cos_Bf, 2.0);
			double N = c / Math.sqrt(1.0 + ETA);  //Querkrümmungshalbmesser
		
			double y_N = rechts / N;
		
			double s1 = Math.pow(y_N, 2.0) * (1.0 + ETA) / 2.0;
			double s2 = Math.pow(y_N, 4.0) * ((5.0 + 3.0 * tan_Bf_2) + 6.0 * ETA * (1.0 - tan_Bf_2)) / 24.0;
			double s3 = Math.pow(y_N, 6.0) * (61.0 + 90.0 * tan_Bf_2 + 45.0 * tan_Bf_4) / 720.0;
			double DB = - s1 + s2 - s3;
			lat = Bf + DB * tan_Bf;         //geogr. Breite in Radiant

			//Länge  aus GK-Koord.
			double DL = y_N - (Math.pow(y_N, 3.0) * (1.0 + 2.0 * tan_Bf_2 + ETA) / 6.0) + Math.pow(y_N, 5.0) * (5.0 + 28.0 * tan_Bf_2 + 24.0 * tan_Bf_4) / 120.0;
			lon = (streifen * 3.0 * DEG2RAD) + (DL / cos_Bf); //geogr. Länge in Rad
	
			out[0] = lon;
			out[1] = lat;
			return out;
    }


		public static double[] convertStrip(double rechts_alt, double hoch_alt, int altStreifen,	int neuStreifen, double[] rechts_hoch_neu) {
			rechts_hoch_neu = gaussToEll(rechts_alt, hoch_alt, altStreifen, rechts_hoch_neu);
			rechts_hoch_neu = ellToGauss(rechts_hoch_neu[0], rechts_hoch_neu[1], neuStreifen, rechts_hoch_neu);
			return rechts_hoch_neu;
		}		

		
// 		public static void main(String[] args) {
// 			//GKTransformation gkt = new GKTransformation();
			
// 			double[] res = GKTransformation.convertStrip(3500000.0,5600000.0,3,2,null);
			
// 			res = GKTransformation.convertStrip(res[0],res[1],2,3,res);

			
//   	}

}
