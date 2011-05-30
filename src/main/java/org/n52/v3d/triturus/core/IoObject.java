package org.n52.v3d.triturus.core;

/**
 * Abstrakte Basisklasse für I/O-Objekte. I/O-Objekte ermöglichen den lesenden und schreibender Zugriff auf externe
 * Datenquellen. Resultat sind Objekte vom Typ <tt>VgFeature</tt>; vgl. Konzept der Visualisierungs-Pipeline.<p>
 * @see T3dProcFilter
 * @see T3dProcMapper
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class IoObject 
{
	/** protokolliert die durchgeführte Transformation. */
	abstract public String log();
}