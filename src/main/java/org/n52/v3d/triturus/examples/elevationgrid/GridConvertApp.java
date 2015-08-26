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
package org.n52.v3d.triturus.examples.elevationgrid;

import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.core.T3dException;

/** 
 * Triturus example application: Reads an elevation grid from a file and writes it to another file. Source and
 * destination format might vary.
 * @author Benno Schmidt, Martin May
 * @see GridConvert
 */
public class GridConvertApp
{
	public static void main(String args[])
	{
		if (args.length == 0 || args.length > 4) {
			System.out.println(
                    "Usage: java GridConvertApp <filename> [AcGeo|ArcIGrd|BSQ] [<outfilename>] [AcGeo|ArcIGrd|XYZ|AcGeoTIN|Vrml2]");
			return;
		}

        IoElevationGridReader reader = null;
		if (args.length == 1)
			reader = new IoElevationGridReader("ArcIGrd");
		if (args.length >= 2)
			reader = new IoElevationGridReader(args[1]);

		GmSimpleElevationGrid grid;

		try {
			grid = reader.readFromFile(args[0]);

			System.out.println(grid);
            System.out.print("The elevation grid's bounding-box: ");
			System.out.println(grid.envelope().toString());

            for (int j = 0; j < grid.numberOfColumns(); j++) {
                for (int i = 0; i < grid.numberOfRows(); i++) {
                    if (! grid.isSet(i, j))
                        grid.setValue(i, j, 0.0);
                }
            }

			if (args.length > 2) {
				IoElevationGridWriter writer;
				if (args.length==4)
                    writer = new IoElevationGridWriter(args[3]);
				else
                    writer = new IoElevationGridWriter("Vrml2");
				writer.writeToFile(grid,args[2]);
			}
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
    }
}
