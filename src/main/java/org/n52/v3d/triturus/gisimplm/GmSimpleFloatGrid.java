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
 * Contact: Benno Schmidt and Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Abstract base class to manage a grid holding arbitrary floating-point 
 * data values. The grid is assumed to be parallel in line with both the 
 * <i>x-</i> and the <i>y</i>-axis. It might be specified as <i>vertex-based</i> 
 * (so-called &quot;Lattice&quot;) or <i>cell-based</i> (&quot;Grid&quot;). 
 * Note that grid values may left unset, since for all grid elements (vertices 
 * or cells) a &quot;no data&quot;-flag can be set.
 * 
 * @author Benno Schmidt
 */
public class GmSimpleFloatGrid extends VgFeature
{
    private GmSimple2dGridGeometry mGeom;
    private double[][] mVal;
    private boolean[][] mIsSetFl;
    private boolean mLatticeMode = false;
    private String mTheme = "(unnamed)";

    
    /**
     * Constructor. This will generate a grid will all elements unset.
     * 
     * @param cols Number of grid cells in <i>x</i>-direction (columns)
     * @param rows Number of grid cells in <i>y</i>-direction (rows)
     * @param origin Origin point
     * @param deltaX Cell-size in <i>x</i>-direction
     * @param deltaY Cell-size in <i>y</i>-direction
     */
    public GmSimpleFloatGrid(
        int cols, int rows, 
        VgPoint origin, 
        double deltaX, double deltaY)
    {
        mGeom = GmSimpleFloatGrid.setUpGeometry(cols, rows, origin, deltaX, deltaY);
        mVal = GmSimpleFloatGrid.setUpValArray(cols, rows); 
        mIsSetFl = GmSimpleFloatGrid.setUpIsSetArray(cols, rows); 

        this.setName("unnamed data grid");
    }
    
    static protected GmSimple2dGridGeometry setUpGeometry(
        int cols, int rows, 
        VgPoint origin, 
        double deltaX, double deltaY)
    {
        return new GmSimple2dGridGeometry(
            cols, rows, 
            origin, 
            deltaX, deltaY);
    }
    
    static protected double[][] setUpValArray(
        int cols, int rows)
    {
        return new double[rows][cols]; 
    }

