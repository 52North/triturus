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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.*;

/**
 * Class to hold a TIN-geometry that is static in its size.<br /><br />
 * <i>German:</i> Klasse zur Verwaltung einer in ihrer Gr&ouml;&szlig;e statischen TIN-Geometrie.
 * <b>TODO: Die Bounding-Box-Berechnung ist noch nicht optimiert; siehe ggf. Coorg.n52.v3d.</b>
 * @author Benno Schmidt, Ilya Abramov
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
     * Constructor.<br /><br />
     * <i>German:</i> Konstruktor. Die TIN-Gr&ouml;&szlig;e l&auml;sst sich &uuml;ber die Methoden
     * <tt>this.newPointList()</tt> und <tt>this.newTriangleList()</tt> nachtr&auml;glich &auml;ndern.
     * @param pNumberOfPoints Number of points of the TIN
     * @param pNumberOfTriangles Number of triangles of the TIN
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

	public int numberOfPoints() {
		return mNumberOfPoints;
	}

	public int numberOfTriangles() {
		return mNumberOfTriangles;
	}

	public VgPoint getPoint(int i) throws T3dException {
		try {
			return mPoints[i];
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

    /**
	 * sets the i-th point (vertex) of the TIN structure.<br /><br />
     * <i>German:</i> setzt den i-ten Punkt (Vertex) des TINs. Es ist stets die Bedingung
     * 0 &lt;<= i &lt; <tt>this.numberOfPoints()</tt> einzuhalten; anderenfalls wird eine <tt>T3dException</tt>
     * geworfen.
     * @param i Point index
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
     * sets the i-th triangle (facet) of the TIN structure.<br /><br />
     * <i>German:</i> setzt das i-te Dreieck (Facette) des TINs als aus den Eckpunkten mit den Indizes
     * <tt>pPntIdx1</tt>, <tt></tt>pPntIdx2</tt> und <tt>pPntIdx3</tt> bestehend. Es ist stets die Bedingung
     * 0 &lt;<= i &lt; <tt>this.numberOfTriangles()</tt> einzuhalten; anderenfalls wird eine <tt>T3dException</tt>
     * geworfen.
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

	public int[] getTriangleVertexIndices(int i) throws T3dException {
		try {
			return new int[] { mTriangles[i][0], mTriangles[i][1], mTriangles[i][2] };
		}
		catch (Exception e) {
			throw new T3dException(e.getMessage());
		}
	}

	/**
     * removes an existing point-list.<br /><br />
     * <i>German:</i> l&ouml;scht die bestehende Punktliste. Hierdurch geht grunds&auml;tzlich auch die bestehende
     * Dreiecksliste verloren.
	 * @param pNumberOfPoints Number of points of the TIN
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
     * removes an existing triangle-list.<br /><br />
     * <i>German:</i> l&ouml;scht die bestehende Dreeicksliste.<p>
     * @param pNumberOfTriangles Number of triangles of the TIN
	 */
	public void newTriangleList(int pNumberOfTriangles) {
		mNumberOfTriangles = pNumberOfTriangles;
		mTriangles = new int[mNumberOfTriangles][3];
	}

	/**
     * returns the TIN geometry's bounding-box.
     * @return Bounding-box (in x-y-plane), or <i>null</i> if an error occurs
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
     * returns the TIN's minimum value (lowest elevation).
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
     * returns the TIN's maximum value (highest elevation).
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
     * deactivates lazy evaluation mode.<br /><br />
     * <i>German:</i> deaktiviert &quot;lazy evaluation&quot; der Bounding-Box. Eine explizite Deaktivierung
     * unmittelbar vor TIN-Editierungen (<tt>this.setPoint()</tt>-Aufrufe) kann aus Performanz-Gr&uuml;nden notwendig
     * werden.
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
		mCalculated = false; // L�sung suboptimal, aber korrekt ;-)
	}

	/**
     * deletes the i-th triangle from the TIN.<br /><br />
     * <i>German:</i> l&ouml;scht das i-te Dreieck (Facette) des TINs, restliche Dreiecke werden
     * &quot;nachgeschoben&quot;.<br />
     * Es ist stets die Bedingung <i>0 &lt;= i &lt; this.numberOfTriangles()</i> einzuhalten; anderenfalls wird eine
     * <i>T3dException</i> geworfen.
     * todo verbessern??
	 * @param i Index of the tringle to be deleted
	 * @throws T3dException
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
     * provides a mesh gemnerated from the TIN structure.<br /><br />
     * <i>German:</i> liefert aus dem TIN generiertes Mesh.
     * TODO ???
	 * @return Mesh
	 */
	public GmSimpleMesh getMesh() {
		mMesh = generateMesh();
		return mMesh;
	}

    private GmSimpleMesh generateMesh() {
		GmSimpleMesh result = new GmSimpleMesh(mNumberOfPoints);
		//	kopiere Vertizes
		//	for (int i = 0; i < mNumberOfPoints; i++) {
		//		result.setPoint(i, getPoint(i));
		//	}
		result.setPoints(mPoints);
		//durchf�hren  der Vermaschung
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
