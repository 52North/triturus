package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines W�rfel-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dCube extends T3dSymbolDef
{
	private double mSize = 1.;
	
	/** 
	 * setzt die Kantenl�nge des W�rfels.<p>
	 * @param pSize Kantenl�nge
	 */
	public void setSize(double pSize) {
		mSize = pSize;
	}

	/**
	 * liefert die Kantenl�nge.<p>
	 * @return gesetzte Kantenl�nge
	 */
	public double getSize() {
		return mSize;
	}
	
}