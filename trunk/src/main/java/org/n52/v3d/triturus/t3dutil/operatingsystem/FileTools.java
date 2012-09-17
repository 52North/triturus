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
