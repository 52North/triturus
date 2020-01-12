/**
 * Copyright (C) 2007-2015 52North Initiative for Geospatial Open Source
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
 * license version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.vgis;

/**
 * Abstract base class for geometric objects in 3-D space.
 * <br/>
 * Note: Geo-objects (called &quot;features&quot; in ISO/OGC jargon) will be 
 * modelled by <tt>VgAttrFeature</tt> objects. Here, geo-objects consist of one
 * or more <i>attributes</i>. The object's geometry is one of those attributes.
 * <br/>
 * All given gemoetries refer to a <i>spatial reference system</i> 
 * (<b>SRS</b>). Today, in the scope of ISO/OGC specifications most often the 
 * term <i>coordinate reference system</i> (<b>CRS</b>) is used. However, in 
 * the scope of the Triturus framework the acronym SRS still is used.
 * 
 * @see org.n52.v3d.triturus.vgis.VgAttrFeature
 * @see org.n52.v3d.triturus.vgis.VgFeature
 * @author Benno Schmidt
 */
abstract public class VgGeomObject 
{
	private String mSRS = SRSNone;

	/**
	 * Spatial reference system (SRS) value, if no coordinate reference system 
	 * is assigned to the object.
	 */
	public static final String SRSNone = "none";

	/**
	 * CRS-constant for geographic coordinates, i.e. decimal longitude/latitude 
	 * values referring to the WGS84-geoid as coordinate reference system 
	 * (<tt>EPSG:4326</tt>).
	 * <br/>
	 * Note: In the scope of the Triturus framework, for geographic coordinates 
	 * usually the rule holds that x-coordinates refer to geographic longitude 
	 * whereas y-coordinates refer to geographic latitude values.
	 */
	public static final String SRSLatLonWgs84 = "EPSG:4326";

	/**
	 * @deprecated
	 * CRS-constant for geographic coordinates, i.e. decimal longitude/latitude 
	 * values referring to the Bessel-geoid as coordinate reference system.
	 */
	public static final String SRSLatLonBessel = "latLonBessel";

	/**
	 * @deprecated
	 * @see VgGeomObject#SRSLatLonWgs84
	 */
	public static final String SRSLatLon = "EPSG:4326";
	
	/**
	 * CRS-constant for Gauss-Kruger zone 2 system (Bessel ellipsoid).
	 * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
	 */
	public static final String SRSGkk2 = "EPSG:31466";
	
	/**
	 * CRS-constant for Gauss-Kruger zone 3 system (Bessel ellipsoid).
	 * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
	 */
	public static final String SRSGkk3 = "EPSG:31467";
	
	/**
	 * CRS-constant for Gauss-Kruger zone 4 system (Bessel ellipsoid).
	 * @see org.n52.v3d.triturus.survey.GaussKrugerTransformator
	 */
	public static final String SRSGkk4 = "EPSG:31468";
	
	/**
	 * CRS-constant for UTM ETRS89 Zone 32 North (WGS84-geoid).
	 */
	public static final String SRSUtmZ32N = "EPSG:25832";
	
	/**
	 * assigns the given coordinate reference system to the x- and 
	 * y-coordinates of a geometric object. The reference system shall be 
	 * given by an EPSG-code, e.g. <tt></tt>&quot;EPSG:31492&quot;</tt>.
	 *
	 * @param EPSGString EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt> or <tt>SRSNone</tt>
	 */
	public void setSRS(String EPSGString) {
		mSRS = EPSGString;
	}
	
	/** 
	 * returns the coordinate reference system (CRS) assigned to a geometric 
	 * object as EPSG-code. If not CRS is set, the method will return 
	 * <tt>SRSNone</tt> as result.
	 *
	 * @return EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt> or <tt>SRSNone</tt>
	 */
	public String getSRS() {
		return mSRS;
	}
	
