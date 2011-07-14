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
 * todo engl. doc
 * Klasse zur Definition eines Quader-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt
 */
public class T3dBox extends T3dSymbolDef
{
	private double mSizeX = 1.;
	private double mSizeY = 1.;
	private double mSizeZ = 1.;
	
	/** 
	 * setzt die Kantenl�ngen des Quaders.<p>
	 * @param pSizeX Kantenl�nge in x-Richtung
	 * @param pSizeY Kantenl�nge in y-Richtung
	 * @param pSizeZ Kantenl�nge in z-Richtung
	 */
	public void setSize(double pSizeX, double pSizeY, double pSizeZ) {
		mSizeX = pSizeX;
		mSizeY = pSizeY;
		mSizeZ = pSizeZ;
	}
	
	/**
	 * liefert die Kantenl�nge in x-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/**
	 * liefert die Kantenl�nge in y-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeY() {
		return mSizeY;
	}

	/**
	 * liefert die Kantenl�nge in z-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeZ() {
		return mSizeZ;
	}
}