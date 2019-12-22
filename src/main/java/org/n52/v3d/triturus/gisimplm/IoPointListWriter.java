/**
 * Copyright (C) 2007-2019 52 North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.util.List;

/**
 * Export of files that contain point coordinates. 
 * 
 * @author Benno Schmidt
 */
public class IoPointListWriter extends IoObject
{
    private String logString = "";
    private String format;

    
    /**
     * File-format type identifier to be used for plain ASCII files holding 
     * x-y-z triples. Each line of the target file gives x-, y- and 
     * z-coordinate separated by a space character (&quot; &quot;).
     */
    public static final String PLAIN = "Plain";
    /**
     * File-format type identifier to be used for plain ASCII files holding 
     * x-y-z triples. Each line of the target file gives x-, y- and 
     * z-coordinate separated by a comma character (&quot;,&quot;).
     */
    public static final String CSV = "CSV";

    /**
     * Constructor. As an input parameter, the file format type identifier must
     * be specified. The supported formats are listed below:<br />
     * <ul>
     * <li><i>Plain:</i> ASCII file, x, y and z line by line, separated by space character</li>
     * <li><i>CSV:</i> ASCII file, x, y and z line by line, separated by comma character</li>
     * </ul>
     * @param pFormat Format-string, e.g. "Plain"
     * @see IoPointListWriter#PLAIN
     */
    public IoPointListWriter(String format) {
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

    /** 
     * sets the format type.
     * 
     * @param pFormat Format-type (e.g. <tt>&quot;Plain&quot;</tt>)
     * @see IoPointListWriter#PLAIN
     */
    public void setFormatType(String format)
    {
        this.format = format;
    }

    /**
     * writes a set of 3-d points to a file. 
     * 
     * @param {@link List} consisting of {@link VgPoint} objects to be written
     * @param filename File name (with path optionally)
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void writeToFile(List<VgPoint> points, String filename) 
    	throws T3dException
    {
        int i = 0;
        if (format.equalsIgnoreCase("Plain")) {
        	i = 1;
        }
        if (format.equalsIgnoreCase("CSV")) {
        	i = 2;
        }
        // --> add more types here...

        try {
            switch (i) {
                case 1: 
                	this.writePlainAscii(points, filename, false); 
                	break;
                case 2: 
                	this.writePlainAscii(points, filename, true); 
                	break;
                // --> add more types here...

                default: 
                	throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }

    private void writePlainAscii(
    	List<VgPoint> points, String filename, boolean commaSeparated) 
    	throws T3dException
    {
    	// TODO: Keep configurable: Separator, x-y-z order, number of written digits,
    	// bounding-box filter, skip point-identifiers etc.
        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(filename));
            String sep = commaSeparated ? "," : " ";
            for (VgPoint p : points) {
                lDat.write(p.getX() + sep + p.getY() + sep + p.getZ());
                lDat.newLine();           	
            }
            lDat.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (Throwable e) {
            throw new T3dException(e.getMessage());
        }        
    } 
}
