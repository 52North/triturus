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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgIndexedTIN;

import java.io.*;
import java.text.DecimalFormat;

/** 
 * Exporter which writes TINs to files or streams.
 * 
 * @author Benno Schmidt
 */
public class IoTINWriter extends IoAbstractWriter
{
    private String logString = "";
    private String format;

    /**
     * Constructor. As a parameter, format type has to be set. For unsupported
     * file formats, a <tt>T3dNotYetImplException</tt> will be thrown. Currently, 
     * these formats are supported:<br />
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO format</li>
     * <li><i>Vrml1:</i> VRML 1.0 scene (as triangle mesh)</li>
     * <li><i>X3d:</i> X3D scene (as IndexedFaceSet)</li>
     * </ul>
     * 
     * @param pFormat Format string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
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
     * @param pFormat Format string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
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
        if (format.equalsIgnoreCase("AcGeo")) i = 1;
        if (format.equalsIgnoreCase("Vrml1")) i = 2;
        if (format.equalsIgnoreCase("X3d")) i = 3;
        // --> add more formats here...

        try {
            switch (i) {
                case 1: this.writeAcadGeoTIN(tin, filename); break;
                case 2: this.writeSimpleVrml(tin, filename); break;
                case 3: this.writeSimpleX3d(tin, filename); break;
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
			FileWriter fWriter = new FileWriter(filename);
			BufferedWriter w = new BufferedWriter(fWriter);

			//GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();
			VgIndexedTIN geom = (VgIndexedTIN) tin.getGeometry();

			w.write("TINBEGIN");
			w.newLine();
			w.write("FORMAT R=OFF C=OFF");
			w.newLine();
			w.write("TIN:");
			w.newLine();

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

			w.write("POINTS " + geom.numberOfPoints() + "\n");
			for (int i = 0; i < geom.numberOfPoints(); i++) {
				w.write(dfXY.format(geom.getPoint(i).getX()) + " ");
				w.write(dfXY.format(geom.getPoint(i).getY()) + " ");
				w.write(dfZ.format(geom.getPoint(i).getZ()) + "\n");
			}
			
			w.write("TRIANGLES " + geom.numberOfTriangles()+"\n");
			for (int i = 0; i < geom.numberOfTriangles(); i++) {
				w.write(geom.getTriangleVertexIndices(i)[0] + " ");
				w.write(geom.getTriangleVertexIndices(i)[1] + " ");
				w.write(geom.getTriangleVertexIndices(i)[2] + "\n");
			}
			w.write("END");
			w.newLine();
			
			w.close();
			fWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	    
    private void writeSimpleVrml(GmSimpleTINFeature tin, String filename) throws T3dException
    {
        try {
            FileWriter fWriter = new FileWriter(filename);
            BufferedWriter w = new BufferedWriter(fWriter);

            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();

            w.write("#VRML V1.0 ascii"); w.newLine();
            w.newLine();
            w.write("Separator {"); w.newLine();
            w.newLine();
            w.write("  DEF SceneInfo Info {"); w.newLine();
            w.write("    string \"Generated by 52n Triturus\""); w.newLine();
            w.write("  }"); w.newLine();
            w.write("  ShapeHints {"); w.newLine();
            w.write("    vertexOrdering CLOCKWISE"); w.newLine();
            w.write("    shapeType SOLID"); w.newLine();
            w.write("    faceType CONVEX"); w.newLine();
            w.write("    creaseAngle 0.0"); w.newLine();
            w.write("  }"); w.newLine();
            w.newLine();
            w.write("  DEF Green_DEM Separator {"); w.newLine();
            w.write("    Material {\n");
            w.write("      diffuseColor 0.0 1.0 0.0"); w.newLine();
            w.write("      ambientColor 0.0 0.1 0.0"); w.newLine();
            w.write("      specularColor 0.8 0.8 0.8"); w.newLine();
            w.write("      shininess 0.1"); w.newLine();
            w.write("    }"); w.newLine();
            w.write("    Coordinate3 {"); w.newLine();
            w.write("      point ["); w.newLine();
            
            // VRML Section 1 (vertices):

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int i = 0; i < geom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(geom.getPoint(i));
                w.write("        " + dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
                w.newLine();
            }

            w.write("      ]"); w.newLine();
            w.write("    }"); w.newLine();
            w.newLine();

            // VRML Section 2 (triangulation):

            w.write("    IndexedFaceSet {"); w.newLine();
            w.write("      coordIndex ["); w.newLine();

            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
            	crn = geom.getTriangleVertexIndices(i);
                w.write("        " + crn[0] + ", " + crn[1] + ", " + crn[2] + ", -1,"); w.newLine();
            }

            w.write("      ]"); w.newLine();
            w.write("    }"); w.newLine();
            w.write("  }"); w.newLine();
            w.write("}"); w.newLine();

            w.close();
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
            FileWriter fWriter = new FileWriter(filename);
            BufferedWriter w = new BufferedWriter(fWriter);

            GmSimpleTINGeometry geom = (GmSimpleTINGeometry) tin.getGeometry();

            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); 
            w.newLine();
            w.write("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.2//EN\" \"http://www.web3d.org/specifications/x3d-3.2.dtd\">"); 
            w.newLine();
            w.write("<X3D profile=\"Interchange\">"); 
            w.newLine();
            w.write("  <Scene>"); 
            w.newLine();
            w.write("    <Shape>"); 
            w.newLine();
            w.write("      <Appearance><Material/></Appearance>"); 
            w.newLine();
            
            w.write("        <IndexedFaceSet solid=\"TRUE\" coordIndex=\""); 
            w.newLine();
            int crn[];
            for (int i = 0; i < geom.numberOfTriangles(); i++) {
            	crn = geom.getTriangleVertexIndices(i);
                w.write(crn[0] + " " + crn[1] + " " + crn[2] + " -1"); 
            	if (i < geom.numberOfTriangles() - 1) 
            		w.write(", "); 
                w.newLine();
            }
            w.write("        \">"); 
            w.newLine();
             
            w.write("          <Coordinate point=\""); 
            w.newLine();
            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            for (int i = 0; i < geom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(geom.getPoint(i));
                w.write(dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
            	if (i < geom.numberOfPoints() - 1) 
            		w.write(", "); 
                w.newLine();
            }
            w.write("          \"/>"); 
            w.newLine();
            
            w.write("        </Coordinate>"); 
            w.newLine();
            w.write("      </IndexedFaceSet>"); 
            w.newLine();
            w.write("    </Shape>"); 
            w.newLine();
            w.write("  </Scene>"); 
            w.newLine();
            w.write("</X3D>"); 
            w.newLine();
            
            w.close();
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
}
