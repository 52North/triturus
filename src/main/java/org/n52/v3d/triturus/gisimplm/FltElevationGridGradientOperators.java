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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dProcFilter;

/**
 * Computation of gradient-related for elevation-grids. Particularly, 
 * geomorphometric parameters such as <i>slope</i> (1st derivative, often also 
 * referred to as <i>dip</i> or <i>inclination</i>), <i>aspect</i> (<i>azimuth</i>, 
 * <i>exposition</i>) and <i>curvatures</i> (2nd derivatives) will be 
 * estimated.<br/>
 * <br/> 
 * By calling this class's <tt>transform()</tt>-method, an additional grid 
 * holding the specified values will be generated as output result. The 
 * resulting grid will refer to the same geometry as the input grid.<br/> 
 * <br/>
 * If possible, the computation will be performed using Horn's formula using
 * finite differences to estimate the gradients (Horn 1981, [1]). The formulas 
 * to compute curvatures can be found in this class's source code.<br/>
 * <br/>
 * Notes:<br/>
 * 1. If the switch <tt>tryMore</tt> is set (default value is <i>false</i>, it
 * will be tried to give result values wherever possible with the help of other
 * finite difference terms than those proposed by Horn (1981). In this case,
 * in the border area (first and last grid row and column) as well as for 
 * locations with unset grid values, computations results may differ in terms 
 * of the informative value due to the different calculation method (e.g., 
 * because the estimates of the gradients might be less precise there).<br/>
 * 2. Using Horn's formulas, for unset elevation grid cells a result value 
 * for aspect, slope, d<i>z</i>/d<i>x</i>, or d<i>z</i>/d<i>y</i> might be 
 * generated, if for all its neighbor elements elevations are available.<br/>
 * <br/>
 * Reference:<br/>
 * [1] Horn, B.K.P. (1981): Hill Shading and the Reflectance Map. Proceedings 
 * of the IEEE, 69(1):14 (Febr. 1981). DOI: 10.1109/PROC.1981.11918<br/>
 *   
 * @author Benno Schmidt
 */
