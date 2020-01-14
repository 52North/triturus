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
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstract base class for geo-referenced cross-sections.
 * <br/>
 * in the scope of the Triturus framework, a <i>profile</i> is a line-segment 
 * geometrie (2-D polyline as <i>definition line</i>), along thats run-length a
 * station parameter t will be managed ("Stationierungsparameter"). Inside the
 * parameter range, unique z-values f(t) can be assigned to t-values.
 * <br/>
 * Often these values f(t) will be elevation values. Thus, the are named as 
 * z-values inside the framework. The <i>parameter range</i> denotes the range
 * where z-values are present for the given definition line. Here this range 
 * is limited to an interval of the form [t_min, t_max] (make sure this is 
 * sufficient for your application). The case of unset ranges might occur, if a 
 * parts of the profile's definition line lie outside of an elevation-grid and 
 * consequqntly no z-value interpolation is possible there.
 * <br/>
 * Note: It will be postulated that the sequence of t-z value pairs (which is
 * available via the method <tt>getTZPair</tt>) grows monotonous with respect 
 * to t. Make sure this condition  holds for your application.
 * <br/>
 * <i>TODO: The current model allows to manage a single value chart z(t) per 
 * profile only. Future implementation might overcome this limitation.
 * 
 * @author Benno Schmidt
 */
abstract public class VgProfile extends VgFeature 
{
    private VgLineString mGeom = null; 
    	// modelling of VgProfile as VgLineString-decorator

    /**
     * sets the base-line.
     * <br/>
     * Note: the geometry's z-value will be considered.
     * 
     * @param geom <tt>VgLineString</tt> object
     */
    public void setGeometry(VgLineString geom) {
    	mGeom = geom;
    }
    
    /** 
     * returns the base-line (definition line of the profile).
     * 
     * @return <tt>VgLineString</tt> object
     */
    public VgGeomObject getGeometry() {
    	return mGeom; 
    }  

    /**
     * returns the number of position vertices ("Stationsstellen ") for that 
     * z(t)-values are present.
     * 
     * @return Number of position vertices
     */
    abstract public int numberOfTZPairs();
    
    /**
     * returns the values of the i-th position point. The first element of the 
     * result array holds the position t ("Stationierung"), the second element 
     * the corresponding z-value.
     * <br/>
     * The sequqnce of t-z value-pairs grows monotonous with respect to t, i.e.
     * for all i the following condition must hold: 
     * <i>getTZPair(i)[i] &lt;= getTZPair(i)[i + 1] </i>.
     * <br/>
     * The condition <i>0 &lt;= i &lt; this.numberOfTZPairs()</i> must hold; 
     * otherwise a <tt>T3dException</tt> will be thrown.
     * 
     * @param i Vertex index
     * @return Array with two element containing the values for t and z(t)
     * @throws T3dException
     */
    abstract public double[] getTZPair(int i) throws T3dException;

    /**
     * returns the value of the position parameter t ("Stationierungsparameter") 
     * for the start-point of the base-line (definition line of the profile).
     * 
     * @return t-value, here = 0
     */
    public double tStart() {
        return 0.;
    }

    /**
     * returns the value of the position parameter t ("Stationierungsparameter")
     * for the end-point of the base-line (definition line of the profile).
     * 
     * @return Base-line length
     */
    public double tEnd() {
    	return mGeom.length();
    }

    /**
     * returns the value of the position parameter t for the begin of the base-line section that is providing
     * z-values.<br /><br />
     * <i>German:</> liefert den Wert des Stationierungsparameters t f&uuml;r den Anfang des Belegungsbereichs des
     * Profils.
     * @return t-value &gt;= 0
     */
    abstract public double tMin();

    /**
     * returns the value of the position parameter t ("Stationierungsparameter") 
     * for the end of the base-line section that is providing z-values (i.e., 
     * the "parameter range" of the profile).
     * 
     * @return t-value &lt;= <tt>this.tEnd()</tt>
     */
    abstract public double tMax();

    /**
     * returns the cross-section's minimal z-value.
     * 
     * @return Minimum of all z(t)
     */
    abstract public double zMin();

    /**
     * returns the cross-section's maximal z-value.
     * 
     * @return Maximum of all z(t)
     */
    abstract public double zMax();
    
	/**
	 * always returns <i>false</i>, since a cross-section describes no 
	 * collection of features.
	 * 
	 * @return <i>false</i>
	 */
    @Override
    public boolean isCollection() {
        return false;
    }

    /**
     * return the cross-section object itself.
     * 
     * @param i (here always 0)
     * @return Cross-section object itself
     * @throws T3dException
     */
    @Override
    public VgFeature getFeature(int i) throws T3dException
    {
        if (i != 0) 
            throw new T3dException("Index out of bounds." ); 
        // else:
        return this;
    }
    
    /**
     * always returns 1 as result, since a cross-section describes no 
     * collection of features.
     * 
     * @return 1
     */
    @Override
    public int numberOfSubFeatures() {
        return 1;
    }

    abstract public String toString();
}
