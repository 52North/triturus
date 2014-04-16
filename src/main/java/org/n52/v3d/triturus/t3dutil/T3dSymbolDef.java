package org.n52.v3d.triturus.t3dutil;

/**
 * Abstract base class for cartographic 3-d symbol definitions. To instantiate such symbols, the class
 * <tt>T3dSymbolInstance</tt> has to be used.<br />
 * Example: A billboard symbol showing a JPEG-bitmap can be specified as follows:
 * <pre>
 * T3dSymbolDef mySymbDef = new T3dVerticalRectangle();
 * mySymbDef.setTexture(".../myTexture.jpg"); 
 * T3dSymbolInstance mySymbInst1 = new T3dSymbolInstance(...);
 * </pre>
 *
 * @see T3dSymbolInstance
 * @author Benno Schmidt
 */
abstract public class T3dSymbolDef
{
	private String mTexture = "";
	
	/** 
	 * sets the image texture to be used for the symbol.<br />
	 * Note: Color definitions might be ignored for textured symbols.
     *
	 * @param pFile File name including file path
	 * @see T3dSymbolInstance#setColor
	 */
	public void setTexture(String pFile) {
		mTexture = pFile;
	}
	
	/** 
	 * get the image texture for the symbol.
     * Note: Color definitions might be ignored for textured symbols.
     *
	 * @return File name including file path
	 * @see T3dSymbolInstance#setColor
	 */
	public String getTexture() {
		return mTexture;
	}

}