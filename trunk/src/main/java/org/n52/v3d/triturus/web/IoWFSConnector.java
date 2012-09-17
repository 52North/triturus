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
 * �ber diese Klasse ist der Zugriff auf OGC-konforme Web Feature Services (WFS) m�glich.<p>
 * <i>Bem.: Diese Klasse ist noch nicht implementiert.</i><p>
 * @author Benno Schmidt
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
     * liefert den Objekt-internen Konnektor, �ber den die Web-Verbindung aufgebaut wird.<p>
     * @see IoURLReader
     * @return <tt>IoURLReader</tt>-Objekt
     */
    public IoURLReader connector() {
        return mConn;
    }
}