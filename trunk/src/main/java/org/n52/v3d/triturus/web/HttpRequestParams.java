package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmLineString;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * Hilfsklasse für den Zugriff auf die Parameterwerte einer HTTP-Anfrage. Diese Klasse erweitert die Schnittstelle
 * <tt>javax.servlet.ServletRequest</tt> um Geo-spezifische Parameter wie Punkt- und Bounding-Box-Angaben. Zudem ist
 * die Vorbelegung fehlender Parameter möglich.
 * <p>
 * Unterstützt werden die folgenden Typen:<p>
 * <ul>
 * <li><tt>&quot;Integer&quot;</tt>: ganzzahlige Werte, Rückgabe als <tt>Integer</tt>-Objekte</li>
 * <li><tt>&quot;Double&quot;</tt>: Gleitpunktzahlen, Rückgabe als <tt>Double</tt>-Objekte</li>
 * <li><tt>&quot;Boolean&quot;</tt>: boolesche Werte, Rückgabe als <tt>Boolean</tt>-Objekte</li>
 * <li><tt>&quot;String&quot;</tt>: Zeichenketten, Rückgabe als <tt>String</tt>-Objekte</li>
 * <li><tt>&quot;VgPoint&quot;</tt>: Punkt-Objekte, Rückgabe als <tt>org.n52.v3d.triturus.vgis.VgPoint</tt>-Objekte</li>
 * <li><tt>&quot;VgEnvelope&quot;</tt>: Bounding-Box-Objekte, Rückgabe als <tt>org.n52.v3d.triturus.vgis.VgEnvelope</tt>-Objekte</li>
 * <li><tt>&quot;VgLineString&quot;</tt>: Liniengeometrien, Rückgabe als <tt>org.n52.v3d.triturus.vgis.VgLineString</tt>-Objekte</li>
 * </ul>
 * <p>
 * Punkte, Bounding-Boxes und Liniengeometrien können dabei in Form Komma-separierter Koordinatenlisten angegeben
 * werden, z. B. <tt>&quot;CENTER=3550000,5750000,50.5&quot;</tt> für einen Punkt oder
 * <tt>&quot;BBOX=3500000,5800000,50,3600000,5900000,51&quot;</tt> für eine Bounding-Box. Die Angaben für die z-Werte
 * sind für Punkte und Bounding-Boxes optional, für Liniengeometrien ist die Angabe der z-Werte verpflichtend.
 * <p>
 * @see org.n52.v3d.triturus.gisimplm.GmPoint#GmPoint(String)
 * @see org.n52.v3d.triturus.gisimplm.GmEnvelope#GmEnvelope(String)
 * @see org.n52.v3d.triturus.gisimplm.GmLineString#GmLineString(String)
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class HttpRequestParams
{
    private ArrayList mParameters = new ArrayList(); // Parameter-Namen
    private ArrayList mValues = new ArrayList(); // Parameter-Werte als String
    private ArrayList mTypes = new ArrayList(); // Parameter-Typen

    private int getIndex(String pParameter)
    {
        for (int i = 0; i < mParameters.size(); i++) {
            if (((String) mParameters.get(i)).equalsIgnoreCase(pParameter))
                return i;
        }
        return -1;
    }

    /**
     * fügt der Parameter-Konfiguration den Parameter mit dem angegebenen Namen hinzu.<p>
     * Bem.: Für den zugehörigen Typ wird "String" als Vorgabewert gesetzt, für den Wert ein Leerstring.<p>
     * @param pParam Parametername
     */
    public void addParameter(String pParam)
    {
        int i = this.getIndex(pParam);
        if (i < 0) {
            mParameters.add(pParam);
            mValues.add("");
            mTypes.add("String");
        }
    }

    /**
     * fügt der Parameter-Konfiguration den Parameter mit dem angegebenen Namen hinzu und setzt zugleich die angegebene
     * Typ-Information und den angegebenen Wert.<p>
     * @param pParam Parametername
     * @param pType Parametertyp
     * @param pVal Parameterwert
     * @throws T3dException
     * @see HttpRequestParams#setType
     * @see HttpRequestParams#setParameterValue
     */
    public void addParameter(String pParam, String pType, String pVal)
    {
        this.addParameter(pParam);
        try {
            this.setType(pParam, pType);
        }
        catch (T3dException e) {
            throw e;
        }
        this.setParameterValue(pParam, pVal);
    }

    /**
     * prüft, ob ein gegebener Typname innerhalb seitens der vorliegenden Klasse verarbeitbar ist.<p>
     * @param pType zu prüfender Typ
     */
    public boolean isTypeImplemented(String pType)
    {
        if (pType.equalsIgnoreCase("Boolean")) return true;
        if (pType.equalsIgnoreCase("Integer")) return true;
        if (pType.equalsIgnoreCase("Double")) return true;
        if (pType.equalsIgnoreCase("String")) return true;
        if (pType.equalsIgnoreCase("VgPoint")) return true;
        if (pType.equalsIgnoreCase("VgEnvelope")) return true;
        if (pType.equalsIgnoreCase("VgLineString")) return true;
        return false;
    }

    /**
     * setzt die Typ-Information für den angegebenen Parameter. Falls der angegebenen Typ innerhalb seitens der
     * vorliegenden Klasse nicht verarbeitbar ist, wird eine <tt>T3dException</tt> geworfen.<p>
     * Bem.: Ist die Typinformation für einen Parameter gesetzt, wird der Parameter bei der Auswertung der HTTP-Anfrage
     * automatisch geparst.<p>
     * @param pParam Parametername
     * @param pType <tt>"Boolean", "Integer", "Double", "String", "VgPoint", "VgEnvelope" </tt> oder <tt>"VgLineString"</tt>
     * @see HttpRequestParams#isTypeImplemented
     */
    public void setType(String pParam, String pType) throws T3dException
    {
        if (!this.isTypeImplemented(pType))
            throw new T3dException("Request parameter type \"" + pType + "\" (" + pParam + ") is not implemented .");

        int i = this.getIndex(pParam);
        if (i < 0) {
            this.addParameter(pParam);
            i = this.getIndex(pParam);
        }
        mTypes.set(i, pType);
    }

    /**
     * liefert die Typ-Information für den angegebenen Parameter.<p>
     * @param pParam Parametername
     * @return <tt>"Boolean", "Integer", "Double", "String", "VgPoint", "VgEnvelope" </tt> oder <tt>"VgLineString"</tt>
     */
    public String getType(String pParam)
    {
        int i = this.getIndex(pParam);
        if (i < 0)
            return null;
        else
            return (String) mTypes.get(i);
    }

    /**
     * setzt den Wert für den angegebenen Parameter. Diese Methode ist insbesondere dazu geeignet, Vorgabewerte für in
     * einer HTTP-Anfrage fehlende Parameter zu setzen.<p>
     * @param pParam Parametername
     * @param pVal Wert als Zeichenkette
     */
    public void setParameterValue(String pParam, String pVal)
    {
        int i = this.getIndex(pParam);
        if (i < 0) {
            this.addParameter(pParam);
            i = this.getIndex(pParam);
        }
        mValues.set(i, pVal);
    }

    /**
     * liefert den Wert für den angegebenen Parameter.<p>
     * @param pParam Parametername
     * @return Wert entsprechend dem spezifizierten Parameter-Typ
     * @see HttpRequestParams#setType
     */
    public Object getParameterValue(String pParam)
    {
        int i = this.getIndex(pParam);
        if (i < 0)
            return null;
        else {
            String val = (String) mValues.get(i);
            if (val == null)
                return null;
            String type = (String) mTypes.get(i);
            if (! this.isTypeImplemented(type))
                throw new T3dException("Request parameter type \"" + type + "\" (" + pParam + ") is not implemented .");

            try {
                if (type.equalsIgnoreCase("Boolean")) {
                    try {
                        int numVal = Integer.valueOf(val).intValue();
                        if (numVal > 0)
                            return new Boolean(true);
                    }
                    catch (Exception e) {
                        ;
                    }
                    return Boolean.valueOf(val);
                }
                if (type.equalsIgnoreCase("Integer"))
                    return Integer.valueOf(val);
                if (type.equalsIgnoreCase("Double"))
                    return Double.valueOf(val);
            }
            catch (Exception e) {
                throw new T3dException("Cannot parse " + type + " value from \"" + val + "\" " +
                    "(parameter " + pParam + ").");
            }

            try {
                if (type.equalsIgnoreCase("VgPoint"))
                    return new GmPoint(val);
                if (type.equalsIgnoreCase("VgEnvelope"))
                    return new GmEnvelope(val);
                if (type.equalsIgnoreCase("VgLineString"))
                    return new GmLineString(val);
            }
            catch (T3dException e) {
                throw new T3dException("Cannot parse geo-coordinates from \"" + val + "\" " +
                    "(parameter " + pParam + ").");
            }

            // sonst Rückgabe als String-Objekt:
            return val;
        }
    }

    /**
     * setzt die Werte für die in einer HTTP-Anfrage angegebenen Parameter.<p>
     * @param pReq HTTP-Anfrage-Objekt
     */
    public void fetchRequestParameters(HttpServletRequest pReq)
    {
        Enumeration lParams = pReq.getParameterNames();
        while (lParams.hasMoreElements()) {
            String lParamName = (String) lParams.nextElement();
            String[] lParamVals = pReq.getParameterValues(lParamName);
            if (lParamVals != null && lParamVals.length >= 1)
                this.setParameterValue(lParamName, lParamVals[0]); // ersten Parameter-Wert setzen
        }
    }
}