package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/**
 * Klasse zur Verwaltung einer Farbinformation.
 * <p>
 * @author Benno Schmidt, Torsten Heinen<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dColor
{
    private float mRed, mGreen, mBlue, mAlpha;
    private int mColMod = 1; // 1 = RGB, 2 = HSV
    
    /**
     * Konstruktor für eine Farbangabe im RGB-Modell.<p>
     * @param pRed Rot-Anteil als Wert zwischen 0 und 1
     * @param pGreen Grün-Anteil als Wert zwischen 0 und 1
     * @param pBlue Blau-Anteil als Wert zwischen 0 und 1
     */
    public T3dColor(float pRed, float pGreen, float pBlue) {
    	this(pRed, pGreen, pBlue, 1.f);
    }

    /** 
     * Konstruktor für eine Farbangabe im RGB-Modell mit Alpha-Wert.<p>
     * @param pRed Rot-Anteil als Wert zwischen 0 und 1
     * @param pGreen Grün-Anteil als Wert zwischen 0 und 1
     * @param pBlue Blau-Anteil als Wert zwischen 0 und 1
     * @param pAlpha Alpha-Wert zwischen 0 und 1
     */
    public T3dColor(float pRed, float pGreen, float pBlue, float pAlpha) {
    	mRed = pRed;
    	mGreen = pGreen;
    	mBlue = pBlue;
    	mAlpha = pAlpha;
    }

    /** 
     * Konstruktor. Der aktuelle Wert wird zunächst auf &quot;Weiß&quot; gesetzt.<p>
     */
    public T3dColor() {
    	this(1.f, 1.f, 1.f, 1.f);
    }

    /**
     * Konstruktor für eine Farbe im angegebenen Farbmodell. Die Angaben müssen jeweils auf den Wertebereich 0...1
     * bezogen sein.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert für Farbmodell "RGB", Hue-Wert (Farbton) für "HSV"
     * @param pVal2 Grün-Wert für Farbmodell "RGB", Sättigungs-Wert für "HSV"
     * @param pVal3 Blau-Wert für Farbmodell "RGB", V-Wert für "HSV"
     */
    public T3dColor(String pColorSystem, float pVal1, float pVal2, float pVal3) 
    {
        this(pColorSystem, pVal1, pVal2, pVal3, 1.f);
    }

    /**
     * Konstruktor für eine Farbe im angegebenen Farbmodell. Mit Ausnahme des Hue-Wertes müssen die Angaben jeweils auf
     * den Wertebereich 0...1 bezogen sein. Der Hue-Wert ist im Bogenmaß anzugeben (0...2*pi).<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert für Farbmodell "RGB", Hue-Wert (Farbton) für "HSV"
     * @param pVal2 Grün-Wert für Farbmodell "RGB", Sättigungs-Wert für "HSV"
     * @param pVal3 Blau-Wert für Farbmodell "RGB", V-Wert für "HSV"
     * @param pAlpha Alpha-Wert
     */
    public T3dColor(String pColorSystem, float pVal1, float pVal2, float pVal3, float pAlpha) 
    {
    	if (pColorSystem.equalsIgnoreCase("HSV")) 
    	{
    		mColMod = 2;
            float h = pVal1, s = pVal2, v = pVal3;
    	    if (s == 0) { // achromatisch/grau
	        	mRed = v; mGreen = v; mBlue = v;
            }
            else {
            	float sixth = (float) (Math.PI / 3.);
                h /= sixth; // Sektoren 0 bis 5
                int i = (int) h;
                float f = h - i; // faktorieller Teil von h
                float p = v * (1.f - s);
                float q = v * (1.f - s * f);
                float t = v * (1.f - s * (1.f - f));
                switch (i) {
                    case 0: mRed = v; mGreen = t; mBlue = p; break;
                    case 1: mRed = q; mGreen = v; mBlue = p; break;
                    case 2: mRed = p; mGreen = v; mBlue = t; break;
                    case 3: mRed = p; mGreen = q; mBlue = v; break;
                    case 4: mRed = t; mGreen = p; mBlue = v; break;
                    default: // case 5
                        mRed = v; mGreen = p; mBlue = q; break;
                }
            }
    	}
    	else {
    		mColMod = 1;
            mRed = pVal1;
            mGreen = pVal2;
            mBlue = pVal3;
    	}
        mAlpha = pAlpha;
    }

    /**      
     * setzt Rot-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Rot-Wert zwischen 0 und 1
     */
    public void setRed(float pVal) {
        mRed = pVal;
    }

    /**      
     * setzt Grün-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Grün-Wert zwischen 0 und 1
     */
    public void setGreen(float pVal) {
        mGreen = pVal;
    }

    /**      
     * setzt Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Blau-Wert zwischen 0 und 1
     */
    public void setBlue(float pVal) {
        mBlue = pVal;
    }
   
    /**      
     * liefert Rot-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Rot-Wert zwischen 0 und 1
     */
    public float getRed() {
        return mRed;
    }

    /**      
     * liefert Grün-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Grün-Wert zwischen 0 und 1
     */
    public float getGreen() {
        return mGreen;
    }

    /**      
     * liefert Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Blau-Wert zwischen 0 und 1
     */
    public float getBlue() {
        return mBlue;
    }

    /** 
     * setzt Opazität/Transparenz.<p> 
     * @param pVal 1.0 für Transparenz = 0% (voll-opak), 0.0 für Transparenz = 100%
     */
    public void setAlpha(float pVal) {
        mAlpha = pVal;
    }
 
    /** 
     * liefert Opazität/Transparenz.<p>
     * @return 1.0 für Transparenz = 0% (voll-opak), 0.0 für Transparenz = 100%
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**      
     * setzt Rot-, Grün- und Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pRed Rot-Wert zwischen 0 und 1
     * @param pGreen Rot-Wert zwischen 0 und 1
     * @param pBlue Rot-Wert zwischen 0 und 1
     */
    public void setRGB(float pRed, float pGreen, float pBlue) {
    	this.setRed(pRed);
    	this.setGreen(pGreen);
    	this.setBlue(pBlue);
    }

    /**      
     * setzt Rot-, Grün- und Blau-Anteil der Farbe bezogen auf RGB-Farbmodell und zusätzlich einen Alpha-Wert.<p>
     * @param pRed Rot-Wert zwischen 0 und 1
     * @param pGreen Rot-Wert zwischen 0 und 1
     * @param pBlue Rot-Wert zwischen 0 und 1
     * @param pAlpha 1.0 für Transparenz = 0 % (voll-opak), 0.0 für Transparenz = 100 %
     */
    public void setRGBA(float pRed, float pGreen, float pBlue, float pAlpha) {
    	this.setRGB(pRed, pGreen, pBlue);
    	this.setAlpha(pAlpha);
    }

    /**      
     * liefert Hue-Wert (Farbton) bezogen auf HSV-Farbmodell.<p>
     * Bem.: Für Grautöne (einschl. Schwarz und Weiß) ist der Sättigungswert 0 und der Hue-Wert undefiniert.<p>
     * @return Hue-Wert im Bogenmaß (0...2*pi)
     */
    public float getHue() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (delta <= 0.) // delta = 0 => Sättigung 0, H undefiniert
            return 0.f;
        float H;
        if (mRed == max)
            H = (mGreen - mBlue) / delta; // zwischen Gelb und Magenta
        else if (mGreen == max)
            H = 2.f + (mBlue - mRed) / delta; // zwischen Cyan und Gelb
        else
            H = 4.f + (mRed - mGreen) / delta; // zwischen Magenta und Cyan
        float sixth = (float) (Math.PI / 3.);
        H *= sixth; // Bogenmaß
        if (H < 0.f)
            H += 6. * sixth;
        return H;
    }

    /**      
     * liefert Sättigungswert bezogen auf HSV-Farbmodell.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @return Sättigungswert zwischen 0 und 1
     */
    public float getSaturation() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (max != 0.f)
            return (delta / max);
        // else: r = g = b = 0 => Sättigung = 0
        return 0.f;
    }

    /**      
     * liefert V-Wert bezogen auf HSV-Farbmodell.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @return V-Wert zwischen 0 und 1
     */
    public float getValue() 
    {
        return this.max(mRed, mGreen, mBlue);
    }

    /**
     * setzt die Farbe durch Angabe eines hexadezimal codiertes Wertes im Format <tt>0xRRGGBB</tt>. <tt>RR</tt>,
     * <tt>GG</tt> und <tt>BB</tt> liegen dabei jeweils im Bereich <tt>00 ... FF</tt>.<p>
     * Bem.: Dieses Format wird u. a. im OGC/ISO-Umfeld häufig verwendet (z. B. WMS- und WTS-Spezifikation).<p>
     * @param pHexVal hexadezimal codierte Farbangabe
     */
    public void setHexEncodedValue(String pHexVal) {
        String str = pHexVal.toLowerCase();
        if (!str.startsWith("0x"))
            throw new T3dException("Hexadecimal color encoding (" + pHexVal + ") requires '0x' prefix.");
        if (str.length() != 8)
            throw new T3dException("Hexadecimal color encoding (" + pHexVal + ") does not match '0xRRGGBB' format.");
        int red256 = 16 * this.hexDigit2Int(str.charAt(2)) + this.hexDigit2Int(str.charAt(3));
        int green256 = 16 * this.hexDigit2Int(str.charAt(4)) + this.hexDigit2Int(str.charAt(5));
        int blue256 = 16 * this.hexDigit2Int(str.charAt(6)) + this.hexDigit2Int(str.charAt(6));
        this.setRGB(((float)red256)/255.f, ((float)green256)/255.f, ((float)blue256)/255.f);
    }

    private int hexDigit2Int(char pDigit) {
        if (pDigit >= '0' && pDigit <= '9') return pDigit - '0';
        if (pDigit >= 'a' && pDigit <= 'f') return 10 + (pDigit - 'a');
        if (pDigit >= 'A' && pDigit <= 'F') return 10 + (pDigit - 'A');
        throw new T3dException("Unexpected hexadecimal character (" + pDigit + ").");
    }

    /**
     * liefert den Farbwert in hexadezimaler Codierung.<p>
     * Bem.: Der Alpha-Wert wird hierbei nicht berücksichtigt.<p>
     * @return hexadezimal codierte Farbangabe
     */
    public String getHexEncodedValue() {
        throw new T3dNotYetImplException("T3dColor#getHexEncodedValue is not implemented yet...");
    }

    /**
     * liefert Rot-Anteil der Farbe bezogen auf RGB-Farbmodell als ganzzahligen Wert im Bereich 0..255.<p>
     * @return gerundeter Rot-Wert zwischen 0 und 255
     */
    public int getRed256() 
    {
    	int r = (int) (256.f * this.getRed());
    	if (r >= 256) 
    		return 255;
    	else 
    		return r;
    }

    /**      
     * liefert Grün-Anteil der Farbe bezogen auf RGB-Farbmodell als ganzzahligen Wert im Bereich 0..255.<p>
     * @return gerundeter Grün-Wert zwischen 0 und 255
     */
    public int getGreen256() 
    {
    	int r = (int) (256.f * this.getGreen());
    	if (r >= 256) 
    		return 255;
    	else 
    		return r;
    }

    /**      
     * liefert Blau-Anteil der Farbe bezogen auf RGB-Farbmodell als ganzzahligen Wert im Bereich 0..255.<p>
     * @return gerundeter Blau-Wert zwischen 0 und 255
     */
    public int getBlue256() 
    {
    	int r = (int) (256.f * this.getBlue());
    	if (r >= 256) 
    		return 255;
    	else 
    		return r;
    }

    public String toString() {
    	if (mColMod == 1)
    		return "[R:" + mRed + ", G:" + mGreen + ", B:" + mBlue + ", A:" + mAlpha + "]";
    	else 
    		return "[H:" + getHue() + ", S:" + getSaturation() + ", V:" + getValue() + ", A:" + getAlpha() + "]";
    	
    }
    
    // Helfer:
    private float min(float x1, float x2, float x3) {
    	float lMin = x1;
    	if (x2 < lMin) lMin = x2; 
    	if (x3 < lMin) lMin = x3; 
        return lMin;
    }

    private float max(float x1, float x2, float x3) {
    	float lMax = x1;
    	if (x2 > lMax) lMax = x2; 
    	if (x3 > lMax) lMax = x3; 
        return lMax;
    }
}