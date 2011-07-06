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

import java.util.ArrayList;
import org.n52.v3d.triturus.t3dutil.GKTransform;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;

/**
 * <tt>VgLineString</tt>-implementation. Object information will be kept in main memory.<br />
 * x- and y-values have to be given with respect to the spatial reference system (SRS) that has been set for the
 * geometric object. z-values might be provided for the object's vertices.
 * @author Benno Schmidt
 */
public class GmLineString extends VgLineString
{
    private ArrayList mVertices = null;
    
    public GmLineString() {
    	mVertices = new ArrayList();
    }

    /**
     * Constructor.<br /><br />
     * <i>German:</i> Konstruktor. Dieser Konstruktor initialisiert den Linienzug anhand der angegebenen
     * Komma-separierten Koordinatenliste. In der Liste,. die mindestens zwei St&uuml;tzpunkt-Angaben enthalten muss,
     * sind stets z-Werte anzugeben.<br />
     * Beispiel: <tt>&quot;3500010,5800010,50.5,3600010,5800010,100&quot;</tt><p>
     * Die Methode wirft eine <tt>T3dException</tt>, falls der String kein interpretierbaren Koordinatenangaben
     * enth&auml;lt.
     * @param pCommaSeparatedList List containing 3 x N coordinates (N > 1)
     */
    public GmLineString(String pCommaSeparatedList)
    {
        String[] coords = pCommaSeparatedList.split(",");
        if (coords.length % 3 != 0)
            throw new T3dException("Cannot parse line string coordinates from \"" + pCommaSeparatedList + "\".");
        int N = coords.length / 3;
        if (N < 2)
            throw new T3dException("Invalid line string specification: \"" + pCommaSeparatedList + "\".");

        mVertices = new ArrayList();

        double x = 0., y = 0., z = 0.;
        VgPoint pt = null;
        for (int i = 0; i < N; i++) {
            x = Double.parseDouble(coords[3 * i]);
            y = Double.parseDouble(coords[3 * i + 1]);
            z = Double.parseDouble(coords[3 * i + 2]);
            pt = new GmPoint(x, y, z);
            this.addVertex(pt);
        }
    }

    /**
     * adds a vertex point to the polyline (at the end of the vertex-list).<br />
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
    	return (GmPoint) mVertices.get(i);
    }

    public int numberOfVertices() {
    	return mVertices.size();
    }
    
    /** 
     * returns the geometric object's bounding-box.
     * @return <tt>GmEnvelope</tt>, or <i>null</i> for <tt>this.numberOfVertices() = 0</tt>.
     */
    public VgEnvelope envelope()
    {
    	if (this.numberOfVertices() > 0) {
    	    GmEnvelope mEnv = new GmEnvelope( this.getVertex(0) );
            for (int i = 0; i < this.numberOfVertices(); i++)
                mEnv.letContainPoint( this.getVertex(i) );
            return mEnv;
        } else
            return null;
    }
    
    /** 
	 * returns the object's footprint geometry (projection to the x-y-plane).
	 * @return &quot;Footprint&quot; as <tt>GmLineString</tt>-object
  	 */
	public VgGeomObject footprint()
	{
		GmLineString res = new GmLineString();
		VgPoint v = null;
		for (int i = 0; i < this.numberOfVertices(); i++) {
			v = new GmPoint( this.getVertex(i) );
			v.setZ(0.);
			this.addVertex(v);
		}	
		return res;
	}
	
	/**
     * TODO: documentation.<br /><br />
	 * <i>German:</i> <b>vor�bergehende Implementierung -> Profillinien sind in anderen GK-Meridianstreifen
	 * zu transformieren. -> sauber in Rahmenwerk einbauen -> TODO Benno
	 */
	public GmLineString getConverted() {
		GmLineString ret = new GmLineString();
		double[] convertedPoint = new double[2];
		VgPoint point;
		for (int i=0;i<mVertices.size();i++) {
			point = ((VgPoint)mVertices.get(i));
			GKTransform.gaussToEll(point.getX(),point.getY(), 2, convertedPoint);
			GKTransform.ellToGauss(convertedPoint[0], convertedPoint[1], 3, convertedPoint);
			ret.addVertex(new GmPoint(convertedPoint[0],convertedPoint[1],point.getZ()));			
			//System.out.println("Conv "+i+ " from: "+ point + " TO " + new GmPoint(convertedPoint[0],convertedPoint[1],point.getZ()));
		}
		return ret;
	}
}