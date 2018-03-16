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
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * Common base class for camera and viewpoint definitions. In the scope of the Triturus framework, 
 * this class allows to specify camera settings independently from a concrete rendering environment.  
 * <p>
 * 3D-applications often use a simplified "synthetic camera" model. However, the given camera parameters 
 * will not always match the capabilities given by the used graphics environment (e.g. Java 3D or VRML/X3D). 
 * Camera parameters which are available in specialized target environments only, should  be specified  
 * using specializations of the <tt>VsCamera</tt> class.
 * <p>
 * Positions have to be given using geo-coordinates. Often the usage of relative coordinates referring to the 
 * scene content's bounding-box (i.e., the spatial extent of all "shapes" inside a scene) is advantageous.   
 * This can cause problems since the bounding-box might change dynamically at run-time. However, if relative 
 * coordinates will be used, special implementations can be used, e.g. the transformation methods offered  
 * by the class <tt>VsSimpleScene</tt>.
 * <p>
 * @author Benno Schmidt
 */
public class VsCamera
{
	private ArrayList<VsViewpoint> mViewpoints = null;
	private int mCurrentViewpoint = -1;

    private String mProjectionType = PerspectiveView;
    /** Constant for perspective projection */
	public static final String PerspectiveView = "PerspectiveView";
    /** Constant for orthographic projection */
	public static final String OrthographicView = "OrthographicView";

	private double mFovy = 60.;
	
	/**
	 * adds a viewpoint to the camera.
	 * <p>
	 * A <tt>VsCamera</tt> might consist of multiple viewpoints. By default, the first viewpoint 
	 * that has been added will be set when starting the visualization.
	 * 
	 * @param viewpoint Viewpoint definition
	 * @see VsCamera#setCurrentViewpoint
	 */
	public void addViewpoint(VsViewpoint viewpoint) 
	{
		if (mViewpoints == null) {
		    mViewpoints = new ArrayList<VsViewpoint>();
		    mCurrentViewpoint = 0;
		}
		mViewpoints.add(viewpoint);
	}

	/**
	 * gets the i-th viewpoint that has  been defined  for the camera.
	 * <p>
	 * Note: The condition 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> must hold.
	 * 
	 * @param i
	 * @return Viewpoint definition
	 * @throws T3dException
	 */
	public VsViewpoint getViewpoint(int i) throws T3dException
	{
		if (mViewpoints == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfViewpoints())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfViewpoints() + "].");
			return (VsViewpoint) mViewpoints.get(i);
		}
	}

	/**
	 * gets the number of defined viewpoints.
	 * 
	 * @return Number &gt;= 0
	 */ 
	public int numberOfViewpoints() 
	{
		if (mViewpoints == null)
			return 0;
		else
			return mViewpoints.size();
	}
	
	/**
	 * sets the active viewpoint.
	 * <p>
	 * Note: The condition 0 &lt;= i &lt; <tt>this.numberOfViewpoints()</tt> must hold.
	 * 
	 * @param i Index of current viewpoint
	 * @throws T3dException
	 */
	public void setCurrentViewpoint(int i) throws T3dException
	{
		if (i < 0 || i >= this.numberOfViewpoints())
			throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfViewpoints() + "].");
		mCurrentViewpoint = i;	
	}

	/**
	 * gets the active viewpoint.
	 * 
	 * @return Viewpoint
	 * @throws T3dException
	 */
	public VsViewpoint getCurrentViewpoint() throws T3dException
	{
		if (mViewpoints == null)
			return null;
		else
			return (VsViewpoint) mViewpoints.get(mCurrentViewpoint);
	}

    /**
     * specifies the projection type for the camera. Supported are perspective and  orthographic
     * projection. By default, perspective projection  is used.
     * <p>
     * Bem.: The type <tt>this.PerspectiveView</tt> should be supported by <b>all</b> 
     * <tt>VsScene</tt>-implementations. If this type is not supported, the specific 
     * <tt>VsScene</tt>-implementation should throw a <tt>T3dNotYetImplException</tt>. 
     * 
     * @param projectionType Projection type, e.g. <tt>this.PerspectiveView</tt>
     * @see VsScene#generateScene
     * @see org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void setProjection(String projectionType) 
    {
        mProjectionType = projectionType;
    }

    /**
     * gets the projection type that has been defined for the camera.
     * 
     * @return Projection type, e.g. <tt>this.PerspectiveView</tt>
     * @see VsCamera#setProjection
     */
    public String getProjectionType () 
    {
        return mProjectionType;
    }

	/**
	 * sets the "field of view" for the camera. By default, a value of 60 degrees will be used.
	 * <p>
	 * Note: Changing this setting might influence your view! E.g., the view  position's distance from the 
	 * shapes might become necessary to set-up a proper visualization.
	 * 
	 * If an orthographic projection is set, this value will not be considered.
	 * 
	 * @param angle Angle in degrees
	 */
	public void setFovy(double angle) {
		mFovy = angle;
	}
	
	/**
	 * gets the camera's "field of view" setting.
	 * 
	 * @return Angle in degrees
	 */
	public double getFovy() {
		return mFovy;
	}
}
