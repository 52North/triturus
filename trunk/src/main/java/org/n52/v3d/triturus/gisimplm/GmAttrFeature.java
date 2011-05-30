package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgFeature;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/** 
 * Implementierung für attributierte Geometrien. Die Anzahl der thematischen Attribute der im Hauptspeicher
 * vorgehaltenen (atomaren) Objekte ist prinzipiell beliebig.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmAttrFeature extends VgAttrFeature
{
    private VgGeomObject mGeom = null;
    private ArrayList mAttrNames = new ArrayList();
    private ArrayList mAttrValues = new ArrayList();
    private ArrayList mAttrTypes = new ArrayList();
    
    /** Konstruktor. */
    public GmAttrFeature() {
    	mAttrNames.clear();
    	mAttrValues.clear();
    	mAttrTypes.clear();
    }

    /** 
     * liefert die Geometrie des Objekts.<p>
     * @return Geometrie oder <i>null</i>
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

    /** 
     * setzt die Geometrie des Objekts. Z. B. für ein Punkt-Feature:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.setGeometry( new GmPoint( 1000., 1500., 0. ) );
     * </pre>
     * @param pGeom Geometrie-Objekt
     */
    public void setGeometry(VgGeomObject pGeom) {
        mGeom = pGeom;
    }

    /** 
     * liefert das i-te Sub-Objekt des Geoobjekts. Da die betrachtete Implementierung nur atomare Objekte
     * berücksichtigt, wirft die Methode stets eine Ausnahme.<p>
     * @throws org.n52.v3d.triturus.core.T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
    	throw new T3dException("Tried to access sub-feature of atomic object.");
    }

    /** 
     * liefert Information, ob das Geoobjekt aus mehreren Geoobjekten zusammengesetzt ist. Da
     * <tt>GmAttrFeature</tt>-Objekte atomar sind, liefert diese Methode stets <i>false</i> als Ergebnis.<p>
     * @return false
     */
    public boolean isCollection() {
    	return false;
    }

    /**
     * liefert die Anzahl der Sub-Objekte des Objekts. Da <tt>GmAttrFeature</tt>-Objekte atomar sind, liefert diese
     * Methode stets den Wert 0.<p>
     * @return 0
     */
    public int numberOfSubFeatures() {
        return 0;
    }

    /** 
     * definiert ein thematisches Attribut. Neben dem Namen des zu definierenden Attributs ist der Typ für die
     * Attributwerte anzugeben. Falls das Attribut bereits existiert, wird eine Ausnahme geworfen.<p>
     * @param pAttrName Name des Attributs
     * @param pAttrType Typ des Attributs (Java-Klassenname)
     * @throws T3dException
     */
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
     * definiert ein thematisches Attribut. Neben dem Namen des zu definierenden Attributs sind der Typ und der
     * Initialwert des Attributs anzugeben. Falls das Attribut bereits existiert, wird eine Ausnahme geworfen.<p>
     * Beispiel:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute( "FEATURE_ID", "java.lang.String", "p1545" );
     * </pre><p>
     * @param pAttrName Name des Attributs
     * @param pAttrType Typ des Attributs (Java-Klassenname)
     * @param pVal Wert des Attributs
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

    /** 
     * liefert die Bezeichnungen der thematischen Attribute des Objekts.<p>
     * @return Liste von Strings
     */
    public String[] getAttributeNames() {
    	String[] lTmp = new String[ mAttrNames.size() ];
    	for (int i = 0; i < mAttrNames.size(); i++)
    	    lTmp[i] = (String) mAttrNames.get(i);
    	return lTmp;
    }

    /** 
     * prüft das Vorhandensein eines thematischen Attributs.<p>
     * @param pAttrName Name des Attributs
     * @return <i>true</i>, falls Attribut definiert
     */
    public boolean hasAttribute(String pAttrName) 
    {
    	return (this.internalAttributePos( pAttrName ) != 0);
    }

    private int internalAttributePos( String pAttrName ) {
    	for (int i = 0; i < mAttrNames.size(); i++) {
    	    if (((String) mAttrNames.get( i )).equalsIgnoreCase( pAttrName ))
    	        return i;
    	}
    	return 0;
    }
    	     
    /** 
     * liefert den Wert eines thematischen Attributs. Falls das angegebene Attribut nicht definiert ist, wird eine
     * <tt>T3dException</tt> geworfen.<p>
     * Beispiel für die Abfrage eines String-wertigen Attributs:
     * <pre>
     * String val = myFeature.getAttributeValue( "FEATURE_ID" );
     * System.out.println("Der Wert des Attributs \"FEATURE_ID\" ist: " + val );
     * </pre><p>
     * @param pAttrName Name des abgefragten Attributs
     * @return Objekt vom Typ des abgefragten Attributs
     * @throws T3dException
     */
    public Object getAttributeValue(String pAttrName) throws T3dException
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i <= 0)
    	    throw new T3dException("Tried to access non-present attribute \"" + pAttrName + "\".");
    	// else:
    	return mAttrValues.get(i);
    }    	

    /** 
     * setzt den Wert eines thematischen Attributs. Falls das Attribut nicht definiert ist oder die Objekttypen nicht
     * aufeinander abgebildet werden können, wird eine <tt>T3dException</tt> geworfen.<p>
     * Beispiel:
     * <pre>
     * GmAttrFeature myFeature = new GmAttrFeature();
     * myFeature.addAttribute( "FEATURE_ID", "java.lang.String" );
     * myFeature.setAttributeValue( "FEATURE_ID", "p1546" );
     * </pre><p>
     * @param pAttrName Name des Attributs, für das der Wert gesetzt werden soll.
     * @param pVal zu setzender Wert
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

    /** 
     * liefert den Typ eines thematischen Attributs. Falls das Attribut nicht definiert ist, wird eine
     * <tt>T3dException</tt> geworfen.<p>
     * @param pAttrName Name des Attributs
     * @return Typ (Java-Klassenname)
     * @throws T3dException
     */
    public String getAttributeType(String pAttrName) 
    {
    	int i = this.internalAttributePos(pAttrName);
    	if (i <= 0)
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
