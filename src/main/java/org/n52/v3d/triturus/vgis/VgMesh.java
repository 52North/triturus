/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
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