package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;

/** 
 * Diese Klasse dient zur Konfiguration der Request-Parameter von Web Coverage Services (WCS).
 * <i>Bem.: Diese Klasse ist noch "under construction" (erstmal eine einfachst gehaltene Arbeitsversion).</i><p>
 * @author Torsten Heinen<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class WCSRequestConfig
{
    private String mBaseURL;
    private String mServiceVersion = "1.0.0";
    private String mServiceName = "WCS";
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
     * @param pURL URL für den WCS
     * @param pLayers Array mit abzufragenden Layern, siehe <tt>this.setLayers()</tt>.
     * @param pBBox Bounding-Box, siehe <tt>this.setBBox()</tt>.
     * @param pEPSGString raumliches Referenzsystem, siehe <tt>this.setSRS()</tt>.
     * @param pWidth Bildbreite, siehe <tt>this.setWidth()</tt>.
     * @param pHeight Bildhöhe, siehe <tt>this.setHeight()</tt>.
     * @param pImageFormat Bildformat, siehe <tt>this.setImageFormat()</tt>.
     */
    public WCSRequestConfig( 
        String pURL, String[] pLayers, VgEnvelope pBBox, String pEPSGString, int pWidth, int pHeight, String pImageFormat, String pServiceVersion)
    {
    	mBaseURL = pURL;
    	mLayers = pLayers;
    	mBBox = pBBox;
        mSRS = pEPSGString;
    	mWidth = pWidth;
    	mHeight = pHeight;
    	mImageFormat = pImageFormat;
    	mServiceVersion = pServiceVersion;
    }

    /**
     * setzt den URL für den WCS.<p>
     * @param pURL gültige URL
     */
    public void setBaseURL(String pURL) {
    	mBaseURL = pURL;
    }

    /**
     * liefert den für den WCS eingestellten URL.<p>
     * @return URL
     */
    public String getBaseURL() {
    	return mBaseURL;
    }

    /** 
     * setzt den Service-Namen für den WCS. Voreingestellt ist der Wert "WCS". Ein Aufruf dieser Methode ist nur dann
     * nötig, falls diese Einstellung überschrieben werden soll.<p>
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
     * setzt die BBOX für den WCS-Request.<p> 
     * <b>Bem.: Die Handhabung von SRS wird von der vorliegenden Klasse z. Zt. noch nicht unterstützt.</b>
     * @param pEnv Bounding-Box
     */
    public void setBBox(VgEnvelope pEnv) {
    	mBBox = pEnv;
    }

    /**
     * liefert die für den Request eingestellte BBOX.<p>
     * @return Bounding-Box
     */
    public VgEnvelope getBBox() {
    	return mBBox;
    }

    /**
     * setzt das räumliche Referenzsystem (EPSG-Code) für den Request.<p>
     * @param pEPSGString EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
     * @see VgGeomObject#setSRS
     */
    public void setSRS(String pEPSGString) {
        mSRS = pEPSGString;
    }

    /**
     * liefert das für den Request gesetzte räumliche Referenzsystem (EPSG-Code).<p>
     * @return EPSG-Code in der Form "EPSG:<EPSG-Nr.>" oder <tt>this.SRSNone</tt>.
     * @see VgGeomObject#setSRS
     */
    public String getSRS() {
        return mSRS;
    }

    /**
     * setzt die Bildbreite für das anzufordernde Bild.<p>
     * @param pWidth Bildbreite in Pixeln
     */
    public void setImageWidth(int pWidth) {
    	mWidth = pWidth;
    }

    /**
     * liefert die für das anzufordernde Bild eingestellte Bildbreite.<p>
     * @return Bildbreite in Pixeln.
     */
    public int getImageWidth() {
    	return mWidth;
    }

    /**
     * setzt die Bildhöhe für das anzufordernde Bild.<p>
     * @param pHeight Bildhöhe in Pixeln
     */
    public void setImageHeight(int pHeight) {
    	mHeight = pHeight;
    }

    /**
     * liefert die für das anzufordernde Bild eingestellte Bildhöhe.<p>
     * @return Bildhöhe in Pixeln
     */
    public int getImageHeight() {
    	return mHeight;
    }

    /** 
     * setzt das Format, in dem die Map angefordert werden soll.<p>
     * Es ist sicherzustellen, dass der angesprochene WCS dieses Format auch unterstützt.<p>
     * @param pFormat MIME-Typ, z. B. "image/jpeg"
     */
    public void setImageFormat(String pFormat) {
    	mImageFormat = pFormat;
    }

    /**
     * liefert das für den Request eingestellte Bildformat.<p>
     * @return MIME-Typ, z. B. "image/jpeg"
     */
    public String getImageFormat() {
    	return mImageFormat;
    }

    /** 
     * setzt den Wert für die Transparenz.<p>
     * Es ist sicherzustellen, dass der angesprochene WCS diese Einstellung auch unterstützt.<p>
     * @param pTransparency Transparenzgrad
     */
    public void setTransparency(boolean pTransparency) {
    	mTransparency = pTransparency;
    }

    /**
     * liefert den für die Transparenz eingestellten Wert.<p>
     * @return Transparenzgrad
     */
    public boolean getTransparency() {
    	return mTransparency;
    }

    /** 
     * setzt die Styles, in dem die Map angefordert werden soll. Das STYLES-Atribut wird im Map-Request nur dann
     * gesetzt, falls das Flag <tt>pUseStylesAttribute</tt> gesetzt ist. Voreingestellt ist der Wert <i>false</i>.<p>
     * Es ist sicherzustellen, dass der angesprochene WCS die jeweilige Einstellung auch unterstützt.<p>
     * @param pUseStylesAttribute <i>true</i>, falls STYLES-Attribut verwendet werden soll
     * @param pStyles Wert für STYLES-Attribut
     */
    public void setStyles(boolean pUseStylesAttribute, String pStyles) {
    	mUseStylesAttribute = pUseStylesAttribute;
    	mStyles = pStyles;
    }
    
    /**
     * liefert den URL für den GetCapabilities-Request an den WCS.<p>
     * <b>Die vorliegende Klasse unterstützt z. Zt. noch nicht das Auslesen der Capabilities-Antwort!</b><p>
     * @return vollständigen URL für den GetCapabilities-Request
     */    
    public String getCapabilitiesRequestURL()
    {
    	return mBaseURL 
    	    + "?SERVICE=" + mServiceName 
    	    + "&REQUEST=GetCapabilities";
    }

    /**
     * liefert den URL für den GetMap-Request an den WCS.<p>
     * TODO resX/resY und width/height Parameter überarbeiten...
     * @return vollständigen URL für den GetCoverage-Request
     */    
    public String getCoverageRequestURL()
    {
    	String lURL = mBaseURL;
    	
    	lURL = lURL +
    	    "?SERVICE=" + mServiceName +
    	    "&REQUEST=GetCoverage";

        lURL = lURL + "&VERSION="+mServiceVersion;

    	lURL = lURL + "&LAYER=";
    	if (mLayers.length > 0) {
    	    lURL = lURL + mLayers[0];
            for (int i = 1; i < mLayers.length; i++) {
                lURL = lURL + "," + mLayers[i];
            }
        }
        
        lURL = lURL + "&BBOX=" + 
            mBBox.getXMin() + "," + mBBox.getYMin() + "," + mBBox.getXMax() + "," + mBBox.getYMax();

        lURL = lURL + "&SRS=" + mSRS;
        
        //lURL = lURL + "&WIDTH=" + mWidth + "&HEIGHT=" + mHeight;
        lURL = lURL + "&RESX=" + mWidth + "&RESY=" + mHeight;
 
        lURL = lURL + "&FORMAT=" + mImageFormat;
 
//        if (mTransparency)
//            lURL = lURL + "&TRANSPARENT=true";
//        else
//            lURL = lURL + "&TRANSPARENT=false";

        if (mUseStylesAttribute)
            lURL = lURL + "&STYLES=" + mStyles; 

    	return lURL;
    }
} 