package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Klasse zur Definition eines Kugel-Symbols. Zur Instanziierung konkreter Symbole ist die Klasse
 * <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dSphere extends T3dSymbolDef
{
	private double mRadius = 1.;
	
	/** 
	 * setzt den Kugelradius.<p>
	 * @param pRadius Radius
	 */
	public void setRadius(double pRadius) {
		mRadius = pRadius;
	}

	/**
	 * liefert den Kugelradius.<p>
	 * @return gesetzter Radius
	 */
	public double getRadius() {
		return mRadius;
	}
}