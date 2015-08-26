/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.t3dutil;

/**
 * @deprecated
 * Klasse zur Verwaltung einer Farbinformation.<p>
 * Bem.: Statt dieser Klasse sollte <tt>T3dColor</tt> verwendet werden.<p>
 * @author Benno Schmidt
 * @see T3dColor
 */
public class VuColor
{
    private float mRed, mGreen, mBlue, mAlpha;
    
    private int mColMod = 1; // 1 = RGB, 2 = HSV
    
    /** 
     * Konstruktor f�r eine Farbangabe im RGB-Modell. 
     * @param pRed Rot-Anteil als Wert zwischen 0 und 1
     * @param pGreen Gr�n-Anteil als Wert zwischen 0 und 1
     * @param pBlue Blau-Anteil als Wert zwischen 0 und 1
     */
    public VuColor(float pRed, float pGreen, float pBlue) {
    	this( pRed, pGreen, pBlue, 1.f );
    }

    /** 
     * Konstruktor f�r eine Farbangabe im RGB-Modell mit Alpha-Wert.
     * @param pRed Rot-Anteil als Wert zwischen 0 und 1
     * @param pGreen Gr�n-Anteil als Wert zwischen 0 und 1
     * @param pBlue Blau-Anteil als Wert zwischen 0 und 1
     * @param pAlpha Alpha-Wert zwischen 0 und 1
     */
    public VuColor(float pRed, float pGreen, float pBlue, float pAlpha) {
    	mRed = pRed;
    	mGreen = pGreen;
    	mBlue = pBlue;
    	mAlpha = pAlpha;
    }

    /** 
     * Konstruktor f�r eine Farbe im angegebenen Farbmodell. 
     * @param pColorSystem "RGB" oder "HSV"
     */
    public VuColor(String pColorSystem) {
    	this( 1.f, 1.f, 1.f, 1.f );
    	if (pColorSystem.equalsIgnoreCase("HSV"))
    	    mColMod = 2;
    	else
    	    mColMod = 1;  	
    }

    /**
     * Konstruktor f�r eine Farbe im angegebenen Farbmodell. Die Angaben m�ssen jeweils auf den
     * Wertebereich 0...1 bezogen sein.
     * <b>Methode ist noch nicht getestet!</b>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert f�r Farbmodell "RGB", Hue-Wert (Farbton) f�r "HSV"
     * @param pVal2 Gr�n-Wert f�r Farbmodell "RGB", S�ttigungs-Wert f�r "HSV"
     * @param pVal3 Blau-Wert f�r Farbmodell "RGB", V-Wert f�r "HSV"
     */
    public VuColor(String pColorSystem, float pVal1, float pVal2, float pVal3) 
    {
    	this( pColorSystem, pVal1, pVal2, pVal3, 1.f ); 
    }

    /**
     * Konstruktor f�r eine Farbe im angegebenen Farbmodell. Mit Ausnahme des Hue-Wertes m�ssen die 
     * Angaben jeweils auf den Wertebereich 0...1 bezogen sein. Der Hue-Wert ist im Bogenma� anzugeben
     * (0...2*pi).
     * <b>noch nicht implementiert!</b>
     * @param pColorSystem "RGB" oder "HSV"
     * @param pVal1 Rot-Wert f�r Farbmodell "RGB", Hue-Wert (Farbton) f�r "HSV"
     * @param pVal2 Gr�n-Wert f�r Farbmodell "RGB", S�ttigungs-Wert f�r "HSV"
     * @param pVal3 Blau-Wert f�r Farbmodell "RGB", V-Wert f�r "HSV"
     * @param pAlpha Alpha-Wert
     */
    public VuColor(String pColorSystem, float pVal1, float pVal2, float pVal3, float pAlpha) 
    {
    	if (pColorSystem.equalsIgnoreCase("HSV")) 
    	{
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
    	    mColMod = 2;
    	}
    	else {
            mRed = pVal1;
            mGreen = pVal2;
            mBlue = pVal3;
    	    mColMod = 1;  	
    	}
        mAlpha = pAlpha;
    }

    /**      
     * liefert Rot-Anteil der Farbe bezogen auf RGB-Farbmodell.
     * @return Rot-Wert zwischen 0 und 1
     */
    public float getRed() {
        return mRed;
    }

    /**      
     * liefert Gr�n-Anteil der Farbe bezogen auf RGB-Farbmodell.
     * @return Gr�n-Wert zwischen 0 und 1
     */
    public float getGreen() {
        return mGreen;
    }

    /**      
     * liefert Blau-Anteil der Farbe bezogen auf RGB-Farbmodell.
     * @return Blau-Wert zwischen 0 und 1
     */
    public float getBlue() {
        return mBlue;
    }

    /** 
     * setzt Opazit�t/Transparenz. 
     * @param pAlpha 1.0 f�r Transparenz = 0 % (voll-opak), 0.0 f�r Transparenz = 100 %
     */
    public void setAlpha(float pAlpha) {
        mAlpha = pAlpha;
    }
 
    /** 
     * liefert Opazit�t/Transparenz.
     * @return 1.0 f�r Transparenz = 0 % (voll-opak), 0.0 f�r Transparenz = 100 %
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**      
     * liefert Hue-Wert (Farbton) bezogen auf HSV-Farbmodell.
     * @return Hue-Wert im Bogenma� (0...2*pi)
     * <b>Methode noch nicht getestet!</b>
     */
    public float getHue() 
    {
	float min = this.min( mRed, mGreen, mBlue );
	float max = this.max( mRed, mGreen, mBlue );
	float delta = max - min;
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
     * liefert S�ttigungswert bezogen auf HSV-Farbmodell.
     * @return S�ttigungswert zwischen 0 und 1
     * <b>Methode noch nicht getestet!</b>
     */
    public float getSaturation() 
    {
	float min = this.min( mRed, mGreen, mBlue );
	float max = this.max( mRed, mGreen, mBlue );
	float delta = max - min;
	if (max != 0.f)
	    return (delta / max);
	// else: r = g = b = 0 -> s = 0, v undef.:
        return 0.f;
    }

    /**      
     * liefert V-Wert bezogen auf HSV-Farbmodell
     * @return V-Wert zwischen 0 und 1
     * <b>Methode noch nicht getestet!</b>
     */
    public float getValue() 
    {
	float min = this.min( mRed, mGreen, mBlue );
	float max = this.max( mRed, mGreen, mBlue );
	return max;
    }
    
    public String toString() {
    	return "[R:" + mRed + ", G:" + mGreen + ", B:" + mBlue + ", A:" + mAlpha + "]";
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