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

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Filter to calculate cross-sections for equidistant elevation-grids that are parallel to the x- and y-axis.
 * @author Benno Schmidt
 */
public class FltElevationGridProfile extends T3dProcFilter
{
    private String mLogString = "";

    private GmSimpleElevationGrid mGrid = null;
    private double xll, yll, xur, yur;
    private int nx, ny;

    private VgProfile mRes = null;
    
    public FltElevationGridProfile() {
        mLogString = this.getClass().getName();
    }

    public String log() {
        return mLogString;
    }

    /** 
     * calculates the cross-section for an elevation-grid.
     * @param pGrid Elevation grid
     * @param pDefLine 2D base-line
     * @return 3D cross-section
     */
    public VgProfile transform(GmSimpleElevationGrid pGrid, VgLineString pDefLine) throws T3dException
    {
        mRes = new GmProfile(pDefLine);
        
        // Gitterparameter setzen (Member-Variable):
    	xll = pGrid.getGeometry().envelope().getXMin();
    	yll = pGrid.getGeometry().envelope().getYMin();
    	xur = pGrid.getGeometry().envelope().getXMax();
    	yur = pGrid.getGeometry().envelope().getYMax();
        nx = ((GmSimple2dGridGeometry) pGrid.getGeometry()).numberOfColumns();
    	ny = ((GmSimple2dGridGeometry) pGrid.getGeometry()).numberOfRows();
        mGrid = pGrid;

        VgPoint ip;

        // 1. z-Wert f�r Startpunkt des ersten Segments ermitteln
        if (pDefLine.numberOfVertices() <= 0)
            return null;
        ip = this.grdProject(pDefLine.getVertex(0));
        if (ip != null)
            this.registerVertex(ip, 0.);

    	VgLineSegment seg;
    	double iFromFp, jFromFp, iToFp, jToFp;
    	int iFrom, jFrom, iTo, jTo;

        double t = 0.;
        for (int k = 0; k < pDefLine.numberOfVertices() - 1; k++) 
        {    
            // Bearbeitung des k-ten Liniensegments:
            
            seg = new GmLineSegment(pDefLine.getVertex(k), pDefLine.getVertex(k + 1));

            // 2. Definitionsliniensegment mit Gitter verschneiden und z-Werte ermitteln
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
            
            // 3. z-Wert f�r Endpunkt des Segments ermitteln
            ip = this.grdProject(seg.getEndPoint());
            if (ip != null)
                this.registerVertex(ip, t + ((VgLineSegment) seg.footprint()).length());
            
            t += seg.length();

        }
        
        return mRes;
    }
    
    // Berechnung der (reellen) Gitterindizes eines Punktes: 
    private double grdIndexX(VgPoint pt) {
        return (pt.getX() - xll) / (xur - xll) * (double)(nx - 1);
    }    

    private double grdIndexY(VgPoint pt) {
        return (pt.getY() - yll) / (yur - yll) * (double)(ny - 1);
    }    
    
    // Berechnung Schnittpunkt zwischen Liniensegment und vertikaler Gitterlinie:
    private VgPoint grdIntersectVert(VgLineSegment seg, int jj) 
    {
        if (jj < 0 || jj >= nx) return null;
        
        double x, y, z;
        
        if (Math.abs(seg.getEndPoint().getX() - seg.getStartPoint().getX()) <= 0.000001) {
            // Segment parallel zu Gitterlinie
            return null; // todo: geht das okay?
        }
        
        // Parameter der Geradengleichung von seg:
        double m = (seg.getEndPoint().getY() - seg.getStartPoint().getY()) 
            / (seg.getEndPoint().getX() - seg.getStartPoint().getX());
        double b = seg.getStartPoint().getY() - m * seg.getStartPoint().getX();

        x = xll + ((double)jj) / ((double)(nx - 1)) * (xur - xll);
        y = m * x + b;
        double iiFp = ((double)(ny - 1)) * (y - yll) / (yur - yll); 
        if (iiFp < 0 || iiFp > (double)(ny - 1)) return null; // Schnittpunkt au�erhalb Gitter
        int ii1 = (int) Math.round(Math.floor(iiFp));
        int ii2 = ii1 + 1;
        if (ii2 >= ny - 1) ii2 = ny - 1; // Sonderfall oberer Gitterrand ber�cksichtigen
        double z1 = this.grdElevation(ii1, jj);
        double z2 = this.grdElevation(ii2, jj);
        z = z1 + (iiFp - (int) Math.floor(iiFp)) * (z2 - z1);
            
        return new GmPoint(x, y, z);
    }

