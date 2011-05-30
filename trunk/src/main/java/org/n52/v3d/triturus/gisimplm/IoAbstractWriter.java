package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Abstrakte Basisklasse f�r I/O-Objekte, welche Geodaten in Dateien oder Str�me schreiben.<p>
 * Bem.: Bez�glich des Formats der geschriebenen x-, y- und z-Koordinaten sind ggf. die Rechner-spezifischen
 * Locale-Einstellungen zu ber�cksichtigen!<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class IoAbstractWriter extends IoObject
{
    private int mPrecisionXY = 2; // # zu schreibender Nachkommastellen f�r x- und y-Koordinaten
    private int mPrecisionZ = 2; // # zu schreibender Nachkommastellen f�r z-Koordinaten

    /**
     * liefert die Anzahl der f�r x- und y-Koordinaten zu schreibenden Nachkommastellen.<p>
     * @return gesetzte Nachkommastellen-Anzahl
     */
    public int getPrecisionXY() {
        return mPrecisionXY;
    }

    /**
     * setzt die Anzahl der f�r x- und y-Koordinaten zu schreibenden Nachkommastellen. Voreinstellungsgem�� ist der
     * Wert 2 gesetzt.<p>
     * @param pPrec zu setzende Nachkommastellen-Anzahl &gt;= 0
     */
    public void setPrecisionXY(int pPrec) {
        if (pPrec < 0)
            throw new T3dException("Invalid value for precision (" + pPrec + ").");
        mPrecisionXY = pPrec;
    }

    /**
     * liefert die Anzahl der f�r z-Koordinaten zu schreibenden Nachkommastellen.<p>
     * @return gesetzte Nachkommastellen-Anzahl
     */
    public int getPrecisionZ() {
        return mPrecisionZ;
    }

    /**
     * setzt die Anzahl der f�r z-Koordinaten zu schreibenden Nachkommastellen. Voreinstellungsgem�� ist der Wert 2
     * gesetzt.<p>
     * @param pPrec zu setzende Nachkommastellen-Anzahl &gt;= 0
     */
    public void setPrecisionZ(int pPrec) {
        if (pPrec < 0)
            throw new T3dException("Invalid value for precision (" + pPrec + ").");
        mPrecisionZ = pPrec;
    }

    /**
     * liefert das der gesetzten Nachkommastellen-Anzahl f�r x- und y-Koordinaten entsprechende
     * <tt>DecimalFormat</tt>-Objekt. Als Dezimalpunkt wird dabei stets das Zeichen '.' verwendet.<p>
     * @return <tt>DecimalFormat</tt>-Objekt
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
     * liefert das der gesetzten Nachkommastellen-Anzahl f�r z-Koordinaten entsprechende <tt>DecimalFormat</tt>-Objekt.
     * Als Dezimalpunkt wird dabei stets das Zeichen '.' verwendet.<p>
     * @return <tt>DecimalFormat</tt>-Objekt
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
