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
	 * setzt die Kantenl�ngen des Quaders.<p>
	 * @param pSizeX Kantenl�nge in x-Richtung
	 * @param pSizeY Kantenl�nge in y-Richtung
	 * @param pSizeZ Kantenl�nge in z-Richtung
	 */
	public void setSize(double pSizeX, double pSizeY, double pSizeZ) {
		mSizeX = pSizeX;
		mSizeY = pSizeY;
		mSizeZ = pSizeZ;
	}
	
	/**
	 * liefert die Kantenl�nge in x-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeX() {
		return mSizeX;
	}

	/**
	 * liefert die Kantenl�nge in y-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeY() {
		return mSizeY;
	}

	/**
	 * liefert die Kantenl�nge in z-Richtung.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSizeZ() {
		return mSizeZ;
	}
}