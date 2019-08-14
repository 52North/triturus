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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

// TODO Test application is still missing.

/**
 * The <tt>IoElevationGridPNGWriter</tt> provides a method to write an 
 * elevation grid (of type {@link GmSimpleElevationGrid} to a PNG file. 
 *
 * @author Nico Jatzek
 */
public class IoElevationGridPNGWriter extends IoObject 
{
	private String mFormat = "TYPE_USHORT_GRAY";
	private String mLogString = "";
	private Color mNoDataValue = new Color(0, 0, 0);
	
    /**
     * File-format type identifier 
     */
    public static final String TYPE_USHORT_GRAY = "TYPE_USHORT_GRAY";
    
    
    /**
     * Constructor. As an input parameter, the file format type identifier must
     * be specified. The supported formats are listed below:<br />
     * <ul>
     * <li><i>TYPE_USHORT_GRAY:</i> PNG USHORT_GRAY format</li>
     * </ul><p>
     *
     * @param format Format-string, e.g. <tt>&quot;TYPE_USHORT_GRAY&quot;</tt>
     * @see IoElevationGridPNGWriter#TYPE_USHORT_GRAY
     */
    public IoElevationGridPNGWriter(String format) {
        mLogString = this.getClass().getName();
        this.setFormatType(format);
    }
    
	public String log() {
	    return mLogString;
	}

	public void setFormatType(String format) {
	    mFormat = format;
	}
	
	public void writeToFile(GmSimpleElevationGrid grid, String filename) 
			throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase(TYPE_USHORT_GRAY)) i = 1;
        // --> if necessary, add more types here...

        try {
            switch (i) {
                case 1: this.writePNG(grid, filename); break;
                // --> if necessary, add more types here...
                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }

	private void writePNG(GmSimpleElevationGrid grid, String filename) 
			throws T3dException, T3dNotYetImplException
    {
    	if (grid == null)
            throw new T3dException("Grid information not available.");      	
        
    	GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

		if (Math.abs((geom.getDeltaX() - geom.getDeltaY()) / geom.getDeltaX()) >= 0.001)
		    throw new T3dException( "Grid requires equal cell-sizes in x- and y-direction." );
		// TODO: Check file name extension, must be .png!

		int width = geom.numberOfColumns();
		int height = geom.numberOfRows();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

        double zMin = grid.minimalElevation();
        double zMax = grid.maximalElevation();
        double dz = zMax - zMin;
      
        // Set pixel values according to elevation values:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	
                if (grid.isSet(height - y - 1, x)) {	
					float greyVal = (float) ((grid.getValue(height - y - 1, x) - zMin) / dz);
					int p = new Color(greyVal, greyVal, greyVal).getRGB();
					img.setRGB(x, y, p);
                }
                else {
					int p = mNoDataValue.getRGB();
					img.setRGB(x, y, p);
                }
            }
        }
        
        // Write image file (.png):
        try {
			File file = new File(filename);
			ImageIO.write(img, "png", file);			
		} catch (IOException e) {
			throw new T3dException(e.getMessage());
		}
    }
}
