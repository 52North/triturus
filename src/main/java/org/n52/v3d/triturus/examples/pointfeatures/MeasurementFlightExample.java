package org.n52.v3d.triturus.examples.pointfeatures;


import org.n52.v3d.triturus.gisimplm.GmMeasurementPath;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;

public class MeasurementFlightExample
{
    public static void main(String args[])
    {
        MeasurementFlightExample app = new MeasurementFlightExample();
        app.runExample1();
        app.runExample2();
    }

    private void runExample1()
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

    private void runExample2()
    {
        GmMeasurementPath flight = new GmMeasurementPath(5);
        long N = 100000;

        VgPoint pos = new GmPoint();
        pos.setSRS(VgGeomObject.SRSLatLonWgs84);
        long time;
        double[] measurement;

        for (int i = 0; i < N; i++) {
            pos = new GmPoint(7.5 + Math.random(), 52.0 + Math.random(), 100. * Math.random());
            time = System.currentTimeMillis();
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
    }
}
