package org.n52.v3d.triturus.t3dutil;

/**
 * Abstrakte Basisklasse zur Definition kartografischer 3D-Symbole. Zur Instanziierung konkreter Symbole ist die 
 * Klasse <tt>T3dSymbolInstance</tt> zu verwenden.<p>
 * Bsp.: Ein Billboard-Symbol mit JPEG-Bitmap lässt sich wie folgt definieren und instanziieren:
 * <pre>
 * T3dSymbolDef mySymbDef = new T3dVerticalRectangle();
 * mySymbDef.setTexture(".../myTexture.jpg"); 
 * T3dSymbolInstance mySymbInst1 = new T3dSymbolInstance(...);
 * </pre>
 * TODO: Das ist natürlich noch nicht ganz rund, da das ja auch noch irgendwie in die Szene muss; zeigt aber die
 * "Marschrichtung"... -> benno
 * @see T3dSymbolInstance
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class T3dSymbolDef
{
	private String mTexture = "";
	
	/** 
	 * setzt die für das Symbol zu verwendende Textur.<p>
	 * Bem.: Texturierte Symbole lassen sich nicht farbig instanziieren.<p>
	 * @param pFile Dateiname mit Pfadangabe
	 * @see T3dSymbolInstance#setColor
	 */
	public void setTexture(String pFile) {
		mTexture = pFile;
	}
	
	/** 
	 * liefert die für das Symbol gesetzte Textur.<p>
	 * Bem.: Texturierte Symbole lassen sich nicht farbig instanziieren.<p>
	 * @return Dateiname mit Pfadangabe
	 * @see T3dSymbolInstance#setColor
	 */
	public String getTexture() {
		return mTexture;
	}

}