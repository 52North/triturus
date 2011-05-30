package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * Allgemeine Basisklasse für Kamera-Definitionen. Die Klasse dient innerhalb des Rahmenwerks dazu, 
 * Kameras unabhängig von der konkret eingesetzten Rendering-/Visualisierungsumgebung spezifizieren
 * zu können.
 * <p>
 * In 3D-Anwendungen wird häufig ein vereinfachtes Modell der "synthetischen Kamera" verwendet. Die vorliegende 
 * Klasse wurde mit Blick auf die Belange der Geovisualisierung unter Wahrung der konzeptuellen Konformität an 
 * dieses Modell angepasst. Die Kamera-Parameter werden sich in den meisten Fällen innerhalb der konkret 
 * genutzten Rendering-Umgebungen (z. B. Java 3D oder VRML) umsetzen lassen. Nicht allgemeingültige Kamera-Parameter, 
 * die z. B. nur von speziellen Renderern unterstützt werden, sollten in Form von Spezialisierungen der Klasse 
 * <tt>VsCamera</tt> Eingang in das vorliegende Rahmenwerk finden.
 * <p>
 * Bem.: Die Positionen sind in Geo-Koordinaten anzugeben. Häufig stellt die Verwendung relativer Koordinaten, 
 * die auf die Bounding-Box des Szeneninhalts (d. h. die räumliche Ausdehnung aller "Shapes" in einer Szene) 
 * bezogen sind, eine wesentliche Arbeitserleichterung dar. Für Geo-Anwendungen ist dies allerdings nicht 
 * unproblematisch, da sich diese Bounding-Box zur Programmlaufzeit dynamisch ändern kann. Um dennoch relative 
 * Koordinaten verwenden zu können, lassen sich z. B. die jeweilige Szenen-Semantik berücksichtigende Methoden 
 * spezieller Szenen-Implementierungen nutzen; siehe z. B. Transformationsmethoden in der Klasse 
 * <tt>VsSimpleScene</tt>.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class VsCamera
{
	private ArrayList mViewpoints = null;
	private int mCurrentViewpoint = -1;

    private String mProjectionType = PerspectiveView;
    /** Konstante für perspektivische Projektion. */
	public static final String PerspectiveView = "PerspectiveView";
    /** Konstante für orthographische Projektion. */
	public static final String OrthographicView = "OrthographicView";

	private double mFovy = 60.;
	
	/**
	 * fügt der Kamera einen Ansichtspunkt hinzu.<p>
	 * Einer <tt>VsCamera</tt> können mehrere Ansichtspunkte hinzugefügt werden. Voreinstellungsgemäß wird
	 * der erste der Kamera hinzugefügte Ansichtspunkt beim Start der Visualisierung gesetzt.<p>
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
	 * liefert den i-ten für die Kamera definierten Ansichtspunkt.<p>
	 * Für i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> einzuhalten.<p>
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
	 * liefert die Anzahl der für die Kamera definierten Ansichtspunkte.<p>
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
	 * Für i ist die Beziehung 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> einzuhalten.<p>
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
     * setzt den für die Kamera eingestellten Projektionstyp. Mögliche Projektionen sind die perspektivische oder
     * orthographische Darstellung. Voreingestellt ist die perspektive Projektion.
     * <p>
     * Bem.: Der Typ <tt>this.PerspectiveView</tt> sollte von <b>allen</b> <tt>VsScene</tt>-Implementierungen
     * unterstützt werden. Falls der Typ <tt>this.PerspectiveView</tt> nicht unterstützt wird, sollte seitens der
     * jeweiligen <tt>VsScene</tt>-Implementierung (z. B. während des Renderns) eine <tt>T3dNotYetImplException</tt>
     * geworfen werden. Weitere Projektionstypen sind denkbar (siehe z. B. in POV-Ray unterstützte Typen.
     * <p>
     * @param pProjectionType Projektionstyp, z. B. <tt>this.PerspectiveView</tt>
     * @see VsScene#generateScene
     * @see org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void setProjection(String pProjectionType) {
        mProjectionType = pProjectionType;
    }

    /**
     * liefert den für die Kamera eingestellten Projektionstyp.<p>
     * @return Projektionstyp, z. B. <tt>this.PerspectiveView</tt>
     * @see VsCamera#setProjection
     */
    public String getProjectionType () {
        return mProjectionType;
    }

	/**
	 * setzt den "Blickwinkel" ("field of view") für die Kamera. Voreingestellt ist ein Wert von 60 Grad.
	 * <p>
	 * Bem.: Die Änderung dieser Einstellung beeinflusst zumeist verschiedene mathematische Eigenschaften 
	 * der Ansicht! So kann z. B. die Neuberechnung des Betrachterabstandes vom Dargestellten notwendig
	 * werden. Falls für die Kamera eine orthographische Ansicht eingestellt ist, wird dieser Wert nicht
     * berücksichtigt.<p>
	 * <p>
	 * @param pAngle Winkelangabe in Altgrad
	 */
	public void setFovy(double pAngle) {
		mFovy = pAngle;
	}
	
	/**
	 * liefert den für die Kamera eingestellten "Blickwinkel" ("field of view").<p>
	 * @return Winkelangabe in Altgrad
	 */
	public double getFovy() {
		return mFovy;
	}
}
