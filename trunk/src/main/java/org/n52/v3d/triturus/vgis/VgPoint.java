package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung punkthafter Geometrien.<p>
 * x- und y-Werte sind bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben. Bei dem z-Wert kann es sich
 * z. B. um einen Höhenwert handeln.<p>
 * Bem.: Innerhalb des Rahmenwerks werden <tt>VgPoint</tt>-Objekte zumeist für die Verarbeitung von <i>Ortsvektoren</i>
 * verwendet. Die Verwendung für <tt>Richtungsvektoren</tt> ist prinzipiell möglich; zumeist ist allerdings die
 * Hilfsklasse <tt>T3dVector</tt> vorzuziehen.<p>
 * @see org.n52.v3d.triturus.t3dutil.T3dVector
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgPoint extends VgGeomObject0d implements Comparable 
{
	/** 
	 * setzt den x-Wert der Punktgeometrie.<p> 
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) ist als x-Wert die geografische Länge anzugeben,
     * für <i>Gauß-Krüger-Koordinaten</i> der Rechtswert.<p>
	 */
	abstract public void setX(double pX);

	/** 
	 * liefert den x-Wert der Punktgeometrie.<p>
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) bezeichnet der x-Wert die geografische Länge,
     * für <i>Gauß-Krüger-Koordinaten</i> den Rechtswert.<p>
     */
	abstract public double getX();

	/** 
	 * setzt den y-Wert der Geometrie.<p> 
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) ist als y-Wert die geografische Breite anzugeben,
     * für <i>Gauß-Krüger-Koordinaten</i> der Hochwert.<p>
	 */
	abstract public void setY(double pY);

	/** 
	 * liefert den y-Wert der Geometrie.<p>
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) bezeichnet der y-Wert die geografische Breite,  
     * für <i>Gauß-Krüger-Koordinaten</i> den Hochwert.<p>
  	 */
	abstract public double getY();

	/** setzt den z-Wert der Geometrie. */
	abstract public void setZ(double pZ);

	/** liefert den z-Wert der Geometrie. */
	abstract public double getZ();

	/** 
	 * kopiert die Koordinaten eines <tt>VgPoint</tt>-Objekts. Die Information über das räumliche Referenzsystem (SRS)
     * wird dabei übernommen.<p>
	 */
	public void set(VgPoint pt) 
	{
		this.setX(pt.getX());
		this.setY(pt.getY());
		this.setZ(pt.getZ());
		this.setSRS(pt.getSRS());
	}

    /**
	 * liefert den Abstand des Punkt von der Punktgeometrie <tt>pt</tt>.<p> 
	 * Der Abstand wird bezogen auf das der Geometrie zugrunde liegenden räumlichen Referenzsystem
     * (<tt>this.getSRS()</tt>) und die den z-Koordinaten zugrunde liegende Einheit berechnet.<p>
	 * Beispiel: Für siebenstellige Gauß-Krüger-Koordinaten in m und Höhenangaben in m ü. NN. ergibt ist das
     * Abstandsmaß 1 m. Seitens der aufrufenden Anwendung ist sicherzustellen, dass in der xy-Ebene und in z-Richtung
     * vernünftige Maße verwendet werden.<p>
	 * Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt> übereinstimmt, wird
     * eine <tt>T3dException</tt> geworfen.<p>
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
		    
		VgPoint p = null;
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
	 * liefert den Abstand der punkthaften Geometrie von der Punktgeometrie <tt>pt</tt> in der xy-Ebene (d. h., die
     * z-Werte werden = 0 gesetzt).<p>
	 * Der Abstand wird in dem der Geometrie zugrunde liegenden räumlichen Referenzsystem (<tt>this.getSRS()</tt>)
     * berechnet. Falls das Referenzsystem der Geometrie <tt>pt</tt> nicht mit dem der Geometrie <tt>this</tt>
     * übereinstimmt, wird eine <tt>T3dException</tt> geworfen.<p>
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
		    
		VgPoint p = null;
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
	 * definiert eine Ordnung (&lt;) auf der Klasse <tt>VgPoint</tt>. Die Methode liefert den Wert -1, falls
     * <tt>this &lt; pt</tt> bzw. +1, falls <tt>this &gt; pt</tt>. Stimmen <tt>this</tt> und <tt>pt</tt> in ihren
     * Koordinaten genau überein, ist der Rückgabewert 0.<p>
	 * Hinweis: Die räumlichen Bezugssysteme der Punkte <tt>this</tt> und <tt>pt</tt> werden durch die Methode nicht
     * auf Kompatibilität geprüft.<p>
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
	 * -> überschreibt equals() von Object, wird benötigt in GmLineSegment in dessen equals() 
	 * TODO vorlaüfige lösung (Martin)
	 */
	public boolean equals(Object pt) 
	{
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
