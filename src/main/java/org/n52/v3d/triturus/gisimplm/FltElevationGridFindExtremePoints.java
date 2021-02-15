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

import java.util.ArrayList;
import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Filter to extract characteristic point features such as local and global 
 * elevation minima and maxima from an elevation grid.
 *   
 * @author Benno Schmidt
 */
public class FltElevationGridFindExtremePoints extends T3dProcFilter
{
    /**
     * Supported terrain analysis types are given in the table below.<br/>
     * <br/>
     * <table border="1">
     *   <tr>
     *     <th>Enumeration value</td>
     *     <th>Performed analysis</td>
     *     <th>Result attribute</td>
     *     <th>Data range</td>
     *   </tr>
     *   <tr>
     *     <td>LOC_MIN</td>
     *     <td>
     *       Extract local minima. As an attribute value, the size <i>N</i> 
     *       of <i>N</i>x<i>N</i> window with all elevations greater or equal 
     *       than the found minimum around the found point will be given.
     *     </td>
     *     <td>CATEGORY</td>
     *     <td>&gt;0</td>
     *   </tr>
     *   <tr>
     *     <td>LOC_MAX</td>
     *     <td>
     *       Extract local maxima. As an attribute value, the size <i>N</i> 
     *       of <i>N</i>x<i>N</i> window with all elevations less or equal 
     *       than the found maximum around the found point will be given.
     *     </td>
     *     <td>CATEGORY</td>
     *     <td>&gt; 0</td>
     *   </tr>
     *   <tr>
     *     <td>GLOBAL_EXTR</td>
     *     <td>
     *       Extract global minima and maxima within the elevation grid. 
     *     </td>
     *     <td>EXTR_TYPE</td>
     *     <td>+1 for maximum, -1 for minimum</td>
     *   </tr>
     * </table>
     */
    public enum AnalysisMode { 
        LOC_MIN, LOC_MAX, GLOBAL_EXTR
    };
    private AnalysisMode mode = AnalysisMode.GLOBAL_EXTR;
    
    private String logString = "";

    private GmSimpleElevationGrid g; 
    private List<VgAttrFeature> output;
    
    
    public FltElevationGridFindExtremePoints() {
        logString = this.getClass().getName();
    }
                                                                                     
    public String log() {
        return logString;
    }

    public FltElevationGridFindExtremePoints(AnalysisMode analysisType) {
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
     * finds extrema inside the given elevation grid.
     *
     * @param grid Input grid
     * @return Resulting point set
     * @throws T3dException
     */
    public List<VgAttrFeature> transform(GmSimpleElevationGrid grid) 
        throws T3dException
    {
        g = grid;
        if (g == null) 
            throw new T3dException("Received null pointer as input grid.");
        int 
            nCols = g.numberOfColumns(),
            nRows = g.numberOfRows();   

        String theme;
        switch (mode) {
            // Note: To ease VTK export, in the following usage of space 
            // characters in object names has been avoided. 
            case LOC_MIN:
                theme = "Local_minima";
                break;
            case LOC_MAX:
                theme = "Local_maxima"; 
                break;                      
            case GLOBAL_EXTR: 
                theme = "Global_extrema";
                break;
            default:
                throw new T3dException("Received unknown mode directive.");
        }

        double zMin = -42., zMax = 42.; // initial dummy values 
        List<VgPoint> 
            globMin = new ArrayList<VgPoint>(), 
            globMax = new ArrayList<VgPoint>();
        output = new ArrayList<VgAttrFeature>();
                
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                switch (mode) {
                    case LOC_MIN:
                        if (isLocalExtremum(i, j, true)) {
                            int size = determineCategory(i, j, true);
                            output.add(generateFeature(g.getPoint(i, j), "CATEGORY", size));
                        }
                        break;
                    case LOC_MAX:
                        if (isLocalExtremum(i, j, false)) {
                            int size = determineCategory(i, j, false);
                            output.add(generateFeature(g.getPoint(i, j), "CATEGORY", size));                            
                        }
                        break;
                    case GLOBAL_EXTR:
                        if (g.isSet(i, j)) {
                            double z = g.getValue(i, j);
                            if (globMin.size() == 0) {
                                zMin = z;
                                globMin.add(g.getPoint(i, j));
                            } 
                            else {
                                if (z <= zMin) {
                                    if (z < zMin) globMin.clear();
                                    zMin = z;
                                    globMin.add(g.getPoint(i, j));
                                }
                            }                   
                            if (globMax.size() == 0) {
                                zMax = z;
                                globMax.add(g.getPoint(i, j));
                            } 
                            else {
                                if (z >= zMax) {
                                    if (z > zMax) globMax.clear();
                                    zMax = z;
                                    globMax.add(g.getPoint(i, j));
                                }
                            }                   
                        }
                        break;
                }
            }
        }

        switch (mode) {
            case LOC_MIN: case LOC_MAX:
                break;                      
            case GLOBAL_EXTR:
                for (VgPoint extr : globMin) {
                    output.add(generateFeature(extr, "EXTR_TYPE", -1));
                }
                for (VgPoint extr : globMax) {
                    output.add(generateFeature(extr, "EXTR_TYPE", +1));
                }
                break;
        }

        if (output.size() == 0)
            output = null;
        return output;
    }

    private boolean isLocalExtremum(int i, int j, boolean minMode) 
    {
        if (!g.isSet(i, j)) 
            return false;
        
        double zExtr = g.getValue(i, j);
        
        for (int ii = i - 1; ii <= i + 1; ii++) {
            for (int jj = j - 1; jj <= j + 1; jj++) {
                if (ii == i && jj == j)
                    continue;
                if (
                    ii >= 0 && ii < g.numberOfRows() && 
                    jj >= 0 && jj < g.numberOfColumns()) 
                {
                    if (g.isSet(ii, jj)) {
                        double z = g.getValue(ii, jj);
                        if (minMode && z < zExtr) 
                            return false;
                        if ((!minMode) && z > zExtr) 
                            return false;
                    }                       
                }
            }           
        }       
        return true;
    }

    private int determineCategory(int i, int j, boolean minMode) {
        double zExtr = g.getValue(i, j);
        int size;
        for (size = 1; size < g.numberOfColumns() && size < g.numberOfColumns(); size++) {
            for (int ii = i - size; ii <= i + size; ii++) {
                for (int jj = j - size; jj <= j + size; jj++) {
                    if (ii == i && jj == j)
                        continue;
                    if (
                        ii >= 0 && ii < g.numberOfRows() && 
                        jj >= 0 && jj < g.numberOfColumns()) 
                    {
                        if (g.isSet(ii, jj)) {
                            double z = g.getValue(ii, jj);
                            if (minMode && z < zExtr) 
                                return size - 1;
                            if ((!minMode) && z > zExtr) 
                                return size - 1;
                        }                       
                    }
                }           
            }       
        }       
        return size;
    }

    private VgAttrFeature generateFeature(VgPoint pt, String attr, int val) {
        GmAttrFeature feat = new GmAttrFeature();
        feat.setGeometry(pt);
        feat.addAttribute(attr, "java.lang.Integer", val);
        return feat;
    }
}
