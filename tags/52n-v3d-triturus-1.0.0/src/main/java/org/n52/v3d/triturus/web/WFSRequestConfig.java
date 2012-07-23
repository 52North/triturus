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