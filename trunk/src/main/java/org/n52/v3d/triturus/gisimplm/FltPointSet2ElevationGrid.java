package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/** 
 * Diese Klasse ermöglicht die Berechnung eines Gittermodells ("Gridding") für eine Menge verstreut liegender (3D-)
 * Datenpunkte. Resultat ist ein <tt>GmSimpleElevationGrid</tt>-Objekt. Zur Berechnung der z-Werte in den Gitterpunkten
 * kann zwischen verschiedenen Sampling-Verfahren gewählt werden.<p>
 * @author Benno Schmidt<br>
 * (c) 1992-1996, Geopro GmbH<br>
 */
public class FltPointSet2ElevationGrid extends T3dProcFilter
{
    private String mLogString = "";

    private GmSimpleElevationGrid mGrid = null; // Zielgitter

    // Verfahrensparameter:
    /** Konstante für "Nächster-Nachbar"-Verfahren. */
    public final static short cNearestNeighbor = 1;
    /** Konstante für Inverse-Distanzen-Verfahren. */
    public final static short cInverseDist = 2;
    private int mInvDistExp = 2; // Exponent 2 als Default für Inverse Distanzen
    /** Konstante für Dreiecksfunktion als Sampling-Verfahren. */
    public final static short cTriangleFnc = 3;
    /** Konstante für Franke/Little-Gewichtung als Sampling-Verfahren. */
    public final static short cFrankeLittle = 4;
    private final double cMaxWeight = 1.e10; // max. Wert für Franke/Little-Gewichtsfkt. 
    // Bem.: Für einen Suchradius von 100 m und einem Abstand von 1 cm ergäbe sich hierbei ein
    // Gewicht von ungefähr 10.000, d. h. der Wert 1e10 müsste eigentlich voll ausreichen...

    private short mWeightFnc = 1; // Nearest-Neighbor als Default

    private double mRadius; // Suchradius (in Geokoordinaten)

    // Der Wert in einem Gitterelement errechnet sich aus gewichteter Summe mSumN[i] der einen
    // Sample-Punkt umliegenden Punkte geteilt durch die Summe der Gewichte mSumZ[i]:
    private double mSumZ[];
    private double mSumN[];

    /**
     * Konstruktor. Als Parameter sind die Zielgeometrie und der Suchradius anzugeben. Bei Verwendung dieses
     * Konstruktors wird das "Nächster-Nachbar"-Verfahren angewendet.<p>
     * @param pGeom Geometrie des Zielgitters
     * @param pRadius Suchradius (bezogen auf verwendetes räumliches Referenzsystem)
     */
    public FltPointSet2ElevationGrid(GmSimple2dGridGeometry pGeom, double pRadius) 
    {
        this.init(pGeom, this.cNearestNeighbor, pRadius);
    }

    /** 
     * Konstruktor. Als Parameter sind der Suchradius und die für das "Gridding" die zu verwendende Gewichtsfunktion
     * anzugeben. Unterstützt werden die folgenden Funktionen:<p>
     * <table border="1">
     * <tr><td><i>pWeightFnc</i></td><td>Gewichtsfunktion</td></tr>
     * <tr><td><tt>this.cNearestNeighbor</tt></td><td>nächster Nachbar erhält größtes Gewicht</td></tr>
     * <tr><td><tt>this.cInverseDist</tt></td><td>Inverse Distanzen (Exponent frei wählbar)</td></tr>
     * <tr><td><tt>this.cTriangleFnc</tt></td><td>Dreiecksfunktion<td></td></tr>
     * <tr><td><tt>this.cFrankeLittle</tt></td><td>Franke/Little-Gewichtung<td></td></tr>
     * </table><p>
     * @param pGeom Geometrie des Zielgitters
     * @param pWeightFnc Konstante für Gewichtsfunktion
     * @param pRadius Suchradius
     */
    public FltPointSet2ElevationGrid(GmSimple2dGridGeometry pGeom, short pWeightFnc, double pRadius)
    {
        this.init(pGeom, pWeightFnc, pRadius);
    }

    private void init(GmSimple2dGridGeometry pGeom, short pWeightFnc, double pRadius)
    {
        mLogString = this.getClass().getName();
        mGrid = new GmSimpleElevationGrid(pGeom);
        this.setEnvelopeInfo(pGeom);
        mRadius = pRadius;
        mWeightFnc = pWeightFnc;
    }

