package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung eines (beliebig im Raum orientierten) Dreiecks.<p>
 * @author Benno Schmidt<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgTriangle extends VgGeomObject2d 
{
	/** 
	 * setzt die Eckpunkte des Dreiecks.<p> 
	 */
	abstract public void setCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3);

	/** 
     * @deprecated
	 * liefert die Eckpunkte des Dreiecks.<p>
	 * <i>TODO: Methode funktioniert in Implementierung gisimplm noch nicht richtig...</i><p>
	 */
	abstract public void getCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3);
    
    /** 
     * liefert die Eckpunkte des Dreiecks.<p>
     * @return dreielementiges Array mit Eckpunktn
     */
    abstract public VgPoint[] getCornerPoints();

	/**
	 * liefert den Flächeninhalt des definierten Dreiecks Objekts bezogen auf das zugrunde liegende räumliche
     * Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	public double area() 
	{
		VgPoint p1 = null, p2 = null, p3 = null;
		this.getCornerPoints(p1, p2, p3);
		
   		double l12 = p2.distance(p1);
   		double l13 = p3.distance(p1);
   		double l23 = p3.distance(p2);
   		
   		double s = (l12 + l13 + l23) / 2.;
   		return Math.sqrt(s * (s - l12) * (s - l13) * (s - l23)); // Heron-Formel
	}

	/**
	 * liefert den Umfang des Dreiecks bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * @see VgGeomObject#getSRS
	 */
	public double circumference()
	{
		VgPoint p1 = null, p2 = null, p3 = null;
		this.getCornerPoints(p1, p2, p3);

		double sum = p2.distance(p1);
		sum += p3.distance(p2);
		sum += p1.distance(p3);
		return sum;
	}
	
	/**
	 * interpoliert auf Grundlage des Dreiecks den z-Wert an der Stelle <tt>pt</tt>. Die Methode liefert auch dann
	 * ein Resultat, wenn er angegebene Punkt außerhalb des Dreiecks liegt (Extrapolation). Im Bedarfsfall ist zuvor
	 * mittels <tt>this.isInside()</tt> zu prüfen, ob der Punkt innerhalb des Dreiecks liegt.<p>
	 * Bem.: Die z-Koordinate von <tt>pt</tt> kann auf einen Dummy-Wert gesetzt sein.<p>
	 * @see VgTriangle#isInsideXY
	 */
	public double interpolateZ(VgPoint pt) 
	{
        VgPoint[] t = this.getCornerPoints();
//System.out.println("t[0] = " + t[0]);
//System.out.println("t[1] = " + t[1]);
//System.out.println("t[2] = " + t[2]);

        T3dVector dir0 = new T3dVector();
        dir0.assignDiff(t[0], t[2]);
        T3dVector dir1 = new T3dVector();
        dir1.assignDiff(t[1], t[2]);

        // Lösung der Ebenengleichung t[2] + s0 * (t[0]-t[2]) + s1 * (t[1]-t[2]) = 0 mit Cramerscher Regel:

        double detNum =
            - dir0.getX() * dir1.getY() * t[2].getZ()
            + dir1.getX() * (pt.getY() - t[2].getY()) * dir0.getZ()
            + (pt.getX() - t[2].getX()) * dir0.getY() * dir1.getZ()
            - dir0.getZ() * dir1.getY() * (pt.getX() - t[2].getX())
            - dir1.getZ() * (pt.getY() - t[2].getY()) * dir0.getX()
            + t[2].getZ() * dir0.getY() * dir1.getX();
            
        double detDenom = -dir0.getX() * dir1.getY() + dir0.getY() * dir1.getX();
//System.out.println("tri = " + this);
//System.out.println("dir0 = " + dir0);
//System.out.println("dir1 = " + dir1);
//System.out.println("pt = " + pt);
//System.out.println("detNum = " + detNum);
//System.out.println("detDenom = " + detDenom);
                             
        if (Math.abs(detDenom) < 0.000001)
            throw new T3dException("Divison by zero error."); // sollte nicht auftreten
        else
            return detNum/detDenom;
	}

	/**
	 * prüft, ob die Stelle <tt>pt</tt> innerhalb des Dreiecks liegt. Wird in <tt>pEdge</tt> der Wert <i>true</i> 
	 * angegeben, so liefert die Methode auch dann <i>true</i> als Resultat, wenn <tt>p</tt> auf einer der Dreiecksseiten 
	 * liegt.<p>
	 * Bem.: Die z-Koordinate von <tt>pt</tt> kann auf einen Dummy-Wert gesetzt sein, da die Berechnung in der x-Ebene
	 * erfolgt.<p>
	 * <b>TODO: Methode ist noch nicht getestet (von C++ portiert...)
	 */
	public boolean isInsideXY(VgPoint pt, boolean pEdge)
    {
        VgPoint[] t = this.getCornerPoints();

        T3dVector dir0 = new T3dVector();
        dir0.assignDiff(t[0], t[2]);
        T3dVector dir1 = new T3dVector();
        dir1.assignDiff(t[1], t[2]);
   
        // Die Gleichung t[2] + s0 * (t[0]-t[2]) + s1 * (t[1]-t[2]) = 0, 0 < s0, s1 < 1, beschreibt die
        // Menge der im durch (t[0]-t[2]), (t[1]-t[2]) festgelegten Parallelogramm liegenden Punkte.
   
        double detNum0 = (pt.getX() - t[2].getX()) * dir1.getY() - (pt.getY() - t[2].getY()) * dir1.getX();
        double detNum1 = dir0.getX() * (pt.getY() - t[2].getY()) - dir0.getY() * (pt.getX() - t[2].getX());
        double detDenom = dir0.getX() * dir1.getY() - dir0.getY() * dir1.getX();
   
        if (Math.abs(detDenom) < 0.000001)
            throw new T3dException("Divison by zero error."); // sollte nicht auftreten

        double s0 = detNum0/detDenom;
        double s1 = detNum1/detDenom;

        // pt liegt im echt Dreieck, wenn 0 < s0 + s1 < 1:
        if (s0 < 0. || s0 < 0. || s0 > 1. || s0 > 1.)
            return false; // Pkt. außerhalb Parallelogramm
        if (s0 + s1 < 1.)
            return true; // Pkt. echt in Dreieck
        if (s0 + s1 == 1. && pEdge)
            return true; // Pkt. auf Dreiecksseite
        return false; // Pkt. in Parallelogramm und nicht in Dreieck
	}
	
	public String toString() {
		VgPoint p1 = null, p2 = null, p3 = null;
		VgPoint x[] = this.getCornerPoints();
		return "[" + x[0].toString() + ", " + x[1].toString() + ", " + x[2].toString() + "]";
	}
}