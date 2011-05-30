package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse für Renderer-Mapper-Objekte. Renderer-Mapper dienen zur Transformation von abstrakten,
 * Renderer-unabhängigen Visualisierungsobjekten in (z. B. abstraktes Shape -&gt; Java 3D-Shape); vgl. Konzept der
 * Visualisierungs-Pipeline.
 * <p>
 * Die Transformation erfolgen in den Implementierungen über geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> abstrakte, Renderer-unabhängige
 * Visualisierungsobjekte übergeben werden und das Ergebnisobjekt Renderer-epszifische Visualisierungsobjekte umfasst.
 * <p>
 * Als spezielle Modellierung für abstrakte Visualisierungsobjekte ist z. B. das Paket <tt>org.n52.v3d.triturus.vscene</tt>
 * nutzbar.
 * <p>
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach Möglichkeit mit dem Präfix "Mpr" versehen werden.
 * <p>
 * @see T3dProcMapper
 * @see T3dProcFilter
 * @see IoObject
 * @author Benno Schmidt und Torsten Heinen<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class T3dProcRendererMapper
{
	/** protokolliert die durchgeführte Transformation. */
	abstract public String log();
}