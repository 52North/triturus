/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.gisimplm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.t3dutil.MpHypsometricColor;

import java.text.DecimalFormat;

/**
 * Writing elevation grids (of type <tt>GmSimpleElevationGrid</tt>) to a file or stram.<br /><br />
 * <i>German:</i> Schreiben von Gitter-basiertern H&ouml;henmodellen (Typ <tt>GmSimpleElevationGrid</tt>) in Dateien
 * oder Str&ouml;me.
 * @author Benno Schmidt
 */
public class IoElevationGridWriter extends IoAbstractWriter
{
    private String mLogString = "";
    private String mFormat;

    private int mNoDataValue = -9999;
    private MpHypsometricColor mHypsometricColMap = null;

    /**
     * Identifier to be used to process elevation-grids in ArcInfo ASCII grid format.
     */
    public static final String ARCINFO_ASCII_GRID = "ArcIGrd";
    /**
     * Identifier to be used to process elevation-grids in ACADGEO format.
     */
    public static final String ACGEO = "AcGeo";
    /**
     * Identifier to be used to process elevation-grids in VRML 2 format.
     */
    public static final String VRML2 = "Vrml2";
    /**
     * Identifier to be used to process elevation-grids inX3D format.
     */
    public static final String X3D = "X3d";

    /**
     * Constructor.<br /><br />
     * <i>German:</i> Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterst&uuml;tzt,
     * wird sp&auml;ter w&auml;hrend des Schreibvorgangs eine Ausnahme geworfen.<br />
     * Es werden z. Zt. die folgenden Formate unterst&uuml;tzt:<br />
     * <ul>
     * <li><i>ArcIGrd:</i> ArcInfo ASCII grids (cell-based)</li>
     * <li><i>AcGeo:</i> ACADGEO format, lattice without color information</li>
     * <li><i>AcGeoTIN:</i> ACADGEO-TIN format</li>
     * <li><i>Vrml1:</i> VRML 1.0 scene (non-optimized triangle mesh)</li>
     * <li><i>Vrml2:</i> VRML 2.0 scene (type ElevationGrid)</li>
     * <li><i>X3d:</i> X3D scene</li>
     * <li><i>XYZ:</i> plain ASCII-file with coordinates of the elevation points</li>
     * </ul><p>
     * Bem.:<p>
     * 1. ArcInfo-ASCII-Grids k&ouml;nnen nur dann geschrieben werden, wenn die Gitterweite in x- und y-Richtung
     * &uuml;bereinstimmen. F&uuml;r nicht-belegte Gitterpunkte wird ein ausgewiesener NODATA-Wert geschrieben. Dieser
     * l&auml;sst sich &uuml;ber die Methode <tt>this.setNoDataValue()</tt> explizit setzen. Default-Wert ist -9999.
     * <br />
     * 2. Der VRML-Export erfolgt durch ein Speicherplatz-intensives Dreiecksnetz und ist insofern optimierbar.<br />
     * 3. Der X3D-Export ist nicht georeferenziert und nur prototypisch implementiert.<br />
     * 4. Um das <tt>GmSimpleElevationGrid</tt> als GIF-Bild abzuspeichern, kann bei Bedarf die Klasse 
     * <tt>IoElevationGridGIFWriter</tt> aus dem Paket org.n52.v3d.triturus.vispovray genutzt werden.
     * @param pFormat Format-string, e.g. <tt>&quot;ArcIGrd&quot;</tt>
     * @see IoElevationGridWriter#ARCINFO_ASCII_GRID
     * @see IoElevationGridWriter#ACGEO
     * @see IoElevationGridWriter#VRML2
     * @see IoElevationGridWriter#X3D
     */
    public IoElevationGridWriter(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * sets the format type.
     * @param pFormat Format-string (e.g. <tt></tt>&quot;ArcIGrd&quot;</tt>)
     * @see IoElevationGridWriter#ARCINFO_ASCII_GRID
     * @see IoElevationGridWriter#ACGEO
     * @see IoElevationGridWriter#VRML2
     * @see IoElevationGridWriter#X3D
     */
    public void setFormatType(String pFormat) {
        mFormat = pFormat;
    }

    /**
     * writes an elevation-grid to a file:<br /><br />
     * <i>German:</i> schreibt ein Elevation-Grid in eine Datei. Wird der spezifizierte Formattyp nicht
     * unterst&uuml;tzt, wirft die Methode eine <tt>T3dNotYetImplException</tt>.
     * @param pGrid Elevation-Grid to be written
     * @param pFilename File path
     * @throws T3dException for framework-specific errors
     * @throws T3dNotYetImplException if the called functionality has not been implemented yet
     */
    public void writeToFile(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase(ARCINFO_ASCII_GRID)) i = 1;
        if (mFormat.equalsIgnoreCase(ACGEO)) i = 2;
        if (mFormat.equalsIgnoreCase("AcGeoTIN")) i = 3;
        if (mFormat.equalsIgnoreCase("Vrml1")) i = 4;
        if (mFormat.equalsIgnoreCase(VRML2)) i = 5;
        if (mFormat.equalsIgnoreCase(X3D)) i = 6;
        if (mFormat.equalsIgnoreCase("XYZ")) i = 7;
        // --> hier ggf. weitere Typen erg�nzen...

        try {
            switch (i) {
                case 1: this.writeArcInfoAsciiGrid(pGrid, pFilename); break;
                case 2: this.writeAcadGeoGrid(pGrid, pFilename); break;
                case 3: this.writeAcadGeoTIN(pGrid, pFilename); break;
                case 4: this.writeSimpleVrml1(pGrid, pFilename); break;
                case 5: this.writeVrml2(pGrid, pFilename); break;
                case 6: this.writeSimpleX3d(pGrid, pFilename); break;
                case 7: this.writeAsciiXYZ(pGrid, pFilename); break;
                // --> hier ggf. weitere Typen erg�nzen...

                default: throw new T3dException("Unsupported file format.");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }  

    private void writeArcInfoAsciiGrid(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
    	if (pGrid == null)
            throw new T3dException("Grid information not available.");
                	
        GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

    	if (Math.abs((lGeom.getDeltaX() - lGeom.getDeltaY()) / lGeom.getDeltaX()) >= 0.001)
    	    throw new T3dException("ArcInfo ASCII grids require equal cell-sizes in x- and y-direction.");
    	    
    	String mNoDataValueStr = "-9999";
    	if (mNoDataValue != -9999) 
    	    mNoDataValueStr = "" + mNoDataValue;

        DecimalFormat dfZ = this.getDecimalFormatZ();

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            lDat.write("ncols         " + lGeom.numberOfColumns()); // line 1
            lDat.newLine();
            lDat.write("nrows         " + lGeom.numberOfRows()); // line 2
            lDat.newLine();
            lDat.write("xllcorner     "); // line 3
            lDat.write("" + (lGeom.envelope().getXMin() - lGeom.getDeltaX()/2.)); // da Grid, nicht Lattice!
            lDat.newLine();
            lDat.write("yllcorner     "); // line 4
            lDat.write("" + (lGeom.envelope().getYMin() - lGeom.getDeltaY()/2.)); // da Grid, nicht Lattice!
            lDat.newLine();
            lDat.write("cellsize      " + lGeom.getDeltaX()); // line 5
            lDat.newLine();
            lDat.write("NODATA_value  " + mNoDataValue); // line 6
            lDat.newLine();

            // Schreiben der H�henwerte f�r Gitterpunkte:
            for (int i = lGeom.numberOfRows() - 1; i >= 0; i--) { 
                for (int j = 0; j < lGeom.numberOfColumns(); j++) {
                    if (pGrid.isSet(i, j))
                        lDat.write(dfZ.format(pGrid.getValue(i, j)));
                    else
                        lDat.write(mNoDataValueStr);
                    lDat.write(" ");
                }
                lDat.newLine();
            }
            
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
    } // writeArcInfoAsciiGrid()

    /**
     * sets the NODATA-value for ArcInfo ASCII grids.<br /><br />
     * <i>German:</i> setzt den NODATA-Wert f&uuml;r ArcInfo-ASCII-Grids. Der Default-Wert von -9999 wird hierdurch
     * &uuml;berschrieben.
     * @param pNoDataValue NODATA-value
     */
    public void setNoDataValue(int pNoDataValue) {
    	mNoDataValue = pNoDataValue;
    }

    private void writeAcadGeoGrid(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

            lDat.write("GRID:"); // line 1
            lDat.newLine();
            lDat.write("C=OFF"); // line 2
            lDat.newLine();
            lDat.write("FROM " + lGeom.envelope().getXMin()); // line 3
            lDat.write(" " + lGeom.envelope().getYMin()); 
            lDat.newLine();
            lDat.write("TO " + lGeom.envelope().getXMax()); // line 4
            lDat.write(" " + lGeom.envelope().getYMax()); 
            lDat.newLine();
            lDat.write("SIZE " + lGeom.numberOfColumns() + " x " + lGeom.numberOfRows()); // line 5
            lDat.newLine();

            DecimalFormat dfZ = this.getDecimalFormatZ();

            // Schreiben der H�henwerte f�r Gitterpunkte:
            for (int j = 0; j < lGeom.numberOfColumns(); j++)
                for (int i = 0; i < lGeom.numberOfRows(); i++) {
                    if (pGrid.isSet(i, j))
                        lDat.write(dfZ.format(pGrid.getValue(i, j)));
                    else
                        lDat.write("?");
                    lDat.newLine();
            }
            
            lDat.write("END");
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
    } // writeAcadGeoGrid()

    private void writeAcadGeoTIN(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
    	if (!pGrid.isSet())
    		throw new T3dNotYetImplException("Can not write unset grid vertices yet!");

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

            lDat.write("TINBEGIN"); // line 1
            lDat.newLine();
            lDat.write("FORMAT R=OFF C=OFF"); // line 2
            lDat.newLine();
            lDat.write("TIN:"); // line 3
            lDat.newLine();
            long numberOfVertices = lGeom.numberOfColumns() * lGeom.numberOfRows();
            lDat.write("POINTS " + numberOfVertices); // line 4
            lDat.newLine();

            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            // Schreiben der H�henwerte f�r Gitterpunkte:
            for (int j = 0; j < lGeom.numberOfColumns(); j++)
                for (int i = 0; i < lGeom.numberOfRows(); i++) {
                    GmPoint pt = new GmPoint(lGeom.getVertexCoordinate( i, j ));
                    lDat.write(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()));
                    lDat.write(" " + dfZ.format(pGrid.getValue(i, j)));
                    lDat.newLine();
            }

            long numberOfTriangles = 2 * (lGeom.numberOfColumns() - 1) * (lGeom.numberOfRows() - 1);
            lDat.write("TRIANGLES " + numberOfTriangles);
            lDat.newLine();

			// Schreiben der Dreiecksvermaschung:
            int crn1, crn2, crn3, crn4;
            for (int j = 0; j <= lGeom.numberOfColumns() - 2; j++) {
                for (int i = 0; i <= lGeom.numberOfRows() - 2; i++)
                {
                    crn1 = j * lGeom.numberOfRows() + i;
                    crn2 = (j + 1) * lGeom.numberOfRows() + i;
                    crn3 = (j + 1)  * lGeom.numberOfRows() + (i + 1);
                    crn4 = j * lGeom.numberOfRows() + (i + 1);

                    lDat.write("" + crn1 + " " + crn2 + " " + crn4); lDat.newLine();
                    lDat.write("" + crn4 + " " + crn2 + " " + crn3); lDat.newLine();
                }
            }

            lDat.write("END");
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
    } // writeAcadGeoTIN()

    private void writeSimpleVrml1(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

            lDat.write("#VRML V1.0 ascii"); lDat.newLine();
            lDat.newLine();
            lDat.write("Separator {"); lDat.newLine();
            lDat.newLine();
            lDat.write("  DEF SceneInfo Info {"); lDat.newLine();
            lDat.write("    string \"Generated by 52N Triturus\""); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.write("  ShapeHints {"); lDat.newLine();
            lDat.write("    vertexOrdering CLOCKWISE"); lDat.newLine();
            lDat.write("    shapeType SOLID"); lDat.newLine();
            lDat.write("    faceType CONVEX"); lDat.newLine();
            lDat.write("    creaseAngle 0.0"); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.newLine();
            lDat.write("  DEF Green_DEM Separator {"); lDat.newLine();
            lDat.write("    Material {"); lDat.newLine();
            lDat.write("      diffuseColor 0.0 1.0 0.0"); lDat.newLine();
            lDat.write("      ambientColor 0.0 0.1 0.0"); lDat.newLine();
            lDat.write("      specularColor 0.8 0.8 0.8"); lDat.newLine();
            lDat.write("      shininess 0.1"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.write("    Coordinate3 {"); lDat.newLine();
            lDat.write("      point ["); lDat.newLine();

            // VRML Teil 1 (Angabe der St�tzpunkte):

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int j = 0; j < lGeom.numberOfColumns(); j++) {
                for (int i = 0; i < lGeom.numberOfRows(); i++) {
                    GmPoint pt = new GmPoint(lGeom.getVertexCoordinate( i, j ));
                    lDat.write("        " + dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pGrid.getValue(i, j)));
                    lDat.newLine();
                } 
            }

            lDat.write("      ]"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.newLine();

            // VRML Teil 2 (Vermaschung):

            lDat.write("    IndexedFaceSet {"); lDat.newLine();
            lDat.write("      coordIndex ["); lDat.newLine();

            int crn1, crn2, crn3, crn4;
            for (int j = 0; j <= lGeom.numberOfColumns() - 2; j++) {
                for (int i = 0; i < lGeom.numberOfRows() - 2; i++)
                {
                    crn1 = j * lGeom.numberOfRows() + i;
                    crn2 = (j + 1) * lGeom.numberOfRows() + i;
                    crn3 = (j + 1)  * lGeom.numberOfRows() + (i + 1);
                    crn4 = j * lGeom.numberOfRows() + (i + 1);

                    if (pGrid.isSet(i, j) && pGrid.isSet(i, j + 1) && 
                        pGrid.isSet(i + 1, j + 1) && pGrid.isSet(i + 1, j))
                    {
                       lDat.write("        " + crn1 + ", " + crn2 + ", " + crn4 + ", -1,"); lDat.newLine();
                       lDat.write("        " + crn4 + ", " + crn2 + ", " + crn3 + ", -1,"); lDat.newLine();
                    }
                }
            }

            lDat.write("      ]"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.write("}"); lDat.newLine();

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
    } // writeSimpleVrml1()

    /**
     * enables the export of hypsometric coloured models.<br /><br />
     * <i>German:</i> erm&ouml;glicht, das H&ouml;hengitter als hypsometrisch eingef&auml;rbtes Modell zu exportieren.
     * Dieser Modus wird nur f&uuml;r dasFormat &quot;Vrml2&quot; unterst�tzt.
     * @param pColMap Hypsometric color-assignment or <i>null</i>, if no colouring shall be carried out
     */
    public void setHypsometricColorMapper(MpHypsometricColor pColMap) {
    	mHypsometricColMap = pColMap;
    }

    private void writeVrml2(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

            // Obere linke Ecke des DGMs:
            double ox = lGeom.envelope().getXMin();
            double oy = lGeom.envelope().getYMin();

            // DGM-Mittelpunkt:
            double cx = lGeom.envelope().getCenterPoint().getX();
            double cy = lGeom.envelope().getCenterPoint().getY();

            // Fovy:
            double fovy = 0.785398; // entspr. 45 Grad
            double zCamera = pGrid.maximalElevation() + 0.5 * lGeom.envelope().diagonalLength() / Math.tan(0.5 * fovy);

            lDat.write("#VRML V2.0 utf8"); lDat.newLine();
            lDat.newLine();
            lDat.write("WorldInfo {"); lDat.newLine();
            lDat.write("    title \"Triturus document\""); lDat.newLine();
            lDat.write("    info \"Generated by 52N Triturus\""); lDat.newLine();
            lDat.write("}"); lDat.newLine();
            lDat.newLine();
            lDat.write("Background {"); lDat.newLine();
            lDat.write("    skyColor 0.0 0.8 0.8"); lDat.newLine();
            lDat.write("}"); lDat.newLine();
            lDat.newLine();
            lDat.write("NavigationInfo {"); lDat.newLine();
            lDat.write("    type \"EXAMINE\""); lDat.newLine();
            lDat.write("}"); lDat.newLine();
            lDat.newLine();
            lDat.write("Viewpoint {"); lDat.newLine();
            lDat.write("    fieldOfView " + fovy); lDat.newLine();
            lDat.write("    orientation 1.0 0.0 0.0 4.712"); lDat.newLine();
            lDat.write("    position " + cx + " " + zCamera  + " " + cy);
            lDat.newLine();
            lDat.write("    description \"default\""); lDat.newLine();
            lDat.write("}"); lDat.newLine();
            lDat.newLine();
lDat.write("Group { children ["); lDat.newLine();                   // TODO
lDat.write("DEF Relief Transform {"); lDat.newLine();
            lDat.write("    scale 1 5 1"); // �berh�hung
            lDat.write("    children ["); lDat.newLine();
            lDat.write("        Transform {"); lDat.newLine();
            lDat.write("            translation " + ox + " 0.0 " + oy); lDat.newLine();
            lDat.write("            children ["); lDat.newLine();
            lDat.write("                Shape {"); lDat.newLine();
            lDat.write("                    appearance Appearance {"); lDat.newLine();
            lDat.write("                        material Material {"); lDat.newLine();
  //          lDat.write("                            diffuseColor 0.2 0.5 0.2"); lDat.newLine();
            lDat.write("                        }"); lDat.newLine();
if (false) {
lDat.write("                        texture ImageTexture {"); lDat.newLine();              // TODO
String lDrape = "http://www.wischelo.de/images/email.png";      // todo variabel machen!!        jpeg m�sste auch gehen...
System.out.println("DRAPE = " + lDrape);                   // todo: Problem: Bild ist noch spiegelbildlich!!
lDat.write("                            url \"" + lDrape + "\""); lDat.newLine();
lDat.write("                        }"); lDat.newLine();
mHypsometricColMap = null;
}
            lDat.write("                    }"); lDat.newLine();
            lDat.write("                    geometry ElevationGrid {"); lDat.newLine();
            lDat.write("                        xDimension " + lGeom.numberOfColumns()); lDat.newLine();
            lDat.write("                        zDimension " + lGeom.numberOfRows()); lDat.newLine();
            lDat.write("                        xSpacing " + lGeom.getCellSizeColumns()); lDat.newLine();
            lDat.write("                        zSpacing " +  + lGeom.getCellSizeRows()); lDat.newLine();
            lDat.write("                        height ["); lDat.newLine();

            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int i = lGeom.numberOfRows() - 1; i >= 0; i--) {
                for (int j = 0; j < lGeom.numberOfColumns(); j++)
                    lDat.write(dfZ.format(pGrid.getValue(i, j)) + ",");
                lDat.newLine();
            }

            lDat.write("                        ]"); lDat.newLine(); // Ende height

            if (mHypsometricColMap != null)
            {
                lDat.write("                        colorPerVertex TRUE"); lDat.newLine();
                lDat.write("                        color Color {"); lDat.newLine();
                lDat.write("                            color ["); lDat.newLine();
                for (int i = lGeom.numberOfRows() - 1; i >= 0; i--) {
                    for (int j = 0; j < lGeom.numberOfColumns(); j++) {
		                T3dColor col = mHypsometricColMap.transform(pGrid.getValue(i, j));
            		    lDat.write(col.getRed() + " " + col.getGreen() + " " + col.getBlue() + ",");
                        lDat.newLine();
                    }
                }
                lDat.write("                            ]"); lDat.newLine();
                lDat.write("                        }"); lDat.newLine(); // Ende color
            }

            lDat.write("                    }"); lDat.newLine(); // Ende geometry
            lDat.write("                }"); lDat.newLine(); // Ende Shape
            lDat.write("            ]"); lDat.newLine(); // Ende children
            lDat.write("        }"); lDat.newLine(); // Ende Transform Verschiebung
            lDat.write("    ]"); lDat.newLine(); // Ende children
            lDat.write("}"); lDat.newLine(); // Ende Transform �berh�hung
lDat.write(", DEF Sensor TouchSensor {} ] }"); lDat.newLine();

lDat.write("DEF Clock TimeSensor {"); lDat.newLine();
lDat.write("  cycleInterval 20.0"); lDat.newLine();
lDat.write("  enabled FALSE"); lDat.newLine();
lDat.write("  loop TRUE"); lDat.newLine();
lDat.write("}"); lDat.newLine();
lDat.write("DEF Interpolator PositionInterpolator {"); lDat.newLine();
lDat.write("  key [0.0, 0.5, 1.0]"); lDat.newLine();
lDat.write("  keyValue [ 1 0 1, 1 10 1, 1 1 1 ]"); lDat.newLine();
lDat.write("}"); lDat.newLine();
lDat.write("ROUTE Sensor.touchTime TO Clock.startTime"); lDat.newLine();
lDat.write("ROUTE Sensor.isActive TO Clock.set_enabled"); lDat.newLine();
lDat.write("ROUTE Clock.fraction_changed TO Interpolator.set_fraction"); lDat.newLine();
lDat.write("ROUTE Interpolator.value_changed TO Relief.set_scale"); lDat.newLine();

            lDat.newLine();

            lDat.close(); // darf nicht vergessen werden!
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
    } // writeVrml2()

    private void writeSimpleX3d(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
        final double lExaggeration = 7.;

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = (GmSimple2dGridGeometry) pGrid.getGeometry();

            lDat.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); lDat.newLine();
            lDat.newLine();
            lDat.write("<X3D profile='Immersive'>"); lDat.newLine();
            lDat.write("  <head>"); lDat.newLine();
            lDat.write("    <meta name='author' content='52N Triturus'/>"); lDat.newLine();
            lDat.write("    <meta name='generator' content='Triturus framework'/>"); lDat.newLine();
            lDat.write("  </head>"); lDat.newLine();
            lDat.write("  <Scene>"); lDat.newLine();

            // DGM-Mittelpunkt:
            double px = lGeom.numberOfColumns() / 2. * lGeom.getCellSizeColumns();
            double py = lGeom.numberOfRows() / 2.  * lGeom.getCellSizeRows();
            double pz = (pGrid.maximalElevation() + pGrid.minimalElevation()) / 2.;

            // Kameraposition und Rotationspunkt:
            lDat.write("    <NavigationInfo type='\"EXAMINE\" \"WALK\" \"FLY\" \"ANY\"'/>"); lDat.newLine();
            lDat.write("    <Viewpoint description=\"Draufsicht\" orientation=\"1 0 0 -1.57\" position=\"");
            lDat.write("" + px + " " + (lExaggeration * 10. * pz) + " " + py);
            lDat.write("\" centerOfRotation=\"" + px + " " + (lExaggeration * pz) + " " + py + "\"/>");
            lDat.newLine();

            lDat.write("    <Shape>"); lDat.newLine();
            lDat.write("      <Appearance>"); lDat.newLine();
            lDat.write("        <Material/>"); lDat.newLine();
            lDat.write("      </Appearance>"); lDat.newLine();
            lDat.write("      <ElevationGrid solid=\"false\"");

            // Gitterparameter:
            lDat.write(" xDimension=\"" + lGeom.numberOfColumns());
            lDat.write("\" xSpacing=\"" + lGeom.getCellSizeColumns());
            lDat.write("\" zDimension=\"" + lGeom.numberOfRows());
            lDat.write("\" zSpacing=\"" + lGeom.getCellSizeRows());
            lDat.write("\"");
            lDat.newLine();

            lDat.write("      height=\"" );

            // Angabe der H�henwerte:
            for (int j = 0; j < lGeom.numberOfColumns(); j++) {
                for (int i = 0; i < lGeom.numberOfRows(); i++) {
                    lDat.write("" + (lExaggeration * pGrid.getValue(i, j)) + " ");
                    // this.getDecimalFormatZ() hier unber�cksichtigt... -> todo
                }
            }
            lDat.write("\"/>"); lDat.newLine();
            lDat.write("    </Shape>"); lDat.newLine();
            lDat.write("  </Scene>"); lDat.newLine();
            lDat.write("</X3D>"); lDat.newLine();
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
    } // writeSimpleX3d()

    private void writeAsciiXYZ(GmSimpleElevationGrid pGrid, String pFilename) throws T3dException
    {
		if (pGrid == null) {
			throw new T3dException("Grid information not available.");
		}

		try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            GmSimple2dGridGeometry lGeom = ((GmSimple2dGridGeometry) pGrid.getGeometry());

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

			for (int j = 0; j < pGrid.numberOfColumns(); j++) {
				for (int i = 0; i < pGrid.numberOfRows(); i++) {
					if (pGrid.isSet(i, j)) {
                        VgPoint pt = lGeom.getVertexCoordinate(i, j);
                        lDat.write(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pGrid.getValue(i, j)));
						lDat.newLine();
					}
				}
			}
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
	} // writeAsciiXYZ()
}
