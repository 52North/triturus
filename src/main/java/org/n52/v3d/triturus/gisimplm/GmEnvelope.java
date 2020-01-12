/**
 * Copyright (C) 2007-2016 52North Initiative for Geospatial Open Source
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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgPolygon;

/**
 * 3D-Bounding-box (<i>envelope</i>) implementation. Object information will be
 * kept in main memory. x- and y-values have to be given with respect to the 
 * spatial reference system (CRS) that has been set for the geometric object. 
 * The assertions
 * <tt>env.getMinX() &lt;= env.getMaxX()</tt>,
 * <tt>env.getMinY() &lt;= env.getMaxY()</tt> and
 * <tt>env.getMinZ() &lt;= env.getMaxZ()</tt> always hold.
 * 
 * @author Benno Schmidt
 */
public class GmEnvelope extends VgEnvelope 
{
	private double mXMin, mXMax, mYMin, mYMax, mZMin, mZMax;
	
	/**
	 * Constructor.
	 * 
	 * @param xMin minimal x-value
	 * @param xMax maximal x-value
	 * @param yMin minimal y-value
	 * @param yMax maximal y-value
	 * @param zMin minimal z-value
	 * @param zMax maximal z-value
	 */
	public GmEnvelope(
			double xMin, double xMax, 
			double yMin, double yMax,
			double zMin, double zMax) 
	{
		mXMin = xMin;
		mXMax = xMax;
		mYMin = yMin;
		mYMax = yMax;
		mZMin = zMin;
		mZMax = zMax;
	
		this.assureOrdering();
	}
	
	/**
	 * Constructor. The geometry of the bounding-box <tt>pEnv</tt> will be 
	 * copied to <tt>this</tt> object.
	 * 
	 * @param env Envelope geometry to be copied
	 */
	public GmEnvelope(VgEnvelope env) 
	{
		mXMin = env.getXMin();
		mXMax = env.getXMax();
		mYMin = env.getYMin();
		mYMax = env.getYMax();
		mZMin = env.getZMin();
		mZMax = env.getZMax();
	
		this.assureOrdering();
	}
	
	/**
	 * Constructor. The given point's coordinates will be taken over; the
	 * resulting bounding-box will have an interior volume of 0.
	 * 
	 * @param pt Point geometry
	 */
	public GmEnvelope(VgPoint pt) 
	{
		this.setSRS(pt.getSRS());
	
		mXMin = pt.getX(); mXMax = mXMin;
		mYMin = pt.getY(); mYMax = mYMin;
		mZMin = pt.getZ(); mZMax = mZMin;
	}


	/**
	 * Constructor. The geometry have to be specified by two opposite
	 * corner-points (e.g., &quot;lower left bottom&quot; and 
	 * &quot;upper righttop&quot;).
	 * 
	 * @param pt1 First corner
	 * @param pt2 Second corner
	 */
	public GmEnvelope(VgPoint pt1, VgPoint pt2) 
	{
		this.setSRS(pt1.getSRS());
		this.assertSRS(pt2);
	
		mXMin = pt1.getX(); mXMax = mXMin;
		mYMin = pt1.getY(); mYMax = mYMin;
		mZMin = pt1.getZ(); mZMax = mZMin;
	
		this.letContainPoint(pt2);
	}
	
	/**
	 * Constructor. The bounding-Box will be constructed on base of the given
	 * center point and the depth- and width- value (extents in x-direction
	 * resp. und y-direction).
	 * 
	 * @param cen Center point
	 * @param width Width (x-direction)
	 * @param depth Depth (y-direction)
	 */
	public GmEnvelope(VgPoint cen, double width, double depth) 
	{
		mXMin = cen.getX() - width / 2.;
		mXMax = cen.getX() + width / 2.;
		mYMin = cen.getY() - depth / 2.;
		mYMax = cen.getY() + depth / 2.;
		mZMin = cen.getZ();
		mZMax = cen.getZ();
	
		// this.assureOrdering(); not needed here
	}

	/**
	 * Constructor. The bounding-box will be initialized by the specified
	 * comma-separated coordinate list. If no z-values are given, z will be set
	 * to 0.
	 * <br/>
	 * Examples:
	 * <tt>&quot;3500000,5800000,3600000,5900000&quot;, &quot;3500000,5800000,50.5,3600000,5900000,100&quot;</tt>
	 * <br/>
	 * If the given string describes no valid coordinate list, a <tt>T3dException</tt> 
	 * will be thrown.
	 * 
	 * @param commaSeparatedList List consisting of 4 or 6 coordinate-values
	 */
	public GmEnvelope(String commaSeparatedList) 
	{
		String[] coords = commaSeparatedList.split(",");
		if (coords.length != 4 && coords.length != 6)
			throw new T3dException(
				"Cannot parse coordinates from \"" + commaSeparatedList + "\".");
		
		if (coords.length == 4) {
			mXMin = Double.parseDouble(coords[0]);
			mXMax = Double.parseDouble(coords[2]);
			mYMin = Double.parseDouble(coords[1]);
			mYMax = Double.parseDouble(coords[3]);
			mZMin = 0.;
			mZMax = 0.;
		} 
		else { // coords.length = 6
			mXMin = Double.parseDouble(coords[0]);
			mXMax = Double.parseDouble(coords[3]);
			mYMin = Double.parseDouble(coords[1]);
			mYMax = Double.parseDouble(coords[4]);
			mZMin = Double.parseDouble(coords[2]);
			mZMax = Double.parseDouble(coords[5]);
		}
		
		this.assureOrdering();
	}

