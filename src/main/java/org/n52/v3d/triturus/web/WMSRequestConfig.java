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
package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;

/** 
 * todo engl. JavaDoc
 * Diese Klasse dient zur Konfiguration der Request-Parameter von Web Map Services (WMS). Die Klasse ist prinzipiell
 * f�r alle OGC-Spezifikationen ab Version 1.0 verwendbar.<p>
 * <i>Bem.: Diese Klasse ist noch "under construction" (erstmal eine einfachst gehaltene Arbeitsversion).</i><p>
 * @author Benno Schmidt
 */
public class WMSRequestConfig
{
    private String mBaseURL;
    private String mServiceName = "WMS";
    private String[] mLayers;
    private VgEnvelope mBBox;
    private String mSRS;
    private int mWidth;
    private int mHeight;
    private String mImageFormat;
    private String mStyles = "";
    private boolean mUseStylesAttribute = false;
    private boolean mTransparency = false;

    /**
     * Konstruktor.<p>
     * @param pURL URL f�r den WMS
     * @param pLayers Array mit abzufragenden Layern, siehe <tt>this.setLayers()</tt>.
     * @param pBBox Bounding-Box, siehe <tt>this.setBBox()</tt>.
     * @param pEPSGString raumliches Referenzsystem, siehe <tt>this.setSRS()</tt>.
     * @param pWidth Bildbreite, siehe <tt>this.setWidth()</tt>.
     * @param pHeight Bildh�he, siehe <tt>this.setHeight()</tt>.
     * @param pImageFormat Bildformat, siehe <tt>this.setImageFormat()</tt>.
     */
    public WMSRequestConfig( 
        String pURL, String[] pLayers, VgEnvelope pBBox, String pEPSGString, int pWidth, int pHeight, String pImageFormat)
    {
    	mBaseURL = pURL;
    	mLayers = pLayers;
    	mBBox = pBBox;
        mSRS = pEPSGString;
    	mWidth = pWidth;
    	mHeight = pHeight;
    	mImageFormat = pImageFormat;
    }

    /**
     * setzt den URL f�r den WMS.<p>
     * @param pURL g�ltige URL
     */
    public void setBaseURL(String pURL) {
    	mBaseURL = pURL;
    }

    /**
     * liefert den f�r den WMS eingestellten URL.<p>
     * @return URL
     */
    public String getBaseURL() {
    	return mBaseURL;
    }

    /** 
     * setzt den Service-Namen f�r den WMS. Voreingestellt ist der Wert "WMS". Ein Aufruf dieser Methode ist nur dann
     * n�tig, falls diese Einstellung �berschrieben werden soll.<p>
     * @param pServiceName Service-Namen
     */
    public void setServiceName(String pServiceName) {
    	mServiceName = pServiceName;
    }

    /**
     * setzt die Namen der abzufragenden Layer.<p>
     * @param pLayers Array mit Layer-Namen, z. B. <tt>{"Bundeslaender", '"Gemeindegrenzen"}</tt>
     */
    public void setLayers(String[] pLayers) {
    	mLayers = pLayers;
    }

    /** 
     * setzt die BBOX f�r den WMS-Request.<p> 
     * <b>Bem.: Die Handhabung von SRS wird von der vorliegenden Klasse z. Zt. noch nicht unterst�tzt.</b>
     * @param pEnv Bounding-Box
     */
    public void setBBox(VgEnvelope pEnv) {
    	mBBox = pEnv;
    }

    /**
     * liefert die f�r den Request eingestellte BBOX.<p>
     * @return Bounding-Box
     */
    public VgEnvelope getBBox() {
    	return mBBox;
    }

    /**
     * setzt das r�umliche Referenzsystem (EPSG-Code) f�r den Request.<p>
     * @param pEPSGString EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
     * @see VgGeomObject#setSRS
     */
    public void setSRS(String pEPSGString) {
        mSRS = pEPSGString;
    }

    /**
     * liefert das f�r den Request gesetzte r�umliche Referenzsystem (EPSG-Code).<p>
     * @return EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
     * @see VgGeomObject#setSRS
     */
    public String getSRS() {
        return mSRS;
    }

    /**
     * setzt die Bildbreite f�r das anzufordernde Bild.<p>
     * @param pWidth Bildbreite in Pixeln
     */
    public void setImageWidth(int pWidth) {
    	mWidth = pWidth;
    }

