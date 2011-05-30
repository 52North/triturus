package org.n52.v3d.triturus.vgis;

/**
 * Abstrakte Basisklasse für 2-dimensionale geometrische Objekte. Die Klasse erweitert die Klasse <tt>VgGeomObject</tt>
 * um Definitionen für den Zugriff auf <i>metrische</i> Eigenschaften der Geometrien.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgGeomObject2d extends VgGeomObject 
{
	/**
	 * liefert den Flächeninhalt des Geometrie-Objekts bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double area();

	/**
	 * liefert den Flächeninhalt der auf die xy-Ebene projizierten Geometrie bezogen auf das zugrunde liegende
     * räumliche Referenzsystem ("footprint area").<p>
	 * @see VgGeomObject#getSRS
	 */
	public double areaXY() {
		return ((VgGeomObject2d) this.footprint()).area();
	}
}
