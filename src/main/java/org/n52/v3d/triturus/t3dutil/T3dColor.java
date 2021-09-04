/**
 * Copyright (C) 2007-2015 52 North Initiative for Geospatial Open Source 
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/**
 * Class to manage color-information.
 * <br>
 * TODO: Note that not all methods of this class have been properly tested yet. 
 * 
 * @author Benno Schmidt, Torsten Heinen
 */
public class T3dColor
{
    private float mRed, mGreen, mBlue, mAlpha;
    private int mColMod = 1; // 1 = RGB, 2 = HSV
    
    /**
     * Constructor for a color specification referring to the RGB-model.
     * 
     * @param red Red-portion as value between 0 and 1
     * @param green Green-portion as value between 0 and 1
     * @param blue Blue-portion as value between 0 and 1
     */
    public T3dColor(float red, float green, float blue) {
    	this(red, green, blue, 1.f);
    }

    /** 
     * Constructor for a color specification referring to the RGB-model with an
     * additional alpha-value.
     * 
     * @param red Red-portion as value between 0 and 1
     * @param green Green-portion as value between 0 and 1
     * @param blue Blue-portion as value between 0 and 1
     * @param alpha Alpha-value as value between 0 and 1
     */
    public T3dColor(float red, float green, float blue, float alpha) {
    	mRed = red;
    	mGreen = green;
    	mBlue = blue;
    	mAlpha = alpha;
    }

    /** 
     * Constructor. The current color will be set to &quot;white&quot;.
     */
    public T3dColor() {
    	this(1.f, 1.f, 1.f, 1.f);
    }

    /**
     * Constructor for a color object referring to a given color model. 
     * Hue-values must be given in the range 0 ... <i>2*pi</i> (given in 
     * radians). All other color component values must be in the range 0...1.
     * 
     * @param colorSystem "RGB" or "HSV"
     * @param val1 Red-value for "RGB" model, hue-value for "HSV" model
     * @param val2 Green-value for "RGB" model, saturation-value for "HSV"
     * @param val3 Blue-value for color model "RGB", V-value for "HSV"
     */
    public T3dColor(String colorSystem, float val1, float val2, float val3) 
    {
        this(colorSystem, val1, val2, val3, 1.f);
    }

    /**
     * Constructor for a color object referring to a given color model. 
     * Hue-values must be given in the range 0 ... <i>2*pi</i> (given in 
     * radians). All other color component values must be in the range 0...1.
     * 
     * @param colorSystem "RGB" or "HSV"
     * @param val1 Red-value for "RGB" model, hue-value for "HSV" model
     * @param val2 Green-value for "RGB" model, saturation-value for "HSV"
     * @param val3 Blue-value for color model "RGB", V-value for "HSV"
     * @param alpha Alpha-value Opaqueness (resp. transparency) value
     */
    public T3dColor(String colorSystem, float val1, float val2, float val3, float alpha) 
    {
    	if (colorSystem.equalsIgnoreCase("HSV")) 
    	{
    		mColMod = 2;
            float h = val1, s = val2, v = val3;
    	    if (s == 0) { // achromatic/grey
	        	mRed = v; mGreen = v; mBlue = v;
            }
            else {
            	float sixth = (float) (Math.PI / 3.);
                h /= sixth; // sectors 0 to 5
                int i = (int) h;
                float f = h - i; // factorial part of h
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
            mRed = val1;
            mGreen = val2;
            mBlue = val3;
    	}
        mAlpha = alpha;
    }

    /**      
     * sets the color's Red portion referring to the RGB color model.
     * 
     * @param val R-value in the range 0 ... 1
     */
    public void setRed(float val) {
        mRed = val;
    }

    /**      
     * sets the color's Green portion referring to the RGB color model.
     * 
     * @param val G-value in the range 0 ... 1
     */
    public void setGreen(float val) {
        mGreen = val;
    }

    /**      
     * sets the color's Blue portion referring to the RGB color model.
     * 
     * @param val B-value in the range 0 ... 1
     */
    public void setBlue(float val) {
        mBlue = val;
    }
   
    /**      
     * gets the color's Red portion referring to the RGB color model.
     * 
     * @return R-value in the range 0 ... 1
     */
    public float getRed() {
        return mRed;
    }

    /**      
     * gets the color's Green portion referring to the RGB color model.
     * 
     * @return G-value in the range 0 ... 1
     */
    public float getGreen() {
        return mGreen;
    }

    /**      
     * gets the color's Blue portion referring to the RGB color model.
     * 
     * @return B-value in the range 0 ... 1
     */
    public float getBlue() {
        return mBlue;
    }

    /**      
     * sets the color's opacity value. For full opaqueness (no transparency), a
     * value of 1.0 has to be given, for complete transparency a value of 0.0.   
     * 
     * @param val Alpha-value in the range 0 ... 1
     */
    public void setAlpha(float val) {
        mAlpha = val;
    }
 
    /**      
     * gets the color's opacity (alpha) value.
     * 
     * @return Alpha-value in the range 0 ... 1
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**      
     * sets the color's Red, Green, and Blue portion referring to the RGB color 
     * model.
     * 
     * @param red R-value in the range 0 ... 1
     * @param green G-value in the range 0 ... 1
     * @param blue B-value in the range 0 ... 1
     */
    public void setRGB(float red, float green, float blue) {
    	this.setRed(red);
    	this.setGreen(green);
    	this.setBlue(blue);
    }

    /**      
     * sets the color's Red, Green, and Blue portion referring to the RGB color 
     * model.
     * 
     * @param red R-value in the range 0 ... 1
     * @param green G-value in the range 0 ... 1
     * @param blue B-value in the range 0 ... 1
     * @param alpha Opaqueness (resp. transparency) value
     */
    public void setRGBA(float red, float green, float blue, float alpha) {
    	this.setRGB(red, green, blue);
    	this.setAlpha(alpha);
    }

    /**      
     * gets the color's hue value referring to the HSV color model.
     * <br>
     * Note: For grey colors (incl. black and white) the saturation-value S is 
     * 0 whereas the hue-value is undefined. 
     * 
     * @return H-value in the range 0 ... <i>2*pi</i> (radians)
     */
    public float getHue() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (delta <= 0.) // delta = 0 => saturation 0, H undefined
            return 0.f;
        float H;
        if (mRed == max)
            H = (mGreen - mBlue) / delta; // between Yellow and Magenta
        else if (mGreen == max)
            H = 2.f + (mBlue - mRed) / delta; // between Cyan and Yellow
        else
            H = 4.f + (mRed - mGreen) / delta; // between Magenta and Cyan
        float sixth = (float) (Math.PI / 3.);
        H *= sixth; // radians
        if (H < 0.f)
            H += 6. * sixth;
        return H;
    }

