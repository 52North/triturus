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

import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold line-strings (polylines) in 3-D space.
 * @author Benno Schmidt
 */
abstract public class VgLineString extends VgGeomObject1d 
{
   /**
    * returns the number of points that are part of the polyline (<i>vertices</i>).
    */
	abstract public int numberOfVertices();

	/**
	 * returns the i-th point (vertex) of the polyline structure.<br /><br />
     * <i>German:</i> Es ist stets die Bedingung 0 &lt;<= i &lt; <tt>this.numberOfVertices()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.
     * @param i Point index
     * @return Vertex object
	 */
	abstract public VgPoint getVertex(int i) throws T3dException;
	
	/**
	 * returns the polyline's length referring to the assigned coordinate reference system.
	 * @see VgGeomObject#getSRS
     * @return Distance value
	 */
    public double length()
	{
		double sum = 0.;
		for (int i = 0; i < this.numberOfVertices() - 1; i++)
			sum += this.getVertex(i + 1).distance( this.getVertex(i) );
		return sum;
	}

	public String toString() 
	{
		String str = "[";
		if (this.numberOfVertices() > 0) {
			for (int i = 0; i < this.numberOfVertices() - 1; i++) {
				str = str + this.getVertex(i).toString() + ", ";
			}
			str = str + this.getVertex(this.numberOfVertices() - 1).toString();
		}
		return str + "]";
	}
}
