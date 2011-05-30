package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.*;

import java.util.Arrays;

/**
 * todo: Kommentar
 * @author Martin May, Ilja Abramovic<br>
 * (c) 2003, Institute for Geoinformatics<br>
 */
public class GmTopoTIN extends VgIndexedTIN {

	private int numberOfVertices = 0;
	private int numberOfEdges = 0;
	private int numberOfTriangles = 0;

	private VgPoint[] vertices;
	private int[] edges; //referenzen auf die vertices, pairs
	private int[] triangles; //referenzen auf die kanten, triples

	public GmTopoTIN(GmSimpleTINGeometry sTinGeom) {
//		long sTime = System.currentTimeMillis();
		firstMethod(sTinGeom);
		//secondMethod(sTinGeom);
//		long eTime = System.currentTimeMillis();
//		long laufzeit = eTime-sTime;
//		System.out.println("laufzeit: "+ laufzeit);
	}

	private void secondMethod(GmSimpleTINGeometry sTinGeom) {
		numberOfVertices = sTinGeom.numberOfPoints();
		numberOfTriangles = sTinGeom.numberOfTriangles();
		vertices = sTinGeom.getPoints();

		triangles = new int[numberOfTriangles * 3];

		int[][] flagMatrix = new int[numberOfVertices][numberOfVertices];
		//eine zelle der matrix = -1, falls i-ter und j-ter vertices nicht verbunden sind,
		//sonst steht da die kantennummer, mit der die verbunden sind.
		for (int i = 0; i < numberOfVertices; i++) {
			Arrays.fill(flagMatrix[i], -1);
			//-1: erstmal sind alle vertices nicht verbunden
		}
		int currentEdgeNr = 0;
		for (int i = 0; i < numberOfTriangles; i++) {
			int[] tr = sTinGeom.getTriangleVertexIndices(i);
			for (int j = 0; j < tr.length; j++) {
				if ((flagMatrix[tr[0]][tr[1]] == -1)
					|| (flagMatrix[tr[1]][tr[0]] == -1)) {
					//also  tr[0] u. tr[1] noch nicht verbunden
					flagMatrix[tr[0]][tr[1]] = currentEdgeNr++;
					flagMatrix[tr[1]][tr[0]] = flagMatrix[tr[0]][tr[1]];
				}
				triangles[i * 3] = flagMatrix[tr[0]][tr[1]];
				if ((flagMatrix[tr[1]][tr[2]] == -1)
					|| (flagMatrix[tr[2]][tr[1]] == -1)) {
					//also  tr[1] u. tr[2] noch nicht verbunden
					flagMatrix[tr[1]][tr[2]] = currentEdgeNr++;
					flagMatrix[tr[2]][tr[1]] = flagMatrix[tr[1]][tr[2]];
				}
				triangles[i * 3 + 1] = flagMatrix[tr[1]][tr[2]];
				if ((flagMatrix[tr[2]][tr[0]] == -1)
					|| (flagMatrix[tr[0]][tr[2]] == -1)) {
					//also  tr[2] u. tr[0] noch nicht verbunden
					flagMatrix[tr[2]][tr[0]] = currentEdgeNr++;
					flagMatrix[tr[0]][tr[2]] = flagMatrix[tr[2]][tr[0]];
				}
				triangles[i * 3 + 2] = flagMatrix[tr[2]][tr[0]];
			}
		}
		numberOfEdges = currentEdgeNr;
		edges = new int[numberOfEdges * 2];
		for (int i = 0; i < flagMatrix.length; i++) {
			for (int j = i + 1; j < flagMatrix.length; j++) {
				if (flagMatrix[i][j] != -1) {
					edges[flagMatrix[i][j] * 2] = i;
					edges[flagMatrix[i][j] * 2 + 1] = j;
				}
			}
		}
	}

