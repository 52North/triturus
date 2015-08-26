/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.survey.coordinatetransform1;

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
