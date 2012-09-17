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

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/** 
 * This class allows to construct a grid model (&quot;gridding&quot;) for a collection of scattered (3-D) data points.
 * The result will be given as <tt>GmSimpleElevationGrid</tt>-object. Various sampling-methods can be used to determine
 * grid-vertices' z-values.
 * @author Benno Schmidt
 */
public class FltPointSet2ElevationGrid extends T3dProcFilter
{
    private String mLogString = "";

    private GmSimpleElevationGrid mGrid = null; // Zielgitter

    // Verfahrensparameter:
    /**
     * Constant for &quot;nearest neighbor&quot; method as gridding method
     */
    public final static short cNearestNeighbor = 1;
    /**
     * Constant for &quot;inverse distances&quot; method as gridding method
     */
    public final static short cInverseDist = 2;
    private int mInvDistExp = 2; // Exponent 2 als Default f�r Inverse Distanzen
    /**
     * Constant for triangular weight function method as gridding method
     */
    public final static short cTriangleFnc = 3;
    /**
     * Constant for Franke/Little weighting as gridding method
     */
    public final static short cFrankeLittle = 4;
    private final double cMaxWeight = 1.e10; // max. Wert f�r Franke/Little-Gewichtsfkt. 
    // Bem.: F�r einen Suchradius von 100 m und einem Abstand von 1 cm erg�be sich hierbei ein
    // Gewicht von ungef�hr 10.000, d. h. der Wert 1e10 m�sste eigentlich voll ausreichen...

    private short mWeightFnc = 1; // Nearest-Neighbor als Default

    private double mRadius; // Suchradius (in Geokoordinaten)

    // Der Wert in einem Gitterelement errechnet sich aus gewichteter Summe mSumN[i] der einen
    // Sample-Punkt umliegenden Punkte geteilt durch die Summe der Gewichte mSumZ[i]:
    private double mSumZ[];
    private double mSumN[];

    /**
     * Constructor. Target geometry and search-radius have to be given as input parameters. For this constructor, the
     * &quot;nearest neighbor&quot; will be used.<br /><br />
     * <i>German:</i> Konstruktor. Als Parameter sind die Zielgeometrie und der Suchradius anzugeben. Bei Verwendung
     * dieses Konstruktors wird das "N�chster-Nachbar"-Verfahren angewendet.
     * @param pGeom the target-grid's geometry
     * @param pRadius Search-radius (referring to the set coordinate reference system)
     * @see FltPointSet2ElevationGrid#cNearestNeighbor
     */
    public FltPointSet2ElevationGrid(GmSimple2dGridGeometry pGeom, double pRadius) 
    {
        this.init(pGeom, cNearestNeighbor, pRadius);
    }

    /** 
     * Constructor. Search-radius and the weight-function used for the gridding process have to be given as input
     * parameters. The following funtions are suppoerted:<br />
     * <table border="1">
     * <tr><td><i>pWeightFnc</i></td><td>Weight function</td></tr>
     * <tr><td><tt>this.cNearestNeighbor</tt></td><td>next neighbor gets highest weight</td></tr>
     * <tr><td><tt>this.cInverseDist</tt></td><td>Inverse distances (selectable exponent)</td></tr>
     * <tr><td><tt>this.cTriangleFnc</tt></td><td>Triangle function<td></td></tr>
     * <tr><td><tt>this.cFrankeLittle</tt></td><td>Franke/Little-Weighting<td></td></tr>
     * </table><p>
     * @param pGeom the target-grid's geometry
     * @param pWeightFnc Constant determining the weight function
     * @param pRadius Search-radius (referring to the set coordinate reference system)
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
     * sets the exponent to be used for the inverse distance weight-function (if this method is to be used).
     * @param pExp Exponent (default-value is 2)
     */
    public void setInverseDistanceExponent(int pExp) {
        mInvDistExp = pExp;
    }

    /**
     * sets the target-grid's geometry.
     * @param pGrid Elevation-grid
     */
    public void setGridGeometry(GmSimpleElevationGrid pGrid) {
        mGrid = pGrid;
    }

    /**
     * sets the search-radius.
     * @param pRadius Search-radius
     */
    public void setSearchRadius(double pRadius) {
        mRadius = pRadius;
    }

    public String log() {
        return mLogString;
    }

    /**
     * estimates the amount of memory needed to perform the gridding process (for test-purposes only). Before starting
     * the gridding operation by a call to <tt>this.transform()</tt> the amount of memory (usually, in heap-space)
     * given in Bytes will estimated.
     * @return gesch�tzter Memory-demand in Bytes
     */
    public int estimateMemoryConsumption() {
    	return 2 * mNX * mNY * /* sizeof(double) = */ 8; // stimmt das?
    }

    /**
     * determines the number of grid-points inside the search-radius with the respect to the target grid.<br /><br />
     * <i>German:</i> F&uuml;r das Funktionieren des durch die Methode <tt>this.transform()</tt> durchgef&uuml;hrten
     * &quot;Griddings&quot; ist diese Funktion unerheblich; vielmehr kann sie zur Absch&auml;tzung des erforderlichen
     * Rechenaufwandes dienlich sein.
     * @return Number of points to be sampled inside the search-circles
     */
    public int numberOfPointsInSearchCircle() 
    {
       int ct = 1; // Gitterpkt. selbst

       // zum Suchkreis geh�riges "Rechteck":
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
     * performs the grid calculation.
     * @param pPointSet List of <tt>VgPoint</tt>-objects
     * @throws T3dException
     */
    public GmSimpleElevationGrid transform(ArrayList pPointSet) throws T3dException
    {   	
        if (mGrid == null)
            throw new T3dException("Destination grid-geometry is missing.");

        mSumZ = new double[mNX * mNY];
        mSumN = new double[mNX * mNY];
                 
        // In mSumZ[] wird im Weiteren die Summe der gewichteten z-Werte zwischengespeichert, in mSumN[] die
        // Summe der Gewichte. Der Interpolationswert ergibt sich dann sp�ter aus mSumZ[i]/mSumN[i], falls
        // mWeightFnc = cInverseDist oder = cFrankeLittle. F�r mWeightFnc = cNearestNeighbor erfolgt die Berechnung
        // anders: sSumN[i] wird als Merker f�r den Abstand des Punktes missbraucht, dessen z-Wert in mSumZ[i]
        // abgelegt ist.
   
        if (mWeightFnc != cNearestNeighbor) {
            for (int i = 0; i < mNX * mNY; i++) {
                mSumZ[i] = 0.;
                mSumN[i] = 0.;
            }
        } 
        else {
            for (int i = 0; i < mNX * mNY; i++) 
                mSumN[i] = -1.; // soll hei�en: noch keinen Abstand gemerkt
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

    // Verarbeitung aller Punkte der �bergebenen Liste und Belegung vom mSumZ[] und mSumN[]:
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

        for (int i = 0; i < pPointSet.size(); i++) // f�r alle Punkte der Liste
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
