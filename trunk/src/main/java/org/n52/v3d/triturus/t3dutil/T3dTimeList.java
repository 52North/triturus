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
package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Helper class to manage named time stamps.<br /><br />
 * <i>German:</i> Hilfsklasse zur Verwaltung benannter Zeitpunkte. Die Klasse eignet sich insbesondere zur
 * Protokollierung von Zeitspannen w&auml;hrend der Programmausf&uuml;hrung. F&uuml;r jeden Listeeintrag werden Start-
 * und Endzeitpunkt verwaltet.
 * @author Benno Schmidt
 */
public class T3dTimeList
{
    private ArrayList mNames = null;
    private ArrayList mStart = null;
    private ArrayList mEnd = null;

    private boolean mLocalDebug = false; // F�r Debug-Zwecke kann Setzen dieses Flags hilfreich sein. 

    /**
     * Constructor.
     */
    public T3dTimeList()
    {
        mNames = new ArrayList();
        mStart = new ArrayList();
        mEnd = new ArrayList();
    }

    /**
     * resets the time.stamp list.<br /><br />
     * <i>Gernan:</i> setzt die Zeitpunkt-Liste zur&uuml;ck.
     */
    public void clear()
    {
        mNames.clear();
        mStart.clear();
        mEnd.clear();
    }

    /**
     * adds a time-stamp to the list.<br /><br />
     * <i>German:</i> f&uuml;gt der Liste einen Zeitpunkt hinzu. Die Startzeit wird auf die aktuelle Zeit gesetzt.
     * <br />
     * Bem.: Gro&szlig;- und Kleinschreibung werden f&uuml;r den Bezeichner nicht unterschieden.
     * @param pName Time-stamp designator
     */
    public void addTimeStamp(String pName)
    {
        if (mLocalDebug)
            System.out.println("Starting activity \"" + pName + "\"...");

        if (mNames.contains(pName.toLowerCase())) {
            int i = mNames.indexOf(pName.toLowerCase());
            mStart.set(i, new Long(new java.util.Date().getTime()));
            mEnd.set(i, new Long(0L));
        }
        else {
            mNames.add(pName.toLowerCase());
            mStart.add(new Long(new java.util.Date().getTime()));
            mEnd.add(new Long(0L));
        }
    }

    /**
     * sets the end-time for a list entry to the present time.<br /><br />
     * <i>German:</i> setzt die Endzeit f&uuml;r einen Listeneintrag auf die aktuelle Zeit.
     * @param pName Time-stamp designator
     */
    public void setFinished(String pName)
    {
        if (mLocalDebug)
            System.out.println("Finishing activity \"" + pName + "\"...");

        if (mNames.contains(pName.toLowerCase())) {
            int i = mNames.indexOf(pName.toLowerCase());
            mEnd.set(i, new Long(new java.util.Date().getTime()));
        } else
            throw new T3dException("Tried to access invalid time stamp \"" + pName + "\"...");
    }

    /**
     * returns a human-readable protocol of the time-stamps.<br /><br />
     * <i>German:</i> liefert ein aufbereitetes Protokoll der Zeitpunkte.<p>
     * @return Array holding strings for the time-stamps
     */
    public String[] protocol()
    {
        // Summe der Rechenzeiten ermitteln:
        long sum = 0L;
        for (int i = 0; i < mNames.size(); i++) {
            if (((Long) mEnd.get(i)).longValue() > 0) {
                long delta = ((Long) mEnd.get(i)).longValue() - ((Long) mStart.get(i)).longValue();
                sum += delta;
            }
        }

        // Format-Objekt f�r prozentuale Ausgabe instanziieren:
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.0", dfs);

        // Ausgabe erstellen:
        String[] res = new String[mNames.size() + 1];
        for (int i = 0; i < mNames.size(); i++)
        {
            if (((Long) mEnd.get(i)).longValue() > 0) {
                long delta = ((Long) mEnd.get(i)).longValue() - ((Long) mStart.get(i)).longValue();
                res[i] = ((String) mNames.get(i)) + ": " // Bezeichner
                    + ((Long) mStart.get(i)).longValue() // Startzeit
                    + " (" + delta + " msec, " // ben�tigte Zeit
                    + df.format(100. * ((float)delta / (float)sum)) + "%)"; // prozentualer Anteil an Gesamtzeit
            }
            else
                res[i] = ((String) mNames.get(i)) + ": " + ((Long) mStart.get(i)).longValue();
        }
        res[mNames.size()] = "Total time: " + sum + " msec";
        return res;
    }
}
