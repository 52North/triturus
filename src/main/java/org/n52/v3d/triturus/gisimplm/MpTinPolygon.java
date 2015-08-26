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
package org.n52.v3d.triturus.gisimplm;

import java.util.Vector;

import org.n52.v3d.triturus.core.T3dProcMapper;

/**
 * @deprecated
 * todo: Diese Sachen sollten in die Klasse FltTINPolygonAssembler wandern!
 * Mapper Klasse zur Verschneidung eines TINs mit einem Polygon.
 * @author Martin May, Ilja Abramovic
 */
public class MpTinPolygon extends T3dProcMapper
{
	private GmSimpleTINGeometry tin;
	private GmPolygon pol;

	/**
	 * Constructor.
	 * @param tin TIN 
	 * @param pol Polygon
	 */
	public MpTinPolygon(GmSimpleTINGeometry tin, GmPolygon pol) {
		this.tin = tin;
		this.pol = pol;
	}

	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * returns the TIN that results from the intersection performed.
	 * @return TIN-geometry
	 */
	public GmSimpleTINGeometry intersect() {
		GmSimpleTINGeometry result =
			new GmSimpleTINGeometry(
				tin.numberOfPoints() + 100,
				tin.numberOfTriangles() + 300);
		return result;
	}

	/**
	 * Liefert ein Array mit Indizien der Vertices, die im Polygon liegen.
	 * @return  ^^^^^^^^
	 */
	private int[] verticesInPolygonIndices() {
		int[] result = {};
		//Create jts polygon:
//		PrecisionModel pm = new PrecisionModel();
//		int numPolPoints = pol.numberOfVertices();
//		Coordinate[] polPoints = new Coordinate[numPolPoints];
//		for (int i = 0; i < polPoints.length; i++) {
//			polPoints[i] =
//				new Coordinate(pol.getVertex(i).getX(), pol.getVertex(i).getY());
//		}
//		LinearRing sh = new LinearRing(polPoints, pm, 0);
//
//		//Polygon jtsPol = new Polygon(sh, pm, 0);
//
//		Vector v = new Vector(); //help var
//		//
//		//	create MCPointInRing algorithm
//		MCPointInRing alg = new MCPointInRing(sh);
//		//hole ein Vertex aus TIN und pr�fe, ob in Polygon
//		int numTinPoints = tin.numberOfPoints();
//		for (int i = 0; i < numTinPoints; i++) {
//			VgPoint p = tin.getPoint(i);
//			Coordinate tinPointAsJtsCoord = new Coordinate(p.getX(), p.getY());
//			if (alg.isInside(tinPointAsJtsCoord))
//				v.add(new Integer(i));
//		}
//		result = new int[v.size()];
//		for (int i = 0; i < result.length; i++) {
//			result[i] = ((Integer) v.elementAt(i)).intValue();
//		}
		return result;
	}

	/**
	 * liefert ein Vector mit Indizien der Vertices, die die Kanten bilden, die den Polygon schneiden. Ein Element im
     * Vector ist ein zweielementiges <i>int</i> Array.
	 * @param vertInPol Array mit Indizien der Vertices, die im Polygon liegen.
	 * @return
	 */
	private Vector involvedEdgesAsVertexPairsIndices(int[] vertInPol) {
		GmSimpleMesh mesh = tin.getMesh();
		Vector v = new Vector(); //help var
		boolean[][] aMatrix = mesh.getAdjMatrix();
		for (int i = 0; i < vertInPol.length; i++) {//i - index eines inneren Vertex
			for (int j = 0; j < mesh.getNumberOfPoints(); j++) {
				if (aMatrix[vertInPol[i]][j] || i!=j) {
					int[] edge = {i,j};
					v.add(edge);
				}
			}
		}
		return v;
	}

}
