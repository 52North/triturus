/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
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
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgPolygon;

/**
 * 3D-Bounding-box (<i>envelope</i>) implementation. Object information will be
 * kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system
 * (SRS) that has been set for the geometric object. The assertions
 * <tt>env.getMinX() &lt;= env.getMaxX()</tt>,
 * <tt>env.getMinY() &lt;= env.getMaxY()</tt> and
 * <tt>env.getMinZ() &lt;= env.getMaxZ()</tt> always hold.
 * <p>
 * 
 * @author Benno Schmidt
 */
public class GmEnvelope extends VgEnvelope {
	private double mXMin, mXMax, mYMin, mYMax, mZMin, mZMax;

	/**
	 * Constructor.
	 * 
	 * @param pXMin
	 *            minimal x-value
	 * @param pXMax
	 *            maximal x-value
	 * @param pYMin
	 *            minimal y-value
	 * @param pYMax
	 *            maximal y-value
	 * @param pZMin
	 *            minimal z-value
	 * @param pZMax
	 *            maximal z-value
	 */
	public GmEnvelope(double pXMin, double pXMax, double pYMin, double pYMax,
			double pZMin, double pZMax) {
		mXMin = pXMin;
		mXMax = pXMax;
		mYMin = pYMin;
		mYMax = pYMax;
		mZMin = pZMin;
		mZMax = pZMax;

		this.assureOrdering();
	}

	/**
	 * Constructor. The geometry of the bounding-box <tt>pEnv</tt> will be taken
	 * over.
	 * 
	 * @param pEnv
	 *            Envelope holding geometry to be taken over
	 */
	public GmEnvelope(VgEnvelope pEnv) {
		mXMin = pEnv.getXMin();
		mXMax = pEnv.getXMax();
		mYMin = pEnv.getYMin();
		mYMax = pEnv.getYMax();
		mZMin = pEnv.getZMin();
		mZMax = pEnv.getZMax();

		this.assureOrdering();
	}

	/**
	 * Constructor. The given point's coordinates will be taken over; the
	 * resulting bounding-box will have an interior volume of 0.
	 * 
	 * @param pPnt
	 *            Point geometry
	 */
	public GmEnvelope(VgPoint pPnt) {
		this.setSRS(pPnt.getSRS());

		mXMin = pPnt.getX();
		mXMax = mXMin;
		mYMin = pPnt.getY();
		mYMax = mYMin;
		mZMin = pPnt.getZ();
		mZMax = mZMin;
	}

	/**
	 * Constructor. The geometry havwe to be specified by two opposite
	 * corner-points (e.g., &quot;lower left bottom&quot; and &quot;upper right
	 * top&quot;).
	 * 
	 * @param pPnt1
	 *            first corner
	 * @param pPnt2
	 *            second corner
	 */
	public GmEnvelope(VgPoint pPnt1, VgPoint pPnt2) {
		this.setSRS(pPnt1.getSRS());
		this.assertSRS(pPnt2);

		mXMin = pPnt1.getX();
		mXMax = mXMin;
		mYMin = pPnt1.getY();
		mYMax = mYMin;
		mZMin = pPnt1.getZ();
		mZMax = mZMin;

		this.letContainPoint(pPnt2);
	}

	/**
	 * Constructor. The bounding-Box will be constructed on base of the given
	 * center point and the depth- and width- value (extents in x-direction
	 * resp. und y-direction).
	 * 
	 * @param pCenter
	 *            Center point
	 * @param pWidth
	 *            Width (x-direction)
	 * @param pDepth
	 *            Depth (y-direction)
	 */
	public GmEnvelope(VgPoint pCenter, double pWidth, double pDepth) {
		mXMin = pCenter.getX() - pWidth / 2.;
		mXMax = pCenter.getX() + pWidth / 2.;
		mYMin = pCenter.getY() - pDepth / 2.;
		mYMax = pCenter.getY() + pDepth / 2.;
		mZMin = pCenter.getZ();
		mZMax = pCenter.getZ();

		// this.assureOrdering(); hier nicht n�tig
	}

