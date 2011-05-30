package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;

/**
 * Filter-Klasse zum Einbau einer Polygon-Geometrie in ein TIN (geometrische Verschneidung).<p>
 * @author Martin May, Ilja Abramovic<br>
 * (c) 2003-2004, Institute for Geoinformatics<br>
 */
public class FltTINPolygonAssembler extends T3dProcFilter
{
	/**
	 * protokolliert die durchgeführte Transformation.
	 * @see org.n52.v3d.triturus.core.T3dProcMapper#log()
	 */
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * liefert das TIN, das Ergebnis der Verschneidung ist.<p>
	 * @param pTIN TIN, in dessen Geometrie die Polygon-Geometrie eingebaut werden soll.
	 * @param pPolygon einzubauende Geometrie
   	 * @return TIN-Geometrie
	 */
	public GmSimpleTINGeometry transform(GmSimpleTINGeometry pTIN, GmPolygon pPolygon)
    {
        // todo
        return null;
	}
}
