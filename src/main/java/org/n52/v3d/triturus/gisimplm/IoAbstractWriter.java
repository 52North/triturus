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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Abstract base-class for I/O objects that write geo-data to files or streams.<br /><br />
 * <i>German:</i> Abstrakte Basisklasse f&uuml;r I/O-Objekte, welche Geodaten in Dateien oder Str&ouml;me schreiben.
 * <br />
 * Bem.: Bez&uuml;glich des Formats der geschriebenen x-, y- und z-Koordinaten sind ggf. die Rechner-spezifischen
 * Locale-Einstellungen zu ber&uuml;cksichtigen!
 * @author Benno Schmidt
 */
abstract public class IoAbstractWriter extends IoObject
{
    private int mPrecisionXY = 2; // # zu schreibender Nachkommastellen f�r x- und y-Koordinaten
    private int mPrecisionZ = 2; // # zu schreibender Nachkommastellen f�r z-Koordinaten

    /**
     * returns the number of decimal places (after decimal point) for x- and y-coordinates to be written.<br /><br />
     * <i>German:</i> liefert die Anzahl der f&uuml;r x- und y-Koordinaten zu schreibenden Nachkommastellen.
     * @return Number of decimal places (after decimal point)
     */
    public int getPrecisionXY() {
        return mPrecisionXY;
    }

    /**
     * sets the number of decimal places (after decimal point) for x- and y-coordinates to be written.<br /><br />
     * <i>German:</i> setzt die Anzahl der f&uuml;r x- und y-Koordinaten zu schreibenden Nachkommastellen.
     * Voreinstellungsgem&auml;&szlig; ist der Wert 2 gesetzt.
     * @param pPrec Number of decimal places (after decimal point)
     */
    public void setPrecisionXY(int pPrec) {
        if (pPrec < 0)
            throw new T3dException("Invalid value for precision (" + pPrec + ").");
        mPrecisionXY = pPrec;
    }

    /**
     * returns the number of decimal places (after decimal point) for z-coordinates to be written.<br /><br />
     * <i>German:</i> liefert die Anzahl der f&uuml;r z-Koordinaten zu schreibenden Nachkommastellen.
     * @return Number of decimal places (after decimal point)
     */
    public int getPrecisionZ() {
        return mPrecisionZ;
    }

    /**
     * sets the number of decimal places (after decimal point) for z-coordinates to be written.<br /><br />
     * <i>German:</i> setzt die Anzahl der f&uuml;r z-Koordinaten zu schreibenden Nachkommastellen.
     * Voreinstellungsgem&auml;&szlig; ist der Wert 2 gesetzt.
     * @param pPrec Number of decimal places (after decimal point) &gt;= 0
     */
    public void setPrecisionZ(int pPrec) {
        if (pPrec < 0)
            throw new T3dException("Invalid value for precision (" + pPrec + ").");
        mPrecisionZ = pPrec;
    }

    /**
     * provides the <tt>DecimalFormat</tt>-object according to the precision that has been set for x- and y-coordinates.
     * <br /><br />
     * <i>German:</i>liefert das der gesetzten Nachkommastellen-Anzahl f&uuml;r x- und y-Koordinaten entsprechende
     * <tt>DecimalFormat</tt>-Objekt. Als Dezimalpunkt wird dabei stets das Zeichen '.' verwendet.
     * @return <tt>DecimalFormat</tt>-object
     */
    protected DecimalFormat getDecimalFormatXY() {
        String format = "0";
        if (mPrecisionXY > 0)
            format = format + ".";
        for (int i = 0; i < mPrecisionXY; i++)
            format = format + "0";
        return new DecimalFormat(format, this.decimalFormatSymbols());
    }

    /**
     * provides the <tt>DecimalFormat</tt>-object according to the precision that has been set for z-coordinates.
     * <br /><br />
     * <i>German:</i>liefert das der gesetzten Nachkommastellen-Anzahl f&uuml;r z-Koordinaten entsprechende
     * <tt>DecimalFormat</tt>-Objekt. Als Dezimalpunkt wird dabei stets das Zeichen '.' verwendet.
     * @return <tt>DecimalFormat</tt>-object
     */
    protected DecimalFormat getDecimalFormatZ() {
        String format = "0";
        if (mPrecisionZ > 0)
            format = format + ".";
        for (int i = 0; i < mPrecisionZ; i++)
            format = format + "0";
        return new DecimalFormat(format, this.decimalFormatSymbols());
    }

    private DecimalFormatSymbols decimalFormatSymbols()
    {
        DecimalFormatSymbols ret = new DecimalFormatSymbols();
        ret.setDecimalSeparator('.');
        return ret;
    }
}
