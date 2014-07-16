/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * If the program is linked with libraries which are licensed under one of the         *
 * following licenses, the combination of the program with the linked library is not   *
 * considered a "derivative work" of the program:                                      *
 *                                                                                     *
 *   - Apache License, version 2.0                                                     *
 *   - Apache Software License, version 1.0                                            *
 *   - GNU Lesser General Public License, version 3                                    *
 *   - Mozilla Public License, versions 1.0, 1.1 and 2.0                               *
 *   - Common Development and Distribution License (CDDL), version 1.0                 *
 *                                                                                     *
 * Therefore the distribution of the program linked with libraries licensed under      *
 * the aforementioned licenses, is permitted by the copyright holders if the           *
 * distribution is compliant with both the GNU General Public License version 2 and    *
 * the aforementioned licenses.                                                        *
 *                                                                                     *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY     *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A     *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.            *
 *                                                                                     *
 **************************************************************************************/
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstract base class for geo-referenced cross-sections.<br /><br />
 * <i>German:</i> Abstrakte Basisklasse zur Verwaltung georeferenzierter Profile. Unter einem <i>Profil</i> wird dabei
 * eine Linienzug-Geometrie (2D-Polylinie als <i>Definitionslinie</i>) verstanden, zu deren Laufl&auml;nge ein
 * Stationierungsparameter t verwaltet wird. Jedem t-Wert innerhalb des Belegungsbereichs ist dabei ein 
 * eindeutiger Wert f(t) zugeordnet.<br />
 * Bei den Werten f(t) kann es sich insbesondere um H&ouml;henwerte handeln. Daher werden sie innerhalb des
 * vorliegenden Rahmenwerks als z-Werte bezeichnet.<br />
 * Der <i>Belegungsbereich</i> bezeichnet den Bereich, in dem zu der Definitionslinie z-Werte vorliegen. 
 * Vorliegend ist dieser auf eine Intervall der Gestalt [t_min, t_max] beschr&auml;nkt. Ggf. ist zu sicherzustellen,
 * dass diese Beschreibungsform ausreichend ist. Der Fall unbelegter Bereiche kann z. B. dann auftreten, 
 * wenn ein Teil der Definitionslinie des Profils au&szlig;erhalb eines H&ouml;hengitters liegt und somit dort keine
 * z-Werte interpoliert werden k&ouml;nnen.<br />
 * Bem.: Es wird vorausgesetzt, dass die &uuml;ber die Methode <tt>getTZPair</tt> abrufbare Folge der t-z-Wertepaare
 * bez�glich t stets monoton w&auml;chst. Die Einhaltung dieser Bedingung durch die implementierenden Klassen ist
 * stets zu gew&auml;hrleisten.<br />
 * <i>TODO: Die aktuelle Modellierung unterst�zt nur die Verwaltung eines Werteverlaufs z(t) je Profil. Zuk�nftig k�nnte
 * dies erweitert werden! -> Benno</i>
 * @author Benno Schmidt
 */
abstract public class VgProfile extends VgFeature 
{
    private VgLineString mGeom = null; // Modellierung von VgProfile als VgLineString-Dekorierer

    /**
     * sets the base-line.<br /><br />
     * <i>German:</i> setzt die Definitionslinie des Profils.<br />
     * Bem.: Die z-Werte dieser Geometrie sind ohne Bedeutung.
     * @param pGeom <tt>VgLineString</tt> object
     */
    public void setGeometry(VgLineString pGeom) {
    	mGeom = pGeom;
    }
    
    /** 
     * returns the base-line.<br /><br />
     * <i>German:</i> liefert die Definitionslinie des Profils.
     * @return <tt>VgLineString</tt> object
     */
    public VgGeomObject getGeometry() {
    	return mGeom; 
    }  

