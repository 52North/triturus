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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgFeature;
import org.n52.v3d.triturus.vgis.VgTIN;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.util.ArrayList;

/**
 * Class to manage simple &quot;2-D&quot; TINs for terrain models.<br /><br />
 * <i>German:</i> Klasse zur Verwaltung einfacher "2D"-TINs f&uuml;r Gel&auml;ndemodelle.
 * @author Benno Schmidt
 */
public class GmSimpleTINFeature extends VgFeature
{
    private VgTIN mGeom;
    private String mTheme = "Elevations"; 

    /**
     * Constructor.<br /><br />
     * <i>German:</i> Konstruktor. Die Punkte und Dreiecke des TINs lassen sich &uuml;ber das Objekt
     * <tt>((GmSimpleTINGeometry) this.getGeometry())</tt> setzen.
     * @see GmSimpleTINGeometry
     */
    public GmSimpleTINFeature() 
    {
       this.setName("unnamed TIN");
       mGeom = new GmSimpleTINGeometry(0, 0);
    }

    /** 
     * provides thematic meta-information.
     * @return List of strings
     */
    public ArrayList getThematicAttributes() {
       ArrayList lList = new ArrayList();
       lList.add( mTheme );
       return lList;
    }
    
    /** 
     * returns the object geometry.
     * @return <tt>GmSimpleTINGeometry</tt>-object
     * @see org.n52.v3d.triturus.gisimplm.GmSimpleTINGeometry
     */
    public VgGeomObject getGeometry() {
        return mGeom;
    }

	/**
	 * sets the object geometry.
	 * @param pGeom TIN-geometry
	 */
	public void setGeometry(VgTIN pGeom) {
		mGeom = pGeom;
	}

    /**
     * always returns <i>false</i>, since a <tt>GmSimpleTINFeature</tt> consists of one and only geo-object.
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * always returns this <tt>GmSimpleTINFeature</tt> itself.
     * @param i (here always 0)
     * @return TIN itself
     * @throws T3dException
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

    /**
     * returns the TIN's bounding-box.
     */
    public VgEnvelope envelope()
    {
        if (mGeom != null)
            return mGeom.envelope();
        else
            return null;
    }

    /**
     * returns the TIN's minimum value (lowest elevation).
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
     * returns the TIN's minimum value (highest elevation).
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
     * deactivates lazy evaluation mode.<br /><br />
     * <i>German:</i> deaktiviert &quot;lazy evaluation&quot; der Ausdehnung in z-Richtung.<br />
     * Eine explizite Deaktivierung unmittelbar vor TIN-Editierungen (<tt>this.setPoint()</tt>-Aufrufe) kann aus
     * Performanz-Gr&uuml;nden notwendig werden.
     */
    public void setBoundsInvalid()
    {
        /*if (mGeom != null)
            mGeom.setBoundsInvalid();
        */
        throw new T3dNotYetImplException();
    }

    /**
     * returns the corresponding footprint geometry.
     * @return Footprint as <tt>GmSimpleTINGeometry</tt>-object
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
