package org.n52.v3d.triturus.t3dutil.operatingsystem;

import org.n52.v3d.triturus.core.T3dException;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Hilfsklasse zum Einlesen von Properties-Dateien.<p>
 * @author Benno Schmidt<br>
 * (c) 2006, con terra GmbH<br>
 */
public class PropertyLoader {
    private Log sLogger = LogFactory.getLog(PropertyLoader.class);

    private static PropertyLoader mPropertyLoader = new PropertyLoader();
    private HashMap mPropertiesMap;

    /**
     * liefert die <tt>PropertyLoader</tt>-Instanz.<p>
     * Bem.: Der <tt>PropertyLoader</tt> ist als Singleton implementiert.<p>
     * @return <tt>PropertyLoader</tt>-Objekt
     */
    public static PropertyLoader getInstance() {
        return mPropertyLoader;
    }

    // privater Konstruktor:
    private PropertyLoader() {
        mPropertiesMap = new HashMap();
    }

    public Set getPropertyKeys() {
        return mPropertiesMap.keySet();
    }

    /**
     * liest die Properties aus der angegebenen Datei.<p>
     * Falls sich die Properties-Datei im Verzeichnis Verzeichnis <tt>WEB-INF/classes</tt> einer Web-Anwendung befindet,
     * ist für den Parameter <tt>pPathComplete</tt> der Wert <i>false</i> zu übergeben, <tt>pFileName</tt> enthält dabei
     * keine Pfadangabe.<p>
     * @param pFileName Dateiname
     * @param pPathComplete <i>true</i>, falls der vollständige Dateipfad angegeben wird, sonst <i>false</i>.
     * @throws IOException
     */
    public void loadProperties(String pFileName, boolean pPathComplete) throws IOException {
        sLogger.debug("loadProperties(file=\"" + pFileName + "\")");

        String lFileName = pFileName;
        if (! pPathComplete)
            lFileName = this.getCompleteFileNameForPropertyFile(pFileName);
        this.readProperties(new FileInputStream(new File(lFileName)));
    }

    /**
     * liest die Properties aus der angegebenen Datei. Diese Methodenaufruf entspricht
     * <tt>this.loadProperties(pFileName, false)</tt>.<p>
     * @param pFileName Dateiname
     * @throws IOException
     */
    public void loadProperties(String pFileName) throws IOException {
        this.loadProperties(pFileName, false);
    }

    public void loadPropertiesWithClassLoader(String pFileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(pFileName);
        this.readProperties(in);
    }

