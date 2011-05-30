package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;

/**
 * @deprecated
 * Diese Klasse dient zur Konfiguration der Request-Parameter eines speziellen Dienstes
 * für den Kartenblatt-orientierten Zugriff auf Geländemodelle. Der zugehörige Dienst
 * lässt sich über die Klasse <tt>IoDEMTileServiceConnector</tt> ansprechen.<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 * @author Benno Schmidt<p><br>
 */
public class DEMTileRequestConfig
{
    private String mBaseURL;
    private String mTileID;
    private String mType;
    private String mFormat;

    /**
     * Konstruktor.
     * @param pURL URL für den Service
     * @param pTileID Bezeichner für abzufragende Kachel (z. B. Kartenblatt-Nummer)
     * @param pDEMType "tin" für Dreiecksnetze oder "grid" für Gittermodelle
     * @param pDEMFormat DGM-Format, z. B. "model/vrml" für VRML-Modelle
     */
    public DEMTileRequestConfig( 
        String pURL, String pTileID, String pDEMType, String pDEMFormat ) 
    {
    	mBaseURL = pURL;
    	mTileID = pTileID;
    	mType = pDEMType;
    	mFormat = pDEMFormat;
    }

    /** setzt den URL für den Service. */
    public void setBaseURL( String pURL ) {
    	mBaseURL = pURL;
    }

    /** liefert den für den Service eingestellten URL. */
    public String getBaseURL() {
    	return mBaseURL;
    }

    /** setzt den Tile-ID für den Service (z. B. Kartenblatt-Nummer). */
    public void setTileID( String pTileID ) {
    	mTileID = pTileID;
    }
   
    /**
     * liefert den URL für den GetDEM-Request an den Service.<p>
     *<b>Der Service unterstützt noch keine GetCapabilities-Requests.</b><p>
     *@return vollständigen URL für den GetDEM-Request
     */    
    public String getDEMRequestURL()
    {
    	String lURL = mBaseURL;
    	
    	lURL = lURL + "&TILE=" + mTileID;
    	lURL = lURL + "&TYPE=" + mType.toLowerCase();
        lURL = lURL + "&FORMAT=" + mFormat;

    	return lURL;
    }
} 