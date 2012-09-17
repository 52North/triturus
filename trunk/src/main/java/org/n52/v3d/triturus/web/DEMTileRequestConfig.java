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
package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.vgis.*;

/**
 * todo engl. JavaDoc
 * @deprecated
 * Diese Klasse dient zur Konfiguration der Request-Parameter eines speziellen Dienstes
 * f�r den Kartenblatt-orientierten Zugriff auf Gel�ndemodelle. Der zugeh�rige Dienst
 * l�sst sich �ber die Klasse <tt>IoDEMTileServiceConnector</tt> ansprechen.<p>
 * @author Benno Schmidt
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