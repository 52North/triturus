package org.n52.v3d.triturus.vgis;

/**
 * Klasse zur Verwaltung eines Gitters von H�henpunkten. Ein Gitter ist wahlweise Vertex-basiert ("Lattices") oder 
 * Zellen-basiert ("Grids").<p>
 * Die Gitterelemente m�ssen nicht notwendigerweise mit H�henwerten belegt sein . F�r jedes Element l�sst sich ein
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
     * setzt den H�henwert <tt>pZ</tt> f�r den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>.<p>
     */
    abstract public void setValue(int pRow, int pCol, double pZ);

    /** 
     * liefert den H�henwert f�r den Zeilenindex <tt>pRow</tt> und den Spaltenindex <tt>pCol</tt>.<p>
     */
    abstract public double getValue(int pRow, int pCol);

    /** 
     * liefert den kleinsten (niedrigsten) im Elevation-Grid enthaltenen H�henwert.<p>
     */
    abstract public double minimalElevation();

    /** 
     * liefert den gr��ten (h�chsten) im Elevation-Grid enthaltenen H�henwert.<p>
     */
    abstract public double maximalElevation();

    /**
     * liefert die Differenz des gr��ten (h�chsten) und kleinsten (niedrigsten) im Elevation-Grid enthaltenen
     * H�henwerts.<p>
     */
    public double elevationDifference() {
        return this.maximalElevation() - this.minimalElevation();
    }
}
