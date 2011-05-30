package org.n52.v3d.triturus.vgis;

/**
 * Klasse zur Verwaltung von Ebenen im dreidimensionalen Darstellungsraum.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgPlane extends VgGeomObject 
{
	/**
	 * liefert die Bounding-Box der Geometrie. Insofern die Ebene nicht parallel zur xy-Ebene oder zur z-Achse ist, ist
     * die Bounding-Box unbegrenzt. Dieser Fall lässt sich durch Analyse des Normalenvektors abfangen. Die
     * <tt>envelope()</tt>-Methode liefert in jedem Fall den Wert <i>null</i>.<p>
	 * @see VgPlane#getNormal
	 * @return stets <i>null</i>
	 */
	public VgEnvelope envelope() {
		return null;
	}

 	/**
 	 * liefert einen zur Ebene gehörigen normierten Normalenvektor.<p>
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Richtungsvektor</i> als <tt>VgPoint</tt>
 	 */
	abstract public VgPoint getNormal();

 	/**
 	 * liefert einen auf der Ebene liegenden Punkt.<p>
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Ortssvektor</i> als <tt>VgPoint</tt>
 	 */
	abstract public VgPoint getAnchor();
	
	/** 
	 * liefert das Objekt, das sich durch Projektion der Geometrie auf die xy-Ebene ergibt. Insofern die Ebene nicht
     * parallel zur xy-Ebene oder zur z-Achse ist, ist die "Footprint"-Geometrie unbegrenzt. Dieser Fall lässt sich
     * durch Analyse des Normalenvektors abfangen. Die <tt>footprint()</tt>-Methode liefert in jedem Fall den Wert
     * <i>null</i>.<p>
	 * @see VgPlane#getNormal
	 * @return stets <i>null</i>
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
