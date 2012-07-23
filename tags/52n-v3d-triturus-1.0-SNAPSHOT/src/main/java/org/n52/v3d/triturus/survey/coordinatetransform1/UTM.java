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

        //6� strip width
        //177� West is Centralmeridian of Zone 1 (180W - 174W)
        centralmeridian = (zone - 1) * 6.0d - 177.0d;
        nulllat = 0.0d;
        factor = 0.9996d;
        northing = 0.0d;
        //easting = 500000.0l;
        easting = zone * 1000000.0d + 500000.0d;
    }

}