public class FltElevationGridGradientOperators extends T3dProcFilter
{
    /**
     * Supported geomorphometric terrain analysis types are given in the table 
     * below. Note that all output values will be written as floating-point 
     * numbers with a precision of two decimal places.<br/>
     * <br/>
     * <table border="1">
     *   <tr>
     *     <th>Enumeration value</td>
     *     <th>Performed analysis for focal cell</td>
     *     <th>Unit</td>
     *     <th>Data range</td>
     *   </tr>
     *   <tr>
     *     <td>DZDX</td>
     *     <td>
     *       Partial derivative d<i>z</i>/d<i>x</i> in <i>x</i>-direction,<br>
     *       positive values indicating elevations grow towards +<i>x</i>-direction
     *     </td>
     *     <td>(unitless)</td>
     *     <td>-oo .. +oo</td>
     *   </tr>
     *   <tr>
     *     <td>DZDY</td>
     *     <td>
     *       Partial derivative d<i>z</i>/d<i>y</i> in <i>y</i>-direction,<br>
     *       positive values indicating elevations grow towards +<i>y</i>-direction
     *     </td>
     *     <td>(unitless)</td>
     *     <td>-oo .. +oo</td>
     *   </tr>
     *   <tr>
     *     <td>SLOPE</td>
     *     <td>Slope</td>
     *     <td>(unitless)</td>
     *     <td>0 .. +oo</td>
     *   </tr>
     *   <tr>
     *     <td>SLOPE_PERCENT</td>
     *     <td>Slope, given as percentage values</td>
     *     <td>(initless)</td>
     *     <td>0 .. 100</td>
     *   </tr>
     *   <tr>
     *     <td>SLOPE_DEGR</td>
     *     <td>Slope, as angles given in degrees</td>
     *     <td>degrees</td>
     *     <td>0 .. 90</td>
     *   </tr>
     *   <tr>
     *     <td>ASPECT_RAD</td>
     *     <td>Aspect, given in radians</td>
     *     <td>radians</td>
     *     <td>0 .. 360</td>
     *   </tr>
     *   <tr>
     *     <td>ASPECT_DEGR</td>
     *     <td>
     *       Aspect, given in degrees,<br/>
     *       where N = 0°, E = 90°, S = 180°. W = 270°.</td>
     *     <td>degrees</td>
     *     <td>0 .. 360</td>
     *   </tr>
     *   <tr>
     *     <td>ASPECT_GON</td>
     *     <td>
     *       Aspect, given in gons,<br/>
     *       where N = 0, E = 100, S = 200. W = 300.</td>
     *     <td>gon</td>
     *     <td>0 .. 400</td>
     *   </tr>
     *   <tr>
     *     <td>ASPECT_CATEGORIES_8</td>
     *     <td>
     *       Aspect, given as 8 + 1 categories,<br/>
     *       N = 1, NE = 2, E = 3, SE = 4, S = 5, SW = 6, W = 7, NW = 8, 0 for plain areas.
     *     </td>
     *     <td>-</td>
     *     <td>0 .. 8</td>
     *   </tr>
     *   <tr>
     *     <td>ASPECT_CATEGORIES_4</td>
     *     <td>
     *       Aspect, given as 4 + 1 categories,<br/>
     *       N = 1, E = 2, S = 3, W = 4, 0 for plain areas.
     *     </td>
     *     <td>-</td>
     *     <td>0 .. 4</td>
     *   </tr>
     *   <tr>
     *     <td>TRY_INFO</td>
     *     <td>
     *       Try information (try number to estimate d<i>z</i>/d<i>x</i><br/>
     *       concatenated with try number to estimate d<i>z</i>/d<i>y</i>,<br/>
     *       '0' for failed computation; see source code for details)
     *     </td>
     *     <td>-</td>
     *     <td>Regular expression:<br/>['0'-'4']['0'-'4']</td></tr>
     *   <tr>
     *     <td>PROFILE_CURVATURE</td>
     *     <td>
     *       Profile curvature<br/>
     *       (i.e. the change in slope in the gradient direction),<br/>
     *       negative values indicating areas of convex &quot;accelerated water flow&quot;),<br/>
     *       positive values indicating areas of concave &quot;slowed&quot;flow.
     *     <td>(unitless,<br/>values multiplied by 100)</td>
     *     <td>-oo .. +oo</td>
     *   </tr>
     *   <tr>
     *     <td>PLAN_CURVATURE</td>
     *     <td>
     *       Plan curvature<br/>
     *       (i.e. the curvature of the contour),<br/>,
     *       negative values indicating areas of divergent flow,<br/>
     *       positive values indicating areas of convergent flow.
     *     </td>
     *     <td>(unitless<br/>values multiplied by 100)</td>
     *     <td>-oo .. +oo</td></tr>
     *   </tr>
     * </table>
     */
    public enum AnalysisMode { 
        DZDX, DZDY,
        SLOPE, SLOPE_PERCENT, SLOPE_DEGR, 
        ASPECT_RAD, ASPECT_DEGR, ASPECT_GON, ASPECT_CATEGORIES_4, ASPECT_CATEGORIES_8,
        TRY_INFO,
        PROFILE_CURVATURE, PLAN_CURVATURE
    };
    private AnalysisMode mode = AnalysisMode.SLOPE;
    
    private String logString = "";

    private GmSimpleElevationGrid input; 
    private double 
        dX, dY,
        dZdX, dZdY,
        D, E, F, G, H;
    private int dZdXDone, dZdYDone;
    private boolean all9PointsAvailable = false;
    private boolean tryMore = false;

    public FltElevationGridGradientOperators() {
        logString = this.getClass().getName();
    }
                                                                                     
    public String log() {
        return logString;
    }

    public FltElevationGridGradientOperators(AnalysisMode analysisType) {
        logString = this.getClass().getName();
        setMode(analysisType);
    }
     
    /**
     * sets the analysis type that will be performed when calling the 
     * <tt>transform()</tt> method. The supported types are listed in the
     * {@link AnalysisMode} documentation. 
     * 
     * @param analysisType Analysis mode specifier
     */
    public void setMode(AnalysisMode analysisType) {
        mode = analysisType;
    }
    
