/*
 * Created on 13.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.n52.v3d.triturus.t3dutil;

import java.io.File;

/**
 * @deprecated -> org.n52.v3d.triturus.t3dutil.operatingsystem
 * Hier werden einige benötigte Datei-Methoden vorgehalten
 * @author Martin May<br>
 * (c) 2003, Institute for Geoinformatics<br>
 */
public class FileTools {
	
	/**
	 * Die Methode gibt den Pfad als String mit abschließendem File-Separator zurück.<p>
	 * @return TempDir 
	 */
	public static String findTempDir() {
		/*
		 * Es ist anzumerken, dass System.getProperty("java.io.tmpdir")
		 * mal mit und mal ohne File-Separator am Ende geliefert wird.
		 * Dies kann über 'File' umgangen werden.
		 */
		File SysTemp = new File(System.getProperty("java.io.tmpdir"));
		String retVal;
		if(SysTemp.isDirectory() && SysTemp.canWrite()){
			retVal = SysTemp.toString() + System.getProperty("file.separator");
		} else {
			//TODO Hier kann sicher noch etwas mehr Intelligenz hinterlegt werden...
			retVal = "C:\\Temp\\";
		}
		System.out.println("Benutze temporäres Verzeichnis: " + retVal);
		return retVal;
		
	}
}
