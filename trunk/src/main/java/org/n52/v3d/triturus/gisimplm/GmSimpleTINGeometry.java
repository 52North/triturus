package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.*;

/**
 * Klasse zur Verwaltung einer in ihrer Größe statischen TIN-Geometrie.<p>
 * <b>TODO: Die Bounding-Box-Berechnung ist noch nicht optimiert; siehe ggf. Coorg.n52.v3d.</b>
 * @author Benno Schmidt, Ilya Abramov<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmSimpleTINGeometry extends VgIndexedTIN
{
	private int mNumberOfPoints;
	private VgPoint[] mPoints;
	private int mNumberOfTriangles;
	private int mTriangles[][];
	private GmEnvelope mEnv;
	private GmSimpleMesh mMesh;

	/**
     * Konstruktor. Die TIN-Größe lässt sich über die Methoden <i>this.newPointList()</i> und
     * <i>this.newTriangleList()</i> nachträglich ändern.<p>
     * @param pNumberOfPoints Anzahl der Punkte des TINs
     * @param pNumberOfTriangles Anzahl der Deriecke des TINs
	 */
	public GmSimpleTINGeometry(int pNumberOfPoints, int pNumberOfTriangles)
    {
		mNumberOfTriangles = pNumberOfTriangles;
		mPoints = new GmPoint[mNumberOfPoints];
		this.allocateStorage();

		mNumberOfPoints = pNumberOfPoints;
		mTriangles = new int[mNumberOfTriangles][3];

		mEnv = null;
	}

	private void allocateStorage() {
		for (int i = 0; i < mNumberOfPoints; i++) {
			mPoints[i] = new GmPoint(0., 0., 0.);
		}
	}

	/** liefert die Anzahl durch das TIN vermaschter Punkte (Vertizes).<p> */
	public int numberOfPoints() {
		return mNumberOfPoints;
	}

	/** liefert die Anzahl der Dreiecke (Facetten) des TINs.<p> */
	public int numberOfTriangles() {
		return mNumberOfTriangles;
	}

	/**
     * liefert den i-ten Punkt (Vertex) des TINs.<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfPoints()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.<p>
     * @throws org.n52.v3d.triturus.core.T3dException
	 */
	public VgPoint getPoint(int i) throws T3dException {
		try {
			return mPoints[i];
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * setzt den i-ten Punkt (Vertex) des TINs.<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfPoints()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.<p>
     * @throws T3dException
	 */
	public void setPoint(int i, VgPoint pPnt) throws T3dException {
		try {
			if (mCalculated) {
				VgPoint lOldPoint = mPoints[i];
				this.updateBounds(lOldPoint, pPnt);
			}

			mPoints[i].set(pPnt);
		}
		catch (T3dException e) {
			throw e;
		}
	}

	/**
     * liefert das i-te Dreieck (Facette) des TINs.<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfTriangles()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.
     * @throws T3dException
	 */
	public VgTriangle getTriangle(int i) throws T3dException {
		try {
			return new GmTriangle(
				mPoints[mTriangles[i][0]],
				mPoints[mTriangles[i][1]],
				mPoints[mTriangles[i][2]]);
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * setzt das i-te Dreieck (Facette) des TINs als aus den Eckpunkten mit den Indizes <tt>pPntIdx1, pPntIdx2</tt> und
     * <tt>pPntIdx3</tt> bestehend.<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfTriangles()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.<p>
     * @throws T3dException
	 */
	public void setTriangle(int i, int pPntIdx1, int pPntIdx2, int pPntIdx3)
		throws T3dException {
		try {
			mTriangles[i][0] = pPntIdx1;
			mTriangles[i][1] = pPntIdx2;
			mTriangles[i][2] = pPntIdx3;
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * liefert die Vertex-Indizes des i-ten Dreiecks (Facette) des TINs.<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfTriangles()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.<p>
     * @throws T3dException
	 */
	public int[] getTriangleVertexIndices(int i) throws T3dException {
		try {
			return new int[] { mTriangles[i][0], mTriangles[i][1], mTriangles[i][2] };
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * löscht die bestehende Punktliste. Hierdurch geht grundsätzlich auch die bestehende Dreiecksliste verloren.<p>
	 * @param pNumberOfPoints Anzahl der Punkte des TINs
	 */
	public void newPointList(int pNumberOfPoints) {
		mNumberOfPoints = pNumberOfPoints;
		mPoints = new GmPoint[mNumberOfPoints];
		this.allocateStorage();

		mNumberOfTriangles = 0;
		mTriangles = null;

		mEnv = null;
	}

	/**
     * löscht die bestehende Dreeicksliste.<p>
     * @param pNumberOfTriangles Anzahl der Dreiecke des TINs
	 */
	public void newTriangleList(int pNumberOfTriangles) {
		mNumberOfTriangles = pNumberOfTriangles;
		mTriangles = new int[mNumberOfTriangles][3];
	}

	/**
     * liefert die Bounding-Box der TIN-Geometrie.<p>
     * @return Bounding-Box (in xy-Ebene); im Fehlerfall wird <i>null</i> zurückgegeben.
     * */
	public VgEnvelope envelope() {
		if (!mCalculated) {
			try {
				this.calculateBounds();
			}
			catch (Exception e) {
				return null;
			}
		}

		return mEnv;
	}

    public VgGeomObject footprint() {
        throw new T3dNotYetImplException();
    }

	/**
     * liefert den niedrigsten im TIN enthaltenen Höhenwert.<p>
     * @throws T3dException
	 */
	public double minimalElevation() throws T3dException {
		if (mEnv == null) {
			throw new T3dException("TIN envelope not available.");
		}
		else {
			return mEnv.getZMin();
		}
	}

	/**
     * liefert den höchsten im TIN enthaltenen Höhenwert.<p>
     * @throws T3dException
	 */
	public double maximalElevation() throws T3dException {
		if (mEnv == null) {
			throw new T3dException("TIN envelope not available.");
		}
		else {
			return mEnv.getZMax();
		}
	}

	/**
     * deaktiviert "lazy evaluation" der Bounding-Box. Eine explizite Deaktivierung unmittelbar vor TIN-Editierungen
     * (<tt>this.setPoint()</tt>-Aufrufen) kann aus Performanz-Gründen notwendig werden.
	 */
	public void setBoundsInvalid() {
		mCalculated = false;
	}

	// private Helfer zur Berechnung der Bounding-Box ("lazy evaluation"!):

	private boolean mCalculated = false;

	private void calculateBounds() throws T3dException {
		if (mNumberOfPoints <= 0) {
			throw new T3dException("Tried to access empty TIN.");
		}

		if (!mCalculated) {
			mEnv =
				new GmEnvelope(
					mPoints[0].getX(),
					mPoints[0].getX(),
					mPoints[0].getY(),
					mPoints[0].getY(),
					mPoints[0].getZ(),
					mPoints[0].getZ());

			for (int i = 1; i < mNumberOfPoints; i++) {
				mEnv.letContainPoint(mPoints[i]);

			}
			mCalculated = true;
		}
	}

	private void updateBounds(VgPoint pOldPnt, VgPoint pNewPnt) {
		mCalculated = false; // Lösung suboptimal, aber korrekt ;-)
	}

	/**
	 * löscht das i-te Dreieck (Facette) des TINs, restliche Dreiecke werden "nachgeschoben".<p>
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfTriangles()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.<p>
	 * @param i Index des zu löschenden Dreiecks
	 * @throws T3dException
	 * todo verbessern??
	 */
	public void deleteTriangle(int i) throws T3dException {
		if (i > this.numberOfTriangles()) {
			throw new T3dException("Wrong index.");
		}
		for (int c = i; c < mTriangles.length; c++) {
			mTriangles[c][0] = mTriangles[c + 1][0];
			mTriangles[c][1] = mTriangles[c + 1][1];
			mTriangles[c][2] = mTriangles[c + 1][2];
		}
	}

	/**
	 * liefert aus dem TIN generiertes Mesh.<p>
	 * @return Mesh
	 * TODO ???
	 */
	public GmSimpleMesh getMesh() {
		mMesh = generateMesh();
		return mMesh;
	}
	/**
	 * generiert Mesh.<p>
	 * @return
	 */
	private GmSimpleMesh generateMesh() {
		GmSimpleMesh result = new GmSimpleMesh(mNumberOfPoints);
		//	kopiere Vertizes
		//	for (int i = 0; i < mNumberOfPoints; i++) {
		//		result.setPoint(i, getPoint(i));
		//	}
		result.setPoints(mPoints);
		//durchführen  der Vermaschung
		int[] trVertInd;
		for (int i = 0; i < mNumberOfTriangles; i++) {
			trVertInd = getTriangleVertexIndices(i);
			result.addLineSegment(trVertInd[0], trVertInd[1]);
			result.addLineSegment(trVertInd[1], trVertInd[2]);
			result.addLineSegment(trVertInd[2], trVertInd[0]);
		}
		return result;
	}

	/**
	 * @return Vertex-Array (VgPoint!)
	 */
	public VgPoint[] getPoints() {
		return mPoints;
	}
}
