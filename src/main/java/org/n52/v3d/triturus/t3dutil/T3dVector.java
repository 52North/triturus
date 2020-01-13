/**
 * Copyright (C) 2007-2019 52North Initiative for Geospatial Open Source
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
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/**
 * Class to hold vectors in 3D Euclidian space. Note that instances of 
 * this class will not be handled as <zz>vgis</tt>-geometries. Geospatial 
 * vector metrics has to be managed by the application which uses the 
 * <tt>T3dVector</tt>.
 * 
 * @author Benno Schmidt
 */
public class T3dVector
{
	private double mX, mY, mZ;
	
	/**
	 * Constructor
	 */
	public T3dVector(double x, double y, double z) {
	    mX = x; mY = y; mZ = z;
	}
	
	/**
	 * Zero vector constructor.
	 */
	public T3dVector() {
	    mX = 0.; mY = 0.; mZ = 0.;
	}
	
	/**
	 * Constructor
	 */
	public T3dVector(T3dVector pVec) {
		this.assign(pVec);
	}
	
	/**
	 * Constructor
	 */
	public T3dVector(VgPoint pPnt) {
		this.assign(pPnt);
	}
	
	/**
	 * sets the vector's x-component.
	 */
	public void setX(double x) { 
	    mX = x; 
	}
	
	/**
	 * returns the vector's x-component.
	 */
	public double getX() {
	    return mX;
	}
	
	/**
	 * sets the vector's y-component.
	 */
	public void setY(double y) { 
	    mY = y; 
	}
	
	/**
	 * returns the vector's y-component.
	 */
	public double getY() {
	    return mY;
	}
	
	/**
	 * sets the vector's z-component.
	 */
	public void setZ(double z) {
	    mZ = z;
	}
	
	/**
	 * returns the vector's z-component.
	 */
	public double getZ() {
	    return mZ;
	}
	
	/**
	 * assigns the values of the position vector <tt>p</tt> to the current 
	 * vector object.
	 */
	public void assign(VgPoint p) {
		mX = p.getX();
		mY = p.getY();
		mZ = p.getZ();
	}
	
	/**
	 * assigns the values of the (direction) vector <tt>v</tt> to the current 
	 * vector object.
	 */
	public void assign(T3dVector v) {
		mX = v.getX();
		mY = v.getY();
		mZ = v.getZ();
	}
	
	/**
	 * assigns the values of the difference vector <tt>p2 - p1</tt> to the 
	 * current vector object.
	 */
	public void assignDiff(VgPoint p2, VgPoint p1) {
		mX = p2.getX() - p1.getX();
		mY = p2.getY() - p1.getY();
		mZ = p2.getZ() - p1.getZ();
	}
	
	/**
	 * assigns the values of the difference vector <tt>v2 - v1</tt> to the 
	 * current vector object.
	 */
	public void assignDiff(T3dVector v2, T3dVector v1) {
		mX = v2.getX() - v1.getX();
		mY = v2.getY() - v1.getY();
		mZ = v2.getZ() - v1.getZ();
	}
	
	/**
	 * assigns the values of the vector sum <tt>p1 + p2</tt> to the current 
	 * vector object.
	 */
	public void assignSum(VgPoint p1, VgPoint p2) {
		mX = p1.getX() + p2.getX();
		mY = p1.getY() + p2.getY();
		mZ = p1.getZ() + p2.getZ();
	}
	
	/**
	 * assigns the values of the vector sum <tt>v1 + v2</tt> to the current 
	 * vector object.
	 */
	public void assignSum(T3dVector v1, T3dVector v2) {
		mX = v1.getX() + v2.getX();
		mY = v1.getY() + v2.getY();
		mZ = v1.getZ() + v2.getZ();
	}
	
	/**
	 * returns the scalar product of the vectors <tt>this</tt> and <tt>v</tt>.
	 */
	public double scalarProd(T3dVector v) {
		return mX * v.getX() + mY * v.getY() + mZ * v.getZ();   
	}
	
