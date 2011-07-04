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

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Class to hold a triangle that might be arbitrarily oriented in 3-D space.
 * @author Benno Schmidt
 */
abstract public class VgTriangle extends VgGeomObject2d 
{
	/** 
	 * sets the triangle's corner-points.
	 */
	abstract public void setCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3);

	/** 
     * @deprecated
	 * <i>TODO: Methode funktioniert in Implementierung gisimplm noch nicht richtig...</i><p>
	 */
	abstract public void getCornerPoints(VgPoint pCorner1, VgPoint pCorner2, VgPoint pCorner3);
    
    /** 
     * returns the triangle's corner-points.
     * @return Array consisting of three elements holding the corner-points
     */
    abstract public VgPoint[] getCornerPoints();

	/**
     * returns the triangle area referring to the assigned coordinate reference system.
     * @return Area value
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
     * returns the triangle's circumference referring to the assigned coordinate reference system.
     * @return Area value
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
     * performs z-value interpolation.<p>
	 * <i>German:</i> interpoliert auf Grundlage des Dreiecks den z-Wert an der Stelle <tt>pt</tt>. Die Methode liefert
     * auch dann ein Resultat, wenn er angegebene Punkt au&szlig;erhalb des Dreiecks liegt (Extrapolation). Im
     * Bedarfsfall ist zuvor mittels <tt>this.isInside()</tt> zu pr&uuml;fen, ob der Punkt innerhalb des Dreiecks
     * liegt.<p>
	 * Bem.: Die z-Koordinate von <tt>pt</tt> kann auf einen Dummy-Wert gesetzt sein.
     * @param pt
     * @return
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

        // L�sung der Ebenengleichung t[2] + s0 * (t[0]-t[2]) + s1 * (t[1]-t[2]) = 0 mit Cramerscher Regel:

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
     * checks, if <tt>pt</tt> is inside the triangle.<p>
	 * <i>German:</i> pr&uml;ft, ob die Stelle <tt>pt</tt> innerhalb des Dreiecks liegt. Wird in <tt>pEdge</tt> der Wert
     * <i>true</i> angegeben, so liefert die Methode auch dann <i>true</i> als Resultat, wenn <tt>p</tt> auf einer der
     * Dreiecksseiten liegt.<p>
	 * Bem.: Die z-Koordinate von <tt>pt</tt> kann auf einen Dummy-Wert gesetzt sein, da die Berechnung in der x-Ebene
	 * erfolgt.
	 * <b>TODO: Methode ist noch nicht getestet (von C++ portiert...)</b>
     * @param pt
     * @param pEdge
     * @return
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
            return false; // Pkt. au�erhalb Parallelogramm
        if (s0 + s1 < 1.)
            return true; // Pkt. echt in Dreieck
        if (s0 + s1 == 1. && pEdge)
            return true; // Pkt. auf Dreiecksseite
        return false; // Pkt. in Parallelogramm und nicht in Dreieck
	}
	
	public String toString() {
		VgPoint x[] = this.getCornerPoints();
		return "[" + x[0].toString() + ", " + x[1].toString() + ", " + x[2].toString() + "]";
	}
}