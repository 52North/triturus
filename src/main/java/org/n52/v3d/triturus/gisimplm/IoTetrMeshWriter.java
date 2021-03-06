/**
 * Copyright (C) 2020 52 North Initiative for Geospatial Open Source
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

import org.n52.v3d.triturus.core.IoFormatType;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgIndexedTetrMesh;

import java.io.*;
import java.text.DecimalFormat;

/** 
 * Writer which exports indexed tetrahedronal meshes to files or streams. 
 * 
 * @author Benno Schmidt
 */
public class IoTetrMeshWriter extends IoAbstractWriter
{
    private String logString = "";
    private String format;
    private BufferedWriter doc;
    
    public boolean 
    	exportTetrId = false, 
    	exportVerticalThickness = false, 
    	exportZ = false; 

    /**
     * Constructor. As a parameter, the format type has to be set. For 
     * unsupported file formats, a <tt>T3dNotYetImplException</tt> will be 
     * thrown. Currently, these formats are supported:<br />
     * <ul>
     * <li><i>VTK:</i> VTK 3.0 format (polydata dataset)</li>
     * </ul>
     * 
     * @param format Format string (e.g. <tt></tt>&quot;VTK&quot;</tt>)
     * @see IoFormatType#VTK_DATASET
     */
    public IoTetrMeshWriter(String format) {
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

    /** 
     * sets the format type.
     * 
     * @param format Format string (e.g. <tt></tt>&quot;VTKDataset&quot;</tt>)
     */
    public void setFormatType(String format)
    {
        this.format = format;
    }

    /**
     * instructs the writer to export tetrahedron IDs to the target file.
     */
    public void generateTetrIds() {
    	this.exportTetrId = true;
    }

    /**
     * instructs the writer to export <i>vertical thickness</i> values (i.e.,
     * z-value differences) as numerical tetrahedron attributes to the target 
     * file. Note that the absolute value (which is always greater or equal to
     * 0) will be exported.
     */
    public void generateVerticalThicknessAttr() {
    	this.exportVerticalThickness = true;
    }

    /**
     * instructs the writer to export z-values as numerical vertex attributes 
     * to the target file.
     */
    public void generateZAttr() {
    	this.exportZ = true;
    }

    /**
     * writes the tetrahedronal mesh to a file.
     * 
     * @param mesh Mesh to be written
     * @param filename File path
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void writeToFile(VgIndexedTetrMesh mesh, String filename) 
        throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (format.equalsIgnoreCase(IoFormatType.VTK_DATASET)) i = 1;
        // --> add more formats here...

        try {
            switch (i) {
                case 1: this.writeVTKUnstructuredGrid(mesh, filename); break;
                // --> add more formats here...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }
    
    private void writeVTKUnstructuredGrid(VgIndexedTetrMesh mesh, String filename) 
        throws T3dException
    {
        try {
            doc = new BufferedWriter(new FileWriter(filename));
            
            wl("# vtk DataFile Version 3.0 generated by 52N Triturus");
            wl("vtk output");
            wl("ASCII");
            wl("DATASET UNSTRUCTURED_GRID");
            
            DecimalFormat dfXY = this.getDecimalFormatXY();
            DecimalFormat dfZ = this.getDecimalFormatZ();
            
            wl("POINTS " + mesh.numberOfPoints() + " float");
            for (int i = 0; i < mesh.numberOfPoints(); i++) {
                w(dfXY.format(mesh.getPoint(i).getX()));
                w(" " + dfXY.format(mesh.getPoint(i).getY()));
                wl(" " + dfZ.format(mesh.getPoint(i).getZ()));
            }
            
            w("CELLS " + mesh.numberOfTetrahedrons());
            wl(" " + (5 * mesh.numberOfTetrahedrons()));
            for (int i = 0; i < mesh.numberOfTetrahedrons(); i++) {
                w("4"); // number of a tetrahedron's vertices
                int[] v = mesh.getTetrahedronVertexIndices(i);
                wl(" " + v[0] + " " + v[1] + " " + v[2] + " " + v[3]);
            }

            wl("CELL_TYPES " + mesh.numberOfTetrahedrons());
            for (int i = 0; i < mesh.numberOfTetrahedrons(); i++) {
                wl("10"); // VTK type number
            }

            if (this.exportTetrId || this.exportVerticalThickness) 
            {
	          	wl("CELL_DATA " + mesh.numberOfTetrahedrons());            	
	
	          	if (this.exportTetrId) {
		        	wl("SCALARS TETR_ID int 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfTetrahedrons(); i++) {
		                wl("" + i);
		            }
	          	}
	          	
	          	if (this.exportVerticalThickness) {
		        	wl("SCALARS VERTICAL_THICKNESS float 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfTetrahedrons(); i++) {
		                int[] v = mesh.getTetrahedronVertexIndices(i);
		                double
		                	z0 = mesh.getPoint(v[0]).getZ(),
		                	z1 = mesh.getPoint(v[1]).getZ(),
		                	z2 = mesh.getPoint(v[2]).getZ(),
		                	z3 = mesh.getPoint(v[3]).getZ();
		                double dz = this.max(z0, z1, z2, z3) - this.min(z0, z1, z2, z3);
		                wl("" + Math.abs(dz));
		            }
	            }            
            }
            
          	if (this.exportZ)
          	{
	          	wl("POINT_DATA " + mesh.numberOfPoints());            	

	        	wl("SCALARS Z float 1");
	        	wl("LOOKUP_TABLE default");
	            for (int i = 0; i < mesh.numberOfPoints(); i++) {
	                wl("" + mesh.getPoint(i).getZ());
	            }
            }

            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double min(double z0, double z1, double z2, double z3) {
    	double min = z0 < z1 ? z0 : z1;
    	min = z2 < min ? z2 : min;
    	return z3 < min ? z3 : min;
    }

    private double max(double z0, double z1, double z2, double z3) {
    	double max = z0 > z1 ? z0 : z1;
    	max = z2 > max ? z2 : max;
    	return z3 > max ? z3 : max;
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
