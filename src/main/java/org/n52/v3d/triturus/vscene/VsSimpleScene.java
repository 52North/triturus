/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.t3dutil.T3dColor;

import java.lang.Math;

/**
 * Specification of a simple scene that consists of a grid-based <i>digital elevation model</i> (DEM), a
 * <i>drape-image</i> referring to this terrain surface, <i>viewpoint definitions</i>, and <i>light-sources</i>.
 * <p>
 * <tt>VsSimpleScene</tt>-descriptions are suitable particularly to produce simple visualizations that show a single
 * elevation-model. From the elevation model's spatial extent, a normalized 3D bounding-box will be calculated:
 * <ul>
 *     <li>In the x-y-plane, this will be a subset of the range -1 &lt;= x' &lt;= +1, -1 &lt;= y' &lt;= +1, whereat the
 *     normalized coordinates are symbolized using an apostrophe ('). For quadratic models this extent will be filled
 *     in completely. For rectangular elevation-models (i.e., different extents with regard to x- and y-direction),
 *     either for x', or for y' only a sub-range will be covered. Anyway, the center-point of the normalized model will
 *     be (x', y') = (0, 0).</li>
 *     <li>The extent in z-direction will be computed from the elevation-values in a way, that vertical height-scale
 *     and horizontal scale (x-y-plane) correspond. Notably, no (!) exaggeration factor will be considered.</li>
 * </ul>
 * <p>
 * For <tt>VsSimpleScene</tt>s, viewpoint and light-source definitions can be specified easily, since the
 * helper-method <tt>denorm()</tt> allows to specify parameters using normalized coordinates. Hence, viewpoints and
 * light-source parameters are <i>invariant with respect to the set exaggeration-value.</i>
 * <p>
 * <i>German:</i>Spezifikation f&uuml;r eine einfache Szene, die ein Gitter-basiertes Gel�ndemodell, eine
 * zugeh&ouml;rige Drape-Datei sowie Ansichtspunkte und Lichtquellen enth&auml;lt.
 * <p>
 * <tt>VsSimpleScene</tt>-Beschreibungen eignen sich insbesondere f&uuml;r einfache Szenen, in denen nur ein einziges
 * Gel&auml;ndemodell visualisiert wird. Aus der r&auml;umlichen Ausdehnung des darzustellenden Gel&auml;ndemodells
 * l&auml;sst sich ein <i>normierter Raumausschnitt</i> ableiten, der alle Visualisierungsobjekte enth&auml;lt:
 * <ul>
 *     <li>In der xy-Ebene ist dies eine Teilmenge der Bereichs -1 &lt;= x' &lt;= +1, -1 &lt;= y' &lt;= +1, wobei die
 *     normierten Koordinaten mit einem Strich (') kenntlich gemacht sind. F�r quadratische Gel&auml;ndemodelle wird
 *     dieser Raumausschnitt voll eingenommen; f&uuml;r Gel&auml;ndemodelle, deren Ausdehnung in x- und y-Richtung
 *     unterschiedlich ist, wird entweder f&uuml;r x' oder f&uuml;r y' (exklusive "Oder"-Verkn&uuml;pfung) nur ein
 *     Teilbereich eingenommen. Der Mittelpunkt des Gel&auml;ndemodells liegt in jedem Fall an der Position
 *     (x', y') = (0, 0).</li>
 *     <li>Die Ausdehnung in z'-Richtung ergibt in der Art und Weise aus den H&ouml;henwerten, als dass der vertikale
 *     H&ouml;henma&szlig;stab und der horizontale Ma&szlig;stab korrespondieren. Insbesondere wird hierbei kein (!)
 *     &Uuml;berh&ouml;hungsfaktor ber&uuml;cksichtigt.</li>
 * </ul>
 * <p>
 * In <tt>VsSimpleScene</tt>-Szenen lassen sich Ansichtspunkte und Lichtquellen mit Hilfe der Methode <tt>denorm()</tt>
 * bezogen auf eine normierte Bounding-Box angeben. Ansichtspunkte und Lichtquellenparameter sind dabei <i>invariant
 * gegen&auml;ber der &Uuml;berh&ouml;hung.</i>
 *
 * @author Benno Schmidt
 */
