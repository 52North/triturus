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
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dException;
import java.util.ArrayList;

/**
 * Abstract base class to manage scene descriptions for generic 3D geo-visualizations. In the scope of the Triturus
 * framework, this class allows to abstract from the concrete visualization-environment that is used.
 * <p>
 * Examples for concrete implementations of this class would be generic scene-graphs or concrete scene-descriptions
 * encoded using Java 3D, Xith3D, VRML, GeoVRML, X3D, AVS/Express networks, POV-Ray-scenes, etc.
 *
 * @author Benno Schmidt
 */
abstract public class VsScene
{
	private double mExaggeration = 1.;
	
	private ArrayList mCameras = null;
	private int mCurrentCamera = -1;
	
	private ArrayList mLights = null;
	
	/**
	 * sets the initial exaggeration value (vertical height scale) of the scene. The default-value is set to 1.0.
     *
	 * @param pExaggeration z-factor
	 */	
	public void setDefaultExaggeration(double pExaggeration) {
		mExaggeration = pExaggeration;
	}

	/**
     * gets the initial exaggeration value (vertical height scale) of the scene.
	 * @return z-factor
	 */	
	public double getDefaultExaggeration() {
		return mExaggeration;
	}

	/**
	 * adds a camera to the scene.
     * <p>
     * A <tt>VsScene</tt> might consist of multiple cameras. By default, the first camera that has been added to the
     * scene, will be used when starting the visualization.
     *
	 * @param pCamera Camera-definition
	 * @see VsScene#setCurrentCamera
	 */
	public void addCamera(VsCamera pCamera) 
	{
		if (mCameras == null) {
		    mCameras = new ArrayList();
		    mCurrentCamera = 0;
		}
		mCameras.add(pCamera);
	}

	/**
	 * gets the i-th camera that has been defined to be part of the scene.
     * <p>
	 * Note: For i, the assertion 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> must hold.
     *
	 * @param i Camera index
	 * @return Camera-definition
	 * @throws T3dException if an unrepairable error occurs
	 */
	public VsCamera getCamera(int i) throws T3dException
	{
		if (mCameras == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfCameras())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfCameras() + "].");
			return (VsCamera) mCameras.get(i);
		}
	}

	/**
	 * gets the number of cameras that have been defined.
     *
	 * @return Number &gt;= 0
	 */
	public int numberOfCameras() {
		if (mCameras == null)
			return 0;
		else
			return mCameras.size();
	}
	
	/**
	 * sets the current camera.
     * <p>
     * Note: For the index i, the assertion 0 &lt;= i &lt; <tt>this.numberOfCameras()</tt> must hold.
     *
	 * @param i the current camera's index
	 * @throws T3dException if an unrepairable error occurs
	 */
	public void setCurrentCamera(int i) throws T3dException
	{
		if (i < 0 || i >= this.numberOfCameras())
			throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfCameras() + "].");
		mCurrentCamera = i;	
	}

	/**
	 * returns the current camera.
     *
	 * @return Camera
	 * @throws T3dException if an unrepairable error occurs
	 */
	public VsCamera getCurrentCamera() throws T3dException
	{
		if (mCameras == null)
			return null;
		else
			return (VsCamera) mCameras.get(mCurrentCamera);
	}

	/**
	 * returns the current viewpoint of the scene. This viewpoint corresponds to the current viewpoint of the current
     * camera.
     *
	 * @return Viewpoint
	 * @throws T3dException if an unrepairable error occurs
	 */
	public VsViewpoint getCurrentViewpoint() throws T3dException
	{
		if (mCameras == null)
			return null;
		else
			return this.getCurrentCamera().getCurrentViewpoint();
	}

    /**
     * removes all cameras from the scene.
     */
    public void removeCameras()
    {
        if (mCameras != null)
            mCameras.clear();
    }

	/**
	 * adds a light-source to the scene.
     * <p>
     * A <tt>VsScene</tt> might consist of multiple light-sources.
     *
	 * @param pLight Light-source definition
	 */
	public void addLightSource(VsLightSource pLight) 
	{
		if (mLights == null)
		    mLights = new ArrayList();
		mLights.add(pLight);
	}

	/**
     * gets the i-th light-source that has been defined to be part of the scene.
     * <p>
     * Note: For i, the assertion 0 &lt;= i &lt; <tt>this.numberOfLightSources()</tt> must hold.
     *
	 * @param i Light-source index
	 * @return Light-source definition
	 * @throws T3dException if an unrepairable error occurs
	 */
	public VsLightSource getLightSource(int i) throws T3dException
	{
		if (mLights == null)
		    return null;
		else {
			if (i < 0 || i >= this.numberOfLightSources())
			    throw new T3dException("Index " + i + "out of bounds [0:" + this.numberOfLightSources() + "].");
			return (VsLightSource) mLights.get(i);
		}
	}

	/**
     * gets the number of light-sources that have been defined.
     *
     * @return Number &gt;= 0
	 */
	public int numberOfLightSources() {
		if (mLights == null)
			return 0;
		else
			return mLights.size();
	}

    /**
     * removes all light-sources from the scene.
     */
    public void removeLightSources()
    {
        if (mLights != null)
            mLights.clear();
    }

	/**
     * generates the scene-description according to the settings of the objects that have been added to the current
     * scene. A call to this method might result in a scene description file (which not necessarily refers to a
     * scene-graph-based format), or a scene-graph object.
     * <p>
	 * Note: This abstract method has to be implemented by a concrete <tt>VsScene</tt>-realisation.
     *
	 * @return Scene-description
	 */
	abstract public Object generateScene();
}
