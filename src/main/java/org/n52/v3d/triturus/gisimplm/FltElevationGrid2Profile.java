/**
 * Copyright (C) 2007-2015 52North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Filter to compute cross-sections for equidistant elevation-grids that are 
 * parallel to the x- and y-axis.
 * 
 * @author Benno Schmidt
 */
public class FltElevationGrid2Profile extends T3dProcFilter
{
    private String logString = "";

    private GmSimpleElevationGrid grid = null;
    private double xll, yll, xur, yur;
    private int nx, ny;

    private VgProfile res = null;
    
    public FltElevationGrid2Profile() {
        logString = this.getClass().getName();
    }
                                                                                     
    public String log() {
        return logString;
    }

    /** 
     * computes the cross-section for an elevation-grid.
     * 
     * @param grid Elevation grid
     * @param defLine 2D base-line
     * @return 3D cross-section
     */
    public VgProfile transform(GmSimpleElevationGrid grid, VgLineString defLine) 
    	throws T3dException
    {
        if (defLine.numberOfVertices() <= 0)
            return null;
        
        // set grid parameters (member variables):
    	xll = grid.getGeometry().envelope().getXMin();
    	yll = grid.getGeometry().envelope().getYMin();
    	xur = grid.getGeometry().envelope().getXMax();
    	yur = grid.getGeometry().envelope().getYMax();
        nx = ((GmSimple2dGridGeometry) grid.getGeometry()).numberOfColumns();
    	ny = ((GmSimple2dGridGeometry) grid.getGeometry()).numberOfRows();
        this.grid = grid;

        res = new GmProfile(defLine);

        // 1. Determine z-value for start-point of 1st segment
        VgPoint ip = this.projectToSurface(defLine.getVertex(0));
        if (ip != null)
            this.registerVertex(ip, 0.);

    	double iFromFp, jFromFp, iToFp, jToFp;
    	int iFrom, jFrom, iTo, jTo;

        double t = 0.;
        for (int k = 0; k < defLine.numberOfVertices() - 1; k++) 
        {    
            // Handle k-th line segment:
            
            VgLineSegment seg = 
            	new GmLineSegment(defLine.getVertex(k), defLine.getVertex(k + 1));

            // 2. Intersect definition-line segment with grid and determine z-values
            jFromFp = this.grdIndexX(seg.getStartPoint());
            iFromFp = this.grdIndexY(seg.getStartPoint());
            jToFp = this.grdIndexX(seg.getEndPoint());
            iToFp = this.grdIndexY(seg.getEndPoint());
            if (jFromFp < 0.) jFromFp = 0.;
            if (jFromFp > (double)(nx - 1)) jFromFp = (double)(nx - 1);
            if (iFromFp < 0.) iFromFp = 0.;
            if (iFromFp > (double)(ny - 1)) iFromFp = (double)(ny - 1);
            if (jToFp < 0.) jToFp = 0.;
            if (jToFp > (double)(nx - 1)) jToFp = (double)(nx - 1);
            if (iToFp < 0.) iToFp = 0.;
            if (iToFp > (double)(ny - 1)) iToFp = (double)(ny - 1);
            if (jFromFp > jToFp) { double x = jFromFp; jFromFp = jToFp; jToFp = x; } // swap
            if (iFromFp > iToFp) { double x = iFromFp; iFromFp = iToFp; iToFp = x; }
            jFrom = (int) Math.round(Math.ceil(jFromFp));
            iFrom = (int) Math.round(Math.ceil(iFromFp));
            jTo = (int) Math.round(Math.floor(jToFp));
            iTo = (int) Math.round(Math.floor(iToFp));
            for (int jj = jFrom; jj <= jTo; jj++) {
                ip = this.grdIntersectVert(seg, jj);
                if (ip != null)
                    this.registerVertex(ip, t + ip.distanceXY(seg.getStartPoint()));
            }
            for (int ii = iFrom; ii <= iTo; ii++) {
                ip = this.grdIntersectHoriz(seg, ii);
                if (ip != null)
                    this.registerVertex(ip, t + ip.distanceXY(seg.getStartPoint()));
            }
            
            // 3. Determine z-value for segment end-point
            ip = this.projectToSurface(seg.getEndPoint());
            if (ip != null)
                this.registerVertex(ip, t + ((VgLineSegment) seg.footprint()).length());
            
            t += seg.length();
        }
        
        return res;
    }
    
    // Compute floating-point grid-indices of a point: 
    private double grdIndexX(VgPoint pt) {
        return (pt.getX() - xll) / (xur - xll) * (double)(nx - 1);
    }    

    private double grdIndexY(VgPoint pt) {
        return (pt.getY() - yll) / (yur - yll) * (double)(ny - 1);
    }    
    
    // Compute intersection point of line-segment and vertical grid line:
    private VgPoint grdIntersectVert(VgLineSegment seg, int jj) 
    {
        if (jj < 0 || jj >= nx) return null;
        
        
        if (Math.abs(seg.getEndPoint().getX() - seg.getStartPoint().getX()) <= 0.000001) {
            return null; // Segment parallel to grid line
        }
        
        // Parameters of line equation of seg:
        double m = 
        	(seg.getEndPoint().getY() - seg.getStartPoint().getY()) / 
        	(seg.getEndPoint().getX() - seg.getStartPoint().getX());
        double b = seg.getStartPoint().getY() - m * seg.getStartPoint().getX();

        double 
        	x = xll + ((double)jj) / ((double)(nx - 1)) * (xur - xll),
        	y = m * x + b;
        double iiFp = ((double)(ny - 1)) * (y - yll) / (yur - yll); 
        if (iiFp < 0 || iiFp > (double)(ny - 1)) 
        	return null; // intersection point outside grid
        int ii1 = (int) Math.round(Math.floor(iiFp));
        int ii2 = ii1 + 1;
        if (ii2 >= ny - 1) 
        	ii2 = ny - 1; // special case: consider upper grid edge 
        Double 
    		z1 = this.grdElevation(ii1, jj),
    		z2 = this.grdElevation(ii2, jj);
        if (z1 != null && z2 != null) {
        	double z = z1 + (iiFp - (int) Math.floor(iiFp)) * (z2 - z1);
            return new GmPoint(x, y, z);
        }
        return null;
    }

