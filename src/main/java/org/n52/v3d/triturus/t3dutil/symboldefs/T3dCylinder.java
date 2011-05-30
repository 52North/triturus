package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Zylinder-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dCylinder extends T3dSymbolDef
{
	private double mRadius = 1.;
	private double mHeight = 1.;
	
	/** 
	 * setzt den Zylinderradius.<p>
	 * @param pRadius Radius
	 */
	public void setRadius(double pRadius) {
		mRadius = pRadius;
	}

	/**
	 * liefert den Zylinderradius.<p>
	 * @return gesetzter Radius
	 */
	public double getRadius() {
		return mRadius;
	}
	
	/** 
	 * setzt die Höhe des Zylinders.<p>
	 * @param pHeight Höhe
	 */
	public void setHeight(double pHeight) {
		mHeight = pHeight;
	}

	/**
	 * liefert die Höhe des Zylinders.<p>
	 * @return gesetzte Höhe
	 */
	public double getHeight() {
		return mHeight;
	}
}