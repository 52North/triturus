/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
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
package org.n52.v3d.triturus.examples.pointfeatures;

import org.n52.v3d.triturus.gisimplm.GmMeasurementPath;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.viskml.KmlScene;

/**
 * Triturus example application: Generates two measurement paths.
 *
 * @author Benno Schmidt
 */
public class MeasurementFlightExample
{
    public static void main(String args[])
    {
        MeasurementFlightExample app = new MeasurementFlightExample();
        
        // Define two measurement flights:
        GmMeasurementPath flight1 = app.runExample1();
        GmMeasurementPath flight2 = app.runExample2();
        
        // Export these flights to a KML file:
    	KmlScene s = new KmlScene();
    	s.add(flight1);
    	s.add(flight2);
    	s.generateScene("C:\\temp\\test.kml");
    }

    private GmMeasurementPath runExample1()
    {
        GmMeasurementPath flight = new GmMeasurementPath(3);

        VgPoint pos = new GmPoint();
        pos.setSRS(VgGeomObject.SRSLatLonWgs84);
        long time;
        double[] measurement;

        pos = new GmPoint(7.5, 52.0, 0.0);
        time = System.currentTimeMillis();
        measurement = new double[]{1.0, 2.0, 3.0};
        flight.addMeasurement(pos, time, measurement);

        pos = new GmPoint(7.51, 52.01, 5.0);
        time = System.currentTimeMillis();
        measurement = new double[]{1.5, 2.5, 3.5};
        flight.addMeasurement(pos, time, measurement);

        pos = new GmPoint(7.51, 52.1, 10.0);
        time = System.currentTimeMillis();
        measurement = new double[]{1.5, 3.0, 3.2};
        flight.addMeasurement(pos, time, measurement);

        pos = new GmPoint(7.5, 52.0, 0.0);
        time = System.currentTimeMillis();
        measurement = new double[]{1.01, 2.0, 3.0};
        flight.addMeasurement(pos, time, measurement);

        System.out.println(flight);
        System.out.println("Waypoints:" + flight.getGeometry());
        for (int i = 0; i < flight.numberOfMeasurements(); i++) {
            System.out.println("Measurement #" + i + ": "
                    + this.getAsString(flight.getMeasurement(i))
                    + " (" + (new java.util.Date(flight.getTimeStamp(i))).toString() + ")");
        }
        
        return flight;
    }

    private String getAsString(double[] vector) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < vector.length; i++) {
            buf.append(vector[i]);
            if (i < vector.length - 1)
                buf.append(" ");
        }
        return buf.toString();
    }

    private GmMeasurementPath runExample2()
    {
        GmMeasurementPath flight = new GmMeasurementPath(5);
        long N = 100000;

        VgPoint pos = new GmPoint();
        pos.setSRS(VgGeomObject.SRSLatLonWgs84);
        long time = System.currentTimeMillis();
        double[] measurement;

        for (int i = 0; i < N; i++) {
            pos = new GmPoint(7.5 + Math.random(), 52.0 + Math.random(), 100. * Math.random());
            time += 1000;
            measurement = new double[]{Math.random(), Math.random(), Math.random(), Math.random(), Math.random()};
            flight.addMeasurement(pos, time, measurement);
        }

        System.out.println(flight);
        System.out.println("Waypoints BBOX:" + flight.getGeometry().envelope());
        for (int i = 0; i < flight.numberOfMeasurements(); i += (N / 10)) {
            System.out.println("Measurement #" + i + ": "
                    + this.getAsString(flight.getMeasurement(i))
                    + " (" + flight.getTimeStamp(i) + ")");
        }
        
        return flight;
    }
}
