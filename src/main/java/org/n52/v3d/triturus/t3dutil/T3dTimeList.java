package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Hilfsklasse zur Verwaltung benannter Zeitpunkte. Die Klasse eignet sich insbesondere zur Protokollierung von
 * Zeitspannen während der Programmausführung. Für jeden Listeeintrag werden Start- und Endzweitpunkt verwaltet.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dTimeList
{
    private ArrayList mNames = null;
    private ArrayList mStart = null;
    private ArrayList mEnd = null;

    private boolean mLocalDebug = false; // Für Debug-Zwecke kann Setzen dieses Flags hilfreich sein. 

    /**
     * Konstruktor.<p>
     */
    public T3dTimeList()
    {
        mNames = new ArrayList();
        mStart = new ArrayList();
        mEnd = new ArrayList();
    }

    /**
     * setzt die Zeitpunkt-Liste zurück.<p>
     */
    public void clear()
    {
        mNames.clear();
        mStart.clear();
        mEnd.clear();
    }

    /**
     * fügt der Liste einen Zeitpunkt hinzu. Die Startzeit wird auf die aktuelle Zeit gesetzt.<p>
     * Bem.: Groß- und Kleinschreibung werden für den Bezeichner nicht unterschieden.<p>
     * @param pName Zeitpunkt-Bezeichner
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
     * setzt die Endzeit für einen Listeneintrag auf die aktuelle Zeit.<p>
     * @param pName Zeitpunkt-Bezeichner
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
     * liefert ein aufbereitetes Protokoll der Zeitpunkte.<p>
     * @return Feld mit String für Zeitpunkte
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

        // Format-Objekt für prozentuale Ausgabe instanziieren:
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
                    + " (" + delta + " msec, " // benötigte Zeit
                    + df.format(100. * ((float)delta / (float)sum)) + "%)"; // prozentualer Anteil an Gesamtzeit
            }
            else
                res[i] = ((String) mNames.get(i)) + ": " + ((Long) mStart.get(i)).longValue();
        }
        res[mNames.size()] = "Total time: " + sum + " msec";
        return res;
    }
}
