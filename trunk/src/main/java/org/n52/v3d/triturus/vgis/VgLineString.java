package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung linienhafter Geometrien (Polylinien).<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgLineString extends VgGeomObject1d 
{
	/** gibt die Anzahl der Vertizes (Stützpunkte) der Polylinie zurück. */
	abstract public int numberOfVertices();

	/**
	 * liefert den i-ten Vertex (Stützpunkt) der Polylinie.<p>
	 * Hierbei ist die Bedingung <i>0 &lt;= i &lt; this.numberOfVertices()</i> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 * @throws T3dException
	 */
	abstract public VgPoint getVertex(int i) throws T3dException;
	
	/**
	 * liefert die Länge der Polylinie bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	public double length() 
	{
		double sum = 0.;
		for (int i = 0; i < this.numberOfVertices() - 1; i++)
			sum += this.getVertex(i + 1).distance( this.getVertex(i) );
		return sum;
	}

	public String toString() 
	{
		String str = "[";
		if (this.numberOfVertices() > 0) {
			for (int i = 0; i < this.numberOfVertices() - 1; i++) {
				str = str + this.getVertex(i).toString() + ", ";
			}
			str = str + this.getVertex(this.numberOfVertices() - 1).toString();
		}
		return str + "]";
	}
}
