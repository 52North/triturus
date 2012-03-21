package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dProcFilter;

/**
 * @deprecated
 * Filter class to integrate polygonal geometries into a TIN (geometric intersection).<br /><br />
 * <i>German:</i> Filter-Klasse zum Einbau einer Polygon-Geometrie in ein TIN (geometrische Verschneidung).
 * @author Martin May, Ilja Abramovic
 */
public class FltTINPolygonAssembler extends T3dProcFilter
{
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * return the TIN that results from the intersection operation.<br /><br />
     * <i>Sorry, this method has not been implemented yet...</i>
	 * <i>German:</i> liefert das TIN, das Ergebnis der Verschneidung ist.<p>
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
