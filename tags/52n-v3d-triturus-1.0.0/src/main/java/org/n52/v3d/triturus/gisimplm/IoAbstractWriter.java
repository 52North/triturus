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
