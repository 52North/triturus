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
 * Contact: Benno Schmidt and Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Class to manage a grid holding elevation values. The grid is assumed 
 * to be parallel in line with the <i>x</i>- and <i>y</i>-axis. It might be specified 
 * as vertex-based (so-called <i>&quot;Lattice&quot;</i>) or cell-based 
 * (<i>&quot;Grid&quot</i>;, often reffered to as &quot;Raster&quot;). 
 * Note that grid's values may left unset, since for all grid elements 
 * (vertices or cells) a &quot;no data&quot;-flag can be set.<br/>
 * <br/>
 * Note: Within this framework, elevation-grids should be used to hold 
 * georeferenced <i>z</i>-value only, e.g terrain elevation values, groundwater 
 * levels, geologic depth data, height values inside the atmosphere etc.
 * To hold thematic ("non-georeferenced") floating-point data such as 
 * temperatures, humidities, wind speeds, air pressure values etc. the
 * class <tt>{@link GmSimpleFloatGrid}</tt> shall be preferred. 
 * 
 * @author Benno Schmidt
 */
public class GmSimpleElevationGrid extends VgElevationGrid
{
    private GmSimple2dGridGeometry mGeom;
    private double[][] mVal;
    private boolean[][] mIsSetFl;
    private boolean mLatticeMode = false;
    private String mTheme = "Elevations";

    
    /**
     * Constructor. This will generate a grid will all elements unset.
     * 
     * @param nCols Number of grid cells in <i>x</i>-direction (columns)
     * @param nRows Number of grid cells in <i>y</i>-direction (rows)
     * @param origin Origin point
     * @param deltaX Cell-size in <i>x</i>-direction
     * @param deltaY Cell-size in <i>y</i>-direction
     */
    public GmSimpleElevationGrid(
        int nCols, int nRows, 
        VgPoint origin, 
        double deltaX, double deltaY)
    {
        // TODO: Im Folgenden besser die static-Methoden aus GmSimpleFloatGrid 
        // nutzen, um Redundanzen zu vermeiden. (-> nächstes Refactoring)
    
        mGeom = new GmSimple2dGridGeometry(
            nCols, nRows, 
            origin, 
            deltaX, deltaY);

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
     * Constructor. This will generate a grid will all elements unset.
     * 
     * @param geom Existing grid geometry
     */
    public GmSimpleElevationGrid(GmSimple2dGridGeometry geom) 
    {
        mGeom = geom;
      
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
     * always returns <i>false</i>, since a {@link GmSimpleElevationGrid} 
     * consists of one and only geo-object.
     * 
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * always returns this {@link GmSimpleElevationGrid} itself.
     * 
     * @param i (here always 0)
     * @return Elevation-grid itself
     * @throws T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
        if (i != 0) 
            throw new T3dException("Index out of bounds." ); 
        // else:
        return this;
    }

    public int numberOfFeatures() {
        return 1;
    }

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
     * sets the elevation value <tt>pZ</tt> for the row index <tt>pRow</tt> 
     * and the column index <tt>pCol</tt>. If one of the assertions 
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i>
     * is violated, a <tt>T3dException</tt> will be thrown. Post-condition: 
     * <tt>this.isSet(pRow, pCol) = true</tt>
     * 
     * @param row Row-index
     * @param col Column-index
     * @param z Elevation-value
     */
    public void setValue(int row, int col, double z) throws T3dException 
    {
        try {
            double zOld = mVal[row][col];
            mVal[row][col] = z;
            mIsSetFl[row][col] = true;
            this.updateZBounds(zOld, z);
        }
        catch (Exception e) {
            throw new T3dException(
            	"Could not set grid value (" + row + ", " + col + ").");
        }
    }

    /** 
     * returns <i>true</i>, if an elevation-value is assigned to a given 
     * grid element. If one of the assertions
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i> 
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
     * returns <i>true</i>, if all <i>z</i>-values are assigned to all grid elements.
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
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i> 
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
     * gets the elevation-value for the row-index <tt>pRow</tt> and the 
     * column-index <tt>pCol</tt>. 
     * If one of the assertions
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i> 
     * is violated, or if the queried element is unset, a 
     * <tt>T3dException</tt> will be thrown. Post-condition:
     * <tt>this.isIsSet(pRow, pCol) = false</tt><p>
     * 
     * @param row Row index
     * @param col Column index
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
     * gets the elevation-value for the georeferenced position <tt>pPos</tt>.
     * Note that the method performs a <i>bilinear</i> interpolation. If the 
     * given position is outside the elevation grid's extent, the method will 
     * return <i>null</i>. If the position-points coordinate reference system 
     * is not compatible to the elevation-grids reference system, a 
     * {@link T3dSRSException} will be thrown.
     * 
     * @param pos Position (x, y)
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
     * returns the elevation grid's bounding-box. Note that this extent 
     * depends on the grid-type (vertex-based &quot;lattice&quot; mode or 
     * cell-based &quot;grid&quot; mode)!
     * 
     * @return 3D-bounding-box, or <i>null</i> if an error occurs
     * @see GmSimpleElevationGrid#setLatticeInterpretation
     * @see GmSimpleElevationGrid#setGridInterpretation
     */
    public VgEnvelope envelope()
    {
       VgEnvelope env = this.getGeometry().envelope();
       double xMin = env.getXMin();
       double xMax = env.getXMax();
       double yMin = env.getYMin();
       double yMax = env.getYMax();

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
     * deactivates lazy evaluation mode for minimal/maximal <i>z</i>-value 
     * computation. For performance reasons, it might be necessary to 
     * deactivate this mode explicitly before starting multiple 
     * <tt>setValue()</tt>-calls.
     */
    public void setZBoundsInvalid() {
        mCalculated = false;
    }

    // Private helpers to compute z-bounds ("lazy evaluation"!):

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

    private void updateZBounds(double zOld, double zNew)     
    { 
        final double eps = 0.000001; // Epsilon for =-operation

        if (mCalculated) {
            if (zNew <= mZMin) { mZMin = zNew; return; }
            // assert: pZMin < pZNew
            if (zNew >= mZMax) { mZMax = zNew; return; }
            // assert: pZMin < pZNew < mZMax 
            if (Math.abs(zOld - mZMin) < eps || Math.abs(zOld - mZMax) < eps)
                mCalculated = false; // re-computation will be necessary later
        } // else:
            ; // skip
    }

    // Get z-value of some set grid element:
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
     * returns the coordinates of a point that is part of the elevation-grid.
     * If there is no <i>z</i>-value assigned to the grid point, the return-value 
     * will be <i>null</i>.
     * 
     * @param row Row-index
     * @param col Column-index
     * @return Elevation-Point 
     */
    public VgPoint getPoint(int row, int col) 
    {
        double 
            x = mGeom.getOrigin().getX() + col * this.getDeltaX(),
            y = mGeom.getOrigin().getY() + row * this.getDeltaY(),
            z;
        if (!this.isSet(row, col))
            return null;
        z = this.getValue(row, col);
        return new GmPoint(x, y, z);
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