    private void readProperties(InputStream pStream) throws IOException {
        sLogger.debug("loadProperties(<stream>)");

        Properties properties = new Properties();
        properties.load(pStream);
        Iterator it = properties.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            mPropertiesMap.put(key, properties.getProperty(key));
        }
    }

    /**
     * liefert den vollständigen Pfad der gegebenen Properties-Datei.<p>
     * @param pFileName Dateiname
     * @return vollständige Pfadangabe
     * @throws FileNotFoundException
     */
    public String getCompleteFileNameForPropertyFile(String pFileName) throws FileNotFoundException {
        URL url = PropertyLoader.class.getResource(pFileName);
        if (url == null)
            throw new FileNotFoundException("File \"" + pFileName + "\" not found!");
        return url.getFile();
    }

    public URL getURLForPropertyFile(String pFileName) throws FileNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(pFileName);
        if (url == null)
            throw new FileNotFoundException("File \"" + pFileName + "\" not found!");
        return url;
    }

    /**
     * liefert den Wert des angegebenen Schlüssels (Property).<p>
     * @param pKey Schlüssel (Property-Name)
     * @return Wert als Zeichenkette
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public String getProperty(String pKey)   {
        Object property = mPropertiesMap.get(pKey);
        if (property == null)
            throw new T3dException("Could not get key \"" + pKey + "\" from given properties-file!");
        return (String) property;
    }

    /**
     * liefert den Wert des angegebenen Schlüssels (Property). Es wird davon ausgegangen, dass für den Schlüssel ein
     * ganzzahliger Wert angegeben ist.<p>
     * @param pKey Schlüssel (Property-Name)
     * @return Wert als Ganzzahl
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public int getIntProperty(String pKey) throws T3dException {
        String property = this.getProperty(pKey);
        return Integer.parseInt(property);
    }

    /**
     * liefert den Wert des angegebenen Schlüssels (Property). Es wird davon ausgegangen, dass für den Schlüssel ein
     * boolescher Wert angegeben ist.<p>
     * @param pKey Schlüssel (Property-Name)
     * @return boolescher Wert
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public boolean getBooleanProperty(String pKey) throws T3dException {
        String property = this.getProperty(pKey);
        return Boolean.valueOf(property).booleanValue();
    }

    /**
     * liefert den Wert des angegebenen Schlüssels (Property). Falls der Schlüssel nicht vorhanden ist, wird der
     * übergebene Vorgabewert zurückgegeben.<p>
     * @param pKey Schlüssel (Property-Name)
     * @param pDefaultValue Vorgabewert
     * @return Wert als Zeichenkette
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public String getProperty(String pKey, String pDefaultValue) {
        try {
            return this.getProperty(pKey);
        } catch (T3dException e) {
            sLogger.error("Key \"" + pKey + "\" not present! Default-value \""+ pDefaultValue + "\" will be used...");
            return pDefaultValue;
        }
    }

    public int getIntProperty(String pKey, int pDefaultValue) {
        try {
            return Integer.parseInt(this.getProperty(pKey));
        } catch (T3dException e) {
            sLogger.error("Key \"" + pKey + "\" not available! Default-value \""+ pDefaultValue + "\" will be used...");
        } catch (NumberFormatException e) {
            sLogger.error("Number format exception for key \"" + pKey + "\"! Default-value \""+ pDefaultValue + "\" will be used...");
        }
        return pDefaultValue;
    }

    public double getDoubleProperty(String pKey, double pDefaultValue) {
        try {
            return Double.parseDouble(this.getProperty(pKey));
        } catch (T3dException e) {
            sLogger.error("Key \"" + pKey + "\" not available! Default-value \""+ pDefaultValue + "\" will be used...");
        } catch (NumberFormatException e) {
            sLogger.error("Number format exception for key \"" + pKey + "\"! Default-value \""+ pDefaultValue + "\" will be used...");
        }
        return pDefaultValue;
    }

    public boolean getBooleanProperty(String pKey, boolean pDefaultValue) {
        try {
            String lBoolValue = this.getProperty(pKey);
            if (lBoolValue != null && (lBoolValue.equalsIgnoreCase("true") || lBoolValue.equalsIgnoreCase("false"))) {
                return Boolean.valueOf(lBoolValue).booleanValue();
            } else {
                return pDefaultValue;
            }
        } catch (T3dException e) {
            sLogger.error("Key \"" + pKey + "\" not available! Default-value \""+ pDefaultValue + "\" will be used...");
            return pDefaultValue;
        }
    }

    public HashMap getPropertiesForPrefix(String pPrefix, HashMap pMap) {
        Iterator it = mPropertiesMap.keySet().iterator();
        pMap.clear();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.indexOf(pPrefix) == 0)
                pMap.put(key, mPropertiesMap.get(key));
        }
        return pMap;
    }

    public HashMap getPropertiesForPrefix(String pPrefix) {
        return this.getPropertiesForPrefix(pPrefix, new HashMap());
    }

    /**
     * liefert die Anzahl der eingelesenen Properties.<p>
     * @return Anzahl > 0
     */
    public int countLoadedProperies() {
        return mPropertiesMap.size();
    }

    /**
     * holt eine Property aus der Properties-Map. Die Methode gibt jedoch keine Exception in das Logfile, falls eine
     * Property nicht vorhanden ist, da keine <tt>T3dException</tt> geworfen wird.<p>
     * Falls der Schlüssel nicht vorhanden ist, wird der übergebene Vorgabewert zurückgegeben.<p>
     * @param pKey Schlüssel (Property-Name)
     * @param pDefaultValue Vorgabewert
     * @return Wert als Zeichenkette
     */
    public String getPropertySilently(String pKey, String pDefaultValue) {
        Object property = mPropertiesMap.get(pKey);
        if (null == property)
            return pDefaultValue;
        else
            return (String) property;

    }

    public String getPropertySilently(String pKey) {
        return getPropertySilently(pKey, null);
    }
}
