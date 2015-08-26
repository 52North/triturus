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
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to manage geo-objects that consist of (thematic) attributes.
 *
 * @author Benno Schmidt
 */
abstract public class VgAttrFeature extends VgFeature 
{
	/**
	 * defines a thematic attribute. The name of the defining attribute has to be given as well as the attribute
     * value's type.
     *
	 * @param pAttrName Attribute name
	 * @param pAttrType Attribute type as Java class-name
	 */
	abstract public void addAttribute(String pAttrName, String pAttrType);

	/**
	 * returns the names of the thematic attributes of an geo-object.
     *
	 * @return List of strings
	 */
	abstract public String[] getAttributeNames();

	/**
	 * return a thematic attribute's value. If the given attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.
     *
	 * @param pAttrName Name of the queried attribute
	 * @return Object of type of the queried attribute
	 */
	abstract public Object getAttributeValue(String pAttrName);

	/**
	 * sets a thematic attribute's value. If the attribute has not been defined, or the given object types can not be
     * mapped on each other, a <tt>T3dException</tt> will be thrown.
     *
	 * @param pAttrName Attribute name
	 * @param pVal Value to be set
	 * @throws T3dException
	 */
	abstract public void setAttributeValue(String pAttrName, Object pVal) throws T3dException;

	/**
	 * returns the type of a thematic attribute. If the attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.
     *
	 * @param pAttrName Attribute name
	 * @return Attribute type as Java class-name
	 */
	abstract public String getAttributeType(String pAttrName);
}
