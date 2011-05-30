package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstrakte Basisklasse f�r 0-dimensionale geometrische Objekte. Die Klasse erweitert die Klasse <tt>VgGeomObject</tt>
 * um Definitionen f�r den Zugriff auf <i>metrische</i> Eigenschaften der Geometrien.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgGeomObject0d extends VgGeomObject 
{
	/**
	 * liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt>.<p> 
	 * Der Abstand wird bezogen auf das der Geometrie zugrunde liegenden r�umlichen Referenzsystem
     * (<tt>this.getSRS()</tt>) und die den z-Koordinaten zugrunde liegende Einheit berechnet.<p>
	 * Beispiel: F�r siebenstellige Gau�-Kr�ger-Koordinaten in m und H�henangaben in m �. NN. ergibt sich das
     * Abstandsma� 1 m. Seitens der aufrufenden Anwendung ist sicherzustellen, dass in der xy-Ebene und in z-Richtung
     * vern�nftige Ma�e verwendet werden.<p>
	 * Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt> �bereinstimmt, wird
     * eine <tt>T3dException</tt> geworfen.<p>
	 * @see VgGeomObject#getSRS
	 */
	abstract public double distance(VgGeomObject0d pt) throws T3dException;
	
	/**
	 * liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt> in der xy-Ebene (d. h., die
     * z-Werte werden = 0 gesetzt).<p>
	 * Der Abstand wird in dem der Geometrie zugrunde liegenden r�umlichen Referenzsystem (<tt>this.getSRS()</tt>)
     * berechnet. Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt>
     * �bereinstimmt, wird eine <tt>T3dException</tt> geworfen.<p>
	 * @see VgGeomObject#getSRS
	 * @see VgGeomObject0d#distance
	 */
	abstract public double distanceXY(VgGeomObject0d pt) throws T3dException;
}
