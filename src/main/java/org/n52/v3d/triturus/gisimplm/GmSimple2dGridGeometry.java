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

import org.n52.v3d.triturus.vgis.VgEquidistGrid;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

import java.lang.Double;

/** 
 * Class to manage equidistant grid geometries that be oriented arbitrarily 
 * inside the x-y plane.
 *
 * @author Benno Schmidt
 */
public class GmSimple2dGridGeometry extends VgEquidistGrid
{
    private int mCols, mRows;
    private VgPoint mOrigin;
    private double mDeltaX, mDeltaY;

    
    /** 
     * Constructor.
     *
     * @param pCols Number of grid columns (x-direction)
     * @param pRows Number of grid rows (y-direction)
     * @param pOrigin Origin point
     * @param pDeltaX Cell-size in x-direction
     * @param pDeltaY Cell-size in y-direction
     */
    public GmSimple2dGridGeometry(
    		int pCols, int pRows, 
    		VgPoint pOrigin, 
    		double pDeltaX, double pDeltaY) 
    {
       mRows = pRows;
       mCols = pCols;

       mOrigin = pOrigin;

       mDeltaX = pDeltaX;
       mDeltaY = pDeltaY;
    }
    
    /** 
     * returns the grid geometry's bounding-box.
     *
     * @return Bounding-box (in x-y-plane; &quot;Lattice&quot; view)
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
     * return the geometry's number of grid columns.
     * 
     * @return Number of grid columns (x-direction)
     */
    public int numberOfColumns() {
        return mCols;
    }
    
    /**
     * return the geometry's number of grid rows.
     * 
     * @return Number of grid rows (y-direction)
     */
    public int numberOfRows() {
        return mRows;
    }

    /**
     * returns the vector that spans the grid's first axis (row direction).
     *
     * @return Direction vector
     */
    public T3dVector getDirectionRows() {
        return new T3dVector(0., 1., 0.);
    }
    
    /**
     * returns the vector that spans the grid's second axis (column direction).
     *
     * @return Direction vector
     */
    public T3dVector getDirectionColumns() {
        return new T3dVector(1., 0., 0.);
    }

    /**
     * sets the grid's origin point.
     *
     * @param pPnt Grid origin
     */
    public void setOrigin(VgPoint pPnt) {
        mOrigin = pPnt;
    }
    
    /** 
     * returns the grid's origin point.
     *
     * @return Grid origin
     */
    public VgPoint getOrigin() {
        return mOrigin;
    }

    /** 
     * returns the grid-cell size in x-direction (along the grid's columns).
     *
     * @return Cell size
     */
    public double getDeltaX() {
        return mDeltaX;
    }

    /**
     * @deprecated
     * @see this#getDeltaX
     */
    public double getCellSizeColumns() {
        return this.getDeltaX();
    }

    /** 
     * returns the grid-cell size in y-direction (along the grid's rows).
     *
     * @return Cell size
     */
    public double getDeltaY() {
        return mDeltaY;
    }
    
    /**
     * @deprecated
     * @see this#getDeltaY
     */
    public double getCellSizeRows() {
        return this.getDeltaY();
    }

    /** 
      * @deprecated
      * @see VgEquidistGrid#getCellSizeRows
      * @see VgEquidistGrid#getCellSizeColumns
      */
    public void getDelta(Double pDeltaRows, Double pDeltaColumns) {
        pDeltaRows = new Double(this.getCellSizeRows());
        pDeltaColumns = new Double(this.getCellSizeColumns());
    }	

    /**
     * @deprecated
     * @see this{@link #getVertexPoint(int, int)}
     */
    public VgPoint getVertexCoordinate(int i, int j) throws T3dException
    {
        return this.getVertexPoint(i, j);
    }

    /**
     * return a grid cell's vertex point.
     * 
     * @param i Grid row index (y-direction)
     * @param j Grid column index (x-direction)
     * @return Vertex point
     */
    public VgPoint getVertexPoint(int i, int j) throws T3dException
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
    
	/**
	 * returns the indices of the nearest grid element for a given 
	 * geo-position <tt>pPos</tt>. If <tt>pPos</tt> is outside the 
	 * elevation-grid, the return-value will be <i>null</i>. Otherwise the
	 * first element of the returned array will give the row index, the
	 * second element will give the column index.
	 * 
	 * @param pPos Query position
	 * @return int[2]-array holding row and column index
	 */
	public int[] getIndices(VgPoint pPos) 
	{
		double 
			col = (pPos.getX() - this.getOrigin().getX()) / this.getDeltaX(),
			row = (pPos.getY() - this.getOrigin().getY()) / this.getDeltaY();
		int 
			colInt = (int) Math.round(col),
			rowInt = (int) Math.round(row);
		if (
			colInt < 0 || colInt >= this.numberOfColumns() || 
			rowInt < 0 || rowInt >= this.numberOfRows())
		{
			return null; 
		}
		
		int[] res = new int[2];
		res[0] = rowInt;
		res[1] = colInt;
		return res; 
	}
}
