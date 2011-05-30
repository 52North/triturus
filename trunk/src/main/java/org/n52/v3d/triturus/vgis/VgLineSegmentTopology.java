package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung von durch Liniensegmente miteinander verbundener Punkte.<p>
 * Bem.: Die Schnittstelle lässt sich z. B. für die Modellierung von Oberflächen-Netzen nutzen. Es ist allerdings zu
 * beachten, dass sich durch die Liniensegment-Topologie nicht zwingend räumlich Facetten ergeben
 * (Tesselierungsproblematik). Die Liniensegmenten der Topologie können beliebige räumliche Beziehungen einnehmen
 * (z. B. Überlappen, Schneiden etc.).<p>
 * @author Martin May und Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgLineSegmentTopology extends VgGeomObject 
{
	/** liefert die Anzahl miteinander vermaschten Punkte (Vertizes). */
	abstract public int numberOfPoints();

	/** liefert die Anzahl der Liniensegmente (Kanten) der Topologie. */
	abstract public int numberOfLineSegments();

	/**
	 * liefert den i-ten Punkt (Vertex) der der Topologie.<p>
	 * Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfPoints()</tt> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 * liefert das i-te Liniensegment (Kante) der Topologie.<p>
	 * Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfLineSegments()</tt> einzuhalten; anderenfalls wird
     * eine <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public VgLineSegment getLineSegment(int i) throws T3dException;

	/**
	 * liefert die Vertex-Indizes des i-ten Liniensegmentes (Kante) der Topologie.<p>
	 * Es ist stets die Bedingung 0 &lt;= i &lt; <tt>this.numberOfLineSegments()</tt> einzuhalten; anderenfalls wird
     * eine <tt>T3dException</tt> geworfen.<p>
	 */
	abstract public int[] getLineSegmentVertexIndices(int i) throws T3dException;

	/**
	 * liefert ein Array mit den Indizes aller Vertizes. Pro Liniensegment ist jeweils ein Vertex-Paar angegeben, so
     * dass das resultierende Array <tt>2 * this.numberOfLineSegments()</tt> Elemente umfasst.<p>
	 * @return Array mit Vertex-Index-Paaren der Topologie
	 */
	abstract public int[] getLineIndexArray();

	/**
	 * liefert die Information, ob zwei Vertizes der Topologie über ein Liniensegment miteinmander verbunden
     * sind.<p>
	 * Es sind stets die Bedingungen 0 &lt;= vertex1 &lt; <tt>this.numberOfPoints()</tt> und
	 * 0 &lt;= vertex2 &lt; <tt>this.numberOfPoints()</tt> einzuhalten; anderenfalls wird eine <tt>T3dException</tt>
     * geworfen.<p>
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 * @return <i>true</i>, falls das Liniensegment in der Topologie enthalten ist, sonst <i>false</i>.
	 */
	abstract public boolean areConnected(int vertex1, int vertex2) throws T3dException;

	/**
	 * fügt der Topologie ein Liniensegment hinzu.<p>
	 * Nach Durchführung der Methode gilt: <tt>this.areConnected(vertex1, vertex2) = true</tt>.<p>
	 * Es sind stets die Bedingungen 0 &lt;= vertex1 &lt; <tt>this.numberOfPoints()</tt> und
	 * 0 &lt;= vertex2 &lt; <tt>this.numberOfPoints()</tt> einzuhalten; anderenfalls wird eine <tt>T3dException</tt>
     * geworfen.<p>
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 */
	abstract public void addLineSegment(int vertex1, int vertex2) throws T3dException;

	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " vertices), " +
			"(# " + this.numberOfLineSegments() + " line segments)]";
	}
}