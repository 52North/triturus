package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jun 24, 2004
 * Time     :   4:43:12 PM
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
public class UTM extends TransverseMercator {

    // static attributes...


    // public attributes


    // private attributes

    private int zone;

    // static methods


    // constructors

    public UTM() {
        this(1);
    }

    public UTM(int zone) {
        setZone(zone);
    }

    public UTM(int zone, String name) {
        this(zone);
        setName(name);
    }
    // public methods

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;

        //6° strip width
        //177° West is Centralmeridian of Zone 1 (180W - 174W)
        centralmeridian = (zone - 1) * 6.0d - 177.0d;
        nulllat = 0.0d;
        factor = 0.9996d;
        northing = 0.0d;
        //easting = 500000.0l;
        easting = zone * 1000000.0d + 500000.0d;
    }

}
