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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridReader;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;

/** 
 * Triturus example application: Reads an elevation grid in ArcInfo ASCII grid format and writes it to a VRML file.
 * @author Benno Schmidt
 * @see GridConvertApp
 */
public class GridConvert
{
	public static void main(String args[])
	{
        IoElevationGridReader reader = new IoElevationGridReader("ArcIGrd");

		try {
            // Read the elevation grid from file:
			GmSimpleElevationGrid grid = reader.readFromFile("/data/example_dem.asc");

            // This is just some control output:
			System.out.println(grid);
            System.out.print("The elevation grid's bounding-box: ");
			System.out.println(grid.envelope().toString());

            // If some grid cell's have NODATA values, assign a value...
            for (int j = 0; j < grid.numberOfColumns(); j++) {
                for (int i = 0; i < grid.numberOfRows(); i++) {
                    if (! grid.isSet(i, j))
                        grid.setValue(i, j, 0.0);
                }
            }

            // Write VRML output:
			IoElevationGridWriter writer = new IoElevationGridWriter("Vrml2");
    		writer.writeToFile(grid, "/data/example_dem.wrl");
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
    }
}
