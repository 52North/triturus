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
package org.n52.v3d.triturus.survey;

/**
 * Helper class holding WGS84-specific constants.
 * @author Benno Schmidt
 */
public class Wgs84Helper
{
    /**
     * Earth radius.<br /><br />
     * <i>German:</i> Erdradius f&uuml;r WGS-84-Geoid in Metern.
     */
    public static final double radius = 6378137;

    /**
     * Earth circumference.<br /><br />
     * <i>German:</i> Erdumfang f&uuml;r WGS-84-Geoid in Metern.
     */
    public static final double circumference = 2. * Math.PI * Wgs84Helper.radius;

    /**
     * Extent of a decimal degree given in meters.<br /><br />
     * <i>German:</i> Ausdehnung eines Winkelgrades am &Auml;quator in Metern.
     */
    public static final double degree2meter = Wgs84Helper.circumference / 360.; // entspr. 111319 m
}
