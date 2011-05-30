package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * Abstrakte Basisklasse für allgemeine Szenenbeschreibungen für 3D-Geovisualisierungen. Die Klasse dient 
 * innerhalb des Rahmenwerks zur Abstraktion von der konkret eingesetzten Rendering-/Visualisierungsumgebung.
 * <p>
 * <i>TODO: Konkrete Implementierungen könnten z. B. generische Szenengraphen, konkrete Szenengraphen für
 * Java 3D, Xith3D, VRML, GeoVRML, X3D, AVS-Netzwerke, POV-Ray-Szenenbeschreibungen oder sonstwas realisieren...</i>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VsScene
{
	private double mExaggeration = 1.;
	
	private ArrayList mCameras = null;
	private int mCurrentCamera = -1;
	
	private ArrayList mLights = null;
	
	/**
	 * setzt die Voreinstellung für die Überhöhung (vertikaler Höhenmaßstab) der Szene. Voreingestellt ist der
	 * Wert 1.0.<p>
	 * @param pExaggeration z-Faktor
	 */	
	public void setDefaultExaggeration(double pExaggeration) {
		mExaggeration = pExaggeration;
	}

	/**
	 * liefert den für die Überhöhung (vertikaler Höhenmaßstab) voreingestellten Wert.<p>
	 * @return z-Faktor
	 */	
	public double getDefaultExaggeration() {
		return mExaggeration;
	}

	/**
	 * fügt der Szene einen Kamera hinzu.<p>
	 * Einer <tt>VsScene</tt> können mehrere Kameras hinzugefügt werden. Voreinstellungsgemäß wird
	 * die erste der Szene hinzugefügte Kamera beim Start der Visualisierung gesetzt.<p>
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
	 * liefert die i-te für die Szene definierten Kamera.<p>
	 * Für i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> einzuhalten.<p>
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
	 * liefert die Anzahl der für die Kamera definierten Kameras.<p>
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
	 * Für i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> einzuhalten.<p>
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
	 * liefert die aktuell gesetzte Kamera.<p>
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
	 * liefert den aktuell gesetzten Ansichtspunkt für die Szene. Dieser Ansichtspunkt entspricht dem
	 * aktuellen Ansichtspunkt der aktuell gesetzten Kamera.<p>
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
     * entfernt alle Kameras aus der Szene.<p>
     */
    public void removeCameras()
    {
        if (mCameras != null)
            mCameras.clear();
    }

	/**
	 * fügt der Szene eine Lichtquelle hinzu.<p>
	 * Einer <tt>VsScene</tt> können mehrere Lichtquellen hinzugefügt werden.<p>
	 * @param pLight Lichtquellen-Definition
	 */
	public void addLightSource(VsLightSource pLight) 
	{
		if (mLights == null)
		    mLights = new ArrayList();
		mLights.add(pLight);
	}

	/**
	 * liefert die i-ten für die Szene definierte Lichtquelle.<p>
	 * Für i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfLightSources()</tt> einzuhalten.<p>
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
	 * liefert die Anzahl der für die Szene definierten Lichtquellen.<p>
	 * @return Anzahl &gt;= 0
	 */
	public int numberOfLightSources() {
		if (mLights == null)
			return 0;
		else
			return mLights.size();
	}

    /**
     * entfernt alle Lichtquellen aus der Szene.<p>
     */
    public void removeLightSources()
    {
        if (mLights != null)
            mLights.clear();
    }

	/**
	 * generiert die zu den gesetzten Werten gehörige Szenen-Beschreibung. Bei dem Resultat des Methodenaufrufs kann
	 * es sich z. B. um eine (nicht notwendigerweise Szenengraph-basierte) Szenenbeschreibungsdatei oder ein
	 * Szenengraph-Objekt handeln.<p>
	 * Bem.: Diese Methode ist durch die konkreten <tt>VsScene</tt>-Realisierungen zu implementieren.<p>
	 * @return Szenen-Beschreibung
	 */
	abstract public Object generateScene();
}
