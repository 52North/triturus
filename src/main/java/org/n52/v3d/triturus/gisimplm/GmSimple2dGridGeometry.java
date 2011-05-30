package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgEquidistGrid;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

import java.lang.Double;

/** 
 * Klasse zur Verwaltung einer einfachen äquidistanten, achsenparallelen Gitter-Geometrie.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmSimple2dGridGeometry extends VgEquidistGrid
{
    private int mCols, mRows;
    private VgPoint mOrigin;
    private double mDeltaX, mDeltaY;

    /** 
     * Konstruktor.<p>
     * @param pCols Anzahl der Gitterspalten (x-Richtung)
     * @param pRows Anzahl der Gitterzeilen (y-Richtung)
     */
    public GmSimple2dGridGeometry(int pCols, int pRows, VgPoint pOrigin, double pDeltaX, double pDeltaY) 
    {
       mRows = pRows;
       mCols = pCols;

       mOrigin = pOrigin;

       mDeltaX = pDeltaX;
       mDeltaY = pDeltaY;
    }

    /** 
     * liefert die Bounding-Box der Gitter-Geometrie.<p>
     * @return Bounding-Box (in xy-Ebene; "Lattice"-Sicht)
     */
    public VgEnvelope envelope()
    {
       double xMin = mOrigin.getX();
       double xMax = mOrigin.getX() + (double)(mCols - 1) * mDeltaX;
       double yMin = mOrigin.getY();
       double yMax = mOrigin.getY() + (double)(mRows - 1) * mDeltaY;

       if (xMin > xMax) { double x = xMin; xMin = xMax; xMax = x; }
       if (yMin > yMax) { double y = yMin; yMin = yMax; yMax = y; }

       return new GmEnvelope(xMin, xMax, yMin, yMax, 0., 0.);
    }

    /** 
     * liefert die Anzahl der Punkte in Richtung der x-Achse (Spalten).<p>
     * @return Spaltenanzahl
     */
    public int numberOfColumns() {
        return mCols;
    }
    
    /** 
     * liefert die Anzahl der Punkte in Richtung der y-Achse (Zeilen).<p>
     * @return Zeilenanzahl
     */
    public int numberOfRows() {
        return mRows;
    }

    /**
     * liefert den Vektor, der die erste Achse des Gitters (Zeilenrichtung) aufspannt.<p>
     * @return Richtungsvektor
     */
    public T3dVector getDirectionRows() {
        return new T3dVector(0., 1., 0.);
    }
    
    /** 
     * liefert den Vektor, der die zweite Achse des Gitters (Spaltenrichtung) aufspannt.<p>
     * @return Richtungsvektor
     */
    public T3dVector getDirectionColumns() {
        return new T3dVector(1., 0., 0.);
    }

    /** 
     * setzt den Ursprungspunkt des Gitters.<p>
     * @param pPnt Gitterursprung
     */
    public void setOrigin(VgPoint pPnt) {
        mOrigin = pPnt;
    }
    
    /** 
     * liefert den Ursprungspunkt des Gitters.<p>
     * @return Gitterursprung
     */
    public VgPoint getOrigin() {
        return mOrigin;
    }

    /** 
     * liefert die Gitterweite in x-Richtung (entlang der Gitter-Spalten).<p>
     * @return Gitterweite
     */
    public double getDeltaX() {
        return mDeltaX;
    }

    /**
     * @deprecated
     */
    public double getCellSizeColumns() {
        return this.getDeltaX();
    }

    /** 
     * liefert die Gitterweite in y-Richtung (entlang der Gitter-Zeilen).<p>
     * @return Gitterweite
     */
    public double getDeltaY() {
        return mDeltaY;
    }
    
    /**
     * @deprecated
     */
    public double getCellSizeRows() {
        return this.getDeltaY();
    }

    /** 
      * @deprecated
      * @see VgEquidistGrid#getCellSizeRows
      * @see VgEquidistGrid#getCellSizeColumns
      * liefert die Gitterweiten für die Achsen in <tt>pDeltaRows</tt> und <tt>pDeltaColumns</tt>.<p> 
      * Bem.: Die Reihenfolge der Ausgabe-Parameter ist zu beachten!<p>
      * @param pDeltaRows Gitterweite in Richtung der 1. Achse (Zeilen)
      * @param pDeltaColumns Gitterweite in Richtung der 2. Achse (Spalten)
      */
    public void getDelta(Double pDeltaRows, Double pDeltaColumns) {
        pDeltaRows = new Double(this.getCellSizeRows());
        pDeltaColumns = new Double(this.getCellSizeColumns());
    }	

    /** 
     * liefert die Koordinate für das Gitterelement mit den angegebenen Indizes.<p>
     * Es sind die Bedingungen <i>0 &lt;= i &lt; this.numberOfRows(), 0 &lt;= j &lt; this.numberOfColumns()</i>
     * einzuhalten; anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
     * @param i Index der Gitterzeile
     * @param j Index der Gitterspalte
     * @return Vertex mit x- und y-Koordinate (z undefiniert)
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public VgPoint getVertexCoordinate(int i, int j) throws T3dException
    {
        if (i < 0 || i >= mRows || j < 0 || j >= mCols) 
            throw new T3dException("Index out of bounds.");
        
        double x = mOrigin.getX() + ((double) j) * mDeltaX;
        double y = mOrigin.getY() + ((double) i) * mDeltaY;
        return new GmPoint(x, y, 0.);
    }
    
    public VgGeomObject footprint() {
        return this;
    }
}
