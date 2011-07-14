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
package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Zylinder-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt
 * todo engl. doc
 */
public class T3dCylinder extends T3dSymbolDef
{
	private double mRadius = 1.;
	private double mHeight = 1.;
	
	/** 
	 * setzt den Zylinderradius.<p>
	 * @param pRadius Radius
	 */
	public void setRadius(double pRadius) {
		mRadius = pRadius;
	}

	/**
	 * liefert den Zylinderradius.<p>
	 * @return gesetzter Radius
	 */
	public double getRadius() {
		return mRadius;
	}
	
	/** 
	 * setzt die H�he des Zylinders.<p>
	 * @param pHeight H�he
	 */
	public void setHeight(double pHeight) {
		mHeight = pHeight;
	}

	/**
	 * liefert die H�he des Zylinders.<p>
	 * @return gesetzte H�he
	 */
	public double getHeight() {
		return mHeight;
	}
}