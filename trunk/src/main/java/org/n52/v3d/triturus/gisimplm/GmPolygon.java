package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgPolygon;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * <tt>VgPolygon</tt>-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden. x- und y-Werte sind
 * bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben. Die Eckpunkte können mit einer z-Koordinate
 * versehen sein.<p>
 * Bem.: In der vorliegenden Implementierung erfolgt keine Prüfung auf Überlappung der Liniensegmente.
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmPolygon extends VgPolygon
{
    private ArrayList mVertices = null;
    
    public GmPolygon() {
    	mVertices = new ArrayList();
    }

    /** 
     * fügt dem Polygon (am Ende der Vertex-Liste) einen Eckpunkt hinzu.<p> 
     * Vorbedingung: <tt>N = this.numberOfVertices()</tt>
     * Nachbedingung: <tt>this.numberOfVertices() = N + 1</tt><p>
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public void addVertex(VgPoint pPnt) throws T3dException
    {
    	this.assertSRS(pPnt);
    	GmPoint lPnt = new GmPoint(pPnt);
    	mVertices.add(lPnt);
    }

    /** 
     * liefert den i-ten Eckpunkt des Polygons.<p> 
     * Hierbei ist die Bedingung <tt>0 &lt;= i &lt; this.numberOfVertices()</tt> einzuhalten; 
     * anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
     * @throws T3dException
     */
    public VgPoint getVertex(int i) throws T3dException 
    {
    	if (i < 0 || i >= this.numberOfVertices())
    	    throw new T3dException("Index out of bounds.");
    	// else:
    	return (GmPoint) mVertices.get( i );
    }

    /** gibt die Anzahl der Eckpunkte des Polygons zurück. */
    public int numberOfVertices() {
    	return mVertices.size();
    }
    
    /** 
     * liefert die Bounding-Box der Geometrie.<p>
     * @return <tt>GmEnvelope</tt> oder <i>null</i>, falls <tt>this.numberOfVertices() = 0</tt>.
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
	 * liefert die zugehörige "Footprint"-Geometrie.<p>
	 * @return "Footprint" als <tt>GmPolygon</tt>-Objekt
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