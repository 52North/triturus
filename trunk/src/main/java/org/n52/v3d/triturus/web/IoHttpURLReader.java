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