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
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * todo engl. JavaDoc
 * Abstrakte Basisklasse f&uuml;r allgemeine Szenenbeschreibungen f&uuml;r 3D-Geovisualisierungen. Die Klasse dient
 * innerhalb des Rahmenwerks zur Abstraktion von der konkret eingesetzten Rendering-/Visualisierungsumgebung.
 * <p>
 * <i>TODO: Konkrete Implementierungen k&ouml;nnten z. B. generische Szenengraphen, konkrete Szenengraphen f&uuml;r
 * Java 3D, Xith3D, VRML, GeoVRML, X3D, AVS-Netzwerke, POV-Ray-Szenenbeschreibungen oder sonstwas realisieren...</i>
 * @author Benno Schmidt
 */
abstract public class VsScene
{
	private double mExaggeration = 1.;
	
	private ArrayList mCameras = null;
	private int mCurrentCamera = -1;
	
	private ArrayList mLights = null;
	
	/**
	 * setzt die Voreinstellung f&uuml;r die &Uuml;berh&ouml;hung (vertikaler H&ouml;henma&szlig;stab) der Szene.
     * Voreingestellt ist der Wert 1.0.
	 * @param pExaggeration z-Faktor
	 */	
	public void setDefaultExaggeration(double pExaggeration) {
		mExaggeration = pExaggeration;
	}

	/**
	 * liefert den f&uuml;r die &Uuml;berh&ouml;hung (vertikaler H&ouml;henma&szlig;stab) voreingestellten Wert.
	 * @return z-Faktor
	 */	
	public double getDefaultExaggeration() {
		return mExaggeration;
	}

	/**
	 * f�gt der Szene einen Kamera hinzu.<p>
	 * Einer <tt>VsScene</tt> k&ouml;nnen mehrere Kameras hinzugef&uuml;gt werden. Voreinstellungsgem&auml;&zzlig; wird
	 * die erste der Szene hinzugef&uuml;gte Kamera beim Start der Visualisierung gesetzt.
	 * @param pCamera Kamera-Definition
	 * @see VsScene#setCurrentCamera
	 */
	public void addCamera(VsCamera pCamera) 
	{
		if (mCameras == null) {
		    mCameras = new ArrayList();
		    mCurrentCamera = 0;
		}
		mCameras.add(pCamera);
	}

	/**
	 * liefert die i-te f&uuml;r die Szene definierten Kamera.<p>
	 * F&uuml;r i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> einzuhalten.
	 * @param i
	 * @return Kamera-Definition
	 * @throws T3dException
	 */
	public VsCamera getCamera(int i) throws T3dException
	{
		if (mCameras == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfCameras())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfCameras() + "].");
			return (VsCamera) mCameras.get(i);
		}
	}

	/**
	 * liefert die Anzahl der f&uuml;r die Kamera definierten Kameras.
	 * @return Anzahl &gt;= 0
	 */
	public int numberOfCameras() {
		if (mCameras == null)
			return 0;
		else
			return mCameras.size();
	}
	
	/**
	 * setzt die aktuelle Kamera.<p>
	 * F&uuml;r i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> einzuhalten.
	 * @param i Index der aktuell gesetzten Kamera
	 * @throws T3dException
	 */
	public void setCurrentCamera(int i) throws T3dException
	{
		if (i < 0 || i >= this.numberOfCameras())
			throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfCameras() + "].");
		mCurrentCamera = i;	
	}

	/**
	 * liefert die aktuell gesetzte Kamera.
	 * @return Kamera
	 * @throws T3dException
	 */
	public VsCamera getCurrentCamera() throws T3dException
	{
		if (mCameras == null)
			return null;
		else
			return (VsCamera) mCameras.get(mCurrentCamera);
	}

	/**
	 * liefert den aktuell gesetzten Ansichtspunkt f&uuml;r die Szene. Dieser Ansichtspunkt entspricht dem
	 * aktuellen Ansichtspunkt der aktuell gesetzten Kamera.
	 * @return Ansichtspunkt 
	 * @throws T3dException
	 */
	public VsViewpoint getCurrentViewpoint() throws T3dException
	{
		if (mCameras == null)
			return null;
		else
			return this.getCurrentCamera().getCurrentViewpoint();
	}

    /**
     * entfernt alle Kameras aus der Szene.
     */
    public void removeCameras()
    {
        if (mCameras != null)
            mCameras.clear();
    }

	/**
	 * f&uuml;gt der Szene eine Lichtquelle hinzu.<p>
	 * Einer <tt>VsScene</tt> k&ouml;nnen mehrere Lichtquellen hinzugef&uuml;gt werden.
	 * @param pLight Lichtquellen-Definition
	 */
	public void addLightSource(VsLightSource pLight) 
	{
		if (mLights == null)
		    mLights = new ArrayList();
		mLights.add(pLight);
	}

	/**
	 * liefert die i-ten f&uuml;r die Szene definierte Lichtquelle.<p>
	 * F&uuml;r i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfLightSources()</tt> einzuhalten.
	 * @param i
	 * @return Lichtquellen-Definition 
	 * @throws T3dException
	 */
	public VsLightSource getLightSource(int i) throws T3dException
	{
		if (mLights == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfLightSources())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfLightSources() + "].");
			return (VsLightSource) mLights.get(i);
		}
	}

	/**
	 * liefert die Anzahl der f&uuml;r die Szene definierten Lichtquellen.
	 * @return Anzahl &gt;= 0
	 */
	public int numberOfLightSources() {
		if (mLights == null)
			return 0;
		else
			return mLights.size();
	}

    /**
     * entfernt alle Lichtquellen aus der Szene.
     */
    public void removeLightSources()
    {
        if (mLights != null)
            mLights.clear();
    }

	/**
	 * generiert die zu den gesetzten Werten geh&ouml;rige Szenen-Beschreibung. Bei dem Resultat des Methodenaufrufs
     * kann es sich z. B. um eine (nicht notwendigerweise Szenengraph-basierte) Szenenbeschreibungsdatei oder ein
	 * Szenengraph-Objekt handeln.<p>
	 * Bem.: Diese Methode ist durch die konkreten <tt>VsScene</tt>-Realisierungen zu implementieren.
	 * @return Szenen-Beschreibung
	 */
	abstract public Object generateScene();
}
