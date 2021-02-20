/**
 * Copyright (C) 2021 52North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 
 * Filter class to construct a grid model from a collection of scattered 
 * (3-D) data with help of the {@link FltPointSet2ElevationGrid} filter and 
 * additional 2-D grids trying to give meta-information about the accuracy and
 * expressiveness of the interpolated elevation values (<i>z</i>-values).<br/>
 * <br/>
 * Currently, the following attributes are available:<br/>
 * <table border="1">
 *   <tr>
 *     <th>Attribute</th><th>Description</th><th>Value range</th> 
 *   </tr>
 *   <tr>
 *     <td>MIN_Z</td><td>Minimum <i>z</i>-values</td><td>(Elevation values)</td> 
 *   </tr>
 *   <tr>
 *     <td>MAX_Z</td><td>Maximum <i>z</i>-values</td><td>(Elevation values)</td> 
 *   </tr>
 *   <tr>
 *     <td>DELTA_Z</td><td>Difference MAX_Z - MIN_Z</td><td>Elevation difference &gt;= 0</td> 
 *   </tr>
 *   <tr>
 *     <td>HIT_COUNT</td>
 *     <td>
 *       Number of source point inside a cell of the target grid.
 *     </td>
 *     <td>
 *       Number of hits &gt;= 0
 *     </td> 
 *   </tr>
 *   <tr>
 *     <td>CLUSTERS</td>
 *     <td>
 *       Number of peaks inside a class of the histogram for the <i>z</i>-value
 *       distribution per target grid cell. <i>(For experimental purposes only. 
 *       For details, see source-code.)</i> 
 *     </td>
 *     <td>
 *       Number of peaks &gt;= 0
 *     </td> 
 *   </tr>
 *   <tr>
 *     <td>NOISE</td>
 *     <td>
 *       Measure for the "noisiness" of the distribution of <i>z</i>-values of 
 *       a target grid cell. <i>(For experimental purposes only. For details, 
 *       see source-code.)</i> 
 *     </td>
 *     <td>
 *       Floating-point number between 0 ("focused") and 1 ("evenly 
 *       distributed").
 *     </td> 
 *   </tr>
 * </table>
 * 
 * @author Benno Schmidt
 */
