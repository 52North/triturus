package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/** 
 * Klasse zur Verwaltung eines Gitters von Höhenpunkten. Das Gitter wird als achsenparallel vorausgesetzt. Ein Gitter
 * ist wahlweise Vertex-basiert ("Lattices") oder Zellen-basiert ("Grids").<p>
 * Die Gitterelemente müssen nicht notwendigerweise mit Höhenwerten belegt sein . Für jedes Element lässt sich ein
 * "no data"-Flag setzen.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmSimpleElevationGrid extends VgElevationGrid
{
    private GmSimple2dGridGeometry mGeom;
    private double[][] mVal;
    private boolean[][] mIsSetFl;
    private boolean mLatticeMode = false;
    private String mTheme = "Elevations";

    /** 
     * Konstruktor. Es wird ein Gitter erzeugt, in dem alle Punkte unbelegt sind.<p>
     * @param pCols Anzahl der Gitterelemente in x-Richtung (Spalten)
     * @param pRows Anzahl der Gitterelemente in y-Richtung (Zeilen)
     */
    public GmSimpleElevationGrid(int pCols, int pRows, VgPoint pOrigin, double pDeltaX, double pDeltaY)
    {
        mGeom = new GmSimple2dGridGeometry(pCols, pRows, pOrigin, pDeltaX, pDeltaY);
        mVal = new double[pRows][pCols]; 
        mIsSetFl = new boolean[pRows][pCols]; 
        for (int i = 0; i < pRows; i++) {
            for (int j = 0; j < pCols; j++) {
                mIsSetFl[i][j] = false;
            }
        }
        this.setName("unnamed elevation grid");
    }

    /** Konstruktor. Es wird ein Gitter erzeugt, in dem alle Punkte unbelegt sind.<p> */
    public GmSimpleElevationGrid(GmSimple2dGridGeometry pGeom) 
    {
        mGeom = pGeom;
        int nRows = mGeom.numberOfRows();
        int nCols = mGeom.numberOfColumns();
        mVal = new double[nRows][nCols]; 
        mIsSetFl = new boolean[nRows][nCols]; 
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                mIsSetFl[i][j] = false;
            }
        }
        this.setName("unnamed elevation grid");
    }

    /** 
     * liefert Metainformation über Thematik.<p>
     * @return Liste von Strings
     */
    public ArrayList getThematicAttributes() {
        ArrayList lList = new ArrayList();
        lList.add(mTheme);
        return lList;
    }
    
    /** 
     * liefert die Objekt-Geometrie.<p>
     * @return <tt>GmSimple2dGridGeometry</tt>-Objekt
     * @see org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das Elevation-Grid ein atomares Geoobjekt ist, liefert
     * diese Methode stets <i>false</i> als Ergebnis.<p>
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle.<p>
     * @param i (hier stets 0)
     * @return Elevation-Grid selbst
     * @throws org.n52.v3d.triturus.core.T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
        if (i != 0) 
        	throw new T3dException("Index out of bounds." ); 
        // else:
        return this;
    }

    public int numberOfSubFeatures() {
        return 1;
    }

    /** liefert die Anzahl der Punkte in Richtung der x-Achse (Spalten).<p> */
    public int numberOfColumns() {
        return mGeom.numberOfColumns();
    }
    
    /** liefert die Anzahl der Punkte in Richtung der y-Achse (Zeilen).<p> */
    public int numberOfRows() {
        return mGeom.numberOfRows();
    }
  
    /** liefert die Gitterweite in x-Richtung.<p> */
    public double getDeltaX() {
        return mGeom.getDeltaX();
    }

    /** liefert die Gitterweite in y-Richtung.<p> */
    public double getDeltaY() {
        return mGeom.getDeltaY();
    }

    /** setzt den Modus für eine Vertex-basierte Interpretation.<p> */
    public void setLatticeInterpretation() {
       mLatticeMode = true;
    }
    
    /** setzt den Modus für eine Zellen-basierte Interpretation.<p> */
    public void setGridInterpretation() {
       mLatticeMode = false;
    }

    /** 
     * setzt den Höhenwert <tt>pZ</tt> für den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>.
     * Wird eine der beiden Bedingungen
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 0 &lt;= pCol &lt; this.numberOfColumns()</i>
     * verletzt, wird eine <tt>T3dException</tt> geworfen.<p>
     * Nachbedingung: <tt>this.isSet(pRow, pCol) = true</tt><p>
     */
    public void setValue(int pRow, int pCol, double pZ) throws T3dException 
    {
        try {
            double zOld = mVal[pRow][pCol];
            mVal[pRow][pCol] = pZ;
            mIsSetFl[pRow][pCol] = true;
            this.updateZBounds(zOld, pZ);
        }
        catch (Exception e) {
            throw new T3dException("Could not set grid value (" + pRow + ", " + pCol + ").");
        }
    }

    /** 
     * liefert <i>true</i>, falls das Gitterelement mit einem Höhenwert belegt ist. Wird eine der beiden Bedingungen
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 0 &lt;= pCol &lt; this.numberOfColumns()</i> verletzt, wird
     * eine <tt>T3DException</tt> geworfen.<p>
     * @throws T3dException
     */
    public boolean isSet(int pRow, int pCol) throws T3dException
    {
        try {
            return mIsSetFl[pRow][pCol];
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }
    }

    /** 
     * liefert <i>true</i>, falls alle Gitterelemente mit einem Höhenwert belegt sind.<p> 
     */
    public boolean isSet()
    {
        for (int i = 0; i < this.numberOfRows(); i++) {
            for (int j = 0; j < this.numberOfColumns(); j++) {
                if (!mIsSetFl[i][j]) return false; 
            }
        }
        return true;
    }

    /** 
     * löscht die Belegung eines Gitterelementes. Wird eine der beiden Bedingungen
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 0 &lt;= pCol &lt; this.numberOfColumns()</i> verletzt, wird
     * eine <tt>T3DException</tt> geworfen.<p>
     * Nachbedingung: <tt>this.isIsSet(pRow, pCol) = false</tt><p>
     * @throws T3dException
     */
    public void unset(int pRow, int pCol) throws T3dException
    {
        try {
            mIsSetFl[pRow][pCol] = false;
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }
    }

    /** 
     * liefert den Höhenwert für den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>. Wird eine der beiden
     * Bedingungen <i>0 &lt;= pRow &lt; this.numberOfRows(), 0 &lt;= pCol &lt; this.numberOfColumns()</i>
     * verletzt oder ist das abgefragte Gitterelement nicht belegt, wird eine <tt>T3DException</tt> geworfen.<p>
     * Nachbedingung: <tt>this.isIsSet(pRow, pCol) = true</tt><p>
     * @throws T3dException
     */
    public double getValue(int pRow, int pCol) throws T3dException
    {
        try {
            if (mIsSetFl[pRow][pCol])
                return mVal[pRow][pCol];
            else
                throw new T3dException("Tried to access unset grid element.");
        }
        catch (Exception e) {
            throw new T3dException("Illegal grid element access. " + e.getMessage());
        }
    }

    /** 
     * liefert die Bounding-Box des Elevation-Grids. Die Ausdehnung ist dabei abhängig davon, ob eine Zellen- oder
     * Vertex-basierte Sicht erfolgt.<p>
     * @return 3D-Bounding-Box, im Fehlerfall <i>null</i>.
     * @see GmSimpleElevationGrid#setLatticeInterpretation
     * @see GmSimpleElevationGrid#setGridInterpretation
     */
    public VgEnvelope envelope()
    {
       VgEnvelope lEnv = this.getGeometry().envelope();
       double xMin = lEnv.getXMin();
       double xMax = lEnv.getXMax();
       double yMin = lEnv.getYMin();
       double yMax = lEnv.getYMax();

       if (!mLatticeMode) {
           double dx = 0.5 * this.getDeltaX();
           xMin -= dx; 
           xMax += dx;
           double dy = 0.5 * this.getDeltaY();
           yMin -= dy; 
           yMax += dy;
       }

       double zMin, zMax;
       try {
           zMin = this.minimalElevation();
           zMax = this.maximalElevation();
       }
       catch (T3dException e) {
           return null;
       }

       return new GmEnvelope(xMin, xMax, yMin, yMax, zMin, zMax);
    }

    /** 
     * liefert den kleinsten (niedrigsten) im Elevation-Grid enthaltenen Höhenwert.<p>
     * @throws T3dException
     */
    public double minimalElevation() throws T3dException
    {
        try {
            this.calculateZBounds();
        }
        catch (T3dException e) {
            throw e;
        }
        return mZMin;
    }

    /** 
     * liefert den größten (höchsten) im Elevation-Grid enthaltenen Höhenwert.<p>
     * @throws T3dException
     */
    public double maximalElevation() throws T3dException
    {
        try {
            this.calculateZBounds();
        }
        catch (T3dException e) {
            throw e;
        }
        return mZMax;
    }

    /**
     * deaktiviert "lazy evaluation" der Ausdehnung in z-Richtung.<p>
     * Eine explizite Deaktivierung unmittelbar vor dem Aufruf mehrerer <tt>setValue()</tt>-Aufrufe kann aus
     * Performanz-Gründen notwendig werden.<p>
     */
    public void setZBoundsInvalid() {
        mCalculated = false;
    }

    // private Helfer zur Berechnung der z-Bounds ("lazy evaluation"!):

    private boolean mCalculated = false;
    private double mZMin, mZMax;

    private void calculateZBounds() throws T3dException
    { 
        if (!mCalculated) {
            try {
                mZMin = this.getFirstSetZValue(); 
            }
            catch (T3dException e) {
                throw e;
            }
            mZMax = mZMin;
            double z;
            for (int i = 0; i < this.numberOfRows(); i++) {
                for (int j = 0; j < this.numberOfColumns(); j++) {
                   if (this.isSet(i, j)) {
                       z = this.getValue(i, j);
                       if (z < mZMin) mZMin = z; else {
                           if (z > mZMax) mZMax = z; }
                   }
                }
            }
            mCalculated = true;
        }
    }

    private void updateZBounds(double pZOld, double pZNew)     
    { 
        final double eps = 0.000001; // Epsilon für Gleichheitstest

        if (mCalculated) {
            if (pZNew <= mZMin) { mZMin = pZNew; return; }
            // assert: pZMin < pZNew
            if (pZNew >= mZMax) { mZMax = pZNew; return; }
            // assert: pZMin < pZNew < mZMax 
            if (Math.abs(pZOld - mZMin) < eps || Math.abs(pZOld - mZMax) < eps)
                mCalculated = false; // spätere Neuberechnung notwendig
        } // else:
            ; // skip
    }

    // Ermittlung des z-Wertes des "erstbesten" belegten Gitterelements:
    private double getFirstSetZValue() throws T3dException 
    {
        for (int i = 0; i < this.numberOfRows(); i++) {
            for (int j = 0; j < this.numberOfColumns(); j++) {
                if (this.isSet(i, j))
                    return this.getValue(i, j);
            }
        }
        // else:
        throw new T3dException("Tried to access empty elevation grid.");
    }

	/** 
	 * liefert die zugehörige "Footprint"-Geometrie.<p>
	 * @return "Footprint" als <tt>GmSimple2dGridGeometry</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return mGeom.footprint();
	}

	/**
     * liefert die Koordinaten eines Punktes im Elevation-Grid. Falls der Gitterpunkt unbelegt ist, wird <i>null</i>
     * zurückgegeben.<p>
	 */
	public VgPoint getPoint(int pRow, int pCol) {
		double x = mGeom.getOrigin().getX() + pCol * getDeltaX();
        double y = mGeom.getOrigin().getY() + pRow * getDeltaY();
        double z;
        if (! this.isSet(pRow, pCol))
            return null;
        z = this.getValue(pRow, pCol);
		return new GmPoint(x, y, z);
    }

    public String toString() {
        String strGeom = "<empty geometry>";
        if (mGeom != null)
            strGeom = mGeom.toString(); 
        return "[" + mTheme + ", \"" + strGeom + "\"]";
    }
}
