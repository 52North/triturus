package org.n52.v3d.triturus.vispovray;

import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.io.*;
import java.awt.*;

//*import ranab.img.gif.*;

/**
 * Schreiben von Gitter-basiertern Höhenmodellen (Typ <tt>GmSimpleElevationGrid</tt>) in Dateien oder Ströme.<p>
 * Bem.: Die Klasse benötigt einen Patch der Bibliothek gif.jar von Rana Bhattacharyya (rana_b@yahoo.com), con terra-
 * Patch (Methode <tt>GifImage#getPalColor</tt>). Für die Verwendung dieser Bibliothek gilt die Apache Software 
 * License, Version 1.1.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoElevationGridGIFWriter extends IoObject
{
    private String mLogString = "";

    private String mFormat;

    private T3dColor mNoDataValue = new T3dColor(0,0,0);
    
    /** 
     * Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterstützt, wird später während
     * des Schreibvorgangs eine Ausnahme geworfen.<p>
     * Es werden z. Zt. die folgenden Formate unterstützt:<p>
     * <ul>
     * <li><i>GIFGreyScale:</i> GIF89a-Bild (Zellen-basiert mit zusätzlichen ESRI-Referenzierungsdateien als Graustufen)</li>
     * <li><i>GIFPalOrder:</i> GIF89a-Bild (Zellen-basiert gemäß Indizes in Palette)</li>
     * </ul><p>
     * Bem.:<p>
     * 1. Für height_field-Objekte in POV-Ray das Format <i>"GIFPalOrder"</i> zu verwenden.
     * 2. Für die Verwendung in POV-Ray sollte die Gitterweite des zu schreibenden Höhenmodells in x- und y-Richtung 
     * übereinstimmen. Anderenfalls wird während des Schreibvorgangs ein Ausnahmefehler geworfen.<p> 
     * 3. Die Anzahl der Gitterzellen in x- und y-Richtung sollte übereinstimmen. Anderenfalls wird während des 
     * Schreibvorgangs ein Ausnahmefehler geworfen.<p> 
     * <i>TODO: Erweiterung auf nicht-quadratische Gitter</i><p>
     * 4. Für nicht-belegte Gitterpunkte wird ein ausgewiesener NODATA-Wert geschrieben. Dieser lässt sich über die 
     * Methode <tt>this.setNoDataValue()</tt> explizit setzen. Default-Wert ist die Farbe "Schwarz".<p>
     * 5. Um das <tt>GmSimpleElevationGrid</tt> in anderen Formaten abzuspeichern, kann bei Bedarf die Klasse 
     * <tt>IoElevationGridWriter</tt> aus dem Paket org.n52.v3d.triturus.gisimplm genutzt werden.
     * <p>
     * @param pFormat Format-String, z. B. "GIFGreyScale"
     */
    public IoElevationGridGIFWriter(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /** 
     * setzt den Formattyp.<p>
     * @param pFormat Format-String, z. B. "GIFGreyScale"
     */
    public void setFormatType(String pFormat) {
        mFormat = pFormat;
    }

    /**
     * schreibt ein Elevation-Grid in eine Datei. Wird der spezifizierte Formattyp nicht unterstützt, wirft die Methode
     * eine <tt>T3dNotYetImplException</tt>.<p>
     * @param pGrid zu schreibendes Elevation-Grid
     * @param pFilename Pfad, unter dem die Datei abgelegt wird
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void writeToFile(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("GIFGreyScale")) i = 1;
        if (mFormat.equalsIgnoreCase("GIFPalOrder")) i = 2;
        // --> hier ggf. weitere Typen ergänzen...

        try {
            switch (i) {
                case 1: this.writeGIFAndRefFiles(pGrid, pFilename, false); break;
                case 2: this.writeGIFAndRefFiles(pGrid, pFilename, true); break;
                // --> hier ggf. weitere Typen ergänzen...

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
// TODO: erst mal auskommentiert -< kann im Weiteren zu Fehlern führen!

		// TODO: Extension in Dateinamen prüfen, muss .gif sein!

//*        GifImage img = new GifImage(lGeom.numberOfColumns(), lGeom.numberOfRows());
        Graphics2D graphics = null; //*img.getGraphics();

        Color pal[] = new Color[256];
        if (pPalOrder) {
        	// Index-Farbpalette holen
            for (int i = 0; i < 256; i++) 
;//*                pal[i] = img.getPalColor(i); // Methode muss in gif.jar ergänzt sein -> Patch verwenden!
        }

        double zMin = pGrid.minimalElevation();
        double zMax = pGrid.maximalElevation();
        double dz = zMax - zMin;
        
        try {        	
            T3dColor col = new T3dColor();
        	
            // Pixel den Höhenwerten entsprechend setzen:
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
//*            img.encode(fos);
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

            lDat.write("zMin: " + pGrid.minimalElevation()); // minimaler Höhenwert
            lDat.newLine();
            lDat.write("zMax: " + pGrid.maximalElevation()); // minimaler Höhenwert
            lDat.newLine();
            lDat.write("zResolution: +/-" + 0.5 * dz/256.); // Genauigkeit der Höhenwerte
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
