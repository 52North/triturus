package org.n52.v3d.triturus.gisimplm;

import java.util.ArrayList;
import org.n52.v3d.triturus.t3dutil.GKTransform;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.core.T3dException;

/**
 * <tt>VgLineString</tt>-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden. x- und y-Werte
 * sind bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben. Die Eckpunkte können mit einer
 * z-Koordinate versehen sein.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmLineString extends VgLineString
{
    private ArrayList mVertices = null;
    
    public GmLineString() {
    	mVertices = new ArrayList();
    }

    /**
     * Konstruktor. Dieser Konstruktor initialisiert den Linienzug anhand der angegebenen Komma-separierten
     * Koordinatenliste. In der Liste,. die mindestens zwei Stützpunkt-Angaben enthalten muss, sind stets z-Werte
     * anzugeben.<p>
     * Beispiel: <tt>&quot;3500010,5800010,50.5,3600010,5800010,100&quot;</tt><p>
     * Die Methode wirft eine <tt>T3dException</tt>, falls der String kein interpretierbaren Koordinatenangaben
     * enthält.<p>
     * @param pCommaSeparatedList Liste mit 3 x N Koordinaten (N > 1)
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
     * fügt der Polylinie (am Ende der Vertex-Liste) einen Stützpunkt hinzu.<p> 
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
     * liefert den i-ten Stützpunkt der Polylinie.<p> 
     * Hierbei ist die Bedingung <tt>0 &lt;= i &lt; this.numberOfVertices()</tt> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
     * @throws T3dException
     */
    public VgPoint getVertex(int i) throws T3dException 
    {
    	if (i < 0 || i >= this.numberOfVertices())
    	    throw new T3dException("Index out of bounds.");
    	// else:
    	return (GmPoint) mVertices.get(i);
    }

    /** gibt die Anzahl der Stützpunkte der Polylinie zurück. */
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
    	    GmEnvelope mEnv = new GmEnvelope( this.getVertex(0) );
            for (int i = 0; i < this.numberOfVertices(); i++)
                mEnv.letContainPoint( this.getVertex(i) );
            return mEnv;
        } else
            return null;
    }
    
    /** 
	 * liefert die zugehörige "Footprint"-Geometrie.<p>
	 * @return "Footprint" als <tt>GmLineString</tt>-Objekt
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
	 * <b>vorübergehende Implementierung -> Profillinien sind in anderen GK-Meridianstreifen
	 * zu transformieren. -> sauber in Rahmenwerk einbauen -> TODO Benno</b>
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