package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Würfel-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dCube extends T3dSymbolDef
{
	private double mSize = 1.;
	
	/** 
	 * setzt die Kantenlänge des Würfels.<p>
	 * @param pSize Kantenlänge
	 */
	public void setSize(double pSize) {
		mSize = pSize;
	}

	/**
	 * liefert die Kantenlänge.<p>
	 * @return gesetzte Kantenlänge
	 */
	public double getSize() {
		return mSize;
	}
	
}