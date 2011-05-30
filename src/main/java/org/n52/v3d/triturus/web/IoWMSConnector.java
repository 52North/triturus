package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Über diese Klasse ist der Zugriff auf OGC-konforme Web Map Services (WMS) möglich.<p>
 * <i>Bem.: Diese Klasse ist noch "under construction" (erstmal eine einfachst gehaltene Arbeitsversion).</i><p>
 * @author Benno Schmidt<p><br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoWMSConnector
{
    private WMSRequestConfig mRequCfg;
    private IoURLReader mConn;
    
    /**
     * Konstruktor.<p>
     * @param pRequestConfig initiale WMS-Request-Konfiguration
     */
    public IoWMSConnector(WMSRequestConfig pRequestConfig) 
    {
    	mConn = new IoURLReader("http", "");
    	mRequCfg = pRequestConfig;	
    }
    
    /**
     * setzt die WMS-Request-Konfiguration.<p>
     * @param pRequestConfig Request-Konfiguration
     */
    public void setRequestConfiguration(WMSRequestConfig pRequestConfig) {
    	mRequCfg = pRequestConfig;
    }

    /**
     * liefert die aktuelle WMS-Request-Konfiguration.<p>
     * @return Request-Konfiguration
     */
    public WMSRequestConfig getRequestConfiguration() {
    	return mRequCfg;
    }
    
    /**
     * führt den GetMap-Request durch. Die Map wird entsprechend der konfigurierten Einstellungen über das Web
     * abgefragt (HTTP) und unter dem angegebenen Dateinamen gespeichert.<p>
     * @param pFilename Dateiname
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void getMap(String pFilename) throws T3dException
    {
    	mConn.setURL(mRequCfg.getMapRequestURL());

        String type = null;
    	try {
    	    type = mConn.getContent(pFilename);
    	}
    	catch (T3dException e) {
    	    throw e;
    	}

        if (!type.toLowerCase().startsWith("image/"))
            throw new T3dException("The requested WMS did not provide an image.");
    }

    /**
     * liefert den Objekt-internen Konnektor, über den die Web-Verbindung aufgebaut wird.<p>
     * @return <tt>IoURLReader</tt>-Objekt
     * @see IoURLReader
     */
    public IoURLReader connector() {
        return mConn;
    }
}