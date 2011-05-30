package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * @deprecated
 * Klasse zur Verwaltung von Mesh's.
 * TODO: prüfen ob klasse ersetzt werden kann durch VgLineSegmentTopology
 * @author
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 * @see org.n52.v3d.triturus.vgis.VgLineSegmentTopology
 */

abstract public class VgMesh
	extends VgGeomObject {

	/** liefert die Anzahl durch das Netz vermaschter Punkte (Vertizes) */
	abstract public int getNumberOfPoints();

	/** liefert die Anzahl der LineSegments (Kanten) des Netzes */
	abstract public int getNumberOfLineSegments();

	/**
	 liefert den i-ten Punkt (Vertex) des Netzes.
	 Es ist stets die Bedingung <i>0 &lt;= i &lt; this.getNumberOfPoints()</i> einzuhalten;
	 anderenfalls wird eine <i>T3dException</i> geworfen.
	 @throws T3dException
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 liefert das i-te LineSegment(Kante) des Netzes.
	 Es ist stets die Bedingung <i>0 &lt;= i &lt;  this.getNumberOfLineSegments()</i> einzuhalten;
	 anderenfalls wird eine <i>T3dException</i> geworfen.
	 @throws T3dException
	 */
	abstract public VgLineSegment getLineSegment(int i) throws T3dException;

	/**
	 liefert die Vertex-Indizes des i-ten LineSegments (Kante) des Netzes.
	 Es ist stets die Bedingung <i>0 &lt;= i &lt; this.getNumberOfLineSegments()</i> einzuhalten;
	 anderenfalls wird eine <i>T3dException</i> geworfen.
	 @throws T3DException
	 */
	abstract public int[] getLineSegmentVertexIndices(int i) throws T3dException;

	/**
	 * liefert ein Array mit Indizien aller Vertizes, je ein Paar Vertizes pro LineSegment
	 * @return ein Array mit der Laenge=this.getNumberOfLineSegments*2
	 */
	abstract public int[] getLineIndexArray();

	/**
	 * liefert Aussage darüber ob zwischen zwei Vertizes im Netz mit einem
	 * LineSegment verbunden sind.
	 * Es ist stets die Bedingung <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i>
	 * und <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i>  einzuhalten;
	 * anderenfalls wird eine <i>T3dException</i> geworfen.
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 * @return <b><i>true</i></b> fals der LineSegment enthalten  ist,
	 *         <b><i>false</i></b> sonst
	 * @throws T3DException
	 *
	 * todo 1. vielleicht ein int[] als Parameter
	 * todo 2. andere Namensgebung?????????
	 */
	abstract public boolean areConnected(int vertex1, int vertex2) throws
		T3dException;

	/**
	 * Methode zum Hinzufügen eines LineSegmentes zwischen zwei Vertizes
	 * Es ist stets die Bedingung <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i>
	 * und <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i>  einzuhalten;
	 * anderenfalls wird eine <i>T3dException</i> geworfen.
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 * @throws T3DException
	 */
	abstract public void addLineSegment(int vertex1, int vertex2) throws
		T3dException;

	public String toString() {
		return "[" +
			"(# " + this.getNumberOfPoints() + " vertices), " +
			"(# " + this.getNumberOfLineSegments() + " triangles)]";
	}
}