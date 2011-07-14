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

import org.n52.v3d.triturus.core.T3dException;

/**
 * todo engl. JavaDoc
 * �ber diese Klasse ist der Zugriff auf OGC-konforme Web Map Services (WMS) m�glich.<p>
 * <i>Bem.: Diese Klasse ist noch "under construction" (erstmal eine einfachst gehaltene Arbeitsversion).</i><p>
 * @author Benno Schmidt
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
     * f�hrt den GetMap-Request durch. Die Map wird entsprechend der konfigurierten Einstellungen �ber das Web
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
     * liefert den Objekt-internen Konnektor, �ber den die Web-Verbindung aufgebaut wird.<p>
     * @return <tt>IoURLReader</tt>-Objekt
     * @see IoURLReader
     */
    public IoURLReader connector() {
        return mConn;
    }
}