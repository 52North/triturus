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

import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.gisimplm.GmSimpleElevationGrid;
import org.n52.v3d.triturus.gisimplm.IoElevationGridWriter;
import org.n52.v3d.triturus.vgis.VgPoint;

/**
 * Triturus example application: Generates a very simple elevation grid and writes it to a VRML file named
 * "hello_world.wrl" in the working directory.
 * @author Benno Schmidt
 */
public class HelloWorld
{
	public static void main(String args[])
	{
        // Construct grid of size 10 x 10:
        VgPoint orig = new GmPoint(0, 0, 0);
        GmSimpleElevationGrid grid = new GmSimpleElevationGrid(10, 10, orig, 100., 100.);

        // Assign some elevation values:
        for (int j = 0; j < grid.numberOfColumns(); j++) {
            for (int i = 0; i < grid.numberOfRows(); i++) {
                grid.setValue(i, j, 100. + 10. * Math.random() - 5. * (Math.abs(i - 5) + Math.abs(j - 5)) );
            }
        }
        System.out.println(grid.minimalElevation());
        System.out.println(grid.maximalElevation());

        // Write VRML output:
    	IoElevationGridWriter writer = new IoElevationGridWriter(IoElevationGridWriter.VRML2);
    	writer.writeToFile(grid, "hello_world.wrl");
    }
}

