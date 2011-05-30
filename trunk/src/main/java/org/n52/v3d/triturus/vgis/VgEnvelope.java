package org.n52.v3d.triturus.vgis;

/**
 * Klasse zur Verwaltung dreidimensionaler Bounding-Boxes.
 * <p>
 * x- und y-Werte sind dabei bezogen auf das eingestellte r�umliche Bezugssystem (SRS) anzugeben. Die Beziehungen
 * <tt>env.getMinX() &lt;= env.getMaxX()</tt> und <tt>env.getMinY() &lt;= env.getMaxY()</tt> sind von den
 * implementierenden Klassen stets einzuhalten.
 * <p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgEnvelope extends VgGeomObject3d 
{
	/**
     * setzt den minimalen x-Wert der Bounding-Box.<p>
     * @param pX x-Wert
     */
	abstract public void setXMin(double pX);

	/**
     * liefert den minimalen x-Wert der Bounding-Box.<p>
     * @return x-Wert
     */
	abstract public double getXMin();

	/**
     * setzt den maximalen x-Wert der Bounding-Box.<p>
     * @param pX x-Wert
     */
	abstract public void setXMax(double pX);

	/**
     * liefert den maximalen x-Wert der Bounding-Box.<p>
     * @return x-Wert
     */
	abstract public double getXMax();

	/**
     * setzt den minimalen y-Wert der Bounding-Box.<p>
     * @param pY y-Wert
     */
	abstract public void setYMin(double pY);

	/**
     * liefert den minimalen y-Wert der Bounding-Box.<p>
     * @return y-Wert
     */
	abstract public double getYMin();

	/**
     * setzt den maximalen y-Wert der Bounding-Box.<p>
     * @param pY y-Wert
     */
	abstract public void setYMax(double pY);

	/**
     * liefert den maximalen y-Wert der Bounding-Box.<p>
     * @return y-Wert
     */
	abstract public double getYMax();

	/**
     * setzt den minimalen z-Wert der Bounding-Box.<p>
     * @param pZ z-Wert
     */
	abstract public void setZMin(double pZ);

	/**
     * liefert den minimalen z-Wert der Bounding-Box.<p>
     * @return z-Wert
     */
	abstract public double getZMin();

	/**
     * setzt den maximalen z-Wert der Bounding-Box.<p>
     * @param pZ z-Wert
     */
	abstract public void setZMax(double pZ);

	/**
     * liefert den maximalen z-Wert der Bounding-Box.<p>
     * @return z-Wert
     */
	abstract public double getZMax();

	/**
     * liefert den Mittelpunkt der Bounding-Box.<p>
     * @return Mittelpunkt
     */
	abstract public VgPoint getCenterPoint();

    /**
     * setzt den Mittelpunkt der Bounding-Box.<p>
     * @param pCenter neuer Mittelpunkt
     */
    abstract public void setCenterPoint(VgPoint pCenter);

    /**
     * Verschieben der Bounding-Box.<p>
     * @param pShift Translationsvektor
     */
    public void translate(VgPoint pShift)
    {
        this.setXMin(this.getXMin() + pShift.getX()); this.setXMax(this.getXMax() + pShift.getX());
        this.setYMin(this.getYMin() + pShift.getY()); this.setYMax(this.getYMax() + pShift.getY());
        this.setZMin(this.getZMin() + pShift.getZ()); this.setZMax(this.getZMax() + pShift.getZ());
    }

    /**
     * Skalierung der Bounding-Box. Der Mittelpunkt bleibt dabei erhalten, die Ausdehnungen der Bounding-Box in x-, y-
     * und z-Richtung werden mit dem angegebenen Wert multipliziert.<p>
     * @param pFactor Skalierungsfaktor
     */
    public void scale(double pFactor)
    {
        double mx = (this.getXMin() + this.getXMax()) / 2.;
        double my = (this.getYMin() + this.getYMax()) / 2.;
        double mz = (this.getZMin() + this.getZMax()) / 2.;

        double dx = this.getXMax() - this.getXMin();
        double dy = this.getYMax() - this.getYMin();
        double dz = this.getZMax() - this.getZMin();

        this.setXMin(mx - pFactor * 0.5 * dx);
        this.setXMax(mx + pFactor * 0.5 * dx);
        this.setYMin(my - pFactor * 0.5 * dy);
        this.setYMax(my + pFactor * 0.5 * dy);
        this.setZMin(mz - pFactor * 0.5 * dz);
        this.setZMax(mz + pFactor * 0.5 * dz);
    }

    /**
     * Skalierung der Bounding-Box. Der Mittelpunkt bleibt dabei erhalten, die Ausdehnungen der Bounding-Box in x-, y-
     * und z-richtung werden auf die angegebenen Werte gesetzt.<p>
     * @param pExtentX neue Ausdehnung in x-Richtung
     * @param pExtentY neue Ausdehnung in y-Richtung
     * @param pExtentZ neue Ausdehnung in z-Richtung
     */
    public void resize(double pExtentX, double pExtentY, double pExtentZ)
    {
        VgPoint center = this.getCenterPoint();

        this.setXMin(center.getX() - pExtentX / 2.);
        this.setXMax(center.getX() + pExtentX / 2.);
        this.setYMin(center.getY() - pExtentY / 2.);
        this.setYMax(center.getY() + pExtentY / 2.);
        this.setZMin(center.getZ() - pExtentZ / 2.);
        this.setZMax(center.getZ() + pExtentZ / 2.);
    }

    /**
     * liefert die Ausdehnung der Bounding-Box in x-Richtung (Breite).<p>
     * @return Ausdehnung &gt;= 0
     */
    public double getExtentX() {
        return Math.abs(this.getXMax() - this.getXMin());
    }

    /**
     * liefert die Ausdehnung der Bounding-Box in y-Richtung (L�nge/Tiefe).<p>
     * @return Ausdehnung &gt;= 0
     */
    public double getExtentY() {
        return Math.abs(this.getYMax() - this.getYMin());
    }

    /**
     * liefert die Ausdehnung der Bounding-Box in z-Richtung (H�he).<p>
     * @return Ausdehnung &gt;= 0
     */
    public double getExtentZ() {
        return Math.abs(this.getZMax() - this.getZMin());
    }

    /**
     * Abfrage der L�nge der (3D-) Diagonalen der Bounding-Box.<p>
     * @return L�nge der Diagonalen
     */
    public double diagonalLength()
    {
        double dx = this.getXMax() - this.getXMin();
        double dy = this.getYMax() - this.getYMin();
        double dz = this.getZMax() - this.getZMin();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

	/**
	 * liefert das Volumen des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem.<p>
     * @return Volumen
	 * @see VgGeomObject#getSRS
	 */
	public double volume() 
	{
		double dx = this.getXMax() - this.getXMin();
		double dy = this.getYMax() - this.getYMin();
		double dz = this.getZMax() - this.getZMin();
		
		return dx * dy * dz;
	}

	/**
	 * liefert die Oberfl�che des Geometrie-Objekts bezogen auf das zugrunde liegende r�umliche Referenzsystem.<p>
     * @return Oberfl�che
	 * @see VgGeomObject#getSRS
	 */
	public double surface()
	{
		double dx = this.getXMax() - this.getXMin();
		double dy = this.getYMax() - this.getYMin();
		double dz = this.getZMax() - this.getZMin();
		
		return (2. * dx * dy + dx * dz + dy * dz);		
	}

    /**
     * erweitert die r�umliche Ausdehnung der Bounding-Box so, dass sie den angegebenen Punkt umfasst.<p>
     */
    public void letContainPoint(VgPoint pPnt)
    {
    	this.assertSRS(pPnt);

        if (pPnt.getX() < this.getXMin()) this.setXMin(pPnt.getX()); else {
            if (pPnt.getX() > this.getXMax()) this.setXMax(pPnt.getX());
        }
        if (pPnt.getY() < this.getYMin()) this.setYMin(pPnt.getY()); else {
            if (pPnt.getY() > this.getYMax()) this.setYMax(pPnt.getY());
        }
        if (pPnt.getZ() < this.getZMin()) this.setZMin(pPnt.getZ()); else {
            if (pPnt.getZ() > this.getZMax()) this.setZMax(pPnt.getZ());
        }
    }

    /**
     * erweitert die r�umliche Ausdehnung der Bounding-Box so, dass sie die angegebene Bounding-Box umfasst
     * (Vereinigungsoperator).<p>
     */
    abstract public void letContainEnvelope(VgEnvelope pEnv);

    /**
     * Pr�fung von Bounding-Box-Objekten auf geometrische �bereinstimmung.<p>
     * @param pEnv Mit <tt>this</tt> zu vergleichende Bounding-Box.
     * @return <i>true</i>, falls geometrische �bereinstimmung, sonst <i>false</i>.
     */
    public boolean isSpatiallyEquivalent(VgEnvelope pEnv)
    {
        double eps = 1.0e-2; // sollte f�r im Metern gegebenen Koordinaten ausreichen (Zentimeter-Genauigkeit) -> todo
        return
            Math.abs(this.getXMin() - pEnv.getXMin()) < eps &&
            Math.abs(this.getXMax() - pEnv.getXMax()) < eps &&
            Math.abs(this.getYMin() - pEnv.getYMin()) < eps &&
            Math.abs(this.getYMax() - pEnv.getYMax()) < eps;
    }

    /**
     * �berpr�ft, ob ein gegebener Punkt innerhalb der Bounding-Box liegt.<p>
     * @param pt zu �berpr�fender Punkt
     * @return <i>true</i>, falls <tt>pt</tt> auf dem Rand oder innerhalb der Bounding-Box liegt, sonst <i>false</i>.
     */
    public boolean contains(VgPoint pt) {
        // TODO: SRS-�berpr�fung fehlt noch!
        double x = pt.getX();
        if (x < this.getXMin() || x > this.getXMax()) return false;
        double y = pt.getY();
        if (y < this.getYMin() || y > this.getYMax()) return false;
        double z = pt.getZ();
        if (z < this.getZMin() || z > this.getZMax()) return false;
        return true;
    }

	public String toString() {
		return "[" +
			"(" + this.getXMin() + ", " + this.getYMin() + ", " + this.getZMin() +
			"), " +
			"(" + this.getXMax() + ", " + this.getYMax() + ", " + this.getZMax() +
			")]";
	}
}