    static protected boolean[][] setUpIsSetArray(
        int cols, int rows)
    {
        boolean[][] isSetFl = new boolean[rows][cols]; 
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                isSetFl[i][j] = false;
            }
        }
        return isSetFl;
    }

    /**
     * Constructor. This will generate a grid will all elements unset.
     * 
     * @param geom Existing grid geometry
     */
    public GmSimpleFloatGrid(GmSimple2dGridGeometry geom) 
    {
        mGeom = geom;
      
        int nRows = geom.numberOfRows();
        int nCols = geom.numberOfColumns();
 
        mVal = GmSimpleFloatGrid.setUpValArray(nCols, nRows); 
        mIsSetFl = GmSimpleFloatGrid.setUpIsSetArray(nCols, nRows); 
        
        this.setName("unnamed data grid");
    }

    public String getTheme() {
        return mTheme;
    }

    public void setTheme(String theme) {
        mTheme = theme;
    }

    /** 
     * provides thematic meta-information.
     * 
     * @return List of strings
     */
    public ArrayList<String> getThematicAttributes() 
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add(mTheme);
        return list;
    }
    
    /** 
     * returns the object geometry.
     * 
     * @return {@link GmSimple2dGridGeometry}-object
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

    /** 
     * always returns <i>false</i>, since a {@link GmSimpleFloatGrid} 
     * consists of one and only geo-object.
     * 
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * always returns this {@link GmSimpleFloatGrid} itself.
     * 
     * @param i (here always 0)
     * @return Elevation-grid itself
     * @throws T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
        throw new T3dException("Tried to access an atomic feature's sub-element." ); 
    }

    public int numberOfFeatures() {
        return 1;
    }

    /** 
     * @deprecated 
     */
    public int numberOfSubFeatures() {
        return numberOfFeatures();
    }

    public int numberOfColumns() {
        return mGeom.numberOfColumns();
    }
    
    public int numberOfRows() {
        return mGeom.numberOfRows();
    }
  
    /**
     * returns the grid's cell-size in <i>x</i>-direction.
     */
    public double getDeltaX() {
        return mGeom.getDeltaX();
    }

    /**
     * returns the grid's cell-size in <i>y</i>-direction.
     */
    public double getDeltaY() {
        return mGeom.getDeltaY();
    }

    /**
     * sets vertex-based interpretation mode. It always holds that either 
     * vertex-based or cell-based interpretation mode is set.
     */
    public void setLatticeInterpretation() {
       mLatticeMode = true;
    }
    
    /**
     * sets cell-based interpretation mode. 
     * 
     * @see this{@link #setLatticeInterpretation()}
     */
    public void setGridInterpretation() {
       mLatticeMode = false;
    }

    /**
     * returns the information, whether vertex-based interpretation mode is 
     * set.
     * 
     * @return <i>true</i> for vertex-based, <i>false</i> for cell-based interpretation 
     * @see this{@link #setLatticeInterpretation()}
     */
    public boolean isLatticeInterpretion() {
        return mLatticeMode;
    }

    /**
     * sets vertex-based interpretation mode to the given value.
     * 
     * @param latticeMode <i>true</i> for vertex-based, <i>false</i> for cell-based interpretation
     * @see this{@link #setLatticeInterpretation()}
     */
    public void setLatticeInterpretation(boolean latticeMode) {
        mLatticeMode = latticeMode;
    }

    /**
     * sets the data value <tt>z</tt> for the row index <tt>row</tt> and the 
     * column index <tt>col</tt>. If one of the assertions 
     * <i>0 &lt;= row &lt; this.numberOfRows(), 
     * 0 &lt;= col &lt; this.numberOfColumns()</i>
     * is violated, a <tt>T3dException</tt> will be thrown. Post-condition: 
     * <tt>this.isSet(row, col) = true</tt>
     * 
     * @param row Row index
     * @param col Column index
     * @param val Data value
     */
    public void setValue(int row, int col, double val) throws T3dException 
    {
        try {
            double zOld = mVal[row][col];
            mVal[row][col] = val;
            mIsSetFl[row][col] = true;
            this.updateValBounds(zOld, val);
        }
        catch (Exception e) {
            throw new T3dException(
            	"Could not set grid value (" + row + ", " + col + ").");
        }
    }

    /** 
     * returns <i>true</i>, if a data-value is assigned to a given 
     * grid element. If one of the assertions
     * <i>0 &lt;= row &lt; this.numberOfRows(), 
     * 0 &lt;= col &lt; this.numberOfColumns()</i> 
     * is violated, a <tt>T3dException</tt> will be thrown.
     * 
     * @param row Row index
     * @param col Column index
     * @throws T3dException
     */
    public boolean isSet(int row, int col) throws T3dException
    {
        try {
            return mIsSetFl[row][col];
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }
    }

    /** 
     * returns <i>true</i>, if all data values are assigned to all grid elements.
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
     * defines a grid element (vertex or cell) as unset (&quot;nodata&quot;). 
     * If one of the assertions
     * <i>0 &lt;= row &lt; this.numberOfRows(), 
     * 0 &lt;= col &lt; this.numberOfColumns()</i> 
     * is violated, a <tt>T3dException</tt> will be thrown. Post-condition:
     * <tt>this.isIsSet(pRow, pCol) = false</tt><p>
     * 
     * @param row Row index
     * @param col Column index
     * @throws T3dException
     */
    public void unset(int row, int col) throws T3dException
    {
        try {
            mIsSetFl[row][col] = false;
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }
    }

    /** 
     * gets the data-value for the row index <tt>row</tt> and the 
     * column index <tt>col</tt>. 
     * If one of the assertions
     * <i>0 &lt;= row &lt; this.numberOfRows(), 
     * 0 &lt;= col &lt; this.numberOfColumns()</i> 
     * is violated, or if the queried element is unset, a 
     * <tt>T3dException</tt> will be thrown. Post-condition:
     * <tt>this.isIsSet(row, col) = false</tt><p>
     * 
     * @param row Row-index
     * @param col Column-index
     * @throws T3dException
     */
    public double getValue(int row, int col) throws T3dException
    {
        try {
            if (mIsSetFl[row][col]) {
                return mVal[row][col];
            } else {
                throw new T3dException("Tried to access unset grid element.");
            }
        }
        catch (Exception e) {
            throw new T3dException(
            	"Illegal grid element access. " + e.getMessage());
        }
    }

    /** 
     * gets the data-value for the georeferenced position <tt>pPos</tt>.
     * Note that the method performs a <i>bilinear</i> interpolation. If the 
     * given position is outside the elevation grid's extent, the method will 
     * return <i>null</i>. If the position-points coordinate reference system 
     * is not compatible to the elevation-grids reference system, a 
     * {@link T3dSRSException} will be thrown.
     * 
     * @param pos Position (<i>x</i>, <i>y</i>)
     * @return Elevation (z) as {@link Double}-object or <i>null</i>
     */
    public Double getValue(VgPoint pos) throws T3dSRSException
    {
        if (pos == null) {
            return null;
        }
    
        if (!(pos.getSRS() == null && mGeom.getSRS() == null)) {
            if (!(pos.getSRS().equalsIgnoreCase(mGeom.getSRS()))) {
                throw new T3dSRSException();
            }
        }
    
        float[] idx = mGeom.getIndicesAsFloat(pos);
        if (idx == null) {
            return null;
        }
    
        float is = idx[0], js = idx[1];
        int row = (int)idx[0], col = (int)idx[1];
    
        if (
            mIsSetFl[row][col] && 
            mIsSetFl[row + 1][col] &&
            mIsSetFl[row][col + 1] &&
            mIsSetFl[row + 1][col + 1])
        {
            double lambda = js - ((float)col), my = is - ((float)row);
            return 
                mVal[row][col] * (1.f - my) * (1.f - lambda) +
                mVal[row + 1][col] * my * (1.f - lambda) +
                mVal[row][col + 1] * (1.f - my) * lambda + 
                mVal[row + 1][col + 1] * my * lambda;    			
        }
    
        // else:
        return null;
    }
    
    /** 
     * returns the data grid's bounding-box. 
     * <br/>
     * Notes:<br/>
     * 1. The extent depends on the grid-type (vertex-based 
     * &quot;lattice&quot; mode or cell-based &quot;grid&quot; mode)!<br/>
     * 2. The given extent in z-direction will not refer to a 
     * &quot;georeferenced&quot; information, but to the thematic attribut 
     * value's unit.
     * 
     * @return 3D-bounding-box, or <i>null</i> if an error occurs
     * @see GmSimpleFloatGrid#setLatticeInterpretation
     * @see GmSimpleFloatGrid#setGridInterpretation
     */
    public VgEnvelope envelope()
    {
        VgEnvelope env = this.getGeometry().envelope();
        double 
           xMin = env.getXMin(),
           xMax = env.getXMax(),
           yMin = env.getYMin(),
           yMax = env.getYMax();

        if (!mLatticeMode) {
            double dx = 0.5 * this.getDeltaX();
            xMin -= dx; 
            xMax += dx;
            double dy = 0.5 * this.getDeltaY();
            yMin -= dy; 
            yMax += dy;
        }

        double valMin, valMax;
        try {
            valMin = this.minimalDataValue();
            valMax = this.maximalDataValue();
        }
        catch (T3dException e) {
            return null;
        }

        return new GmEnvelope(xMin, xMax, yMin, yMax, valMin, valMax);
    }

    public double minimalDataValue() throws T3dException
    {
        try {
            this.calculateValBounds();
        }
        catch (T3dException e) {
            throw e;
        }
        return mValMin;
    }

    public double maximalDataValue() throws T3dException
    {
        try {
            this.calculateValBounds();
        }
        catch (T3dException e) {
            throw e;
        }
        return mValMax;
    }

    /**
     * deactivates lazy evaluation mode for minimal/maximal data-value 
     * computation. For performance reasons, it might be necessary to 
     * deactivate this mode explicitly before starting multiple 
     * <tt>setValue()</tt>-calls.
     */
    public void setDataValBoundsInvalid() {
        mCalculated = false;
    }

    // Private helpers to compute data value bounds ("lazy evaluation"!):

    private boolean mCalculated = false;
    private double mValMin, mValMax;

    private void calculateValBounds() throws T3dException
    { 
        if (!mCalculated) {
            try {
                mValMin = this.getFirstSetValue(); 
            }
            catch (T3dException e) {
                throw e;
            }
            mValMax = mValMin;
            double val;
            for (int i = 0; i < this.numberOfRows(); i++) {
                for (int j = 0; j < this.numberOfColumns(); j++) {
                   if (this.isSet(i, j)) {
                       val = this.getValue(i, j);
                       if (val < mValMin) 
                    	   mValMin = val; 
                       else {
                           if (val > mValMax) 
                        	   mValMax = val; 
                       }
                   }
                }
            }
            mCalculated = true;
        }
    }

    private void updateValBounds(double valOld, double valNew)     
    { 
        final double eps = 0.000001; // Epsilon for =-operation

        if (mCalculated) {
            if (valNew <= mValMin) { mValMin = valNew; return; }
            // assert: pValMin < pValNew
            if (valNew >= mValMax) { mValMax = valNew; return; }
            // assert: pValMin < pValNew < mValMax 
            if (Math.abs(valOld - mValMin) < eps || Math.abs(valOld - mValMax) < eps)
                mCalculated = false; // re-computation will be necessary later
        } // else:
            ; // skip
    }

    // Get value of some set grid element:
    private double getFirstSetValue() throws T3dException 
    {
        for (int i = 0; i < this.numberOfRows(); i++) {
            for (int j = 0; j < this.numberOfColumns(); j++) {
                if (this.isSet(i, j))
                    return this.getValue(i, j);
            }
        }
        // else:
        throw new T3dException("Tried to access empty data grid.");
    }

    /**
     * returns the coordinates and the value of a point that is part of the 
     * data-grid. If there is no data value assigned to the grid point, the 
     * return-value will be <i>null</i>.
     * 
     * @param row Row index
     * @param col Column index
     * @return Vector consisting of x- and y-geocoordinate and data-value as third component
     */
    public T3dVector getPoint(int row, int col) 
    {
        double 
           x = mGeom.getOrigin().getX() + col * this.getDeltaX(),
           y = mGeom.getOrigin().getY() + row * this.getDeltaY(),
           val;
        if (!this.isSet(row, col))
            return null;
        val = this.getValue(row, col);
        return new T3dVector(x, y, val);
    }

    /** 
     * returns the corresponding footprint geometry.
     * 
     * @return Footprint as {@link GmSimple2dGridGeometry}-object
     */
    public VgGeomObject footprint() {
        return mGeom.footprint();
    }

    public String toString() {
        String strGeom = "<empty geometry>";
        if (mGeom != null)
            strGeom = mGeom.toString(); 
        return "[" + mTheme + ", \"" + strGeom + "\"]";
    }
}
