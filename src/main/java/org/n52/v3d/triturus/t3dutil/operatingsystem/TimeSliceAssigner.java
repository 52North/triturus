package org.n52.v3d.triturus.t3dutil.operatingsystem;

/**
 * Klasse zur Steuerung von Zeitscheiben-bezogener Ressourcen-Zuteilungen.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH<br>
 */
public class TimeSliceAssigner
{
    // Zeitpunkt der Instanziierung des TimeSliceAssigner-Objekts:
    private long mInitTime;
    // interne Nummer der nächsten freien Zeitscheibe:
    private long mNextFreeSlice;
    // max. Zeitraum, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf (angegeben als
    // Zeitscheiben-Anzahl):
    private int mMaxWaitTime = 5;
    // Zeitscheiben-Länge in in Millisekunden
    private long mDeltaT = 10000;
    
    /**
     * Konstruktor. Als Parameter sind der maximale Zeitraum, um den eine angeforderte Zeitscheibe in der Zukunft liegen
     * darf,  und die zeitliche Länge der Zeitscheiben zu übergeben.<p>
     * Der maximale Zeitraum, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf, ist als
     * Zeitscheiben-Anzahl anzugeben. Die Zeitdauer in Millisekunden beträgt
     * <tt>this.getSliceInterval() * this.maxWaitTime()</tt>. Voreingestellt ist der Wert 5. Wird diese Anzahl
     * überschritten, liefert die Methode <tt>getAssignedSlice</tt> bei der Zuteilung keine nutzbare Zeitscheibe.<p>
     * Für die zeitliche Länge der Zeitscheiben ist der Wert 10000 (entspr. 10 Sekunden) voreingestellt.<p>
     * @param pMaxWaitTime max. Zeitscheiben-Anzahl, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf
     * @param pDeltaT Dauer in Millisekunden
     * @see TimeSliceAssigner#getAssignedSlice
     * @see TimeSliceAssigner#getSliceInterval
     * @see TimeSliceAssigner#maxWaitTime
     */
    public TimeSliceAssigner(int pMaxWaitTime, long pDeltaT) {
        mInitTime = this.currentTime();    
        mNextFreeSlice = 0;
        mMaxWaitTime = pMaxWaitTime;
        mDeltaT = pDeltaT;
    }

    /**
     * Konstruktor.<p>
     */
    public TimeSliceAssigner() {
        mInitTime = this.currentTime();
        mNextFreeSlice = 0;
    }

    /**
     * liefert die aktuelle Systemzeit.<p>
     */
    public long currentTime() {
        return new java.util.Date().getTime();
    }

    /**
     * liefert die konfigurierte zeitliche Länge der Zeitscheiben.<p>
     * @return Dauer in Milisekunden
     */
    public long getSliceInterval() {
        return mDeltaT;
    }

    /**
     * liefert den maximalen Zeitraum, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf (angegeben als
    // Zeitscheiben-Anzahl).<p>
     * @return max. Zeitscheiben-Anzahl, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf
     */
    public int maxWaitTime() {
        return mMaxWaitTime;
    }

    /**
     * teilt dem anfragenden Prozess eine Zeitscheibe zu und gibt den zugehörigen Startzeitpunkt zurück. Falls keine der
     * nächsten <i>N</i> Zeitscheiben frei ist, wird <i>null</i> zurückgegeben. Dieser Fall tritt auf, wenn die
     * konfigurierte Anzahl <i>N</i> der Zeitscheiben, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf,
     * überschritten wird.<p>
     * Bei jedem Methodenaufruf wird eine neue Zeitscheibe belegt. Seitens der aufrufenden Anwendung ist somit
     * sicherzustellen, dass je anzufordernder Zeitscheibe nur ein einziger Aufruf erfolgt!<p>
     * Bem.: Die Dauer die Zeitscheibe ist mittels <tt>getSliceInterval()</tt> abfragbar, die maximale
     * Zeitscheiben-Anzahl, um den eine angeforderte Zeitscheibe in der Zukunft liegen darf, über die Methode
     * <tt>maxWaitTime()</tt>. Die Kontrolle dessen, was sich während des Ablaufs der Zeitscheibe ereignet, obliegt der
     * Anwendung, welche die vorliegende Klasse nutzt.<p>
     * @return Startzeitpunkt der zugeteilten Zeitscheibe oder <i>null</i>
     */
    public Long getAssignedSlice() 
    { 
        long lCurrTime = this.currentTime();
        long lSliceNo = (long) Math.floor((lCurrTime - mInitTime) / mDeltaT);
        if (mNextFreeSlice <= lSliceNo) {
            mNextFreeSlice = lSliceNo + 1;
            return this.startTime(lSliceNo);
        }
        else {
            // mNextFreeSlice > lSliceNo
            if (mNextFreeSlice - lSliceNo >= mMaxWaitTime)
                return null; // da "busy"
            else {
                mNextFreeSlice++;
                return this.startTime(mNextFreeSlice);
            }
        }
    }

    private Long startTime(long pSliceNo) {
        return new Long(mInitTime + pSliceNo * mDeltaT);
    }
}