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

import java.util.ArrayList;

import org.n52.v3d.triturus.t3dutil.GKTransform;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;

/**
 * <tt>VgLineString</tt>-implementation. x- and y-values have to be given with 
 * respect to the spatial reference system (SRS) that has been set for the 
 * geometric object. z-values might be provided for the object's vertices.
 * 
 * @author Benno Schmidt
 */
public class GmLineString extends VgLineString
{
    private ArrayList<VgPoint> mVertices = null;
    
    public GmLineString() {
    	mVertices = new ArrayList<VgPoint>();
    }

    /**
     * Constructor. The line string will be initialized using the given 
     * comma-separated coordinate list. This list must consist if at least two
     * vertices; z-values mist be given.
     * <br/>
     * Example: <tt>&quot;3500010,5800010,50.5,3600010,5800010,100&quot;</tt><p>
     * <br/>
     * The constructor method will throw a <tt>T3dException</tt>, if the given
     * string does not contain interpretable coordinates.
     * 
     * @param commaSeparatedList List containing 3 x N coordinates (N > 1)
     */
    public GmLineString(String commaSeparatedList)
    {
        String[] coords = commaSeparatedList.split(",");
        if (coords.length % 3 != 0)
            throw new T3dException("Cannot parse line string coordinates from \"" + commaSeparatedList + "\".");
        int N = coords.length / 3;
        if (N < 2)
            throw new T3dException("Invalid line string specification: \"" + commaSeparatedList + "\".");

        mVertices = new ArrayList<VgPoint>();

        double x = 0., y = 0., z = 0.;
        VgPoint pt = null;
        for (int i = 0; i < N; i++) {
            x = Double.parseDouble(coords[3 * i]);
            y = Double.parseDouble(coords[3 * i + 1]);
            z = Double.parseDouble(coords[3 * i + 2]);
            pt = new GmPoint(x, y, z);
            this.addVertex(pt);
        }
    }

    /**
     * adds a vertex point to the polyline (at the end of the vertex-list).
     * <br/>
     * Precondition: <tt>N = this.numberOfVertices()</tt>
     * Postcondition: <tt>this.numberOfVertices() = N + 1</tt>
     * 
     * @throws T3dException
     */
    public void addVertex(VgPoint p) throws T3dException
    {
    	this.assertSRS(p);
    	GmPoint lPnt = new GmPoint(p);
    	mVertices.add(lPnt);
    }

    public VgPoint getVertex(int i) throws T3dException
    {
    	if (i < 0 || i >= this.numberOfVertices())
    	    throw new T3dException("Index out of bounds.");
    	// else:
    	return (GmPoint) mVertices.get(i);
    }

    public int numberOfVertices() {
    	return mVertices.size();
    }
    
    /** 
     * returns the geometric object's bounding-box.
     * 
     * @return <tt>GmEnvelope</tt>, or <i>null</i> for <tt>this.numberOfVertices() = 0</tt>.
     */
    public VgEnvelope envelope()
    {
    	if (this.numberOfVertices() > 0) {
    	    GmEnvelope env = new GmEnvelope( this.getVertex(0) );
            for (int i = 0; i < this.numberOfVertices(); i++)
                env.letContainPoint(this.getVertex(i));
            return env;
        } else
            return null;
    }
    
    /** 
	 * returns the object's footprint geometry (projection to the x-y plane).
	 * 
	 * @return &quot;Footprint&quot; as <tt>GmLineString</tt>-object
  	 */
	public VgGeomObject footprint()
	{
		GmLineString res = new GmLineString();
		VgPoint v = null;
		for (int i = 0; i < this.numberOfVertices(); i++) {
			v = new GmPoint(this.getVertex(i));
			v.setZ(0.);
			this.addVertex(v);
		}	
		return res;
	}
	
	/**
	 * @deprecated
	 * Temporary implementation used in the context of cross-section processing
	 * (&quot;profiles&quot;).
	 * <br/>
	 * TODO: Transform cross-sections to other SRSs (&quot;GK-Meridianstreifen&quot;).
	 * TODO: Proper integration of this functionality into the Triturus framework.
	 */
	public GmLineString getConverted() {
		GmLineString ret = new GmLineString();
		double[] convertedPoint = new double[2];
		VgPoint point;
		for (int i = 0; i < mVertices.size(); i++) {
			point = (VgPoint) mVertices.get(i);
			GKTransform.gaussToEll(point.getX(), point.getY(), 2, convertedPoint);
			GKTransform.ellToGauss(convertedPoint[0], convertedPoint[1], 3, convertedPoint);
			ret.addVertex(new GmPoint(convertedPoint[0], convertedPoint[1], point.getZ()));			
			//System.out.println("Conv "+ i + " from: " + point + " TO " + new GmPoint(convertedPoint[0], convertedPoint[1], point.getZ()));
		}
		return ret;
	}
}