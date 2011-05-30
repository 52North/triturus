package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstrakte Basisklasse zur Verwaltung georeferenzierter Profile. Unter einem <i>Profil</i> wird dabei 
 * eine Linienzug-Geometrie (2D-Polylinie als <i>Definitionslinie</i>) verstanden, zu deren Lauflänge ein 
 * Stationierungsparameter t verwaltet wird. Jedem t-Wert innerhalb des Belegungsbereichs ist dabei ein 
 * eindeutiger Wert f(t) zugeordnet.<p>
 * Bei den Werten f(t) kann es sich insbesondere um Höhenwerte handeln. Daher werden sie innerhalb des 
 * vorliegenden Rahmenwerks als z-Werte bezeichnet.<p>
 * Der <i>Belegungsbereich</i> bezeichnet den Bereich, in dem zu der Definitionslinie z-Werte vorliegen. 
 * Vorliegend ist dieser auf eine Intervall der Gestalt [t_min, t_max] beschränkt. Ggf. ist zu sicherzustellen, 
 * dass diese Beschreibungsform ausreichend ist. Der Fall unbelegter Bereiche kann z. B. dann auftreten, 
 * wenn ein Teil der Definitionslinie des Profils außerhalb eines Höhengitters liegt und somit dort keine 
 * z-Werte interpoliert werden können.<p>
 * Bem.: Es wird vorausgesetzt, dass die über die Methode <tt>getTZPair</tt> abrufbare Folge der t-z-Wertepaare 
 * bezüglich t stets monoton wächst. Die Einhaltung dieser Bedingung durch die implementierenden Klassen ist
 * stets zu gewährleisten.<p>
 * <i>TODO: Die aktuelle Modellierung unterstüztt nur die Verwaltung eines Werteverlaufs z(t) je Profil. 
 * Zukünftig könnte dies erweitert werden! -> Benno</u>
 * @author Benno Schmidt<br>
 * (c) 1992-1996, Geopro GmbH, 2004 con terra GmbH<br>
 */
abstract public class VgProfile extends VgFeature 
{
    private VgLineString mGeom = null; // Modellierung von VgProfile als VgLineString-Dekorierer

    /**
     * setzt die Definitionslinie des Profils.<p>
     * Bem.: Die z-Werte dieser Geometrie sind ohne Bedeutung.<p>
     * @param pGeom <tt>VgLineString</tt>-Objekt
     */
    public void setGeometry(VgLineString pGeom) {
    	mGeom = pGeom;
    }
    
    /** 
     * liefert die Definitionslinie des Profils.<p>
     * @return <tt>VgLineString</tt>-Objekt
     */
    public VgGeomObject getGeometry() {
    	return mGeom; 
    }  

    /**
     * liefert die Anzahl der Stationsstellen des Profils, zu denen z(t)-Werte vorhanden sind.<p>
     * @return Anzahl der Stützstellen
     */
    abstract public int numberOfTZPairs();
    
    /**
     * liefert die Werte der i-ten Stationsstelle des Profils. Das erste Element des Ergebnisfeldes enthält die 
     * Stationierung t, das zweite Element den zugehörigen z-Wert.<p>
     * Die Folge der t-z-Wertepaare wächst bezüglich t stets monoton, d. h. für alle i gilt stets 
     * <i>getTZPair(i)[i] &lt;= getTZPair(i)[i + 1] </i>.<p>
     * Es ist die Bedingung <i>0 &lt;= i &lt; this.numberOfTZPairs()</i> einzuhalten; anderenfalls wird eine
     * <tt>T3dException</tt> geworfen.<p>
     * @param i Stützpunkt-Index
     * @return zweielementiges Feld mit Werten für t und z(t)
     * @throws T3dException
     */
    abstract public double[] getTZPair(int i) throws T3dException;

    /**
     * liefert den Wert des Stationierungsparameters t für den Startpunkt der Definitionslinie des Profils.<p>
     * @return t-Wert, hier = 0
     */
    public double tStart() {
        return 0.;
    }

    /**
     * liefert den Wert des Stationierungsparameters t für den Endpunkt der Definitionslinie des Profils.<p>
     * @return Länge der Definitionslinie
     */
    public double tEnd() {
    	return mGeom.length();
    }

    /**
     * liefert den Wert des Stationierungsparameters t für den Anfang des Belegungsbereichs des Profils.<p>
     * @return t-Wert &gt;= 0
     */
    abstract public double tMin();

    /**
     * liefert den Wert des Stationierungsparameters t das Ende des Belegungsbereichs des Profils.<p>
     * @return t-Wert &lt;= <tt>this.tEnd()</tt>
     */
    abstract public double tMax();

    /**
     * liefert den minimalen z-Wert des Profils.<p>
     * @return Minimum aller z(t)
     */
    abstract public double zMin();

    /**
     * liefert den maximalen z-Wert des Profils.<p>
     * @return Maximum aller z(t)
     */
    abstract public double zMax();
    
    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das Profil ein atomares Geoobjekt ist, liefert
     * diese Methode stets <i>false</i> als Ergebnis.<p>
     * @return <i>false</i>
     */
    public boolean isCollection() {
        return false;
    }

    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle.<p>
     * @param i (hier stets 0)
     * @return Profil-Objekt selbst
     * @throws org.n52.v3d.triturus.core.T3dException
     */ 
    public VgFeature getFeature(int i) throws T3dException
    {
        if (i != 0) 
            throw new T3dException("Index out of bounds." ); 
        // else:
        return this;
    }
    
    /** 
     * Methode aus der <tt>VgFeature</tt>-Schnittstelle. Da das Profil ein atomares Geoobjekt ist, liefert
     * diese Methode stets 1 als Ergebnis.<p>
     * @return 1
     */
    public int numberOfSubFeatures() {
        return 1;
    }

    abstract public String toString();
}
