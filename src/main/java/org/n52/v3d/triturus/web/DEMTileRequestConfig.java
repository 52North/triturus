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