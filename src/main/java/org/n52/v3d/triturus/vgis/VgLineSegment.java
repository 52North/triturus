package org.n52.v3d.triturus.vgis;

/**
 * Klasse zur Verwaltung von Liniensegmenten.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgLineSegment extends VgGeomObject1d 
{
	/** setzt den Startpunkt des Liniensegments. */
	abstract public void setStartPoint(VgPoint pPStart) ;

	/** liefert den Startpunkt des Liniensegments. */
	abstract public VgPoint getStartPoint();

	/** setzt den Endpunkt des Liniensegments. */
	abstract public void setEndPoint(VgPoint pPEnd);

	/** liefert den Endpunkt des Liniensegments. */
	abstract public VgPoint getEndPoint();

	/**
	 * liefert die Länge des Liniensegments bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	public double length()
	{
		return this.getEndPoint().distance(this.getStartPoint());
	}

	public String toString() {
		return "[" + this.getStartPoint().toString() + ", " +
			this.getEndPoint().toString() + "]";
	}
}