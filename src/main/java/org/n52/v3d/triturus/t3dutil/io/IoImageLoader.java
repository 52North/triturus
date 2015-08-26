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
