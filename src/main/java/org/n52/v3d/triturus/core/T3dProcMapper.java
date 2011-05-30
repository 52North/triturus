package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse f�r Mapper-Objekte. Mapper-Objekte dienen zur Transformation von Geoobjekten in abstrakte,
 * Renderer-unabh�ngige Visualisierungsobjekte (z. B. <tt>VgFeature</tt> -&gt; abstraktes Shape); vgl. Konzept der
 * Visualisierungs-Pipeline.
 * <p>
 * Die Transformation erfolgen in den Implementierungen �ber geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte �bergeben werden und das
 * Ergebnisobjekt Visualisierungsobjekte umfasst.
 * <p>
 * Als spezielle Modellierung f�r abstrakte Visualisierungsobjekte ist z. B. das Paket <tt>org.n52.v3d.triturus.vscene</tt>
 * nutzbar.
 * <p>
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M�glichkeit mit dem Pr�fix "Mp" versehen werden.
 * <p>
 * @see T3dProcFilter
 * @see T3dProcRendererMapper
 * @see IoObject
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class T3dProcMapper
{
    /**
     * protokolliert die durchgef�hrte Transformation.<p>
     */
    abstract public String log();
}