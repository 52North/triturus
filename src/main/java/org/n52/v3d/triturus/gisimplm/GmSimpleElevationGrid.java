/**
 * Copyright (C) 2007-2016 52 North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Class to manage a grid holding elevation values. The grid is assumed 
 * to be parallel in line with the x- and y-axis. It might be specified 
 * as vertex-based (so-called &quot;Lattice&quot;) or cell-based 
 * (&quot;Grid&quot;). Note that grid's values may left unset, since for all 
 * grid elements (vertices or cells) a &quot;no data&quot;-flag can be set.
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
     * @param pCols Number of grid cells in x-direction (columns)
     * @param pRows Number of grid cells in y-direction (rows)
     * @param pOrigin Origin point
     * @param pDeltaX Cell-size in x-direction
     * @param pDeltaY Cell-size in y-direction
     */
    public GmSimpleElevationGrid(
    		int pCols, int pRows, 
    		VgPoint pOrigin, 
    		double pDeltaX, double pDeltaY)
    {
        mGeom = new GmSimple2dGridGeometry(
        		pCols, pRows, 
        		pOrigin, 
        		pDeltaX, pDeltaY);
        
        mVal = new double[pRows][pCols]; 
        mIsSetFl = new boolean[pRows][pCols]; 
        for (int i = 0; i < pRows; i++) {
            for (int j = 0; j < pCols; j++) {
                mIsSetFl[i][j] = false;
            }
        }
        
        this.setName("unnamed elevation grid");
    }

    /**
     * Constructor. This will generate a grid will all elements unset.
     * 
     * @param pGeom Existing grid geometry
     */
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
     * provides thematic meta-information.
     * 
     * @return List of strings
     */
    public ArrayList<String> getThematicAttributes() 
    {
        ArrayList<String> lList = new ArrayList<String>();
        lList.add(mTheme);
        return lList;
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

    public int numberOfSubFeatures() {
        return 1;
    }

    public int numberOfColumns() {
        return mGeom.numberOfColumns();
    }
    
    public int numberOfRows() {
        return mGeom.numberOfRows();
    }
  
    /**
     * returns the grid's cell-size in x-direction.
     */
    public double getDeltaX() {
        return mGeom.getDeltaX();
    }

    /**
     * returns the grid's cell-size in y-direction.
     */
    public double getDeltaY() {
        return mGeom.getDeltaY();
    }

    /**
     * sets vertex-based interpretation mode.
     */
    public void setLatticeInterpretation() {
       mLatticeMode = true;
    }
    
    /**
     * sets cell-based interpretation mode.
     */
    public void setGridInterpretation() {
       mLatticeMode = false;
    }

    /**
     * sets the elevation value <tt>pZ</tt> for the row index <tt>pRow</tt> 
     * and the column index <tt>pCol</tt>. If one of the assertions 
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i>
     * is violated, a <tt>T3dException</tt> will be thrown. Post-condition: 
     * <tt>this.isSet(pRow, pCol) = true</tt>
     * 
     * @param pRow Row-index
     * @param pCol Column-index
     * @param pZ Elevation-value
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
            throw new T3dException(
            	"Could not set grid value (" + pRow + ", " + pCol + ").");
        }
    }

    /** 
     * returns <i>true</i>, if an elevation-value is assigned to a given 
     * grid element. If one of the assertions
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i> 
     * is violated, a <tt>T3dException</tt> will be thrown.
     * 
     * @param pRow Row-index
     * @param pCol Column-index
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
     * returns <i>true</i>, if all z-values are assigned to all grid elements.
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
     * @param pRow Row-index
     * @param pCol Column-index
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
     * gets the elevation-value for the row-index <tt>pRow</tt> and the 
     * column-index <tt>pCol</tt>. 
     * If one of the assertions
     * <i>0 &lt;= pRow &lt; this.numberOfRows(), 
     * 0 &lt;= pCol &lt; this.numberOfColumns()</i> 
     * is violated, or if the queried element is unset, a 
     * <tt>T3dException</tt> will be thrown. Post-condition:
     * <tt>this.isIsSet(pRow, pCol) = false</tt><p>
     * 
     * @param pRow Row-index
     * @param pCol Column-index
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
            throw new T3dException(
            	"Illegal grid element access. " + e.getMessage());
        }
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
     * deactivates lazy evaluation mode for minimal/maximal z-value 
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

    private void updateZBounds(double pZOld, double pZNew)     
    { 
        final double eps = 0.000001; // Epsilon for =-operation

        if (mCalculated) {
            if (pZNew <= mZMin) { mZMin = pZNew; return; }
            // assert: pZMin < pZNew
            if (pZNew >= mZMax) { mZMax = pZNew; return; }
            // assert: pZMin < pZNew < mZMax 
            if (Math.abs(pZOld - mZMin) < eps || Math.abs(pZOld - mZMax) < eps)
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
	 * returns the corresponding footprint geometry.
	 * 
	 * @return Footprint as {@link GmSimple2dGridGeometry}-object
  	 */
	public VgGeomObject footprint() {
		return mGeom.footprint();
	}

	/**
     * returns the coordinates of a point that is part of the elevation-grid.
     * If there is no z-value assigned to the grid point, the return-value 
     * will be <i>null</i>.
     * 
     * @param pRow Row-index
     * @param pCol Column-index
     * @return Elevation-Point 
	 */
	public VgPoint getPoint(int pRow, int pCol) 
	{
		double x = mGeom.getOrigin().getX() + pCol * this.getDeltaX();
        double y = mGeom.getOrigin().getY() + pRow * this.getDeltaY();
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
