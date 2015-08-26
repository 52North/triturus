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
