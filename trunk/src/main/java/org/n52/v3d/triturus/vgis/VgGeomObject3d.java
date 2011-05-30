package org.n52.v3d.triturus.vgis;

/**
 * Abstrakte Basisklasse f�r 3-dimensionale geometrische Objekte. Die Klasse erweitert die Klasse <tt>VgGeomObject</tt>
 * um Definitionen f�r den Zugriff auf <i>metrische</i> Eigenschaften der Geometrien.<p>
 * @author Benno Schmidt<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 */
abstract public class VgGeomObject3d extends VgGeomObject 
{
	/**
	 * liefert das Volumen des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem und die in
     * z-Richtung verwendete Einheit.<p>
	 * Bem.: Ggf. ist sicherzustellen, dass dem Fl�chenma� eine vern�nftige Einheit zugrunde liegt.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double volume();

	/**
	 * liefert die Oberfl�che des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem und die
     * in z-Richtung verwendete Einheit.<p>
	 * Bem.: Ggf. ist sicherzustellen, dass dem Fl�chenma� eine vern�nftige Einheit zugrunde liegt.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double surface();

	/**
	 * liefert die Oberfl�che des auf die xy-Ebene projizierten Geometrie-Objekts bezogen auf das zugrunde liegende
     * r�umliche Referenzsystem ("footprint area").<p>
	 * @see VgGeomObject#getSRS
	 */
	public double areaXY() {
		return ((VgGeomObject2d) this.footprint()).area();
	}
}
