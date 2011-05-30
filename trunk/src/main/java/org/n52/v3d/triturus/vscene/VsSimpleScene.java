package org.n52.v3d.triturus.vscene;

import org.n52.v3d.triturus.vgis.*;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.t3dutil.T3dColor;

import java.lang.Math;

/**
 * Spezifikation für eine einfache Szene, die ein Gitter-basiertes Geländemodell, eine zugehörige Drape-Datei sowie
 * Ansichtspunkte und Lichtquellen enthält.
 * <p>
 * <tt>VsSimpleScene</tt>-Beschreibungen eignen sich insbesondere für einfache Szenen, in denen nur ein einziges
 * Geländemodell visualisiert wird. Aus der räumlichen Ausdehnung des darzustellenden Geländemodells lässt sich ein
 * <i>normierter Raumausschnitt</i> ableiten, der alle Visualisierungsobjekte enthält:
 * <ul>
 * <li>In der xy-Ebene ist dies eine Teilmenge der Bereichs -1 &lt;= x' &lt;= +1, -1 &lt;= y' &lt;= +1, wobei die
 * normierten Koordinaten mit einem Strich (') kenntlich gemacht sind. Für quadratische Geländemodelle wird dieser
 * Raumausschnitt voll eingenommen; für Geländemodelle, deren Ausdehnung in x- und y-Richtung unterschiedlich ist, wird
 * entweder für x' oder für y' (exklusive "Oder"-Verknüpfung) nur ein Teilbereich eingenommen. Der Mittelpunkt des
 * Geländemodells liegt in jedem Fall an der Position (x', y') = (0, 0).</li>
 * <li>Die Ausdehnung in z'-Richtung ergibt in der Art und Weise aus den Höhenwerten, als dass der vertikale
 * Höhenmaßstab und der horizontale Maßstab korrespondieren. Insbesondere wird hierbei kein (!) Überhöhungsfaktor
 * berücksichtigt.</li>
 * <p>
 * In <tt>VsSimpleScene</tt>-Szenen lassen sich Ansichtspunkte und Lichtquellen mit Hilfe der Methode <tt>denorm()</tt>
 * bezogen auf eine normierte Bounding-Box angeben. Ansichtspunkte und Lichtquellenparameter sind dabei <i>invariant
 * gegenüber der Überhöhung.</i>
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VsSimpleScene extends VsScene
{
	private VgElevationGrid mTerrain = null;
	private String mDrape = null;

	private double mScale; // Skalierung für Normierung von Geo-Koordinaten
	private T3dVector mOffset = new T3dVector(); // Translation für Normierung von Geo-Koordinaten
    private double mAspect; // Verhältnis y-Ausdehnung : x-Ausdehnung

    private T3dColor mBackgroundColor = new T3dColor(0.f,0.f,0.f);

	private boolean mDrawBBox = false;
    private boolean mDrawPedestal = false;
    private T3dColor mPedestalColor = new T3dColor(0.5f,0.5f,0.5f);

	/**
	 * setzt das Geländemodell, das als Relief ("Terrain") dargestellt werden soll.<p>
	 * Einer <tt>VsSimpleScene</tt> kann nur genau ein Geländemodell zugeordnet sein.<p>
	 * Bem.: Bei dem Geländemodell muss es sich um ein Gitter-basiertes Modell handeln; TINs z. B. werden von einer
     * <tt>VsSimpleScene</tt> nicht unterstützt. -> todo: bei Bedarf entsprechende <tt>VsScene</tt>-Spezialisierung bereitstellen.
	 * @param pTerrain Geländemodell
	 */
	public void setTerrain(VgElevationGrid pTerrain) {
		mTerrain = pTerrain;
		this.calculateNormTransformation();
	}

	/**
	 * liefert das Geländemodell, das als Relief ("Terrain") dargestellt werden soll.<p>
	 * @return Geländemodell
	 */
	public VgElevationGrid getTerrain() {
		return mTerrain;
	}

	/**
	 * setzt die Textur, die auf das Relief ("Terrain") projiziert werden soll.<p>
	 * Einer <tt>VsSimpleGeoScene</tt> kann nur genau eine Drape-Textur zugeordnet sein. Seitens der aufrufenden
	 * Anwendung ist dafür zu sorgen, dass diese Textur georeferenziert ist. Soll keine Drape-Textur verwendet
	 * werden, kann der Wert <i>null</i> als Parameter angegeben werden.<p>
	 * @param pImageFile Bilddatei mit vollständiger Pfadangabe oder <i>null</i>
	 */
	public void setDrape(String pImageFile) {
		mDrape = pImageFile;
	}

	/**
	 * liefert die Textur, die auf das Relief ("Terrain") projiziert werden soll.<p>
	 * @return Bilddatei mit vollständiger Pfadangabe oder <i>null</i>
	 */
	public String getDrape() {
		return mDrape;
	}

	/**
	 * liefert den zu einer im räumlichen Referenzsystem vorliegenden Position gehörigen Punkt im normierten
	 * Koordinatensystem der <tt>VsSimpleScene</tt>. Für die x'- und y'-Koordinate des Ergebnisses gilt stets
	 * -1 &lt;= x' &lt; +1, -1 &lt;= y' &lt; +1, insofern der Punkt innerhalb der Bounding-Box des Geländemodells
	 * liegt.<p>
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
	 * gehörigen Punkt im räumlichen Referenzsystem der Szene.<p>
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
	 * liefert den Skalierungsfaktor für die Normierung von Geo-Koordinaten innerhalb der <tt>VsSimpleScene</tt>.<p>
	 * Bem.: Für die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
	 * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.<p>
	 * @return Normierungsfaktor
	 * @see VsSimpleScene#getOffset
	 */
	public double getScale() {
		return mScale;
	}

    /**
     * liefert das Seitenverhältnis der y-Ausdehnung zur x-Audehnung des Geländemodells.<p>
     * @return Seitenverhältnis
     */
    public double getAspect() {
        return mAspect;
    }

	/**
	 * liefert den Translationsvektor für die Normierung von Geo-Koordinaten innerhalb der
	 * <tt>VsSimpleScene</tt>.<p>
	 * Bem.: Für die Normierung wird erst die Skalierung <tt>this.getScale()</tt>, dann die Translation
	 * <tt>this.getOffset()</tt> auf die Geo-Koordinaten angewendet.<p>
	 * @return Translation
	 * @see VsSimpleScene#getScale
	 */
	public T3dVector getOffset() {
		return mOffset;
	}

	/**
	 * liefert den minimalen z-Wert des Geländemodells <tt>this.getTerrain</tt> bezogen auf das System der
	 * normierten Koordinaten.<p>
	 * @return nomierte z-Koordinate für die minimale Geländehöhe der <tt>VsSimpleScene</tt>
	 */
	public double normZMin() {
		return mTerrain.minimalElevation() * mScale;
	}

	/**
	 * liefert den maximalen z-Wert des Geländemodells <tt>this.getTerrain</tt> bezogen auf das System der
	 * normierten Koordinaten.<p>
	 * @return nomierte z-Koordinate für die maximale Geländehöhe der <tt>VsSimpleScene</tt>
	 */
	public double normZMax() {
		return mTerrain.maximalElevation() * mScale;
	}

    /**
     * setzt die Hintergrundfarbe der POV-Ray-Szene. Voreinstellungsgemäß ist ein schwarzer Hintergrund gesetzt.<p>
     * @param pColor Hintergrundfarbe
     */
    public void setBackgroundColor(T3dColor pColor) {
        mBackgroundColor = pColor;
    }

    /**
     * liefert die gesetzte Hintergrundfarbe.<p>
     * @return Hintergrundfarbe
     */
    public T3dColor getBackgroundColor() {
        return mBackgroundColor;
    }
    
	/**
	 * steuert, ob die zur dem Geländemodell gehörige Bounding-Box als Shape zur Szene hinzugefügt wird.<p>
	 * @param pDrawBBox <i>true</i>, um der Szene Bounding-Box-Shape hinzuzufügen
	 */
	public void drawBBoxShape(boolean pDrawBBox) {
		mDrawBBox = pDrawBBox;
	}

    protected boolean drawBBox() {
		return mDrawBBox;
	}

    /**
     * steuert, ob ein zum Geländemodell gehöriger Sockel generiert und zur Szene hinzugefügt wird.<p>
     * Bem.: Dieser Modus wird (noch?) nicht von allen Visualisierungsumgebungen unterstützt.<p>
     * @param pDrawPedestal <i>true</i>, um der Szene Geländemodell-Sockel hinzuzufügen
     */
    public void drawTerrainPedestal(boolean pDrawPedestal) {
        mDrawPedestal = pDrawPedestal;
    }

    protected boolean drawTerrainPedestal() {
		return mDrawPedestal;
	}

    /**
     * setzt die Farbe für den Geländemodell-Sockel. Voreinstellungsgemäß ist die Farbe Grau (50 %) gesetzt.<p>
     * @param pColor Sockelfarbe
     */
    public void setPedestalColor(T3dColor pColor) {
        mPedestalColor = pColor;
    }

    /**
     * liefert die gesetzte Sockelfarbe.<p>
     * @return Sockelfarbe
     */
    public T3dColor getPedestalColor() {
        return mPedestalColor;
    }
}