    // Compute intersection point of line-segment and horizontal grid line:
    private VgPoint grdIntersectHoriz(VgLineSegment seg, int ii) 
    {
        if (ii < 0 || ii >= ny) return null;

        if (Math.abs(seg.getEndPoint().getY() - seg.getStartPoint().getY()) <= 0.000001) {
            return null; // Segment parallel to grid line
        }
                    
        // Parameters of line equation of seg:
        double m2 = 
        	(seg.getEndPoint().getX() - seg.getStartPoint().getX()) / 
        	(seg.getEndPoint().getY() - seg.getStartPoint().getY());
        double b2 = seg.getStartPoint().getX() - m2 * seg.getStartPoint().getY();

        double 
        	y = yll + ((double)ii) / ((double)(ny - 1)) * (yur - yll),
        	x = m2 * y + b2;
        double jjFp = ((double)(nx - 1)) * (x - xll) / (xur - xll); 
        if (jjFp < 0 || jjFp > (double)(nx - 1)) 
        	return null; // intersection point outside grid
        int jj1 = (int) Math.round(Math.floor(jjFp));
        int jj2 = jj1 + 1;
        if (jj2 >= nx - 1) 
        	jj2 = nx - 1; // special case: consider upper grid edge 
        Double 
        	z1 = this.grdElevation(ii, jj1),
        	z2 = this.grdElevation(ii, jj2);
        if (z1 != null && z2 != null) {
        	double z = z1 + (jjFp - Math.floor(jjFp)) * (z2 - z1);
            return new GmPoint(x, y, z);
        }
        return null;
    }
 
    // Project point to elevation surface:
    private VgPoint projectToSurface(VgPoint pt) 
    {
        // Determine floating-point indices of pt in grid:
        double 
        	jFp = this.grdIndexX(pt),
        	iFp = this.grdIndexY(pt);
        if (jFp < 0. || jFp > (double)nx - 1 || iFp < 0. || iFp > (double)ny - 1)
            return null;
  
        // Determine corner indices of rectangular grid-cell:
        int jl = (int) Math.round(Math.floor(jFp));
        int jr = jl + 1;
        int il = (int) Math.round(Math.floor(iFp)); 
        int iu = il + 1;      
        double jrem = jFp - (double)jl; // jFp's portion after decimal point 
        double irem = iFp - (double)il;
        if (jr >= nx - 1 && jrem < 0.000001) 
        	{ jr--; jl--; } // special case: consider upper grid edge 
        if (iu >= ny - 1 && irem < 0.000001) { iu--; il--; }

        // Check if grid-cell is set:
        if (!(
        	this.grdIsSet(il, jl) && 
        	this.grdIsSet(iu, jl) && 
        	this.grdIsSet(il, jr) && 
        	this.grdIsSet(iu, jr)))
        {
            return null;
        }
        
        // To interpolate elevation values, the grid-cell is subdivided into
        // four triangles. The triangle corners are given by the grid-cell 
        // corners and the center of gravity of the grid-cell.
        VgPoint // corners and center of gravity, not georeferenced here
        	pll = new GmPoint(0., 0., this.grdElevation(il, jl)),
        	plr = new GmPoint(0., 1., this.grdElevation(il, jr)),
        	pul = new GmPoint(1., 0., this.grdElevation(iu, jl)),
        	pur = new GmPoint(1., 1., this.grdElevation(iu, jr)),
        	pm = new GmPoint(
        			0.5, 0.5, 
        			0.25 * (pll.getZ() + plr.getZ() + pul.getZ() + pur.getZ()));
		//System.out.println("pll = " + pll);
		//System.out.println("plr = " + plr);
		//System.out.println("pul = " + pul);
		//System.out.println("pur = " + pur);
  
        VgTriangle tri;
        if (jrem > 1. - 0.5 * irem) {
            if (irem > jrem) // right triangle
                tri = new GmTriangle(pm, plr, pur);
            else // upper triangle
                tri = new GmTriangle(pm, pur, pul);
        } 
        else {
            if (irem > jrem) // lower triangle
                tri = new GmTriangle(pm, pll, plr);
            else // left triangle
                tri = new GmTriangle(pm, pul, pll);
        }
        return new GmPoint(
        	pt.getX(), 
        	pt.getY(), 
        	tri.interpolateZ(new GmPoint(irem, jrem, 0. /*dummy*/)));
    }
    
    private boolean grdIsSet(int i, int j) {
        return grid.isSet(i, j);
    }

    private Double grdElevation(int i, int j) {
    	return grdIsSet(i, j) ? grid.getValue(i, j) : null;
    }
    
    private void registerVertex(VgPoint pt, double t) // TODO: put this into GmProfile
    {
    	//System.out.println("register vertex t = " + t + ", " + pt);
        double[] tzp = new double[2];
        tzp[0] = t;
        tzp[1] = pt.getZ();
        ((GmProfile) res).addTZPair(tzp);
    }
}
