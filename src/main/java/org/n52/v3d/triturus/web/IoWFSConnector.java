package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * Über diese Klasse ist der Zugriff auf OGC-konforme Web Feature Services (WFS) möglich.<p>
 * <i>Bem.: Diese Klasse ist noch nicht implementiert.</i><p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoWFSConnector
{
    private WFSRequestConfig mRequCfg;
    private IoURLReader mConn;
    
    /**
     * Konstruktor.<p>
     * @param pRequestConfig initiale WFS-Request-Konfiguration
     */
    public IoWFSConnector(WFSRequestConfig pRequestConfig) 
    {
    	mConn = new IoURLReader("http", "");
    	mRequCfg = pRequestConfig;	
    }
    
    /**
     * setzt die WFS-Request-Konfiguration.<p>
     * @param pRequestConfig Request-Konfiguration
     */
    public void setRequestConfiguration(WFSRequestConfig pRequestConfig) {
    	mRequCfg = pRequestConfig;
    }

    /**
     * liefert die aktuelle WFS-Request-Konfiguration.<p>
     * @return Request-Konfiguration
     */
    public WFSRequestConfig getRequestConfiguration() {
    	return mRequCfg;
    }
    
    /** <b>TODO</b> */
    public void getFeatures()
    {
    	throw new T3dNotYetImplException();
    }

    /**
     * liefert den Objekt-internen Konnektor, über den die Web-Verbindung aufgebaut wird.<p>
     * @see IoURLReader
     * @return <tt>IoURLReader</tt>-Objekt
     */
    public IoURLReader connector() {
        return mConn;
    }
}