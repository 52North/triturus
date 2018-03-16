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
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.t3dutil.MpHypsometricColor;
import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.t3dutil.T3dSymbolInstance;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgElevationGrid;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/**
 * Specification of a scene that consists of an arbitrary number of grid-based 
 * <i>digital elevation models</i> (DEMs), an arbitrary number of <i>marker 
 * objects</i>, <i>viewpoint definitions</i>, and </i>light-sources</i>.
 * <p>
 * Examples for typical use cases:
 * <ul>
 *     <li>Visualization of > 1 (neighboring) elevation models</li>
 *     <li>Visualization of elevation models referring to different thematic 
 *     aspects (e.g., terrain surface and groundwater levels)</li>
 * </ul>
 * <p>
 * From the elevation models' spatial extents, a normalized 3D bounding-box 
 * will be calculated dynamically (as for {@link VsSimpleScene}s, see description 
 * there):
 * <ul>
 *     <li>In the x-y-plane, this will be a subset of the range 
 *     -1 &lt;= x' &lt;= +1, -1 &lt;= y' &lt;= +1, whereat the normalized 
 *     coordinates are symbolized using an apostrophe (').</li>
 *     <li>The extent in z-direction will be computed from the elevation-values 
 *     in a way, that vertical height-scale and horizontal scale (x-y-plane) 
 *     correspond (no exaggeration factor!).</li>
 * </ul>
 * <p>
 * For {@link MultiTerrainScene}s, viewpoint and light-source definitions can 
 * be specified easily, since the helper-method <tt>denorm()</tt> allows to 
 * specify parameters using normalized coordinates. Hence, viewpoints and 
 * light-source parameters are <i>invariant with respect to the set 
 * exaggeration-value.</i>
 *
 * @author Benno Schmidt
 * @see VsScene
 * @see VsSimpleScene
 */
public class MultiTerrainScene extends VsScene
{
    private ArrayList<VgElevationGrid> mTerrains = null;
    private VgEnvelope mBBox = null; // 'null' to indicate BBox-calculation is necessary

    private ArrayList<T3dSymbolInstance> mMarkers = null;

    private double mScale; // Scaling factor used for geo-coordinate normalization
    private T3dVector mOffset = new T3dVector(); // Translation vector used for geo-coordinate normalization
    private double mAspect; // Aspect ratio y-extent : x-extent

    private T3dColor mDefaultReliefColor = new T3dColor(0.f,1.f,0.f);
    private T3dColor mBackgroundColor = new T3dColor(0.f,0.f,0.f);
    private T3dColor mBBoxColor = new T3dColor(1.f,1.f,1.f);
    private boolean mDrawBBox = false;
    private MpHypsometricColor mHypsometricColMap = null;


    /**
     * adds an elevation model to be visualized as relief ("terrain") to the 
     * current scene. You can not add more than one elevation-model to the 
     * {@link VsSimpleScene}. Note, that the elevation-model must be a 
     * grid-based model. E.g., {@link MultiTerrainScene} descriptions do
     * not support triangulated irregular networks (TINs) yet (although it 
     * should be easy to implement this).
     *
     * @param terrain Elevation-model
     */
    public void addTerrain(VgElevationGrid terrain)
    {
        if (mTerrains == null)
            mTerrains = new ArrayList<VgElevationGrid>();

        if (terrain != null) {
            mTerrains.add(terrain);

            // Determine new Bounding-box:
            VgEnvelope lBBox = new GmEnvelope(
                    terrain.getGeometry().envelope().getXMin(),
                    terrain.getGeometry().envelope().getXMax(),
                    terrain.getGeometry().envelope().getYMin(),
                    terrain.getGeometry().envelope().getYMax(),
                    terrain.minimalElevation(),
                    terrain.maximalElevation());

            if (mBBox == null) {
                // Since mTerrains must not be empty (after calls to 
            	// removeTerrain()), mBBox = lBBox is not sufficient!
                mBBox = this.envelope();
            }

            mBBox.letContainEnvelope(lBBox);

            this.calculateNormTransformation();
        }
    }

    /**
     * gets the elevation-models to be visualized. If no elevation-models have 
     * been added to the scene, the method will return <i>null</i>.
     *
     * @return List of elevation-models 
     */
    public ArrayList<VgElevationGrid> getTerrains() {
        return mTerrains;
    }

    /**
     * removes an elevation-model from the current scene.
     *
     * @param terrain Elevation-model object to be removed
     */
    public void removeTerrain(VgElevationGrid terrain)
    {
        mTerrains.remove(terrain);
        mBBox = null; // i.e., BBox is invalid, requires calculation
    }

