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
package org.n52.v3d.triturus.t3dutil;

/**
 * Abstract base class for cartographic 3-d symbol definitions. To instantiate such symbols, the class
 * <tt>T3dSymbolInstance</tt> has to be used.<br>
 * Example: A billboard symbol showing a JPEG-bitmap can be specified as follows:
 * <pre>
 * T3dSymbolDef mySymbDef = new T3dVerticalRectangle();
 * mySymbDef.setTexture(".../myTexture.jpg"); 
 * T3dSymbolInstance mySymbInst1 = new T3dSymbolInstance(...);
 * </pre>
 *
 * @see T3dSymbolInstance
 * @author Benno Schmidt
 */
abstract public class T3dSymbolDef
{
	private String mTexture = "";
	
	/** 
	 * sets the image texture to be used for the symbol.<br>
	 * Note: Color definitions might be ignored for textured symbols.
     *
	 * @param pFile File name including file path
	 * @see T3dSymbolInstance#setColor
	 */
	public void setTexture(String pFile) {
		mTexture = pFile;
	}
	
	/** 
	 * get the image texture for the symbol.
     * Note: Color definitions might be ignored for textured symbols.
     *
	 * @return File name including file path
	 * @see T3dSymbolInstance#setColor
	 */
	public String getTexture() {
		return mTexture;
	}

}