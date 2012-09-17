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
package org.n52.v3d.triturus.vgis;

/**
 * Class to hold planes in 3-D space.
 * @author Benno Schmidt
 */
abstract public class VgPlane extends VgGeomObject 
{
    /**
     * returns <i>null</i> as bounding-box information.<br /><br />
     * <i>German:</i> Insofern die Ebene nicht parallel zur xy-Ebene oder zur z-Achse ist, ist die
     * Bounding-Box unbegrenzt. Dieser Fall l&auml;sst sich durch Analyse des Normalenvektors abfangen. Die
     * <tt>envelope()</tt>-Methode liefert in jedem Fall den Wert <i>null</i>.
     * @return always <i>null</i>
	 * @see VgPlane#getNormal
	 */
	public VgEnvelope envelope() {
		return null;
	}

 	/**
     * returns the normal vector.<br /><br />
 	 * <i>German:</i> liefert einen zur Ebene geh&ouml;rigen normierten Normalenvektor (als Richtungsvektor!).
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Directional vector</i> as <tt>VgPoint</tt>
 	 */
	abstract public VgPoint getNormal();

 	/**
 	 * returns a point that lies on the plane.<br /><br />
     * <i>German:</i> liefert einen auf der Ebene liegenden Punkt (als Ortsvektor!).
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
