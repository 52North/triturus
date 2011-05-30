package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;

/**
 * @deprecated
 * Diese Klasse dient zur Konfiguration der Request-Parameter eines speziellen Dienstes
 * f�r den Kartenblatt-orientierten Zugriff auf Gel�ndemodelle. Der zugeh�rige Dienst
 * l�sst sich �ber die Klasse <tt>IoDEMTileServiceConnector</tt> ansprechen.<p>
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
     * @param pURL URL f�r den Service
     * @param pTileID Bezeichner f�r abzufragende Kachel (z. B. Kartenblatt-Nummer)
     * @param pDEMType "tin" f�r Dreiecksnetze oder "grid" f�r Gittermodelle
     * @param pDEMFormat DGM-Format, z. B. "model/vrml" f�r VRML-Modelle
     */
    public DEMTileRequestConfig( 
        String pURL, String pTileID, String pDEMType, String pDEMFormat ) 
    {
    	mBaseURL = pURL;
    	mTileID = pTileID;
    	mType = pDEMType;
    	mFormat = pDEMFormat;
    }

    /** setzt den URL f�r den Service. */
    public void setBaseURL( String pURL ) {
    	mBaseURL = pURL;
    }

    /** liefert den f�r den Service eingestellten URL. */
    public String getBaseURL() {
    	return mBaseURL;
    }

    /** setzt den Tile-ID f�r den Service (z. B. Kartenblatt-Nummer). */
    public void setTileID( String pTileID ) {
    	mTileID = pTileID;
    }
   
    /**
     * liefert den URL f�r den GetDEM-Request an den Service.<p>
     *<b>Der Service unterst�tzt noch keine GetCapabilities-Requests.</b><p>
     *@return vollst�ndigen URL f�r den GetDEM-Request
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