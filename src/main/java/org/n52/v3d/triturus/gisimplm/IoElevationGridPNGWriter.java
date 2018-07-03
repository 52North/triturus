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
 * 
 * @author Jatzek
 */
public class IoElevationGridPNGWriter extends IoObject {

	private String mFormat="TYPE_USHORT_GRAY";
	private String mLogString = "";
	private Color mNoDataValue = new Color(0, 0, 0);
	
	public String log() {
        return mLogString;
    }

	public void setFormatType(String pFormat) {
        mFormat = pFormat;
    }
	
	public void writeToFile(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException, T3dNotYetImplException

    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("TYPE_USHORT_GRAY")) i = 1;
        // --> hier ggf. weitere Typen ergï¿½nzen...

        try {
            switch (i) {
                case 1: this.writePNG(pGrid, pFilename); break;
                // --> hier ggf. weitere Typen ergï¿½nzen...
                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }

	private void writePNG(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException, T3dNotYetImplException
    {
    	if (pGrid == null)
            throw new T3dException("Grid information not available.");      	
        
    	GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

    	if (Math.abs((lGeom.getDeltaX() - lGeom.getDeltaY()) / lGeom.getDeltaX()) >= 0.001)
    	    throw new T3dException( "Grid requires equal cell-sizes in x- and y-direction." );
    	// if (lGeom.getNumberOfRows() != lGeom.getNumberOfColumns())
        //		throw new T3dNotYetImplException( "Grid must be quadratic." );
        // TODO: erst mal auskommentiert -< kann im Weiteren zu Fehlern fï¿½hren!
		// TODO: Extension in Dateinamen prï¿½fen, muss .png sein!

    	int width = lGeom.numberOfColumns();
		int height = lGeom.numberOfRows();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

        double zMin = pGrid.minimalElevation();
        double zMax = pGrid.maximalElevation();
        double dz = zMax - zMin;
        
        // Pixel den Höhenwerten entsprechend setzen:
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
            	
                if (pGrid.isSet(x, y)){	
                    float greyVal = (float) ((pGrid.getValue(x, y) - zMin) / dz);
                    int p = new Color(greyVal, greyVal, greyVal).getRGB();
        			img.setRGB(x, y, p);
        			
                }
                else 
                {
                	int p = mNoDataValue.getRGB();
                	img.setRGB(x, y, p);
                }
            }
        }
        
        // Bild schreiben (.png):
        try {
        	File file = new File(pFilename);
			ImageIO.write(img, "png", file);
			
		} catch (IOException e) {
			throw new T3dException(e.getMessage());
		}
    }
}
