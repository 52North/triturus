package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse für Filter-Objekte. Filter-Objekte dienen zur Transformation von Geoobjekten in Geoobjekte
 * (z. B. <tt>VgFeature -&gt; VgFeature</tt>); vgl. Konzept der Visualisierungs-Pipeline.
 * <p>
 * Die Transformation erfolgen in den Implementierungen über geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte übergeben werden und das
 * Ergebnisobjekt ebenfalls Geoobjekte umfasst.
 * <p>
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach Möglichkeit mit dem Präfix "Flt" versehen werden.
 * <p>
 * @see T3dProcMapper
 * @see IoObject
 * @author Benno Schmidt<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 */
abstract public class T3dProcFilter
{
	/**
     * protokolliert die durchgeführte Transformation.<p>
     */
	abstract public String log();
}