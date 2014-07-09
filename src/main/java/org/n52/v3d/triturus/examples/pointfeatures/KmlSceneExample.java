/***************************************************************************************
 * Copyright (C) 2014 by 52 North Initiative for Geospatial Open Source Software GmbH  *
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
package org.n52.v3d.triturus.examples.pointfeatures;

import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.viskml.KmlScene;

/**
 * Triturus example application: Shows how to export points of interest (POIs) to KML.
 *
 * @author Benno Schmidt
 */
public class KmlSceneExample 
{
    public static void main(String args[])
    {
    	KmlSceneExample app = new KmlSceneExample();
        app.run();
    }

    private void run() 
    {
    	KmlScene s = new KmlScene();
    	
    	for (int i = 0; i < 10; i++) {
    		VgPoint p = new GmPoint(7.5 + Math.random(), 51.5 + Math.random(), 0.0);
        	p.setSRS("EPSG:4326");
        	s.add(p);
    	}
   	
    	// TODO Beispiele für addPointFeatures und addSymbols ergänzen
    	
    	s.generateScene("C:\\temp\\test.kml");
    }
}
