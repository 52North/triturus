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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.security.Security;

import org.n52.v3d.triturus.core.T3dException;

import javax.net.ssl.HttpsURLConnection;

/** 
 * todo engl. JavaDoc
 * Einlesen von Dokumenten �ber einen HTTP-Zugriff. Hierbei kann es sich z. B. um Textdateien, HTML- und
 * XML-Dateien, VRML-Dateien oder bin�r kodierte Bitmaps handeln.
 * <p>
 * Die vorliegende Klasse unterscheidet sich onsofern von der Klasse <tt>IoURLReader</tt>, dass die Implementierung
 * statt <tt>java.net.URLConnection</tt> die Klasse <tt>java.net.HttpURLConnection</tt> nutzt, so dass sich
 * HTTP-spezifische M�glichkeiten wie z. B. die Eigenschaften </tt>proxySet</tt>, <tt>http.proxyHost</tt>,
 * <tt>http.proxyPort</tt>, <tt>http.nonProxyHosts</tt>,  etc. oder Authentifizierungsmechanismen nutzen lassen. Siehe
 * dazu Dokumentation der verwendeten Java-API.
 * <p>
 * Beispiel:
 * <pre>
 * Properties sysProps = System.getProperties();
 * sysProps.setProperty("http.proxyHost", "proxy");
 * sysProps.setProperty("http.proxyPort", "8080");
 * sysProps.setProperty("http.nonProxyHosts", "localhost|myserver");
 * </pre>
 * <p>
 * @see IoURLReader
 * @author Benno Schmidt
 */
public class IoHttpURLReader
{
    private String mURLStr = "";
    private URL mURL = null;

    /** 
     * Konstruktor.<p>
     * @param pURL URL f&uuml;r den Web-Request
     */
    public IoHttpURLReader(String pURL) {
    	mURLStr = pURL;
    }

    /**
     * setzt den URL f�r den Web-Request.<p>
     * @param pURL URL
     */
    public void setURL(String pURL) {
    	mURLStr = pURL;
    }

    /**
     * liefert den f�r den Web-Request gesetzten URL.<p>
     * @return URL
     */
    public String getURL() {
    	return mURLStr;
    }

    private void setURLObj() throws T3dException
    {
        try {
            mURL = new URL(mURLStr);
        }
        catch (MalformedURLException e) {
         	throw new T3dException("Bad URL: " + mURL);
        }
    }

    /**
     * liefert den Wert des <tt>content-type</tt>-Header-Feldes.<p>
     * @return MIME-Type, z. B. <tt>"text/html"</tt> oder <tt>"image/jpeg"</tt>
     * @throws T3dException
     */
    public String getContentType() throws T3dException
    {
    	try {
            this.setURLObj();
        }
        catch (T3dException e) {
            throw e;
        }
        
        try {
            HttpURLConnection lConn = null;
            lConn = (HttpURLConnection) mURL.openConnection();
            lConn.connect();
            return lConn.getContentType();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
        // todo: Schlie�en der Verbindung?
    }

    /** 
     * liefert den Wert des <tt>content-length</tt>-Header-Feldes.<p>
     * Bem.: Einige Server senden die L?nge nur, falls eine bin&auml;re Datei geschickt wird; d. h., bei Textdateien
     * fehlt diese Information.<p>
     * @return Anzahl vorhandener Bytes
     * @throws T3dException
     */
    public int getContentLength() throws T3dException
    {
    	try {
            this.setURLObj();
        }
        catch (T3dException e) {
            throw e;
        }
        
        try {
            HttpURLConnection lConn = null;
            lConn = (HttpURLConnection) mURL.openConnection();
            lConn.connect();
            return lConn.getContentLength();
        }
        catch (IOException e) {
            throw new T3dException("IO Error: " + e.getMessage());
        }
        // todo: Schlie�en der Verbindung?
    }

    /**
     * fragt das Dokument mit der spezifizierten URL ab und speichert den Inhalt in einer Datei.<p>
     * @param pFilename Name (optional mit Pfad) der Zieldatei
     * @throws T3dException
     */
    public void getContent(String pFilename) throws T3dException
    {
    	boolean lDebug = false;

    	try {
            this.setURLObj();
        }
        catch (T3dException e) {
            throw e;
        }

        HttpURLConnection lConn = null;
        try {
            lDebug = true;
            boolean lHttpsTest = false; // todo: wieder auf false setzen
            if (lHttpsTest) {
                System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                lConn = (HttpsURLConnection) mURL.openConnection();
            } else
                lConn = (HttpURLConnection) mURL.openConnection();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
        //lConn.setRequestProperty("User-Agent", "conTerraTriturus");
        //lConn.setRequestProperty("Connection", "close");
        //lConn.setRequestProperty("Accept", "*/*");

        if (lDebug) System.out.println("Content-type = " + lConn.getContentType());

        if (lConn.getContentType() != null && lConn.getContentType().startsWith("text/"))
        {
            if (lDebug) System.out.println("lese Textstrom");

            try {
                String str = new String();
                str = "";
                StringBuffer lBuf = new StringBuffer();
                DataInputStream lData = null;
                lData = new DataInputStream(new BufferedInputStream(lConn.getInputStream()));

                String line;
                while ((line = lData.readLine()) != null)
                    lBuf.append(line + "\n");
                str = str + lBuf.toString();
                if (lDebug) System.out.println(str);

                // Datei schreiben:
                FileWriter lFileWrite = new FileWriter(pFilename);
                BufferedWriter lDat = new BufferedWriter(lFileWrite);
                lDat.write(str);
                lDat.close();
            }
            catch (IOException e) {
                throw new T3dException("IO Error: " + e.getMessage());
            }
        }
        else
        {
            if(lDebug)
                System.out.println("lese Bin�rstrom");
            char c = '\200';
            int i = lConn.getContentLength();
            if (i <= 0) {
                i = 0xf4240; // falls nix funkt, versuchsweise mal setzen...
                // System.out.println("Warning: Maybe there is no proper content present (content-length is " + i + ").");
            }

            try {
                InputStream inputstream = lConn.getInputStream();
                byte abyte0[] = new byte[i + c];
                int j = 0;
                for (int k = 0; j >= 0; k += j) {
                    j = inputstream.read(abyte0, k, c);
                    if (j == -1)
                        break;
                }

                FileOutputStream fileoutputstream = new FileOutputStream(pFilename);
                fileoutputstream.write(abyte0);
                fileoutputstream.close();
            }
            catch(Exception e) {
                throw new T3dException("IO Error: " + e.getMessage());
            }
        }
    }
}