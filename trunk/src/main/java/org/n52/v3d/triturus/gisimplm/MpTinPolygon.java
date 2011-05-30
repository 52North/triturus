
package org.n52.v3d.triturus.gisimplm;

import java.util.Vector;

import org.n52.v3d.triturus.core.T3dProcMapper;

/**
 * @deprecated
 * todo: Diese Sachen sollten in die Klasse FltTINPolygonAssembler wandern!
 * Mapper Klasse zur Verschneidung eines TINs mit einem Polygon.<p>
 * @author Martin May, Ilja Abramovic<p>
 * (c) 2003, Institute for Geoinformatics<br>
 */
public class MpTinPolygon extends T3dProcMapper
{
	private GmSimpleTINGeometry tin;
	private GmPolygon pol;

	/**
	 * Konstruktor.
	 * @param tin TIN 
	 * @param pol Polygon
	 */
	public MpTinPolygon(GmSimpleTINGeometry tin, GmPolygon pol) {
		this.tin = tin;
		this.pol = pol;
	}

	/**
	 * protokolliert die durchgeführte Transformation.
	 * @see org.n52.v3d.triturus.core.T3dProcMapper#log()
	 */
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * liefert das TIN, das Ergebnis der Verschneidung ist.<p>
	 * @return TIN-Geometrie
	 */
	public GmSimpleTINGeometry intersect() {
		GmSimpleTINGeometry result =
			new GmSimpleTINGeometry(
				tin.numberOfPoints() + 100,
				tin.numberOfTriangles() + 300);
		return result;
	}

	/**
	 * Liefert ein Array mit Indizien der Vertices, die im Polygon liegen.<p>
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
//		//hole ein Vertex aus TIN und prüfe, ob in Polygon
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
	 * Liefert ein Vector mit Indizien der Vertices, die die Kanten bilden, die den Polygon schneiden. Ein Element im
     * Vector ist ein zweielementiges <i>int</i> Array.<p>
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