    /** 
     * calculates specified gradient-related information for an elevation-grid.
     *
     * @param grid Input grid
     * @return Result grid
     * @throws T3dException
     */
    public GmSimpleFloatGrid transform(GmSimpleElevationGrid grid) 
        throws T3dException
    {
        if (grid == null) 
            throw new T3dException("Received null pointer as input grid.");
        input = grid;
    
        int 
            nCols = input.numberOfColumns(),
            nRows = input.numberOfRows();   
        dX = input.getDeltaX();
        dY = input.getDeltaY();
    
        GmSimpleFloatGrid output = new GmSimpleFloatGrid(
             nCols, nRows,
             ((GmSimple2dGridGeometry) input.getGeometry()).getOrigin(),
             dX, dY);
        output.setLatticeInterpretation(grid.isLatticeInterpretion());

        String theme = "(unnamed data theme)";
        switch (mode) {
            // Note: To ease VTK export, in the following usage of space 
            // characters in object names has been avoided. 
            case DZDX:
                theme = "dZ/dX_gradients";
                break;
            case DZDY:
                theme = "dZ/dY_gradients"; 
                break;                      
            case SLOPE: 
                theme = "Slope"; 
                break;
            case SLOPE_PERCENT: 
                theme = "Slope_[%]"; 
                break;
            case SLOPE_DEGR:
                theme = "Slope_[degr]"; 
                break;
            case ASPECT_RAD:
                theme = "Aspect_[rad]"; 
                break;
            case ASPECT_GON:
                theme = "Aspect_[gon]"; 
                break;
            case ASPECT_DEGR:
                theme = "Aspect_[degr]"; 
                break;
            case ASPECT_CATEGORIES_4:
                theme = "Aspect_[4+1_categories]"; 
                break;
            case ASPECT_CATEGORIES_8:
                theme = "Aspect_[8+1_categories]"; 
                break;
            case TRY_INFO:
                theme = "Try_info_code"; 
                break;
            case PROFILE_CURVATURE: 
                theme = "Profile_Curvature_[x100]"; 
                break;
            case PLAN_CURVATURE: 
                theme = "Plan_Curvature_[x100]"; 
                break;
            default:
                throw new T3dException("Received unknown mode directive.");
        }
        output.setName(theme); 
        output.setTheme(theme);

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                performCalculation(i, j);

                output.unset(i, j); 
                switch (mode) {
                    case DZDX:
                        if (dZdXDone > 0) 
                            output.setValue(i, j, dZdX);
                        break;
                    case DZDY:
                        if (dZdYDone > 0)  
                            output.setValue(i, j, dZdY);
                        break;                      
                    case SLOPE:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, this.slope());
                        break;
                    case SLOPE_PERCENT:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, 100. * this.slope());
                        break;
                    case SLOPE_DEGR:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, Math.atan(this.slope()));
                        break;
                    case ASPECT_RAD:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, this.aspect() >= 0 ? this.aspect() : -1.);
                            // -1. indicating plain areas
                        break;
                    case ASPECT_DEGR:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, this.aspect() >= 0 ? this.aspect() * 180./Math.PI : -1.);
                            // -1. indicating plain areas
                        break;
                    case ASPECT_GON:
                        if (dZdXDone > 0 && dZdYDone > 0) 
                            output.setValue(i, j, this.aspect() >= 0 ? this.aspect() * 200./Math.PI : -1.);
                        // -1. indicating plain areas
                        break;                  
                    case ASPECT_CATEGORIES_8:
                        if (dZdXDone > 0 && dZdYDone > 0) { 
                            double 
                                val = this.aspect() * 200./Math.PI, 
                                res = 0.;  
                            if (val <= 0.) res = 0.; // 0. indicating plain areas
                            if (val >= 0 && val < 25) res = 1.;
                            if (val >= 25 && val < 75) res = 2.;
                            if (val >= 75 && val < 125) res = 3.;
                            if (val >= 125 && val < 175) res = 4.;
                            if (val >= 175 && val < 225) res = 5.;
                            if (val >= 225 && val < 275) res = 6.;
                            if (val >= 275 && val < 325) res = 7.;
                            if (val >= 325 && val < 375) res = 8.;
                            if (val >= 375 && val <= 400) res = 1.;
                            output.setValue(i, j, res);
                        }
                        break;          
                    case ASPECT_CATEGORIES_4:
                        if (dZdXDone > 0 && dZdYDone > 0) { 
                            double 
                                val = this.aspect() * 200./Math.PI, 
                                res = 0.;  
                            if (val <= 0.) res = 0.; // 0. indicating plain areas
                            if (val >= 0 && val < 50) res = 1.;
                            if (val >= 50 && val < 150) res = 2.;
                            if (val >= 150 && val < 250) res = 3.;
                            if (val >= 250 && val < 350) res = 4.;
                            if (val >= 350 && val <= 400) res = 1.;
                            output.setValue(i, j, res);
                        }
                        break;          
                    case TRY_INFO: 
                        output.setValue(i, j, dZdXDone * 10 + dZdYDone);
                        break;
                    case PROFILE_CURVATURE: 
                        if (all9PointsAvailable && this.profileCurvature() != null) 
                            output.setValue(i, j, 100. * this.profileCurvature());
                        break;
                    case PLAN_CURVATURE: 
                        if (all9PointsAvailable && this.planCurvature() != null) 
                            output.setValue(i, j, 100. * this.planCurvature());
                        break;
                    default:
                        throw new T3dException("Received unknown mode directive.");
                }
            }
        }

        return output;
    }

    private double slope() {
        if (dZdXDone > 0 && dZdYDone > 0) 
            return Math.sqrt(dZdX * dZdX + dZdY * dZdY);
        else
            throw new T3dException("Logical error");
    }
    
    private double aspect() {
        if (dZdXDone > 0 && dZdYDone > 0) {
            if (dZdX == 0.) {
                if (dZdY == 0.) {
                    // Aspect is not defined, thus:
                    return -1.;
                } // else:
                return dZdY > 0. ? Math.PI : 1.5 * Math.PI;
            } 
            // else:
            double aspect = 1.5 * Math.PI - Math.atan2(dZdY, dZdX);
            if (aspect > 2. * Math.PI) 
                aspect -= 2. * Math.PI;
            if (aspect < 0.) 
                aspect += 2. * Math.PI;     
            return aspect;
        }
        else
            throw new T3dException("Logical error");
    }
    
    private Double profileCurvature() {
        if (all9PointsAvailable) {
            if (G*G + H*H != 0.)
                return -2. * (D * G*G + E * H*H + F * G * H) / (G*G + H*H);
            else 
                return null; // TODO: Can we return a proper value in this case? 
        } else
            return null;
    }

    private Double planCurvature() {
        if (all9PointsAvailable) {
            if (G*G + H*H != 0.) 
                return 2. * (D * H*H + E * G*G + F * G * H) / (G*G + H*H);
            else 
                return null; // TODO: Can we return a proper value in this case?
        } else
            return null;
    }

    private void performCalculation(int i, int j) {
        double[][] z = new double[3][3];
        boolean[][] isSet = new boolean[3][3];
        final int foc = 1;
        
        for (int ii = -1; ii <= +1; ii++) {
            for (int jj = -1; jj <= +1; jj++) 
            {
                isSet[foc + ii][foc + jj] = false;
                if (
                    i + ii >= 0 && i + ii < input.numberOfRows() &&
                    j + jj >= 0 && j + jj < input.numberOfColumns()
                   )
                {
                    if (input.isSet(i + ii, j + jj)) {
                        isSet[foc + ii][foc + jj] = true;
                        
                        z[foc + ii][foc + jj] = input.getValue(i + ii, j + jj);
                    }
                }
                // else: Tried to access grid value out of bounds (grid's edge)
            }
        }

        dZdXDone = 0;
        dZdYDone = 0;
        all9PointsAvailable = false;
      
        if (
            isSet[foc + 1][foc - 1] && isSet[foc + 1][foc] && isSet[foc + 1][foc + 1] && 
            isSet[foc][foc - 1] && isSet[foc][foc] && isSet[foc][foc + 1] &&
            isSet[foc - 1][foc - 1] && isSet[foc - 1][foc] && isSet[foc - 1][foc + 1]
           )
        {
            all9PointsAvailable = true;

            // Here, the second order derivative will be the curvature which 
            // includes the planform and profile curvatures. The profile 
            // curvature is parallel to the direction of the maximum slope and 
            // the plan curvature is perpendicular to the direction of maximum 
            // slope. A negative value indicates a convex form; a positive 
            // value indicates a concave form. To compute these values for an
            // elevation-grid, in the following the local 4th order polynomial 
            // f(x, y) = A*x^2*y^2 + B*x^2*y + C*x*y^2 + D*x^2 + E*y^2 + F*x*y + G*x + H*y + I 
            // with the 9 parameters A, B ... I will be fitted through all the 
            // nine points of the 3x3 neighborhood of the focus point. 
                
            double 
                z1 = z[foc + 1][foc - 1],
                z2 = z[foc + 1][foc],
                z3 = z[foc + 1][foc + 1],
                z4 = z[foc][foc - 1],
                z5 = z[foc][foc],
                z6 = z[foc][foc + 1],
                z7 = z[foc - 1][foc - 1],
                z8 = z[foc - 1][foc],
                z9 = z[foc - 1][foc + 1];
           double
                h5 = z5,
                h2_8 = (z2 - z8) / 2.,
                h46 = (z4 + z6) / 2.,
                h28 = (z2 + z8) / 2.,
                h_137_9 = (-z1 + z3 + z7 - z9) / 4.,
                h_46 = (-z4 + z6) / 2.;
            double
                L = Math.sqrt(dX*dX + dY*dY) / Math.sqrt(2.),
                L2 = L * L;
            // Todo warnung falls dx != dy oder stark unterschiedl.
            
            D = (h46 - h5) / L2;
            E = (h28 - h5) / L2;
            F = h_137_9 / L2;
            G = h_46 / L;
            H = h2_8 / L;
        }
            
        if (
            isSet[foc + 1][foc + 1] && isSet[foc + 1][foc - 1] && 
            isSet[foc][foc + 1] && isSet[foc][foc - 1] &&
            isSet[foc - 1][foc + 1] && isSet[foc - 1][foc - 1]
           ) 
        {
            // 1st try: Formula given by Horn (1981) 
            // to estimate "west to east" gradient:
            dZdX = (
                z[foc + 1][foc + 1] - z[foc + 1][foc - 1] +
                2. * (z[foc][foc + 1] - z[foc][foc - 1]) +
                z[foc - 1][foc + 1] - z[foc - 1][foc - 1]
                   ) / (8. * dX);
            dZdXDone = 1;
        }

        if (tryMore) {
            if (dZdXDone <= 0) {
                // 2nd try: Use simpler finite difference
                // to estimate "west to east" gradient:
                if (isSet[foc][foc + 1] && isSet[foc][foc - 1]) 
                {
                    dZdX = (z[foc][foc + 1] - z[foc][foc - 1]) / dX;
                    dZdXDone = 2;   
                }
            }
    
            if (dZdXDone <= 0) {
                // 3rd try:
                if (isSet[foc][foc + 1] && isSet[foc][foc]) 
                {
                    dZdX = (z[foc][foc + 1] - z[foc][foc]) / dX;
                    dZdXDone = 3;   
                }
            }
            
            if (dZdXDone <= 0) {
                // 4th try:
                if (isSet[foc][foc] && isSet[foc][foc - 1]) 
                {
                    dZdX = (z[foc][foc] - z[foc][foc - 1]) / dX;
                    dZdXDone = 4;   
                }
            }
        }
        
        if (
            isSet[foc + 1][foc - 1] && isSet[foc - 1][foc - 1] && 
            isSet[foc + 1][foc] && isSet[foc - 1][foc] &&
            isSet[foc + 1][foc + 1] && isSet[foc - 1][foc + 1]
           ) 
        {
            // 1st try: Formula given by Horn (1981) 
            // to estimate "south to north" gradient:
            dZdY = (
                z[foc + 1][foc - 1] - z[foc - 1][foc - 1] +
                2. * (z[foc + 1][foc] - z[foc - 1][foc]) +
                z[foc + 1][foc + 1] - z[foc - 1][foc + 1]
                   ) / (8. * dY);
            dZdYDone = 1;
        }

        if (tryMore) {
            if (dZdYDone <= 0) {
                // 2nd try: Use simpler finite difference
                // to estimate "south to north" gradient:
                if (
                    isSet[foc + 1][foc] && isSet[foc - 1][foc] 
                   ) 
                {
                    dZdX = (z[foc + 1][foc] - z[foc - 1][foc]) / dY;
                    dZdYDone = 2;   
                }
            }
    
            if (dZdYDone <= 0) {
                // 3rd try:
                if (
                    isSet[foc + 1][foc] && isSet[foc][foc] 
                   ) 
                {
                    dZdX = (z[foc + 1][foc] - z[foc][foc]) / dY;
                    dZdYDone = 3;   
                }
            }
    
            if (dZdYDone <= 0) {
                // 4th try:
                if (
                    isSet[foc][foc] && isSet[foc - 1][foc] 
                   ) 
                {
                    dZdX = (z[foc][foc] - z[foc - 1][foc]) / dY;
                    dZdYDone = 4;   
                }
            }
        }
    }

    public void setTryMore(boolean tryMore) {
        this.tryMore = tryMore;
    }
}
