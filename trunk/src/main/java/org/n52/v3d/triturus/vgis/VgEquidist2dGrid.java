package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Verwaltung äquidistanter, beliebig in der xy-Ebene orientierter Gittergeometrien.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgEquidist2dGrid extends VgGeomObject
{
	/** liefert die Anzahl der Zeilen des Gitters (erste Gitterachse).<p> */
	abstract public int numberOfRows();

	/** liefert die Anzahl der Spalten des Gitters (zweite Gitterachse).<p> */
	abstract public int numberOfColumns();

	/** liefert den Richtungsvektor der ersten Gitterachse (Zeilenrichtung).<p> */
	abstract public VgPoint getDirectionColumns();

	/** liefert den Richtungsvektor der zweiten Gitterachse (Spaltenrichtung).<p> */
	abstract public VgPoint getDirectionRows();

	/** liefert die Gitterweiten.<p> */
	abstract public void getDelta(Double pDeltaRows, Double pDeltaColumns);

	/** liefert den Ursprungspunkt des Gitters.<p> */
	abstract public VgPoint getOrigin();

	/**
     * liefert die Koordinate für das Gitterelement mit den angegebenen Indizes.<p>
     * Es sind die Bedingungen <i>0 &lt;= i &lt; this.numberOfRows(), 0 &lt;= j &lt; this.numberOfColumns()</i>
     * einzuhalten; anderenfalls wird eine <i>T3dException</i> geworfen.<p>
     * @param i Index der Gitterzeile
     * @param j Index der Gitterspalte
     * @return Vertex mit x- und y-Koordinate (z undefiniert)
     * @throws T3dException
	 */
	abstract public VgPoint getVertexCoordinate(int i, int j) throws T3dException;

	public String toString() {
		return "[" + "(#" + this.numberOfRows() + " rows x #" +
			this.numberOfColumns() + " cols)" + "]";
	}
}