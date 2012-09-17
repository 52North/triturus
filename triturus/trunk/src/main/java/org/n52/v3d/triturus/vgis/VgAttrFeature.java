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
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to manage geo-objects that consist of (thematic) attributes.
 * @author Benno Schmidt
 */
abstract public class VgAttrFeature extends VgFeature 
{
	/**
	 * defines a thematic attribute. The defining attribute's name have to be given as well as the attribute value's
     * type.
	 * @param pAttrName Attribute name
	 * @param pAttrType Attribute type as Java class-name
	 */
	abstract public void addAttribute(String pAttrName, String pAttrType);

	/**
	 * returns the names of the thematic attributes of an geo-object.
	 * @return List of strings
	 */
	abstract public String[] getAttributeNames();

	/**
	 * return a thematic attribute's value. If the given attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.
	 * @param pAttrName Name of the queried attribute
	 * @return Object of type of the queried attribute
	 */
	abstract public Object getAttributeValue(String pAttrName);

	/**
	 * sets a thematic attribute's value. If the attribute has not been defined, or the given object types can not be
     * mapped on each other, a <tt>T3dException</tt> will be thrown.
	 * @param pAttrName Attribute name
	 * @param pVal Value to be set
	 * @throws T3dException
	 */
	abstract public void setAttributeValue(String pAttrName, Object pVal) throws T3dException;

	/**
	 * returns the type of a thematic attribute. If the attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.
	 * @param pAttrName Attribute name
	 * @return Attribute type as Java class-name
	 */
	abstract public String getAttributeType(String pAttrName);
}