	private void firstMethod(GmSimpleTINGeometry sTinGeom) {
		numberOfVertices = sTinGeom.numberOfPoints();
		numberOfTriangles = sTinGeom.numberOfTriangles();
		vertices = sTinGeom.getPoints();
		//Dies wird der neue Dreiecks-Index - auf die Kanten:
		triangles = new int[numberOfTriangles * 3];
		//Hier wird zunächst ein zu großer Puffer gebildet, um performanten Zugriff zu ermöglichen:
		edges = new int[numberOfTriangles * 3 * 2]; //max mögl. anzahl
		int[] exists = { -1, -1, -1 }; //0-1, 1-2, 0-2
		for (int i = 0; i < numberOfTriangles; i++) {
			int[] tr = sTinGeom.getTriangleVertexIndices(i);
			//Richtung der Kanten geht verloren:
			Arrays.sort(tr);
			//Die erzeugten Kanten werden auf Existenz geprüft
			//Äußerst unperformant...
			for (int j = 0; j < numberOfEdges; j++) {
				if (tr[0] == edges[j * 2]) {
					if (tr[1] == edges[j * 2 + 1]) {
						exists[0] = j;
					}
					if (tr[2] == edges[j * 2 + 1]) {
						exists[2] = j;
					}
				}
				if (tr[1] == edges[j * 2]) {
					if (tr[2] == edges[j * 2 + 1]) {
						exists[1] = j;
					}
				}
			}

			//			if (exists[0] == -1) {
			//				edges[numberOfEdges * 2] = tr[0];
			//				edges[numberOfEdges * 2 + 1] = tr[1];
			//				triangles[i] = numberOfEdges++;
			//			}
			//			else {
			//				triangles[i] = exists[0];
			//				exists[0] = -1;
			//			}
			//			if (exists[1] == -1) {
			//				edges[numberOfEdges * 2] = tr[1];
			//				edges[numberOfEdges * 2 + 1] = tr[2];
			//				triangles[i] = numberOfEdges++;
			//			}
			//			else {
			//				triangles[i] = exists[1];
			//				exists[1] = -1;
			//			}
			//			if (exists[2] == -1) {
			//				edges[numberOfEdges * 2] = tr[0];
			//				edges[numberOfEdges * 2 + 1] = tr[2];
			//				triangles[i] = numberOfEdges++;
			//			}
			//			else {
			//				triangles[i] = exists[2];
			//				exists[2] = -1;
			//			}

			if (exists[0] == -1) {
				edges[numberOfEdges * 2] = tr[0];
				edges[numberOfEdges * 2 + 1] = tr[1];
				triangles[i * 3] = numberOfEdges++;
			}
			else {
				triangles[i * 3] = exists[0];
				exists[0] = -1;
			}
			if (exists[1] == -1) {
				edges[numberOfEdges * 2] = tr[1];
				edges[numberOfEdges * 2 + 1] = tr[2];
				triangles[i * 3 + 1] = numberOfEdges++;
			}
			else {
				triangles[i * 3 + 1] = exists[1];
				exists[1] = -1;
			}
			if (exists[2] == -1) {
				edges[numberOfEdges * 2] = tr[0];
				edges[numberOfEdges * 2 + 1] = tr[2];
				triangles[i * 3 + 2] = numberOfEdges++;
			}
			else {
				triangles[i * 3 + 2] = exists[2];
				exists[2] = -1;
			}
		}
		int[] temp = new int[numberOfEdges * 2];
		System.arraycopy(edges, 0, temp, 0, numberOfEdges * 2);
		//Altes Array ist Garbage...
		edges = temp;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgTIN#numberOfPoints()
	 */
	public int numberOfPoints() {
		// TODO Auto-generated method stub
		return numberOfVertices;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgTIN#numberOfTriangles()
	 */
	public int numberOfTriangles() {
		// TODO Auto-generated method stub
		return numberOfTriangles;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgTIN#getPoint(int)
	 */
	public VgPoint getPoint(int i) throws T3dException {
		// TODO Auto-generated method stub
		return vertices[i];
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgTIN#getTriangle(int)
	 */
	public VgTriangle getTriangle(int i) throws T3dException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param i Dreieckindex
	 * @return Kantenarray des i-ten Dreiecks
	 * @throws T3dException
	 */
	public int[] getTriangleAsEdges(int i) throws T3dException {
		int[] result =
			{ triangles[i * 3], triangles[i * 3 + 1], triangles[i * 3 + 2] };
		return result;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgGeomObject#envelope()
	 */
	public VgEnvelope envelope() {
		// TODO Auto-generated method stub
		return null;
	}

    public VgGeomObject footprint() {
        throw new T3dNotYetImplException();
    }

	/**
	 * @return Kantenanzahl
	 */
	public int numberOfEdges() {
		return numberOfEdges;
	}

	/**
	 * @return kantenarray
	 */
	public int[] getEdges() {
		return edges;
	}

	/**
	 * @return Vertexarray
	 */
	public VgPoint[] getVertices() {
		return vertices;
	}

	/**
	 * @param i Vertexindex
	 * @return i-ter Vertex
	 */
	public VgPoint getVertex(int i) {
		return vertices[i];
	}

	/**
	 * @return Dreieckarray
	 */
	public int[] getTriangles() {
		return triangles;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgTIN#getTriangleVertexIndices(int)
	 */
	public int[] getTriangleVertexIndices(int i) throws T3dException {
		// TODO Auto-generated method stub
		return null;
	}

}
