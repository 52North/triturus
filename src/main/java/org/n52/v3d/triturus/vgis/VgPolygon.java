package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung polygonaler Geometrien ("geschlossene Polylinien" mit Flächeneigenschaft). Die Polygone müssen
 * <i>planar</i> sein.<p>
 * <b>Planarität wird z. Zt. in der Implementierung <tt>GmPlane</tt> noch nicht überpüft. Die Flächenberechnung ist nur
 * für den 2D-Fall (xy-Ebene) Ebene implementiert. Lösungsansatz: Bestimmung der zugehörigen Ebene (Klasse
 * <tt>VgPlane</tt>) durch Minimierung der Abweichung der Polygon-Vertizes von der Ebene; einfache Rechenübung!
 * -> todo</b><p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgPolygon extends VgGeomObject2d 
{
	/**
	 * liefert den i-ten Eckpunkt des Polygons.<p>
	 * Hierbei ist die Bedingung 0 &lt;= i &lt; <tt>this.numberOfCorners()</tt> einzuhalten;
	 * anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
	 * @throws T3dException
	 */
	abstract public VgPoint getVertex(int i) throws T3dException;
	
	/** gibt die Anzahl der Eckpunkte des Polygons zurück. */
	abstract public int numberOfVertices();

	/**
	 * liefert den Flächeninhalt des Geometrie-Objekts bezogen auf das zugrunde liegende 
	 * räumliche Referenzsystem.<p>
	 * Bem.: <b>Die Methode ist noch nicht getestet.</b><p>
	 * @see VgGeomObject#getSRS
	 */
	public double area() {
		return this.sumUpAreaXY()[0]; 
	}

 	/**
	 * liefert den Umfang des Polygons bezogen auf das zugrunde liegende räumliche Referenzsystem.<p>
	 * Bem.: <b>Die Methode ist noch nicht getestet.</b><p>
	 * @see VgGeomObject#getSRS
	 */
	public double circumference()
	{
		return this.sumUpAreaXY()[1]; 
	}

	private double[] sumUpAreaXY() 
	{
		int N = this.numberOfVertices();
		double A = 0., C = 0.;
			
		if (N >= 3) 
		{
			double dx, dy, dz, sx;
			VgPoint pt1, pt2;
		
			for (int i = 0; i < N - 1; i++) 
			{
				pt1 = this.getVertex(i);
				pt2 = this.getVertex(i + 1);
				dx = pt2.getX() - pt1.getX();
				dy = pt2.getY() - pt1.getY();
				dz = pt2.getZ() - pt1.getZ();
   				sx = pt1.getX() + pt2.getX();
   				A += sx * dy;
   				C += Math.sqrt(dx*dx + dy*dy);  
   			}
		
			pt1 = this.getVertex(N - 1);
			pt2 = this.getVertex(0);
			dx = pt2.getX() - pt1.getX();
			dy = pt2.getY() - pt1.getY();
   			sx = pt1.getX() + pt2.getX();
   			A += sx * dy;
   			C += Math.sqrt(dx*dx + dy*dy);  
   		}
   		else {
   			if (N == 2) 
   				C = this.getVertex(0).distanceXY(this.getVertex(1));
   		}
 
 		double[] ret = new double[2];
 		ret[0] = A;
 		ret[1] = C;
 		return ret;
	}

	public String toString() {
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
