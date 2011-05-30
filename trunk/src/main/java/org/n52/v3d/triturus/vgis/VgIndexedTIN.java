package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung indizierter TINs (Spezialisierung der Klasse <tt>VgTIN</tt>).<p>
 * Die Stützpunkte (Vertizes) des indizierten TINs sind nummeriert, die Dreiecke (Facetten) lassen sich unter
 * Verwendung dieser Indizierung ansprechen.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgIndexedTIN extends VgTIN
{
	/**
	 * liefert die Vertex-Indizes des i-ten Dreiecks (Facette) des TINs.<p>
	 * Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public int[] getTriangleVertexIndices(int i) throws T3dException;

	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " indexed vertices), " +
			"(# " + this.numberOfTriangles() + " triangles)]";
	}
}