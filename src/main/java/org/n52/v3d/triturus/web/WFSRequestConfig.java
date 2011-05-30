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
     * @param pURL URL für den WFS
     */
    public WFSRequestConfig(String pURL)
    {
    	mBaseURL = pURL;
    }

    /** setzt den URL für den WFS. */
    public void setBaseURL(String pURL) {
    	mBaseURL = pURL;
    }

    /** liefert die für den WFS eingestellten URL. */
    public String getBaseURL() {
    	return mBaseURL;
    }
    
    /**
     * liefert den URL für den GetCapabilities-Request an den WFS.<p>
     * <b>Die vorliegende Klasse unterstützt z. Zt. noch nicht das Auslesen der Capabilities-Antwort!</b>
     * <b>TODO</b><p>
     * @return vollständige URL für den GetCapabilities-Request
     */    
    public String getCapabilitiesRequestURL()
    {
    	throw new T3dNotYetImplException();
    }


    /**
     * liefert die URL für den GetFeature-Request an den WFS.
     * <b>TODO</b>
     * @return vollständige URL für den GetFeature-Request
     */    
    public String getFeatureRequestURL()
    {
    	throw new T3dNotYetImplException();
    }
} 