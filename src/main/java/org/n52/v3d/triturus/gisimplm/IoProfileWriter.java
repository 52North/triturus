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

import java.io.*;
import java.text.DecimalFormat;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgProfile;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Writes cross-section data to a file or stream. Cross-section information, 
 * which inside the Triturus framework is held in {@link VgProfile}-objects, 
 * might be written to ASCII or SVG files. 
 * 
 * @author Benno Schmidt
 */
public class IoProfileWriter extends IoAbstractWriter
{
    private String mLogString = "";
    private String mFormat;

    /**
     * Identifier to render cross-sections to SVG (scalable vector graphics) 
     * documents.
     */
    public static final String SVG = "SVG";
    
    /**
     * Identifier to export cross-section information in ACADGEO format.
     */
    public static final String ACGEO = "AcGeo";

    
    /**
     * Constructor. As an input parameter, the target format type identifier 
     * must be specified. The supported formats are listed below:<br />
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO format for cross-sections</li>
     * <li><i>SVG:</i> SVG file</li>
     * </ul>
     * 
     * @param pFormat Format-string, e.g. <tt>&quot;AcGeo&quot;</tt>
     */
    public IoProfileWriter(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

   /**
     * sets the format type.
     * 
     * @param pFormat Format-string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
     * @see IoProfileWriter#ACGEO
     * @see IoProfileWriter#SVG
     */
   public void setFormatType(String pFormat) {
        mFormat = pFormat;
    }

    /**
     * writes cross-section data to a file.
     * 
     * @param pProfile Cross-section to be written
     * @param pFilename File path
     * @throws T3dException for framework-specific errors
     */
    public void writeToFile(VgProfile pProfile, String pFilename) 
    	throws T3dException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase(ACGEO)) i = 1;
        if (mFormat.equalsIgnoreCase(SVG)) i = 2;
        // --> add more types here...

        try {
            switch (i) {
                case 1: this.writeAcadGeoProfile(pProfile, pFilename); break;
                case 2: this.writeSVG(pProfile, pFilename); break;
                // --> add more types here...

                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }  

    private void writeAcadGeoProfile(VgProfile pProfile, String pFilename) 
    	throws T3dException
    {
        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            lDat.write("PROFILE:");
            lDat.newLine();

            // Write the stations:
            VgLineString pDefLine = (VgLineString) pProfile.getGeometry();
            DecimalFormat dfXY = this.getDecimalFormatXY();
            lDat.write("STATIONS");
            lDat.newLine();
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                lDat.write(dfXY.format(t));
                lDat.newLine();
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    lDat.write(dfXY.format(t));
                    lDat.newLine();
                    last = pDefLine.getVertex(i);
                }                
            }

            // Write the z-values:
            DecimalFormat dfZ = this.getDecimalFormatZ();
            lDat.write("DATA");
            lDat.newLine();
            lDat.write("NAME unnamed");
            lDat.newLine();
            for (int i = 0; i < pProfile.numberOfTZPairs(); i++) {
                lDat.write(dfXY.format((pProfile.getTZPair(i))[0]));
                lDat.write(" ");
                lDat.write(dfZ.format((pProfile.getTZPair(i))[1]));
                lDat.newLine();
            }

            lDat.write("END");
            lDat.newLine();

