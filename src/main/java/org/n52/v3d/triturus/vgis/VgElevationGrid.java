package org.n52.v3d.triturus.vgis;

/**
 * Klasse zur Verwaltung eines Gitters von Höhenpunkten. Ein Gitter ist wahlweise Vertex-basiert ("Lattices") oder 
 * Zellen-basiert ("Grids").<p>
 * Die Gitterelemente müssen nicht notwendigerweise mit Höhenwerten belegt sein . Für jedes Element lässt sich ein
 * "no data"-Flag setzen.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class VgElevationGrid extends VgFeature 
{
    /**  
     * liefert die Anzahl der Punkte in Richtung der x-Achse (Spalten).<p>
     */
    abstract public int numberOfColumns();

    /** 
     * liefert die Anzahl der Punkte in Richtung der y-Achse (Zeilen).<p>
     */
    abstract public int numberOfRows();
    
    /** 
     * setzt den Höhenwert <tt>pZ</tt> für den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>.<p>
     */
    abstract public void setValue(int pRow, int pCol, double pZ);

    /** 
     * liefert den Höhenwert für den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>.<p>
     */
    abstract public double getValue(int pRow, int pCol);

    /** 
     * liefert den kleinsten (niedrigsten) im Elevation-Grid enthaltenen Höhenwert.<p>
     */
    abstract public double minimalElevation();

    /** 
     * liefert den größten (höchsten) im Elevation-Grid enthaltenen Höhenwert.<p>
     */
    abstract public double maximalElevation();

    /**
     * liefert die Differenz des größten (höchsten) und kleinsten (niedrigsten) im Elevation-Grid enthaltenen
     * Höhenwerts.<p>
     */
    public double elevationDifference() {
        return this.maximalElevation() - this.minimalElevation();
    }
}
