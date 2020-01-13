/**
 * Copyright (C) 2020 52North Initiative for Geospatial Open Source
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
package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.t3dutil.T3dVector;

/**
 * Abstract base class to hold a tetrahedronal geometry.
 * 
 * @author Benno Schmidt
 */
abstract public class VgTetrahedron extends VgGeomObject3d 
{
	/** 
	 * sets the tetrahedron's corner-points.
	 * 
	 * @param p1 First corner point
	 * @param p2 Second corner point
	 * @param p3 Third corner point
	 * @param p4 Fourth corner point
	 */
	abstract public void setCornerPoints(
		VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4);
	
	/** 
	 * returns the tetrahedron's corner-points.
	 * 
	 * @return Array consisting of four elements holding the corner-points
	 */
	abstract public VgPoint[] getCornerPoints();

	@Override
	public double surface() {
		// TODO
		throw new T3dNotYetImplException();
	}

	@Override
	public VgGeomObject footprint() {
		// TODO
		throw new T3dNotYetImplException();
	}    

	@Override
	public double volume() {
		// TODO Method has not been tested yet.
		VgPoint[] p = this.getCornerPoints();
		T3dVector 
			v3 = new T3dVector(),
			v2 = new T3dVector(),
			v1 = new T3dVector(),
			v23 = new T3dVector();
		v3.assignDiff(p[3], p[0]);
		v2.assignDiff(p[2], p[0]);
		v1.assignDiff(p[1], p[0]);
		v23.assignCrossProd(v3, v2);
		return Math.abs(v1.scalarProd(v23)) / 6.;
	}
	
	public String toString() {
		VgPoint[] p = this.getCornerPoints();
		return "[" + 
			p[0].toString() + ", " + 
			p[1].toString() + ", " + 
			p[2].toString() + ", " + 
			p[3].toString() + "]";
	}
}