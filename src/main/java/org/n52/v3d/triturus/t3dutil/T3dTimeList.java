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
