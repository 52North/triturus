package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.vgis.VgPlane;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.VgTriangle;
import org.n52.v3d.triturus.core.T3dException;

/** 
 * <tt>VgPlane</tt>-Implementierung zur Verwaltung von Ebenen im dreidimensionalen Darstellungsraum.<p>
 * <i>todo: Klasse ist noch nicht getestet.</i>
 * @author Benno Schmidt<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 */
public class GmPlane extends VgPlane
{
    private double mA, mB, mC, mD; // Ebenengleichung A*x + B*y + C*z + D = 0

    /** 
     * Konstruktor. Es wird die durch die drei gegebenen Punkte definierte Ebene konstruiert. Falls die drei
     * angegebenen Punkte kollinear sind, wird eine <tt>T3dException</tt> geworfen.<p>
     */
    public GmPlane(VgPoint pt1, VgPoint pt2, VgPoint pt3)
    {
    	GmTriangle triangle = new GmTriangle(pt1, pt2, pt3);
    	this.init(triangle);
    }

    /** 
     * Konstruktor. Es wird die durch die gegebenen Dreieckspunkte definierte Ebene konstruiert. Falls die
     * Dreiecksfläche = 0 ist, wird eine <tt>T3dException</tt> geworfen.
     */
	public GmPlane(VgTriangle pTriangle) {
		this.init(pTriangle);
	}
	
	private void init(VgTriangle pTriangle) throws T3dException
	{
		if (pTriangle.area() < .000001)
			throw new T3dException("Plane instantiation failed. Maybe the definition points are collinear.");
			
		VgPoint pt1 = null, pt2 = null, pt3 = null;
		pTriangle.getCornerPoints(pt1, pt2, pt3);		
		
		T3dVector v1 = new T3dVector();
		v1.assignDiff(pt2, pt1);
		T3dVector v2 = new T3dVector();
		v1.assignDiff(pt3, pt1);

		T3dVector normal = new T3dVector();
		normal.assignCrossProd(v1, v2);

		mA = normal.getX();
		mB = normal.getY();
		mC = normal.getZ();
		mD = -1. * (mA * pt1.getX() + mB * pt1.getY() + mC * pt1.getZ());		
	}

 	/**
 	 * liefert einen zur Ebene gehörigen normierten Normalenvektor.<p>
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Richtungsvektor</i> als <tt>VgPoint</tt>
 	 */
	public VgPoint getNormal()
	{
		T3dVector hlp = new T3dVector(mA, mB, mC); 
		hlp.doNorm();

		VgPoint ret = null;
		ret = new GmPoint(hlp.getX(), hlp.getY(), hlp.getZ()); 
		ret.setSRS(this.getSRS());
		return ret;
	}

 	/**
 	 * liefert einen auf der Ebene liegenden Punkt.<p>
 	 * @see org.n52.v3d.triturus.vgis.VgPoint
 	 * @return <i>Ortssvektor</i> als <tt>VgPoint</tt>
 	 */
	public VgPoint getAnchor()
	{
		VgPoint ret = null;
		ret = new GmPoint(0., 0., 0.);
		ret.setSRS(this.getSRS());
		if (mC != 0.) {
			ret.setZ(-mD/mC); 
			return ret;
		}
		if (mB != 0.) {
			ret.setY(-mD/mB);
			return ret;
		}
		if (mA != 0.) {
			ret.setX(-mD/mA);
			return ret;
		}
		// sonst: A = B = C = 0:
		throw new T3dException("Numerical error.");
	}
	
	/**
	 * projiziert den Punkt <tt>pt</tt> in z.Richtung auf die Ebene. Ist die Ebene parallel zur z-Achse, wird eine
     * <tt>T3dException</tt> geworfen.<p>
	 * @return <tt>GmPoint</tt> mit gleicher x- und y-Koordinate wie <tt>pt</tt>.
	 * @throws T3dException
	 */
	public VgPoint projectPointZ(VgPoint pt)
	{
		VgPoint ret = null;
		ret = new GmPoint(pt);
		
		if (mC != 0.) {
			ret.setZ( (-mD - mA*pt.getX() - mB*pt.getY()) / mC); 
			return ret;
		}
		// sonst: C = 0:
		throw new T3dException("Numerical error. Can't get unique z-value for vertical plane.");
	}
}