    /**      
     * gets the color's saturation value referring to the HSV color model.
     * 
     * @return S-value in the range 0 ... 1
     */
    public float getSaturation() 
    {
        float min = this.min(mRed, mGreen, mBlue);
        float max = this.max(mRed, mGreen, mBlue);
        float delta = max - min;
        if (max != 0.f)
            return (delta / max);
        // else: R = G = B = 0 => saturation = 0
        return 0.f;
    }

    /**      
     * gets the color's V-value referring to the HSV color model.
     * 
     * @return V-value in the range 0 ... 1
     */
    public float getValue() 
    {
        return this.max(mRed, mGreen, mBlue);
    }

    /**
     * sets a color by giving a hexadecimal-coded value in the format 
     * <tt>0xRRGGBB</tt>. <tt>RR</tt>, <tt>GG</tt> and <tt>BB</tt> must be in 
     * the range 00 ... FF.
     * <br>
     * Note: This format is often used in the scope of OGC/ISO or W3C 
     * specifications (e.g. OGC-WMS, OGC-WTS, CSS, ...).
     * 
     * @param hexVal Hexadecimal-coded color value string
     */
    public void setHexEncodedValue(String hexVal) 
    {
        String str = hexVal.toLowerCase();
        if (!str.startsWith("0x"))
            throw new T3dException("Hexadecimal color encoding (" + hexVal + ") requires '0x' prefix.");
        if (str.length() != 8)
            throw new T3dException("Hexadecimal color encoding (" + hexVal + ") does not match '0xRRGGBB' format.");

        int red256 = 16 * this.hexDigit2Int(str.charAt(2)) + this.hexDigit2Int(str.charAt(3));
        int green256 = 16 * this.hexDigit2Int(str.charAt(4)) + this.hexDigit2Int(str.charAt(5));
        int blue256 = 16 * this.hexDigit2Int(str.charAt(6)) + this.hexDigit2Int(str.charAt(6));
        
        this.setRGB(
        	((float) red256) / 255.f, 
        	((float) green256) / 255.f, 
        	((float) blue256) / 255.f);
    }

    private int hexDigit2Int(char figit) {
        if (figit >= '0' && figit <= '9') return figit - '0';
        if (figit >= 'a' && figit <= 'f') return 10 + (figit - 'a');
        if (figit >= 'A' && figit <= 'F') return 10 + (figit - 'A');
        throw new T3dException("Unexpected hexadecimal character (" + figit + ").");
    }

    /**
     * provides a color specification as hexadecimal-coded string. Note that the 
     * alpha-value (opaqueness resp. transparency) will be ignored here!
     * 
     * @return Hexadecimal-coded color value string
     */
    public String getHexEncodedValue() {
        throw new T3dNotYetImplException("T3dColor#getHexEncodedValue is not implemented yet...");
    }

    /**
     * provides the Red-portion of the color referring to the RGB color model 
     * as integer value in the range 0 ... 255.
     * 
     * @return Rounded R-value 
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
     * provides the Green-portion of the color referring to the RGB color model
     * as integer value in the range 0 ... 255.
     * 
     * @return Rounded G-value 
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
     * provides the Blue-portion of the color referring to the RGB color model 
     * as integer value in the range 0 ... 255.
     * 
     * @return Rounded B-value 
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
    
    // Helpers:
    
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