    /**
     * setzt den Exponenten für die Inverse-Distanzen-Gewichtsfunktion (insofern diese verwendet wird).<p>
     * @param pExp Exponent (Default-mäßig auf 2 gesetzt)
     */
    public void setInverseDistanceExponent(int pExp) {
        mInvDistExp = pExp;
    }

    /**
     * setzt die Zielgitter-Geometrie.<p>
     * @param pGrid Höhengitter
     */
    public void setGridGeometry(GmSimpleElevationGrid pGrid) {
        mGrid = pGrid;
    }

    /**
     * setzt den Suchradius.<p>
     * @param pRadius Suchradius
     */
    public void setSearchRadius(double pRadius) {
        mRadius = pRadius;
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /**
     * liefert den benötigten Speicherplatz-Bedarf (nur für Testzwecke). Unmittelbar vor Durchführung des "Griddings"
     * durch die Methode <tt>this.transform()</tt> wird eine Schätzung des benötigten Speichers (auf dem Heap) in Byte
     * ermittelt.<p>
     * @return geschätzter Speicherplatz-Bedarf in Byte
     */
    public int estimateMemoryConsumption() {
    	return 2 * mNX * mNY * /* sizeof(double) = */ 8; // stimmt das?
    }

    /**
     * ermittelt die Anzahl der innerhalb des Suchkreises liegenden Gitterpunkte bezogen auf das Ziel-Gitter. Für das
     * Funktionieren des durch die Methode <tt>this.transform()</tt> durchgeführten "Griddings" ist diese Funktion
     * unerheblich; vielmehr kann sie zur Abschätzung des erforderlichen Rechenaufwandes dienlich sein.<p>
     * @return Anzahl der zu samplenden Punkte innerhalb der festgelegten Suchkreise
     */
    public int numberOfPointsInSearchCircle() 
    {
       int ct = 1; // Gitterpkt. selbst

       // zum Suchkreis gehöriges "Rechteck":
       int ctX = (int)(mRadius / mDX);
       int ctY = (int)(mRadius / mDY);
       
       ct += ctX * 2 + ctY * 2; // + Pkt. auf Achsen

       double s1, s2;
   
       for (int j = 1; j < ctX; j++) {
           for (int i = 1; i < ctY; i++) 
           {
               s1 = (double)j * mDX;
               s2 = (double)i * mDY;
               if (s1 * s1 + s2 * s2 < mRadius * mRadius)
                   ct += 4; // + Pkt. in Quadranten
           }
       }
       return ct;
    }   

    /** 
     * führt die Gitter-Berechnung durch.<p>
     * @param pPointSet Liste von <tt>VgPoint</tt>-Objekten
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public GmSimpleElevationGrid transform(ArrayList pPointSet) throws T3dException
    {   	
        if (mGrid == null)
            throw new T3dException("Destination grid-geometry is missing.");

        mSumZ = new double[mNX * mNY];
        mSumN = new double[mNX * mNY];
                 
        // In mSumZ[] wird im Weiteren die Summe der gewichteten z-Werte zwischengespeichert, in mSumN[] die
        // Summe der Gewichte. Der Interpolationswert ergibt sich dann später aus mSumZ[i]/mSumN[i], falls
        // mWeightFnc = cInverseDist oder = cFrankeLittle. Für mWeightFnc = cNearestNeighbor erfolgt die Berechnung
        // anders: sSumN[i] wird als Merker für den Abstand des Punktes missbraucht, dessen z-Wert in mSumZ[i]
        // abgelegt ist.
   
        if (mWeightFnc != this.cNearestNeighbor) {
            for (int i = 0; i < mNX * mNY; i++) {
                mSumZ[i] = 0.;
                mSumN[i] = 0.;
            }
        } 
        else {
            for (int i = 0; i < mNX * mNY; i++) 
                mSumN[i] = -1.; // soll heißen: noch keinen Abstand gemerkt
        }

        try {
            this.processPoints(pPointSet);
            this.putToElevationGrid();
        }
        catch (T3dException e) {
            throw e;
        }

        return mGrid;
    }

    // Berechnung des Abstands der Position (is, js) (in Gitterkoordinaten) vom Gitterpkt. (i,j):
    private double iDistance(int i, int j, double is, double js)
    {
        double k1 = Math.abs(is - (double)i) * mDX;
        double k2 = Math.abs(js - (double)j) * mDY;
        return Math.sqrt(k1 * k1 + k2 * k2);
    }

    // Verarbeitung aller Punkte der übergebenen Liste und Belegung vom mSumZ[] und mSumN[]:
    private void processPoints(ArrayList pPointSet)
    {
        // Suchradius in Matrixkoord.:
        int radIdxX = (int)(Math.floor(mRadius / mDX)) + 1;
        int radIdxY = (int)(Math.floor(mRadius / mDY)) + 1;

        // Hilfsvar.
        double faktorX = ((double)mNX - 1.) / (mXMax - mXMin);
        double faktorY = ((double)mNY - 1.) / (mYMax - mYMin);

        int ii, jj, index;
        double x, y, z;
        double is, js;
        double r, weight = 0.;
        VgPoint pnt;

        for (int i = 0; i < pPointSet.size(); i++) // für alle Punkte der Liste
        {
            pnt = ((VgPoint) pPointSet.get(i));
            x = pnt.getX();
            y = pnt.getY();
            z = pnt.getZ();
      
            // (reelle) Indizes im Gitter:
            js = faktorX * (x - mXMin);
            is = faktorY * (y - mYMin);

            for (jj = (((int)Math.floor(js)) - radIdxX - 1);  // -1 nur zur Sicherheit
                 jj <= ((int)Math.floor(js)) + radIdxX + 1;
                 jj++)
            {
                for (ii = (((int)Math.floor(is)) - radIdxY - 1); 
                     ii <= ((int)Math.floor(is)) + radIdxY + 1; 
                     ii++)
                {
                    if (jj >= 0 && jj <= mNX - 1 && ii >= 0 && ii <= mNY - 1) 
                    {
                        r = iDistance(jj, ii, js, is);

                        if (r <= mRadius)
                        {
                            index = jj * mNY + ii;
                  
                            switch (mWeightFnc) 
                            {
                                case cTriangleFnc:
                                    weight = 1. - r / mRadius;
                                    break;
                    
                                case cInverseDist:
                                    if (r > Math.pow(cMaxWeight, -1. / (double)mInvDistExp))
                                        weight = 1. / Math.pow(r, (double)mInvDistExp);
                                    else
                                        weight = cMaxWeight;
                                    break;

                                case cFrankeLittle:
                                    if (mRadius - r <= Math.sqrt( cMaxWeight ) * mRadius * r) {
                                        weight = (mRadius - r) / (mRadius * r);
                                        weight = weight * weight;
                                    } else
                                        weight = cMaxWeight;                        
                                    break;
                     
                                case cNearestNeighbor:        
                                    if (mSumN[index] < 0. || (mSumN[index] >= 0. && r < mSumN[index])) {
                                        mSumZ[index] = z;
                                        mSumN[index] = r;
                                    }
                                    break;
   
                                default: throw new T3dException("Unexpected error.");
                            }

                            if (mWeightFnc != cNearestNeighbor) {
                                mSumZ[index] += weight * z;
                                mSumN[index] += weight;
                            }
                        }
                    }
                } // for ii (Zeilen)
            } // for jj (Spalten)
        } // for i (alle Punkte)
    } // processPoints()

    // Belegen des Zielgitters:
    private void putToElevationGrid() 
    {
        int index;

        for (int j = 0; j < mNX; j++) {
            for (int i = 0; i < mNY; i++) {
                index = j * mNY + i;
                
                if (mWeightFnc != cNearestNeighbor) {
                    if (mSumN[index] == 0.)
                        ; // Punkt bleibt unbelegt
                    else 
                        mGrid.setValue(i, j, mSumZ[index] / mSumN[index]);
                }
                else {
                    if (mSumN[index] < 0.)
                        ; // Punkt bleibt unbelegt
                    else 
                        mGrid.setValue(i, j, mSumZ[index]);
                }
            }
        }
    } // putToElevationGrid()

    // Weitere private Helfer:

    private int mNX, mNY;
    private double mDX, mDY;
    private double mXMin, mXMax, mYMin, mYMax;
    
    private void setEnvelopeInfo(GmSimple2dGridGeometry pGeom) 
    {
    	mNX = pGeom.numberOfColumns();
    	mNY = pGeom.numberOfRows();
    	mDX = pGeom.getDeltaX();
    	mDY = pGeom.getDeltaY();
    	mXMin = pGeom.envelope().getXMin();
    	mXMax = pGeom.envelope().getXMax();
    	mYMin = pGeom.envelope().getYMin();
    	mYMax = pGeom.envelope().getYMax();
    }
}