// TODO "Vs" in VsScene-Implementierungsklassen als Präfix entfernen
abstract public class VsSimpleScene extends VsScene
{
	private VgElevationGrid mTerrain = null;
	private String mDrape = null;

    private double mScale; // Scaling factor used for geo-coordinate normalization
    private T3dVector mOffset = new T3dVector(); // Translation vector used for geo-coordinate normalization
    private double mAspect; // Aspect ratio y-extent : x-extent

    private T3dColor mBackgroundColor = new T3dColor(0.f,0.f,0.f);

	private boolean mDrawBBox = false;
    private boolean mDrawPedestal = false;
    private T3dColor mPedestalColor = new T3dColor(0.5f,0.5f,0.5f);

	/**
	 * sets the elevation model that will be visualized as relief ("terrain").
     * <p>
     * You can not add more than one elevation-model to the <tt>VsSimpleScene</tt>.
     * <p>
     * Note, that the elevation-model must be a grid-based model. E.g., <tt>MultiTerrainScene</tt> descriptions do
     * not support triangulated irregular networks (TINs) yet (although it should be easy to implement this).
     *
	 * @param pTerrain Elevation-model
	 */
	public void setTerrain(VgElevationGrid pTerrain) {
		mTerrain = pTerrain;
		this.calculateNormTransformation();
	}

	/**
	 * gets the elevation-model that will be visualized as relief ("terrain").
     *
	 * @return Elevation-model
	 */
	public VgElevationGrid getTerrain() {
		return mTerrain;
	}

	/**
	 * defines the drape image that will be projected to the relief ("terrain") as texture.
     * <p>
	 * You can define not more than one drape-image for a <tt>VsSimpleScene</tt>. The concrete implementation processing
     * the <tt>VsSimpleScene</tt> has to ensure that this drape-texture is geo-referenced. If no drape-image shall be
     * used, you might pass <i>null</i> as parameter.
     *
	 * @param pImageFile Image file-name (complete file path), or <i>null</i>
	 */
	public void setDrape(String pImageFile) {
		mDrape = pImageFile;
	}

	/**
	 * gets the file-name of the image that will be projected to the relief ("terrain") as texture.
     *
	 * @return Image file-name (complete file path), or <i>null</i>
	 */
	public String getDrape() {
		return mDrape;
	}

	/**
	 * returns a position referring to the normalized coordinate-space for a position in geo-coordinate-space. I.e.,
     * this method transforms geo-coordinates into (<tt>VsSimpleScene</tt>-specific) normalized coordinates.
     * <p>
     * For the result point, the assertion -1 &lt;= x' &lt; +1, -1 &lt;= y' &lt; +1 must hold, if and only if the
     * position is inside the model's bounding-box.
     * <i>German:</i>: liefert den zu einer im r&auml;umlichen Referenzsystem vorliegenden Position geh&ouml;rigen
     * Punkt im normierten Koordinatensystem der <tt>VsSimpleScene</tt>.
     *
	 * @param pGeoPos Georeferenced point
	 * @return Point in normalized coordinate space
	 * @see VsSimpleScene#denorm
	 */
	public T3dVector norm(VgPoint pGeoPos)
	{
		return new T3dVector(
		    pGeoPos.getX() * mScale + mOffset.getX(),
		    pGeoPos.getY() * mScale + mOffset.getY(),
		    pGeoPos.getZ() * mScale);
	}

	/**
     * returns a position referring to the geo-coordinate-space for a position in the normalized coordinate-space.
     * I.e., this method transforms (<tt>VsSimpleScene</tt>-specific) normalized coordinates into geo-coordinates.
     * <p>
	 * <i>German:</i> liefert den zu einem im nomierten Koordinatensystem der <tt>VsSimpleScene</tt> vorliegenden
     * Position geh&ouml;rigen Punkt im r&auml;umlichen Referenzsystem der Szene.
     *
	 * @param pNormPos Georeferenced point
	 * @return Point in normalized coordinate space
	 * @see VsSimpleScene#norm
	 */
	public VgPoint denorm(T3dVector pNormPos)
	{
		return new GmPoint(
		    (pNormPos.getX() - mOffset.getX()) / mScale,
		    (pNormPos.getY() - mOffset.getY()) / mScale,
		    pNormPos.getZ() / mScale);
	}

