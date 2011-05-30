package org.n52.v3d.triturus.core;

import java.util.HashMap;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Klasse zur Übersetzung Rahmenwerk-spezifischer Ausnahmen in Fehlermeldungstexte.<p>
 * Die Fehlermeldungstexte sind über eine ASCII-Datei zu konfigurieren, in welcher zeilenweise eine eindeutige
 * Fehlernummer, ein Komma und ein einzeiliger Fehlermeldungstext angegeben sind.<p>
 * Bem.: Die Nutzung der auf Grundlage der ID übersetzten Fehlermeldungstexte obliegt der Applikation;
 * Rahmenwerk-intern werden die angegebenen Fehlermeldungen verwendet. Zur Nutzung siehe z. B. Anwendung
 * <tt>org.n52.v3d.conterra.suite.terrainservice.povraywts.WebTerrainServlet</tt>. Weitere Hinweise können dem
 * Installationshandbuch des <i>sdi-suite terrainServers</i> entnommen werden.<p>
 * @see T3dException
 * @author Benno Schmidt<br>
 * (c) 2005, con terra GmbH<br>
 */
public class T3dExceptionMessage
{
    private static T3dExceptionMessage sInstance = null;
    private static HashMap sMessages = null;

    private T3dExceptionMessage() {
    }

    /**
     * liefert eine Instanz eines <tt>T3dExceptionMessage</tt>-Objektes. Die Klasse ist als Singleton implementiert.<p>
     * @return <tt>T3dExceptionMessage</tt>-Instanz
     */
    static public T3dExceptionMessage getInstance() {
        if (sInstance == null)
            sInstance = new T3dExceptionMessage();
        return sInstance;
    }

    /**
     * setzt die Konfigurationsdatei mit den Fehlermeldungstexten.<p>
     * Wird für <tt>pLocation</tt> der Wert <i>null</i> oder ein Leerstring übergeben, erfolgt kein Lesevorgang.<p>
     * @param pLocation Pfad, unter dem die Datei abgelegt ist, oder gültiger URL.
     */
    public void readConfiguration(String pLocation)
    {
        if (pLocation == null || pLocation.length() <= 0)
            return;

        InputStream is = null;
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
        catch (T3dException e) {
            throw e;
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
     * liefert den Fehlermeldungstext für das angegebene <tt>Throwable</tt>-Objekt. Falls es sich bei dem angegebenen
     * Objekt um eine <tt>T3dException</tt> handelt <i>und</i> eine Fehlernummer angegeben ist, wird der in der
     * Konfigurationsdatei angegebene Fehlermeldungstext zurückgegeben.<p>
     * @param e <tt>Throwable</tt>-Objekt
     * @return Fehlermeldungstext
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
