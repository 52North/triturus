/**
 * Copyright (C) 2007-2020 52North Initiative for Geospatial Open Source
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

import java.io.*;
import java.text.DecimalFormat;

import org.n52.v3d.triturus.core.IoFormatType;
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
    /**
     * Identifier to render cross-sections to SVG (scalable vector graphics) 
     * documents.
     */
    public static final String SVG = "SVG";
    
    private String logString = "";
    private String format;
    private BufferedWriter doc;

    /**
     * Constructor. As an input parameter, the target format type identifier 
     * must be specified. The supported formats are listed below:<br/>
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO format for cross-sections</li>
     * <li><i>SVG:</i> SVG file</li>
     * </ul>
     * 
     * @param format Format-string, e.g. <tt>&quot;AcGeo&quot;</tt>
     */
    public IoProfileWriter(String format) { 
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

   /**
     * sets the format type.
     * 
     * @param format Format-string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
	 * @see IoFormatType#ACGEO
	 * @see IoProfileWriter#SVG
     */
	public void setFormatType(String format) {
		this.format = format;
	}

    /**
     * writes cross-section data to a file.
     * 
     * @param profile Cross-section to be written
     * @param filename File path
     * @throws T3dException for framework-specific errors
     */
    public void writeToFile(VgProfile profile, String filename) 
    	throws T3dException
    {
        int i = 0;
        if (format.equalsIgnoreCase(IoFormatType.ACGEO)) i = 1;
        if (format.equalsIgnoreCase(SVG)) i = 2;
        // --> add more types here...

        try {
            switch (i) {
                case 1: this.writeAcadGeoProfile(profile, filename); break;
                case 2: this.writeSVG(profile, filename); break;
                // --> add more types here...

                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }  

    private void writeAcadGeoProfile(VgProfile profile, String filename) 
    	throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));

            wl("PROFILE:");

            // Write the stations:
            VgLineString pDefLine = (VgLineString) profile.getGeometry();
            DecimalFormat dfXY = this.getDecimalFormatXY();
            wl("STATIONS");
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                wl(dfXY.format(t));
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    wl(dfXY.format(t));
                    last = pDefLine.getVertex(i);
                }                
            }

            // Write the z-values:
            DecimalFormat dfZ = this.getDecimalFormatZ();
            wl("DATA");
            wl("NAME unnamed");
            for (int i = 0; i < profile.numberOfTZPairs(); i++) {
            	Double 
            		t = profile.getTZPair(i)[0],
            		z = profile.getTZPair(i)[1];
            	if (t == null || z == null) {
                    t = 0.;
                    z = 0.; // since ACADGEO profile files can not handle no-data values
            	} 
                w(dfXY.format(t));
                w(" ");
                wl(dfZ.format(z));
            }

            wl("END");
            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (Throwable e) {
            throw new T3dException(e.getMessage());
        }
    } // writeAcadGeoProfile()

    public SVGParameters SVGParams = new SVGParameters();
    public class SVGParameters {
    	public int 
    		imageWidth = 500, 
    		imageHeight = 300, 
    		imageBorder = 10, 
    		addInfoHeight1 = 30,
    		addInfoHeight2 = 20,
    		zAnnotWidth = 30,
    		tAnnotHeight = 30;
    	public String
    		titleText = "Cross-section",
    		infoText = "Generated by 52N Triturus";
    }
    
    private void writeSVG(VgProfile profile, String filename) 
    	throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));

            wl("<?xml version=\"1.0\" standalone=\"no\"?>");
            wl("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"" +
            	" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
            wl("<svg " + 
            	" width=\"" + SVGParams.imageWidth + "px\"" + 
            	" height=\"" + SVGParams.imageHeight + "px\"" + 
            	" id=\"Layer_1\"" + 
            	" version=\"1.1\"" + 
            	" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" + 
            	" xml:space=\"preserve\"" +
            	" xmlns=\"http://www.w3.org/2000/svg\">");
            wl("  <desc>Triturus profile export</desc>");
            wl("  <rect" + 
            	" width=\"" + SVGParams.imageWidth + "\"" + 
            	" height=\"" + SVGParams.imageHeight + "\"" + 
            	" style=\"fill:rgb(255,255,255)\"/>");

            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            this.setCanvasPrf( // profile canvas
            	SVGParams.imageBorder + SVGParams.zAnnotWidth, // xmin
            	SVGParams.imageWidth - SVGParams.imageBorder, // xmax
            	SVGParams.imageBorder + SVGParams.addInfoHeight1, // ymin
            	SVGParams.imageHeight - SVGParams.imageBorder 
            		- SVGParams.addInfoHeight2 - SVGParams.tAnnotHeight); // ymax
            this.setTZRange(
                profile.tStart(), profile.tEnd(), profile.zMin(), profile.zMax());

            double[] zLevels = this.calculateZLevels(mZMin, mZMax);
            if (zLevels != null)
                this.setTZRange(
                    profile.tStart(), profile.tEnd(), zLevels[0], zLevels[zLevels.length - 1]);

            // Box top (headline):
            w("  <rect x=\"" + SVGParams.imageBorder + "\" y=\"" + SVGParams.imageBorder + "\" ");
            w("width=\"" + (mCanvPrfXMax - SVGParams.imageBorder) + "\" ");
            w("height=\"" + SVGParams.addInfoHeight1 + "\""); 
            w(" style=\"fill:rgb(200,200,200)\" rx=\"0\" ry=\"0\"/>");
            wl();
            wl("  <g style=\"font-family:sans-serif;font-size:14;fill:rgb(0,0,40)\">"); 
            w(
            	"    <text" + 
            	" x=\"" + (SVGParams.imageBorder + 6) + "\"" + 
            	" y=\"" + (SVGParams.imageBorder + 6 + 14) + "\">" +
            	SVGParams.titleText +
            	"</text>");
            wl("  </g>");

			// Box for text annotations:
            w("  <rect x=\"" + SVGParams.imageBorder + "\" y=\"" + mCanvPrfYMin + "\" ");
            w("width=\"" + SVGParams.zAnnotWidth + "\" ");
            w("height=\"" + (mCanvPrfYMax - mCanvPrfYMin) + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");
            wl();
            w("  <rect x=\"" + mCanvPrfXMin + "\" y=\"" + mCanvPrfYMax + "\" ");
            w("width=\"" + (mCanvPrfXMax - mCanvPrfXMin) + "\" ");
            w("height=\"" + SVGParams.tAnnotHeight + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");
            wl();
            w("  <rect x=\"" + SVGParams.imageBorder + "\" y=\"" + mCanvPrfYMax + "\" ");
            w("width=\"" + SVGParams.zAnnotWidth + "\" ");
            wl("height=\"" + SVGParams.tAnnotHeight + "\" style=\"fill:rgb(227,227,227)\" rx=\"0\" ry=\"0\"/>");

            // Box bottom (copyright):
            w("  <rect x=\"" + SVGParams.imageBorder + "\" y=\"" + (mCanvPrfYMax + SVGParams.tAnnotHeight) + "\" ");
            w("width=\"" + (mCanvPrfXMax - SVGParams.imageBorder) + "\" ");
            w("height=\"" + SVGParams.addInfoHeight2 + "\" style=\"fill:rgb(200,200,200)\" rx=\"0\" ry=\"0\"/>");
            wl();
            wl("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
            w(
            	"    <text" + 
            	" x=\"" + (SVGParams.imageBorder + 6) + "\"" + 
            	" y=\"" + (mCanvPrfYMax + SVGParams.tAnnotHeight + 14) + "\">" +
            	SVGParams.infoText + "</text>");
            wl("  </g>");

            // Stations (vertical lines):
            VgLineString pDefLine = (VgLineString) profile.getGeometry();
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                w("  <line ");
                w("x1=\"" + this.transformT(t) + "\" y1=\"" + mCanvPrfYMin + "\" ");
                w("x2=\"" + this.transformT(t) + "\" y2=\"" + mCanvPrfYMax + "\" ");
                wl("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    w("  <line ");
                    w("x1=\"" + this.transformT(t) + "\" y1=\"" + mCanvPrfYMin + "\" ");
                    w("x2=\"" + this.transformT(t) + "\" y2=\"" + mCanvPrfYMax + "\" ");
                    wl("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                    last = pDefLine.getVertex(i);
                }                
            }
            
            // Elevation levels (horizontal lines):
            if (zLevels != null) {
                for (int i = 0; i < zLevels.length; i++) {
                    w("  <line ");
                    w("x1=\"" + mCanvPrfXMin + "\" y1=\"" + this.transformZ(zLevels[i]) + "\" ");
                    w("x2=\"" + mCanvPrfXMax + "\" y2=\"" + this.transformZ(zLevels[i]) + "\" ");
                    wl("style=\"stroke:rgb(0,255,0);fill:none\"/>");
                }
            }

            // Annotation t-axis:
            if (pDefLine.numberOfVertices() > 0) {
                double t = 0.;
                wl("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                w(
                	"    <text writing-mode=\"tb-rl\"" + 
                	" x=\"" + (this.transformT(0) + 5) + "\"" + 
                	" y=\"" + (mCanvPrfYMax + 3) + 
                	"\">0 m</text>");
                wl("  </g>");
                VgPoint last = pDefLine.getVertex(0);
                for (int i = 1; i < pDefLine.numberOfVertices(); i++) {
                    VgPoint curr = pDefLine.getVertex(i);
                    t += curr.distanceXY(last);
                    last = pDefLine.getVertex(i);
                    wl("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                    w(
                    	"    <text writing-mode=\"tb-rl\"" + 
                    	" x=\"" + (this.transformT(t) - 5) + "\"" + 
                    	" y=\"" + (mCanvPrfYMax + 3) + 
                    	"\">" + Math.round(t) + "</text>");
                    wl("  </g>");
                }
            }

            // Annotations z-axis:
             if (zLevels != null) {
                for (int i = 0; i < zLevels.length; i++) {
                    String zText = "" + (int) Math.round((float) zLevels[i]);
                    wl("  <g style=\"font-family:sans-serif;font-size:10;fill:rgb(0,0,40)\">");
                    w("    <text text-anchor=\"end\" x=\"" + (SVGParams.imageBorder + SVGParams.zAnnotWidth - 4) + "\" y=\"" + (this.transformZ(zLevels[i]) + 8) + "\">");
                    wl(zText + "</text>");
                    wl("  </g>");
                }
            }

            // Profile path z(t):
            wl("  <path d=\"");
            boolean first = true;
            for (int i = 0; i < profile.numberOfTZPairs(); i++) {
            	Double 
            		t = profile.getTZPair(i)[0],
            		z = profile.getTZPair(i)[1];
        		if (t != null && z != null) {
        			float x = this.transformT(t);
	                float y = this.transformZ(z);
	                if (first) {
	                    w("M"); first = false;
	                } else
	                    w(" L"); 
	                w("" + dfXY.format(x) + " " + dfZ.format(y)); 
        		} else {
        			// do not draw holes (unset values) in the path 
        			first = true;
        			//wl();
        		}
            }
            wl("\"");
            wl("   style=\"stroke:rgb(255,0,0);fill:none\"/>");

            wl("</svg>");
            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (Throwable e) {
            throw new T3dException(e.getMessage());
        }
    } // writeSVG()
    
    @SuppressWarnings("unused")
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
    
    private void w(String line) {
        try {
            doc.write(line);
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private void wl(String line) {
        try {
            doc.write(line);
            doc.newLine();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }

    private void wl() {
        try {
            doc.newLine();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
    }
}
