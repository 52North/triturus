/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.vispovray;

import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vispovray.gifwriter.GifEncodedRaster;

import java.io.*;
import java.awt.*;

/**
 * POV-Ray-specific implementation to write a grid-based elevation models (type <tt>GmSimpleElevationGrid</tt>) to a GIF
 * file.<br /><br />
 * @author Benno Schmidt
 */
public class IoElevationGridGIFWriter extends IoObject
{
    private String mLogString = "";

    private String mFormat;

    private T3dColor mNoDataValue = new T3dColor(0,0,0);
    
    /**
     * constructor.<br /><br />
     * <i>German:</i> Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterst&uuml;tzt,
     * wird sp&auml;ter w&auml;hrend des Schreibvorgangs eine Ausnahme geworfen.<br />
     * Es werden z. Zt. die folgenden Formate unterst&uuml;tzt:<br />
     * <ul>
     * <li><i>GIFGreyScale:</i> GIF89a-Bild (Zellen-basiert mit zus�tzlichen ESRI-Referenzierungsdateien als Graustufen)</li>
     * <li><i>GIFPalOrder:</i> GIF89a-Bild (Zellen-basiert gem&auml;&szlig; Indizes in Palette)</li>
     * </ul><br />
     * Bem.:<br />
     * 1. F&uuml;r height_field-Objekte in POV-Ray das Format <tt>&quot;GIFPalOrder&quot;</tt> zu verwenden.
     * 2. F&uuml;r die Verwendung in POV-Ray sollte die Gitterweite des zu schreibenden H&ouml;henmodells in x- und
     * y-Richtung &uuml;bereinstimmen. Anderenfalls wird w&auml;hrend des Schreibvorgangs ein Ausnahmefehler geworfen.
     * <br />
     * 3. Die Anzahl der Gitterzellen in x- und y-Richtung sollte &uuml;bereinstimmen. Anderenfalls wird w&auml;hrend
     * des Schreibvorgangs ein Ausnahmefehler geworfen.<br />
     * <i>TODO: Erweiterung auf nicht-quadratische Gitter</i><br />
     * 4. F&uuml;r nicht-belegte Gitterpunkte wird ein ausgewiesener NODATA-Wert geschrieben. Dieser l&auml;sst sich
     * &uuml;ber die Methode <tt>this.setNoDataValue()</tt> explizit setzen. Default-Wert ist die Farbe
     * &quot;Schwarz&quot;.<br />
     * 5. Um das <tt>GmSimpleElevationGrid</tt> in anderen Formaten abzuspeichern, kann bei Bedarf die Klasse 
     * <tt>IoElevationGridWriter</tt> aus dem Paket org.n52.v3d.triturus.gisimplm genutzt werden.
     * @param pFormat Format-string, e.g. <tt>&quot;GIFGreyScale&quot;<tt>
     */
    public IoElevationGridGIFWriter(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * sets the format type.
     * @param pFormat Format-String, z. B. <tt></tt>&quot;GIFGreyScale&quot;</tt>
     */
    public void setFormatType(String pFormat) {
        mFormat = pFormat;
    }

    /**
     * writes an elevation-grid to a file.<br /><br />
     * <i>German:</i> schreibt ein Elevation-Grid in eine Datei. Wird der spezifizierte Formattyp nicht
     * unterst&uuml;tzt, wirft die Methode eine <tt>T3dNotYetImplException</tt>.
     * @param pGrid Elevation-grid to be written
     * @param pFilename Target file path
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void writeToFile(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("GIFGreyScale")) i = 1;
        if (mFormat.equalsIgnoreCase("GIFPalOrder")) i = 2;
        // --> hier ggf. weitere Typen erg�nzen...

        try {
            switch (i) {
                case 1: this.writeGIFAndRefFiles(pGrid, pFilename, false); break;
                case 2: this.writeGIFAndRefFiles(pGrid, pFilename, true); break;
                // --> hier ggf. weitere Typen erg�nzen...

                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }  

    private void writeGIFAndRefFiles(GmSimpleElevationGrid pGrid, String pFilename, boolean pPalOrder) 
        throws T3dException, T3dNotYetImplException
    {
    	if (pGrid == null)
            throw new T3dException("Grid information not available.");
                	
        GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

    	if (Math.abs((lGeom.getDeltaX() - lGeom.getDeltaY()) / lGeom.getDeltaX()) >= 0.001)
    	    throw new T3dException( "Grid requires equal cell-sizes in x- and y-direction." );
//    	if (lGeom.getNumberOfRows() != lGeom.getNumberOfColumns())
//            throw new T3dNotYetImplException( "Grid must be quadratic." );
// TODO: erst mal auskommentiert -< kann im Weiteren zu Fehlern f�hren!

		// TODO: Extension in Dateinamen pr�fen, muss .gif sein!

        GifEncodedRaster img = new GifEncodedRaster(lGeom.numberOfColumns(), lGeom.numberOfRows());
        Graphics2D graphics = img.getGraphics();

        Color pal[] = new Color[256];
        if (pPalOrder) {
        	// Index-Farbpalette holen
            for (int i = 0; i < 256; i++) 
                pal[i] = img.getPalColor(i); // Methode muss in gif.jar erg�nzt sein
        }

        double zMin = pGrid.minimalElevation();
        double zMax = pGrid.maximalElevation();
        double dz = zMax - zMin;
        
        try {        	
            T3dColor col = new T3dColor();
        	
            // Pixel den H�henwerten entsprechend setzen:
            for (int i = 0; i < lGeom.numberOfRows(); i++) {
                for (int j = 0; j < lGeom.numberOfColumns(); j++)
                {
                    if (pGrid.isSet(i, j)) 
                    {
                    	if (pPalOrder) {
                            int palVal = (int) (220. * ((pGrid.getValue(i, j) - zMin) / dz));
                            if (pal[palVal].getRed() > .98 && pal[palVal].getGreen() > .98 && pal[palVal].getBlue() > .98) palVal++;
                            if (palVal >= 220) palVal = 220; // TODO!!
                            graphics.setColor(pal[palVal]);
                        } 
                        else {
                            float greyVal = (float) ((pGrid.getValue(i, j) - zMin) / dz);
                            col.setRGB(greyVal,greyVal,greyVal);
	                        graphics.setColor(this.transformColor(col));
                        }
                    }
                    else 
                    {
                    	if (pPalOrder)
                            graphics.setColor(pal[0]);
                        else
                            graphics.setColor(this.transformColor(mNoDataValue));
                    }

                    /*
                    int ii = lGeom.getNumberOfRows() - i - 1;
                    int jj = lGeom.getNumberOfColumns() - j - 1;
                    */
                    int ii = j;
                    int jj = lGeom.numberOfRows() - i - 1;
                    graphics.drawLine(ii, jj, ii, jj);
                }
            }
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }

        // Bild schreiben (.gif):
        try {
            FileOutputStream fos = new FileOutputStream(pFilename);
            img.encode(fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }

        if (pPalOrder)
        	return;

		// xy-Referenzierungsdatei (.gifw) schreiben:

		try {
            FileWriter lFileWrite = new FileWriter(pFilename + "w");
            BufferedWriter lDat = new BufferedWriter(lFileWrite);

            lDat.write("" + lGeom.getDeltaX()); // Zellweite X
            lDat.newLine();
            lDat.write("0.0");
            lDat.newLine();
            lDat.write("0.0");
            lDat.newLine();
            lDat.write("" + (-lGeom.getDeltaY())); // Zellweite Y
            lDat.newLine();
            lDat.write("" + (lGeom.envelope().getXMin() - lGeom.getDeltaX()/2.)); // da Grid, nicht Lattice!
            lDat.newLine();
            lDat.write("" + (lGeom.envelope().getYMax() + lGeom.getDeltaY()/2.)); // da Grid, nicht Lattice!
            lDat.newLine();

            lDat.close();
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

		// z-Referenzierungsdatei (.gifz) schreiben:
		try {
            FileWriter lFileWrite = new FileWriter(pFilename + "z");
            BufferedWriter lDat = new BufferedWriter(lFileWrite);

            lDat.write("zMin: " + pGrid.minimalElevation()); // minimaler H�henwert
            lDat.newLine();
            lDat.write("zMax: " + pGrid.maximalElevation()); // minimaler H�henwert
            lDat.newLine();
            lDat.write("zResolution: +/-" + 0.5 * dz/256.); // Genauigkeit der H�henwerte
            lDat.newLine();
            lDat.write("isCompletelySet: " + pGrid.isSet()); // alle Gitterpunkte belegt?
            lDat.newLine();

            lDat.close();
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

    } // writeGIFAndRefFiles()

    private Color transformColor(T3dColor pCol)
    {
    	return new Color(pCol.getRed256(), pCol.getGreen256(), pCol.getBlue256());
    }
}
