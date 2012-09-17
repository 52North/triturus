/***************************************************************************************
 * Copyright (C) 2012 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
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
