/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
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
package org.n52.v3d.triturus.t3dutil.io;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Helper class to load image formats.<br /><br />
 * <i>German:</i> Hilfsklasse zum Laden von Bildformaten. Verwendung wie IoFeatureLoader.
 * @author Torsten Heinen
 */
public class IoImageLoader extends IoObject {
	public static String TEXTURE_PATH;
		
	public IoImageLoader() {
		TEXTURE_PATH = "testdata/DrapeTextures/";
	}
	
	public IoImageLoader(String texturePath) {
		TEXTURE_PATH = texturePath;		
	}
	
		
	/**
     * loads an image fro a given file.
	 * @param location Ort der Daten, kann eine URL (file:// oder http://) oder ein relativer Dateiname sein (wenn Pfad �ber Konstruktor gesetzt).
	 * @return Buffered image object
	 * @throws T3dException
	 */
    // todo engl. JavaDoc für Parameter
	public BufferedImage loadImage(String location) throws T3dException {
		StringBuffer log = new StringBuffer();
		long sTime = System.currentTimeMillis();		

		String imageLocation = null;
		BufferedImage retImage = null;
		try {
			if (location.startsWith("http:") || location.startsWith("file:")) {
				URL url = new URL( location );
				imageLocation = url.toExternalForm();
				InputStream is = null;
				try {
					is = url.openStream();
					retImage = javax.imageio.ImageIO.read(is);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				//System.out.println("Image loaded!: width="+ textures[i].getWidth() + ", height=" +textures[i].getHeight());	
			}
			else {
				imageLocation = TEXTURE_PATH + location;
				InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(imageLocation); 
		        BufferedReader lDatRead = null;
		        
				try { 
					if(input == null) 
						input =  new FileInputStream(imageLocation); 
					retImage = javax.imageio.ImageIO.read(input);
				} 
				catch( Exception e ){ 
					try{ 
						retImage = javax.imageio.ImageIO.read(new java.io.File(imageLocation)); 
					}
					catch( Exception newE){
						log.append("Image not found: " +imageLocation );
						System.out.println(log.toString());
					}
				}
				
			}
		} catch (MalformedURLException e1) {
			System.out.println(log.toString());
			e1.printStackTrace();
		}
			
		if (retImage == null)
			log.append("<IoImageLoader> WARNING: Unable to load Image "+imageLocation);
		else
			log.append("<IoImageLoader> Image loaded in "+(System.currentTimeMillis()-sTime)+"ms: "+imageLocation);
		
		System.out.println(log.toString());
		return retImage;
	}

	public String log() {
		// TODO Auto-generated method stub
		return null;
	}
}
