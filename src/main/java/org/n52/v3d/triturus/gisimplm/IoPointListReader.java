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

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.util.ArrayList;

/**
 * Import of files that contain point coordinates. Here, a special simple ASCII 
 * format us used: Each line of the input files gives x-, y- and z-coordinate
 * separated by a space character (&quot; &quot;).
 * 
 * @author Benno Schmidt
 */
public class IoPointListReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private ArrayList<VgPoint> mPointList = null;

    private VgEnvelope mSpatialFilter = null;

    
    /**
     * File-format type identifier to be used for plain ASCII files holding 
     * XYZ triples.
     */
    public static final String PLAIN = "Plain";

    /**
     * Constructor. As an input parameter, the file format type identifier must
     * be specified. The supported formats are listed below:<br />
     * <ul>
     * <li><i>Plain:</i> ASCII file, x, y and z line by line, separated by space character</li>
     * </ul>
     * @param pFormat Format-string, e.g. "Plain"
     * @see IoPointListReader#PLAIN
     */
    public IoPointListReader(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * sets the format type.
     * 
     * @param pFormat Format-type (e.g. <tt>&quot;Plain&quot;</tt>)
     * @see IoPointListReader#PLAIN
     */
    public void setFormatType(String pFormat)
    {
        mFormat = pFormat;
    }

    /**
     * reads in a set of 3-d points from a file. 
     * 
     * @param pFilename File name (with path optionally)
     * @return {@link ArrayList} consisting of {@link VgPoint} objects
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public ArrayList<VgPoint> readFromFile(String pFilename) 
    		throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("Plain")) {
        	i = 1;
        }
        // --> add more types here...

        try {
            switch (i) {
                case 1: 
                	this.readPlainAscii(pFilename); 
                	break;
                // --> add more types here...

                default: 
                	throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }

        return mPointList;
    }

    private void readPlainAscii(String pFilename) throws T3dException
    {
    	// TODO: Keep configurable: Separator, x-y-z order, skip point-identifiers etc.
        String line = "";
        int lineNumber = 0;

        mPointList = new ArrayList<VgPoint>();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String tok1, tok2, tok3;
            double x, y, z;
            VgPoint pt = null;

            line = lDatRead.readLine();
            while (line != null) {
                lineNumber++;

                tok1 = this.getStrTok(line, 1, " ");
                tok2 = this.getStrTok(line, 2, " ");
                tok3 = this.getStrTok(line, 3, " ");

                x = this.toDouble(tok1);
                y = this.toDouble(tok2);
                z = this.toDouble(tok3);

                pt = new GmPoint(x, y, z);

                if (mSpatialFilter != null) {
                    if (mSpatialFilter.contains(pt))
                        mPointList.add(pt);
                } else
                    mPointList.add(pt);

                line = lDatRead.readLine();
                //if (lineNumber % 1000000 == 0) System.out.println("lineNumber = " + lineNumber);
            }
            lDatRead.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
        }
        //System.out.println("lineNumber = " + lineNumber);
    } // readPlainAscii()

    /**
     * defines a spatial filter. Points outside the given envelope will be ignored 
     * when importing points. If no filter shall be used (i.e., import all points from 
     * the source-file), the filter envelope has to be set to <i>null</i> (which is the 
     * default-value). Note: When using such filters, do not forget to set proper
     * z-values!
     * 
     * @param pFilter Bounding-Box
     */
    public void setSpatialFilter(VgEnvelope pFilter) {
        mSpatialFilter = pFilter;
    }

    /**
     * returns the set spatial filter.
     * 
     * @return 3-D bounding-Box (if a spatial filter is set, else <i>null</i>)
     * @see this{@link #setSpatialFilter(VgEnvelope)}
     */
    public VgEnvelope getSpatialFilter() {
        return mSpatialFilter;
    }

    // private helpers, used by readPlainAscii():

    // Extraction of the i-th token from a string ('sep' as separator):
    private String getStrTok(String str, int i, String sep) throws T3dException
    {
		String[] tok = str.split("[" + sep + "]+");
		if (tok != null) {
			boolean empty1 = tok[0].length() == 0 ? true : false; // preceding empty token 
			if (empty1) {
				if (i < tok.length)
					return tok[i]; 
			} else {
				if (i - 1 < tok.length)
					return tok[i - 1];
			}
		} 
		throw new T3dException("Logical parser error.");
    } 

    // Convert String to floating-point number:
    private double toDouble(String pStr) 
    {
    	pStr = pStr.replaceAll(",", "."); // TODO: ',' as decimal-point
        return Double.parseDouble(pStr);
    } 

    // Convert String to integer:
    private int toInt(String pStr)
    {
        return Integer.parseInt(pStr);
    } 
}
