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

/**
 * Abstract base class to hold a linear 3-D wedge geometry. A wedge consists 
 * of two triangular faces (situated opposite to each other) and three 
 * quadrilateral faces. The first three vertices of a wedge give the base
 * triangle. It is assumed that all normal vectors of the wedge are pointing
 * outward (i.e., the normal vectors direct towards the observer for vertices
 * given in counter-clockwise order).
 * 
 * @author Benno Schmidt
 */
abstract public class VgWedge extends VgGeomObject3d 
{
	/** 
	 * sets the wedges's corner-points.
	 * 
	 * @param p0 1st corner point (base triangle)
	 * @param p1 2nd corner point (base triangle)
	 * @param p2 3rd corner point (base triangle)
	 * @param p3 4th corner point (connected with 1st point)
	 * @param p4 5th corner point (connected with 2nd point)
	 * @param p5 6th corner point (connected with 3rd point)
	 */
	abstract public void setCornerPoints(
		VgPoint p0, VgPoint p1, VgPoint p2, VgPoint p3, VgPoint p4, VgPoint p5);
	
	/** 
	 * returns the wedges's corner-points.
	 * 
	 * @return Array consisting of six elements holding the corner-points
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
		// TODO
		throw new T3dNotYetImplException();
	}
	
	public String toString() {
		VgPoint[] p = this.getCornerPoints();
		return "[" + 
			p[0].toString() + ", " + 
			p[1].toString() + ", " + 
			p[2].toString() + ", " + 
			p[3].toString() + ", " + 
			p[4].toString() + ", " + 
			p[5].toString() + "]";
	}
}