    /**
     * liefert die f�r das anzufordernde Bild eingestellte Bildbreite.<p>
     * @return Bildbreite in Pixeln.
     */
    public int getImageWidth() {
    	return mWidth;
    }

    /**
     * setzt die Bildh�he f�r das anzufordernde Bild.<p>
     * @param pHeight Bildh�he in Pixeln
     */
    public void setImageHeight(int pHeight) {
    	mHeight = pHeight;
    }

    /**
     * liefert die f�r das anzufordernde Bild eingestellte Bildh�he.<p>
     * @return Bildh�he in Pixeln
     */
    public int getImageHeight() {
    	return mHeight;
    }

    /** 
     * setzt das Format, in dem die Map angefordert werden soll.<p>
     * Es ist sicherzustellen, dass der angesprochene WMS dieses Format auch unterst�tzt.<p>
     * @param pFormat MIME-Typ, z. B. "image/jpeg"
     */
    public void setImageFormat(String pFormat) {
    	mImageFormat = pFormat;
    }

    /**
     * liefert das f�r den Request eingestellte Bildformat.<p>
     * @return MIME-Typ, z. B. "image/jpeg"
     */
    public String getImageFormat() {
    	return mImageFormat;
    }

    /** 
     * setzt den Wert f�r die Transparenz.<p>
     * Es ist sicherzustellen, dass der angesprochene WMS diese Einstellung auch unterst�tzt.<p>
     * @param pTransparency Transparenzgrad
     */
    public void setTransparency(boolean pTransparency) {
    	mTransparency = pTransparency;
    }

    /**
     * liefert den f�r die Transparenz eingestellten Wert.<p>
     * @return Transparenzgrad
     */
    public boolean getTransparency() {
    	return mTransparency;
    }

    /** 
     * setzt die Styles, in dem die Map angefordert werden soll. Das STYLES-Atribut wird im Map-Request nur dann
     * gesetzt, falls das Flag <tt>pUseStylesAttribute</tt> gesetzt ist. Voreingestellt ist der Wert <i>false</i>.<p>
     * Es ist sicherzustellen, dass der angesprochene WMS die jeweilige Einstellung auch unterst�tzt.<p>
     * @param pUseStylesAttribute <i>true</i>, falls STYLES-Attribut verwendet werden soll
     * @param pStyles Wert f�r STYLES-Attribut
     */
    public void setStyles(boolean pUseStylesAttribute, String pStyles) {
    	mUseStylesAttribute = pUseStylesAttribute;
    	mStyles = pStyles;
    }
    
    /**
     * liefert den URL f�r den GetCapabilities-Request an den WMS.<p>
     * <b>Die vorliegende Klasse unterst�tzt z. Zt. noch nicht das Auslesen der Capabilities-Antwort!</b><p>
     * @return vollst�ndigen URL f�r den GetCapabilities-Request
     */    
    public String getCapabilitiesRequestURL()
    {
    	return mBaseURL 
    	    + "?SERVICE=" + mServiceName 
    	    + "&REQUEST=GetCapabilities";
    }

    /**
     * liefert den URL f�r den GetMap-Request an den WMS.<p>
     * @return vollst�ndigen URL f�r den GetMap-Request
     */    
    public String getMapRequestURL()
    {
    	String lURL = mBaseURL;
    	
    	lURL = lURL +
    	    "?SERVICE=" + mServiceName +
    	    "&REQUEST=GetMap";

        lURL = lURL + "&VERSION=1.1.0"; // todo

    	lURL = lURL + "&LAYERS=";
    	if (mLayers.length > 0) {
    	    lURL = lURL + mLayers[0];
            for (int i = 1; i < mLayers.length; i++) {
                lURL = lURL + "," + mLayers[i];
            }
        }
        
        lURL = lURL + "&BBOX=" + 
            mBBox.getXMin() + "," + mBBox.getYMin() + "," + mBBox.getXMax() + "," + mBBox.getYMax();

        lURL = lURL + "&SRS=" + mSRS;

        lURL = lURL + "&WIDTH=" + mWidth + "&HEIGHT=" + mHeight;
 
        lURL = lURL + "&FORMAT=" + mImageFormat;
 
        if (mTransparency)
            lURL = lURL + "&TRANSPARENT=true";
        else
            lURL = lURL + "&TRANSPARENT=false";

        if (mUseStylesAttribute)
            lURL = lURL + "&STYLES=" + mStyles; 

    	return lURL;
    }
} 