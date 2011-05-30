package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Rechteck-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dHorizontalRectangle extends T3dSymbolDef
{
	private double mSizeX = 1.;
	private double mSizeY = 1.;
	
	/** 
	 * setzt den Ausdehnung in x-Richtung.<p>
	 * @param pSize Ausdehnung
	 */
	public void setSizeX(double pSize) {
		mSizeX = pSize;
	}

	/**
	 * liefert die Ausdehnung in x-Richtung.<p>
	 * @return gesetzte Ausdehnung
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/** 
	 * setzt den Ausdehnung in y-Richtung.<p>
	 * @param pSize Ausdehnung
	 */
	public void setSizeY(double pSize) {
		mSizeY = pSize;
	}

	/**
	 * liefert die Ausdehnung in y-Richtung.<p>
	 * @return gesetzte Ausdehnung
	 */
	public double getSizeY() {
		return mSizeY;
	}
}