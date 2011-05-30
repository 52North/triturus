package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Comparable;

/**
 * <tt>VgProfile</tt>-Implementierung, bei der die Profil-Geometrie im Speicher vorgehalten wird.<p>
 * Bem.: Die vorliegende Implementierung liegt eine vereinfachte ACAD-GEO-Modellierung zugrunde (Beschränkung 
 * auf einen Werteverlauf z(t) pro Definitionslinie, keine Berücksichtigung visueller Attribute).<p>
 * @see GmProfile.TZPair
 * @author Benno Schmidt<br>
 * (c) 1992-1996, Geopro GmbH, 2004 con terra GmbH<br>
 */
public class GmProfile extends VgProfile
{
    private ArrayList mProfileData = null;
    private boolean mOrdered;
    
    /**
     * Konstruktor.<p>
     * @param pGeom <tt>VgLineString</tt>-Objekt mit Defintionslinie
     */
    public GmProfile(VgLineString pGeom) {
    	this.setGeometry(pGeom);
    	mProfileData = new ArrayList();
    	mOrdered = true;
    }
    
    /**
     * liefert die Anzahl der Stationsstellen des Profils, zu denen z(t)-Werte vorhanden sind.<p>
     * @return Anzahl der Stützstellen
     */
    public int numberOfTZPairs() {
        this.provideOrder();
        return mProfileData.size();
    }

    /**
     * liefert die Werte der i-ten Stationsstelle des Profils. Das erste Element des Ergebnisfeldes enthält die 
     * Stationierung t, das zweite Element den zugehörigen z-Wert.<p>
     * Die Folge der t-z-Wertepaare wächst bezüglich t stets monoton, d. h. für alle i gilt stets 
     * <i>getTZPair(i)[i] &lt;= getTZPair(i)[i + 1] </i>.<p>
     * Es ist die Bedingung <i>0 &lt;= i &lt; this.numberOfTZPairs()</i> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
     * @param i Stützpunkt-Index
     * @return zweielementiges Feld mit Werten für t und z(t)
     * @throws T3dException
     */
    public double[] getTZPair(int i) throws T3dException
    {
        this.provideOrder();
        if (i < 0 || i >= this.numberOfTZPairs())
            throw new T3dException("Index out of bounds.");
        double[] ret = new double[2];
        ret[0] = ((TZPair) mProfileData.get(i)).getT();
        ret[1] = ((TZPair) mProfileData.get(i)).getZ();
        return ret;
    }
    
    /**
     * fügt dem Profil eine Stationsstelle hinzu. Das erste Element des Parameterfeldes enthält die 
     * Stationierung t, das zweite Element den zugehörigen z-Wert. Durch die Operation erhöht sich der Wert 
     * für <tt>this.numberOfTZPairs()</tt> um 1.<p>
     * Bem.: Der Fall, dass für die angegebenen Station t bereits ein z-Wert im Profilverlauf enthalten ist, wird
     * nicht abgefangen.<p> 
     * @param pVal zweielementiges Feld mit Werten für t und z(t)
     */
    public void addTZPair(double[] pVal) throws T3dException
    {
        mProfileData.add(new TZPair(pVal[0], pVal[1]));
        if (mProfileData.size() >= 2) {
            if (pVal[0] <= ((TZPair) mProfileData.get(mProfileData.size() - 2)).getT())
                mOrdered = false; 
            else
                ; // mOrdered wie vor addTZPair-Aufruf belassen
        }
    }

    /**
     * liefert den Wert des Stationierungsparameters t für den Anfang des Belegungsbereichs des Profils.<p>
     * @return t-Wert &gt;= 0
     */
    public double tMin() {
        if (mProfileData.size() > 0)
            return ((TZPair) mProfileData.get(0)).getT();
        else
            return 0.;
    }

    /**
     * liefert den Wert des Stationierungsparameters t das Ende des Belegungsbereichs des Profils.<p>
     * @return t-Wert &lt;= <tt>this.tEnd()</tt>
     */
    public double tMax() {
        if (mProfileData.size() > 0)
            return ((TZPair) mProfileData.get(mProfileData.size() - 1)).getT();
        else
            return 0.;
    }