	/**
	 * returns the information whether the coordinate reference system that has
	 * been assigned to the geometric object is metric (i.e., if x- and 
	 * y-values refer to meters as distance measure).
	 *
	 * @return <i>true</i> for metric coordinate reference systems, else <i>false</i>
	 */
	public boolean hasMetricSRS()
	{
	    if (
	    	mSRS.equalsIgnoreCase(VgGeomObject.SRSNone) ||
	        mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLon) ||
	        mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonBessel) ||
	        mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84))
	    {
	        return false;
	    }
	    return true;
	}
	
	/**
	 * returns the information whether the coordinate reference system that has
	 * been assigned to the geometric object is given in geographic coordinates 
	 * (i.e., longitute/latitude values).
	 *
	 * @return <i>true</i> for geographic coordinates, else <i>false</i>
	 */
	public boolean hasGeographicSRS()
	{
	    if (
	    	mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLon) ||
	        mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonBessel) ||
	        mSRS.equalsIgnoreCase(VgGeomObject.SRSLatLonWgs84))
	    {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * checks the compatibility of the coordinate reference systems referring 
	 * to the current geometric object and another given geometric object. If 
	 * the coordinate reference systems are incompatible, a <tt>T3dSRSException</tt>
	 * will be thrown.
	 * <br/>
	 * If no coordinate reference system has been assigned to the current 
	 * object <tt>this</tt>, this will be done according to the CRS-setting of 
	 * the object <tt>obj</tt>.
	 * <br/>
	 * Note: The method call might be useful when implementing methods that 
	 * lead to geometry manipulations.
	 *
	 * @param pObj Geometric object
	 * @throws T3dSRSException in the case of incompatible coordinate reference systems
	 */
	protected void assertSRS(VgGeomObject obj) throws T3dSRSException 
	{
		if (mSRS.equalsIgnoreCase(SRSNone)) {
			mSRS = obj.getSRS();
			return;
		}
		
		if (!mSRS.equalsIgnoreCase(obj.getSRS())) 
			throw new T3dSRSException("Incompatibility in spatial reference systems.");
	}
	
	/**
	 * translates deprecated or unusual EPSG-codes into equivalent codes.
	 *
	 * @param EPSGString EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt>
	 * @return EPSG-code, e.g. <tt>&quot;"EPSG:</tt>&lt;EPSG-number&gt;</tt>&quot;</tt>
	 */
	static public String mapDeprecatedSRSCodes(String EPSGString)
	{
		if (EPSGString.equalsIgnoreCase("EPSG:25832") || EPSGString.equalsIgnoreCase("EPSG:32632"))
		    return VgGeomObject.SRSUtmZ32N;
		if (EPSGString.equalsIgnoreCase("EPSG:31466") || EPSGString.equalsIgnoreCase("EPSG:31492"))
		    return VgGeomObject.SRSGkk2;
		if (EPSGString.equalsIgnoreCase("EPSG:31467") || EPSGString.equalsIgnoreCase("EPSG:31493"))
		    return VgGeomObject.SRSGkk3;
		if (EPSGString.equalsIgnoreCase("EPSG:31468") || EPSGString.equalsIgnoreCase("EPSG:31494"))
		    return VgGeomObject.SRSGkk4;
		return EPSGString;
	}
	
	/**
	 * returns the geometric object's bounding-box.
	 * <br/>
	 * Note: For some geometric objects the bounding-box might have infinite 
	 * extent. In this case the method will return <i>null</i> as result.
	 *
	 * @return Bounding-box, or <i>null</i>
	 */
	abstract public VgEnvelope envelope();
	
	/**
	 * returns the object's footprint geometry (projection to the x-y plane).
	 * <br/>
	 * Examples:
	 * <li>
	 *   <ul>
	 *     The projection of a line-string (see class <tt>VgLineString</tt>) in
	 *     3-D space will be a planar line-string inside the x-y plane.
	 *   </ul>
	 *   <ul>
	 *     The projection of a sphere will be a circle.
	 *   </ul>
	 * </li>
	 * <p>
	 * Note: For some of the geometries modelled inside the Triturus framework,
	 * the bounding-box will get an infinite extent. For these cases, the 
	 * method will return <i>null</i> as result.
	 *
	 * @return Footprint object (or <i>null</i>)
	 */
	abstract public VgGeomObject footprint();
	
	/**
	 * returns human-readable information about the geometric object.
	 */
	abstract public String toString();
}
