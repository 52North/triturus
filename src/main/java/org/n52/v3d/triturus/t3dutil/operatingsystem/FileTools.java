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
package org.n52.v3d.triturus.t3dutil.operatingsystem;

import java.io.File;

/**
 * Class holding various helper methods to work with files.<br /><br />
 * <i>German:</i> Klasse mit verschiedenen Hilfsmethoden f&uuml;r die Arbeit mit Dateien.
 * @author Martin May, Benno Schmidt
 */
public class FileTools
{
	/**
     * todo engl. JavaDoc
	 * liefert das �ber die Java-Property <tt>java.io.tmpdir</tt> konfigurierte Tempor�rverzeichnis. Die Methode gibt
     * den Pfad als String mit abschlie�endem File-Separator zur�ck.<p>
	 * @return Pfadangabe
	 */
	static public String findTempDir()
    {
		// Es ist anzumerken, dass System.getProperty("java.io.tmpdir") mal mit und mal ohne File-Separator
        // am Ende geliefert wird. Dies kann �ber 'File' umgangen werden.

        File SysTemp = new File(System.getProperty("java.io.tmpdir"));
		String retVal;

		if (SysTemp.isDirectory() && SysTemp.canWrite())
			retVal = SysTemp.toString() + System.getProperty("file.separator");
		else
			retVal = "C:\\Temp\\"; // TODO: Hier kann sicher noch etwas mehr Intelligenz hinterlegt werden...

        System.out.println("Benutze tempor�res Verzeichnis: " + retVal);
		return retVal;
	}

    /**
     * todo engl. JavaDoc
     * liefert die Extension einer Pfadangabe oder eines URLs. Falls keine Extension ermittelt werden kann, wird ein
     * Leerstring zur�ckgegeben.<p>
     * Beispiele: F�r die Eingabe &quot;C:/temp/beispiel.jpg&quot; liefert die Methode als Resultat die Zeichenkette
     * &quot;jpg&quot, f�r &quot;http://localhost/test/beispiel.png&quot; die Zeichenkette &quot;png&quot.<p>
     * @param pFilename Pfadangabe oder URL
     * @return Extension (ohne '.') oder Leerstring
     */
    static public String getExtension(String pFilename)
    {
        if (pFilename == null || pFilename.length() <= 0)
            return "";

        int pos = pFilename.lastIndexOf(".");
        if (pos < 0) {
            // pFilename enth�lt keinen '.'
            return "";
        }

        // pFilename enth�lt mindestens einen '.'
        if (pFilename.length() - pos - 1 <= 0)
            return ""; // da keine Zeichen hinter '.' vorhanden
        else
            return pFilename.substring(pos + 1, pFilename.length());
    }
}
