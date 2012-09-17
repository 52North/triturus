/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/**
 * Class to manage color-information.
 * @author Benno Schmidt, Torsten Heinen
 */
public class T3dColor
{
    private float mRed, mGreen, mBlue, mAlpha;
    private int mColMod = 1; // 1 = RGB, 2 = HSV
    
    /**
     * Constructor for a color specification referring to the RGB-model.
     * @param pRed Red-portion as value between 0 and 1
     * @param pGreen Green-portion as value between 0 and 1
     * @param pBlue Blue-portion as value between 0 and 1
     */
    public T3dColor(float pRed, float pGreen, float pBlue) {
    	this(pRed, pGreen, pBlue, 1.f);
    }

    /** 
     * Constructor for a color specification referring to the RGB-model with an additional alpha-value.
     * @param pRed Red-portion as value between 0 and 1
     * @param pGreen Green-portion as value between 0 and 1
     * @param pBlue Blue-portion as value between 0 and 1
     * @param pAlpha Alpha-value as value between 0 and 1
     */
    public T3dColor(float pRed, float pGreen, float pBlue, float pAlpha) {
    	mRed = pRed;
    	mGreen = pGreen;
    	mBlue = pBlue;
    	mAlpha = pAlpha;
    }

    /** 
     * Constructor. The current color will be set to &quot;white&quot;.
     */
    public T3dColor() {
    	this(1.f, 1.f, 1.f, 1.f);
    }

    /**
     * todo engl. javaDoc
     * Konstruktor f�r eine Farbe im angegebenen Farbmodell. Die Angaben m�ssen jeweils auf den Wertebereich 0...1
     * bezogen sein.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert f�r Farbmodell "RGB", Hue-Wert (Farbton) f�r "HSV"
     * @param pVal2 Gr�n-Wert f�r Farbmodell "RGB", S�ttigungs-Wert f�r "HSV"
     * @param pVal3 Blau-Wert f�r Farbmodell "RGB", V-Wert f�r "HSV"
     */
    public T3dColor(String pColorSystem, float pVal1, float pVal2, float pVal3) 
    {
        this(pColorSystem, pVal1, pVal2, pVal3, 1.f);
    }

    /**
     * todo engl. javaDoc
     * Konstruktor f�r eine Farbe im angegebenen Farbmodell. Mit Ausnahme des Hue-Wertes m�ssen die Angaben jeweils auf
     * den Wertebereich 0...1 bezogen sein. Der Hue-Wert ist im Bogenma� anzugeben (0...2*pi).<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert f�r Farbmodell "RGB", Hue-Wert (Farbton) f�r "HSV"
     * @param pVal2 Gr�n-Wert f�r Farbmodell "RGB", S�ttigungs-Wert f�r "HSV"
     * @param pVal3 Blau-Wert f�r Farbmodell "RGB", V-Wert f�r "HSV"
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
     * todo engl. javaDoc
     * setzt Rot-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Rot-Wert zwischen 0 und 1
     */
    public void setRed(float pVal) {
        mRed = pVal;
    }

    /**      
     * todo engl. javaDoc
     * setzt Gr�n-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Gr�n-Wert zwischen 0 und 1
     */
    public void setGreen(float pVal) {
        mGreen = pVal;
    }

    /**      
     * todo engl. javaDoc
     * setzt Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @param pVal Blau-Wert zwischen 0 und 1
     */
    public void setBlue(float pVal) {
        mBlue = pVal;
    }
   
    /**      
     * todo engl. javaDoc
     * liefert Rot-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Rot-Wert zwischen 0 und 1
     */
    public float getRed() {
        return mRed;
    }

    /**      
     * todo engl. javaDoc
     * liefert Gr�n-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Gr�n-Wert zwischen 0 und 1
     */
    public float getGreen() {
        return mGreen;
    }

    /**      
     * todo engl. javaDoc
     * liefert Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
     * @return Blau-Wert zwischen 0 und 1
     */
    public float getBlue() {
        return mBlue;
    }

    /** 
     * todo engl. javaDoc
     * setzt Opazit�t/Transparenz.<p>
     * @param pVal 1.0 f�r Transparenz = 0% (voll-opak), 0.0 f�r Transparenz = 100%
     */
    public void setAlpha(float pVal) {
        mAlpha = pVal;
    }
 
