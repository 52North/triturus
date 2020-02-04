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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.t3dutil.MpHypsometricColor;

import java.text.DecimalFormat;

/**
 * The <tt>IoElevationGridWriter</tt> provides methods to write elevation grids
 * (of type {@link GmSimpleElevationGrid} to a file or stream. Various output
 * formats such as VRML/X3D or ArcInfo ASCII grids are supported.
 *
 * @author Benno Schmidt
 */
public class IoElevationGridWriter extends IoAbstractWriter 
{
    private String logString = "";
    private String format;
    private BufferedWriter doc;

    private int noDataValue = -9999;
    private MpHypsometricColor hypsometricColMap = null;
    private double exaggeration = 1.;
    
    /**
     * Constructor. As an input parameter, the file format type identifier must
     * be specified. The supported formats are listed below:<br/>
     * <ul>
     * <li><i>ArcIGrd:</i> ArcInfo ASCII grids (cell-based)</li>
     * <li><i>AcGeo:</i> ACADGEO format, lattice without color information</li>
     * <li><i>AcGeoTIN:</i> ACADGEO-TIN format</li>
     * <li><i>OBJ:</i> Wavefront OBJ file</li>
     * <li><i>VRML1:</i> VRML 1.0 scene (non-optimized triangle mesh)</li>
     * <li><i>VRML2:</i> VRML 2.0 scene (type ElevationGrid)</li>
     * <li><i>VTK:</i> VTK 3.0 format (polydata with cell attributes)</li>
     * <li><i>X3D:</i> X3D scene</li>
     * <li><i>X3DOM:</i> HTML5 with embedded X3DOM description</li>
     * <li><i>XYZ:</i> plain ASCII-file with coordinates of the elevation points</li>
     * </ul><br/>
     * Notes:<br/>
     * 1. For ArcInfo ASCII grids, cell-sizes in x- and y-direction must be
     * equal. For vertices with missing elevation-information, a NODATA-value
     * will be written to the file. The NODATA-value can be defined using the
     * method <tt>this.setNoDataValue()</tt>. (The default-value is -9999.)
     * <br/>
     * 2. VRML export might be optimized, since a memory-intensive triangle mesh
     * is used.<br/>
     * 3. X3D-export is not georeferenced yet and implemented prototypical
     * only.<br/>
     * 4. To save the {@link GmSimpleElevationGrid}-object as GIF-image, the
     * class {@link org.n52.v3d.triturus.vispovray.IoElevationGridGIFWriter} 
     * might be suitable.
     *
     * @param format Format-string, see {@link #setFormatType(String)}
     */
    public IoElevationGridWriter(String format) {
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

    /**
     * sets the format type.
     *
     * @param format Format-string (e.g. <tt>&quot;ArcIGrd&quot;</tt>)
     * @see IoFormatType#ARCINFO_ASCII_GRID
     * @see IoFormatType#ACGEO
     * @see IoFormatType#OBJ
     * @see IoFormatType#VRML2
     * @see IoFormatType#VTK_DATASET
     * @see IoFormatType#X3D
     * @see IoFormatType#X3DOM
     */
    public void setFormatType(String format) {
        this.format = format;
    }

    /**
     * writes an elevation-grid to a file.
     *
     * @param grid Elevation-Grid to be written
     * @param filename File name (with path optionally)
     * @throws T3dException for framework-specific errors
     * @throws T3dNotYetImplException if the called functionality has not been implemented yet
     */
    public void writeToFile(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException, T3dNotYetImplException 
    {
        int i = 0;
        if (format.equalsIgnoreCase(IoFormatType.ARCINFO_ASCII_GRID)) i = 1;
        if (format.equalsIgnoreCase(IoFormatType.ACGEO)) i = 2;
        if (format.equalsIgnoreCase("AcGeoTIN")) i = 3;
        if (format.equalsIgnoreCase("VRML1")) i = 4;
        if (format.equalsIgnoreCase(IoFormatType.VRML2)) i = 5;
        if (format.equalsIgnoreCase(IoFormatType.X3D)) i = 6;
        if (format.equalsIgnoreCase("XYZ")) i = 7;
        if (format.equalsIgnoreCase(IoFormatType.X3DOM)) i = 8;
        if (format.equalsIgnoreCase(IoFormatType.OBJ)) i = 9;
        if (format.equalsIgnoreCase(IoFormatType.VTK_DATASET)) i = 10;
        // --> add more types here...

        switch (i) {
            case 1:
                this.writeArcInfoAsciiGrid(grid, filename);
                break;
            case 2:
                this.writeAcadGeoGrid(grid, filename);
                break;
            case 3:
                this.writeAcadGeoTIN(grid, filename);
                break;
            case 4:
                this.writeSimpleVrml1(grid, filename);
                break;
            case 5:
                this.writeVrml2(grid, filename);
                break;
            case 6:
                this.writeSimpleX3d(grid, filename, false);
                break;
            case 7:
                this.writeAsciiXYZ(grid, filename);
                break;
            case 8:
                this.writeSimpleX3d(grid, filename, true);
                break;
            case 9:
                this.writeSimpleObj(grid, filename);
                break;
            case 10:
                this.writeVtkDataset(grid, filename);
                break;
            // --> add more types here...

            default:
                throw new T3dException("Unsupported file format.");
        }
    }

    private void writeArcInfoAsciiGrid(GmSimpleElevationGrid grid, String filename) 
   		throws T3dException 
    {
        if (grid == null)
            throw new T3dException("Grid information not available.");

        GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

        if (Math.abs((geom.getDeltaX() - geom.getDeltaY()) / geom.getDeltaX()) >= 0.001) {
            throw new T3dException(
            	"ArcInfo ASCII grids require equal cell-sizes in x- and y-direction.");
        }

        String noDataValueStr = "-9999";
        if (noDataValue != -9999) {
            noDataValueStr = "" + noDataValue;
        }

        DecimalFormat dfZ = this.getDecimalFormatZ();

        try {
            doc = new BufferedWriter(new FileWriter(filename));

            w("ncols         " + geom.numberOfColumns()); // line 1
            wl();
            w("nrows         " + geom.numberOfRows()); // line 2
            wl();
            w("xllcorner     "); // line 3
            w("" + 
            	(geom.envelope().getXMin() - geom.getDeltaX() / 2.)); 
            	// since this is a grid, not a lattice!
            wl();
            w("yllcorner     "); // line 4
            w("" + 
            	(geom.envelope().getYMin() - geom.getDeltaY() / 2.)); 
            	// since this is a grid, not a lattice!
            wl();
            w("cellsize      " + geom.getDeltaX()); // line 5
            wl();
            w("NODATA_value  " + noDataValue); // line 6
            wl();

            // Write elevation-values for grid vertices:
            for (int i = geom.numberOfRows() - 1; i >= 0; i--) {
                for (int j = 0; j < geom.numberOfColumns(); j++) {
                    if (grid.isSet(i, j)) {
                        w(dfZ.format(grid.getValue(i, j)));
                    }
                    else {
                        w(noDataValueStr);
                    }
                    w(" ");
                }
                wl();
            }
            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeArcInfoAsciiGrid()

    /**
     * sets the NODATA-value for ArcInfo ASCII grids. Calling this method, the
     * default-value (-9999) will be overwritten.
     *
     * @param noDataValue NODATA-value
     */
    public void setNoDataValue(int noDataValue) {
        this.noDataValue = noDataValue;
    }

    private void writeAcadGeoGrid(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException 
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

            w("GRID:"); // line 1
            wl();
            w("C=OFF"); // line 2
            wl();
            w("FROM " + geom.envelope().getXMin()); // line 3
            w(" " + geom.envelope().getYMin());
            wl();
            w("TO " + geom.envelope().getXMax()); // line 4
            w(" " + geom.envelope().getYMax());
            wl();
            w("SIZE " + geom.numberOfColumns() + " x " + geom.numberOfRows()); // line 5
            wl();

            DecimalFormat dfZ = this.getDecimalFormatZ();

            // Write elevation-values for grid vertices:
            for (int j = 0; j < geom.numberOfColumns(); j++) {
                for (int i = 0; i < geom.numberOfRows(); i++) {
                    if (grid.isSet(i, j)) {
                        w(dfZ.format(grid.getValue(i, j)));
                    }
                    else {
                        w("?");
                    }
                    wl();
                }
            }

            w("END");
            wl();

            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeAcadGeoGrid()

    private void writeAcadGeoTIN(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException 
    {
        if (!grid.isSet()) {
            throw new T3dNotYetImplException("Can not write unset grid vertices yet!");
        }

        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

            w("TINBEGIN"); // line 1
            wl();
            w("FORMAT R=OFF C=OFF"); // line 2
            wl();
            w("TIN:"); // line 3
            wl();
            long numberOfVertices = geom.numberOfColumns() * geom.numberOfRows();
            w("POINTS " + numberOfVertices); // line 4
            wl();

            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            // Write elevation-values for grid vertices:
            for (int j = 0; j < geom.numberOfColumns(); j++) {
                for (int i = 0; i < geom.numberOfRows(); i++) 
                {
                    GmPoint pt = new GmPoint(geom.getVertexPoint(i, j));
                    
                    w(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()));
                    w(" " + dfZ.format(grid.getValue(i, j)));
                    wl();
                }
            }

            long numberOfTriangles = 
            	2 * (geom.numberOfColumns() - 1) * (geom.numberOfRows() - 1);
            w("TRIANGLES " + numberOfTriangles);
            wl();

            // Write triangulation:
            int crn1, crn2, crn3, crn4;
            for (int j = 0; j <= geom.numberOfColumns() - 2; j++) {
                for (int i = 0; i <= geom.numberOfRows() - 2; i++) 
                {
                    crn1 = j * geom.numberOfRows() + i;
                    crn2 = (j + 1) * geom.numberOfRows() + i;
                    crn3 = (j + 1) * geom.numberOfRows() + (i + 1);
                    crn4 = j * geom.numberOfRows() + (i + 1);

                    w("" + crn1 + " " + crn2 + " " + crn4);
                    wl();
                    w("" + crn4 + " " + crn2 + " " + crn3);
                    wl();
                }
            }

            w("END");
            wl();

            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeAcadGeoTIN()

    private void writeSimpleVrml1(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException 
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

            wl("#VRML V1.0 ascii");
            wl();
            wl("Separator {");
            wl();
            wl("  DEF SceneInfo Info {");
            wl("    string \"Generated by 52N Triturus\"");
            w("  }");
            wl();
            wl("  ShapeHints {");
            wl("    vertexOrdering CLOCKWISE");
            wl("    shapeType SOLID");
            wl("    faceType CONVEX");
            wl("    creaseAngle 0.0");
            wl("  }");
            wl();
            wl("  DEF Green_DEM Separator {");
            wl("    Material {");
            wl("      diffuseColor 0.0 1.0 0.0");
            wl("      ambientColor 0.0 0.1 0.0");
            wl("      specularColor 0.8 0.8 0.8");
            wl("      shininess 0.1");
            wl("    }");
            wl("    Coordinate3 {");
            wl("      point [");

            // VRML part 1 (vertices):
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int j = 0; j < geom.numberOfColumns(); j++) {
                for (int i = 0; i < geom.numberOfRows(); i++) 
                {
                    GmPoint pt = new GmPoint(geom.getVertexPoint(i, j));
                    wl("        " 
                    		+ dfXY.format(pt.getX()) + " " 
                    		+ dfXY.format(pt.getY()) + " " 
                    		+ dfZ.format(grid.getValue(i, j)));
                }
            }

            wl("      ]");
            wl("    }");
            wl();

            // VRML part 2 (mesh topology):
            wl("    IndexedFaceSet {");
            wl("      coordIndex [");
            
            int crn1, crn2, crn3, crn4;
            for (int j = 0; j <= geom.numberOfColumns() - 2; j++) {
                for (int i = 0; i < geom.numberOfRows() - 2; i++) 
                {
                    crn1 = j * geom.numberOfRows() + i;
                    crn2 = (j + 1) * geom.numberOfRows() + i;
                    crn3 = (j + 1) * geom.numberOfRows() + (i + 1);
                    crn4 = j * geom.numberOfRows() + (i + 1);

                    if (
                    		grid.isSet(i, j) && 
                    		grid.isSet(i, j + 1) && 
                    		grid.isSet(i + 1, j + 1) && 
                    		grid.isSet(i + 1, j)) 
                    {
                        wl("        " + crn1 + ", " + crn2 + ", " + crn4 + ", -1,");
                        wl("        " + crn4 + ", " + crn2 + ", " + crn3 + ", -1,");
                    }
                }
            }

            wl("      ]");
            wl("    }");
            wl("  }");
            wl("}");

            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeSimpleVrml1()

    /**
     * enables the export of hypsometric colored models. As parameter, a hypsometric 
     * a color-mapper has to be given or <i>null</i>, if no coloring shall be carried 
     * out. Note that this mode is supported for the {@link VRML2} format only.
     *
     * @param colMap Hypsometric color-assignment or <i>null</i> for no coloring 
     */
    public void setHypsometricColorMapper(MpHypsometricColor colMap) {
        hypsometricColMap = colMap;
    }

    /**
     * sets a vertical &quot"exaggeration&quot; factor. Note that this mode is 
     * supported for the {@link VRML2} export only.
     *
     * @param exaggeration Exaggeration factor
     */
    public void setExaggeration(double exaggeration) {
    	this.exaggeration = exaggeration;
    }

    private void writeVrml2(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException 
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();

            // Upper left DEM corner:
            double ox = geom.envelope().getXMin();
            double oy = geom.envelope().getYMin();

            // The DEM's center point:
            double cx = geom.envelope().getCenterPoint().getX();
            double cy = geom.envelope().getCenterPoint().getY();

            // Fovy:
            double fovy = 0.785398; // i.e., 45 degrees
            double zCamera = grid.maximalElevation() + 
            		0.5 * geom.envelope().diagonalLength() / Math.tan(0.5 * fovy);

            wl("#VRML V2.0 utf8");
            wl();
            wl("WorldInfo {");
            wl("    title \"Triturus document\"");
            wl("    info \"Generated by 52N Triturus\"");
            wl("}");
            wl();
            wl("Background {");
            wl("    skyColor 0.0 0.8 0.8");
            wl("}");
            wl();
            wl("NavigationInfo {");
            wl("    type \"EXAMINE\"");
            wl("}");
            wl();
            wl("Viewpoint {");
            wl("    fieldOfView " + fovy);
            wl("    orientation 1.0 0.0 0.0 4.712");
            wl("    position " + cx + " " + zCamera + " " + cy);
            wl("    description \"default\"");
            wl("}");
            wl();
            wl("Group { children [");
            wl("DEF Relief Transform {");
            w("    scale 1 " + exaggeration + " 1"); // Exaggeration
            wl("    children [");
            wl("        Transform {");
            wl("            translation " + ox + " 0.0 " + oy);
            wl("            children [");
            wl("                Shape {");
            wl("                    appearance Appearance {");
            wl("                        material Material {");
            // w("                            diffuseColor 0.2 0.5 0.2"); lDat.newLine();
            wl("                        }");
            wl("                    }");
            wl("                    geometry ElevationGrid {");
            wl("                        xDimension " + geom.numberOfColumns());
            wl("                        zDimension " + geom.numberOfRows());
            wl("                        xSpacing " + geom.getDeltaX());
            wl("                        zSpacing " + +geom.getDeltaY());
            wl("                        height [");
            
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int i = geom.numberOfRows() - 1; i >= 0; i--) {
                for (int j = 0; j < geom.numberOfColumns(); j++) {
                    w(dfZ.format(grid.getValue(i, j)) + ",");
                }
                wl();
            }

            w("                        ]");
            wl(); // End height

            if (hypsometricColMap != null) {
                wl("                        colorPerVertex TRUE");
                wl("                        color Color {");
                wl("                            color [");
                
                for (int i = geom.numberOfRows() - 1; i >= 0; i--) {
                    for (int j = 0; j < geom.numberOfColumns(); j++) {
                        T3dColor col = hypsometricColMap.transform(grid.getValue(i, j));                    
                        wl(col.getRed() + " " + col.getGreen() + " " + col.getBlue() + ",");
                    }
                }
                
                wl("                            ]");
                w("                        }");
                wl(); // End color
            }

            w("                    }");
            wl(); // End geometry
            w("                }");
            wl(); // End Shape
            w("            ]");
            wl(); // End children
            w("        }");
            wl(); // End Transform Translation
            w("    ]");
            wl(); // End children
            w("}");
            wl(); // End Transform exaggeration
            wl(", DEF Sensor TouchSensor {} ] }");

            /*
            wl("DEF Clock TimeSensor {");
            wl("  cycleInterval 20.0");
            wl("  enabled FALSE");
            wl("  loop TRUE");
            wl("}");
            wl("DEF Interpolator PositionInterpolator {");
            wl("  key [0.0, 0.5, 1.0]");
            wl("  keyValue [ 1 0 1, 1 10 1, 1 1 1 ]");
            wl("}");
            wl("ROUTE Sensor.touchTime TO Clock.startTime");
            wl("ROUTE Sensor.isActive TO Clock.set_enabled");
            wl("ROUTE Clock.fraction_changed TO Interpolator.set_fraction");
            wl("ROUTE Interpolator.value_changed TO Relief.set_scale");
            */
            
            wl();

            doc.close(); // Don't forget this!
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeVrml2()

    private void writeSimpleX3d(GmSimpleElevationGrid grid, String filename, boolean isX3dom) 
   		throws T3dException 
    {
        final double lExaggeration = 7.;

        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();
            if (isX3dom) {
                wl("<html xmlns='http://www.w3.org/1999/xhtml'>");
                wl("<head>");
                // @Adhitya: This should be later changed to links pointing to the actual repository
                wl("<link rel=\"stylesheet\" type=\"text/css\" href=\"https://rawgit.com/kamakshidasan/triturus/master/src/main/resources/css/x3dom.css\" />");
                wl("<script type=\"text/javascript\" src=\"https://rawgit.com/kamakshidasan/triturus/master/src/main/resources/js/x3dom-full.js\"></script>");
                //wl("<script type=\"text/javascript\" src=\"../src/main/resources/js/select.js\"></script>");
                wl("</head>");
                wl("<body>");
            }
            wl("<X3D profile='Immersive' height='400px' width='400px' showLog='false'>");
            wl("  <Scene id=\"root\">");

            // DEM center point:
            double px = geom.numberOfColumns() / 2. * geom.getDeltaX();
            double py = geom.numberOfRows() / 2. * geom.getDeltaY();
            double pz = (grid.maximalElevation() + grid.minimalElevation()) / 2.;
            
            System.out.println("Origin: "+geom.getOrigin());

            // Camera position and rotation point:
            wl("    <navigationInfo type='\"EXAMINE\" \"WALK\" \"FLY\" \"ANY\"'></navigationInfo>");
            w("    <Viewpoint description=\"Top view\" orientation=\"1 0 0 -1.57\" position=\"");
            w("" + px + " " + (lExaggeration * 10. * pz) + " " + py);
            w("\" centerOfRotation=\"" + px + " " + (lExaggeration * pz) + " " + py + "\"></Viewpoint>");
            wl();
            
            w("    <MetadataDouble DEF=\"origin\" name=\"elevation_origin\"" +
            		" value='"+
                    (geom.envelope().getXMin() - geom.getDeltaX() / 2.) + ", " + 
                    (geom.envelope().getYMin() - geom.getDeltaY() / 2.) + 
                    "'></MetadataDouble>");
            wl();
            
            wl("    <Transform id=\"elevationTransform\" scale=\"1 " + (lExaggeration) + " 1\">");
            wl("    <Shape>");
            wl("      <Appearance>");
            wl("        <Material/>");
            wl("      </Appearance>");
            w("      <ElevationGrid id=\"grid\" solid=\"false\"");

            // Grid parameters:
            w(" xDimension=\"" + geom.numberOfColumns());
            w("\" xSpacing=\"" + geom.getDeltaX());
            w("\" zDimension=\"" + geom.numberOfRows());
            w("\" zSpacing=\"" + geom.getDeltaY());
            wl("\"");
            
            w("      height=\"");

            DecimalFormat dfZ = this.getDecimalFormatZ();

            // Elevation-values:
            for (int i = geom.numberOfRows() - 1; i >= 0; i--) {
                for (int j = 0; j < geom.numberOfColumns(); j++) {
                    w("" + dfZ.format(grid.getValue(i, j)) + " ");
                }
            }
            
            wl("\">");
            wl("      </ElevationGrid>");
            wl("    </Shape>");
            wl("    </Transform>");
            wl("  </Scene>");
            wl("</X3D>");
            if (isX3dom) {
                wl("</body>");
                wl("</html>");
            }
            
            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeSimpleX3d()

    private void writeAsciiXYZ(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException 
    {
        if (grid == null)
            throw new T3dException("Grid information not available.");

        try {
            doc = new BufferedWriter(new FileWriter(filename));

            GmSimple2dGridGeometry geom = ((GmSimple2dGridGeometry) grid.getGeometry());

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int j = 0; j < grid.numberOfColumns(); j++) {
                for (int i = 0; i < grid.numberOfRows(); i++) 
                {
                    if (grid.isSet(i, j)) {
                        VgPoint pt = geom.getVertexPoint(i, j);                        
                        wl(
                        	dfXY.format(pt.getX()) + " " +
                        	dfXY.format(pt.getY()) + " " +
                        	dfZ.format(grid.getValue(i, j)));
                    }
                }
            }
            doc.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
    } // writeAsciiXYZ()
    
    private void writeSimpleObj(GmSimpleElevationGrid grid, String filename) 
    	throws T3dException
    {
        try {
        	doc = new BufferedWriter(new FileWriter(filename));
        	
        	GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();
            
            double 
            	scale = 1.,
            	offsetX = 0.,
            	offsetY = 0.;  
            
            wl("# test file");
            wl("o ElevationGrid");
            
            // Write vertex information:
            // DecimalFormat dfXY = this.getDecimalFormatXY();
            // DecimalFormat dfZ = this.getDecimalFormatZ();
            GmPoint pt;
            for (int j = 0; j < geom.numberOfColumns(); j++) {
                for (int i = 0; i < geom.numberOfRows(); i++) 
                {
                	if (grid.isSet(i, j))
                		pt = new GmPoint(grid.getPoint(i, j));
                	else
                		pt = new GmPoint(0., 0., 0.); // dummy values
                	
                	wl("v "+ (pt.getX() * scale + offsetX) + " " + (pt.getY() * scale + offsetY) + " " + (pt.getZ() * scale) );
                }
            }
            
            wl("s off"); // disable smoothing 
            
            // Write face information:
            int crn1, crn2, crn3, crn4;
            for (int j = 0; j < geom.numberOfColumns() - 1; j++) {
                for (int i = 0; i < geom.numberOfRows() - 1; i++) 
                {
                	if (grid.isSet(i, j) && grid.isSet(i + 1, j) && grid.isSet(i + 1, j + 1) && grid.isSet(i, j + 1)) {
                		crn1 = i + j * geom.numberOfRows();
                		crn2 = (i + 1) + j * geom.numberOfRows();
                		crn3 = (i + 1) + (j + 1) * geom.numberOfRows();
                		crn4 = i + (j + 1) * geom.numberOfRows();
                    	wl("f " + (crn1 + 1) + " " + (crn2 + 1) + " " + (crn3 + 1) + " " + (crn4 + 1));
                	}
                }
            }
            
			doc.close();	
		} 
        catch (IOException e) {
			throw new T3dException(e.getMessage());
		}
    }

    private void writeVtkDataset(GmSimpleElevationGrid grid, String filename) 
       	throws T3dException
    {
        try {
        	doc = new BufferedWriter(new FileWriter(filename));
        	
        	GmSimple2dGridGeometry geom = (GmSimple2dGridGeometry) grid.getGeometry();
            
            wl("# vtk DataFile Version 3.0 generated by 52N Triturus");
            wl("vtk output");
            wl("ASCII");
            wl("DATASET POLYDATA");
            
            int 
            	nRows = geom.numberOfRows(),
            	nCols = geom.numberOfColumns();
            	
            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            
        	wl("POINTS " + (nRows * nCols) + " float");
        	GmPoint pt;
            for (int j = 0; j < geom.numberOfColumns(); j++) {
                for (int i = 0; i < geom.numberOfRows(); i++) 
                {
                	if (grid.isSet(i, j))
                		pt = new GmPoint(grid.getPoint(i, j));
                	else
                		pt = new GmPoint(0., 0., 0.); // dummy values

                    w(dfXY.format(pt.getX()));
                    w(" " + dfXY.format(pt.getY()));
                    wl(" " + dfZ.format(pt.getZ()));
                }
            }
            
            // Pass 1: Determine number of cells
            int nCells = 0;
            for (int j = 0; j < nCols - 1; j++) {
                for (int i = 0; i < nRows - 1; i++) 
                {
                	if (grid.isSet(i, j) && grid.isSet(i + 1, j) && grid.isSet(i + 1, j + 1) && grid.isSet(i, j + 1)) {
                		nCells++;
                	}
                }
            }
            w("POLYGONS " + nCells);
            wl(" " + (5 * nCells));

            // Pass 2: Write cell data
            int crn1, crn2, crn3, crn4;
            for (int j = 0; j < nCols - 1; j++) {
                for (int i = 0; i < nRows - 1; i++) 
                {
                	if (grid.isSet(i, j) && grid.isSet(i + 1, j) && grid.isSet(i + 1, j + 1) && grid.isSet(i, j + 1)) {
                		crn1 = i + j * nRows;
                		crn2 = (i + 1) + j * nRows;
                		crn3 = (i + 1) + (j + 1) * nRows;
                		crn4 = i + (j + 1) * nRows;
                        w("4"); // number of polygon vertices
                        w(" " + crn1);
                        w(" " + crn2);
                        w(" " + crn3);
                        w(" " + crn4);
                        wl();
                	}
                }
            }

            // Write field data:
//          if (this.exportZ)
//          {
	          	wl("POINT_DATA " + (nRows * nCols));            	

	        	wl("SCALARS Z float 1");
	        	wl("LOOKUP_TABLE default");

	            for (int j = 0; j < geom.numberOfColumns(); j++) {
	                for (int i = 0; i < geom.numberOfRows(); i++) 
	                {
			        	if (grid.isSet(i, j))
		            		pt = new GmPoint(grid.getPoint(i, j));
		            	else
		            		pt = new GmPoint(0., 0., 0.); // dummy values
		
		                wl("" + dfZ.format(pt.getZ()));
	                }
	            }
//         	}

			doc.close();	
		} 
        catch (IOException e) {
			throw new T3dException(e.getMessage());
		}
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
