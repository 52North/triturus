package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * Diese Klasse dient zur Konfiguration der Request-Parameter von Web Feature Services (WFS).<p>
 * <b>Diese Klasse ist noch nicht implementiert.</b><p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
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