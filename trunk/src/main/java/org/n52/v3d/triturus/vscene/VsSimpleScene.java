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
 * todo engl. JavaDoc
 * Spezifikation f&uuml;r eine einfache Szene, die ein Gitter-basiertes Gel�ndemodell, eine zugeh&ouml;rige Drape-Datei
 * sowie Ansichtspunkte und Lichtquellen enth&auml;lt.
 * <p>
 * <tt>VsSimpleScene</tt>-Beschreibungen eignen sich insbesondere f&uuml;r einfache Szenen, in denen nur ein einziges
 * Gel&auml;ndemodell visualisiert wird. Aus der r&auml;umlichen Ausdehnung des darzustellenden Gel&auml;ndemodells
 * l&auml;sst sich ein <i>normierter Raumausschnitt</i> ableiten, der alle Visualisierungsobjekte enth&auml;lt:
 * <ul>
 * <li>In der xy-Ebene ist dies eine Teilmenge der Bereichs -1 &lt;= x' &lt;= +1, -1 &lt;= y' &lt;= +1, wobei die
 * normierten Koordinaten mit einem Strich (') kenntlich gemacht sind. F�r quadratische Gel&auml;ndemodelle wird dieser
 * Raumausschnitt voll eingenommen; f&uuml;r Gel&auml;ndemodelle, deren Ausdehnung in x- und y-Richtung unterschiedlich
 * ist, wird entweder f&uuml;r x' oder f&uuml;r y' (exklusive "Oder"-Verkn&uuml;pfung) nur ein Teilbereich eingenommen.
 * Der Mittelpunkt des Gel&auml;ndemodells liegt in jedem Fall an der Position (x', y') = (0, 0).</li>
 * <li>Die Ausdehnung in z'-Richtung ergibt in der Art und Weise aus den H&ouml;henwerten, als dass der vertikale
 * H&ouml;henma&szlig;stab und der horizontale Ma&szlig;stab korrespondieren. Insbesondere wird hierbei kein (!)
 * &Uuml;berh&ouml;hungsfaktor ber&uuml;cksichtigt.</li>
 * <p>
 * In <tt>VsSimpleScene</tt>-Szenen lassen sich Ansichtspunkte und Lichtquellen mit Hilfe der Methode <tt>denorm()</tt>
 * bezogen auf eine normierte Bounding-Box angeben. Ansichtspunkte und Lichtquellenparameter sind dabei <i>invariant
 * gegen&auml;ber der &Uuml;berh&ouml;hung.</i>
 * <p>
 * @author Benno Schmidt
 */
abstract public class VsSimpleScene extends VsScene
{
	private VgElevationGrid mTerrain = null;
	private String mDrape = null;

	private double mScale; // Skalierung f�r Normierung von Geo-Koordinaten
	private T3dVector mOffset = new T3dVector(); // Translation f�r Normierung von Geo-Koordinaten
    private double mAspect; // Verh�ltnis y-Ausdehnung : x-Ausdehnung

    private T3dColor mBackgroundColor = new T3dColor(0.f,0.f,0.f);

	private boolean mDrawBBox = false;
    private boolean mDrawPedestal = false;
    private T3dColor mPedestalColor = new T3dColor(0.5f,0.5f,0.5f);

	/**
	 * setzt das Gel&auml;ndemodell, das als Relief ("Terrain") dargestellt werden soll.<p>
	 * Einer <tt>VsSimpleScene</tt> kann nur genau ein Gel&auml;ndemodell zugeordnet sein.<p>
	 * Bem.: Bei dem Gel&auml;ndemodell muss es sich um ein Gitter-basiertes Modell handeln; TINs z. B. werden von einer
     * <tt>VsSimpleScene</tt> nicht unterst&uuml;tzt. -> todo: bei Bedarf entsprechende <tt>VsScene</tt>-Spezialisierung bereitstellen.
	 * @param pTerrain Gel&auml;ndemodell
	 */
	public void setTerrain(VgElevationGrid pTerrain) {
		mTerrain = pTerrain;
		this.calculateNormTransformation();
	}

	/**
	 * liefert das Gel&auml;ndemodell, das als Relief ("Terrain") dargestellt werden soll.
	 * @return Gel&auml;ndemodell
	 */
	public VgElevationGrid getTerrain() {
		return mTerrain;
	}

	/**
	 * setzt die Textur, die auf das Relief ("Terrain") projiziert werden soll.<p>
	 * Einer <tt>VsSimpleGeoScene</tt> kann nur genau eine Drape-Textur zugeordnet sein. Seitens der aufrufenden
	 * Anwendung ist daf&uuml;r zu sorgen, dass diese Textur georeferenziert ist. Soll keine Drape-Textur verwendet
	 * werden, kann der Wert <i>null</i> als Parameter angegeben werden.
	 * @param pImageFile Bilddatei mit vollst&auml;ndiger Pfadangabe oder <i>null</i>
	 */
	public void setDrape(String pImageFile) {
		mDrape = pImageFile;
	}

	/**
	 * liefert die Textur, die auf das Relief ("Terrain") projiziert werden soll.
	 * @return Bilddatei mit vollst&auml;ndiger Pfadangabe oder <i>null</i>
	 */
	public String getDrape() {
		return mDrape;
	}

	/**
	 * liefert den zu einer im r&auml;umlichen Referenzsystem vorliegenden Position geh&ouml;rigen Punkt im normierten
	 * Koordinatensystem der <tt>VsSimpleScene</tt>. F&uuml;r die x'- und y'-Koordinate des Ergebnisses gilt stets
	 * -1 &lt;= x' &lt; +1, -1 &lt;= y' &lt; +1, insofern der Punkt innerhalb der Bounding-Box des Gel&auml;ndemodells
	 * liegt.
	 * @param pGeoPos georeferenzierter Punkt
	 * @return Punkt im normierten Koordinatenraum der <tt>VsSimpleScene</tt>
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
	 * liefert den zu einem im nomierten Koordinatensystem der <tt>VsSimpleScene</tt> vorliegenden Position
	 * geh&ouml;rigen Punkt im r&auml;umlichen Referenzsystem der Szene.
	 * @param pNormPos georeferenzierter Punkt
	 * @return Punkt im normierten Koordinatenraum der <tt>VsSimpleScene</tt>
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
	 * liefert den Skalierungsfaktor f&uuml;r die Normierung von Geo-Koordinaten innerhalb der <tt>VsSimpleScene</tt>.
     * <p>
	 * Bem.: F&uuml;r die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
	 * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.
	 * @return Normierungsfaktor
	 * @see VsSimpleScene#getOffset
	 */
	public double getScale() {
		return mScale;
	}

    /**
     * liefert das Seitenverh&auml;ltnis der y-Ausdehnung zur x-Audehnung des Gel&auml;ndemodells.
     * @return Seitenverh&auml;ltnis
     */
    public double getAspect() {
        return mAspect;
    }

	/**
	 * liefert den Translationsvektor f&uuml;r die Normierung von Geo-Koordinaten innerhalb der
	 * <tt>VsSimpleScene</tt>.<p>
	 * Bem.: F&auml;r die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
	 * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.
	 * @return Translation
	 * @see VsSimpleScene#getScale
	 */
	public T3dVector getOffset() {
		return mOffset;
	}

	/**
	 * liefert den minimalen z-Wert des Gel&auml;ndemodells <tt>this.getTerrain</tt> bezogen auf das System der
	 * normierten Koordinaten.
	 * @return nomierte z-Koordinate f&uuml;r die minimale Gel�ndeh�he der <tt>VsSimpleScene</tt>
	 */
	public double normZMin() {
		return mTerrain.minimalElevation() * mScale;
	}

	/**
	 * liefert den maximalen z-Wert des Gel&auml;ndemodells <tt>this.getTerrain</tt> bezogen auf das System der
	 * normierten Koordinaten.
	 * @return nomierte z-Koordinate f&uuml;r die maximale Gel�ndeh�he der <tt>VsSimpleScene</tt>
	 */
	public double normZMax() {
		return mTerrain.maximalElevation() * mScale;
	}

    /**
     * setzt die Hintergrundfarbe der POV-Ray-Szene. Voreinstellungsgem&auml;&szlig; ist ein schwarzer Hintergrund
     * gesetzt.
     * @param pColor Hintergrundfarbe
     */
    public void setBackgroundColor(T3dColor pColor) {
        mBackgroundColor = pColor;
    }

    /**
     * liefert die gesetzte Hintergrundfarbe.
     * @return Hintergrundfarbe
     */
    public T3dColor getBackgroundColor() {
        return mBackgroundColor;
    }
    
	/**
	 * steuert, ob die zur dem Gel&auml;ndemodell geh&ouml;rige Bounding-Box als Shape zur Szene hinzugef&uuml;gt wird.
	 * @param pDrawBBox <i>true</i>, um der Szene Bounding-Box-Shape hinzuzuf&uuml;gen
	 */
	public void drawBBoxShape(boolean pDrawBBox) {
		mDrawBBox = pDrawBBox;
	}

    protected boolean drawBBox() {
		return mDrawBBox;
	}

    /**
     * steuert, ob ein zum Gel&auml;ndemodell geh&ouml;riger Sockel generiert und zur Szene hinzugef&uuml;gt wird.<p>
     * Bem.: Dieser Modus wird (noch?) nicht von allen Visualisierungsumgebungen unterst&uuml;tzt.
     * @param pDrawPedestal <i>true</i>, um der Szene Gel&auml;ndemodell-Sockel hinzuzuf&uuml;gen
     */
    public void drawTerrainPedestal(boolean pDrawPedestal) {
        mDrawPedestal = pDrawPedestal;
    }

    protected boolean drawTerrainPedestal() {
		return mDrawPedestal;
	}

    /**
     * setzt die Farbe f&uuml;r den Gel&auml;ndemodell-Sockel. Voreinstellungsgem&auml;&szlig; ist die Farbe Grau (50 %)
     * gesetzt.
     * @param pColor Sockelfarbe
     */
    public void setPedestalColor(T3dColor pColor) {
        mPedestalColor = pColor;
    }

    /**
     * liefert die gesetzte Sockelfarbe.
     * @return Sockelfarbe
     */
    public T3dColor getPedestalColor() {
        return mPedestalColor;
    }
}
