/**
 * Copyright (C) 2007-2015 52 North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Comparable;

/**
 * <tt>VgProfile</tt>-implementation. Object information will be kept in main
 * memory.
 * <br/>
 * x- and y-values have to be given with respect to the spatial reference 
 * system (SRS) that has been set for the geometric object. z-values might be
 * provided for the object's vertices.
 * <br/>
 * Note: This implementierung is based on a simplified ACAD-GEO model 
 * (limitation to a single value chart z(t) per definition line and no 
 * consideration of visual attributes).
 * 
 * @see GmProfile.TZPair
 * @author Benno Schmidt
 */
public class GmProfile extends VgProfile
{
    private ArrayList<TZPair> mProfileData = null;
    private boolean mOrdered;
    
    /**
     * Constructor.
     * 
     * @param geom <tt>VgLineString</tt>-object holding defining base-line
     */
    public GmProfile(VgLineString geom) {
    	this.setGeometry(geom);
    	mProfileData = new ArrayList<TZPair>();
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
     * adds a point to the cross-section.
     * <br/>
     * The first element of the parameter field holds the station-value t, the
     * second element the z-value belonging to this station. By calling this 
     * operation the value of <tt>this.numberOfTZPairs()</tt> will be incremented.
     * <br/>
     * Note: The case that for the given station t a z-value is already present 
     * will not be caught.
     * 
     * @param val Array consisting of two elements holding the values for t and z(t)
     */
    public void addTZPair(double[] val) throws T3dException
    {
        mProfileData.add(new TZPair(val[0], val[1]));
        if (mProfileData.size() >= 2) {
            if (val[0] <= ((TZPair) mProfileData.get(mProfileData.size() - 2)).getT())
                mOrdered = false; 
            else
                ; // leave mOrdered unchanged (value before addTZPair-call)
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
        this.provideOrder(); // since t-values might occur twice

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
     * returns the cross-section's maximum z-value.
     * <br/>
     * Note: If <tt>this.numberOfTZPairs()</tt> = 0, the result will be 0.0. 
     * This case has to be handeld by the calling application.
     * 
     * @return Maximum of all z(t)
     */
    public double zMax()
    {
        this.provideOrder(); // since t-values might occur twice

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
        return "[" + 
            this.getName() + 
            ", {# " + this.numberOfTZPairs() + " t-z-pairs}, " + strGeom + "]";
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
         * returns t ("Stationierungsparameter").
         */
    	public double getT() { return mT; }

    	/**
         * sets t ("Stationierungsparameter").
         */
    	public void setT(double t) { mT = t; }

    	/**
         * returns z (elevation value).
         */
    	public double getZ() { return mZ; }

    	/**
         * sets z (elevation value).
         */
    	public void setZ(double z) { mZ = z; }
    	
    	/**
         * defines an order-realation on t-z value-pairs.
         */
    	public int compareTo(Object tzp) {
    	    if (mT > ((TZPair) tzp).getT()) return 1;
    	    if (mT < ((TZPair) tzp).getT()) return -1;
    	    return 0;
    	}
    }
}
