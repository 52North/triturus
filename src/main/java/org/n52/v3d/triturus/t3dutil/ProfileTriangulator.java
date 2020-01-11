/**
 * Copyright (C) 2007-2015 52North Initiative for Geospatial Open Source
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
 * license version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.t3dutil;

import java.util.ArrayList;

/**
 * @deprecated
 * The <tt>ProfileTriangulator</tt> allows to triangulate "profiles" (e.g., 
 * given cross-section data) and common line segments. 
 * 
 * @author Torsten Heinen
 */
public class ProfileTriangulator 
{
	// Diese Klasse verbindet Profile und allgemeine Liniensegmente zu einem TIN.
	// Zur Triangulierung wird intern bis jetzt nur ein 2D Delaunay Algorithmus 
	// verwendet.
	// Die 3D Punkte der Profile werden f�r jedes Profil einzelnd und nacheinander
	// �bergeben. Dabei ist es m�glich ein Profil auch segmentiert, d.h. in verschiedenen
	// Bereiche (z.B. Uferbereich, Hang und Flussbett) aufgeteilt anzugeben. Diese Bereiche
	// werden dann getrennt voneinander mit dem darauffolgenden Profil trianguliert.

	public final int DELAUNAY = 0;
	private int triangulationType;
	
	private Delaunay del;
	private double[][] prevPoints;	
	
	private ArrayList tinPoints = new ArrayList(200);
	private ArrayList tinIndizes = new ArrayList(600);
	private int numProfiles = 0;
	
	public ProfileTriangulator() {
		triangulationType = this.DELAUNAY;
	}
	
	public ProfileTriangulator(int type) {
		triangulationType = type;
	}
	
	/**
	 * provides the vertex points used to set-up the triangulation.
	 * 
	 * @return Array if used 3-D points x1,y1,z1,...xn,yn,zn
	 */
	public double[] resultPoints() {
		double[] ret = new double[tinPoints.size()];
		for (int i=0; i<tinPoints.size();i++) {
			ret[i] = ((Double)tinPoints.get(i)).doubleValue();
			//System.out.println("resultPoints: " + i + ") "+ ret[i]);
		}
		return ret;			
	}

	/**
	 * provides the triangle indices if the result TIN.
	 * 
	 * @return Array holding triangle indices (t1_1, t1_2, t1_3, t2_1 ...)
	 */
	public int[] resultIndizes() {
		int[] ret = new int[tinIndizes.size()];
		for (int i = 0; i < tinIndizes.size(); i++) {
			ret[i] = ((Integer) tinIndizes.get(i)).intValue();
		}
		return ret;			
	}
	
	/**
	 * adds "profiles" to be triangulated. Note that the order in which the 
	 * profiles are given is relevant. The array <tt>points</tt> may contain 
	 * from different segments. These segments will be triangulated 
	 * independently from each other and will be connected to the next profile.
	 * <br/> 
	 * <b>Important note:</b> All given profiles must consist of the same 
	 * number of segments! 
	 * <br/>
	 * Example:<br/>
	 * double[0][x1,y1,z1,...xn,yn,zn] = Points of a profile from the left waterside
	 * double[1][x1,y1,z1,...xn,yn,zn] = Points of the river bed
	 * double[2][x1,y1,z1,...xn,yn,zn] = Points of a profile from the right waterside
	 * <br/>or:</br/>
	 * double[0][x1,y1,z1,...xn,yn,zn] = Points of a single profile 
	 */
	public void addSegmentedProfiles(double[][] points) {
		if (numProfiles!=0) {
			triangulate(prevPoints, points);
			storePoints(points);
		}
		else {
			storePoints(points);
		}
		numProfiles++;
	}
	
	private void storePoints(double[][] p) {
		prevPoints = new double[p.length][];
		for (int i = 0; i < p.length; i++) {
			prevPoints[i] = new double[p[i].length];
			System.arraycopy(p[i], 0, prevPoints[i], 0, p[i].length);
			//tinPoints.add(p);				
		}
	}
	
