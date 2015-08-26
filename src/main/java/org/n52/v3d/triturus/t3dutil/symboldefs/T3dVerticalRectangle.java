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
package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Definition of a rectangular (cartographic) symbol that is oriented parallel to the z-axis. To instantiate a concrete
 * symbol, the class <tt>T3dSymbolInstance</tt> has to be used.
 *
 * @see org.n52.v3d.triturus.t3dutil.T3dSymbolInstance
 * @author Benno Schmidt
 */
public class T3dVerticalRectangle extends T3dSymbolDef
{
	private double mSizeX = 1.;
	private double mSizeZ = 1.;
	
	/** 
	 * sets the rectangle's extent with regard to the xy-plane.
     *
	 * @param pSize horizontal extent
	 */
	public void setSizeX(double pSize) {
		mSizeX = pSize;
	}

	/**
     * gets the rectangle's extent with regard to the xy-plane.
     *
	 * @return horizontal extent
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/** 
     * sets the rectangle's extent with regard to the z-axis.
     *
	 * @param pSize vertical extent
	 */
	public void setSizeZ(double pSize) {
		mSizeZ = pSize;
	}

	/**
     * gets the rectangle's extent with regard to the z-axis.
     *
	 * @return vertical extent
	 */
	public double getSizeZ() {
		return mSizeZ;
	}
}