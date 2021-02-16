/**
 * Copyright (C) 2007-2016 52North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.List;

/** 
 * Filter class to construct a grid model (&quot;gridding&quot;) from a 
 * collection of scattered (3-D) data points. The result will be given as 
 * {@link GmSimpleElevationGrid}-object. Various sampling-methods are available
 * to determine the grid-vertices' <i>z</i>-values:<br/>
 * <br/>
 * <table border="1">
 *   <tr>
 *     <th><tt>pWeightFnc</tt></th>
 *     <th>Weight function</th>
 *   </tr>
 *   <tr>
 *      <td><tt>this.cNearestNeighbor</tt></td>
 *      <td>next neighbor gets highest weight</td>
 *    </tr>
 *   <tr>
 *     <td><tt>this.cInverseDist</tt></td>
 *     <td>Inverse distances (selectable exponent)</td>
 *   </tr>
 *   <tr>
 *     <td><tt>this.cTriangleFnc</tt></td>
 *     <td>Triangle function</td>
 *   </tr>
 *   <tr>
 *     <td><tt>this.cFrankeLittle</tt></td>
 *     <td>Franke/Little-Weighting</td>
 *   </tr>
 * </table>
 * 
 * @author Benno Schmidt
 */
public class FltPointSet2ElevationGrid extends T3dProcFilter
{
    private String mLogString = "";

    private GmSimpleElevationGrid mGrid = null; // Destination grid

    /**
     * Identifier for &quot;nearest neighbor&quot; method as gridding method.
     */
    public final static short cNearestNeighbor = 1;

    /**
     * Identifier for &quot;inverse distances&quot; method as gridding method.
     */
    public final static short cInverseDist = 2;
    private int mInvDistExp = 2; // Exponent 2 as default for inverse distance method

    /**
     * Identifier for triangular weight function method as gridding method.
     */
    public final static short cTriangleFnc = 3;

    /**
     * Identifier for Franke/Little weighting as gridding method.
     */
    public final static short cFrankeLittle = 4;
    private final double cMaxWeight = 1.e10; // max. value for Franke/Little weight function. 
    // Note: For a search-radius of 100 m and a distance of 1 cm this would result in a 
    // weight of approx. 10.000; this the proposed value of 1e10 should do...

    private short mWeightFnc = 1; // Nearest-Neighbor as default

    private double mRadius; // search radius (given in geo-coordinates)

    // The value of a grid-cell/vertex will be given by the weighted sum of the values 
    // of the points inside the search-circle (mSumN[i]) divided by the sum of the 
    // weights (mSumZ[i]):
    private double mSumZ[];
    private double mSumN[];

    
    /**
     * Constructor. Target geometry and search radius have to be given as input 
     * parameters. For this constructor, the &quot;nearest neighbor&quot; will be used.
     * 
     * @param geom the target grid's geometry
     * @param radius Search radius (referring to the set coordinate reference system)
     * @see FltPointSet2ElevationGrid#cNearestNeighbor
     */
    public FltPointSet2ElevationGrid(GmSimple2dGridGeometry geom, double radius) 
    {
        this.init(geom, cNearestNeighbor, radius);
    }

    /** 
     * Constructor. Search-radius and the weight-function used for the gridding 
     * process have to be given as input parameters. 
     *  
     * @param geom the target grid's geometry
     * @param weightFnc Constant determining the weight function
     * @param radius Search radius (referring to the set coordinate reference system)
     */
    public FltPointSet2ElevationGrid(
            GmSimple2dGridGeometry geom, 
            short weightFnc, 
            double radius)
    {
        this.init(geom, weightFnc, radius);
    }

    private void init(GmSimple2dGridGeometry geom, short weightFnc, double radius)
    {
        mLogString = this.getClass().getName();
        
        mGrid = new GmSimpleElevationGrid(geom);
        this.setEnvelopeInfo(geom);
        mRadius = radius;
        mWeightFnc = weightFnc;
    }

    /**
     * sets the exponent to be used for the inverse distance weight-function 
     * (if this method is to be used).
     * 
     * @param exp Exponent (default-value is 2)
     */
    public void setInverseDistanceExponent(int exp) {
        mInvDistExp = exp;
    }

    /**
     * sets the target grid's geometry.
     * 
     * @param grid Elevation-grid
     */
    public void setGridGeometry(GmSimpleElevationGrid grid) {
        mGrid = grid;
    }

    /**
     * sets the search radius.
     * 
     * @param radius Search radius
     */
    public void setSearchRadius(double radius) {
        mRadius = radius;
    }

    public String log() {
        return mLogString;
    }

    /**
     * estimates the amount of memory needed to perform the gridding process 
     * (for test-purposes only). Before starting the gridding operation by 
     * using to <tt>this.transform()</tt>, the amount of memory (heap space) 
     * given in Bytes will be estimated.
     * 
     * @return Estimated Memory-demand in Bytes
     */
    public int estimateMemoryConsumption() {
        return 2 * mNX * mNY * /* sizeof(double) = */ 8; // stimmt das?
    }