	private void triangulate(double[][] prevPoints, double[][] points) {		
		// for each set of points...
		for (int i=0; i<points.length; i++) {
			// convert from 3-D to 2-D...
			double[] points2D;
			try {
				points2D = mergeAndConvert(i, prevPoints, points);
				if (points2D.length != 0) {
					//System.out.println("Triangulating 3D PointSet " + i + " / 2D points: "+points2D.length/2 + " / new 3D Points: " + points[i].length/3 + " / old 3D Points: " + prevPoints[i].length/3);
					del = new Delaunay(points2D);
					addIndizes(del.getIndices(), prevPoints[i].length / 3);
					add3DPoints(prevPoints[i]);
					add3DPoints(points[i]);
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	/**
	 * merges 3-D arrays holding the current and the last profile. Here, 3-D
	 * coordinates will be converted to 2-D coordinates excluding the z-value
	 * (x1, y1, z1)->(x1, y1)
	 * 
	 * @param prevPoints
	 * @param points
	 * @return
	 */
	private double[] mergeAndConvert(
		int segmentIndex, double[][] prevPoints, double[][] points) 
		throws Exception 
	{
		if (prevPoints.length != points.length) {
			throw new Exception("Number of Segments must be equal."); 
		}
		int pointCount = points[segmentIndex].length;
		int prevPointCount = prevPoints[segmentIndex].length;
		int pointCount2D = pointCount-pointCount/3;
		int prevPointCount2D = prevPointCount-prevPointCount/3;
		
		double[] points2D = new double[pointCount2D + prevPointCount2D]; // allocate 2/3 storage
		if (pointCount + prevPointCount != 0) {
//			System.out.println("Points (p+pp): " + (pointCount + prevPointCount));
//			System.out.println("Points (array): " + points2D.length);
//			System.out.println("Points2D: " + pointCount2D);
//			System.out.println("Prev2D  : " + prevPointCount2D);
			if (prevPoints[segmentIndex].length != 0) {
				for (int j = 0; j < prevPointCount2D; j = j + 2) {
//					System.out.println("convert Prev: " + j + "/" + prevPoints[segmentIndex].length);
//					System.out.println("convert Prev: " + (j+0) + "->" + (j/2+j+0) + " AND " + (j+1) +"->" + (j/2+j+1));
					points2D[j + 0] = prevPoints[segmentIndex][j/2 + j + 0]; 
					points2D[j + 1] = prevPoints[segmentIndex][j/2 + j + 1];
				}
			} 
			if (points[segmentIndex].length != 0) {
				for (int j = 0; j < pointCount2D; j = j + 2) {
//					System.out.println("convert Point: " + j + "/"+points[segmentIndex].length);
//					System.out.println("convert Point: " + (prevPointCount2D+j+0) + "->" +(j/2+j+0) + " UND " + (prevPointCount2D+j+1) +"->" + (j/2+j+1));
					points2D[prevPointCount2D + j + 0] = points[segmentIndex][j/2 + j + 0]; 
					points2D[prevPointCount2D + j + 1] = points[segmentIndex][j/2 + j + 1];
				}
			} 
		}
		return points2D;
	}

	private void addIndizes(int[] ind, int numPrevPoints) {
		// hier noch nach Dreiecken suchen, die alle Punkte auf einem Profil haben...		
		for (int i=0; i<ind.length;i=i+3) {
			if (i<ind.length-2 && ind[i]<numPrevPoints && ind[i+1]<numPrevPoints && ind[i+2]<numPrevPoints) {
				// ignoriere das Dreieck
				//System.out.println("Falsches Dreieck (in Prev) gefunden: "+ind[i] + " " + ind[i+1] + " " +ind[i+2]);
			}
			else if (i<ind.length-2 && ind[i]>numPrevPoints && ind[i+1]>numPrevPoints && ind[i+2]>numPrevPoints) {
				// ignoriere das Dreieck
				//System.out.println("Falsches Dreieck (in Points) gefunden: "+ind[i] + " " + ind[i+1] + " " +ind[i+2]);
			}			
			else {
				tinIndizes.add(new Integer(ind[i] + (tinPoints.size()/3)));
				tinIndizes.add(new Integer(ind[i+1] + (tinPoints.size()/3)));
				tinIndizes.add(new Integer(ind[i+2] + (tinPoints.size()/3)));
			}
		}	
	}
	
	private void add3DPoints(double[] p3d) {
		for (int i=0; i<p3d.length;i++) {
			tinPoints.add(new Double(p3d[i]));
		}
	}
	
	public String toString() {
		String ret="";
		ret = 	ret + "ProfileTriangulator: " + 
				"\nProfiles: " + numProfiles +
				"\nTIN Points: " + tinPoints.size()/3 +
				"\nTIN Triangles: " + tinIndizes.size()/3 +		 
				"\nTIN Indizes: " + tinIndizes.size() + "\n";
		for (int i = 0; i < tinPoints.size(); i = i + 3) {
			ret = ret + i/3 + ") = " + (Double) tinPoints.get(i) + "/" + (Double) tinPoints.get(i+1) + "/" + (Double) tinPoints.get(i+2) + "\n"; 
		}
		return ret;
	}

	public int getNumPoints() {
		return tinPoints.size()/3;
	}	
	
   	public int getNumProfiles() {
	   return numProfiles;
   	}

	public int getNumFaces() {
		return tinIndizes.size()/3;
	}
}
