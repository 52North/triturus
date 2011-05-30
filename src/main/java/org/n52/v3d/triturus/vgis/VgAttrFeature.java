package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse für attributierte Geoobjekte.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgAttrFeature extends VgFeature 
{
	/**
	 * definiert ein thematisches Attribut. Neben dem Namen des zu definierenden Attributs ist der Typ für die
     * Attributwerte anzugeben.<p>
	 * @param pAttrName Name des Attributs
	 * @param pAttrType Typ des Attributs (Java-Klassenname)
	 */
	abstract public void addAttribute(String pAttrName, String pAttrType);

	/**
	 * liefert die Bezeichnungen der thematischen Attribute des Objekts.<p>
	 * @return Liste von Strings
	 */
	abstract public String[] getAttributeNames();

	/**
	 * liefert den Wert eines thematischen Attributs. Falls das angegebene Attribut nicht definiert ist, wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 * @param pAttrName Name des abgefragten Attributs
	 * @return Objekt vom Typ des abgefragten Attributs
	 * @throws T3dException
	 */
	abstract public Object getAttributeValue(String pAttrName);

	/**
	 * setzt den Wert eines thematischen Attributs. Falls das Attribut nicht definiert ist oder die Objekttypen nicht
     * aufeinander abgebildet werden können, wird eine <tt>T3dException</tt> geworfen.<p>
	 * @param pAttrName Name des Attributs, für das der Wert gesetzt werden soll
	 * @param pVal zu setzender Wert
	 * @throws T3dException
	 */
	abstract public void setAttributeValue(String pAttrName, Object pVal) throws T3dException;

	/**
	 * liefert den Typ eines thematischen Attributs. Falls das Attribut nicht definiert ist, wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 * @param pAttrName Name des Attributs
	 * @return Typ (Java-Klassenname)
	 * @throws T3dException
	 */
	abstract public String getAttributeType(String pAttrName);
}
