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
 * Hilfsklassen zum Laden von Bildformaten. Verwendung wie IoFeatureLoader
 *
 * @author Torsten Heinen
 * Copyright (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
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
	 * laden einer Bilddatei.
	 * 
	 * @param location Ort der Daten, kann eine URL (file:// oder http://) oder ein relativer Dateiname sein (wenn Pfad über Konstruktor gesetzt).
	 * @return
	 * @throws T3dException
	 */
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
	
	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.core.IoObject#log()
	 */
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}
}
