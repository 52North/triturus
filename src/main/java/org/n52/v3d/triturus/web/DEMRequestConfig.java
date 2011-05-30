package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.vgis.VgEnvelope;

/**
 * Diese Klasse dient zur Konfiguration der Request-Parameter des DGM-Dienstes, der durch die Applikation
 * <tt>$TERRA3D_HOME/serverapp/DEMService/DERMServlet.java</tt> realisiert ist.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class DEMRequestConfig
{
    private String mBaseURL;
    private VgEnvelope mBBox;
    private double mCellSize;
    private String mSRS;
    private String mDEMFormat;

    /**
     * Konstruktor.<p>
     * @param pURL URL f�r den Dienst
     * @param pBBox Bounding-Box, siehe <tt>this.setBBox()</tt>.
     * @param pEPSGString raumliches Referenzsystem, siehe <tt>this.setSRS()</tt>.
     * @param pCellSize Gitterweite, siehe <tt>this.setCellSize()</tt>.
     * @param pDEMFormat Zielformat, siehe <tt>this.setDEMFormat()</tt>.
     */
    public DEMRequestConfig(
    	String pURL, VgEnvelope pBBox, String pEPSGString, double pCellSize, String pDEMFormat)
    {
        mBaseURL = pURL;
        mBBox = pBBox;
        mSRS = pEPSGString;
        mCellSize = pCellSize;
        mDEMFormat = pDEMFormat;
    }

    /** setzt den URL f�r den Dienst. */
    public void setBaseURL(String pURL) {
        mBaseURL = pURL;
    }

    /** liefert den f�r den Dienst eingestellten URL. */
    public String getBaseURL() {
        return mBaseURL;
    }

    /**
     * setzt die BBOX f�r den Request.<p>
     * <b>Bem.: Die Handhabung von SRS wird von der vorliegenden Klasse z. Zt. noch nicht unterst�tzt.</b><p>
     */
    public void setBBox(VgEnvelope pEnv) {
        mBBox = pEnv;
    }

    /** liefert die f�r den Request eingestellte BBOX. */
    public VgEnvelope getBBox() {
        return mBBox;
    }

    /** setzt die Gitterweite f�r das anzufordernde Gel�ndemodell. */
    public void setCellSize(double pCellSize) {
        mCellSize = pCellSize;
    }

    /** liefert die f�r das anzufordernde Gel�ndemodell eingestellte Gitterweite. */
    public double getCellSize() {
        return mCellSize;
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
     * setzt das Format, in dem das Gel�ndmodell angefordert werden soll.<p>
     * @param pFormat Format-Angabe laut Dienst-Spezifikation
     */
    public void setDEMFormat(String pFormat) {
        mDEMFormat = pFormat;
    }

    /** liefert das eingestellte Gel�ndemodell-Format. */
    public String getDEMFormat() {
        return mDEMFormat;
    }

    /**
     * liefert den URL f�r den GetCapabilities-Request an den WMS.<p>
     * <b>Die vorliegende Klasse unterst�tzt z. Zt. noch nicht das Auslesen der Capabilities-Antwort!</b><p>
     * @return vollst�ndigen URL f�r den GetCapabilities-Request
     */
    public String getCapabilitiesRequestURL()
    {
        return mBaseURL + "?REQUEST=GetCapabilities";

    }

    /**
     * liefert den URL f�r den GetDEM-Request an den Dienst.<p>
     * @return vollst�ndigen URL f�r den GetDEM-Request
     */
    public String getDEMRequestURL()
    {
        String lURL = mBaseURL;

        lURL = lURL + "?REQUEST=GetDEM";

        lURL = lURL + "&BBOX=" +
            Math.round(mBBox.getXMin()) + "," + 
            Math.round(mBBox.getYMin()) + "," + 
            Math.round(mBBox.getXMax()) + "," + 
            Math.round(mBBox.getYMax());

		lURL = lURL + "&SRS=" + mSRS;
		
        lURL = lURL + "&CELLSIZE=" + mCellSize;

        lURL = lURL + "&FORMAT=" + mDEMFormat;

        return lURL;
    }
}
