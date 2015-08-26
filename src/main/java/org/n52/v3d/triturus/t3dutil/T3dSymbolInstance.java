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

import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Klasse zur Verwaltung von Instanzen kartografischer 3D-Symbole. F�r die Instanziierung wird eine Symbol-Definition
 * ben�tigt.<p>
 * @see T3dSymbolDef
 * @author Benno Schmidt
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
	 * @param pPos georefenzierter Einf�gepunkt
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
	 * setzt die Position, an der das Symbol in die Szene eingef�gt werden soll.<p>
	 * @param pPos georefenzierter Einf�gepunkt
	 */
	public void setPosition(VgPoint pPos) {
		mPos = pPos;
	} 		
	
	/** 
	 * liefert die Position, an der Symbol eingef�gt werden soll.<p>
	 * @return georefenzierter Einf�gepunkt
	 */
	public VgPoint getPosition() {
		return mPos;
	} 		

	/** 
	 * setzt die Symbolgr��e. Voreinstellungsgem�� ist der Wert 1 gesetzt.<p>
	 * @param pScale Symbolgr��e
	 */
	public void setScale(double pScale) {
		mScale = pScale;
	} 		
	
	/** 
	 * liefert die Symbolgr��e.<p>
	 * @return Symbolgr��e
	 */
	public double getScale() {
		return mScale;
	} 		

	/** 
	 * setzt den Drehwinkel in der xy-Ebene, unter dem das Symbol eingef�gt werden soll. Voreinstellungsgem�� ist 
	 * der Wert 0 gesetzt.<p>
	 * Der Winkel ist entgegen dem Uhrzeigersinn im Bogenma� anzugeben ("mathematische Angabe"). Ein Winkel von 0 
	 * entspricht dabei der x-Richtung des zugrunde liegenden r�umlichen Bezugssystems (also der �stlichen Richtung 
	 * bei Verwendung des Gau�-Kr�ger-Systems). Die positive y-Richtung entspricht einem Winkel von PI/2 (n�rdliche
	 * Richtung in Gau�-Kr�ger-System).<p>
	 * @param pAngleXY Drehwinkel im Bogenma�
	 * @see org.n52.v3d.triturus.vgis.VgGeomObject
	 */
	public void setAngleXY(double pAngleXY) {
		mAngleXY = pAngleXY;
	} 		
	
	/** 
	 * liefert den gesetzten Drehwinkel in der xy-Ebene.<p>
	 * @return Drehwinkel im Bogenma�
	 */
	public double getAngleXY() {
		return mAngleXY;
	} 		

	/** 
	 * setzt den Drehwinkel gegen�ber der z-Achse, unter dem das Symbol eingef�gt werden soll. Voreinstellungsgem�� 
	 * ist der Wert 0 gesetzt.<p>
	 * @param pAngleZ Drehwinkel im Bogenma�
	 */
	public void setAngleZ(double pAngleZ) {
		mAngleZ = pAngleZ;
	} 		
	
	/** 
	 * liefert den gesetzten Drehwinkel gegen�ber der z-Achse.<p>
	 * @return Drehwinkel im Bogenma�
	 */
	public double getAngleZ() {
		return mAngleZ;
	} 		

	/** 
	 * setzt die Symbolfarbe. Voreinstellungsgem�� ist keine Farbangabe gesetzt.<p>
	 * Bem.: Diese Angabe wird nur f�r spezielle Symbol-Definitionen ber�cksichtigt.<p>
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