    /**
     * returns the number of position vertices.<br /><br />
     * <i>German:</i> liefert die Anzahl der Stationsstellen des Profils, zu denen z(t)-Werte vorhanden sind.
     * @return Number of position vertices
     */
    abstract public int numberOfTZPairs();
    
    /**
     * returns the values of the i-th position point. The first element of the result array holds the position t, the
     * second element the corresponding z-value.<br /><br />
     * <i>German:</> liefert die Werte der i-ten Stationsstelle des Profils. Das erste Element des Ergebnisfeldes
     * enth&auml;lt die Stationierung t, das zweite Element den zugeh&ouml;rigen z-Wert.<p>
     * Die Folge der t-z-Wertepaare w&auml;chst bez&uuml;glich t stets monoton, d. h. f&uuml;r alle i gilt stets
     * <i>getTZPair(i)[i] &lt;= getTZPair(i)[i + 1] </i>.<br />
     * Es ist die Bedingung <i>0 &lt;= i &lt; this.numberOfTZPairs()</i> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.
     * @param i Vertex index
     * @return Array with two element containing the values for t and z(t)
     * @throws T3dException
     */
    abstract public double[] getTZPair(int i) throws T3dException;

    /**
     * returns the value of the position parameter t for the start-point of the base-line.<br /><br />
     * <i>German:</> liefert den Wert des Stationierungsparameters t f&uuml;r den Startpunkt der Definitionslinie des
     * Profils.
     * @return t-value, here = 0
     */
    public double tStart() {
        return 0.;
    }

    /**
     * returns the value of the position parameter t for the end-point of the base-line.<br /><br />
     * <i>German:</> liefert den Wert des Stationierungsparameters t f&uuml;r den Endpunkt der Definitionslinie des
     * Profils. Zur&uuml;ckgegeben wird also die L&auml;nge der Definitionslinie.
     * @return Base-line length
     */
    public double tEnd() {
    	return mGeom.length();
    }

    /**
     * returns the value of the position parameter t for the begin of the base-line section that is providing
     * z-values.<br /><br />
     * <i>German:</> liefert den Wert des Stationierungsparameters t f&uuml;r den Anfang des Belegungsbereichs des
     * Profils.
     * @return t-value &gt;= 0
     */
    abstract public double tMin();

    /**
     * returns the value of the position parameter t for the end of the base-line section that is providing
     * z-values.<br /><br />
     * <i>German:</> liefert den Wert des Stationierungsparameters t f&uuml;r das Ende des Belegungsbereichs des
     * Profils.
     * @return t-value &lt;= <tt>this.tEnd()</tt>
     */
    abstract public double tMax();

    /**
     * returns the cross-section's minimal z-value.<br /><br />
     * <i>German:</> liefert den minimalen z-Wert des Profils.
     * @return Minimum of all z(t)
     */
    abstract public double zMin();

    /**
     * returns the cross-section's maximal z-value.<br /><br />
     * <i>German:</> liefert den maximalen z-Wert des Profils.
     * @return Maximum of all z(t)
     */
    abstract public double zMax();
    
    /**
     * always returns <i>false</i>, since a cross-section describes no collection of features.<br /><br />
     * <i>German:</> Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das Profil ein atomares Geoobjekt ist, liefert
     * diese Methode stets <i>false</i> als Ergebnis.
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /**
     * return the cross-section object itself.<br /><br />
     * <i>German:</> Methode aus der <tt>VgFeature</tt>-Schnittstelle.
     * @param i (here always 0)
     * @return Cross-section object itself
     * @throws T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
        if (i != 0) 
            throw new T3dException("Index out of bounds." ); 
        // else:
        return this;
    }
    
    /**
     * always returns 1 as resuilt, since a cross-section describes no collection of features.<br /><br />
     * <i>German:</> Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das Profil ein atomares Geoobjekt ist, liefert
     * diese Methode stets 1 als Ergebnis.
     * @return 1
     */
    public int numberOfSubFeatures() {
        return 1;
    }

    abstract public String toString();
}
