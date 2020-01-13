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
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgIndexedWedgeMesh;

import java.io.*;
import java.text.DecimalFormat;

/** 
 * Writer which exports indexed 3-D wedge meshes to files or streams. 
 * 
 * @author Benno Schmidt
 */
public class IoWedgeMeshWriter extends IoAbstractWriter
{
    private String logString = "";
    private String format;
    private BufferedWriter doc;
    
    public boolean 
    	exportWedgeId = false, 
    	exportVerticalThickness = false, 
    	exportAssumedThickness = false,
    	exportThicknessDelta = false;

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
    public IoWedgeMeshWriter(String format) {
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
    public void generateWedgeIds() {
    	this.exportWedgeId = true;
    }

    /**
     * instructs the writer to export <i>vertical thickness</i> values (i.e.,
     * z-value differences) as numerical wedge attributes to the target file. 
     * Note that the values might negative if the wedges base triangle does not
     * lie under the opposite triangle face (with respect to the z-axis).
     * 
     * @see VgWedge
     */
    public void generateVerticalThicknessAttr() {
    	this.exportVerticalThickness = true;
    }

    /**
     * instructs the writer to export <i>assumed thickness</i> values as 
     * numerical vertex attributes to the target file. Here, an &quot;assumed 
     * thickness&quot; t' will be calculated making use of the vertex-specific 
     * dip values <i>alpha1</i> and <i>alpha2</i> for the opposite wedge 
     * triangles and the vertical thickness t (in the direction of -z): 
     * <i>t' = t * (cos(alpha1) + cos(alpha2)) / 2</i>.
     */
    public void generateAssumedThicknessAttr() {
    	this.exportAssumedThickness = true;
    }

    /**
     * instructs the writer to export the differences between vertical and 
     * assumed thickness, i.e. <i>t - t'</i>, where <i>t</i> gives the vertical
     * thickness and <i>t'</i> the assumed thickness. 
     * 
     * @see generateVerticalThicknessAttr
     * @see generateAssumedThicknessAttr
     */
    public void generateThicknessDeltaAttr() {
    	this.exportThicknessDelta = true;
    }


    /**
     * writes the wedge mesh to a file.
     * 
     * @param mesh Mesh to be written
     * @param filename File path
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void writeToFile(VgIndexedWedgeMesh mesh, String filename) 
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
    
    private void writeVTKUnstructuredGrid(VgIndexedWedgeMesh mesh, String filename) 
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
            
            w("CELLS " + mesh.numberOfWedges());
            wl(" " + (7 * mesh.numberOfWedges()));
            for (int i = 0; i < mesh.numberOfWedges(); i++) {
                w("6"); // number of a wedges's vertices
                int[] v = mesh.getWedgeVertexIndices(i);
                wl(
                	" " + v[0] + 
                	" " + v[1] + 
                	" " + v[2] + 
                	" " + v[3] + 
                	" " + v[4] + 
                	" " + v[5]);
            }

            wl("CELL_TYPES " + mesh.numberOfWedges());
            for (int i = 0; i < mesh.numberOfWedges(); i++) {
                wl("13"); // VTK type number
            }

            if (
            	this.exportWedgeId || 
            	this.exportVerticalThickness || 
            	this.exportAssumedThickness || 
            	this.exportThicknessDelta) 
            {
	          	wl("CELL_DATA " + mesh.numberOfWedges());            	
	
	          	if (this.exportWedgeId) {
		        	wl("SCALARS WEDG_ID int 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfWedges(); i++) {
		                wl("" + i);
		            }
	          	}
	          	
	          	if (this.exportVerticalThickness) {
		        	wl("SCALARS VERTICAL_THICKNESS float 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfWedges(); i++) {
		                int[] v = mesh.getWedgeVertexIndices(i);
		                double dz = verticalThickness(mesh, v);
		                wl("" + dz);
		            }
	            }            

	          	if (this.exportAssumedThickness) {
		        	wl("SCALARS ASSUMED_THICKNESS float 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfWedges(); i++) {
		                int[] v = mesh.getWedgeVertexIndices(i);
		                double t = assumedThickness(mesh, v);
		                wl("" + t);
		            }
	            }            

	          	if (this.exportThicknessDelta) {
		        	wl("SCALARS THICKNESS_DELTA float 1");
		        	wl("LOOKUP_TABLE default");
		            for (int i = 0; i < mesh.numberOfWedges(); i++) {
		                int[] v = mesh.getWedgeVertexIndices(i);
		                double 
		                	dz = verticalThickness(mesh, v),
		                	t = assumedThickness(mesh, v),
		                	delta = dz - t;
		                wl("" + delta);
		            }
	            }            
            }
            
            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

	static final T3dVector 
		vec000 = new T3dVector(0., 0., 0.),
		vec001 = new T3dVector(0., 0., 1.);

	private double assumedThickness(VgIndexedWedgeMesh mesh, int[] v) {
		GmTriangle 
			tri1 = new GmTriangle(
				mesh.getPoint(v[0]),
				mesh.getPoint(v[1]),
				mesh.getPoint(v[2])),
			tri2 = new GmTriangle(
				mesh.getPoint(v[3]),
				mesh.getPoint(v[4]),
				mesh.getPoint(v[5]));
		T3dVector 
			norm1 = tri1.normal(),
			norm2 = tri2.normal();
		double 
			alpha1 = norm1.angle(vec000, vec001),
			alpha2 = norm2.angle(vec000, vec001),
			dz = verticalThickness(mesh, v),
			t1 = dz * Math.cos(alpha1),
			t2 = dz * Math.cos(alpha2),
			t = (t1 + t2) / 2.;
		return t;
	}

	private double verticalThickness(VgIndexedWedgeMesh mesh, int[] v) {
		double
			z0 = mesh.getPoint(v[0]).getZ(),
			z1 = mesh.getPoint(v[1]).getZ(),
			z2 = mesh.getPoint(v[2]).getZ(),
			z3 = mesh.getPoint(v[3]).getZ(),
			z4 = mesh.getPoint(v[4]).getZ(),
			z5 = mesh.getPoint(v[5]).getZ();
		double dz = 
			this.max(z0, z1, z2, z3, z4, z5) - 
			this.min(z0, z1, z2, z3, z4, z5);
		return dz;
	}

    private double min(double z0, double z1, double z2, double z3, double z4, double z5) {
    	double min = z0 < z1 ? z0 : z1;
    	min = z2 < min ? z2 : min;
    	min = z3 < min ? z3 : min;
    	min = z4 < min ? z4 : min;
    	return z5 < min ? z5 : min;
    }

    private double max(double z0, double z1, double z2, double z3, double z4, double z5) {
    	double max = z0 > z1 ? z0 : z1;
    	max = z2 > max ? z2 : max;
    	max = z3 > max ? z3 : max;
    	max = z4 > max ? z4 : max;
    	return z5 > max ? z5 : max;
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
