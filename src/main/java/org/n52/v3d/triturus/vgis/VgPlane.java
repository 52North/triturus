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
package org.n52.v3d.triturus.vgis;

/**
 * Class to hold planes in 3-D space.
 *
 * @author Benno Schmidt
 */
abstract public class VgPlane extends VgGeomObject 
{
    /**
     * returns <i>null</i> as bounding-box information.<br /><br />
     * <i>German:</i> Insofern die Ebene nicht parallel zur xy-Ebene oder zur z-Achse ist, ist die
     * Bounding-Box unbegrenzt. Dieser Fall l&auml;sst sich durch Analyse des Normalenvektors abfangen. Die
     * <tt>envelope()</tt>-Methode liefert in jedem Fall den Wert <i>null</i>.
     *
     * @return always <i>null</i>
	 * @see VgPlane#getNormal
	 */
	public VgEnvelope envelope() {
		return null;
	}

 	/**
     * returns the normal vector.<br /><br />
 	 * <i>German:</i> liefert einen zur Ebene geh&ouml;rigen normierten Normalenvektor (als Richtungsvektor!).
      *
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Directional vector</i> as <tt>VgPoint</tt>
 	 */
	abstract public VgPoint getNormal();

 	/**
 	 * returns a point that lies on the plane.<br /><br />
     * <i>German:</i> liefert einen auf der Ebene liegenden Punkt (als Ortsvektor!).
      *
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Position vector</i> as <tt>VgPoint</tt>
 	 */
	abstract public VgPoint getAnchor();

    /**
     * returns <i>null</i> as footprint geometry.<br /><br />
	 * <i>German:</i> liefert das Objekt, das sich durch Projektion der Ebene auf die xy-Ebene ergibt.<p>
	 * Insofern die Ebene nicht parallel zur xy-Ebene oder zur z-Achse ist, ist die &quot;Footprint&quot;-Geometrie
     * unbegrenzt. Dieser Fall l&auml;sst sich durch Analyse des Normalenvektors abfangen. Die
     * <tt>footprint()</tt>-Methode liefert in jedem Fall den Wert <i>null</i>.
     *
     * @see VgPlane#getNormal
	 * @return always <i>null</i>
  	 */
	public VgGeomObject footprint() {
		return null;
	}
	
	public String toString() {
		VgPoint p0 = this.getAnchor();
		VgPoint p1 = this.getNormal();
		return "[" + p0.toString() + ", " + p1.toString() + "]";
	}
}
