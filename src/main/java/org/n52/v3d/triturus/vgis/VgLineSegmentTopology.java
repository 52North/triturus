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
 * Class to manage points that are connected by line-segments.<br /><br />
 * <i>German:</i> Bem.: Die Schnittstelle l&auml;sst sich z. B. f&uuml;r die Modellierung von Oberfl&auml;chen-Netzen
 * nutzen. Es ist allerdings zu beachten, dass sich durch die Liniensegment-Topologie nicht zwingend r&auml;umlich
 * Facetten ergeben (Tesselierungsproblematik). Die Liniensegmente der Topologie k&ouml;nnen beliebige r&auml;umliche
 * Beziehungen einnehmen (z. B. &Uuml;berlappen, Schneiden etc.).
 * @author Martin May, Benno Schmidt
 */
abstract public class VgLineSegmentTopology extends VgGeomObject 
{
	/**
     * returns the number of intermeshed points (<i>vertices</i>).
     * @return Number of vertices
     */
	abstract public int numberOfPoints();

    /**
     * returns the topology's number of line-segments (<i>edges</i>).
     * @return Number of line-segments
     */
	abstract public int numberOfLineSegments();

	/**
	 * returns the i-th point (vertex) of the topological structure. The assertion
     * 0 &lt;= i &lt; <tt>this.numberOfPoints()</tt> must always hold; otherwise a <tt>T3dException</tt> will be thrown.
     * @param i Vertex index
     * @return Point geometry
     * @throws T3dException
	 */
	abstract public VgPoint getPoint(int i) throws T3dException;

	/**
	 * returns the i-th line-segment (edge) of the topological structure. The assertion
	 * 0 &lt;= i &lt; <tt>this.numberOfLineSegments()</tt> must always hold; otherwise a <tt>T3dException</tt> will be
     * thrown.
     * @param i the line-segment's index
     * @return Line-segment geometry
     * @throws T3dException
	 */
	abstract public VgLineSegment getLineSegment(int i) throws T3dException;

	/**
	 * returns the vertex-indices of the i-th line-segment (edge) of the topological structure. The assertion
	 * 0 &lt;= i &lt; <tt>this.numberOfLineSegments()</tt> must always hold; otherwise a <tt>T3dException</tt> will be
     * thrown.
     * @return Array holding vertex-indices
     * @throws T3dException
	 */
	abstract public int[] getLineSegmentVertexIndices(int i) throws T3dException;

	/**
	 * returns an array holding the indices of all vertices. Per line-segment a pair of vertices will be given. Thus,
     * the, result array will contain <tt>2 * this.numberOfLineSegments()</tt> elements.
	 * @return Array holding vertex-index pairs of the topological structure
	 */
	abstract public int[] getLineIndexArray();

	/**
	 * returns the information, whether two vertices are connected to each other inside the defined topological
     * structure.the assertions &lt;= vertex1 &lt; <tt>this.numberOfPoints()</tt> and
	 * 0 &lt;= vertex2 &lt; <tt>this.numberOfPoints()</tt> must hold; otherwise a <tt>T3dException</tt> will be
     * thrown.
	 * @param vertex1 Index of first vertex
	 * @param vertex2 Index of second vertex
	 * @return <i>true</i> if the topological structure contains the line segment, else <i>false</i>
     * @throws T3dException
	 */
	abstract public boolean areConnected(int vertex1, int vertex2) throws T3dException;

	/**
	 * adds a line-segment to the topological structure. After method execution, the assertion
	 * <tt>this.areConnected(vertex1, vertex2) = true</tt> holds.<br />
	 * The assertions 0 &lt;= vertex1 &lt; <tt>this.numberOfPoints()</tt> and
	 * 0 &lt;= vertex2 &lt; <tt>this.numberOfPoints()</tt> must hold; otherwise a <tt>T3dException</tt> will be
     * thrown.
	 * @param vertex1 Index of first vertex
	 * @param vertex2 Index of second vertex
     * @throws T3dException
	 */
	abstract public void addLineSegment(int vertex1, int vertex2) throws T3dException;

	public String toString() {
		return "[" +
			"(# " + this.numberOfPoints() + " vertices), " +
			"(# " + this.numberOfLineSegments() + " line segments)]";
	}
}