    /**
     * returns the 3D bounding-box with respect to the elevation-grids that 
     * are part of the scene. If no elevation-grids are part of the scene,
     * the method will return <i>null</i>. Note that marker locations will 
     * be ignored.
     *
     * @return Bounding-box (or <i>null</i>)
     */
    public VgEnvelope envelope()
    {
        if (mBBox != null)
            return mBBox;

        if (mTerrains == null || mTerrains.size() <= 0)
            return null;

        // Else calculate bounding-box:
        VgEnvelope bb2d, bb;
        for (VgElevationGrid t : mTerrains) {
            bb2d = t.getGeometry().envelope();
            bb = new GmEnvelope(
                    bb2d.getXMin(),
                    bb2d.getXMax(),
                    bb2d.getYMin(),
                    bb2d.getYMax(),
                    t.minimalElevation(),
                    t.maximalElevation());

            if (mBBox == null) {
                mBBox = bb;
            } else {
                mBBox.letContainEnvelope(bb);
            }
        }

        this.calculateNormTransformation();

        return mBBox;
    }

    /**
     * adds a marker to be visualized to the current scene.
     *
     * @param marker Marker specification
     */
    public void addMarker(T3dSymbolInstance marker)
    {
        if (mMarkers == null)
            mMarkers = new ArrayList<T3dSymbolInstance>();

        if (marker != null)
            mMarkers.add(marker);
    }

    /**
     * gets the markers to be visualized. If no markers have been added to 
     * the scene, the method will return <i>null</i>.
     *
     * @return List of markers
     */
    public ArrayList<T3dSymbolInstance> getMarkers() {
        return mMarkers;
    }

    /**
     * removes a marker from the current scene.
     *
     * @param pMarker Marker object to be removed
     */
    public void removeMarker(T3dSymbolInstance pMarker)
    {
        // TODO Method has not been tested yet...
        mMarkers.remove(pMarker);
    }

    /**
     * returns a position referring to the normalized coordinate-space for 
     * a position in geo-coordinate-space. I.e., this method transforms 
     * geo-coordinates into (<tt>MultiTerrainScene</tt>-specific) normalized 
     * coordinates.
     * <p>
     * For the result point, the assertion 
     * -1 &lt;= x' &lt; +1, -1 &lt;= y' &lt; +1 must hold, if and only if the
     * position is inside the model's bounding-box.
     *
     * @param pGeoPos Georeferenced point
     * @return Point in normalized coordinate space
     * @see MultiTerrainScene#denorm
     */
    public T3dVector norm(VgPoint geoPos)
    {
        // todo: Method is identical to VsSimpleScene#norm - maybe this should be refactored...

        return new T3dVector(
            geoPos.getX() * mScale + mOffset.getX(),
            geoPos.getY() * mScale + mOffset.getY(),
            geoPos.getZ() * mScale);
    }

    /**
     * returns a position referring to the geo-coordinate-space for a position 
     * in the normalized coordinate-space. I.e., this method transforms 
     * (@link MultiTerrainScene}-specific) normalized coordinates into 
     * geo-coordinates.
     *
	 * @param normPos Georeferenced point
	 * @return Point in normalized coordinate space
	 * @see MultiTerrainScene#norm
	 */
	public VgPoint denorm(T3dVector normPos)
	{
        // todo: Method is identical to VsSimpleScene#denorm 
		// - maybe this should be refactored in the future...

		return new GmPoint(
		    (normPos.getX() - mOffset.getX()) / mScale,
		    (normPos.getY() - mOffset.getY()) / mScale,
		    normPos.getZ() / mScale);
	}

    private void calculateNormTransformation()
	{
        // todo: Method is rather identical to VsSimpleScene#calculateNormTransformation 
		// - maybe this should be refactored in the future...

        VgEnvelope envXY = this.envelope();
		double xMinGeo = envXY.getXMin();
		double xMaxGeo = envXY.getXMax();
		double yMinGeo = envXY.getYMin();
		double yMaxGeo = envXY.getYMax();

        double dx = xMaxGeo - xMinGeo;
        double dy = yMaxGeo - yMinGeo;
        mAspect = dy/dx;

        if (Math.abs(dx) > Math.abs(dy)) {
        	mScale = 2./dx;
        	mOffset.setX(-(xMinGeo + xMaxGeo)/ dx);
        	mOffset.setY(-(yMinGeo + yMaxGeo)/ dx);
        }
        else {
        	mScale = 2./dy;
        	mOffset.setX(-(xMinGeo + xMaxGeo)/ dy);
        	mOffset.setY(-(yMinGeo + yMaxGeo)/ dy);
        }
	}