    /**
     * liefert den minimalen z-Wert des Profils.<p>
     * Bem.: Falls <tt>this.numberOfTZPairs()</tt> = 0 ist, wird der Wert 0.0 als Ergebnis zurückgegeben. Dieser Fall
     * ist von der aufrufenden Anwendung explizit abzufangen.<p>
     * @return Minimum aller z(t)
     */
    public double zMin() 
    {
        this.provideOrder(); // da t-Werte doppelt vorkommen können

        if (this.numberOfTZPairs() <= 0)
            return 0.;
    
        double z, zMin = ((TZPair) mProfileData.get(0)).getZ();
        for (int i = 1; i < this.numberOfTZPairs(); i++) {
            z = ((TZPair) mProfileData.get(i)).getZ();
            if (z < zMin) zMin = z;
        }
        return zMin;
    }

    /**
     * liefert den maximalen z-Wert des Profils.<p>
     * Bem.: Falls <tt>this.numberOfTZPairs()</tt> = 0 ist, wird der Wert 0.0 als Ergebnis zurückgegeben. Dieser Fall
     * ist von der aufrufenden Anwendung explizit abzufangen.<p>
     * @return Maximum aller z(t)
     */
    public double zMax() 
    {
        this.provideOrder(); // da t-Werte doppelt vorkommen können

        if (this.numberOfTZPairs() <= 0)
            return 0.;
    
        double z, zMax = ((TZPair) mProfileData.get(0)).getZ();
        for (int i = 1; i < this.numberOfTZPairs(); i++) {
            z = ((TZPair) mProfileData.get(i)).getZ();
            if (z > zMax) zMax = z;
        }
        return zMax;
    }
    
    public String toString() {
        String strGeom = "<empty geometry>";
        if (this.getGeometry() != null)
            strGeom = this.getGeometry().toString(); 
        return "[" + this.getName() + ", {# " + this.numberOfTZPairs() + " t-z-pairs}, " + strGeom + "]";
    }

    private void provideOrder() 
    {
        if (mOrdered || mProfileData.size() < 2) 
            return;
        else {
            Collections.sort(mProfileData);
            int ct = 1;
            double t0, t1;
            for (int i = 0; i < mProfileData.size() - 1; i++) {
                t0 = ((TZPair) mProfileData.get(i)).getT();
                t1 = ((TZPair) mProfileData.get(i + 1)).getT();
                if (t0 == t1) {
                    double z0, z1;
                    z0 = ((TZPair) mProfileData.get(i)).getZ();
                    z1 = ((TZPair) mProfileData.get(i + 1)).getZ();
                    ct++;
                    double avg = ((double) (ct - 1))/((double) ct) * z0 + 1./((double) ct) * z1;                  
                    ((TZPair) mProfileData.get(i)).setZ(avg);
                    mProfileData.remove(i + 1);
                    i--;
                }
                else
                    ct = 1;
            }
            mOrdered = true;
        }
    }
    
    /** 
     * Innere Klasse zur Haltung von t-z-Wertepaaren.<p>
     */
    public class TZPair implements Comparable
    {
    	private double mT;
    	private double mZ;
    	
    	/** Konstruktor.<p> */
    	public TZPair(double t, double z) { mT = t; mZ = z; };
    	
    	/** liefert den Stationierungsparameter.<p> */
    	public double getT() { return mT; }

    	/** setzt den Stationierungsparameter.<p> */
    	public void setT(double t) { mT = t; }

    	/** liefert den Höhenwert.<p> */
    	public double getZ() { return mZ; }

    	/** setzt den Höhenwert.<p> */
    	public void setZ(double z) { mZ = z; }
    	
    	/** definiert eine Ordnung auf t-z-Wertepaaren.<p> */
    	public int compareTo(Object tzp) {
    	    if (mT > ((TZPair) tzp).getT()) return 1;
    	    if (mT < ((TZPair) tzp).getT()) return -1;
    	    return 0;
    	}
    }
}
