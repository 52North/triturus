package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung von TINs ("triangulated irregular networks").<p>
 * Bem.: In <tt>VgTIN</tt>-Objekten ist die Verwendung indizierter Vertizes nicht notwendig (hierdurch werden die
 * Anforderungen an die nutzbaren Implementierungen kleinstmöglich gehalten!). Die spezialisierte Schnittstelle
 * <tt>VgIndexedTIN</tt> ermöglicht stattdessen den Zugriff auf indizierte Vertizes.<p>
 * @see VgIndexedTIN
 * @author Benno Schmidt<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 */
abstract public class VgTIN extends VgGeomObject2d 
{
	/** liefert die Anzahl durch das TIN vermaschter Punkte (Vertizes).<p> */
	abstract public int numberOfPoints();

	/** liefert die Anzahl der Dreiecke (Facetten) des TINs.<p> */
	abstract public int numberOfTriangles();

	/**
	 * liefert den i-ten Punkt (Vertex) des TINs.<p>
	 * Es ist stets die Bedingung 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 * liefert das i-te Dreieck (Facette) des TINs.<p>
	 * Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public VgTriangle getTriangle(int i) throws T3dException;

	/**
	 * liefert den Oberfläche des TINs bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	public double area()
	{
		double sum = 0.;
		VgTriangle tri;
		for (int i = 0; i < this.numberOfTriangles(); i++) {
			tri = this.getTriangle(i);
			sum += tri.area();
		}
		return sum;
	}
	
	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " vertices), " +
			"(# " + this.numberOfTriangles() + " triangles)]";
	}
}