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

import org.n52.v3d.triturus.vgis.VgPolygon;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * <tt>VgPolygon</tt>-implementation. Object information will be kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system (SRS) that has been set for the
 * geometric object. z-values might be provided for the object's vertices.<br /><br />
 * <i>German:</i> <tt>VgPolygon</tt>-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden. x-
 * und y-Werte sind bezogen auf das eingestellte r�umliche Bezugssystem (SRS) anzugeben. Die Eckpunkte k&ouml;nnen mit
 * einer z-Koordinate versehen sein.<br />
 * Bem.: In der vorliegenden Implementierung erfolgt keine Pr&uuml;fung auf &Uuml;berlappung der Liniensegmente.
 * @author Benno Schmidt
 */
public class GmPolygon extends VgPolygon
{
    private ArrayList mVertices = null;
    
    public GmPolygon() {
    	mVertices = new ArrayList();
    }

    /**
     * adds a vertex point to the polygon (at the end of the vertex-list).<br />
     * Pre-condition: <tt>N = this.numberOfVertices()</tt>
     * Post-condition: <tt>this.numberOfVertices() = N + 1</tt>
     * @throws T3dException
     */
    public void addVertex(VgPoint pPnt) throws T3dException
    {
    	this.assertSRS(pPnt);
    	GmPoint lPnt = new GmPoint(pPnt);
    	mVertices.add(lPnt);
    }

    public VgPoint getVertex(int i) throws T3dException
    {
    	if (i < 0 || i >= this.numberOfVertices())
    	    throw new T3dException("Index out of bounds.");
    	// else:
    	return (GmPoint) mVertices.get( i );
    }

    public int numberOfVertices() {
    	return mVertices.size();
    }
    
   /**
    * returns the polygon's bounding-box.
    * @return <tt>GmEnvelope</tt>, or <i>null</i> for <tt>this.numberOfVertices() = 0</tt>.
    */
   public VgEnvelope envelope()
    {
    	if (this.numberOfVertices() > 0) {
    	    GmEnvelope mEnv = new GmEnvelope(this.getVertex(0));
            for (int i = 0; i < this.numberOfVertices(); i++)
                mEnv.letContainPoint(this.getVertex(i));
            return mEnv;
        } else
            return null;
    }
    
    /** 
	 * returns the object's footprint geometry (projection to the x-y-plane).
	 * @return &quot;Footprint&quot; as <tt>GmPolygon</tt>-object
  	 */
	public VgGeomObject footprint()
	{
		GmPolygon res = new GmPolygon();
		VgPoint v = null;
		for (int i = 0; i < this.numberOfVertices(); i++) {
			v = new GmPoint(this.getVertex(i));
			v.setZ(0.);
			this.addVertex(v);
		}	
		return res;
	}
}