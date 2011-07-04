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
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold 3D point coordinates. E.g., the object's z-value might be elevation value.<br /><br />
 * <i>German: </i>Klasse zur Verwaltung punkthafter Geometrien.<p>
 * x- und y-Werte sind bezogen auf das eingestellte r&auml;umliche Bezugssystem (SRS) anzugeben. Bei dem z-Wert kann es
 * sich z. B. um einen H&ouml;henwert handeln.<br />
 * Bem.: Innerhalb des Rahmenwerks werden <tt>VgPoint</tt>-Objekte zumeist f&uuml;r die Verarbeitung von
 * <i>Ortsvektoren</i> verwendet. Die Verwendung f&uuml;r <tt>Richtungsvektoren</tt> ist prinzipiell m&ouml;glich;
 * zumeist ist allerdings die Hilfsklasse <tt>T3dVector</tt> vorzuziehen.
 * @see org.n52.v3d.triturus.t3dutil.T3dVector
 * @author Benno Schmidt
 */
abstract public class VgPoint extends VgGeomObject0d implements Comparable 
{
	/** 
	 * sets the point's x-value.<br /><br />
	 * <i>German: </i> Bem.: F&uml;r <i>geografische Koordinaten</i> (<tt>&quot;EPSG:4326&quot;</tt>) ist als x-Wert die
     * geografische L&auml;nge anzugeben, f&uuml;r <i>Gau&szlig;-Kr&uuml;ger-Koordinaten</i> der Rechtswert.
     * @param pX x-coordinate
	 */
	abstract public void setX(double pX);

	/** 
     * returns the point's x-value.<br /><br />
     * <i>German: </i> Bem.: F&uml;r <i>geografische Koordinaten</i> (<tt>&quot;EPSG:4326&quot;</tt>) wird die
     * geografische L&auml;nge zur&uuml;ckgegeben, f&uuml;r <i>Gau&szlig;-Kr&uuml;ger-Koordinaten</i> der Rechtswert.
     * @return x-coordinate
     */
	abstract public double getX();

	/** 
     * sets the point's y-value.<br /><br />
     * <i>German: </i> Bem.: F&uml;r <i>geografische Koordinaten</i> (<tt>&quot;EPSG:4326&quot;</tt>) ist als x-Wert die
     * geografische Breite anzugeben, f&uuml;r <i>Gau&szlig;-Kr&uuml;ger-Koordinaten</i> der Hochwert.   #
     * @param pY y-coordinate
	 */
	abstract public void setY(double pY);

	/** 
     * return the point's y-value.<br /><br />
     * <i>German: </i> Bem.: F&uml;r <i>geografische Koordinaten</i> (<tt>&quot;EPSG:4326&quot;</tt>) wird die
     * geografische breite zur&uuml;ckgegeben, f&uuml;r <i>Gau&szlig;-Kr&uuml;ger-Koordinaten</i> der Hochwert.
     * @return y-coordinate
  	 */
	abstract public double getY();

	/**
     * sets the point's z-value.
     * @param pZ z-coordinate
     */
	abstract public void setZ(double pZ);

    /**
     * returns the point's z-value.
     * @return z-coordinate
     */
	abstract public double getZ();

	/** 
	 * copies the coordinates of a <tt>VgPoint</tt>-object. The information about the coordinate reference system
     * will be taken over.
     * @param pt Point geometry
	 */
	public void set(VgPoint pt) 
	{
		this.setX(pt.getX());
		this.setY(pt.getY());
		this.setZ(pt.getZ());
		this.setSRS(pt.getSRS());
	}