    /**
     * returns the scale factor that corresponds to the 
     * normalization-transformation. Note that the normalization will 
     * be performed applying the scaling <tt>this.getScale()</tt> to 
     * geo-coordinates first, and then the translation 
     * <tt>this.getOffset()</tt>.
     *
     * @return Scaling factor
     * @see VsSimpleScene#getOffset
     */
    public double getScale() {
        // todo: Method is identical to VsSimpleScene#getScale 
		// - maybe this should be refactored in the future...
        return mScale;
    }

    /**
     * returns the aspect-ratio of y-extent and x-extent of the 
     * elevation-model.
     *
     * @return Aspect-ratio
     */
    public double getAspect() {
        // todo: Method is identical to VsSimpleScene#getAspect 
		// - maybe this should be refactored in the future...
        return mAspect;
    }

    /**
     * returns the translation vector that corresponds to the 
     * normalization-transformation. Note that the normalization will 
     * be performed applying the scaling <tt>this.getScale()</tt> to 
     * geo-coordinates first, and then the translation 
     * <tt>this.getOffset()</tt>.
     *
     * @return Translation vector
     * @see VsSimpleScene#getScale
     */
    public T3dVector getOffset() {
        // TODO: Method is identical to VsSimpleScene#getOffset 
		// - maybe this should be refactored in the future...
        return mOffset;
    }

    /**
     * gets the minimum z'-value of the elevation-model <tt>this.getTerrain</tt> 
     * with respect to normalized coordinate space.
     *
     * @return normalized z'-coordinate for the minimum elevation-value inside the <tt>VsSimpleScene</tt>
     */
    public double normZMin() {
        return this.envelope().getZMin() * mScale;
    }

    /**
     * gets the maximum z'-value of the elevation-model <tt>this.getTerrain</tt> 
     * with respect to normalized coordinate space.
     *
     * @return normalized z'-coordinate for the maximum elevation-value inside the <tt>VsSimpleScene</tt>
     */
    public double normZMax() {
        return this.envelope().getZMax() * mScale;
    }

    /**
     * sets the scene's background-color. By default, a black background is set.
     *
     * @param pColor Background-color
     */
    public void setBackgroundColor(T3dColor pColor) {
        mBackgroundColor = pColor;
    }

    /**
     * gets the scene's background-color.
     *
     * @return Background-color
     */
    public T3dColor getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * sets the relief shapes' default color. By default, a green coloring 
     * will be used. Note that this value will ignored, if a color-mapper is 
     * assigned to the scene.
     *
     * @see this#setHypsometricColorMapper(org.n52.v3d.triturus.t3dutil.MpHypsometricColor)
     * @param col Relief-color
     */
    public void setDefaultReliefColor(T3dColor col) {
        mDefaultReliefColor = col;
    }

    /**
     * gets the relief shapes' default color.
     *
     * @return Relief-color
     */
    public T3dColor getDefaultReliefColor() {
        return mDefaultReliefColor;
    }

    /**
     * sets the bounding-box color. By default, a white bounding-box will be drawn.
     *
     * @see this#drawBBoxShape
     * @param col Bounding-box color
     */
    public void setBBoxColor(T3dColor col) {
        mBBoxColor = col;
    }

    /**
     * gets the bounding-box color.
     *
     * @return Bounding-box color
     */
    public T3dColor getBBoxColor() {
        return mBBoxColor;
    }

    /**
     * controls, if the 3D-bounding-box corresponding to the elevation-model 
     * will be visible.
     *
     * @param drawBBox <i>true</i>, to add the bounding-box shape to the scene
     */
    public void drawBBoxShape(boolean drawBBox) {
        mDrawBBox = drawBBox;
    }

    protected boolean drawBBox() {
        return mDrawBBox;
    }

    /**
     * enables the usage of a hypsometric color map for terrain visualization.
     * If no coloring shall be carried out, call the method with a 
     * <i>null</i>-value for <tt>pColMap</tt>.
     *
     * @param colMap Hypsometric color-assignment or <i>null</i>
     */
    public void setHypsometricColorMapper(MpHypsometricColor colMap) {
    	mHypsometricColMap = colMap;
    }

    /**
     * gets the hypsometric color mapper that will be used to color the 
     * relief color. If no coloring will be used, the method will return
     * <i>null</i>.
     *
     * @return color-mapper object (<i>null</i>, if no coloring used)
     */
    public MpHypsometricColor getHypsometricColorMapper() {
        return mHypsometricColMap;
    }

    public Object generateScene() {
        // ???
        return new T3dNotYetImplException(); // todo ???
    }
}