	private void calculateNormTransformation()
	{
        VgEnvelope envXY = this.getTerrain().getGeometry().envelope();
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
     * returns the scale factor that corresponds to the normalization-transformation.
     * <p>
     * Note: The normalization will be performed applying the scaling <tt>this.getScale()</tt> to geo-coordinates
     * first, and then the translation <tt>this.getOffset()</tt>.
     * <p>
     * <i>German:</i> liefert den Skalierungsfaktor f&uuml;r die Normierung von Geo-Koordinaten innerhalb der
     * <tt>VsSimpleScene</tt>.
     * <p>
	 * Bem.: F&uuml;r die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
	 * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.
     *
	 * @return Scaling factor
	 * @see VsSimpleScene#getOffset
	 */
	public double getScale() {
		return mScale;
	}

    /**
     * returns the aspect-ratio of y-extent and x-extent of the elevation-model.
     * <p>
     * <i>German:</i> liefert das Seitenverh&auml;ltnis der y-Ausdehnung zur x-Audehnung des Gel&auml;ndemodells.
     *
     * @return Aspect-ratio
     */
    public double getAspect() {
        return mAspect;
    }

    /**
     * returns the translation vector that corresponds to the normalization-transformation.
     * <p>
     * Note: The normalization will be performed applying the scaling <tt>this.getScale()</tt> to geo-coordinates
     * first, and then the translation <tt>this.getOffset()</tt>.
     * <p>
     * <i>German:</i> liefert den Translationsvektor f&uuml;r die Normierung von Geo-Koordinaten innerhalb der
     * <tt>VsSimpleScene</tt>.
     * <p>
     * Bem.: F&uuml;r die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
     * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.
     *
     * @return Translation vector
     * @see VsSimpleScene#getScale
     */
	public T3dVector getOffset() {
		return mOffset;
	}

	/**
	 * gets the minimum z'-value of the elevation-model <tt>this.getTerrain</tt> with respect to normalized coordinate
     * space.
     *
	 * @return normalized z'-coordinate for the minimum elevation-value inside the <tt>VsSimpleScene</tt>
	 */
	public double normZMin() {
		return mTerrain.minimalElevation() * mScale;
	}

    /**
     * gets the maximum z'-value of the elevation-model <tt>this.getTerrain</tt> with respect to normalized coordinate
     * space.
     *
     * @return normalized z'-coordinate for the maximum elevation-value inside the <tt>VsSimpleScene</tt>
     */
	public double normZMax() {
		return mTerrain.maximalElevation() * mScale;
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
	 * controls, if the 3D-bounding-box corresponding to the elevation-model will be visible.

	 * @param pDrawBBox <i>true</i>, to add the bounding-box shape to the scene
	 */
	public void drawBBoxShape(boolean pDrawBBox) {
		mDrawBBox = pDrawBBox;
	}

    protected boolean drawBBox() {
		return mDrawBBox;
	}

    /**
     * controls, if a pedestal shape will be visualized.
     * <p>
     * Note: This option might not be supported by all visualisation implementations.
     * <p>
     * <i>German:</i> steuert, ob ein zum Gel&auml;ndemodell geh&ouml;riger Sockel generiert und zur Szene
     * hinzugef&uuml;gt wird.
     *
     * @param pDrawPedestal <i>true</i>, to add a pedestal to the scene
     */
    public void drawTerrainPedestal(boolean pDrawPedestal) {
        mDrawPedestal = pDrawPedestal;
    }

    protected boolean drawTerrainPedestal() {
		return mDrawPedestal;
	}

    /**
     * sets he pedestal's color. By default, gray (50 %) will be used.
     *
     * @param pColor Pedestal color
     */
    public void setPedestalColor(T3dColor pColor) {
        mPedestalColor = pColor;
    }

    /**
     * sets he pedestal's color.
     *
     * @return Pedestal color
     */
    public T3dColor getPedestalColor() {
        return mPedestalColor;
    }
}
