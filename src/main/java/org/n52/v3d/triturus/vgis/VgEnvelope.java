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
package org.n52.v3d.triturus.vgis;

/**
 * Three-dimensional bounding-box objects. x- and y-coodinates refer to the set spatial reference system (SRS). Note
 * that the following assertions must hold for all implementations: <tt>env.getMinX() &lt;= env.getMaxX()</tt> and
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
     * @param pX x-coordinate referring to the set spatial reference system
     */
	abstract public void setXMin(double pX);

    /**
     * gets the bounding-box's minimal x-coordinate.
     *
     * @return x-coordinate referring to the set spatial reference system
     */
	abstract public double getXMin();

    /**
     * sets the bounding-box's maximal x-coordinate.
     *
     * @param pX x-coordinate referring to the set spatial reference system
     */
	abstract public void setXMax(double pX);

    /**
     * gets the bounding-box's maximal x-coordinate.
     *
     * @return x-coordinate referring to the set spatial reference system
     */
	abstract public double getXMax();

    /**
     * sets the bounding-box's minimal y-coordinate.
     *
     * @param pY y-coordinate referring to the set spatial reference system
     */
	abstract public void setYMin(double pY);

    /**
     * gets the bounding-box's minimal y-coordinate.
     *
     * @return y-coordinate referring to the set spatial reference system
     */
	abstract public double getYMin();

    /**
     * sets the bounding-box's maximal y-coordinate.
     *
     * @param pY y-coordinate referring to the set spatial reference system
     */
	abstract public void setYMax(double pY);

    /**
     * gets the bounding-box's maximal y-coordinate.
     *
     * @return y-coordinate referring to the set spatial reference system
     */
	abstract public double getYMax();

    /**
     * sets the bounding-box's minimal z-coordinate.
     *
     * @param pZ z-coordinate
     */
	abstract public void setZMin(double pZ);

    /**
     * gets the bounding-box's minimal z-coordinate.
     *
     * @return z-coordinate
     */
	abstract public double getZMin();

    /**
     * sets the bounding-box's maximal z-coordinate.
     *
     * @param pZ z-coordinate
     */
	abstract public void setZMax(double pZ);

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
     * sets the bounding-box's center point. Note that bounding-box will be translated by this operation.
     *
     * @param pCenter new center point
     */
    abstract public void setCenterPoint(VgPoint pCenter);

    /**
     * translates the bounding-box.
     * @param pShift Translation vector
     */
    public void translate(VgPoint pShift)
    {
        this.setXMin(this.getXMin() + pShift.getX()); this.setXMax(this.getXMax() + pShift.getX());
        this.setYMin(this.getYMin() + pShift.getY()); this.setYMax(this.getYMax() + pShift.getY());
        this.setZMin(this.getZMin() + pShift.getZ()); this.setZMax(this.getZMax() + pShift.getZ());
    }

    /**
     * performs a bounding-box scaling. The center-point will not be changed, but the bounding-box's extents in x-, y-
     * and z-direction will be multiplied by the given factor.
     *
     * @param pFactor Scaling factor
     */
    public void scale(double pFactor)
    {
        double mx = (this.getXMin() + this.getXMax()) / 2.;
        double my = (this.getYMin() + this.getYMax()) / 2.;
        double mz = (this.getZMin() + this.getZMax()) / 2.;

        double dx = this.getXMax() - this.getXMin();
        double dy = this.getYMax() - this.getYMin();
        double dz = this.getZMax() - this.getZMin();

        this.setXMin(mx - pFactor * 0.5 * dx);
        this.setXMax(mx + pFactor * 0.5 * dx);
        this.setYMin(my - pFactor * 0.5 * dy);
        this.setYMax(my + pFactor * 0.5 * dy);
        this.setZMin(mz - pFactor * 0.5 * dz);
        this.setZMax(mz + pFactor * 0.5 * dz);
    }

    /**
     * performs a bounding-box scaling. The center-point will not be changed. The bounding-box's extents in x-, y-
     * and z-direction will be set to the specified values.
     *
     * @param pExtentX new extent in x-direction
     * @param pExtentY new extent in y-direction
     * @param pExtentZ new extent in z-direction
     */
    public void resize(double pExtentX, double pExtentY, double pExtentZ)
    {
        VgPoint center = this.getCenterPoint();

        this.setXMin(center.getX() - pExtentX / 2.);
        this.setXMax(center.getX() + pExtentX / 2.);
        this.setYMin(center.getY() - pExtentY / 2.);
        this.setYMax(center.getY() + pExtentY / 2.);
        this.setZMin(center.getZ() - pExtentZ / 2.);
        this.setZMax(center.getZ() + pExtentZ / 2.);
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
     * returns the length of bounding-box's diagonal in 3D space.
     *
     * @return Diagonal length
     */
    public double diagonalLength()
    {
        double dx = this.getXMax() - this.getXMin();
        double dy = this.getYMax() - this.getYMin();
        double dz = this.getZMax() - this.getZMin();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

	/**
	 * returns the bounding-box's volume with respect to the assigned spatial reference system.
     *
     * @return Volume
	 */
	public double volume() 
	{
		double dx = this.getXMax() - this.getXMin();
		double dy = this.getYMax() - this.getYMin();
		double dz = this.getZMax() - this.getZMin();
		
		return dx * dy * dz;
	}

	/**
	 * returns the surface area with respect to the assigned spatial reference system.
     *
     * @return Surface area
	 */
	public double surface()
	{
		double dx = this.getXMax() - this.getXMin();
		double dy = this.getYMax() - this.getYMin();
		double dz = this.getZMax() - this.getZMin();
		
		return (2. * dx * dy + dx * dz + dy * dz);		
	}

    /**
     * extends the bounding-box's spatial extent such that the specified point will be not lie outside of the
     * bounding-box.
     *
     * @param pPnt Point that will be element of the modified bounding-box
     */
    public void letContainPoint(VgPoint pPnt)
    {
    	this.assertSRS(pPnt);

        if (pPnt.getX() < this.getXMin()) this.setXMin(pPnt.getX()); else {
            if (pPnt.getX() > this.getXMax()) this.setXMax(pPnt.getX());
        }
        if (pPnt.getY() < this.getYMin()) this.setYMin(pPnt.getY()); else {
            if (pPnt.getY() > this.getYMax()) this.setYMax(pPnt.getY());
        }
        if (pPnt.getZ() < this.getZMin()) this.setZMin(pPnt.getZ()); else {
            if (pPnt.getZ() > this.getZMax()) this.setZMax(pPnt.getZ());
        }
    }

    /**
     * extends the bounding-box's spatial extent such that te specified bounding-box will be part of the
     * bounding-box (&quot;union operator&quot;).
     *
     * @param pEnv Bounding-box that will be element of the modified bounding-box
     */
    abstract public void letContainEnvelope(VgEnvelope pEnv);

    /**
     * checks bounding-boxes for geometrical equivalence.
     *
     * @param pEnv Bounding-box that has to be compared with <tt>this</tt> object
     * @return <i>true</i>, if for geometrical equivalence, else <i>false</i>
     */
    public boolean isSpatiallyEquivalent(VgEnvelope pEnv)
    {
        double eps = 1.0e-2; // sollte f�r in Metern gegebene Koordinaten ausreichen (Zentimeter-Genauigkeit) -> todo
        return
            Math.abs(this.getXMin() - pEnv.getXMin()) < eps &&
            Math.abs(this.getXMax() - pEnv.getXMax()) < eps &&
            Math.abs(this.getYMin() - pEnv.getYMin()) < eps &&
            Math.abs(this.getYMax() - pEnv.getYMax()) < eps;
    }

    /**
     * checks, if a given point lies inside the bounding-Box.
     *
     * @param pt Point to be checked
     * @return <i>true</i>, if <tt>pt</tt> lies inside the bounding-box or on the border, else <i>false</i>
     */
    public boolean contains(VgPoint pt) {
        // TODO: SRS-�berpr�fung fehlt noch!
        double x = pt.getX();
        if (x < this.getXMin() || x > this.getXMax()) return false;
        double y = pt.getY();
        if (y < this.getYMin() || y > this.getYMax()) return false;
        double z = pt.getZ();
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
