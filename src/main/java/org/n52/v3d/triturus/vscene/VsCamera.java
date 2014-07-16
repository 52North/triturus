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
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * todo engl. JavaDoc
 * Allgemeine Basisklasse f�r Kamera-Definitionen. Die Klasse dient innerhalb des Rahmenwerks dazu,
 * Kameras unabh�ngig von der konkret eingesetzten Rendering-/Visualisierungsumgebung spezifizieren
 * zu k�nnen.
 * <p>
 * In 3D-Anwendungen wird h�ufig ein vereinfachtes Modell der "synthetischen Kamera" verwendet. Die vorliegende 
 * Klasse wurde mit Blick auf die Belange der Geovisualisierung unter Wahrung der konzeptuellen Konformit�t an 
 * dieses Modell angepasst. Die Kamera-Parameter werden sich in den meisten F�llen innerhalb der konkret 
 * genutzten Rendering-Umgebungen (z. B. Java 3D oder VRML) umsetzen lassen. Nicht allgemeing�ltige Kamera-Parameter, 
 * die z. B. nur von speziellen Renderern unterst�tzt werden, sollten in Form von Spezialisierungen der Klasse 
 * <tt>VsCamera</tt> Eingang in das vorliegende Rahmenwerk finden.
 * <p>
 * Bem.: Die Positionen sind in Geo-Koordinaten anzugeben. H�ufig stellt die Verwendung relativer Koordinaten, 
 * die auf die Bounding-Box des Szeneninhalts (d. h. die r�umliche Ausdehnung aller "Shapes" in einer Szene) 
 * bezogen sind, eine wesentliche Arbeitserleichterung dar. F�r Geo-Anwendungen ist dies allerdings nicht 
 * unproblematisch, da sich diese Bounding-Box zur Programmlaufzeit dynamisch �ndern kann. Um dennoch relative 
 * Koordinaten verwenden zu k�nnen, lassen sich z. B. die jeweilige Szenen-Semantik ber�cksichtigende Methoden 
 * spezieller Szenen-Implementierungen nutzen; siehe z. B. Transformationsmethoden in der Klasse 
 * <tt>VsSimpleScene</tt>.
 * <p>
 * @author Benno Schmidt
 */
public class VsCamera
{
	private ArrayList mViewpoints = null;
	private int mCurrentViewpoint = -1;

    private String mProjectionType = PerspectiveView;
    /** Konstante f�r perspektivische Projektion. */
	public static final String PerspectiveView = "PerspectiveView";
    /** Konstante f�r orthographische Projektion. */
	public static final String OrthographicView = "OrthographicView";

	private double mFovy = 60.;
	
	/**
	 * f�gt der Kamera einen Ansichtspunkt hinzu.<p>
	 * Einer <tt>VsCamera</tt> k�nnen mehrere Ansichtspunkte hinzugef�gt werden. Voreinstellungsgem�� wird
	 * der erste der Kamera hinzugef�gte Ansichtspunkt beim Start der Visualisierung gesetzt.<p>
	 * @param pViewpoint Ansichtspunkt-Definition
	 * @see VsCamera#setCurrentViewpoint
	 */
	public void addViewpoint(VsViewpoint pViewpoint) 
	{
		if (mViewpoints == null) {
		    mViewpoints = new ArrayList();
		    mCurrentViewpoint = 0;
		}
		mViewpoints.add(pViewpoint);
	}

	/**
	 * liefert den i-ten f�r die Kamera definierten Ansichtspunkt.<p>
	 * F�r i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> einzuhalten.<p>
	 * @param i
	 * @return Ansichtspunkt-Definition
	 * @throws T3dException
	 */
	public VsViewpoint getViewpoint(int i) throws T3dException
	{
		if (mViewpoints == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfViewpoints())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfViewpoints() + "].");
			return (VsViewpoint) mViewpoints.get(i);
		}
	}

	/**
	 * liefert die Anzahl der f�r die Kamera definierten Ansichtspunkte.<p>
	 * @return Anzahl &gt;= 0
	 */
	public int numberOfViewpoints() {
		if (mViewpoints == null)
			return 0;
		else
			return mViewpoints.size();
	}
	
	/**
	 * setzt den aktuellen Ansichtspunkt.<p>
	 * F�r i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> einzuhalten.<p>
	 * @param i Index des aktuell gesetzten Ansichtspunktes
	 * @throws T3dException
	 */
	public void setCurrentViewpoint(int i) throws T3dException
	{
		if (i < 0 || i >= this.numberOfViewpoints())
			throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfViewpoints() + "].");
		mCurrentViewpoint = i;	
	}

	/**
	 * liefert den aktuell gesetzten Ansichtspunkt.<p>
	 * @return Ansichtspunkt 
	 * @throws T3dException
	 */
	public VsViewpoint getCurrentViewpoint() throws T3dException
	{
		if (mViewpoints == null)
			return null;
		else
			return (VsViewpoint) mViewpoints.get(mCurrentViewpoint);
	}

    /**
     * setzt den f�r die Kamera eingestellten Projektionstyp. M�gliche Projektionen sind die perspektivische oder
     * orthographische Darstellung. Voreingestellt ist die perspektive Projektion.
     * <p>
     * Bem.: Der Typ <tt>this.PerspectiveView</tt> sollte von <b>allen</b> <tt>VsScene</tt>-Implementierungen
     * unterst�tzt werden. Falls der Typ <tt>this.PerspectiveView</tt> nicht unterst�tzt wird, sollte seitens der
     * jeweiligen <tt>VsScene</tt>-Implementierung (z. B. w�hrend des Renderns) eine <tt>T3dNotYetImplException</tt>
     * geworfen werden. Weitere Projektionstypen sind denkbar (siehe z. B. in POV-Ray unterst�tzte Typen.
     * <p>
     * @param pProjectionType Projektionstyp, z. B. <tt>this.PerspectiveView</tt>
     * @see VsScene#generateScene
     * @see org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void setProjection(String pProjectionType) {
        mProjectionType = pProjectionType;
    }

    /**
     * liefert den f�r die Kamera eingestellten Projektionstyp.<p>
     * @return Projektionstyp, z. B. <tt>this.PerspectiveView</tt>
     * @see VsCamera#setProjection
     */
    public String getProjectionType () {
        return mProjectionType;
    }

	/**
	 * setzt den "Blickwinkel" ("field of view") f�r die Kamera. Voreingestellt ist ein Wert von 60 Grad.
	 * <p>
	 * Bem.: Die �nderung dieser Einstellung beeinflusst zumeist verschiedene mathematische Eigenschaften 
	 * der Ansicht! So kann z. B. die Neuberechnung des Betrachterabstandes vom Dargestellten notwendig
	 * werden. Falls f�r die Kamera eine orthographische Ansicht eingestellt ist, wird dieser Wert nicht
     * ber�cksichtigt.<p>
	 * <p>
	 * @param pAngle Winkelangabe in Altgrad
	 */
	public void setFovy(double pAngle) {
		mFovy = pAngle;
	}
	
	/**
	 * liefert den f�r die Kamera eingestellten "Blickwinkel" ("field of view").<p>
	 * @return Winkelangabe in Altgrad
	 */
	public double getFovy() {
		return mFovy;
	}
}
