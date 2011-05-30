package org.n52.v3d.triturus.vgis;

/**
 * Abstrakte Basisklasse f�r 1-dimensionale geometrische Objekte. Die Klasse erweitert die Klasse <tt>VgGeomObject</tt>
 * um Definitionen f�r den Zugriff auf <i>metrische</i> Eigenschaften der Geometrien.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgGeomObject1d extends VgGeomObject 
{
	/**
	 * liefert die L�nge des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double length();

	/**
	 * liefert die L�nge der auf die xy-Ebene projizierten Geometrie bezogen auf das zugrunde liegende r�umliche
     * Referenzsystem ("footprint length").<p>
	 * @see VgGeomObject#getSRS
	 */
	public double lengthXY() {
		return ((VgGeomObject1d) this.footprint()).length();
	}
}
