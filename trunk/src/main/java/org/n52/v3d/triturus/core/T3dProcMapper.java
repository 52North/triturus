package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse für Mapper-Objekte. Mapper-Objekte dienen zur Transformation von Geoobjekten in abstrakte,
 * Renderer-unabhängige Visualisierungsobjekte (z. B. <tt>VgFeature</tt> -&gt; abstraktes Shape); vgl. Konzept der
 * Visualisierungs-Pipeline.
 * <p>
 * Die Transformation erfolgen in den Implementierungen über geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte übergeben werden und das
 * Ergebnisobjekt Visualisierungsobjekte umfasst.
 * <p>
 * Als spezielle Modellierung für abstrakte Visualisierungsobjekte ist z. B. das Paket <tt>org.n52.v3d.triturus.vscene</tt>
 * nutzbar.
 * <p>
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach Möglichkeit mit dem Präfix "Mp" versehen werden.
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
     * protokolliert die durchgeführte Transformation.<p>
     */
    abstract public String log();
}