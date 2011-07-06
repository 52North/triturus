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

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Comparable;

/**
 * <tt>VgProfile</tt>-implementation. Object information will be kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system (SRS) that has been set for the
 * geometric object. z-values might be provided for the object's vertices.<br /><br />
 * <i>German:</i> <tt>VgProfile</tt>-Implementierung, bei der die Profil-Geometrie im Speicher vorgehalten wird.<br />
 * Bem.: Die vorliegende Implementierung liegt eine vereinfachte ACAD-GEO-Modellierung zugrunde (Beschr&auml;nkung
 * auf einen Werteverlauf z(t) pro Definitionslinie, keine Ber&uuml;cksichtigung visueller Attribute).<br />
 * @see GmProfile.TZPair
 * @author Benno Schmidt
 */
public class GmProfile extends VgProfile
{
    private ArrayList mProfileData = null;
    private boolean mOrdered;
    
    /**
     * Constructor.
     * @param pGeom <tt>VgLineString</tt>-object holding defining base-line
     */
    public GmProfile(VgLineString pGeom) {
    	this.setGeometry(pGeom);
    	mProfileData = new ArrayList();
    	mOrdered = true;
    }
    
    public int numberOfTZPairs() {
        this.provideOrder();
        return mProfileData.size();
    }

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
     * adds a point to the cross-section.<br /><br />
     * <i>German:</i> f&uuml;gt dem Profil eine Stationsstelle hinzu. Das erste Element des Parameterfeldes enth&auml;lt
     * die Stationierung t, das zweite Element den zugeh&ouml;rigen z-Wert. Durch die Operation erh&ouml;ht sich der
     * Wert f&uuml;r <tt>this.numberOfTZPairs()</tt> um 1.<br />
     * Bem.: Der Fall, dass f&uuml;r die angegebenen Station t bereits ein z-Wert im Profilverlauf enthalten ist, wird
     * nicht abgefangen.
     * @param pVal Array consisting of two elements holding the values for t and z(t)
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

    public double tMin() {
        if (mProfileData.size() > 0)
            return ((TZPair) mProfileData.get(0)).getT();
        else
            return 0.;
    }

    public double tMax() {
        if (mProfileData.size() > 0)
            return ((TZPair) mProfileData.get(mProfileData.size() - 1)).getT();
        else
            return 0.;
    }

    public double zMin()
    {
        this.provideOrder(); // da t-Werte doppelt vorkommen k�nnen

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
     * returns the cross-section's maximal z-value.<br /><br />
     * <i>German:</> liefert den maximalen z-Wert des Profils.<br />
     * Bem.: Falls <tt>this.numberOfTZPairs()</tt> = 0 ist, wird der Wert 0.0 als Ergebnis zur&uuml;ckgegeben. Dieser
     * Fall ist von der aufrufenden Anwendung explizit abzufangen.
     * @return Maximum of all z(t)
     */
    public double zMax()
    {
        this.provideOrder(); // da t-Werte doppelt vorkommen k�nnen

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
     * Inner class to hold t-z value pairs.
     */
    public class TZPair implements Comparable
    {
    	private double mT;
    	private double mZ;
    	
    	/**
         * Constructor.
         */
    	public TZPair(double t, double z) { mT = t; mZ = z; };
    	
    	/**
         * returns t.<br /><br />
         * <i>German:</i> liefert den Stationierungsparameter.
         */
    	public double getT() { return mT; }

    	/**
         * sets t.<br /><br />
         * <i>German:</i> setzt den Stationierungsparameter.
         */
    	public void setT(double t) { mT = t; }

    	/**
         * returns z.<br /><br />
         * <i>German:</i> liefert den H&ouml;henwert.
         */
    	public double getZ() { return mZ; }

    	/**
         * sets z.<br /><br />
         * <i>German:</i> setzt den H&ouml;henwert.
         */
    	public void setZ(double z) { mZ = z; }
    	
    	/**
         * defines an order-realtion on t-z value-pairs.<br /><br />
         * <i>German:</i> definiert eine Ordnung auf t-z-Wertepaaren.
         */
    	public int compareTo(Object tzp) {
    	    if (mT > ((TZPair) tzp).getT()) return 1;
    	    if (mT < ((TZPair) tzp).getT()) return -1;
    	    return 0;
    	}
    }
}