public class FltPointSet2InterpolationQualityIndicators 
    extends FltPointSet2ElevationGrid
{
    private String mLogString = "";

    static final public String 
        MIN_Z = "MIN_Z",
        MAX_Z = "MAX_Z",
        DELTA_Z = "DELTA_Z",
        HIT_COUNT = "HIT_COUNT",
        CLUSTERS = "CLUSTERS",
        NOISE = "NOISE";

    GmSimpleElevationGrid 
        mGridMIN_Z, 
        mGridMAX_Z, 
        mGridDELTA_Z; 
    private GmSimpleFloatGrid 
        mGridHIT_COUNT, // number of source points inside a target grid cell
        mGridCLUSTERS,
        mGridNOISE;

    // The value of a grid-cell/vertex will be given by the weighted sum of the values 
    // of the points inside the search-circle (mSumN[i]) divided by the sum of the 
    // weights (mSumZ[i]):
    private 
        @SuppressWarnings("rawtypes")
        List[][] mFoundPoints;
        @SuppressWarnings("rawtypes")
        List[][] mFoundElevations;
    
    private GmSimple2dGridGeometry mGridGeom;
    
    public FltPointSet2InterpolationQualityIndicators(
        GmSimple2dGridGeometry geom, 
        short weightFnc, 
        double radius) 
    {
        super(geom, weightFnc, radius);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * constructs an elevation-grid by calling the {@link FltPointSet2ElevationGrid}'s
     * <<tt>transform</tt> method. After finishing this, additional meta-information 
     * about the accuracy and expressiveness of the interpolated elevation values 
     * will be available via the <tt>meta&lt;Attrib&gt;()</tt> methods of <tt>this</tt> 
     * class.
     * 
     * @param pointSet List of <tt>VgPoint</tt>-objects
     * @throws T3dException
     * @see {@link this#metaMIN_Z()}
     * @see {@link this#metaMAX_Z()}
     * @see {@link this#metaDELTA_Z()}
     * @see {@link this#metaHIT_COUNT()}
     * @see {@link this#metaCLUSTERS()}
     * @see {@link this#metaNOISE()} 
     */
    public GmSimpleElevationGrid transform(List<VgPoint> pointSet) throws T3dException
    {       
        if (pointSet == null || pointSet.size() <= 0)
            return null;
        
        GmSimpleElevationGrid elevGrid = super.transform(pointSet);
        System.out.println("Finished gridding.");
        
        this.provideMetadata(elevGrid, pointSet);
        return elevGrid;
    }
    
    @SuppressWarnings("unchecked")
    private void provideMetadata(GmSimpleElevationGrid elevGrid, List<VgPoint> pointSet) 
    {
        // Create copy of grid geometry:
        mGridGeom = new GmSimple2dGridGeometry(
            (GmSimple2dGridGeometry) elevGrid.getGeometry());
        this.setEnvelopeInfo(mGridGeom);

        mFoundPoints = 
            new ArrayList[mGridGeom.numberOfRows()][mGridGeom.numberOfColumns()];
        mFoundElevations = 
            new ArrayList[mGridGeom.numberOfRows()][mGridGeom.numberOfColumns()];
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                mFoundPoints[i][j] = new ArrayList<VgPoint>();
                mFoundElevations[i][j] = new ArrayList<Double>();
            }
        }

        for (int k = 0; k < pointSet.size(); k++) {
            VgPoint pnt = pointSet.get(k);     
            // (real) grid-indices:
            double 
                js = factorX * (pnt.getX() - mXMin),
                is = factorY * (pnt.getY() - mYMin);
            int 
                ii = (int) Math.round(is),
                jj = (int) Math.round(js);
            mFoundPoints[ii][jj].add(pnt);
            mFoundElevations[ii][jj].add(pnt.getZ());
        }
        System.out.println("Finished cell list construction.");
            
        this.sortFoundElevations();
        System.out.println("Finished elevation sorting.");
        
        this.compMIN_Z();
        System.out.println("Finished " + MIN_Z + " computation.");
        this.compMAX_Z(); 
        System.out.println("Finished " + MAX_Z + " computation.");
        this.compDELTA_Z(); 
        System.out.println("Finished " + DELTA_Z + " computation.");
        this.compCOUNT_SRCPOINTS(); 
        System.out.println("Finished " + HIT_COUNT + " computation.");
        this.compCLUSTERS(); 
        System.out.println("Finished " + CLUSTERS + " computation.");
        this.compNOISE(); 
        System.out.println("Finished " + NOISE + " computation.");
                
        // The meta-information grids will be interpreted as "grids", opposite
        // to the elevation grid, which refers to a "lattice" model (and thus 
        // should have been named as "elevation lattice"...).
        mGridMIN_Z.setLatticeInterpretation(false); 
        mGridMAX_Z.setLatticeInterpretation(false); 
        mGridDELTA_Z.setLatticeInterpretation(false); 
        mGridHIT_COUNT.setLatticeInterpretation(false); 
        mGridCLUSTERS.setLatticeInterpretation(false); 
        mGridNOISE.setLatticeInterpretation(false); 
    }

    @SuppressWarnings("unchecked")
    private void sortFoundElevations() {
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                List<Double> vals = mFoundElevations[i][j];
                if (vals != null && vals instanceof ArrayList) {
                    Collections.sort((ArrayList<Double>) vals);
                }
            }
        }       
    }

    private void compMIN_Z() {
        mGridMIN_Z = new GmSimpleElevationGrid(mGridGeom); 
        mGridMIN_Z.setName(MIN_Z);
           
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                if (mFoundElevations[i][j].size() > 0) {
                    mGridMIN_Z.setValue(i, j, (Double) mFoundElevations[i][j].get(0));
                } else {
                    mGridMIN_Z.unset(i, j); 
                }
            }
        }
    }

    private void compMAX_Z() {
        mGridMAX_Z = new GmSimpleElevationGrid(mGridGeom); 
        mGridMAX_Z.setName(MAX_Z);
        
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                int N = mFoundElevations[i][j].size();
                if (N > 0) {
                    mGridMAX_Z.setValue(i, j, (Double) mFoundElevations[i][j].get(N - 1));
                } else {
                    mGridMAX_Z.unset(i, j); 
                }
            }
        }
    }

    private void compDELTA_Z() {
        mGridDELTA_Z = new GmSimpleElevationGrid(mGridGeom); 
        mGridDELTA_Z.setName(DELTA_Z);
        
        int N;
        double min, max;
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                N = mFoundElevations[i][j].size();
                if (N > 0) {
                    min = (Double) mFoundElevations[i][j].get(0); 
                    max = (Double) mFoundElevations[i][j].get(N - 1); 
                    mGridDELTA_Z.setValue(i, j, max - min);
                } else {
                    mGridDELTA_Z.unset(i, j); 
                }
            }
        }
    }

    private void compCOUNT_SRCPOINTS() {
        mGridHIT_COUNT = new GmSimpleFloatGrid(mGridGeom); 
        mGridHIT_COUNT.setName(HIT_COUNT);

        int N;
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                N = mFoundElevations[i][j].size();
                mGridHIT_COUNT.setValue(i, j, (float) N);
            }
        }
    }

    private void compCLUSTERS() {  
        mGridCLUSTERS = new GmSimpleFloatGrid(mGridGeom);          
        mGridCLUSTERS.setName(CLUSTERS);

        int N;
        double min, max, deltaZ;
        int numberOfClasses;
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                N = mFoundElevations[i][j].size();
                if (N < 5) { // not enough data points 
                    if (N == 0)
                        mGridCLUSTERS.setValue(i, j, 0); 
                    else 
                        mGridCLUSTERS.setValue(i, j, 1);
                } else {
                    // Build histogram:
                    min = (Double) mFoundElevations[i][j].get(0); 
                    max = (Double) mFoundElevations[i][j].get(N - 1); 
                    deltaZ = max - min;
                    numberOfClasses = Math.min(
                        (int) Math.sqrt((double) N), 
                        15) + 2;
//                  System.out.print(numberOfClasses + ": ");
                    int[] group = new int[numberOfClasses];
                    group[0] = 0;
                    group[numberOfClasses - 1] = 0;
                    for (int k = 0; k < N; k++) {
                        double z = (Double) mFoundElevations[i][j].get(k);
                        int m = 1;
                        boolean sorted = false;
                        while (m < numberOfClasses - 1 && !sorted) {
                            if (z < min + m * deltaZ / numberOfClasses) {
                                group[m]++;
                                sorted = true;
                            }
                            m++;
                        }
                    }
                    // Determine number of peaks in histogram:
                    int numberOfPeaks = 0;
                    double last = -42.; // dummy
                    for (int m = 2; m < numberOfClasses; m++) {
//                      System.out.print(group[m] + " ");
                        if (m == 2 || last != group[m - 2]) {
                            last = group[m - 2];
                        }
                        if (last < group[m - 1] && group[m - 1] > group[m])
                            numberOfPeaks++;
                    }
//                  System.out.println(" -> " + numberOfPeaks);
                    mGridCLUSTERS.setValue(i, j, numberOfPeaks);
                }
            }
        }
    }

    private void compNOISE() {
        mGridNOISE = new GmSimpleFloatGrid(mGridGeom);         
        mGridNOISE.setName(NOISE);

        int N;
        double min, max, deltaZ;
        int numberOfClasses;
        for (int j = 0; j < mGridGeom.numberOfColumns(); j++) {
            for (int i = 0; i < mGridGeom.numberOfRows(); i++) {
                N = mFoundElevations[i][j].size();
                if (N < 5) { // not enough data points 
                    if (N == 0)
                        mGridNOISE.setValue(i, j, 0); 
                    else 
                        mGridNOISE.setValue(i, j, 1);
                } else {
                    // Build histogram:
                    min = (Double) mFoundElevations[i][j].get(0); 
                    max = (Double) mFoundElevations[i][j].get(N - 1); 
                    deltaZ = max - min;
                    numberOfClasses = Math.min(
                        (int) Math.sqrt((double) N), 
                        15) + 2;
                    int[] group = new int[numberOfClasses];
                    group[0] = 0;
                    group[numberOfClasses - 1] = 0;
                    for (int k = 0; k < N; k++) {
                        double z = (Double) mFoundElevations[i][j].get(k);
                        int m = 1;
                        boolean sorted = false;
                        while (m < numberOfClasses - 1 && !sorted) {
                            if (z < min + m * deltaZ / numberOfClasses) {
                                group[m]++;
                                sorted = true;
                            }
                            m++;
                        }
                    }
                    // Determine number of histograms classes with 
                    // > 0.5 * N / (numberOfClasses - 2) entries:
                    int ct = 0;
                    for (int m = 1; m < numberOfClasses - 1; m++) {
                        if (group[m] > 0.5 * N / (numberOfClasses - 2))
                            ct++;
                    }
                    double noise = ((double) ct) / ((double) (numberOfClasses - 2));
                    mGridNOISE.setValue(i, j, noise);
                }
            }
        }
    }

    // More private helpers:

    private int mNX, mNY;
    private double mXMin, mXMax, mYMin, mYMax;
    private double factorX, factorY;
    
    private void setEnvelopeInfo(GmSimple2dGridGeometry geom) 
    {
        mNX = geom.numberOfColumns();
        mNY = geom.numberOfRows();
        mXMin = geom.envelope().getXMin();
        mXMax = geom.envelope().getXMax();
        mYMin = geom.envelope().getYMin();
        mYMax = geom.envelope().getYMax();
        factorX = ((double) mNX - 1.) / (mXMax - mXMin);
        factorY = ((double) mNY - 1.) / (mYMax - mYMin);
    }
    
    /**
     * provides access to the grid holding the computed MIN_Z data.
     * 
     * @return Result grid (holding <i>z</i>-values)
     */
    public GmSimpleElevationGrid metaMIN_Z() {
        return mGridMIN_Z;
    }

    /**
     * provides access to the grid holding the computed MAX_Z data.
     * 
     * @return Result grid (holding <i>z</i>-values)
     */
    public GmSimpleElevationGrid metaMAX_Z() {
        return mGridMAX_Z;
    }

    /**
     * provides access to the grid holding the computed DELTA_Z data.
     * 
     * @return Result grid (holding <i>z</i>-values) 
     */
    public GmSimpleElevationGrid metaDELTA_Z() {
        return mGridDELTA_Z;
    }

    /**
     * provides access to the grid holding the computed HIT_COUNT data.  
     * Note that these values are integer-valued, but given as floating-point
     * numbers.
     * 
     * @return Result grid 
     */
    public GmSimpleFloatGrid metaHIT_COUNT() {
        return mGridHIT_COUNT;
    }

    /**
     * provides access to the grid holding the computed CLUSTERS data. 
     * Note that these values are integer-valued, but given as floating-point
     * numbers.
     * 
     * @return Result grid 
     */
    public GmSimpleFloatGrid metaCLUSTERS() {
        return mGridCLUSTERS;
    }

    /**
     * provides access to the grid holding the computed DIVERSITY data. 
     * 
     * @return Result grid (holding floating-point values) 
     */
    public GmSimpleFloatGrid metaNOISE() {
        return mGridNOISE;
    }
}
