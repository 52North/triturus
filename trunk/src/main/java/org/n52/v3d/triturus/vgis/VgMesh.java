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
 * @deprecated
 * Class to hold meshes.
 * TODO: pr�fen ob klasse ersetzt werden kann durch VgLineSegmentTopology
 * @author
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
	 * liefert Aussage dar�ber ob zwischen zwei Vertizes im Netz mit einem
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
	 * Methode zum Hinzuf�gen eines LineSegmentes zwischen zwei Vertizes
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