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
package org.n52.v3d.triturus.core;

import java.util.HashMap;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Translation of framework-specific exceptions into error messages. The message text will be read from an ASCII
 * file.<br />
 * Note: The implemented error message mechanism is primarily used by the 52N terrainServer.
 * <br /><br />
 * <i>German:</i>: Klasse zur &Uuml;bersetzung Rahmenwerk-spezifischer Ausnahmen in Fehlermeldungstexte. Die
 * Fehlermeldungstexte sind &uuml;ber eine ASCII-Datei zu konfigurieren, in welcher zeilenweise eine eindeutige
 * Fehlernummer, ein Komma und ein einzeiliger Fehlermeldungstext angegeben sind.<br />
 * Bem.: Die Nutzung der auf Grundlage der ID &uuml;bersetzten Fehlermeldungstexte obliegt der Applikation;
 * Rahmenwerk-intern werden die angegebenen Fehlermeldungen verwendet. Zur Nutzung siehe z. B. Anwendung
 * <tt>org.n52.v3d.coordinatetransform1.suite.terrainservice.povraywts.WebTerrainServlet</tt>. Weitere Hinweise k&ouml;nnen dem
 * Installationshandbuch des 52N terrainServers entnommen werden.<p>
 * @see T3dException
 * @author Benno Schmidt
 */
public class T3dExceptionMessage
{
    private static T3dExceptionMessage sInstance = null;
    private static HashMap sMessages = null;

    private T3dExceptionMessage() {
    }

    /**
     * returns the <tt>T3dExceptionMessage</tt> instance (Singleton implementation).<br /><br />
     * <i>German:</i>: liefert eine Instanz eines <tt>T3dExceptionMessage</tt>-Objektes. Die Klasse ist als Singleton
     * implementiert.
     * @return <tt>T3dExceptionMessage</tt> instance
     */
    static public T3dExceptionMessage getInstance() {
        if (sInstance == null)
            sInstance = new T3dExceptionMessage();
        return sInstance;
    }

    /**
     * reads the configuration file containing the message texts.<br /><br />
     * <i>German:</i>: setzt die Konfigurationsdatei mit den Fehlermeldungstexten. Wird f&uuml;r <tt>pLocation</tt> der
     * Wert <i>null</i> oder ein Leerstring &uuml;bergeben, erfolgt kein Lesevorgang.
     * @param pLocation File name (incl. path) of message file, or valid URL.
     */
    public void readConfiguration(String pLocation)
    {
        if (pLocation == null || pLocation.length() <= 0)
            return;

        InputStream is;
        try {
            if (pLocation.startsWith("http"))
                is = this.createInputStream(new URL(pLocation));
            else
                is = this.createInputStream(pLocation);
        }
        catch (MalformedURLException e) {
            throw new T3dException("Couldn't read location \"" + pLocation + "\" (malformed URL)." );
        } catch (IOException e) {
            throw new T3dException("Couldn't read location \"" + pLocation + "\" (IO error)." );
        }

        BufferedReader pDatRead = this.createBufferedReader(is);

        try {
            sMessages = new HashMap();

            String line = pDatRead.readLine();
            while (line != null)
            {
                int errNo = this.parseErrNo(line);
                if (errNo >= 0) {
                    String errMsg = this.parseErrMsg(line);
                    //System.out.println("Read line \"" + line + "\"");
                    //System.out.println("  -> #" + errNo + ", \"" + errMsg + "\"");
                    sMessages.put(new Integer(errNo), errMsg);
                }

                line = pDatRead.readLine();
            }
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private InputStream createInputStream(URL url) throws IOException {
    	return url.openConnection().getInputStream();
    }

    private InputStream createInputStream(String pFilename) throws FileNotFoundException {
        InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename);
    	if(input == null)
    		input =  new FileInputStream(pFilename);
    	return input;
    }

    private BufferedReader createBufferedReader(InputStream pInputStream) {
        return new BufferedReader( new InputStreamReader(pInputStream) );
    }

    private int parseErrNo(String pLine) {
        String line = pLine.trim();
        if (line.length() <= 0 || line.startsWith("#"))
            return -1;
        int k = pLine.indexOf(",");
        if (k < 0)
            throw new T3dException("Format error in message configuration file (line \"" + pLine + "\"...");
        return Integer.parseInt(pLine.substring(0, k));
    }

    private String parseErrMsg(String pLine) {
        int k = pLine.indexOf(",");
        if (k < 0)
            throw new T3dException("Format error in message configuration file (line \"" + pLine + "\"...");
        return pLine.substring(k + 1);
    }

    /**
     * returns the error message text for a given <tt>Throwable</tt>-object. if this object is a <tt>T3dException</tt>
     * <i>and</i> an error number is available, the correpsonding message text from the error message file will be
     * returned.<br /><br />
     * <i>German:</i> liefert den Fehlermeldungstext f&uml;r das angegebene <tt>Throwable</tt>-Objekt. Falls es sich bei
     * dem angegebenen Objekt um eine <tt>T3dException</tt> handelt <i>und</i> eine Fehlernummer angegeben ist, wird der
     * in der Konfigurationsdatei angegebene Fehlermeldungstext zur&uuml;ckgegeben.
     * @param e <tt>Throwable</tt>-Objekt
     * @return Error message text
     */
    public String translate(Throwable e)
    {
        String res = "";
        if (e == null)
            return res;

        res = e.getMessage();
        if (! (e instanceof T3dException))
            return res;

        if (sInstance == null)
            sInstance = new T3dExceptionMessage();
        int id = ((T3dException) e).getId();
        if (id >= 0) {
            String str = this.lookUpMessage(id);
            if (str != null)
                res = str;
        }

        return res;
    }

    private String lookUpMessage(int id) {
        if (sMessages == null)
            return null;
        String res = (String) sMessages.get(new Integer(id));
        return res;
    }
}