    /**
     * determines the number of grid points inside the search radius with 
     * respect to the target grid. This value might be useful to estimate the 
     * resulting calculation efforts.
     * 
     * @return Number of points to be sampled inside the search circles
     */
    public int numberOfPointsInSearchCircle() 
    {
       int ct = 1; // Grid-point itself

       // "Rectangle" corresponding to search circle:
       int ctX = (int)(mRadius / mDX);
       int ctY = (int)(mRadius / mDY);
       
       ct += ctX * 2 + ctY * 2; // + points on axes

       double s1, s2;
   
       for (int j = 1; j < ctX; j++) {
           for (int i = 1; i < ctY; i++) 
           {
               s1 = (double)j * mDX;
               s2 = (double)i * mDY;
               if (s1 * s1 + s2 * s2 < mRadius * mRadius)
                   ct += 4; // + points in quadrants
           }
       }
       return ct;
    }   

    /** 
     * performs the grid calculation.
     * 
     * @param pointSet List of <tt>VgPoint</tt>-objects
     * @throws T3dException
     */
    public GmSimpleElevationGrid transform(List<VgPoint> pointSet) throws T3dException
    {       
        if (mGrid == null) {
            throw new T3dException("Destination grid geometry is missing.");
        }
        
        mSumZ = new double[mNX * mNY];
        mSumN = new double[mNX * mNY];
                 
        // In the following, in mSumZ[] will hold the sum of weighted z-values. 
        // mSumN[] will be used to hold the sum of weights. Later, the inter-
        // polation value will be given by mSumZ[i]/mSumN[i], if mWeightFnc = 
        // cInverseDist or = cFrankeLittle. For mWeightFnc = cNearestNeighbor, 
        // the way of computation will be different: sSumN[i] will be used to 
        // store the distance of the point thats z-value is held in mSumZ[i].
   
        if (mWeightFnc != cNearestNeighbor) {
            for (int i = 0; i < mNX * mNY; i++) {
                mSumZ[i] = 0.;
                mSumN[i] = 0.;
            }
        } 
        else {
            for (int i = 0; i < mNX * mNY; i++) 
                mSumN[i] = -1.; // will have the meaning: "did not store a distance yet"
        }

        try {
            this.processPoints(pointSet);
            this.putToElevationGrid();
        }
        catch (T3dException e) {
            throw e;
        }

        return mGrid;
    }

    // Computation of the distance of the position (is, js) (referring to 
    // grid coordinates) from the grid's point (i,j):
    private double iDistance(int i, int j, double is, double js)
    {
        double k1 = Math.abs(is - (double)i) * mDX;
        double k2 = Math.abs(js - (double)j) * mDY;
        return Math.sqrt(k1 * k1 + k2 * k2);
    }

    // Processing of all points of the given point-list and assignment of 
    // values to mSumZ[] and mSumN[]:
    private void processPoints(List<VgPoint> pPointSet)
    {
        // Search radius referring to matrix coordinates:
        int radIdxX = (int)(Math.floor(mRadius / mDX)) + 1;
        int radIdxY = (int)(Math.floor(mRadius / mDY)) + 1;

        // Helpers:
        double faktorX = ((double) mNX - 1.) / (mXMax - mXMin);
        double faktorY = ((double) mNY - 1.) / (mYMax - mYMin);

        int ii, jj, index;
        double x, y, z;
        double is, js;
        double r, weight = 0.;
        VgPoint pnt;

        for (int i = 0; i < pPointSet.size(); i++) // for all points in the list
        {
            pnt = ((VgPoint) pPointSet.get(i));
            x = pnt.getX();
            y = pnt.getY();
            z = pnt.getZ();
      
            // (real) grid-indices:
            js = faktorX * (x - mXMin);
            is = faktorY * (y - mYMin);

            for (jj = (((int)Math.floor(js)) - radIdxX - 1);  // -1 just to be sure
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
                                    if (mRadius - r <= Math.sqrt(cMaxWeight) * mRadius * r) {
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
                } // for ii (rows)
            } // for jj (columns)
        } // for i (all points)
    } // processPoints()

    // Assignment of values to the target grid:
    private void putToElevationGrid() 
    {
        int index;

        for (int j = 0; j < mNX; j++) {
            for (int i = 0; i < mNY; i++) {
                index = j * mNY + i;
                
                if (mWeightFnc != cNearestNeighbor) {
                    if (mSumN[index] == 0.) {
                        ; // point will remain without value
                    } else {
                        mGrid.setValue(i, j, mSumZ[index] / mSumN[index]);
                    }
                }
                else {
                    if (mSumN[index] < 0.) {
                        ; // point will remain without value
                    } else {
                        mGrid.setValue(i, j, mSumZ[index]);
                    }
                }
            }
        }
    } // putToElevationGrid()

    // More private helpers:

    private int mNX, mNY;
    private double mDX, mDY;
    private double mXMin, mXMax, mYMin, mYMax;
    
    private void setEnvelopeInfo(GmSimple2dGridGeometry geom) 
    {
        mNX = geom.numberOfColumns();
        mNY = geom.numberOfRows();
        mDX = geom.getDeltaX();
        mDY = geom.getDeltaY();
        mXMin = geom.envelope().getXMin();
        mXMax = geom.envelope().getXMax();
        mYMin = geom.envelope().getYMin();
        mYMax = geom.envelope().getYMax();
    }
}
