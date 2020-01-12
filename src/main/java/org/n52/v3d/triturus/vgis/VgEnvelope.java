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

/**
 * Three-dimensional bounding-box objects. x- and y-coordinates refer to the 
 * set spatial reference system (SRS). Note that the following assertions must 
 * hold for all implementations: <tt>env.getMinX() &lt;= env.getMaxX()</tt> and
 * <tt>env.getMinY() &lt;= env.getMaxY()</tt>.
 *
 * @author Benno Schmidt
 * @see VgGeomObject#getSRS
 */
abstract public class VgEnvelope extends VgGeomObject3d 
{
	/**
	 * sets the bounding-box's minimal x-coordinate.
	 *
	 * @param x x-coordinate referring to the set spatial reference system
	 */
	abstract public void setXMin(double x);
	
	/**
	 * gets the bounding-box's minimal x-coordinate.
	 *
	 * @return x-coordinate referring to the set spatial reference system
	 */
	abstract public double getXMin();
	
	/**
	 * sets the bounding-box's maximal x-coordinate.
	 *
	 * @param x x-coordinate referring to the set spatial reference system
	 */
	abstract public void setXMax(double x);
	
	/**
	 * gets the bounding-box's maximal x-coordinate.
	 *
	 * @return x-coordinate referring to the set spatial reference system
	 */
	abstract public double getXMax();
	
	/**
	 * sets the bounding-box's minimal y-coordinate.
	 *
	 * @param y y-coordinate referring to the set spatial reference system
	 */
	abstract public void setYMin(double y);
	
	/**
	 * gets the bounding-box's minimal y-coordinate.
	 *
	 * @return y-coordinate referring to the set spatial reference system
	 */
	abstract public double getYMin();
	
	/**
	 * sets the bounding-box's maximal y-coordinate.
	 *
	 * @param y y-coordinate referring to the set spatial reference system
	 */
	abstract public void setYMax(double y);
	
	/**
	 * gets the bounding-box's maximal y-coordinate.
	 *
	 * @return y-coordinate referring to the set spatial reference system
	 */
	abstract public double getYMax();
	
	/**
	 * sets the bounding-box's minimal z-coordinate.
	 *
	 * @param z z-coordinate
	 */
	abstract public void setZMin(double z);
	
	/**
	 * gets the bounding-box's minimal z-coordinate.
	 *
	 * @return z-coordinate
	 */
	abstract public double getZMin();
	
	/**
	 * sets the bounding-box's maximal z-coordinate.
	 *
	 * @param z z-coordinate
	 */
	abstract public void setZMax(double z);
	
	/**
	 * gets the bounding-box's maximal z-coordinate.
	 *
	 * @return z-coordinate
	 */
	abstract public double getZMax();
	
	/**
	 * gets the bounding-box's center point.
	 *
	 * @return Center point
	 */
	abstract public VgPoint getCenterPoint();
	
	/**
	 * sets the bounding-box's center point. Note that bounding-box will be 
	 * translated by this operation.
	 *
	 * @param cen new center point
	 */
	abstract public void setCenterPoint(VgPoint cen);
	
	/**
	 * translates the bounding-box.
	 * @param shift Translation vector
	 */
	public void translate(VgPoint shift)
	{
	    this.setXMin(this.getXMin() + shift.getX()); 
	    this.setXMax(this.getXMax() + shift.getX());
	    
	    this.setYMin(this.getYMin() + shift.getY()); 
	    this.setYMax(this.getYMax() + shift.getY());
	    
	    this.setZMin(this.getZMin() + shift.getZ()); 
	    this.setZMax(this.getZMax() + shift.getZ());
	}
	
	/**
	 * performs a bounding-box scaling. The center-point will not be changed, 
	 * but the bounding-box's extents in x-, y- and z-direction will be 
	 * multiplied by the given factor.
	 *
	 * @param factor Scaling factor
	 */
	public void scale(double factor)
	{
		double 
			mx = (this.getXMin() + this.getXMax()) / 2.,
			my = (this.getYMin() + this.getYMax()) / 2.,
			mz = (this.getZMin() + this.getZMax()) / 2.;
		
		double 
			dx = this.getXMax() - this.getXMin(),
			dy = this.getYMax() - this.getYMin(),
			dz = this.getZMax() - this.getZMin();
		
		this.setXMin(mx - factor * 0.5 * dx);
		this.setXMax(mx + factor * 0.5 * dx);
		this.setYMin(my - factor * 0.5 * dy);
		this.setYMax(my + factor * 0.5 * dy);
		this.setZMin(mz - factor * 0.5 * dz);
		this.setZMax(mz + factor * 0.5 * dz);
	}

	/**
	 * performs a bounding-box scaling. The center-point will not be changed. 
	 * The bounding-box's extents in x-, y- and z-direction will be set to the 
	 * specified values.
	 *
	 * @param extX new extent in x-direction
	 * @param extY new extent in y-direction
	 * @param extZ new extent in z-direction
	 */
	public void resize(double extX, double extY, double extZ)
	{
	    VgPoint center = this.getCenterPoint();
	
	    this.setXMin(center.getX() - extX / 2.);
	    this.setXMax(center.getX() + extX / 2.);
	    this.setYMin(center.getY() - extY / 2.);
	    this.setYMax(center.getY() + extY / 2.);
	    this.setZMin(center.getZ() - extZ / 2.);
	    this.setZMax(center.getZ() + extZ / 2.);
	}

