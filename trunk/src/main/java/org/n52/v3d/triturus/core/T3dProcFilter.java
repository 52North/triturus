package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse f�r Filter-Objekte. Filter-Objekte dienen zur Transformation von Geoobjekten in Geoobjekte
 * (z. B. <tt>VgFeature -&gt; VgFeature</tt>); vgl. Konzept der Visualisierungs-Pipeline.
 * <p>
 * Die Transformation erfolgen in den Implementierungen �ber geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte �bergeben werden und das
 * Ergebnisobjekt ebenfalls Geoobjekte umfasst.
 * <p>
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M�glichkeit mit dem Pr�fix "Flt" versehen werden.
 * <p>
 * @see T3dProcMapper
 * @see IoObject
 * @author Benno Schmidt<p>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics
 */
abstract public class T3dProcFilter
{
	/**
     * protokolliert die durchgef�hrte Transformation.<p>
     */
	abstract public String log();
}