    /** 
     * todo engl. javaDoc
     * liefert Opazit�t/Transparenz.<p>
     * @return 1.0 f�r Transparenz = 0% (voll-opak), 0.0 f�r Transparenz = 100%
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**      
     * todo engl. javaDoc
     * setzt Rot-, Gr�n- und Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.<p>
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
     * todo engl. javaDoc
     * setzt Rot-, Gr�n- und Blau-Anteil der Farbe bezogen auf RGB-Farbmodell und zus�tzlich einen Alpha-Wert.<p>
     * @param pRed Rot-Wert zwischen 0 und 1
     * @param pGreen Rot-Wert zwischen 0 und 1
     * @param pBlue Rot-Wert zwischen 0 und 1
     * @param pAlpha 1.0 f�r Transparenz = 0 % (voll-opak), 0.0 f�r Transparenz = 100 %
     */
    public void setRGBA(float pRed, float pGreen, float pBlue, float pAlpha) {
    	this.setRGB(pRed, pGreen, pBlue);
    	this.setAlpha(pAlpha);
    }

    /**      
     * todo engl. javaDoc
     * liefert Hue-Wert (Farbton) bezogen auf HSV-Farbmodell.<p>
     * Bem.: F�r Graut�ne (einschl. Schwarz und Wei�) ist der S�ttigungswert 0 und der Hue-Wert undefiniert.<p>
     * @return Hue-Wert im Bogenma� (0...2*pi)
     */
    public float getHue() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (delta <= 0.) // delta = 0 => S�ttigung 0, H undefiniert
            return 0.f;
        float H;
        if (mRed == max)
            H = (mGreen - mBlue) / delta; // zwischen Gelb und Magenta
        else if (mGreen == max)
            H = 2.f + (mBlue - mRed) / delta; // zwischen Cyan und Gelb
        else
            H = 4.f + (mRed - mGreen) / delta; // zwischen Magenta und Cyan
        float sixth = (float) (Math.PI / 3.);
        H *= sixth; // Bogenma�
        if (H < 0.f)
            H += 6. * sixth;
        return H;
    }

    /**      
     * todo engl. javaDoc
     * liefert S�ttigungswert bezogen auf HSV-Farbmodell.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @return S�ttigungswert zwischen 0 und 1
     */
    public float getSaturation() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (max != 0.f)
            return (delta / max);
        // else: r = g = b = 0 => S�ttigung = 0
        return 0.f;
    }

    /**      
     * todo engl. javaDoc
     * liefert V-Wert bezogen auf HSV-Farbmodell.<p>
     * <b>Methode ist noch nicht getestet!</b><p>
     * @return V-Wert zwischen 0 und 1
     */
    public float getValue() 
    {
        return this.max(mRed, mGreen, mBlue);
    }

    /**
     * todo engl. javaDoc
     * setzt die Farbe durch Angabe eines hexadezimal codiertes Wertes im Format <tt>0xRRGGBB</tt>. <tt>RR</tt>,
     * <tt>GG</tt> und <tt>BB</tt> liegen dabei jeweils im Bereich <tt>00 ... FF</tt>.<p>
     * Bem.: Dieses Format wird u. a. im OGC/ISO-Umfeld h�ufig verwendet (z. B. WMS- und WTS-Spezifikation).<p>
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
     * todo engl. javaDoc
     * liefert den Farbwert in hexadezimaler Codierung.<p>
     * Bem.: Der Alpha-Wert wird hierbei nicht ber�cksichtigt.<p>
     * @return hexadezimal codierte Farbangabe
     */
    public String getHexEncodedValue() {
        throw new T3dNotYetImplException("T3dColor#getHexEncodedValue is not implemented yet...");
    }

    /**
     * todo engl. javaDoc
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
     * todo engl. javaDoc
     * liefert Gr�n-Anteil der Farbe bezogen auf RGB-Farbmodell als ganzzahligen Wert im Bereich 0..255.<p>
     * @return gerundeter Gr�n-Wert zwischen 0 und 255
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
     * todo engl. javaDoc
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