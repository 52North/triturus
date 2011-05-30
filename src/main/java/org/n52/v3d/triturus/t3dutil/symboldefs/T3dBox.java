package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Quader-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dBox extends T3dSymbolDef
{
	private double mSizeX = 1.;
	private double mSizeY = 1.;
	private double mSizeZ = 1.;
	
	/** 
	 * setzt die Kantenlängen des Quaders.<p>
	 * @param pSizeX Kantenlänge in x-Richtung
	 * @param pSizeY Kantenlänge in y-Richtung
	 * @param pSizeZ Kantenlänge in z-Richtung
	 */
	public void setSize(double pSizeX, double pSizeY, double pSizeZ) {
		mSizeX = pSizeX;
		mSizeY = pSizeY;
		mSizeZ = pSizeZ;
	}
	
	/**
	 * liefert die Kantenlänge in x-Richtung.<p>
	 * @return gesetzte Kantenlänge
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/**
	 * liefert die Kantenlänge in y-Richtung.<p>
	 * @return gesetzte Kantenlänge
	 */
	public double getSizeY() {
		return mSizeY;
	}

	/**
	 * liefert die Kantenlänge in z-Richtung.<p>
	 * @return gesetzte Kantenlänge
	 */
	public double getSizeZ() {
		return mSizeZ;
	}
}