	/**
     * returns the distance between two points.<br /><br />
	 * <i>German:</i> liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt>.<br />
	 * Der Abstand wird bezogen auf das der Geometrie zugrunde liegenden r&auml;umlichen Referenzsystem
     * (<tt>this.getSRS()</tt>) und die den z-Koordinaten zugrunde liegende Einheit berechnet.<br />
	 * Beispiel: F&uml;r siebenstellige Gau&szlig;-Kr&uuml;ger-Koordinaten in m und H&ouml;henangaben in m &uuml;. NN.
     * ergibt sich das Abstandsma&szlig; 1 m. Seitens der aufrufenden Anwendung ist sicherzustellen, dass in der
     * xy-Ebene und in z-Richtung vern&uuml;nftige Ma&szlig;e verwendet werden.<br />
	 * Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt> &uuml;bereinstimmt,
     * wird eine <tt>T3dException</tt> geworfen.
	 * @see VgGeomObject#getSRS
     * @throws T3dException
     * @throws ClassCastException
	 */
	public double distance(VgGeomObject0d pt) throws T3dException, ClassCastException
	{
		if (!(this.getSRS().equalsIgnoreCase(pt.getSRS()))) {
			String msg = "SRS-incompatibility prevents from distance calculation";
			msg += " (" + this.getSRS() + ", " + pt.getSRS() + ").";
		    throw new T3dException(msg);
		}
		    
		VgPoint p;
		try {
			p = (VgPoint) pt;
		}
		catch (ClassCastException e) {
			throw e;
		}
		
		double dx = this.getX() - p.getX();
		double dy = this.getY() - p.getY();
		double dz = this.getZ() - p.getZ();

		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	/**
     * returns the distance between two points with respect to the x-y plane (i.e., z-values will be set to 0.
     * <br /><br />
	 * <i>German:</i> liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt> in der xy-Ebene
     * (d. h., die z-Werte werden = 0 gesetzt).<br />
	 * Der Abstand wird in dem der Geometrie zugrunde liegenden r&auml;umlichen Referenzsystem (<tt>this.getSRS()</tt>)
     * berechnet. Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt>
     * &uml;bereinstimmt, wird eine <tt>T3dException</tt> geworfen.
	 * @see VgGeomObject#getSRS
	 * @see VgGeomObject0d#distance
     * @throws T3dSRSException
     * @throws ClassCastException
	 */
    public double distanceXY(VgGeomObject0d pt) throws T3dSRSException, ClassCastException
	{
		if (!this.getSRS().equalsIgnoreCase(pt.getSRS())) {
			String msg = "SRS-incompatibility prevents from distance calculation";
			msg += " (" + this.getSRS() + ", " + pt.getSRS() + ").";
		    throw new T3dSRSException(msg);
		}
		    
		VgPoint p;
		try {
			p = (VgPoint) pt;
		}
		catch (ClassCastException e) {
			throw e;
		}
		
		double dx = this.getX() - p.getX();
		double dy = this.getY() - p.getY();

		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
     * defines an order relation on <tt>VgPoint</tt>.<br /><br />
	 * <i>German: </i>definiert eine Ordnung (&lt;) auf der Klasse <tt>VgPoint</tt>. Die Methode liefert den Wert -1,
     * falls <tt>this &lt; pt</tt> bzw. +1, falls <tt>this &gt; pt</tt>. Stimmen <tt>this</tt> und <tt>pt</tt> in ihren
     * Koordinaten genau &uuml;berein, ist der R&uuml;ckgabewert 0.<br />
	 * Hinweis: Die r&auml;umlichen Bezugssysteme der Punkte <tt>this</tt> und <tt>pt</tt> werden durch die Methode
     * nicht auf Kompatibilit&auml;t gepr&uuml;ft.
	 * @return -1, 1 oder 0
	 */
	public int compareTo(Object pt) 
	{
		VgPoint p = (VgPoint) pt;
	    
	    if (this.getX() < p.getX()) return -1;
		if (this.getX() > p.getX()) return 1;

		// Falls gleiche x-Werte:
    	if (this.getY() < p.getY()) return -1;
		if (this.getY() > p.getY()) return 1;
		
		// Falls gleiche x- und y-Werte:
		if (this.getZ() < p.getZ()) return -1;
		if (this.getZ() > p.getZ()) return 1;

		// Falls gleiche x-, y- und z-Werte:
		return 0;
	}
	
	/**
	 * @deprecated
     * @author Martin May
	 */
	public boolean equals(Object pt) 
	{
        // -> �berschreibt equals() von Object, wird ben�tigt in GmLineSegment in dessen equals()
        // TODO vorla�fige l�sung (Martin)
		VgPoint p = (VgPoint) pt;

		boolean res = false;
		if (this.getX() == p.getX() && this.getY() == p.getY() && this.getZ() == p.getZ()) 
		    res = true;
		return res;
	}

	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + " (" + this.getSRS() + "))";
	}
}
