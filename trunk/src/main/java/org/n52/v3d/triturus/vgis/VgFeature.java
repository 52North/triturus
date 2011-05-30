package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Zentrale Basisklasse zur Verwaltung von Geoobjekten.<p>
 * Der Realisierung der vorliegenden Klassen-Bibliothek lagen folgende Entwurfsziele zugrunde:
 * <ul>
 * <li>Anwendungsbereich sind Anwendungen zur 3D-Geovisualisierung.</li>
 * <li>Die Basisklassen sind abstrakt definiert, so dass unterschiedliche Implementierungen 
 * (z. B. org.n52.v3d.triturus.gisimplm) verwendet werden können.</li>
 * <li>Die Bibliothek sollte schnell zu realisieren sein.</li>
 * </ul><p>
 * @see org.n52.v3d.triturus.vgis.VgAttrFeature
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgFeature 
{
	private String mName = "";

	/**
	 * setzt den Namen des Geoobjektes.<p>
	 * Der Objektname muss nicht notwendigerweise eindeutig sein.<p>
	 */
	public void setName(String pName) {
		mName = pName;
	}

	/** liefert den Objektnamen des Geoobjekts. */
	public String getName() {
		return mName;
	}

	/** liefert die Geometrie eines (atomaren) <tt>VgFeature</tt>-Objekts. */
	abstract public VgGeomObject getGeometry();

	/**
	 * liefert das i-te Sub-Objekt des Geoobjekts.<p>
	 * Hierbei ist die Bedingung 0 &lt;= i &lt; <tt>this.numberOfSubFeatures()</tt> einzuhalten;anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 * @throws T3dException
	 */
	abstract public VgFeature getFeature(int i) throws T3dException;

	/**
	 * liefert Information, ob das Geoobjekt aus mehreren Geoobjekten zusammengesetzt ist ("feature collection").
	 */
	abstract public boolean isCollection();

	/**
	 * liefert Information, ob das Geoobjekt aus mehreren Geoobjekten zusammengesetzt ist ("feature collection"). Es
     * gilt stets: <i>obj.isCollection() = !obj.isAtomic()</i>.<p>
	 * @return <i>true</i>, falls Objekt nicht zusammengesetzt ist.
	 */
	public boolean isAtomic() {
		return! (this.isCollection());
	}

	/**
	 * liefert die Anzahl der Sub-Objekte des Geoobjekts. Ist das Objekt atomar, so ist der Rückgabewert 1.
	 */
	abstract public int numberOfSubFeatures();

	abstract public String toString();
}