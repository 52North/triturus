package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.*;

/**
 * Implementierende Klasse zur Verwaltung (beliebig im Raum orientierter) Dreiecke.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmTriangle extends VgTriangle 
{
    GmPoint mP1, mP2, mP3;
    private GmEnvelope mEnv = null;
    private boolean mEnvIsCalculated = false;

    /* 
     * Konstruktor.<p>
     */
    public GmTriangle(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3) 
    {
        this.setCornerPoints(pCorner1, pCorner2, pCorner3); 
    }

    /** 
     * setzt die Eckpunkte des Dreiecks.<p> 
     */
    public void setCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3) 
    {
        mP1 = new GmPoint(pCorner1);
        this.assertSRS(pCorner2); 
        mP2 = new GmPoint(pCorner2);
        this.assertSRS(pCorner3); 
        mP3 = new GmPoint(pCorner3);
        mEnvIsCalculated = false;
    }
    
	/** 
	 * @deprecated
	 * liefert die Eckpunkte des Dreiecks.<p>
	 * <i>TODO: Methode funktioniert noch nicht richtig...</i><p>
	 */
	public void getCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3) {
		pCorner1 = mP1;
		pCorner2 = mP2;
		pCorner3 = mP3;
	}

    /** 
     * liefert die Eckpunkte des Dreiecks.<p>
     * @return dreielementiges Array mit <tt>GmPoint</tt>-Objekten
     */
    public VgPoint[] getCornerPoints() {
    	GmPoint[] res = new GmPoint[3];
        res[0] = mP1; res[1] = mP2; res[2] = mP3;
        return res;
    }

    /** 
     * liefert die Bounding-Box der Geometrie.<p>
     */
    public VgEnvelope envelope() 
    {
        if (!mEnvIsCalculated) {
             mEnv = new GmEnvelope( 
                 mP1.getX(), mP1.getX(),
                 mP1.getY(), mP1.getY(),
                 mP1.getZ(), mP1.getZ() );
             mEnv.letContainPoint(mP2);
             mEnv.letContainPoint(mP3);
        }
        return mEnv;
    }
    
	/** 
	 * liefert das zugehörige "Footprint"-Dreieck.<p>
	 * @return "Footprint" als <tt>GmTriangle</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return new GmTriangle(
			(VgPoint) mP1.footprint(), 
			(VgPoint) mP2.footprint(), 
			(VgPoint) mP3.footprint());
	}
}