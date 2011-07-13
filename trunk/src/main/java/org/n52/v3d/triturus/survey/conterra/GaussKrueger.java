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
public class GaussKrueger extends TransverseMercator {

    // static attributes...


    // public attributes


    // private attributes

    private int strip;
    private int stripWidth;     //3� BRD (6� in DDR)

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
            //1-er Streifen geht von 0� bis 6� mit Zentralmeridian 3�
            centralmeridian = strip * stripWidth - 3.0;
        }
        else {
            //1-er Streifen geht von 1,5� bis 4,5� mit Zentralmeridian 3�
            centralmeridian = strip * stripWidth;
        }

        nulllat = 0.0d;
        easting = strip * 1000000.0d + 500000.0d;
    }
}
