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

import java.util.ArrayList;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgLinearRing;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Implementation of {@link VgLinearRing}. To construct a valid object you need
 * to repeatedly call {@link GmLinearRing#addVertex(VgPoint)}. Note that the
 * implementation does not check whether the first point equals the final point.
 * 
 * <br />
 * x- and y-values have to be given with respect to the spatial reference system
 * (SRS) that has been set for the geometric object. z-values might be provided
 * for the object's vertices.<br />
 * 
 * @author Christian Danowski
 *
 */
public class GmLinearRing extends VgLinearRing {

	private ArrayList<VgPoint> mVertices = null;

	public GmLinearRing() {
		mVertices = new ArrayList<VgPoint>();
	}

	/**
	 * adds a vertex point to the LinearRing (at the end of the vertex-list).<br />
	 * Pre-condition: <tt>N = this.numberOfVertices()</tt> Post-condition:
	 * <tt>this.numberOfVertices() = N + 1</tt>
	 * 
	 * @throws T3dException
	 */
	public void addVertex(VgPoint pPnt) throws T3dException {
		this.assertSRS(pPnt);
		GmPoint lPnt = new GmPoint(pPnt);
		mVertices.add(lPnt);
	}

	public VgPoint getVertex(int i) throws T3dException {
		if (i < 0 || i >= this.getNumberOfVertices())
			throw new T3dException("Index out of bounds.");
		// else:
		return (GmPoint) mVertices.get(i);
	}

	public int getNumberOfVertices() {
		return mVertices.size();
	}
	
	@Override
	public VgEnvelope envelope() {
		if (this.getNumberOfVertices() > 0) {
			GmEnvelope mEnv = new GmEnvelope(this.getVertex(0));
			for (int i = 0; i < this.getNumberOfVertices(); i++)
				mEnv.letContainPoint(this.getVertex(i));
			return mEnv;
		} else
			return null;
	}

	@Override
	public VgGeomObject footprint() {
		GmLinearRing res = new GmLinearRing();
		VgPoint v = null;
		for (int i = 0; i < this.getNumberOfVertices(); i++) {
			v = new GmPoint(this.getVertex(i));
			v.setZ(0.);
			this.addVertex(v);
		}
		return res;
	}

}
