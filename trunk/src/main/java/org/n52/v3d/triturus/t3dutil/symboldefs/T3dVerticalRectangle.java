package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Rechteck-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dVerticalRectangle extends T3dSymbolDef
{
	private double mSizeX = 1.;
	private double mSizeZ = 1.;
	
	/** 
	 * setzt den Ausdehnung in der xy-Ebene.<p>
	 * @param pSize Ausdehnung
	 */
	public void setSizeX(double pSize) {
		mSizeX = pSize;
	}

	/**
	 * liefert die Ausdehnung in der xy-Ebene.<p>
	 * @return gesetzte Ausdehnung
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/** 
	 * setzt den Ausdehnung in z-Richtung.<p>
	 * @param pSize Ausdehnung
	 */
	public void setSizeZ(double pSize) {
		mSizeZ = pSize;
	}

	/**
	 * liefert die Ausdehnung in z-Richtung.<p>
	 * @return gesetzte Ausdehnung
	 */
	public double getSizeZ() {
		return mSizeZ;
	}
}