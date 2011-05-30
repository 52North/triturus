package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;

/**
 * <tt>VgLineSegment</tt>-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden. x- und y-Werte
 * sind bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmLineSegment extends VgLineSegment
{
    private GmPoint mPStart = new GmPoint(0., 0., 0.);
    private GmPoint mPEnd = new GmPoint(0., 0., 0.);

    /** Konstruktor */
    public GmLineSegment(VgPoint pPStart, VgPoint pPEnd) {
        mPStart.set(pPStart);
        mPEnd.set(pPEnd);
    }

    /** setzt den Startpunkt des Liniensegments. */
    public void setStartPoint(VgPoint pPStart) {
    	this.assertSRS(pPStart);
        mPStart.set(pPStart);
    }
    /** liefert den Startpunkt des Liniensegments. */
    public VgPoint getStartPoint() {
        return mPStart;
    }

    /** setzt den Endpunkt des Liniensegments. */
    public void setEndPoint( VgPoint pPEnd ) {
    	this.assertSRS(pPEnd);
        mPEnd.set( pPEnd );
    }
    /** liefert den Endpunkt des Liniensegments. */
    public VgPoint getEndPoint() {
        return mPEnd;
    }

    /** liefert die Bounding-Box der Geometrie. */
    public VgEnvelope envelope() 
    {
        GmEnvelope mEnv = new GmEnvelope(mPStart);
        mEnv.letContainPoint(mPEnd);
        return mEnv;
    }

	/** 
	 * liefert das zugehörige "Footprint"-Liniensegment.<p>
	 * @return "Footprint" als <tt>GmLineSegment</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return new GmLineSegment(
			(VgPoint) this.getStartPoint().footprint(),
			(VgPoint) this.getEndPoint().footprint());
	}

    /** liefert den durch das Liniensegment definierten Richtungsvektor. */
    public VgPoint getDirection() {
        double dx = this.getEndPoint().getX() - this.getStartPoint().getX();
        double dy = this.getEndPoint().getY() - this.getStartPoint().getY();
        double dz = this.getEndPoint().getZ() - this.getStartPoint().getZ();
        return new GmPoint(dx, dy, dz);
    }
}