package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgFeature;
import org.n52.v3d.triturus.vgis.VgTIN;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.util.ArrayList;

/** 
 * Klasse zur Verwaltung einfacher "2D"-TINs für Geländemodelle.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmSimpleTINFeature extends VgFeature
{
    private VgTIN mGeom;
    private String mTheme = "Elevations"; 

    /** 
     * Konstruktor. Die Punkte und Dreiecke des TINs lassen sich über das Objekt
     * <tt>((GmSimpleTINGeometry) this.getGeometry())</tt> setzen.<p>
     * @see GmSimpleTINGeometry
     */
    public GmSimpleTINFeature() 
    {
       this.setName("unnamed TIN");
       mGeom = new GmSimpleTINGeometry(0, 0);
    }

    /** 
     * liefert Metainformation über Thematik.<p>
     * @return Liste von Strings
     */
    public ArrayList getThematicAttributes() {
       ArrayList lList = new ArrayList();
       lList.add( mTheme );
       return lList;
    }
    
    /** 
     * liefert die Objekt-Geometrie.<p>
     * @return <tt>GmSimpleTINGeometry</tt>-Objekt
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

	/**
	 * setzt die Objekt-Geometrie.<p>
	 * @param pGeom TIN-Geometrie
	 */
	public void setGeometry(VgTIN pGeom) {
		mGeom = pGeom;
	}

	/**
	 * Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das TIN ein atomares Geoobjekt ist, liefert diese Methode
     * stets <i>false</i> als Resultat.<p>
     * @return false
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle.<p>
     * @param i (hier stets 0)
     * @return TIN selbst
     * @throws org.n52.v3d.triturus.core.T3dException
     */ 
    public VgFeature getFeature( int i ) throws T3dException
    {
        if (i != 0) 
        	throw new T3dException("Index out of bounds."); 
        // else:
        return this;
    }

    public int numberOfSubFeatures() {
        return 1;
    }

    /** liefert die Bounding-Box des TINs. */
    public VgEnvelope envelope()
    {
        if (mGeom != null)
            return mGeom.envelope();
        else
            return null;
    }

    /** 
     * liefert den kleinsten (niedrigsten) im TIN enthaltenen Höhenwert.<p>
     * @throws T3dException
     */
    public double minimalElevation() throws T3dException
    {
        /*try {
            return mGeom.minimalElevation();
        } 
        catch (Exception e) {
            throw new T3dException("Could not access TIN bounds.");
        } */
        throw new T3dNotYetImplException();
    }

    /** 
     * liefert den größten (höchsten) im TIN enthaltenen Höhenwert.<p>
     * @throws T3dException
     */
    public double maximalElevation() throws T3dException
    {
        /*try {
            return mGeom.maximalElevation();
        } 
        catch (T3dException e) {
            throw new T3dException("Could not access TIN bounds.");
        } */
        throw new T3dNotYetImplException();
    }

    /** 
     * deaktiviert "lazy evaluation" der Bounding-Box.<p>
     * Eine explizite Deaktivierung unmittelbar vor TIN-Editierungen (<tt>this.setPoint()</tt>-Aufrufen) 
     * kann aus Performanz-Gründen notwendig werden.<p>
     */
    public void setBoundsInvalid()
    {
        /*if (mGeom != null)
            mGeom.setBoundsInvalid();
        */
        throw new T3dNotYetImplException();
    }

	/**
	 * liefert die zugehörige "Footprint"-Geometrie.<p>
	 * @return "Footprint" als <tt>GmSimpleTINGeometry</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return mGeom.footprint();
	}

    public String toString() {
        String strGeom = "<empty geometry>";
        if (mGeom != null)
            strGeom = mGeom.toString(); 
        return "[" + mTheme + ", \"" + strGeom + "\"]";
    }
}
