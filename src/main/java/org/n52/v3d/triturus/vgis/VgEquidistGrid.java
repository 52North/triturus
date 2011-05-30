package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung äquidistanter, beliebig in der xy-Ebene orientierter Gittergeometrien.<p>
 * <tt>VgEquidistGrid</tt>-Objekte sind keine <tt>VgGeomObject2d</tt>-Objekte, da die zugehörige Interpretation
 * fehlt. Eine interpretierbare, konkrete Realisierung ist z. B. die im Paket "gisimplm" implementierte Klasse
 * <tt>GmSimple2dGridGeometry</tt>.<p>
 * @see org.n52.v3d.triturus.gisimplm.GmSimple2dGridGeometry
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgEquidistGrid extends VgGeomObject
{
	/** liefert die Anzahl der Zeilen des Gitters (erste Gitterachse).<p> */
	abstract public int numberOfRows();

	/** liefert die Anzahl der Spalten des Gitters (zweite Gitterachse).<p> */
	abstract public int numberOfColumns();

	/** liefert den Richtungsvektor der ersten Gitterachse (Zeilenrichtung).<p> */
	abstract public T3dVector getDirectionColumns();

	/** liefert den Richtungsvektor der zweiten Gitterachse (Spaltenrichtung).<p> */
	abstract public T3dVector getDirectionRows();

	/** 
	 * @deprecated
	 * @see VgEquidistGrid#getCellSizeRows
	 * @see VgEquidistGrid#getCellSizeColumns
	 * liefert die Gitterweiten für die Achsen in <tt>pDeltaRows</tt> und <tt>pDeltaColumns</tt>.<p> 
     * @param pDeltaRows Gitterweite in Richtung der 1. Achse (Zeilen)
     * @param pDeltaColumns Gitterweite in Richtung der 2. Achse (Spalten)
	 */
	abstract public void getDelta(Double pDeltaRows, Double pDeltaColumns);

	/** 
	 * liefert den Gitter-Abstand für die Gitter-Zeilen.<p>
	 * @return Gitter-Abstand bezogen auf das zugrunde liegende SRS
	 */
	abstract public double getCellSizeRows();

	/** 
	 * liefert den Gitter-Abstand für die Gitter-Spalten.<p>
	 * @return Gitter-Abstand bezogen auf das zugrunde liegende SRS
	 */
	abstract public double getCellSizeColumns();

	/** liefert den Ursprungspunkt des Gitters. */
	abstract public VgPoint getOrigin();

	/**
	 * liefert die Koordinate für das Gitterelement mit den angegebenen Indizes.<p>
	 * Es sind die Bedingungen 0 &lt;= i &lt; <tt>this.getNoOfRows()</tt>, 0 &lt;= j &lt; <tt>this.getNoOfColumns()</tt>
	 * einzuhalten; anderenfalls wird eine <tt>T3dException</tt> geworfen.<p>
	 * @param i Index der Gitterzeile
	 * @param j Index der Gitterspalte
	 * @return Vertex mit x- und y-Koordinate (z undefiniert)
	 */
	abstract public VgPoint getVertexCoordinate(int i, int j) throws T3dException;

	public String toString() {
		return "[" + "(#" + this.numberOfRows() + " rows x #" +
			this.numberOfColumns() + " cols)" + "]";
	}
}