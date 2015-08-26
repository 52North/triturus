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
 * Provides Web-based document access. These documents might be text documents, HTML or XML documents, VRML scenes or
 * binary codes bitmaps.
 * <p>
 * Note: Often, it is advantageous to use the class <tt>org.n52.v3d.triturus.web.IoHttpURLReader</tt>.
 *
 * @see IoHttpURLReader
 * @author Benno Schmidt, Martin May
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
     * Constructor.
     * <p>
     * By default, no proxy server is configured. Proxies might be configured using the <tt>setProxy()</tt> method.
     * <p>
     * Note:Proxy-setting will be read from the file <tt>./testdata/config/proxy.cfg</tt>, if present.
     *
     * @param pProtocol Protocol used for Web-access (currently, only "http" is supported)
     * @param pURL web request URL
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
     * sets the URL used to perform the Web request.
     *
     * @param pURL URL
     */
    public void setURL(String pURL) {
    	mURLStr = pURL;
    }

    /**
     * gets the URL used to perform the Web request.
     *
     * @return URL
     */
    public String getURL() {
    	return mURLStr;
    }

    /** 
     * allows to specify the host name or the IP of the proxy-server and the proxy-port that will be used to perform the
     * Web request. In case no proxy server shall be used, an empty string should be passed as host-name
     * (<tt>pProxy = ""</tt>).
     *
     * @param pProxyHost Host name of proxy-server
     * @param pProxyPort Proxy-port or -1 to use the default port of the specified protocol
     */
    public void setProxy(String pProxyHost, int pProxyPort) {
    	mProxyHost = pProxyHost;
    	mProxyPort = pProxyPort;
    }

    /**
     * @deprecated -> IoURLReader#setProxyHost
     * @see IoURLReader#setProxyHost
     *
     * @param pProxyHost Host name of proxy-server
     */
    public void setProxy(String pProxyHost) {
    	mProxyHost = pProxyHost;
    }

    /**
     * specifies the proxy-server. In case no proxy server shall be used, an empty string should be passed as host-name
     * (<tt>pProxy = ""</tt>).
     *
     * @param pProxyHost Host name of proxy-server
     */
    public void setProxyHost(String pProxyHost) {
    	mProxyHost = pProxyHost;
    }

    /**
     * @deprecated -> IoURLReader#setProxy
     * @see IoURLReader#setProxyPort
     *
     * @param pPort Proxy-port or -1 to use the default port of the specified protocol
     */
    public void setPort(int pPort) {
    	mProxyPort = pPort;
    }

    /**
     * specifies the proxy-port.
     *
     * @param pPort Proxy-port number
     * @see IoURLReader#setProxyPort
     */
    public void setProxyPort(int pPort) {
    	mProxyPort = pPort;
    }

    /**
     * @deprecated -> IoURLReader#getProxyHost
     * @see IoURLReader#setProxyHost
     *
     * @return Host name of proxy-server
     */
    public String getProxy() {
    	return mProxyHost;
    }

    /**
     * gets the set proxy-server's name or IP. If no proxy-server has been specified, an empty string will be returned.
     *
     * @return Proxy-server name or IP
     * @see IoURLReader#setProxyHost
     */
    public String getProxyHost() {
    	return mProxyHost;
    }

    /**
     * @deprecated -> IoURLReader#getProxyPort
     * @see IoURLReader#setProxy
     *
     * @return Port number
     */
    public int getPort() {
    	return mProxyPort;
    }

    /**
     * returns the set port number.
     *
     * @return Port number
     * @see IoURLReader#setProxyPort
     */
    public int getProxyPort() {
    	return mProxyPort;
    }

    private void parseProxy(File pProxyConf)
    {
    	try {
			BufferedReader proxyIn = new BufferedReader(new FileReader(pProxyConf));
			String line;
			while ((line = proxyIn.readLine()) != null) {
				if (line.charAt(0) != '#') {
					if (line.length() > 1) {
						if ("".equalsIgnoreCase(this.getProxyHost())) {
							this.setProxyHost(line);
						}
						else if (!("".equalsIgnoreCase(this.getProxyHost()) && this.getProxyPort() == -1)) {
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
     * gets the content of the header field <tt>content-type</tt>.
     *
     * @return MIME type, e.g. <tt>"text/html"</tt> or <tt>"image/jpeg"</tt>
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
            URLConnection lConn;
            lConn = mURL.openConnection();
            lConn.connect();
            return lConn.getContentType();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
    }

    /** 
     * gets the content of the header field <tt>content-length</tt>.
     * <p>
     * Note: Some server send the length information for binary files only. For text documents, this information might
     * not be present.
     *
     * @return Number of bytes
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
            URLConnection lConn;
            lConn = mURL.openConnection();
            lConn.connect();
            return lConn.getContentLength();
        }
        catch (IOException e) {
            throw new T3dException("IO Error: " + e.getMessage());
        }
    }

    /**
     * requests the document with the specified URL ab and writes the document's content to a file.
     *
     * @param pFilename Name of target file (file path)
     * @return MIME-type of read content
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

        URLConnection lConn;
        try {
            lConn = mURL.openConnection();
        }
        catch (IOException e) {
             throw new T3dException("IO Error: " + e.getMessage());
        }
        //lConn.setRequestProperty("User-Agent", "52n Triturus");
        //lConn.setRequestProperty("Connection", "close");
        //lConn.setRequestProperty("Accept", "*/*");

        String contentType = lConn.getContentType();
        if (lDebug) System.out.println("Content-type = " + contentType);

        if (contentType.startsWith("text/") || contentType.equalsIgnoreCase("application/vnd.ogc.se_xml"))
        {
            if (lDebug) System.out.println("read text stream");

            try {
                String str = "";
                StringBuffer lBuf = new StringBuffer();
                DataInputStream lData = new DataInputStream(new BufferedInputStream(lConn.getInputStream()));

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
            if (lDebug) System.out.println("read binary stram");

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
                // Curiously, this method might throw an java.lang.IndexOutOfBoundsException for large
                // documents (k >= 1.000.000). (?)

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