	/**
	 * sets the bounding-box's minimal x-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param x x-coordinate referring to the set spatial reference system
	 */
	public void setXMin(double x) {
		mXMin = x;
	}
	
	public double getXMin() {
		return mXMin;
	}
	
	/**
	 * sets the bounding-box's maximal x-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param x x-coordinate referring to the set spatial reference system
	 */
	public void setXMax(double x) {
		mXMax = x;
	}
	
	public double getXMax() {
		return mXMax;
	}
	
	/**
	 * sets the bounding-box's minimal y-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param y y-coordinate referring to the set spatial reference system
	 */
	public void setYMin(double y) {
		mYMin = y;
	}
	
	public double getYMin() {
		return mYMin;
	}
	
	/**
	 * sets the bounding-box's maximal y-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param y y-coordinate referring to the set spatial reference system
	 */
	public void setYMax(double y) {
		mYMax = y;
	}
	
	public double getYMax() {
		return mYMax;
	}
	
	/**
	 * sets the bounding-box's minimal z-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param z z-coordinate
	 */
	public void setZMin(double z) {
		mZMin = z;
	}
	
	public double getZMin() {
		return mZMin;
	}
	
	/**
	 * sets the bounding-box's maximal z-coordinate. After method execution it is 
	 * not asserted that the condition <i>env.getXMin() &lt;= env.getXMax()</i> 
	 * holds. If necessary, the method <tt>assureOrdering()</tt> has to be called 
	 * explicitly!
	 * 
	 * @param z z-coordinate
	 */
	public void setZMax(double z) {
		mZMax = z;
	}
	
	public double getZMax() {
		return mZMax;
	}

	/**
	 * returns the bounding-box's &quot;lower left bottom&quot; corner.
	 * 
	 * @return <tt>GmPoint</tt> with minimum x-, y- and z-value
	 */
	public VgPoint getLowerLeftFrontCorner() 
	{
		VgPoint ret = new GmPoint(mXMin, mYMin, mZMin);
		ret.setSRS(this.getSRS());
		return ret;
	}
	
	/**
	 * returns the bounding-box's &quot;upper right top&quot; corner.
	 * 
	 * @return <tt>GmPoint</tt> with maximum x-, y- and z-value
	 */
	public VgPoint getUpperRightBackCorner() 
	{
		VgPoint ret = new GmPoint(mXMax, mYMax, mZMax);
		ret.setSRS(this.getSRS());
		return ret;
	}
	
	public VgPoint getCenterPoint() {
		return new GmPoint(
			0.5 * (mXMin + mXMax), 
			0.5 * (mYMin + mYMax),
			0.5 * (mZMin + mZMax));
	}

	@Override	
	public void setCenterPoint(VgPoint pcen) 
	{
		double 
			mx = (this.getXMax() + this.getXMin()) / 2.,
			my = (this.getYMax() + this.getYMin()) / 2.,
			mz = (this.getZMax() + this.getZMin()) / 2.;
	
		GmPoint delta = new GmPoint(
				pcen.getX() - mx, 
				pcen.getY() - my,
				pcen.getZ() - mz);
		
		this.translate(delta);
	}

