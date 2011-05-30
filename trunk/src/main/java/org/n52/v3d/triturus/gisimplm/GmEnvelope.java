package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPolygon;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.t3dutil.T3dVector;

/**
 * Bounding-Box-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden.
 * <p>
 * x- und y-Werte sind dabei bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben. Die Beziehungen
 * <tt>env.getMinX() &lt;= env.getMaxX()</tt>, <tt>env.getMinY() &lt;= env.getMaxY()</tt> und
 * <tt>env.getMinZ() &lt;= env.getMaxZ()</tt> werden stets eingehalten.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmEnvelope extends VgEnvelope
{
    private double mXMin, mXMax, mYMin, mYMax, mZMin, mZMax;

    /** 
     * Konstruktor.<p>
     * @param pXMin minimaler x-Wert
     * @param pXMax maximaler x-Wert
     * @param pYMin minimaler y-Wert
     * @param pYMax maximaler y-Wert
     * @param pZMin minimaler z-Wert
     * @param pZMax maximaler z-Wert
     */
    public GmEnvelope(double pXMin, double pXMax, double pYMin, double pYMax, double pZMin, double pZMax) 
    {
        mXMin = pXMin; mXMax = pXMax;
        mYMin = pYMin; mYMax = pYMax;
        mZMin = pZMin; mZMax = pZMax;

        this.assureOrdering();
    }

    /** 
     * Konstruktor. Die Geometrie der Bounding-Box <tt>pEnv</tt> wird dabei übernommen.<p>
     * @param pEnv Envelope mit zu übernehmender Geometrie
     */
    public GmEnvelope(VgEnvelope pEnv)
    {
        mXMin = pEnv.getXMin(); mXMax = pEnv.getXMax();
        mYMin = pEnv.getYMin(); mYMax = pEnv.getYMax();
        mZMin = pEnv.getZMin(); mZMax = pEnv.getZMax();

        this.assureOrdering();
    }

    /**
     * Konstruktor. Die Koordinaten des angegebenen Punktes werden dabei übernommen, es entsteht eine Bounding-Box
     * mit der Fläche 0.<p>
     * @param pPnt Punktgeometrie
     */
    public GmEnvelope(VgPoint pPnt)
    {
    	this.setSRS(pPnt.getSRS());

        mXMin = pPnt.getX(); mXMax = mXMin;
        mYMin = pPnt.getY(); mYMax = mYMin;
        mZMin = pPnt.getZ(); mZMax = mZMin;
    }

    /**
     * Konstruktor. Die Geometrie der bounding-Box wird durch zwei Eckpunkte spezifiziert.<p>
     * @param pPnt1 erster Eckpunkt
     * @param pPnt2 zweiter Eckpunkt
     */
    public GmEnvelope(VgPoint pPnt1, VgPoint pPnt2)
    {
    	this.setSRS(pPnt1.getSRS());
    	this.assertSRS(pPnt2);

        mXMin = pPnt1.getX(); mXMax = mXMin;
        mYMin = pPnt1.getY(); mYMax = mYMin;
        mZMin = pPnt1.getZ(); mZMax = mZMin;
        
        this.letContainPoint(pPnt2);
    }

    /**
     * Konstruktor. Die Bounding-Box wird anhand des angegebenen Mittelpunktes und der Breiten- und Tiefenangabe
     * (Ausdehnungen in x- und y-Richtung) konstruiert.<p>
     * @param pCenter Mittelpunkt
     * @param pWidth Breitenangabe (x-Richtung)
     * @param pDepth Tiefenangabe (y-Richtung)
     */
    public GmEnvelope(VgPoint pCenter, double pWidth, double pDepth)
    {
        mXMin = pCenter.getX() - pWidth / 2.; mXMax = pCenter.getX() + pWidth / 2.;
        mYMin = pCenter.getY() - pDepth / 2.; mYMax = pCenter.getY() + pDepth / 2.;
        mZMin = pCenter.getZ(); mZMax = pCenter.getZ();

        // this.assureOrdering(); hier nicht nötig
    }

    /**
     * Konstruktor. Dieser Konstruktor initialisiert die Bounding-Box anhand der angegebenen Komma-separierten
     * Koordinatenliste. Sind keine z-Werte angegeben, wird z = 0 gesetzt.<p>
     * Beispiele: <tt>&quot;3500000,5800000,3600000,5900000&quot;, &quot;3500000,5800000,50.5,3600000,5900000,100&quot;</tt><p>
     * Die Methode wirft eine <tt>T3dException</tt>, falls der String kein interpretierbaren Koordinatenangaben
     * enthält.<p>
     * @param pCommaSeparatedList Liste mit 4 oder 6 Koordinaten
     */
    public GmEnvelope(String pCommaSeparatedList)
    {
        String[] coords = pCommaSeparatedList.split(",");
        if (coords.length != 4 && coords.length != 6)
            throw new T3dException("Cannot parse geo-coordinates from \"" + pCommaSeparatedList + "\".");

        if (coords.length == 4) {
            mXMin = Double.parseDouble(coords[0]); mXMax = Double.parseDouble(coords[2]);
            mYMin = Double.parseDouble(coords[1]); mYMax = Double.parseDouble(coords[3]);
            mZMin = 0.; mZMax = 0.;
        }
        else { // coords.length = 6
            mXMin = Double.parseDouble(coords[0]); mXMax = Double.parseDouble(coords[3]);
            mYMin = Double.parseDouble(coords[1]); mYMax = Double.parseDouble(coords[4]);
            mZMin = Double.parseDouble(coords[2]); mZMax = Double.parseDouble(coords[5]);
        }

        this.assureOrdering();
    }

    /**
     * setzt den minimalen x-Wert der Bounding-Box. Der x-Wert ist bezogen auf das eingestellte räumliche Bezugssystem
     * (SRS) anzugeben.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getXMin() &lt;= env.getXMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pX x-Wert
     */
    public void setXMin(double pX) {
        mXMin = pX;
    }

    /**
     * liefert den minimalen x-Wert der Bounding-Box. Der Wert bezieht sich auf das eingestellte räumliches
     * Bezugssystem (SRS).<p>
     * @return x-Wert
     */
    public double getXMin() {
        return mXMin;
    }

    /** 
     * setzt den maximalen x-Wert der Bounding-Box. Der x-Wert ist bezogen auf das eingestellte räumliche Bezugssystem
     * (SRS) anzugeben.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getXMin() &lt;= env.getXMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pX x-Wert
     */
    public void setXMax(double pX) {
        mXMax = pX;
    }

    /**
     * liefert den maximalen x-Wert der Bounding-Box. Der Wert bezieht sich auf das eingestellte räumliches
     * Bezugssystem (SRS).<p>
     * @return x-Wert
     */
    public double getXMax() {
        return mXMax;
    }

    /** 
     * setzt den minimalen y-Wert der Bounding-Box. Der y-Wert ist bezogen auf das eingestellte räumliche Bezugssystem
     * (SRS) anzugeben.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getYMin() &lt;= env.getYMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pY y-Wert
     */
    public void setYMin(double pY) {
        mYMin = pY;
    }

    /**
     * liefert den minimalen y-Wert der Bounding-Box. Der Wert bezieht sich auf das eingestellte räumliches
     * Bezugssystem (SRS).<p>
     * @return y-Wert
     */
    public double getYMin() {
        return mYMin;
    }

    /** 
     * setzt den maximalen y-Wert der Bounding-Box. Der y-Wert ist bezogen auf das eingestellte räumliche Bezugssystem
     * (SRS) anzugeben.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getYMin() &lt;= env.getYMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pY y-Wert
     */
    public void setYMax(double pY) {
        mYMax = pY;
    }

    /**
     * liefert den maximalen y-Wert der Bounding-Box. Der Wert bezieht sich auf das eingestellte räumliche Bezugssystem
     * (SRS).<p>
     * @return y-Wert
     */
    public double getYMax() {
        return mYMax;
    }

    /**
     * setzt den minimalen z-Wert der Bounding-Box.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getZMin() &lt;= env.getZMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pZ z-Wert
     */
    public void setZMin(double pZ) {
       mZMin = pZ;
    }

    /**
     * liefert den minimalen z-Wert der Bounding-Box.<p>
     * @return z-Wert
     */
    public double getZMin() {
        return mZMin;
    }

    /**
     * setzt den maximalen z-Wert der Bounding-Box.<p>
     * Bem.: Nach Aufruf dieser Methode ist die Einhaltung der Bedingung <i>env.getZMin() &lt;= env.getZMax()</i> nicht
     * zugesichert. Falls nötig, ist die Methode <tt>assureOrdering()</tt> explizit aufzurufen!<p>
     * @param pZ z-Wert
      */
    public void setZMax(double pZ) {
        mZMax = pZ;
    }

    /**
     * liefert den maximalen z-Wert der Bounding-Box.<p>
     * @return z-Wert
     */
    public double getZMax() {
        return mZMax;
    }

    /**
     * liefert den unteren linken vorderen Eckpunkt der Bounding-Box.<p>
     * @return <tt>GmPoint</tt> mit minimalem x-, y- und z-Wert
     */
    public VgPoint getLowerLeftFrontCorner() {
        VgPoint ret = new GmPoint(mXMin, mYMin, mZMin);
        ret.setSRS(this.getSRS());
        return ret;
    }

    /**
     * liefert den oberen rechten hinteren Eckpunkt der Bounding-Box.<p>
     * @return <tt>GmPoint</tt> mit maximalem x-, y- und z-Wert
     */
    public VgPoint getUpperRightBackCorner() {
        VgPoint ret = new GmPoint(mXMax, mYMax, mZMax);
        ret.setSRS(this.getSRS());
        return ret;
    }

	/**
     * liefert den Mittelpunkt der Bounding-Box.<p>
     * @return Mittelpunkt
     */
	public VgPoint getCenterPoint() {
		return new GmPoint(0.5 * (mXMin + mXMax), 0.5 * (mYMin + mYMax), 0.5 * (mZMin + mZMax));
	}

    /**
     * setzt den Mittelpunkt der Bounding-Box.<p>
     * @param pCenter neuer Mittelpunkt
     */
    public void setCenterPoint(VgPoint pCenter) {
        double mx = (this.getXMax() + this.getXMin()) / 2.;
        double my = (this.getYMax() + this.getYMin()) / 2.;
        double mz = (this.getZMax() + this.getZMin()) / 2.;
        GmPoint delta = new GmPoint(pCenter.getX() - mx, pCenter.getY() - my, pCenter.getZ() - mz);
        this.translate(delta);
    }

    /**
     * liefert eine innerhalb der xy-Ebene um den Ursprung gedrehte Bounding-Box.<p>
     * @param pAzimuth Drehwinkel im Bogenmaß (im Uhrzeigersinn)
     * @return gedrehte Bounding-Box als Polygon
     */
    public VgPolygon rotateXY(double pAzimuth) {
        double cx = this.getCenterPoint().getX();
        double cy = this.getCenterPoint().getY();
        VgPolygon ret = new GmPolygon();
        T3dVector p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy, 0.);
        T3dVector p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy, 0.);
        T3dVector p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy, 0.);
        T3dVector p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy, 0.);
        T3dVector q1 = p1.rotateXY(pAzimuth);
        T3dVector q2 = p2.rotateXY(pAzimuth);
        T3dVector q3 = p3.rotateXY(pAzimuth);
        T3dVector q4 = p4.rotateXY(pAzimuth);
        ((GmPolygon) ret).addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
        return ret;
    }

    /**
     * liefert eine gegenüber der xy-Ebene um den Ursprung gedrehte Bounding-Box.<p>
     * TODO: Methode ist noch nicht getestet
     * @param pInclination Drehwinkel im Bogenmaß
     * @return gedrehte Bounding-Box als Polygon
     */
    public VgPolygon rotateZ(double pInclination) {
        double cx = this.getCenterPoint().getX();
        double cy = this.getCenterPoint().getY();
        VgPolygon ret = new GmPolygon();
        T3dVector p1 = new T3dVector(this.getXMin() - cx, this.getYMin() - cy, 0.);
        T3dVector p2 = new T3dVector(this.getXMax() - cx, this.getYMin() - cy, 0.);
        T3dVector p3 = new T3dVector(this.getXMax() - cx, this.getYMax() - cy, 0.);
        T3dVector p4 = new T3dVector(this.getXMin() - cx, this.getYMax() - cy, 0.);
        T3dVector q1 = p1.rotateZ(pInclination);
        T3dVector q2 = p2.rotateZ(pInclination);
        T3dVector q3 = p3.rotateZ(pInclination);
        T3dVector q4 = p4.rotateZ(pInclination);
        ((GmPolygon) ret).addVertex(new GmPoint(q1.getX(), q1.getY(), q1.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q2.getX(), q2.getY(), q2.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q3.getX(), q3.getY(), q3.getZ()));
        ((GmPolygon) ret).addVertex(new GmPoint(q4.getX(), q4.getY(), q4.getZ()));
        return ret;
    }

    /**
     * erweitert die räumliche Ausdehnung der Bounding-Box so, dass sie die angegebene Bounding-Box umfasst
     * (Vereinigungsoperator).<p>
     */
    public void letContainEnvelope(VgEnvelope pEnv)
    {
    	this.assertSRS(pEnv);
    	this.letContainPoint(new GmPoint(pEnv.getXMin(), pEnv.getYMin(), pEnv.getZMin()));
    	this.letContainPoint(new GmPoint(pEnv.getXMax(), pEnv.getYMax(), pEnv.getZMax()));
    }

    public VgEnvelope envelope() {
        return this;
    }

	/** 
	 * liefert das zugehörige "Footprint"-Objekt.<p>
	 * @return "Footprint" als <tt>GmEnvelope</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
        GmPolygon res = new GmPolygon();
		res.addVertex(new GmPoint(mXMin, mYMin, 0.));
        res.addVertex(new GmPoint(mXMax, mYMin, 0.));
        res.addVertex(new GmPoint(mXMax, mYMax, 0.));
        res.addVertex(new GmPoint(mXMin, mYMax, 0.));
		return res;
	}

    /**
     * Veranlassung, dass die Bedingungen <i>this.getXMin() &lt;= this.getXMax()</i>,
     * <i>this.getYMin() &lt;= this.getYMax()</i> und <i>this.getZMin() &lt;= this.getZMax()</i> eingehalten werden.<p>
     */
    public void assureOrdering() {
        this.assureOrderingX();
        this.assureOrderingY();
        this.assureOrderingZ();
    }

    /**
     * Veranlassung, dass die Bedingung <i>this.getXMin() &lt;= this.getXMax()</i> eingehalten wird.<p>
     */
    public void assureOrderingX() {
        if (mXMin > mXMax) { double hlp = mXMin; mXMin = mXMax; mXMax = hlp; }
    }

    /**
     * Veranlassung, dass die Bedingung <i>this.getYMin() &lt;= this.getYMax()</i> eingehalten wird.<p>
     */
    public void assureOrderingY() {
        if (mYMin > mYMax) { double hlp = mYMin; mYMin = mYMax; mYMax = hlp; }
    }

    /**
     * Veranlassung, dass die Bedingung <i>this.getZMin() &lt;= this.getZMax()</i> eingehalten wird.<p>
     */
    public void assureOrderingZ() {
        if (mZMin > mZMax) { double hlp = mZMin; mZMin = mZMax; mZMax = hlp; }
    }
}
