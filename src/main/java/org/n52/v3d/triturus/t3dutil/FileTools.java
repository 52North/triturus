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
/*
 * Created on 13.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.n52.v3d.triturus.t3dutil;

import java.io.File;

/**
 * @deprecated See org.n52.v3d.triturus.t3dutil.operatingsystem
 * Hier werden einige ben�tigte Datei-Methoden vorgehalten
 * @author Martin May
 */
public class FileTools {
	
	/**
	 * Die Methode gibt den Pfad als String mit abschlie�endem File-Separator zur�ck.<p>
	 * @return TempDir 
	 */
	public static String findTempDir() {
		/*
		 * Es ist anzumerken, dass System.getProperty("java.io.tmpdir")
		 * mal mit und mal ohne File-Separator am Ende geliefert wird.
		 * Dies kann �ber 'File' umgangen werden.
		 */
		File SysTemp = new File(System.getProperty("java.io.tmpdir"));
		String retVal;
		if(SysTemp.isDirectory() && SysTemp.canWrite()){
			retVal = SysTemp.toString() + System.getProperty("file.separator");
		} else {
			//TODO Hier kann sicher noch etwas mehr Intelligenz hinterlegt werden...
			retVal = "C:\\Temp\\";
		}
		System.out.println("Benutze tempor�res Verzeichnis: " + retVal);
		return retVal;
		
	}
}
