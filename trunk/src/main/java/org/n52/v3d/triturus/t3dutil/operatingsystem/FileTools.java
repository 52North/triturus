package org.n52.v3d.triturus.t3dutil.operatingsystem;

import java.io.File;

/**
 * Klasse mit verschiedenen Hilfsmethoden für die Arbeit mit Dateien.<p>
 * @author Martin May, Benno Schmidt<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class FileTools
{
	/**
	 * liefert das über die Java-Property <tt>java.io.tmpdir</tt> konfigurierte Temporärverzeichnis. Die Methode gibt
     * den Pfad als String mit abschließendem File-Separator zurück.<p>
	 * @return Pfadangabe
	 */
	static public String findTempDir()
    {
		// Es ist anzumerken, dass System.getProperty("java.io.tmpdir") mal mit und mal ohne File-Separator
        // am Ende geliefert wird. Dies kann über 'File' umgangen werden.

        File SysTemp = new File(System.getProperty("java.io.tmpdir"));
		String retVal;

		if (SysTemp.isDirectory() && SysTemp.canWrite())
			retVal = SysTemp.toString() + System.getProperty("file.separator");
		else
			retVal = "C:\\Temp\\"; // TODO: Hier kann sicher noch etwas mehr Intelligenz hinterlegt werden...

        System.out.println("Benutze temporäres Verzeichnis: " + retVal);
		return retVal;
	}

    /**
     * liefert die Extension einer Pfadangabe oder eines URLs. Falls keine Extension ermittelt werden kann, wird ein
     * Leerstring zurückgegeben.<p>
     * Beispiele: Für die Eingabe &quot;C:/temp/beispiel.jpg&quot; liefert die Methode als Resultat die Zeichenkette
     * &quot;jpg&quot, für &quot;http://localhost/test/beispiel.png&quot; die Zeichenkette &quot;png&quot.<p>
     * @param pFilename Pfadangabe oder URL
     * @return Extension (ohne '.') oder Leerstring
     */
    static public String getExtension(String pFilename)
    {
        if (pFilename == null || pFilename.length() <= 0)
            return "";

        int pos = pFilename.lastIndexOf(".");
        if (pos < 0) {
            // pFilename enthält keinen '.'
            return "";
        }

        // pFilename enthält mindestens einen '.'
        if (pFilename.length() - pos - 1 <= 0)
            return ""; // da keine Zeichen hinter '.' vorhanden
        else
            return pFilename.substring(pos + 1, pFilename.length());
    }
}
