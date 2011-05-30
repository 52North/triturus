package org.n52.v3d.triturus.t3dutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Ermittlung hypsometrischer Farbwerte. Die Zuordnung basiert auf einer Farbtabelle, die verschiedenen
 * Höhen jeweils einen Farbwert zugeordnet. Die durch die Methode <tt>transform()</tt> abgefragten Farbwerte werden
 * (wenn nötig) mittels linearer Interpolation bestimmt.<p>
 * Die Farbtabellen können bis jetzt nur durch das Laden von Dateien im GMT-konformen Farbtabellen-Format erzeugt
 * werden. (TODO addDivision(height, color) Methoden...)<p>
 * Farbtabelle RGB -> Interpolation in RGB -> Farbwert RGB
 * Farbtabelle HSV -> Interpolation in HSV -> Farbwert HSV
 * 
 * Werden minimale und maximale Höhen im Konstruktor angegeben, dann wird die Farbtabelle 
 * auf diese genormt und die Transformation liefert relative und keine absoluten Farbwerte.
 * 
 * @see org.n52.v3d.triturus.t3dutil.MpSimpleHypsometricColor
 * @author Torsten Heinen
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class MpGMTHypsometricColor extends MpHypsometricColor
{
    private String mLogString = "";

    private final int cArrayInitSize = 10;
    private ArrayList mColors;
	private double[] mHeights;

	private String mFilename ="";
	private String mDescription="";
	private String mColorSpace="";
	private String mInterpolationSpace="";	

	private double mMinElevation = 0, mMaxElevation = 0; // min/max Höhen aus der Datei
	private double mRelMinElevation = 0, mRelMaxElevation = 0; // min/max der relativen Höhen
	
    /** Konstruktor. */
    public MpGMTHypsometricColor() {
        mLogString = this.getClass().getName();
        mColors = new ArrayList(cArrayInitSize);
    }

    /**
     * Konstruktor. Als Parameter ist die Datei mit den Farbzuordnungen anzugeben. Transformiert wird dann mit
     * absoluten Werten, also wie in der Datei angegeben.<p>
     * @param pMapFile Dateiname mit Pfadangabe
     * @see MpHypsometricColor#setColorMapFile
     */
    public MpGMTHypsometricColor(String pMapFile) {
        mLogString = this.getClass().getName();
        mColors = new ArrayList(cArrayInitSize);
        this.setColorMapFile(pMapFile);
    }

    /**
     * Konstruktor. Als Parameter sind die Datei mit den Farbzuordnungen sowie die minimale und maximale Höhe für die
     * relative Transformation anzugeben.<p>
     * Bei der relativer Transformation werden die Höhen in der Datei (min bzw. max) auf die 
     * übergebenen Höhen genormt.
     * @param pMapFile Dateiname mit Pfadangabe
     * @param pRelMinElevation minimaler Höhenwert
     * @param pRelMaxElevation maximaler Höhenwert
     * @see MpHypsometricColor#setColorMapFile
     */
    public MpGMTHypsometricColor(String pMapFile, String pInterpolationSpace, double pRelMinElevation, double pRelMaxElevation) {
        mLogString = this.getClass().getName();
        mColors = new ArrayList(cArrayInitSize);
        this.setColorMapFile(pMapFile);
        mRelMinElevation = pRelMinElevation;
        mRelMaxElevation = pRelMaxElevation;
        setInterpolationSpace(pInterpolationSpace);
    }    

    /** protokolliert die durchgeführte Operation. */
    public String log() {
        return mLogString;
    }

    /**
     * Lädt eine Farbtabelle aus der angegebenen Datei. Bisher werden nur GMT-konforme Farbtabellen unterstützt
     * (http://gmt.soest.hawaii.edu)<p>
     * Tritt ein Fehler beim Laden auf, wird <i>false</i> als Ergebnis zurückgegeben; eine Transformation würde im
     * Weiteren zu einem Fehler führen! (TODO!)<p>
     * @param pFilename Den zu ladenen Farbtabelle
     * @return <i>true</i>, falls Laden der Tabelle erfolgreich, sonst <i>false</i>.
     */
    public boolean setColorMapFile(String pFilename) {
       mFilename = pFilename;
       mInterpolationSpace = mColorSpace;
       if (loadGMTColorFile(mFilename)) {
        mInterpolationSpace = mColorSpace; //"HSV" oder "RGB"
        return true;
       }	
       else 
       	return false;
    }

    /**
     * todo
     * @param min
     * @param max
     */
    public void setRelativeMinMax(double min, double max) {
        mRelMinElevation = min;
        mRelMaxElevation = max;        
    }

    /**
     * todo
     * @param pInterpolationSpace
     */
    public void setInterpolationSpace(String pInterpolationSpace) {
		if (pInterpolationSpace.equalsIgnoreCase("HSV") || pInterpolationSpace.equalsIgnoreCase("RGB"))
			mInterpolationSpace = pInterpolationSpace;
		else
			throw new T3dException("RGB and HSV color-space are supported only...");
    }
	 
    /**
     * liefert die zu einem Höhenwert gehörige Farbe. Die Farbwerte werden durch lineare Interpolation basierend auf
     * der zuvor gesetzten Farbtabelle ermittelt.<p>
     * Basiert die Farbtabelle auf RGB-Werten, so wird auch im RGB-Farbraum interpoliert. Die zurückgegebene
     * Farbinformation ist dann im RGB-Farbraum angegeben. Entsprechendes gilt für HSV-Farbtabellen.<p>
     * @param pElevation Höhenwert
     * @return der Höhe zugewiesener Farbwert
     */
    public T3dColor transform(double pElevation)
    {
    	double relative=0;
    	
    	//System.out.print("Transform: " + pElevation + " -> ");
    	if (mRelMinElevation - mRelMaxElevation != 0.0) {
    		relative = (pElevation - mRelMinElevation) / (mRelMaxElevation - mRelMinElevation);
    		pElevation = ((mMaxElevation - mMinElevation) * relative) + mMinElevation;
    	}
    	//System.out.println("Transform: " + mMinElevation +  " " + mMaxElevation);
    	//System.out.print(pElevation +  " " + relative);
    	for (int i = 0; i < mHeights.length; i++) {
    		if (mHeights[i] == pElevation)
    			return (T3dColor) mColors.get(i);
    	}
    	return this.interpolate(pElevation);
    }
        
    /**
     * Ermittelt den Farbwert zu der angegebene Höhe durch lineare 
     * Interpolation zwischen der Farbtabelle. Die Farbtabelle muss 
     * zuvor mit setColorFile(filename) erfolgreich ausgelesen worden sein.
     * <p>Höhen, die ausserhalb der eingelesenden Werte liegen werden an
     * die minimalen bzw. maximalen Werte angepasst.
     * 
	 * @param elevation
	 * @return Die interpolierte Farbe
	 */
	private T3dColor interpolate(double elevation) {
		int midIndex = findKeyIndex(elevation);
		if (midIndex < 0)
			return (T3dColor) mColors.get(0);
		else if (midIndex >= (mHeights.length-1)) {
			return (T3dColor) mColors.get(mHeights.length-1);
		}

		double height0 = mHeights[midIndex];
		T3dColor color0 = (T3dColor) mColors.get(midIndex);

		double height1 = mHeights[midIndex+1];
		T3dColor color1 = (T3dColor) mColors.get(midIndex+1);
		
		float 	x0=0, x1=0, x_dist=0,
				y0=0, y1=0, y_dist=0,
				z0=0, z1=0, z_dist=0;

		if (mInterpolationSpace.equalsIgnoreCase("HSV")) {
			x0 = color0.getHue();
			x1 = color1.getHue();
	        // if both are NaN do nothing
	        if(Float.isNaN(x0) && !Float.isNaN(x1))
	            x0 = x1;
	        else if(!Float.isNaN(x0) && Float.isNaN(x1))
	        	x1 = x0;
			
			y0 = color0.getSaturation();
			y1 = color1.getSaturation();
			
			z0 = color0.getValue();
			z1 = color1.getValue();
		}
		else {
			x0 = color0.getRed();
			x1 = color1.getRed();
			
			y0 = color0.getGreen();
			y1 = color1.getGreen();
			
			z0 = color0.getBlue();
			z1 = color1.getBlue();
			
		}
		x_dist = x1-x0;
		y_dist = y1-y0;
		z_dist = z1-z0;
	
//		 Lineare Interpolation 
		double fraction = 0;
		if ( height0 - height1 != 0) {
			fraction = (elevation - height0) / (height1 - height0);
		}
		
		return new T3dColor( mColorSpace,
							(float)(x0 + fraction * x_dist),
							(float)(y0 + fraction * y_dist),
							(float)(z0 + fraction * z_dist) );
	}
	
//	Hilfsvariablen
	private double[] tHeights;
	private int tNumHeights = 0;
	/** 
	 * Hilfmethode zum Speichern der Farbwerte in die beiden 
	 * Daten Arrays (mHeights & mColors).
	 * Konvertiert RGB[0..255] -> [0..1], HSV wird übernommen (gmt werte in grad, T3DColor???)
	 */
	private void put(double height, double rORh, double gORs, double bORv) {
		if (tHeights==null)
			tHeights = new double[cArrayInitSize];
		if (tNumHeights >= tHeights.length-1) {
			double[] temp = new double[tHeights.length*2];
			System.arraycopy(tHeights, 0, temp, 0, tHeights.length);
			tHeights = temp;
		}
		if (mMinElevation>height) mMinElevation = height;
		if (mMaxElevation<height) mMaxElevation = height;
		tHeights[tNumHeights] = height;
		if (mColorSpace.equalsIgnoreCase("RGB")) {
			mColors.add(	new T3dColor("RGB", 
							(float)rORh/255, 
							(float)gORs/255, 
							(float)bORv/255));
		}
		else {
			mColors.add(	new T3dColor("HSV", 
							(float)rORh, 
							(float)gORs, 
							(float)bORv));
		}
		
		tNumHeights++;
	}
    
    public String toString() {
    	if (mHeights == null)
    		return "MpHypsometricColor == Empty";
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("Filename: " + mFilename + "\n");
    	buffer.append("Description: " + mDescription + "\n");    	
    	buffer.append("ColorSpace: " + mColorSpace + "\n");
    	for (int i=0;i<mHeights.length; i++) {
    		buffer.append("Height["+i+"]=" + mHeights[i]);
    		buffer.append(" <Color: " + ((T3dColor)mColors.get(i)).toString() + ">\n");
    	}
    	return buffer.toString();
    }
    
	/** 
	 * Liest eine GMT-Farbelle aus der angegebenen Datei. <p>
	 * Eigene Farbtabelle können einfach erstellt und benutzt werden, wenn sie dem
	 * folgendem Format entsprechen:<p>
	 * Es müssen zwei beliebige Header-Zeilen existieren (werden aber 
	 * nicht mit gespeichert)! Ab der dritten Zeile kann dann optional eine Beschreibung 
	 * folgen. Nach der Beschreibung muss "COLOR_MODEL" gefolgt von "RGB" oder "HSV" folgen. 
	 * Danach die Farbtabelle im Format "von_Höhe X Y Z bis_Höhe X Y Z", wobei X, Y und Z die 
	 * Farbwert im zugehörigen "COLOR_MODEL" sind, also X=R oder H, Y=G oder S und Z=B oder V.<p>
	 * 
	 * Achtung: In GMT-Tabellen werden nur die Höhen- und Farbwerte werden ausgelesen! 
	 * Einige Bezeichnung in dem Format sind mir nicht ganz klar:
	 * 1) Am Ende von RGB-Dateien:
	 * 		F	255	255	255				
	 *		B	0	0	0
	 *		N	128	128	128
	 * 2) In den HSV-Dateien:
	 * - Ein U oder L nach einem Wertepaar: 3000    315     0.2     1       U
	 * - Am Ende der Datei:
	 * 		B       255     0.6     1
	 * 		F       315     0.2     1
	 */
	private boolean loadGMTColorFile(String pFilename) {
        InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename); 
        BufferedReader lDatRead = null;     
        StreamTokenizer tokenizer = null;
        try { 
        	//System.out.println("Loading ColorFile: "+input);        	
        	if(input == null) 
        		input =  new FileInputStream(pFilename); 
        	lDatRead = new BufferedReader(new InputStreamReader(input));
        	tokenizer= new StreamTokenizer(lDatRead);
        } 
        catch( Exception e){ 
        	//System.out.println("LoadingStreamFailed: "+pFilename);
        	return false;
        } 
        
  		double tempheight = 0, tempheight2 = 0, temp1 = 0, temp2 = 0, temp3 = 0, temp4 = 0, temp5 = 0, temp6 = 0;
		try {
			tokenizer.lowerCaseMode(false);
			tokenizer.wordChars('_','_');
			tokenizer.wordChars(' ',' ');
			tokenizer.wordChars(',',',');
			tokenizer.ordinaryChar('#');
			tokenizer.eolIsSignificant(true);
			
			//id aus der ersten zeile überlesen
			tokenizer.nextToken();
			while (tokenizer.ttype != StreamTokenizer.TT_EOL) {
					tokenizer.nextToken();					
			}

			//zweite (leere) zeile auslesen
			tokenizer.nextToken();
			while (tokenizer.ttype != StreamTokenizer.TT_EOL) {
				tokenizer.nextToken();
			}
								
			//description auslesen bis COLOR_MODEL gelesen worden ist
			boolean colorspaceFound=false; 
			while (!colorspaceFound) {	
				tokenizer.nextToken();				
				if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
					if (tokenizer.sval.equals("RGB") || tokenizer.sval.equals("HSV")) {
						if (tokenizer.sval.equalsIgnoreCase("HSV")) {
							mColorSpace = "HSV";
						}
						else { 
							mColorSpace = "RGB";
						}
						colorspaceFound = true;	
					}
					else if (!tokenizer.sval.equalsIgnoreCase("COLOR_MODEL ")) {
						mDescription = mDescription + tokenizer.sval;
					}
				}
				else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
					mDescription = mDescription + String.valueOf(tokenizer.nval);
				}		

				else if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
					//description += '\n';
					mDescription += ' ';
				}
			} 
			tokenizer.nextToken(); //EOL auslesen
					
			//jetzt die höhen und farbwerte
			while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
				//so lange zahlen auslesen, bis eine nummer gelesen wird
				tokenizer.nextToken();
				if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
					tempheight = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp1 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp2 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp3 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					tempheight2 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp4 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp5 = tokenizer.nval;
					tokenizer.nextToken(); //next number
					temp6 = tokenizer.nval;
					//System.out.println("ADDING: " + tempheight+" "+ temp1+" "+ temp2+" "+ temp3);
					put(tempheight, temp1,temp2,temp3);
				}
				else if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
					//wenn B/F/N angaben folgen, dann ignorier sie...
					//B F S einlesen not implemented!
					if (tokenizer.sval.equals("B") || tokenizer.sval.equals("F") || tokenizer.sval.equals("N")) {
						while (tokenizer.ttype != StreamTokenizer.TT_EOL) {
								tokenizer.nextToken();					
						}
					}
				}
				else if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
					//save the last label
					//System.out.println("ADDING: " + tempheight2+" "+ temp4+" "+ temp5+" "+ temp6);
					put(tempheight2, temp4,temp5,temp6);
					//Array anpassen...
					mHeights = new double[tNumHeights];
					System.arraycopy(tHeights,0,mHeights,0,tNumHeights);
					tHeights = null; tNumHeights = 0;
					lDatRead.close();
					return true;
				}						
															
				
			}//END OF FILE
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
					
	}

    /**
     * 
     * Hilfsmethode zum Auffinden des mittleren Index aus mHeights.
     * Basiert auf J3D.org Interpolator (org.j3d.util.interpolator.Interpolator)
     * 
     * Find the key in the array. Performs a fast binary search of the values
     * to locate the right index.  Returns the index i such that
     * key[i]<key<=key[i+1].  If the key is less than or equal to all
     * keys, returns -1.
     * The binary search is O(log n).
     *
     * @param key The key to search for
     * @return The index i such that key[i]<key<=key[i+1].
     */
    private int findKeyIndex(double key) {
        // some special case stuff - check the extents of the array to avoid
        // the binary search
        if((mHeights.length == 0) || (key <= mHeights[0]))
            return -1;
        else if(key == mHeights[mHeights.length - 1])
            return mHeights.length - 1;
        else if(key > mHeights[mHeights.length - 1])
        // REVISIT - this return value is an exception from the general
        // pattern.  I think currentSize-1 would make more sense here.
        // I wont change it since it appears to be working.
        // [GC 21-Oct-2002]
            return mHeights.length;

        int start = 0;
        int end = mHeights.length - 1;
        int mid = mHeights.length >> 1;  // identical to (start + end + 1) >> 1

        // Non-recursive binary search.
        // Searches for the largest i such that keys[i]<key.
        // Differs a little from a classical binary search
        // in that we cannot discard the middle value from
        // the search when key>keys[mid] (because keys[mid] may
        // turn out to be the best solution, and we cannot
        // terminate when key==keys[mid] (because there may be
        // more than one i with keys[i]==key, and we must find the
        // first one.
        // Round up when computing the new mid value to avoid
        // a possible infinite loop with start==mid<end.

        while(start < end)
        {
            double test = mHeights[mid];

            if(test >= key)
                end = mid - 1;
            else
                start = mid;     // note we don't exclude mid from range

            // We recompute mid at the end so that
            // it is correct when loop terminates.
            // Note that we round up.  This is required to avoid
            // getting stuck with mid==start.
            mid = (start + end + 1) >> 1;
        }

        return mid;
    }
}