/**
 * Copyright (C) 2007-2015 52North Initiative for Geospatial Open Source
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
 * license version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * General base class to manage "geo-objects" (using OGC/ISO jargon: 
 * <i>&quot;features&quot;</i> without thematic properties).<br/>
 * <br/>
 * Note: Inside this framework, for geo-objects with thematic properties 
 * (using OGC/ISO jargon: <i>&quot;features&quot;</i>) the class 
 * <tt>VgAttrFeature</tt> should be used. In fact, this class seems to 
 * be present for historical reasons only. (In the future, maybe it 
 * could be replaced by a class called <tt>VgGeometricFeature</tt>; 
 * <tt>VgAttrFeature</tt> could be renamed to <tt>VgFeature</tt> then... 
 * -> TODO)<br/>
 * <br/>
 * Basically, within the Triturus framework, a <tt>VgFeatures</tt> might
 * be composed of an arbitrary number > 0 of <tt>VgFeatures</tt>. This way 
 * <i>feature collections</i> might be set up.
 *
 * @see org.n52.v3d.triturus.vgis.VgAttrFeature
 * @author Benno Schmidt
 */
abstract public class VgFeature 
{
    private String mName = "unnamed feature";

    /**
     * sets the geo-object's name (e.g., a designator or a title). Note that 
     * inside the framework this object name must not be unique.
     *
     * @param name arbitrary string
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * return the geo-object's name.
     *
     * @return Object name (must not be unique)
     */
    public String getName() {
        return mName;
    }

    /**
     * return an (atomic) <tt>VgFeature</tt> object's geometry.
     *
     * @return Object geometry
     */
    abstract public VgGeomObject getGeometry();

    /**
     * returns the <i>i</i>-th object of a &quot;non-atomic&quot; 
     * geo-object. (I.e. an geo-object that is part of a &quot;collection&quot;, 
     * i.e. a <tt>VgFeature</tt> consisting of > 1 features. Thus, in the scope
     * of this framework, a feature itself might be given by an &quot;array&quot;
     * of features (&quot;flat&quot; feature hierarchy).<br/>
     * <br/>
     * Note that the condition 0 &lt;= <i>i</i> &lt; <tt>this.numberOfSubFeatures()</tt> 
     * must hold; otherwise a <tt>T3dException</tt> will be thrown.
     *
     * @param i Index
     * @return Geo-object
     * @throws T3dException if an error occurs
     * @see {@link #isCollection()}
     */
    abstract public VgFeature getFeature(int i) throws T3dException;

    /**
     * returns the information whether the geo-object consists of more than one
     * geo-object (&quot;feature collection&quot;).
     *
     * @return <i>true</i>, if the geo-object consists of more than one sub-object, else <i>false</i>
     */
    abstract public boolean isCollection();

    /**
     * returns the information whether the geo-object consists of only one 
     * geo-object. Note that the assertion <i>obj.isCollection() == !obj.isAtomic()</i> 
     * always must hold.<br/>
     * <br/>
     * For an atomic feature, <tt>getFeature(0)</tt> will give the feature 
     * itself (<tt>this</tt>-object).
     *
     * @return <i>true</i>, if the object does not consist of > 1 objects, else <i>false</i>
     */
    public boolean isAtomic() {
        return !(this.isCollection());
    }

    /**
     * returns the number of elements of the feature collection. Note that for 
     * an atomic geo-object, the return-value will be 1.
     *
     * @return Number of elements 
     */
    abstract public int numberOfFeatures();

    /**
     * @deprecated (misleading method naming, should be &quot;numberOfFeatures&quot;) 
     * @see {@link numberOfFeatures}
     */
    abstract public int numberOfSubFeatures();

    abstract public String toString();
}