	/**
	 * returns the bounding-box's extent in x-direction (width resp. length).
	 *
	 * @return Extent &gt;= 0
	 */
	public double getExtentX() {
	    return Math.abs(this.getXMax() - this.getXMin());
	}
	
	/**
	 * returns the bounding-box's extent in y-direction (length resp. width).
	 *
	 * @return Extent &gt;= 0
	 */
	public double getExtentY() {
	    return Math.abs(this.getYMax() - this.getYMin());
	}
	
	/**
	 * returns the bounding-box's extent in z-direction (height).
	 *
	 * @return Extent &gt;= 0
	 */
	public double getExtentZ() {
	    return Math.abs(this.getZMax() - this.getZMin());
	}
	
	/**
	 * returns the length of bounding-box's diagonal in 3-D space.
	 *
	 * @return Diagonal length
	 */
	public double diagonalLength()
	{
	    double 
	    	dx = this.getXMax() - this.getXMin(),
	    	dy = this.getYMax() - this.getYMin(),
	    	dz = this.getZMax() - this.getZMin();
	
	    return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	/**
	 * returns the bounding-box's volume with respect to the assigned spatial 
	 * reference system.
	 *
	 * @return Volume
	 */
	public double volume() 
	{
		double 
			dx = this.getXMax() - this.getXMin(),
			dy = this.getYMax() - this.getYMin(),
			dz = this.getZMax() - this.getZMin();
		
		return dx * dy * dz;
	}

	/**
	 * returns the surface area with respect to the assigned spatial reference 
	 * system.
     *
     * @return Surface area
	 */
	public double surface()
	{
		double 
			dx = this.getXMax() - this.getXMin(),
			dy = this.getYMax() - this.getYMin(),
			dz = this.getZMax() - this.getZMin();
		
		return 2. * (dx*dy + dx*dz + dy*dz);		
	}
	
	/**
	 * extends the bounding-box's spatial extent such that the specified point 
	 * will be not lie outside of the bounding-box.
	 *
	 * @param pt Point that will be element of the modified bounding-box
	 */
	public void letContainPoint(VgPoint pt)
	{
		this.assertSRS(pt);
		
		if (pt.getX() < this.getXMin()) this.setXMin(pt.getX()); 
		else {
		    if (pt.getX() > this.getXMax()) this.setXMax(pt.getX());
		}
		if (pt.getY() < this.getYMin()) this.setYMin(pt.getY()); 
		else {
		    if (pt.getY() > this.getYMax()) this.setYMax(pt.getY());
		}
		if (pt.getZ() < this.getZMin()) this.setZMin(pt.getZ()); 
		else {
		    if (pt.getZ() > this.getZMax()) this.setZMax(pt.getZ());
		}
	}
	
	/**
	 * extends the bounding-box's spatial extent such that the specified 
	 * bounding-box will be part of the bounding-box (&quot;union operator&quot;).
	 *
	 * @param env Bounding-box that will be element of the modified bounding-box
	 */
	abstract public void letContainEnvelope(VgEnvelope env);
	
	/**
	 * checks bounding-boxes for geometrical equivalence.
	 *
	 * @param env Bounding-box that has to be compared with <tt>this</tt> object
	 * @return <i>true</i>, if for geometrical equivalence, else <i>false</i>
	 */
	public boolean isSpatiallyEquivalent(VgEnvelope env)
	{
		double eps = 1.0e-2; 
			// should do for coordinates given in meters (centimeter precision) -> TODO
		return
		    Math.abs(this.getXMin() - env.getXMin()) < eps &&
		    Math.abs(this.getXMax() - env.getXMax()) < eps &&
		    Math.abs(this.getYMin() - env.getYMin()) < eps &&
		    Math.abs(this.getYMax() - env.getYMax()) < eps;
	}
	
	/**
	 * checks, if a given point lies inside the bounding-box.
	 *
	 * @param pt Point to be checked
	 * @return <i>true</i>, if <tt>pt</tt> lies inside the bounding-box or on the border, else <i>false</i>
	 */
	public boolean contains(VgPoint pt) {
	    // TODO: SRS-check still missing!
		
		double 
	    	x = pt.getX(),
	    	y = pt.getY(),
	    	z = pt.getZ();
	    
		if (x < this.getXMin() || x > this.getXMax()) return false;
	    if (y < this.getYMin() || y > this.getYMax()) return false;
	    if (z < this.getZMin() || z > this.getZMax()) return false;
	    
	    return true;
	}
	
	public String toString() {
		return "[" +
			"(" + this.getXMin() + ", " + this.getYMin() + ", " + this.getZMin() +
			"), " +
			"(" + this.getXMax() + ", " + this.getYMax() + ", " + this.getZMax() +
			")]";
	}
}
