/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
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