            lDat.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (Throwable e) {
            throw new T3dException(e.getMessage());
        }
    } // writeAcadGeoProfile()

    private void writeSVG(VgProfile pProfile, String pFilename) 
    	throws T3dException
    {
        int lImageWidth = 500;      // TODO better set from outside
        int lImageHeight = 300;     // TODO better set from outside
        int lImageBorder = 10;
        int lAddInfoHeight1 = 30;
        int lAddInfoHeight2 = 20;
        int lZAnnotWidth = 30;
        int lTAnnotHeight = 30;

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            lDat.write("<?xml version=\"1.0\" standalone=\"no\"?>");
            lDat.newLine();
            lDat.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"");
            lDat.write(" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
            lDat.newLine();
            lDat.write("<svg width=\"" + lImageWidth + "px\" height=\"" + lImageHeight + "px\" version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xml:space=\"preserve\">");
            lDat.write(" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">");
            lDat.newLine();
            lDat.write("  <desc>Triturus profile export</desc>");
            lDat.newLine();
            lDat.write("  <rect width=\"" + lImageWidth + "\" height=\"" + lImageHeight + "\" style=\"fill:rgb(255,255,255)\"/>");
            lDat.newLine();

            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            this.setCanvasPrf( // Profil-Canvas
                lImageBorder + lZAnnotWidth, // xMin
                lImageWidth - lImageBorder, // xMax
                lImageBorder + lAddInfoHeight1, // yMin
                lImageHeight - lImageBorder - lAddInfoHeight2 - lTAnnotHeight); // yMax
            this.setTZRange(
                pProfile.tStart(), pProfile.tEnd(), pProfile.zMin(), pProfile.zMax());

            double[] zLevels = this.calculateZLevels(mZMin, mZMax);
            if (zLevels != null)
                this.setTZRange(
                    pProfile.tStart(), pProfile.tEnd(), zLevels[0], zLevels[zLevels.length - 1]);

            // Box top (headline):
            lDat.write("  <rect x=\"" + lImageBorder + "\" y=\"" + lImageBorder + "\" ");
            lDat.write("width=\"" + (mCanvPrfXMax - lImageBorder) + "\" ");
            lDat.write("height=\"" + lAddInfoHeight1 + "\" style=\"fill:rgb(200,200,200)\" rx=\"0\" ry=\"0\"/>");
            lDat.newLine();
            lDat.write("  <g style=\"font-family:sans-serif;font-size:14;fill:rgb(0,0,40)\">"); 
            lDat.newLine();
            lDat.write("    <text x=\"" + (lImageBorder + 6) + "\" y=\"" + (lImageBorder + 6 + 14) + "\">");
            lDat.write("Triturus cross-section profile</text>");  // todo von au�en setzbar machen, z. B. sdi.suite terrainServer cross-section profile
            lDat.newLine();
            lDat.write("  </g>");
            lDat.newLine();

            // Box for text annotations:
            lDat.write("  <rect x=\"" + lImageBorder + "\" y=\"" + mCanvPrfYMin + "\" ");
            lDat.write("width=\"" + lZAnnotWidth + "\" ");
            lDat.write("height=\"" + (mCanvPrfYMax - mCanvPrfYMin) + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");
            lDat.newLine();
            lDat.write("  <rect x=\"" + mCanvPrfXMin + "\" y=\"" + mCanvPrfYMax + "\" ");
            lDat.write("width=\"" + (mCanvPrfXMax - mCanvPrfXMin) + "\" ");
            lDat.write("height=\"" + lTAnnotHeight + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");
            lDat.newLine();
            lDat.write("  <rect x=\"" + lImageBorder + "\" y=\"" + mCanvPrfYMax + "\" ");
            lDat.write("width=\"" + lZAnnotWidth + "\" ");
            lDat.write("height=\"" + lTAnnotHeight + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");
            lDat.newLine();

            // Box bottom (copyright):
            lDat.write("  <rect x=\"" + lImageBorder + "\" y=\"" + (mCanvPrfYMax + lTAnnotHeight) + "\" ");
            lDat.write("width=\"" + (mCanvPrfXMax - lImageBorder) + "\" ");
            lDat.write("height=\"" + lAddInfoHeight2 + "\" style=\"fill:rgb(200,200,200)\" rx=\"0\" ry=\"0\"/>");
            lDat.newLine();
            lDat.write("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
            lDat.newLine();
            lDat.write("    <text x=\"" + (lImageBorder + 6) + "\" y=\"" + (mCanvPrfYMax + lTAnnotHeight + 14) + "\">");
            lDat.write("Generated by 52N Triturus</text>");
            lDat.newLine();
            lDat.write("  </g>");
            lDat.newLine();

            // Stations (vertical lines):
            VgLineString pDefLine = (VgLineString) pProfile.getGeometry();
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                lDat.write("  <line ");
                lDat.write("x1=\"" + this.transformT(t) + "\" y1=\"" + mCanvPrfYMin + "\" ");
                lDat.write("x2=\"" + this.transformT(t) + "\" y2=\"" + mCanvPrfYMax + "\" ");
                lDat.write("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                lDat.newLine();
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    lDat.write("  <line ");
                    lDat.write("x1=\"" + this.transformT(t) + "\" y1=\"" + mCanvPrfYMin + "\" ");
                    lDat.write("x2=\"" + this.transformT(t) + "\" y2=\"" + mCanvPrfYMax + "\" ");
                    lDat.write("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                    lDat.newLine();
                    last = pDefLine.getVertex(i);
                }                
            }

            // Elevation levels (horizontal lines):
            if (zLevels != null) {
                for (int i = 0; i < zLevels.length; i++) {
                    lDat.write("  <line ");
                    lDat.write("x1=\"" + mCanvPrfXMin + "\" y1=\"" + this.transformZ(zLevels[i]) + "\" ");
                    lDat.write("x2=\"" + mCanvPrfXMax + "\" y2=\"" + this.transformZ(zLevels[i]) + "\" ");
                    lDat.write("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                    lDat.newLine();
                }
            }

            // Annotation t-axis:
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                lDat.write("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                lDat.newLine();
                lDat.write("    <text writing-mode=\"tb-rl\" x=\"" + (this.transformT(0) + 5) + "\" y=\"" + (mCanvPrfYMax + 3) + "\">");
                lDat.write("0 m</text>");
                lDat.newLine();
                lDat.write("  </g>");
                lDat.newLine();
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    last = pDefLine.getVertex(i);
                    lDat.write("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                    lDat.newLine();
                    lDat.write("    <text writing-mode=\"tb-rl\" x=\"" + (this.transformT(t) - 5) + "\" y=\"" + (mCanvPrfYMax + 3) + "\">");
                    lDat.write("" + Math.round(this.transformT(t)) + "</text>");
                    lDat.newLine();
                    lDat.write("  </g>");
                    lDat.newLine();
                }
            }

            // Annotations z-axis:
             if (zLevels != null) {
                for (int i = 0; i < zLevels.length; i++) {
                    String zText = "" + (int) Math.round((float) zLevels[i]);
                    lDat.write("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                    lDat.newLine();
                    lDat.write("    <text text-anchor=\"end\" x=\"" + (lImageBorder + lZAnnotWidth - 4) + "\" y=\"" + (this.transformZ(zLevels[i]) + 8) + "\">");
                    lDat.write(zText + "</text>");
                    lDat.newLine();
                    lDat.write("  </g>");
                    lDat.newLine();
                }
            }

            // Profile path z(t):
            lDat.write("  <path d=\"");
            lDat.newLine();
            boolean first = true;
            for (int i = 0; i < pProfile.numberOfTZPairs(); i++) {
                float x = this.transformT((pProfile.getTZPair(i))[0]);
                float y = this.transformZ((pProfile.getTZPair(i))[1]);
                if (first) {
                    lDat.write("M"); first = false;
                } else
                    lDat.write(" L"); 
                lDat.write("" + dfXY.format(x) + " " + dfZ.format(y)); 
            }
            lDat.write("\" style=\"stroke:rgb(255,0,0);fill:none\"/>");
            lDat.newLine();

            lDat.write("</svg>");
            lDat.newLine();

            lDat.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (Throwable e) {
            throw new T3dException(e.getMessage());
        }
    } // writeSVG()
    
    private double mTMin, mTMax, mZMin, mZMax;

    private void setTZRange(
    		double pTMin, double pTMax, 
    		double pZMin, double pZMax) 
    {
        mTMin = pTMin; mTMax = pTMax; mZMin = pZMin; mZMax = pZMax;
    }

    private float mCanvPrfXMin, mCanvPrfXMax, mCanvPrfYMin, mCanvPrfYMax;

    private void setCanvasPrf(
    		float pXMin, float pXMax, 
    		float pYMin, float pYMax) 
    {
        mCanvPrfXMin = pXMin; mCanvPrfXMax = pXMax; mCanvPrfYMin = pYMin; mCanvPrfYMax = pYMax;
    }

    private float transformT(double t) {
        return mCanvPrfXMin + (mCanvPrfXMax - mCanvPrfXMin) * (float)(t/mTMax);
    }

    private float transformZ(double z) {
        return mCanvPrfYMax + (mCanvPrfYMin - mCanvPrfYMax) * (float)((z - mZMin) / (mZMax - mZMin));
    }

    // Compute horizontal lines:
    private double[] calculateZLevels(double pZMin, double pZMax) 
    {
    	double[] res = null;

        boolean lSimpleTest = false;
        if (lSimpleTest) {
            res = new double[11];
            for (int i = 0; i <= 10; i++) {
                double z = mZMin + (double)i / 10. * (mZMax - mZMin);
                res[i] = z;
            }
        }
        else {
            int lNumberOfLevels = 0;
            int lDoubledNumberOfZeroDigits = 7; 
            	// unpaired values for 5'er-steps
            int lMin = 0, lMax = 0;
            int lDiv = 0;
            do {
                lDoubledNumberOfZeroDigits--;
                lDiv = 1;
                for (int i = 0; i < lDoubledNumberOfZeroDigits / 2; i++)
                    lDiv *= 10;
                if (lDoubledNumberOfZeroDigits % 2 == 1)
                    lDiv *= 5;
                lMin = this.roundLower(pZMin, lDiv);
                lMax = this.roundUpper(pZMax, lDiv);
                lNumberOfLevels = (lMax - lMin) / lDiv + 1;

                if (lNumberOfLevels >= 5)
                    break;
                if (lDoubledNumberOfZeroDigits <= 0)
                    break;
            }
            while (true);

            if (lNumberOfLevels >= 5) {
                res = new double[lNumberOfLevels];
                for (int i = 0; i < lNumberOfLevels; i++) {
                    double z = lMin + (double) i * lDiv;
                    res[i] = z;
                }
            }
            else {
                lMin = this.roundLower(pZMin, 1);
                lMax = this.roundUpper(pZMax, 1);
                res = new double[2];
                res[0] = lMin;
                res[1] = lMax;
            }
        }
        return res;
    }

    private int roundLower(double pZ, int pDiv) {
        return pDiv * Math.round((float) Math.floor(pZ / pDiv));
    }

    private int roundUpper(double pZ, int pDiv) {
        return pDiv * Math.round((float) Math.ceil(pZ / pDiv));
    }
}
