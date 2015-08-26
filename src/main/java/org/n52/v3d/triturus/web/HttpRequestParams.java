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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmLineString;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * todo engl. JavaDoc
 * Hilfsklasse f�r den Zugriff auf die Parameterwerte einer HTTP-Anfrage. Diese Klasse erweitert die Schnittstelle
 * <tt>javax.servlet.ServletRequest</tt> um Geo-spezifische Parameter wie Punkt- und Bounding-Box-Angaben. Zudem ist
 * die Vorbelegung fehlender Parameter m�glich.
 * <p>
 * Unterst�tzt werden die folgenden Typen:<p>
 * <ul>
 * <li><tt>&quot;Integer&quot;</tt>: ganzzahlige Werte, R�ckgabe als <tt>Integer</tt>-Objekte</li>
 * <li><tt>&quot;Double&quot;</tt>: Gleitpunktzahlen, R�ckgabe als <tt>Double</tt>-Objekte</li>
 * <li><tt>&quot;Boolean&quot;</tt>: boolesche Werte, R�ckgabe als <tt>Boolean</tt>-Objekte</li>
 * <li><tt>&quot;String&quot;</tt>: Zeichenketten, R�ckgabe als <tt>String</tt>-Objekte</li>
 * <li><tt>&quot;VgPoint&quot;</tt>: Punkt-Objekte, R�ckgabe als <tt>org.n52.v3d.triturus.vgis.VgPoint</tt>-Objekte</li>
 * <li><tt>&quot;VgEnvelope&quot;</tt>: Bounding-Box-Objekte, R�ckgabe als <tt>org.n52.v3d.triturus.vgis.VgEnvelope</tt>-Objekte</li>
 * <li><tt>&quot;VgLineString&quot;</tt>: Liniengeometrien, R�ckgabe als <tt>org.n52.v3d.triturus.vgis.VgLineString</tt>-Objekte</li>
 * </ul>
 * <p>
 * Punkte, Bounding-Boxes und Liniengeometrien k�nnen dabei in Form Komma-separierter Koordinatenlisten angegeben
 * werden, z. B. <tt>&quot;CENTER=3550000,5750000,50.5&quot;</tt> f�r einen Punkt oder
 * <tt>&quot;BBOX=3500000,5800000,50,3600000,5900000,51&quot;</tt> f�r eine Bounding-Box. Die Angaben f�r die z-Werte
 * sind f�r Punkte und Bounding-Boxes optional, f�r Liniengeometrien ist die Angabe der z-Werte verpflichtend.
 * <p>
 * @see org.n52.v3d.triturus.gisimplm.GmPoint#GmPoint(String)
 * @see org.n52.v3d.triturus.gisimplm.GmEnvelope#GmEnvelope(String)
 * @see org.n52.v3d.triturus.gisimplm.GmLineString#GmLineString(String)
 * @author Benno Schmidt
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
     * f�gt der Parameter-Konfiguration den Parameter mit dem angegebenen Namen hinzu.<p>
     * Bem.: F�r den zugeh�rigen Typ wird "String" als Vorgabewert gesetzt, f�r den Wert ein Leerstring.<p>
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
     * f�gt der Parameter-Konfiguration den Parameter mit dem angegebenen Namen hinzu und setzt zugleich die angegebene
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
     * pr�ft, ob ein gegebener Typname innerhalb seitens der vorliegenden Klasse verarbeitbar ist.<p>
     * @param pType zu pr�fender Typ
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
     * setzt die Typ-Information f�r den angegebenen Parameter. Falls der angegebenen Typ innerhalb seitens der
     * vorliegenden Klasse nicht verarbeitbar ist, wird eine <tt>T3dException</tt> geworfen.<p>
     * Bem.: Ist die Typinformation f�r einen Parameter gesetzt, wird der Parameter bei der Auswertung der HTTP-Anfrage
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
     * liefert die Typ-Information f�r den angegebenen Parameter.<p>
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
     * setzt den Wert f�r den angegebenen Parameter. Diese Methode ist insbesondere dazu geeignet, Vorgabewerte f�r in
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
     * liefert den Wert f�r den angegebenen Parameter.<p>
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

            // sonst R�ckgabe als String-Objekt:
            return val;
        }
    }

    /**
     * setzt die Werte f�r die in einer HTTP-Anfrage angegebenen Parameter.<p>
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