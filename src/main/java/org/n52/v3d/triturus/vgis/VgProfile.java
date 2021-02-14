/**
 * Copyright (C) 2007-2020 52North Initiative for Geospatial Open Source
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
 * Abstract base class for geo-referenced cross-sections.<br/>
 * <br/>
 * In the scope of the Triturus framework, a <i>profile</i> is a line-string 
 * geometry (2-D polyline as <i>definition line</i>), along thats run-length
 * a station parameter <i>t</i> will be managed ("Stationierungsparameter"). 
 * Inside the parameter range, unique <i>z</i>-values <i>f(t)</i> can be 
 * assigned to <i>t</i>-values.<br/>
 * <br/>
 * Often <i>f(t)</i> will give elevation values. Thus, these values are denoted
 * as <i>z</i>-values inside the framework. The <i>parameter range</i> denotes  
 * the range where <i>z</i>-values are present for the given definition line. 
 * Here this range is limited to intervals of the form [<i>t_min</i>, <i>t_max</i>] 
 * (make sure this is sufficient for your application). The case of ranges 
 * where no <i>z</i>-values are available ("unset values") might occur, if parts of 
 * the profile's definition line lie outside of an elevation-grid and consequently 
 * no <i>z</i>-value interpolation is possible there.<br/>
 * <br/>
 * Note: It will be postulated that the sequence of <i>t</i>-<i>z</i> value pairs 
 * (which is available via the method <tt>getTZPair</tt>) grows monotonously with 
 * respect to <i>t</i>. Make sure this condition holds for your application.<br/>
 * <br/>
 * <i>TODO: The current model allows to manage a single value chart z(t) per 
 * profile only. Future implementations might overcome this limitation.</i>
 * 
 * @author Benno Schmidt
 */
abstract public class VgProfile extends VgFeature 
{
    private VgLineString mGeom = null; 
        // modelling of VgProfile as VgLineString-decorator

    /**
     * sets the definition line ("base-line"). Note that the <i>z</i>-values 
     * of the definition line's vertices will not be relevant here.
     * 
     * @param geom <tt>VgLineString</tt> object
     */
    public void setGeometry(VgLineString geom) {
        mGeom = geom;
    }
    
    /** 
     * returns the definition line ("base-line" of the profile).
     * 
     * @return <tt>VgLineString</tt> object
     */
    public VgGeomObject getGeometry() {
        return mGeom; 
    }  

    /**
     * returns the number of position vertices ("Stationsstellen") for 
     * that <i>z</i>(<i>t</i>)-values or null-values are present. Here, 
     * <tt>null</tt>-values are used to mark unset profile positions.
     * 
     * @return Number of position vertices
     */
    abstract public int numberOfTZPairs();
    
    /**
     * returns the value pair of the <i>i</i>-th position point. The  
     * first element of the result array holds the position <i>t</i> 
     * (&quot;Stationierung&quot;), the second element the corresponding 
     * <i>z</i>-value.<br/>
     * <br/>
     * The sequence of <i>t</i>-<i>z</i> value-pairs grows monotonously with 
     * respect to <i>t</i>, i.e. for all <i>i</i> the following condition must
     * hold: <i>getTZPair(i)[0] &lt;= getTZPair(i + 1)[0]</i>.<br/>
     * <br/>
     * The condition <i>0 &lt;= i &lt; this.numberOfTZPairs()</i> also must 
     * hold; otherwise a <tt>T3dException</tt> will be thrown.
     * 
     * @param i Vertex index
     * @return Array with two elements containing the values for <i>t</i> and <i>z</i>(<i>t</i>)
     * @throws T3dException
     */
    abstract public Double[] getTZPair(int i) throws T3dException;

    /**
     * returns the value of the position parameter <i>t</i> (&quot;Stationierungsparameter&quot;) 
     * for the start-point of the definition line (&quot;base-line&quot; of the profile).
     * 
     * @return <i>t</i>-value, here = 0
     */
    public double tStart() {
        return 0.;
    }

    /**
     * returns the value of the position parameter <i>t</i> (&quot;Stationierungsparameter&quot;)
     * for the end-point of the definition line (&quot;base-line&quot; of the profile).
     * 
     * @return Base-line length
     */
    public double tEnd() {
        return mGeom.length();
    }

    /**
     * returns the value of the position parameter <i>t</i> for the begin of the 
     * definition line range that is providing <i>z</i>-values.<br/>
     * <br/>
     * Note that the return value may be <tt>null</tt>, if no <i>z</i>-values are 
     * given at all (e.g., for a definition line which is situated in unset 
     * areas of an elevation grid). This case has to be handled by the calling 
     * application.
     * 
     * @return <i>t</i>-value &gt;= 0
     */
    abstract public Double tMin();

    /**
     * returns the value of the position parameter <i>t</i> for the end of the 
     * definition line range that is providing <i>z</i>-values.<br/>
     * <br/>
     * Note that the return value may be <tt>null</tt>, if no <i>z</i>-values are 
     * given at all (e.g., for a definition line which is situated in unset 
     * areas of an elevation grid). This case has to be handled by the calling 
     * application.
     * 
     * @return <i>t</i>-value &lt;= <tt>this.tEnd()</tt>
     */
    abstract public Double tMax();

    /**
     * returns the cross-section's minimum <i>z</i>-value.<br/>
     * <br/>
     * Note that the return value may be <tt>null</tt>, if no <i>z</i>-values are 
     * given at all (e.g., for a definition line which is situated in unset 
     * areas of an elevation grid). This case has to be handled by the calling 
     * application.
     * 
     * @return Minimum of all <i>z</i>(<i>t</i>)
     */
    abstract public Double zMin();

    /**
     * returns the cross-section's maximum <i>z</i>-value.<br/>
     * <br/>
     * Note that the return value may be <tt>null</tt>, if no <i>z</i>-values are 
     * given at all (e.g., for a definition line which is situated in unset 
     * areas of an elevation grid). This case has to be handled by the calling 
     * application.
     * 
     * @return Maximum of all <i>z</i>(<i>t</i>)
     */
    abstract public Double zMax();
    
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