	/**
	 * assigns the cross product <tt>v1 x v2</tt> to the current vector object.
	 */
		public void assignCrossProd(T3dVector v1, T3dVector v2) {
			mX = v1.getY() * v2.getZ() - v1.getZ() * v2.getY();
			mY = v1.getZ() * v2.getX() - v1.getX() * v2.getZ();
			mZ = v1.getX() * v2.getY() - v1.getY() * v2.getX();
		}
	  
	    /**
	 * returns the absolute value (length) of the vector.
	 */
	public double length() {
		return Math.sqrt(mX * mX + mY * mY + mZ * mZ);  
	}
	
	/** 
	 * normalizes the vector. When method execution finishes, the components of
	 * <tt>this</tt> object will be modified sich that the vector length is 1.
	 * Cf. method <tt>this.norm()</tt>. Note that if the method will be called
	 * for a zero vector, the method call will be without effect.
	 * 
	 * @see T3dVector#norm
	 */
	public void doNorm() {
		double len = this.length();
		if (len == 0.)
			return;
		mX /= len; mY /= len; mZ /= len;
	}
	
	/**
	 * returns a vector with length 1 with the same direction as <tt>this</tt>. 
	 * If the method is called for a zero vector, a <tt>T3dException</tt> will 
	 * be thrown.
	 * 
	 * @see T3dVector#doNorm
	 */
	public T3dVector norm() throws T3dException {
		double len = this.length();
		if (len == 0.)
			throw new T3dException("Tried to normalize vector with length 0.");
		return new T3dVector(mX / len, mY / len, mZ / len);
	}
	
	/**
	 * assigns an normalized vector which is orthogonal to the vectors 
	 * <tt>v1</tt> and <tt>v2</tt> to the current vector object. The resulting
	 * vector is explicitly defined (right-hand rule: <tt>v1</tt> = thumb, 
	 * <tt>v2</tt> = index finger, result vector = middle finger). If no vector
	 * can be determined, a <tt>T3dException</tt> will be thrown.
	 * 
	 * @throws T3dException
	 */
	public void ortho(T3dVector v1, T3dVector v2) {
		this.assignCrossProd(v1, v2);
		this.doNorm();
	}

	/**
	 * returns a rotated vector (rotation in x-y plane, origin as rotation center.
	 * 
	 * @param azimuth Rotation angle in radians (clockwise)
	 * @return Rotated bounding-box as polygon
	 */
	public T3dVector rotateXY(double azimuth) {
		return new T3dVector(
			Math.cos(azimuth) * this.getX() - Math.sin(azimuth) * this.getY(),
			Math.sin(azimuth) * this.getX() + Math.cos(azimuth) * this.getY(),
			this.getZ());
	}

	/**
	 * returns a rotated vector (rotation against x-y plane, i.e. against the 
	 * z-axis, origin as rotation center.
	 * 
	 * @param inclination Rotation angle in radians
	 * @return Rotated bounding-box as polygon
	 */
	public T3dVector rotateZ(double inclination) {
		throw new T3dNotYetImplException("Bounding-box rotation against z-axis");
	}

	/**
	 * returns the angle between the position vectors <tt>pos1</tt>, 
	 * <tt>this</tt> (as apex), and <tt>pos2</tt>. 
	 * <br/>
	 * Note: <tt>pos1</tt>, <tt>pos2</tt> and <tt>this</tt> must be different
	 * from one another, otherwise the result will be 0 here.
	 * 
	 * @param pos1 Position 
	 * @param pos2 Position 
	 * @return Angle given in radians (in the range 0 ... <tt>Math.PI</tt>)
	 */
	public double angle(T3dVector pos1, T3dVector pos2)
	{   
		T3dVector 
			v10 = new T3dVector(),
			v20 = new T3dVector(),
			v21 = new T3dVector();
		v10.assignDiff(pos1, this);
		v20.assignDiff(pos2, this);
		v21.assignDiff(pos2, pos1);
		double 
			l10 = v10.length(),
			l20 = v20.length(),
			l21 = v21.length();
		if (l10 == 0. || l20 == 0. || l21 == 0.)
			return 0.;
		double cosPhi =
			(l10 * l10 + l21 * l21 - l20 * l20) / (2. * l10 * l21);
		return Math.acos(cosPhi);
	}
	
	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")";
	}
}
