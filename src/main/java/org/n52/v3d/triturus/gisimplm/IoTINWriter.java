/**
 * Copyright (C) 2007-2018 52 North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgIndexedTIN;

import java.io.*;
import java.text.DecimalFormat;

/** 
 * Writer which exports TINs to files or streams. Various formats such as
 * X3D oder HTML5/X3DOM are supported. 
 * 
 * @author Benno Schmidt
 */
public class IoTINWriter extends IoAbstractWriter
{
    /**
     * File-format type identifier to be used for export in ACADGEO format.
     */
    public static final String ACGEO = "AcGeo";
    /**
     * File-format type identifier to be used for (old-fashioned) VRML1 export.
     */
    public static final String VRML1 = "Vrml1";
    /**
     * File-format type identifier to be used for X3D export.
     */
    public static final String X3D = "X3d";
    /**
     * File-format type identifier to be used for export to a HTML5/X3DOM 
     * description.
     */
    public static final String X3DOM = "X3Dom";
    /**
     * File-format type identifier to be used for Wavefront OBJ export.
     */
    public static final String OBJ = "Obj";

    private String logString = "";
    private String format;
    private BufferedWriter doc;
    
    /**
     * Constructor. As a parameter, format type has to be set. For unsupported
     * file formats, a <tt>T3dNotYetImplException</tt> will be thrown. Currently, 
     * these formats are supported:<br />
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO format</li>
     * <li><i>Vrml1:</i> VRML 1.0 scene (as plain triangle mesh)</li>
     * <li><i>X3d:</i> X3D scene (as IndexedFaceSet without viewpoint setting)</li>
     * <li><i>X3Dom:</i> X3D scene (as IndexedFaceSet without viewpoint setting)</li>
     * <li><i>Obj:</i> Wavefront OBJ file</li>
     * </ul>
     * 
     * @param format Format string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
     * @see IoTINWriter#ACGEO
     * @see IoTINWriter#VRML1
     * @see IoTINWriter#X3D
     * @see IoTINWriter#X3DOM
     * @see IoTINWriter#OBJ
     */
    public IoTINWriter(String format) {
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

    /** 
     * sets the format type.
     * 
     * @param format Format string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
     */
    public void setFormatType(String format)
    {
        this.format = format;
    }

    /**
     * writes the TIN to a file.
     * 
     * @param tin TIN to be written
     * @param filename File path
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void writeToFile(GmSimpleTINFeature tin, String filename) 
        throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (format.equalsIgnoreCase(ACGEO)) i = 1;
        if (format.equalsIgnoreCase(VRML1)) i = 2;
        if (format.equalsIgnoreCase(X3D)) i = 3;
        if (format.equalsIgnoreCase(X3DOM)) i = 4;
        if (format.equalsIgnoreCase(OBJ)) i = 5;
        // --> add more formats here...

        try {
            switch (i) {
                case 1: this.writeAcadGeoTIN(tin, filename); break;
                case 2: this.writeSimpleVrml(tin, filename); break;
                case 3: this.writeSimpleX3d(tin, filename); break;
                case 4: this.writeSimpleX3Dom(tin, filename); break;
                case 5: this.writeSimpleObj(tin, filename); break;
                // --> add more formats here...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }
    
    private void writeAcadGeoTIN(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));
            
            //GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
            VgIndexedTIN geom = (VgIndexedTIN) tin.getGeometry();
            
            wl("TINBEGIN");
            wl("FORMAT R=OFF C=OFF");
            wl("TIN:");
            
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            
            wl("POINTS " + geom.numberOfPoints());
            for (int i = 0; i < geom.numberOfPoints(); i++) {
                w(dfXY.format(geom.getPoint(i).getX()));
                w(" " + dfXY.format(geom.getPoint(i).getY()));
                wl(" " + dfZ.format(geom.getPoint(i).getZ()));
            }
            
            wl("TRIANGLES " + geom.numberOfTriangles());
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
                w("" + geom.getTriangleVertexIndices(i)[0]);
                w(" " + geom.getTriangleVertexIndices(i)[1]);
                wl(" " + geom.getTriangleVertexIndices(i)[2]);
            }
            wl("END");
            
            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeSimpleVrml(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));
            
            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
            
            wl("#VRML V1.0 ascii"); 
            wl();
            wl("Separator {"); 
            wl();
            wl("  DEF SceneInfo Info {"); 
            wl("    string \"Generated by 52n Triturus\""); 
            wl("  }"); 
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
            
            // VRML Section 1 (vertices):
            
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            
            for (int i = 0; i < geom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(geom.getPoint(i));
                wl("        " + dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
            }
            
            wl("      ]"); 
            wl("    }"); 
            wl();
            
            // VRML Section 2 (triangulation):
            
            wl("    IndexedFaceSet {"); 
            wl("      coordIndex ["); 
            
            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
                crn = geom.getTriangleVertexIndices(i);
                wl("        " + crn[0] + ", " + crn[1] + ", " + crn[2] + ", -1,"); 
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
    }
    
    private void writeSimpleX3d(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));
            
            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
            
            wl("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); 
            wl("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.2//EN\" \"http://www.web3d.org/specifications/x3d-3.2.dtd\">"); 
            wl("<X3D profile=\"Interchange\">"); 
            wl("  <Scene>"); 
            wl("    <Shape>"); 
            wl("      <Appearance><Material/></Appearance>"); 
            
            wl("        <IndexedFaceSet solid=\"TRUE\" coordIndex=\""); 
            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
                crn = geom.getTriangleVertexIndices(i);
                w(crn[0] + " " + crn[1] + " " + crn[2] + " -1"); 
                if (i < geom.numberOfTriangles() - 1) 
                     w(", "); 
                wl();
            }
            wl("        \">"); 
             
            wl("          <Coordinate point=\""); 
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            for (int i = 0; i < geom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(geom.getPoint(i));
                w(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
                if (i < geom.numberOfPoints() - 1) 
                    w(", "); 
                wl();
            }
            wl("          \"/>"); 
            
            wl("        </Coordinate>"); 
            wl("      </IndexedFaceSet>"); 
            wl("    </Shape>"); 
            wl("  </Scene>"); 
            wl("</X3D>"); 
   
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
    }
    
    private void writeSimpleX3Dom(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));
            
            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
            
            wl("<html>"); 
            wl("  <head>"); 
            wl("    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>"); 
            wl("    <title>52n Triturus TIN</title>"); 
            wl("    <script type='text/javascript' src='https://www.x3dom.org/download/x3dom.js'> </script>"); 
            wl("    <link rel='stylesheet' type='text/css' href='https://www.x3dom.org/download/x3dom.css'></link>"); 
            wl("  </head>"); 
            wl("  <body>"); 
            wl("    <h1>52n Triturus TIN export page</h1>"); 
            wl("    <p>This HTML page contains an interactive 3D scene (Internet connection required).</p>"); 
            wl("    <x3d width='500px' height='400px'>"); 
            wl("      <scene>"); 
            wl("        <shape>"); 
            wl("          <appearance>"); 
            wl("            <material diffuseColor='1 0 0'></material>"); 
            wl("          </appearance>"); 
            wl("          <IndexedFaceSet solid='false' coordIndex='"); 
 
            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
                crn = geom.getTriangleVertexIndices(i);
                w(crn[0] + " " + crn[1] + " " + crn[2]); 
                if (i < geom.numberOfTriangles() - 1) 
                    w(" -1"); 
                wl();
            }
            wl("          '>"); 
         
            wl("            <Coordinate point='"); 
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            for (int i = 0; i < geom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(geom.getPoint(i));
                w(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
                if (i < geom.numberOfPoints() - 1) 
                    w(","); 
                wl();
            }
            wl("            '>"); 
            wl("            </Coordinate>");
        
            wl("          </IndexedFaceSet>"); 
            wl("        </shape>"); 
            wl("      </scene>"); 
            wl("    </x3d>"); 
            wl("  </body>"); 
            wl("</html>"); 
            
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
    }
    
    private void writeSimpleObj(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
        	doc = new BufferedWriter(new FileWriter(filename));
        	
            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
            
            double 
            	scale = 1.0,
            	offsetX = 0.0,
            	offsetY = 0.0;  
/*
            double extX = geom.envelope().getExtentX();
            double extY = geom.envelope().getExtentY();              

            // see calculateNormTransformation() of VsSimpleScene
            if (Math.abs(extX) > Math.abs(extY)) {
          	  scale = 2./extX; 
          	  offsetX = -(geom.envelope().getXMin() + geom.envelope().getXMax()) / extX;
              offsetY = -(geom.envelope().getYMin() + geom.envelope().getYMax()) / extX;
            }
            else{
            	scale = 2./extY; 
            	offsetX = -(geom.envelope().getXMin() + geom.envelope().getXMax()) / extY;
                offsetY = -(geom.envelope().getYMin() + geom.envelope().getYMax()) / extY;
            }
*/
            
            wl("# test file");
            wl("o TIN");
            
            // Write vertex information:
            // DecimalFormat dfXY = this.getDecimalFormatXY();
            // DecimalFormat dfZ = this.getDecimalFormatZ();
            for (int i = 0; i < geom.numberOfPoints(); i++){
            	GmPoint pt = new GmPoint(geom.getPoint(i));
            	wl("v "+ (pt.getX() * scale + offsetX) + " " + (pt.getY() * scale + offsetY) + " " + (pt.getZ() * scale) );

            }
            
            wl("s off"); // disable smoothing 
            
            // Write triangle face information:
            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++){
            	crn = geom.getTriangleVertexIndices(i);
            	wl("f " + (crn[0] + 1) + " " + (crn[1] + 1) + " " + (crn[2] + 1));
            }
            
			doc.close();
		} catch (IOException e) {
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