	/**
	 *
	 * Constructor. The bounding-box will be initialized by the specified
	 * comma-separated coordinate list. If no z-values are given, z will be set
	 * to 0.<br />
	 * Examples:
	 * <tt>&quot;3500000,5800000,3600000,5900000&quot;, &quot;3500000,5800000,50.5,3600000,5900000,100&quot;</tt>
	 * <br />
	 * If the given string describes no valid coordinate list, a
	 * <tt>T3dException</tt> will be thrown.
	 * 
	 * @param pCommaSeparatedList
	 *            List consisting of 4 or 6 coordinate-values
	 */
	public GmEnvelope(String pCommaSeparatedList) {
		String[] coords = pCommaSeparatedList.split(",");
		if (coords.length != 4 && coords.length != 6)
			throw new T3dException("Cannot parse geo-coordinates from \""
					+ pCommaSeparatedList + "\".");

		if (coords.length == 4) {
			mXMin = Double.parseDouble(coords[0]);
			mXMax = Double.parseDouble(coords[2]);
			mYMin = Double.parseDouble(coords[1]);
			mYMax = Double.parseDouble(coords[3]);
			mZMin = 0.;
			mZMax = 0.;
		} else { // coords.length = 6
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
	 * sets the bounding-box's minimal x-coordinate.<br />
	 * After method execution it is not asserted that the condition
	 * <i>env.getXMin() &lt;= env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pX
	 *            x-coordinate referring to the set spatial reference system
	 */
	public void setXMin(double pX) {
		mXMin = pX;
	}

	public double getXMin() {
		return mXMin;
	}

	/**
	 * sets the bounding-box's maximal x-coordinate. After method execution it
	 * is not asserted that the condition <i>env.getXMin() &lt;=
	 * env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pX
	 *            x-coordinate referring to the set spatial reference system
	 */
	public void setXMax(double pX) {
		mXMax = pX;
	}

	public double getXMax() {
		return mXMax;
	}

	/**
	 * sets the bounding-box's minimal y-coordinate. After method execution it
	 * is not asserted that the condition <i>env.getXMin() &lt;=
	 * env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pY
	 *            y-coordinate referring to the set spatial reference system
	 */
	public void setYMin(double pY) {
		mYMin = pY;
	}

	public double getYMin() {
		return mYMin;
	}

	/**
	 * sets the bounding-box's maximal y-coordinate. After method execution it
	 * is not asserted that the condition <i>env.getXMin() &lt;=
	 * env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pY
	 *            y-coordinate referring to the set spatial reference system
	 */
	public void setYMax(double pY) {
		mYMax = pY;
	}

	public double getYMax() {
		return mYMax;
	}

	/**
	 * sets the bounding-box's minimal z-coordinate. After method execution it
	 * is not asserted that the condition <i>env.getXMin() &lt;=
	 * env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pZ
	 *            z-coordinate
	 */
	public void setZMin(double pZ) {
		mZMin = pZ;
	}

	public double getZMin() {
		return mZMin;
	}

	/**
	 * sets the bounding-box's maximal z-coordinate. After method execution it
	 * is not asserted that the condition <i>env.getXMin() &lt;=
	 * env.getXMax()</i> holds. If necessary, the method
	 * <tt>assureOrdering()</tt> has to be called explicitly!
	 * 
	 * @param pZ
	 *            z-coordinate
	 */
	public void setZMax(double pZ) {
		mZMax = pZ;
	}

	public double getZMax() {
		return mZMax;
	}

	/**
	 * returns the bounding-box's &quot;lower left bottom&quot; corner.
	 * 
	 * @return <tt>GmPoint</tt> with minimum x-, y- and z-value
	 */
	public VgPoint getLowerLeftFrontCorner() {
		VgPoint ret = new GmPoint(mXMin, mYMin, mZMin);
		ret.setSRS(this.getSRS());
		return ret;
	}

	/**
	 * returns the bounding-box's &quot;upper right top&quot; corner.
	 * 
	 * @return <tt>GmPoint</tt> with maximum x-, y- and z-value
	 */
	public VgPoint getUpperRightBackCorner() {
		VgPoint ret = new GmPoint(mXMax, mYMax, mZMax);
		ret.setSRS(this.getSRS());
		return ret;
	}

	public VgPoint getCenterPoint() {
		return new GmPoint(0.5 * (mXMin + mXMax), 0.5 * (mYMin + mYMax),
				0.5 * (mZMin + mZMax));
	}

	public void setCenterPoint(VgPoint pCenter) {
		double mx = (this.getXMax() + this.getXMin()) / 2.;
		double my = (this.getYMax() + this.getYMin()) / 2.;
		double mz = (this.getZMax() + this.getZMin()) / 2.;
		GmPoint delta = new GmPoint(pCenter.getX() - mx, pCenter.getY() - my,
				pCenter.getZ() - mz);
		this.translate(delta);
	}

	/**
	 * returns a bounding-box that has been rotated around the
	 * coordinate-system's origin (0, 0, 0) with respect to the x-y-plane.
	 * 
	 * @param pAzimuth
	 *            Rotation angle in radians (given clockwise)
	 * @return rotated &quot;bounding-box&quot; (which is no bounding-box any
	 *         more) as polygon
	 */
	public VgPolygon rotateXY(double pAzimuth) {
		double cx = this.getCenterPoint().getX();
		double cy = this.getCenterPoint().getY();
		GmLinearRing ret = new GmLinearRing();
		T3dVector p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy,
				0.);
		T3dVector p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy,
				0.);
		T3dVector p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy,
				0.);
		T3dVector p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy,
				0.);
		T3dVector q1 = p1.rotateXY(pAzimuth);
		T3dVector q2 = p2.rotateXY(pAzimuth);
		T3dVector q3 = p3.rotateXY(pAzimuth);
		T3dVector q4 = p4.rotateXY(pAzimuth);
		ret.addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
		ret.addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
		ret.addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
		ret.addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
		return new GmPolygon(ret);
	}

	/**
	 * returns a bounding-box that has been rotated around the
	 * coordinate-system's origin (0, 0, 0) with respect to the z-axis. TODO:
	 * Methode ist noch nicht getestet
	 * 
	 * @param pInclination
	 *            Rotation angle in radians
	 * @return rotated &quot;bounding-box&quot; (which is no bounding-box any
	 *         more) as polygon
	 */
	public VgPolygon rotateZ(double pInclination) {
		double cx = this.getCenterPoint().getX();
		double cy = this.getCenterPoint().getY();
		GmLinearRing ret = new GmLinearRing();
		T3dVector p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy,
				0.);
		T3dVector p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy,
				0.);
		T3dVector p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy,
				0.);
		T3dVector p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy,
				0.);
		T3dVector q1 = p1.rotateZ(pInclination);
		T3dVector q2 = p2.rotateZ(pInclination);
		T3dVector q3 = p3.rotateZ(pInclination);
		T3dVector q4 = p4.rotateZ(pInclination);
		ret.addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
		ret.addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
		ret.addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
		ret.addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
		return new GmPolygon(ret);
	}

	public void letContainEnvelope(VgEnvelope pEnv) {
		this.assertSRS(pEnv);
		this.letContainPoint(new GmPoint(pEnv.getXMin(), pEnv.getYMin(), pEnv
				.getZMin()));
		this.letContainPoint(new GmPoint(pEnv.getXMax(), pEnv.getYMax(), pEnv
				.getZMax()));
	}

	public VgEnvelope envelope() {
		return this;
	}

	public VgGeomObject footprint() {
		GmLinearRing res = new GmLinearRing();
		res.addVertex(new GmPoint(mXMin, mYMin, 0.));
		res.addVertex(new GmPoint(mXMax, mYMin, 0.));
		res.addVertex(new GmPoint(mXMax, mYMax, 0.));
		res.addVertex(new GmPoint(mXMin, mYMax, 0.));
		return new GmPolygon(res);
	}

	/**
	 * provokes that the conditions <i>this.getXMin() &lt;= this.getXMax()</i>,
	 * <i>this.getYMin() &lt;= this.getYMax()</i> and <i>this.getZMin() &lt;=
	 * this.getZMax()</i> will hold.
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
			double hlp = mXMin;
			mXMin = mXMax;
			mXMax = hlp;
		}
	}

	/**
	 * provokes that the condition <i>this.getYMin() &lt;= this.getYMax()</i>
	 * will hold.
	 */
	public void assureOrderingY() {
		if (mYMin > mYMax) {
			double hlp = mYMin;
			mYMin = mYMax;
			mYMax = hlp;
		}
	}

	/**
	 * provokes that the condition <i>this.getZMin() &lt;= this.getZMax()</i>
	 * will hold.
	 */
	public void assureOrderingZ() {
		if (mZMin > mZMax) {
			double hlp = mZMin;
			mZMin = mZMax;
			mZMax = hlp;
		}
	}
}
