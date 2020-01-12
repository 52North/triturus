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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold 3D point coordinates. E.g., the object's z-value might be an
 * elevation or depth value.
 * <br/>
 * x- and y-values have to be given with respect to the set spatial reference 
 * system (SRS).
 * <br/>
 * Note: Within the Triturus framework, <tt>VgPoint</tt>-objects primarily will
 * be used to hold <i>position vectors</i>. To give <i>direction vectors</i>, 
 * usually the helper class <tt>T3dVector</tt> should be preferred.
 *
 * @see org.n52.v3d.triturus.t3dutil.T3dVector
 * @author Benno Schmidt, Martin May
 */
abstract public class VgPoint 
	extends VgGeomObject0d 
	implements Comparable 
{
	/** 
	 * sets the point's x-value.
	 * <br/>
	 * Note: For <i>geographic coordinates</i> (<tt>&quot;EPSG:4326&quot;</tt>)
	 * the x-value usually gives the geographic longitude, for <i>Gauss-Krueger 
	 * coordinates</i> the &quot;Rechtswert&quot;.
	 *
	 * @param x x-coordinate
	 */
	abstract public void setX(double pX);
	
	/** 
	 * returns the point's x-value.
	 *
	 * @return x-coordinate
	 */
	abstract public double getX();
	
	/** 
	 * sets the point's y-value.
	 * <br/>
	 * Note: For <i>geographic coordinates</i> (<tt>&quot;EPSG:4326&quot;</tt>)
	 * the y-value usually gives the geographic latitude, for <i>Gauss-Krueger 
	 * coordinates</i> the &quot;Hochwert&quot;.
	 *
	 * @param y y-coordinate
	 */
	abstract public void setY(double y);
	
	/** 
	 * returns the point's y-value.
	 *
	 * @return y-coordinate
	 */
	abstract public double getY();
	
	/** 
	 * sets the point's z-value.
	 *
	 * @param z z-coordinate
	 */
	abstract public void setZ(double pZ);
	
	/** 
	 * returns the point's z-value.
	 *
	 * @return z-coordinate
	 */
	abstract public double getZ();
	
	/** 
	 * sets the coordinates of a <tt>VgPoint</tt> object. The information about 
	 * the spatial reference system will be taken over. Note that this method
	 * will not allocate storage for a new <tt>VgPoint</tt> object.
	 *
	 * @param pt Point geometry
	 */
	public void set(VgPoint pt) 
	{
		this.setX(pt.getX());
		this.setY(pt.getY());
		this.setZ(pt.getZ());
		this.setSRS(pt.getSRS());
	}

	/**
	 * returns the distance between two points.
	 * <br/>
	 * The distance will be given with respect to the set spatial reference 
	 * system (<tt>this.getSRS()</tt>) and the unit underlying the given 
	 * z-coordinates. If the point geometry <tt>pt</tt> and <tt>this</tt> 
	 * object do not refer to the same spatial reference system (in the x-y
	 * plane), a <tt>T3dException</tt> will be thrown.
	 * <br/>
	 * Example: For Gauss-Krueger coordinates (consisting of seven numerical 
	 * digits) given in meters and elevation values given in meters above 
	 * sea-level the distance measure will be 1 m. 
	 * <br/>
	 * Make sure your application refers to proper measures with respect to the
	 * x-y plane as well as to the z-direction.
	 *
	 * @see VgGeomObject#getSRS
	 * @throws T3dException
	 * @throws ClassCastException
	 */
	public double distance(VgGeomObject0d pt) 
		throws T3dException, ClassCastException
	{
		if (!(this.getSRS().equalsIgnoreCase(pt.getSRS()))) {
			String msg = "SRS incompatibility prevents from distance calculation";
			msg += " (" + this.getSRS() + ", " + pt.getSRS() + ").";
		    throw new T3dException(msg);
		}
		    
		VgPoint p;
		try {
			p = (VgPoint) pt;
		}
		catch (ClassCastException e) {
			throw e;
		}
		
		double 
			dx = this.getX() - p.getX(),
			dy = this.getY() - p.getY(),
			dz = this.getZ() - p.getZ();
	
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	/**
	 * returns the distance between two points with respect to the x-y plane 
	 * (i.e., z-values will be set to 0.
	 * <br/>
	 * The distance will be given with respect to the set spatial reference 
	 * system (<tt>this.getSRS()</tt>). If the point geometry <tt>pt</tt> and 
	 * <tt>this</tt> object do not refer to the same spatial reference system, 
	 * a <tt>T3dException</tt> will be thrown.
	 *
	 * @see VgGeomObject#getSRS
	 * @see VgGeomObject0d#distance
	 * @throws T3dSRSException
	 * @throws ClassCastException
	 */
    public double distanceXY(VgGeomObject0d pt) 
    	throws T3dSRSException, ClassCastException
	{
		if (!this.getSRS().equalsIgnoreCase(pt.getSRS())) {
			String msg = "SRS-incompatibility prevents from distance calculation";
			msg += " (" + this.getSRS() + ", " + pt.getSRS() + ").";
		    throw new T3dSRSException(msg);
		}
		    
		VgPoint p;
		try {
			p = (VgPoint) pt;
		}
		catch (ClassCastException e) {
			throw e;
		}
		
		double 
			dx = this.getX() - p.getX(),
			dy = this.getY() - p.getY();
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * defines an order relation on <tt>VgPoint</tt>. The methode will return 
	 * -1, if <tt>this &lt; pt</tt> resp. +1, if <tt>this &gt; pt</tt>. If 
	 * <tt>this</tt> is equal to <tt>pt</tt>, the return value will be 0.
	 * <br/>
	 * Note: the method will not check whether <tt>pt</tt> refers to the same
	 * spatial reference system as <tt>this</tt>.
	 *
	 * @return -1, 1, or 0
	 */
	public int compareTo(Object pt) 
	{
		VgPoint p = (VgPoint) pt;
		
		if (this.getX() < p.getX()) return -1;
		if (this.getX() > p.getX()) return 1;
		
		// If x-values are equal:
		if (this.getY() < p.getY()) return -1;
		if (this.getY() > p.getY()) return 1;
		
		// If x- and y-values are equal:
		if (this.getZ() < p.getZ()) return -1;
		if (this.getZ() > p.getZ()) return 1;
		
		// If x-, y- and z-values are equal:
		return 0;
	}
	
	/**
	 * @deprecated
     * // @author Martin May
	 */
	public boolean equals(Object pt) 
	{
		// -> ueberschreibt equals() von Object, wird benoetigt in GmLineSegment 
		// in dessen equals()
		// TODO vorlaeufige Loesung (Martin)
		VgPoint p = (VgPoint) pt;
		
		boolean res = false;
		if (this.getX() == p.getX() && this.getY() == p.getY() && this.getZ() == p.getZ()) 
		    res = true;
		return res;
	}

	public String toString() {
		return "(" + 
			this.getX() + ", " + 
			this.getY() + ", " + 
			this.getZ() + " (" + this.getSRS() + "))";
	}
}
