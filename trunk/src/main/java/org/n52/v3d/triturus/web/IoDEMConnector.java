package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Über diese Klasse ist der Zugriff auf den durch die Applikation 
 * <tt>$TERRA3D_HOME/serverapp/DEMService/DEMServlet.java</tt> realisierten DGM-Dienst möglich.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoDEMConnector
{
    private DEMRequestConfig mRequCfg;
    private IoURLReader mConn;
    
    /**
     * Konstruktor.<p>
     * @param pRequestConfig initiale Request-Konfiguration
     */
    public IoDEMConnector(DEMRequestConfig pRequestConfig) 
    {
    	mConn = new IoURLReader("http", "");
    	mRequCfg = pRequestConfig;	
    }
    
    /**
     * setzt die Request-Konfiguration.<p>
     * @param  pRequestConfig Request-Konfiguration
     */
    public void setRequestConfiguration(DEMRequestConfig pRequestConfig) {
    	mRequCfg = pRequestConfig;
    }

    /**
     * liefert die aktuelle Request-Konfiguration.<p>
     * @return Request-Konfiguration
     */
    public DEMRequestConfig getRequestConfiguration() {
    	return mRequCfg;
    }
    
    /**
     * führt den GetDEM-Request durch. Das Geländemodell wird entsprechend der konfigurierten Einstellungen 
     * über das Web abgefragt (HTTP) und unter dem angegebenen Dateinamen gespeichert.<p>
     * @param pFilename Dateiname
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void getDEM(String pFilename) throws T3dException
    {
    	mConn.setURL(mRequCfg.getDEMRequestURL());
    	
    	try {
    	    mConn.getContent(pFilename);
    	}
    	catch (T3dException e) {
    	    throw e;
    	}
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