
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.util.Vector;

/**
 * @author Martin May
 */
public class GmSimpleMesh extends VgMesh {

	private int mNumberOfPoints;
	private VgPoint mPoints[];
	private int mNumberOfLineSegments = 0;
	//private int mLineSegments[][];
	private Vector mLineSegments;
	private GmEnvelope mEnv;
	private boolean[][] adjMatrix;
	private int doppelCount = 0;

	/**
		 Konstruktor. Die Netz-Größe lässt sich über die Methoden <i>this.newPointList()</i> und
		 <i>this.newLineSegmentList()</i> nachträglich ändern.
		 TODO ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
		 @param pNumberOfPoints Anzahl der Punkte des Netzes
		 */
	public GmSimpleMesh(int pNumberOfPoints) {
		mNumberOfPoints = pNumberOfPoints;
		mPoints = new GmPoint[mNumberOfPoints];
		adjMatrix = new boolean[mNumberOfPoints][mNumberOfPoints];
		this.allocateStorage();
		//mLineSegments = new int[mNumberOfLineSegments][3];
		mLineSegments = new Vector();
		mEnv = null;
	}

	/**
	 * Methode zum Hinzufügen eines LineSegmentes zwischen zwei Vertizes
	 * Es ist stets die Bedingung <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i>
	 * und <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i>  einzuhalten;
	 * anderenfalls wird eine <i>T3dException</i> geworfen.
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 * @throws org.n52.v3d.triturus.core.T3dException
	 * 
	 * TODO Implementierung zu "Naiv"....
	 */
	public void addLineSegment(int vertex1, int vertex2) throws T3dException {
		try {
			if (!areConnected(vertex1, vertex2)) {
				mLineSegments.add(
					new GmLineSegment(getPoint(vertex1), getPoint(vertex2)));
				mNumberOfLineSegments++;
				adjMatrix[vertex1][vertex2] = true; //markiert als verbunden
				adjMatrix[vertex2][vertex1] = true; //markiert als verbunden
				adjMatrix[vertex1][vertex1] = true; //markiert als verbunden
				adjMatrix[vertex2][vertex2] = true; //markiert als verbunden
			}
			else
				doppelCount++;
		}
		catch (T3dException e) {
		}
	}

	/**
	 * liefert Aussage darüber, ob zwei Vertizes im Netz mit einem
	 * LineSegment verbunden sind.
	 * Es ist stets die Bedingung <i>0 &lt;= vertex1 &lt; this.getNumberOfPoints()</i>
	 * und <i>0 &lt;= vertex2 &lt; this.getNumberOfPoints()</i>  einzuhalten;
	 * anderenfalls wird eine <i>T3dException</i> geworfen.
	 * @param vertex1 Index des ersten Vertex
	 * @param vertex2 Index des zweiten Vertex
	 * @return <b><i>true</i></b> fals der LineSegment enthalten  ist,
	 *         <b><i>false</i></b> sonst
	 * @throws T3dException
	 *
	 * TODO 1. vielleicht ein int[] als Parameter
	 * TODO 2. andere Namensgebung?????????
	 */
	public boolean areConnected(int vertex1, int vertex2) throws T3dException {
		boolean result = false;
//				try {
//					VgPoint sPoint = getPoint(vertex1);
//					VgPoint ePoint = getPoint(vertex2);
//					GmLineSegment ls = new GmLineSegment(sPoint, ePoint);
//					if (mLineSegments.contains(ls)) {
//						result = true;
//						//TODO muss raus
//						//doppelCount++;
//					}
//				}
//				catch (T3dException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

		try {
			if (adjMatrix[vertex1][vertex2] || adjMatrix[vertex2][vertex1]) {
				result = true;
				//doppelCount++;
			}
		}
		catch (T3dException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getLineIndexArray()
	 */
	public int[] getLineIndexArray() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getLineSegment(int)
	 */
	public VgLineSegment getLineSegment(int i) throws T3dException {
		// TODO Auto-generated method stub
		return (VgLineSegment) mLineSegments.get(i);
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getLineSegmentVertexIndices(int)
	 */
	public int[] getLineSegmentVertexIndices(int i) throws T3dException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getNumberOfLineSegments()
	 */
	public int getNumberOfLineSegments() {
		// TODO Auto-generated method stub
		return mNumberOfLineSegments;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getNumberOfPoints()
	 */
	public int getNumberOfPoints() {
		// TODO Auto-generated method stub
		return mNumberOfPoints;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.VgMesh#getPoint(int)
	 */
	public VgPoint getPoint(int i) throws T3dException {
		// TODO Auto-generated method stub
		return mPoints[i];
	}

    public VgEnvelope envelope() {
        throw new T3dNotYetImplException();
    }

    public VgGeomObject footprint() {
        throw new T3dNotYetImplException();
    }

	private void allocateStorage() {
		for (int i = 0; i < mNumberOfPoints; i++) {
			mPoints[i] = new GmPoint(0., 0., 0.);
			for (int j = 0; j < mNumberOfPoints; j++) {
				adjMatrix[i][j] = false;
			}
		}
	}

	/**
	* setzt den i-ten Punkt (Vertex) des Netzes
	* Es ist stets die Bedingung <i>0 &lt;= i &lt; this.getNumberOfPoints()</i> einzuhalten;
	* anderenfalls wird eine <i>T3dException</i> geworfen.
	* @throws T3dException
	*/
	public void setPoint(int i, VgPoint pPnt) throws T3dException {
		try {
			mPoints[i].set(pPnt);
		}
		catch (T3dException e) {
			throw e;
		}
	}
	
	public void setPoints(VgPoint[] pointArray){
		mPoints = pointArray;
	}

	/**
	 * @return anzahl ausgeschlossener kanten(doppelten)
	 * TODO muss raus
	 */
	public int getDoppelCount() {
		return doppelCount;
	}
	
	public boolean[][] getAdjMatrix(){
		return adjMatrix;
	}

}
