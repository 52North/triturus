package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.core.T3dException;

/**
 * <tt>VgPoint</tt>-Implementierung, bei der die Punktkoordinaten im Speicher vorgehalten werden. x- und y-Werte sind
 * bezogen auf das eingestellte räumliche Bezugssystem (SRS) anzugeben.<p>
 * @author Benno Schmidt<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class GmPoint extends VgPoint
{
    private double mX, mY, mZ;

    /**
     * Konstruktor. Dieser Konstruktor initialisiert x, y und z mit den angegebenen Koordinaten.<p>
     */
    public GmPoint(double pX, double pY, double pZ) {
        mX = pX;
        mY = pY;
        mZ = pZ;
    }

    /**
     * Konstruktor. Dieser Konstruktor initialisiert x, y und z jeweils mit dem Wert 0.<p>
     */
    public GmPoint() {
        mX = 0.;
        mY = 0.;
        mZ = 0.;
    }

    /**
     * Konstruktor. Dieser Konstruktor initialisiert x, y und z mit Koordinaten des angegebenen Punktobjekts.<p>
     */
    public GmPoint(VgPoint pt) {
    	this.set(pt);
    }

    /**
     * Konstruktor. Dieser Konstruktor initialisiert x, y und z anhand der angegebenen Komma-separierten
     * Koordinatenliste. Ist kein z-Wert angegeben, wird z = 0 gesetzt.<p>
     * Beispiele: <tt>&quot;3500000,5800000&quot;, &quot;3500000,5800000,50.5&quot;</tt><p>
     * Die Methode wirft eine <tt>T3dException</tt>, falls der String kein interpretierbaren Koordinatenangaben
     * enthält.<p>
     * @param pCommaSeparatedList Liste mit 2 oder 3 Koordinaten
     */
    public GmPoint(String pCommaSeparatedList) {
        String[] coords = pCommaSeparatedList.split(",");
        if (coords.length < 2 || coords.length > 3)
            throw new T3dException("Cannot parse geo-coordinates from \"" + pCommaSeparatedList + "\".");
        mX = Double.parseDouble(coords[0]);
        mY = Double.parseDouble(coords[1]);
        if (coords.length >= 3)
            mZ = Double.parseDouble(coords[2]);
        else
            mZ = 0.;
    }

	/**
	 * setzt den x-Wert der Punktgeometrie.<p> 
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) ist als x-Wert die geografische Breite anzugeben, für
     * <i>Gauß-Krüger-Koordinaten</i> der Rechtswert.<p>
	 */
    public void setX(double pX) { 
        mX = pX; 
    }

    /**
     * liefert den x-Wert der Punktgeometrie.<p>
     */
    public double getX() {
        return mX;
    }

	/** 
	 * setzt den y-Wert der Geometrie.<p> 
	 * Bem.: Für <i>geografische Koordinaten</i> (EPSG:4326) ist als y-Wert die geografische Breite anzugeben, für
     * <i>Gauß-Krüger-Koordinaten</i> der Hochwert.<p>
	 */
    public void setY(double pY) { 
        mY = pY; 
    }

    /**
     * liefert den y-Wert der Geometrie.<p>
     */
    public double getY() {
        return mY;
    }

    /**
     * setzt den z-Wert der Geometrie.<p>
     */
    public void setZ(double pZ) {
        mZ = pZ;
    }

    /**
     * liefert den z-Wert der Geometrie (z. B. Höhenwert).<p>
     */
    public double getZ() {
        return mZ;
    }

    /**
     * liefert die Bounding-Box der Geometrie.<p>
     */
    public VgEnvelope envelope() {
        VgEnvelope lEnv = new GmEnvelope(mX, mX, mY, mY, mZ, mZ);
        return lEnv;
    }

	/** 
	 * liefert den zugehörigen "Footprint"-Punkt.<p>
	 * @return "Footprint" als <tt>GmPoint</tt>-Objekt
  	 */
	public VgGeomObject footprint() {
		return new GmPoint(mX, mY, 0.);
	}

    /**
     * @deprecated
     * berechnet den Abstand zweier Punkte bezogen auf die xy-Ebene. Falls die Punkte nicht im gleichen räumlichen
     * Bezugssystem liegen, wird eine <tt>T3dSRSException</tt> geworfen.<p>
     * @throws org.n52.v3d.triturus.vgis.T3dSRSException
     * @see VgPoint#distanceXY
     */
    public double distance2d(GmPoint pPnt) throws T3dException
    {
       if (!this.getSRS().equalsIgnoreCase(pPnt.getSRS()))
          throw new T3dException("Spatial reference systems are incompatible.");

       if (this.getSRS().equalsIgnoreCase("EPSG:31411") // GKK3
           || this.getSRS().equalsIgnoreCase("EPSG:31412") // GKK4
           || this.getSRS().equalsIgnoreCase("EPSG:31413")) // GKK5
       {
           double dx = pPnt.getX() - this.getX();
           double dy = pPnt.getY() - this.getY();
           return Math.sqrt(dx * dx + dy * dy);
       }
       // else:
       throw new T3dSRSException("Spatial reference systems is not supported (yet).");
    }

    /** 
     * @deprecated
     * berechnet den Abstand zweier Punkte im Dreidimensionalen. Falls die Punkte nicht im gleichen räumlichen
     * Bezugssystem liegen, wird eine <tt>T3dNotYetImplException</tt> geworfen.<p>
     * @throws T3dSRSException
     * @see VgPoint#distance
     */
    public double distance3d(GmPoint pPnt) throws T3dSRSException
    {
       if (!this.getSRS().equalsIgnoreCase(pPnt.getSRS()))
          throw new T3dSRSException("Spatial reference systems are incompatible.");
       if (this.getSRS().equalsIgnoreCase("EPSG:31411") // GKK3
           || this.getSRS().equalsIgnoreCase("EPSG:31412") // GKK4
           || this.getSRS().equalsIgnoreCase("EPSG:31413")) // GKK5
       {
           double dx = pPnt.getX() - this.getX();
           double dy = pPnt.getY() - this.getY();
           double dz = pPnt.getZ() - this.getZ();
           return Math.sqrt(dx * dx + dy * dy + dz * dz);
       }
       // else:
       throw new T3dSRSException("Spatial reference systems is not supported (yet).");
    }
}
