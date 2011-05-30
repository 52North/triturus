package org.n52.v3d.triturus.survey.conterra;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   Jul 2, 2003
 * Time     :   4:20:26 PM
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
public class GaussKrueger extends TransverseMercator {

    // static attributes...


    // public attributes


    // private attributes

    private int strip;
    private int stripWidth;     //3° BRD (6° in DDR)

    // static methods


    // constructors

    public GaussKrueger() {
        this(3, 3);
    }

    public GaussKrueger(int strip, int stripWidth) {
        this.strip = strip;
        this.stripWidth = stripWidth;
        init();
    }

    public GaussKrueger(int strip, int stripWidth, String name) {
        this(strip, stripWidth);
        setName(name);
    }

    // public methods

    public int getStripZone() {
        return strip;
    }

    private void init() {
        factor = 1.0d;
        northing = 0.0d;
        if (stripWidth == 6) {	//Ostdeutschland
            //1-er Streifen geht von 0° bis 6° mit Zentralmeridian 3°
            centralmeridian = strip * stripWidth - 3.0;
        }
        else {
            //1-er Streifen geht von 1,5° bis 4,5° mit Zentralmeridian 3°
            centralmeridian = strip * stripWidth;
        }

        nulllat = 0.0d;
        easting = strip * 1000000.0d + 500000.0d;
    }
}