	/**
	 * returns a bounding-box that has been rotated around the 
	 * coordinate-system's origin (0, 0, 0) with respect to the x-y plane. Note
	 * that the result usually is no bounding-box any more.
	 * 
	 * @param azimuth Rotation angle in radians (given clockwise)
	 * @return Rotated cuboid as polygon
	 */
	public VgPolygon rotateXY(double azimuth) 
	{
		double 
			cx = this.getCenterPoint().getX(),
			cy = this.getCenterPoint().getY();
		T3dVector 
			p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy, 0.),
			p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy, 0.),
			p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy, 0.),
			p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy, 0.);
		T3dVector 
			q1 = p1.rotateXY(azimuth),
			q2 = p2.rotateXY(azimuth),
			q3 = p3.rotateXY(azimuth),
			q4 = p4.rotateXY(azimuth);
		
		GmLinearRing ret = new GmLinearRing();
		ret.addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
		ret.addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
		ret.addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
		ret.addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
		
		return new GmPolygon(ret);
	}

	/**
	 * returns a bounding-box that has been rotated around the
	 * coordinate-system's origin (0, 0, 0) with respect to the z-axis. Note 
	 * that the result usually is no bounding-box any more.
	 * TODO: This method has not been tested yet!
	 * 
	 * @param inclination Rotation angle in radians
	 * @return Rotated cuboid as polygon
	 */
	public VgPolygon rotateZ(double inclination) 
	{
		double 
			cx = this.getCenterPoint().getX(),
			cy = this.getCenterPoint().getY();	
		T3dVector 
			p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy, 0.),
			p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy, 0.),
			p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy, 0.),
			p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy, 0.);
		T3dVector 
			q1 = p1.rotateZ(inclination),
			q2 = p2.rotateZ(inclination),
			q3 = p3.rotateZ(inclination),
			q4 = p4.rotateZ(inclination);
		
		GmLinearRing ret = new GmLinearRing();
		ret.addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
		ret.addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
		ret.addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
		ret.addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
		
		return new GmPolygon(ret);
	}

	@Override	
	public void letContainEnvelope(VgEnvelope env) {
		this.assertSRS(env);
		this.letContainPoint(
			new GmPoint(env.getXMin(), env.getYMin(), env.getZMin()));
		this.letContainPoint(
			new GmPoint(env.getXMax(), env.getYMax(), env.getZMax()));
	}

	/**
	 * computes the intersection C = env(A * B) of two given envelopes.
	 *
	 * @param envA First envelope
	 * @param envB Second envelope 
	 * @return Intersection box C as envelope object
	 */
	static public VgEnvelope intersect(VgEnvelope envA, VgEnvelope envB) {
		return new GmEnvelope(
			Math.max(envA.getXMin(), envB.getXMin()),
			Math.min(envA.getXMax(), envB.getXMax()),
			Math.max(envA.getYMin(), envB.getYMin()),
			Math.min(envA.getYMax(), envB.getYMax()),
			Math.max(envA.getZMin(), envB.getZMin()),
			Math.min(envA.getZMax(), envB.getZMax()));
	}

	/**
	 * computes the envelope C = env(A + B) of the union of two given 
	 * envelopes.
	 *
	 * @param env1 First envelope
	 * @param env1 Second envelope 
	 * @return Envelope C which is the envelope of the union of A and B
	 */
	static public VgEnvelope union(VgEnvelope envA, VgEnvelope envB) {
		return new GmEnvelope(
			Math.min(envA.getXMin(), envB.getXMin()),
			Math.max(envA.getXMax(), envB.getXMax()),
			Math.min(envA.getYMin(), envB.getYMin()),
			Math.max(envA.getYMax(), envB.getYMax()),
			Math.min(envA.getZMin(), envB.getZMin()),
			Math.max(envA.getZMax(), envB.getZMax()));
	}
	
	/**
	 * provokes that the conditions <i>this.getXMin() &lt;= this.getXMax()</i>,
	 * <i>this.getYMin() &lt;= this.getYMax()</i> and 
	 * <i>this.getZMin() &lt;= this.getZMax()</i> will hold.
	 */
	public void assureOrdering() {
		this.assureOrderingX();
		this.assureOrderingY();
		this.assureOrderingZ();
	}

	/**
	 * provokes that the condition <i>this.getXMin() &lt;= this.getXMax()</i>
	 * will hold.
	 */
	public void assureOrderingX() {
		if (mXMin > mXMax) {
			double hlp = mXMin; mXMin = mXMax; mXMax = hlp;
		}
	}
	
	/**
	 * provokes that the condition <i>this.getYMin() &lt;= this.getYMax()</i>
	 * will hold.
	 */
	public void assureOrderingY() {
		if (mYMin > mYMax) {
			double hlp = mYMin; mYMin = mYMax; mYMax = hlp;
		}
	}
	
	/**
	 * provokes that the condition <i>this.getZMin() &lt;= this.getZMax()</i>
	 * will hold.
	 */
	public void assureOrderingZ() {
		if (mZMin > mZMax) {
			double hlp = mZMin; mZMin = mZMax; mZMax = hlp;
		}
	}
	
	@Override
	public VgEnvelope envelope() {
		return this;
	}

	@Override
	public VgGeomObject footprint() {
		GmLinearRing res = new GmLinearRing();
		res.addVertex(new GmPoint(mXMin, mYMin, 0.));
		res.addVertex(new GmPoint(mXMax, mYMin, 0.));
		res.addVertex(new GmPoint(mXMax, mYMax, 0.));
		res.addVertex(new GmPoint(mXMin, mYMax, 0.));
		return new GmPolygon(res);
	}
}