    // Berechnung Schnittpunkt zwischen Liniensegment und horizontaler Gitterlinie:
    private VgPoint grdIntersectHoriz(VgLineSegment seg, int ii) 
    {
        if (ii < 0 || ii >= ny) return null;

        double x, y, z;
        
        if (Math.abs(seg.getEndPoint().getY() - seg.getStartPoint().getY()) <= 0.000001) {
            // Segment parallel zu Gitterlinie
            return null; // todo: geht das okay?
        }
                    
        // Parameter der Geradengleichung von seg:
        double m2 = (seg.getEndPoint().getX() - seg.getStartPoint().getX()) 
            / (seg.getEndPoint().getY() - seg.getStartPoint().getY());
        double b2 = seg.getStartPoint().getX() - m2 * seg.getStartPoint().getY();

        y = yll + ((double)ii) / ((double)(ny - 1)) * (yur - yll);
        x = m2 * y + b2;
        double jjFp = ((double)(nx - 1)) * (x - xll) / (xur - xll); 
        if (jjFp < 0 || jjFp > (double)(nx - 1)) return null; // Schnittpunkt au�erhalb Gitter
        int jj1 = (int) Math.round(Math.floor(jjFp));
        int jj2 = jj1 + 1;
        if (jj2 >= nx - 1) jj2 = nx - 1; // Sonderfall oberer Gitterrand ber�cksichtigen
        double z1 = this.grdElevation(ii, jj1);
        double z2 = this.grdElevation(ii, jj2);
        z = z1 + (jjFp - Math.floor(jjFp)) * (z2 - z1);
            
        return new GmPoint(x, y, z);
    }
 
    // Projektion eines Punktes auf das Gel�ndeoberfl�che:
    private VgPoint grdProject(VgPoint pt) 
    {
        // Reelle Indizes von pt im Gitter ermitteln:
        double jFp = this.grdIndexX(pt);
        double iFp = this.grdIndexY(pt);
        if (jFp < 0. || jFp > (double)nx - 1 || iFp < 0. || iFp > (double)ny - 1)
            return null;
  
        // Indizes der Ecken der zugeh�rigen rechteckigen Gitterzelle ermitteln:
        int jl = (int) Math.round(Math.floor(jFp));
        int jr = jl + 1;
        int il = (int) Math.round(Math.floor(iFp)); 
        int iu = il + 1;      
        double jrem = jFp - (double)jl; // Nachkommaanteil von jFp 
        double irem = iFp - (double)il;
        if (jr >= nx - 1 && jrem < 0.000001) { jr--; jl--; } // Sonderfall oberer Gitterrand ber�cksichtigen
        if (iu >= ny - 1 && irem < 0.000001) { iu--; il--; }

        // Pr�fen, ob Gitterzelle belegt ist:
        if (!(this.grdIsSet(il, jl) && this.grdIsSet(iu, jl) && this.grdIsSet(il, jr) && this.grdIsSet(iu, jr)))
            return null;
    
        // F�r die H�henwert-Interpolation wird die Gitterzelle in vier Dreiecke unterteilt. Die Dreiecksecken sind dabei
        // durch die Gitterzellenecken und den (2D-)Schwerpunkt der Gitterzelle gegeben.
        VgPoint pll, plr, pul, pur, pm; // Ecken u. Schwerpkt., hier nicht georeferenziert
        pll = new GmPoint(0., 0., this.grdElevation(il, jl));
        plr = new GmPoint(0., 1., this.grdElevation(il, jr));
        pul = new GmPoint(1., 0., this.grdElevation(iu, jl));
        pur = new GmPoint(1., 1., this.grdElevation(iu, jr));
//System.out.println("pll = " + pll);
//System.out.println("plr = " + plr);
//System.out.println("pul = " + pul);
//System.out.println("pur = " + pur);
        pm = new GmPoint(0.5, 0.5, 0.25 * (pll.getZ() + plr.getZ() + pul.getZ() + pur.getZ()));
  
        VgTriangle tri;
        if (jrem > 1. - 0.5 * irem) {
            if (irem > jrem) // rechtes Dreieck
                tri = new GmTriangle(pm, plr, pur);
            else // oberes Dreieck 
                tri = new GmTriangle(pm, pur, pul);
        } 
        else {
            if (irem > jrem) // unteres Dreieck 
                tri = new GmTriangle(pm, pll, plr);
            else // linkes Dreieck
                tri = new GmTriangle(pm, pul, pll);
        }
        return new GmPoint(pt.getX(), pt.getY(), tri.interpolateZ(new GmPoint(irem, jrem, 0. /*dummy*/)));
    }
    
    private boolean grdIsSet(int i, int j) {
        return mGrid.isSet(i, j);
    }

    private double grdElevation(int i, int j) {
        return mGrid.getValue(i, j);
    }
    
    private void registerVertex(VgPoint pt, double t) 
    {
//System.out.println("register vertex t = " + t + ", " + pt);
        double[] tzp = new double[2];
        tzp[0] = t;
        tzp[1] = pt.getZ();
        ((GmProfile) mRes).addTZPair(tzp);
    }
    
    // TODO: und jetzt noch alles in GmProfile packen!
}
