package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Klasse zur Verwaltung von Instanzen kartografischer 3D-Symbole. Für die Instanziierung wird eine Symbol-Definition
 * benötigt.<p>
 * @see T3dSymbolDef
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dSymbolInstance
{
	private T3dSymbolDef mSymbol = null;
	private VgPoint mPos = null;
	private double mScale = 1.;
	private double mAngleXY = 0.;
	private double mAngleZ = 0.;
	private T3dColor mColor = null;

	/**
	 * Konstruktor.<p>
	 * @param pSymbol Symbol-Definition
	 * @param pPos georefenzierter Einfügepunkt
	 * @see T3dSymbolInstance#setSymbol
	 * @see T3dSymbolInstance#setPosition
	 */
	public T3dSymbolInstance(T3dSymbolDef pSymbol, VgPoint pPos) {
		this.setSymbol(pSymbol);
		this.setPosition(pPos);
	} 		
	
	/** 
	 * setzt das zu verwendende Symbol.<p>
	 * @param pSymbol Symbol-Definition
	 */
	public void setSymbol(T3dSymbolDef pSymbol) {
		mSymbol = pSymbol;
	} 		
	
	/** 
	 * liefert die gesetzte Symboldefinition.<p>
	 * @return Symbol-Definition
	 */
	public T3dSymbolDef getSymbol() {
		return mSymbol;
	} 		

	/** 
	 * setzt die Position, an der das Symbol in die Szene eingefügt werden soll.<p>
	 * @param pPos georefenzierter Einfügepunkt
	 */
	public void setPosition(VgPoint pPos) {
		mPos = pPos;
	} 		
	
	/** 
	 * liefert die Position, an der Symbol eingefügt werden soll.<p>
	 * @return georefenzierter Einfügepunkt
	 */
	public VgPoint getPosition() {
		return mPos;
	} 		

	/** 
	 * setzt die Symbolgröße. Voreinstellungsgemäß ist der Wert 1 gesetzt.<p>
	 * @param pScale Symbolgröße
	 */
	public void setScale(double pScale) {
		mScale = pScale;
	} 		
	
	/** 
	 * liefert die Symbolgröße.<p>
	 * @return Symbolgröße
	 */
	public double getScale() {
		return mScale;
	} 		

	/** 
	 * setzt den Drehwinkel in der xy-Ebene, unter dem das Symbol eingefügt werden soll. Voreinstellungsgemäß ist 
	 * der Wert 0 gesetzt.<p>
	 * Der Winkel ist entgegen dem Uhrzeigersinn im Bogenmaß anzugeben ("mathematische Angabe"). Ein Winkel von 0 
	 * entspricht dabei der x-Richtung des zugrunde liegenden räumlichen Bezugssystems (also der östlichen Richtung 
	 * bei Verwendung des Gauß-Krüger-Systems). Die positive y-Richtung entspricht einem Winkel von PI/2 (nördliche
	 * Richtung in Gauß-Krüger-System).<p>
	 * @param pAngleXY Drehwinkel im Bogenmaß
	 * @see org.n52.v3d.triturus.vgis.VgGeomObject
	 */
	public void setAngleXY(double pAngleXY) {
		mAngleXY = pAngleXY;
	} 		
	
	/** 
	 * liefert den gesetzten Drehwinkel in der xy-Ebene.<p>
	 * @return Drehwinkel im Bogenmaß
	 */
	public double getAngleXY() {
		return mAngleXY;
	} 		

	/** 
	 * setzt den Drehwinkel gegenüber der z-Achse, unter dem das Symbol eingefügt werden soll. Voreinstellungsgemäß 
	 * ist der Wert 0 gesetzt.<p>
	 * @param pAngleZ Drehwinkel im Bogenmaß
	 */
	public void setAngleZ(double pAngleZ) {
		mAngleZ = pAngleZ;
	} 		
	
	/** 
	 * liefert den gesetzten Drehwinkel gegenüber der z-Achse.<p>
	 * @return Drehwinkel im Bogenmaß
	 */
	public double getAngleZ() {
		return mAngleZ;
	} 		

	/** 
	 * setzt die Symbolfarbe. Voreinstellungsgemäß ist keine Farbangabe gesetzt.<p>
	 * Bem.: Diese Angabe wird nur für spezielle Symbol-Definitionen berücksichtigt.<p>
	 * @param pColor Farbangabe
	 */
	public void setColor(T3dColor pColor) {
		mColor = pColor;
	} 		
	
	/** 
	 * liefert die gesetzte Symbolfarbe.<p>
	 * @return Farbangabe
	 */
	public T3dColor getColor() {
		return mColor;
	} 		
}