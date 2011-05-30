package org.n52.v3d.triturus.vgis;

/**
 * Abstrakte Basisklasse f�r 2-dimensionale geometrische Objekte. Die Klasse erweitert die Klasse <tt>VgGeomObject</tt>
 * um Definitionen f�r den Zugriff auf <i>metrische</i> Eigenschaften der Geometrien.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgGeomObject2d extends VgGeomObject 
{
	/**
	 * liefert den Fl�cheninhalt des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double area();

	/**
	 * liefert den Fl�cheninhalt der auf die xy-Ebene projizierten Geometrie bezogen auf das zugrunde liegende
     * r�umliche Referenzsystem ("footprint area").<p>
	 * @see VgGeomObject#getSRS
	 */
	public double areaXY() {
		return ((VgGeomObject2d) this.footprint()).area();
	}
}
