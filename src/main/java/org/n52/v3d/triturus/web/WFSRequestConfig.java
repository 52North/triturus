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

import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * todo engl. JavaDoc
 * Diese Klasse dient zur Konfiguration der Request-Parameter von Web Feature Services (WFS).<p>
 * <b>Diese Klasse ist noch nicht implementiert.</b><p>
 * @author Benno Schmidt
 */
public class WFSRequestConfig
{
    private String mBaseURL;

    /**
     * Konstruktor.
     * @param pURL URL f�r den WFS
     */
    public WFSRequestConfig(String pURL)
    {
    	mBaseURL = pURL;
    }

    /** setzt den URL f�r den WFS. */
    public void setBaseURL(String pURL) {
    	mBaseURL = pURL;
    }

    /** liefert die f�r den WFS eingestellten URL. */
    public String getBaseURL() {
    	return mBaseURL;
    }
    
    /**
     * liefert den URL f�r den GetCapabilities-Request an den WFS.<p>
     * <b>Die vorliegende Klasse unterst�tzt z. Zt. noch nicht das Auslesen der Capabilities-Antwort!</b>
     * <b>TODO</b><p>
     * @return vollst�ndige URL f�r den GetCapabilities-Request
     */    
    public String getCapabilitiesRequestURL()
    {
    	throw new T3dNotYetImplException();
    }


    /**
     * liefert die URL f�r den GetFeature-Request an den WFS.
     * <b>TODO</b>
     * @return vollst�ndige URL f�r den GetFeature-Request
     */    
    public String getFeatureRequestURL()
    {
    	throw new T3dNotYetImplException();
    }
} 