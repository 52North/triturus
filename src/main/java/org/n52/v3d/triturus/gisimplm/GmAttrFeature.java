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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgFeature;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Implementation for attributed geometric objects.<br /><br />
 * <i>German: </i> Implementierung f&uuml;r attributierte Geometrien. Die Anzahl der thematischen Attribute der im
 * Hauptspeicher vorgehaltenen (atomaren) Objekte ist prinzipiell beliebig.
 * @author Benno Schmidt
 */
public class GmAttrFeature extends VgAttrFeature
{
    private VgGeomObject mGeom = null;
    private ArrayList mAttrNames = new ArrayList();
    private ArrayList mAttrValues = new ArrayList();
    private ArrayList mAttrTypes = new ArrayList();
    
    /**
     * Constructor.
     */
    public GmAttrFeature() {
    	mAttrNames.clear();
    	mAttrValues.clear();
    	mAttrTypes.clear();
    }

	/**
     * returns the object's geometry.
     * @return Object geometry or <i>null</i>
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

    /** 
     * assigns a geometry to the object. E.g. for a point feature:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.setGeometry(new GmPoint(1000., 1500., 0.));
     * </pre>
     * @param pGeom geometric object
     */
    public void setGeometry(VgGeomObject pGeom) {
        mGeom = pGeom;
    }

    public VgFeature getFeature(int i) throws T3dException
    {
    	throw new T3dException("Tried to access sub-feature of atomic object.");
    }

    public boolean isCollection() {
    	return false;
    }

    public int numberOfSubFeatures() {
        return 0;
    }

    public void addAttribute(String pAttrName, String pAttrType) throws T3dException
    {
    	if (this.hasAttribute(pAttrName))
    	    throw new T3dException("The attribute \"" + pAttrName + "\" is already present.");
    	// else:    
    	mAttrNames.add(pAttrName);
    	mAttrTypes.add(pAttrType);
    	mAttrValues.add("empty");
    }

	/**
	 * defines a thematic attribute.<br /><br />
     * <i>German:</i> definiert ein thematisches Attribut. Neben dem Namen des zu definierenden Attributs sind der Typ
     * und der Initialwert des Attributs anzugeben. Falls das Attribut bereits existiert, wird eine Ausnahme geworfen.
     * <br />
     * Beispiel:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute("FEATURE_ID", "java.lang.String", "p1545");
     * </pre><p>
	 * @param pAttrName Attribute name
	 * @param pAttrType Attribute type as Java class-name
     * @param pVal Attribute value
     * @throws T3dException 
     */
    public void addAttribute(String pAttrName, String pAttrType, Object pVal) throws T3dException
    {
    	if (this.hasAttribute(pAttrName))
    	    throw new T3dException("The attribute \"" + pAttrName + "\" is already present.");
    	// else:    
    	mAttrNames.add(pAttrName);
    	mAttrTypes.add(pAttrType);
    	mAttrValues.add(pVal);
    }

    public String[] getAttributeNames() {
    	String[] lTmp = new String[ mAttrNames.size() ];
    	for (int i = 0; i < mAttrNames.size(); i++)
    	    lTmp[i] = (String) mAttrNames.get(i);
    	return lTmp;
    }

    /** 
     * checks if the given attribute has been defined.
     * @param pAttrName Attribute name
     * @return <i>true</i> if an attribute has been defined
     */
    public boolean hasAttribute(String pAttrName) 
    {
    	return (this.internalAttributePos(pAttrName) >= 0);
    }

    private int internalAttributePos(String pAttrName) {
    	for (int i = 0; i < mAttrNames.size(); i++) {
    	    if (((String) mAttrNames.get(i)).equalsIgnoreCase( pAttrName ))
    	        return i;
    	}
    	return -1;
    }
    	     
	/**
	 * return a thematic attribute's value. If the given attribute is not defined, a <tt>T3dException</tt> will be
     * thrown.<br /><br />
     * <i>German:</i> liefert den Wert eines thematischen Attributs. Falls das angegebene Attribut nicht definiert ist,
     * wird eine <tt>T3dException</tt> geworfen.<br />
     * Beispiel f&uuml;r die Abfrage eines String-wertigen Attributs:
     * <pre>
     * String val = myFeature.getAttributeValue( "FEATURE_ID" );
     * System.out.println("Der Wert des Attributs \"FEATURE_ID\" ist: " + val );
     * </pre>
	 * @param pAttrName Name of the queried attribute
	 * @return Object of type of the queried attribute
	 * @throws T3dException
	 */
    public Object getAttributeValue(String pAttrName) throws T3dException
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i < 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	return mAttrValues.get(i);
    }    	

    /**
     * sets a thematic attribute's value. If the attribute has not been defined, or the given object types can not be
     * mapped on each other, a <tt>T3dException</tt> will be thrown.<br /><br />
     * <i>German:</i> setzt den Wert eines thematischen Attributs. Falls das Attribut nicht definiert ist oder die
     * Objekttypen nicht aufeinander abgebildet werden k&ouml;nnen, wird eine <tt>T3dException</tt> geworfen.<br />
     * Beispiel:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute("FEATURE_ID", "java.lang.String");
     * myFeature.setAttributeValue("FEATURE_ID", "p1546");
     * </pre><br />
     * @param pAttrName Attribute name
     * @param pVal Value to be set
     * @throws T3dException
     */
    public void setAttributeValue(String pAttrName, Object pVal) throws T3dException
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i <= 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	mAttrValues.set(i, pVal);
    }

    public String getAttributeType(String pAttrName)
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i < 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	return (String) mAttrTypes.get(i);
    }

    public String toString() {
        String strGeom = "<empty geometry>";
        if (mGeom != null)
            strGeom = mGeom.toString(); 
        String lRet = "[{";
    	for (int i = 0; i < mAttrNames.size(); i++)
    	    lRet = lRet + "(" + (String) mAttrNames.get( i ) + ": " + mAttrValues.get( i ) + ")";
        lRet = lRet + "}, ";
        lRet = lRet + strGeom + "]";
        return lRet;
    }
}
