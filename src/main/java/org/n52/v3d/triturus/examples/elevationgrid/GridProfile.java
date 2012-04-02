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
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgProfile;

/**
 * Triturus example application: Generates a cross-section for a given elevation grid.
 * @author Benno Schmidt
 * @see GridProfileApp
 */
public class GridProfile
{
	public static void main(String args[])
	{
        // Read the elevation grid from file:
		IoElevationGridReader reader = new IoElevationGridReader("ArcIGrd");
		GmSimpleElevationGrid grid = null;
		try {
			grid = reader.readFromFile("/data/example_dem.asc");
		}
		catch (T3dException e) {
			e.printStackTrace();
		}

        // This is just some control output:
    	System.out.println(grid);
        System.out.print("The elevation grid's bounding-box: ");
		System.out.println(grid.envelope().toString());

        // Give definition-line (sequence of x, y, z coordinates, z will be ignored):
		VgLineString defLine = new GmLineString("2670740,5811200,0,2670700,5811000,0");
		System.out.println(defLine); // control output
		
		// Generate cross-section:
		FltElevationGrid2Profile proc = new FltElevationGrid2Profile();
		VgProfile prof = proc.transform(grid, defLine);

		// Cross-section output...
		// to console:
        for (int i = 0; i < prof.numberOfTZPairs(); i++)
            System.out.println((prof.getTZPair(i))[0] + ", " + (prof.getTZPair(i))[1]);
        // to SVG:
        System.out.println("Writing SVG-file...");
        IoProfileWriter lWriter = new IoProfileWriter("SVG");
        lWriter.writeToFile(prof, "/temp/cross-sec-1.svg");
        // to ASCII-file:
        System.out.println("Exporting to ASCII-file...");
        lWriter.setFormatType("AcGeo");
        lWriter.writeToFile(prof, "/temp/cross-sec-1.prf");
    }
}
