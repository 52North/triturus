package org.n52.v3d.triturus.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.n52.v3d.triturus.core.T3dException;

/** 
 * Einlesen von Dokumenten &uuml;ber einen Web-Zugriff. Hierbei kann es sich z. B. um Textdateien, HTML- und
 * XML-Dateien, VRML-Dateien oder bin&auml;r kodierte Bitmaps handeln.
 * <p>
 * Bem.: In vielen Fällen kann die Verwendung der Klasse <tt>org.n52.v3d.triturus.web.IoHttpURLReader</tt> vorteilhaft sein.
 * <p>
 * @see IoHttpURLReader
 * @author Benno Schmidt, Martin May<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoURLReader
{
    private String mProtocol = "http";
    private String mURLStr = "";
    private URL mURL = null;
    private String mProxyHost = "";
    private int mProxyPort = -1;
    private static final String mConfigFile = "testdata/CONFIG/proxy.cfg";

    /** 
     * Konstruktor.
     * <p>
     * Voreinstellungsgem&auml;&szlig; wird f&uuml;r den Web-Zugriff kein Proxy-Server verwendet. Ggf. sind &uuml;ber
     * die Methode <tt>setProxy()</tt> der Host-Name des Proxy-Servers und ein Proxy-Port zu spezifizieren.
     * <p>
     * <b>Hinweis:</b> Die Proxy-Einstellungen werden aus <tt>./testdata/config/proxy.cfg</tt> eingelesen, falls
     * vorhanden.</b>
     * <p>
     * @param pProtocol Protokoll f&uuml;r Web-Zugriff (bislang wird nur "http" unterst&uuml;tzt)
     * @param pURL URL f&uuml;r den Web-Request
     */
    public IoURLReader(String pProtocol, String pURL) {
    	mProtocol = pProtocol;
    	mURLStr = pURL;
    	
    	// Proxy-Konfiguration suchen
    	File proxyconf = new File(mConfigFile);
    	if (proxyconf.exists())
    		this.parseProxy(proxyconf);
    }

    /**
     * setzt den URL f&uuml;r den Web-Request.<p>
     * @param pURL URL
     */
    public void setURL(String pURL) {
    	mURLStr = pURL;
    }

    /**
     * liefert den f&uuml;r den Web-Request gesetzten URL.<p>
     * @return URL
     */
    public String getURL() {
    	return mURLStr;
    }

    /** 
     * setzt den Host-Namen oder die IP des Proxy-Servers und den Proxy-Port f&uuml;r den Web-Request. Falls kein
     * Proxy-Server verwendet werden soll, ist als Host-Name f&uuml;r den Proxy-Server ein Leerstring zu &uuml;bergeben
     * (<tt>pProxy = ""</tt>).<p>
     * @param pProxyHost Host-Name des Proxy-Servers
     * @param pProxyPort Proxy-Port or -1, um den Vorgabe-Port des verwendeten Protokolls zu verwenden.
     */
    public void setProxy(String pProxyHost, int pProxyPort) {
    	mProxyHost = pProxyHost;
    	mProxyPort = pProxyPort;
    }

    /**
     * @deprecated -> IoURLReader#setProxyHost
     * setzt den Proxy-Server. Falls kein Proxy-Server verwendet werden soll, ist als Host-Name f&uuml;r den
     * Proxy-Server ein Leerstring zu &uuml;bergeben (<tt>pProxy = ""</tt>).<p>
     * @param pProxyHost Name des Proxy-Servers
     */
    public void setProxy(String pProxyHost) {
    	mProxyHost = pProxyHost;
    }

    /**
     * setzt den Proxy-Server. Falls kein Proxy-Server verwendet werden soll, ist als Host-Name f&uuml;r den
     * Proxy-Server ein Leerstring zu &uuml;bergeben (<tt>pProxy = ""</tt>).<p>
     * @param pProxyHost Name des Proxy-Servers
     */
    public void setProxyHost(String pProxyHost) {
    	mProxyHost = pProxyHost;
    }

    /**
     * @deprecated -> IoURLReader#setProxy
     * setzt die Port-Nummer.<p>
     * @param pPort Port-Nummer
     * @see IoURLReader#setProxyPort
     */
    public void setPort(int pPort) {
    	mProxyPort = pPort;
    }

    /**
     * setzt die Proxy-Port-Nummer.<p>
     * @param pPort Nummer des Proxy-Ports
     * @see IoURLReader#setProxyPort
     */
    public void setProxyPort(int pPort) {
    	mProxyPort = pPort;
    }

    /**
     * @deprecated -> IoURLReader#getProxyHost
     * liefert den gesetzten Proxy-Server. Falls kein Proxy-Server gesetzt ist, wird ein Leerstring
     * zur&uuml;ckgegeben.<p>
     * @return Name des Proxy-Servers
     * @see IoURLReader#setProxyHost
     */
    public String getProxy() {
    	return mProxyHost;
    }

    /**
     * liefert den gesetzten Proxy-Server. Falls kein Proxy-Server gesetzt ist, wird ein Leerstring
     * zur&uuml;ckgegeben.<p>
     * @return Name des Proxy-Servers
     * @see IoURLReader#setProxyHost
     */
    public String getProxyHost() {
    	return mProxyHost;
    }

    /**
     * @deprecated -> IoURLReader#getProxyPort
     * liefert die gesetzte Port-Nummer.<p>
     * @return Port-Nummer
     * @see IoURLReader#setProxy
     */
    public int getPort() {
    	return mProxyPort;
    }

    /**
     * liefert die gesetzte Port-Nummer.<p>
     * @return Port-Nummer
     * @see IoURLReader#setProxyPort
     */
    public int getProxyPort() {
    	return mProxyPort;
    }

    private void parseProxy(File pProxyConf)
    {
    	try {
			BufferedReader proxyIn = new BufferedReader(new FileReader(pProxyConf));
			String line = "";
			while ((line = proxyIn.readLine()) != null) {
				if (line.charAt(0) != '#') {
					if (line.length() > 1) {
						if (this.getProxyHost() == "") {
							this.setProxyHost(line);
						}
						else if (this.getProxyHost() != "" && this.getProxyPort() == -1) {
							this.setProxyPort(Integer.parseInt(line));
						}
					}
				}
			}
			proxyIn.close();
        }
        catch (FileNotFoundException e) {
            // Wenn das nicht da ist, wird die Methode gar nicht aufgerufen...
			e.printStackTrace();
		}
        catch (IOException e) {
			// Wenn das passiert, isses auch wurscht...
			e.printStackTrace();
		}
    }

    private void setURLObj() throws T3dException
    {
        if (mProxyHost != null && mProxyHost.length() > 0) {
            // Proxy-Server verwenden
            try {
                mURL = new URL(mProtocol, mProxyHost, mProxyPort, mURLStr);
            }
            catch (MalformedURLException e) {
            	throw new T3dException("Bad URL: " + mURL);
            }
        }
        else {
            // Zugriff ohne Proxy
            try {
                mURL = new URL(mURLStr);
            }
            catch (MalformedURLException e) {
            	throw new T3dException("Bad URL: " + mURL);
            }
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
            URLConnection lConn = null;
            lConn = mURL.openConnection();
            lConn.connect();
            return lConn.getContentType();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
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
            URLConnection lConn = null;
            lConn = mURL.openConnection();
            lConn.connect();
            return lConn.getContentLength();
        }
        catch (IOException e) {
            throw new T3dException("IO Error: " + e.getMessage());
        }
    }

    /**
     * fragt das Dokument mit der spezifizierten URL ab und speichert den Inhalt in einer Datei.<p>
     * @param pFilename Name (optional mit Pfad) der Zieldatei
     * @return MIME-Typ des gelesenen Inhalts
     * @throws T3dException
     */
    public String getContent(String pFilename) throws T3dException
    {
    	boolean lDebug = false;

    	try {
            this.setURLObj();
        }
        catch (T3dException e) {
            throw e;
        }

        URLConnection lConn = null;
        try {
            lConn = mURL.openConnection();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
        //lConn.setRequestProperty("User-Agent", "conTerraTriturus");
        //lConn.setRequestProperty("Connection", "close");
        //lConn.setRequestProperty("Accept", "*/*");

        String contentType = lConn.getContentType();
        if (lDebug) System.out.println("Content-type = " + contentType);

        if (contentType.startsWith("text/") || contentType.equalsIgnoreCase("application/vnd.ogc.se_xml"))
        {
            if (lDebug) System.out.println("lese Textstrom");

            try {
                String str = new String();
                str = "";
                StringBuffer lBuf = new StringBuffer();
                DataInputStream lData = null;
                lData = new DataInputStream(new BufferedInputStream(lConn.getInputStream()));

                String line;
                while ((line = lData.readLine()) != null) {
                    lBuf.append(line + "\n");
                }
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
            if (lDebug) System.out.println("lese Binärstrom");

            char c = '\200';
            int i = lConn.getContentLength();
            if (i <= 0) {
                i = 0xf4240; // falls nix funkt, versuchsweise mal setzen...
                // System.out.println("Warning: Maybe there is no proper content present (content-length is " + i + ").");
            }

            try {
                InputStream ins = lConn.getInputStream();
                byte abyte0[] = new byte[i + c];
                int j = 0;
                for (int k = 0; j >= 0; k += j) {
                    j = ins.read(abyte0, k, c);
                    if (j == -1)
                        break;
                }

                FileOutputStream outs = new FileOutputStream(pFilename);
                outs.write(abyte0);
                outs.close();
            }
            catch(Exception e) {
                throw new T3dException("IO Error: " + e.getMessage());
            }
        }

        return contentType;
    }
}