package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;

/**
 * @deprecated
 * Über diese Klasse ist der Zugriff auf einen speziellen Dienst für den Kartenblatt-orientierten Zugriff auf
 * Geländemodelle möglich.<p>
 * <i>Bem.: Diese Klasse ist noch "under construction" (erstmal eine einfachst gehaltene Arbeitsversion).</i><p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoDEMTileServiceConnector
{
    private DEMTileRequestConfig mRequCfg;
    private IoURLReader mConn;
    
    /**
     * Konstruktor.<p>
     * @param pRequestConfig initiale Request-Konfiguration
     */
    public IoDEMTileServiceConnector(DEMTileRequestConfig pRequestConfig) 
    {
    	mConn = new IoURLReader( "http", "" );
    	mRequCfg = pRequestConfig;	
    }
    
    /**
     * setzt die Request-Konfiguration.
     * @param pRequestConfig ...
     */
    public void setRequestConfiguration(DEMTileRequestConfig pRequestConfig) {
    	mRequCfg = pRequestConfig;
    }

    /**
     * liefert die aktuelle Request-Konfiguration.
     */
    public DEMTileRequestConfig getRequestConfiguration() {
    	return mRequCfg;
    }
    
    /**
     * führt den GetDEM-Request durch. Das Geländemodell wird entsprechend der konfigurierten
     * Einstellungen über das Web abgefragt (HTTP) und unter dem angegebenen Dateinamen
     * gespeichert.
     * @param pFilename Dateiname
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void getDEM(String pFilename) throws T3dException
    {
    	mConn.setURL( mRequCfg.getDEMRequestURL() );
    	
    	try {
    	    mConn.getContent( pFilename );
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