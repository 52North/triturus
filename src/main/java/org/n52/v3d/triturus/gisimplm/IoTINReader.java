package org.n52.v3d.triturus.gisimplm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.net.MalformedURLException;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Einlesen von TINs (Typ <tt>GmSimpleTINFeature</tt>) aus Dateien.<p>
 * @author Benno Schmidt und Martin May<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoTINReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private GmSimpleTINFeature mTIN = null;

    /** 
     * Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterstützt, wird später während
     * des Lesevorgangs eine Ausnahme geworfen.<p>
     * Es werden die folgenden Formate unterstützt:
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO-Format</li>
	 * <li><i>Vrml2:</i> VRML 2</li>
	 * <li><i>ArcTIN:</i> ESRI-TIN-Format</li>
	 * <li><i>GMT_TIN:</i> ...</li>
     * </ul>
     * Bem.: 1. Der Reader für das Format <i>VRML2</i> ist noch methodisch zu testen: Was passiert, wenn mehrere TINs
     * in VRML-Datei (z. B. pro Farbe ein Teil-TIN, Gebäude als TIN, ...)? 2. Ist der <i>ArcTIN</i>-Reader wirklich ein
     * Arc- bzw. ESRI-Format? 3. Ist der <i>GMT_TIN</i>-Reader getestet? Was für ein Format ist das?<p>
     * @param pFormat Format-String, z. B. "AcGeo"
     */
    public IoTINReader(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType( pFormat );
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /** 
     * setzt den Formattyp.<p>
     * @param pFormat Dateityp (z. B. "AcGeo")
     */
    public void setFormatType(String pFormat)
    {
        mFormat = pFormat;
    }

    /**
     * @deprecated
     * liest ein TIN aus einer Datei ein. Wird der spezifizierte Formattyp nicht unterstützt, wirft die Methode eine
     * <tt>T3dNotYetImplException</tt>.<p>
     * @param pFilename Pfad, unter dem die Datei abgelegt ist
     * @return TIN (im Fehlerfall <i>null</i>)
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public GmSimpleTINFeature readFromFile(String pFilename)
    	throws T3dException, T3dNotYetImplException
    {
        return read(pFilename);
    }

    /**
     * liest ein TIN aus einer Datei ein oder über die Angabe eines URLs ein. Wird der spezifizierte Formattyp nicht
     * unterstützt, wirft die Methode eine <tt>T3dNotYetImplException</tt>.<p>
     * @param pLocation Pfad, unter dem die Datei abgelegt ist, oder ein gültiger URL.
     * @return TIN (im Fehlerfall <i>null</i>)
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public GmSimpleTINFeature read(String pLocation)
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("AcGeo")) i = 1;
        if (mFormat.equalsIgnoreCase("Vrml2")) i = 2;
		if (mFormat.equalsIgnoreCase("ArcTIN")) i = 3;
		if (mFormat.equalsIgnoreCase("GMT_TIN")) i = 4;
        // --> hier ggf. weitere Typen ergänzen...

        BufferedReader reader = null;
        try {
        	if (pLocation.startsWith("http"))
				reader = createBufferedReader(new URL(pLocation));
			else
        		reader = createBufferedReader(pLocation);

        	switch (i) {
                case 1: this.readAcadGeoTIN(reader, pLocation); break;
                case 2: this.readVRML2(pLocation); break;
                case 3: this.readARCTin(pLocation); break;
                case 4: String xyzFilename="", tinFilename="";
					    if (pLocation.endsWith("tin")) {
							tinFilename = pLocation;
							int dot = tinFilename.lastIndexOf(".");
							xyzFilename = tinFilename.substring(0, dot);
							xyzFilename = xyzFilename + ".xyz";
						}
						//System.out.println("Load GMT file: " + xyzFilename+ " "+ tinFilename);
						this.readGMTTin(xyzFilename, tinFilename);
						break;
                // --> hier ggf. weitere Typen ergänzen...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        } catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

        return mTIN;
    }

    private BufferedReader createBufferedReader(URL url) {
		InputStream is = null;
		try {
			is = url.openStream();
			return new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
        	System.out.println("<TINReader> Loading failed: "+url);
			e.printStackTrace();
			return null;
		}
    }

    private BufferedReader createBufferedReader(String pFilename) {
        InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename);
        BufferedReader lDatRead = null;

        try {
        	//System.out.println("LoadingStream: "+input);
        	if(input == null)
        		input =  new FileInputStream(pFilename);
        	lDatRead = new BufferedReader(new InputStreamReader(input));
        }
        catch( Exception e){
        	System.out.println("<TINReader> Loading failed: "+pFilename);
        	e.printStackTrace();
			return null;
        }
        return lDatRead;
    }

    private void readAcadGeoTIN(BufferedReader pDatRead, String pFilename) throws T3dException
    {
        String line = "";
        int lineNumber = 0;
             
        try {
            String tok1, tok2, tok3;

            line = pDatRead.readLine(); // line 1
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("TINBEGIN")) 
                throw new T3dException("Expected key-word TINBEGIN in \"" + pFilename + "\":" + lineNumber);

            line = pDatRead.readLine(); // line 2
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("FORMAT")) 
                throw new T3dException("Expected key-word FORMAT in \"" + pFilename + "\":" + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            boolean refPresent = false;
            if (tok2.equalsIgnoreCase("R=ON"))
                refPresent = true;    
            tok3 = this.getStrTok(line, 3, " ");
            boolean colPresent = false;
            if (tok3.equalsIgnoreCase("C=ON"))
                colPresent = true;    

            if (colPresent) 
                throw new T3dException("Coloured AcadGeo-TINs are not supported (yet)."); 
            if (refPresent) 
                throw new T3dException("TINs holding full topology information are not supported (yet)."); 
           
            line = pDatRead.readLine(); // line 3
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("TIN:")) 
                throw new T3dException("Expected key-word TIN: in \"" + pFilename + "\":" + lineNumber);

            line = pDatRead.readLine(); // line 4
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("POINTS")) 
                throw new T3dException("Expected key-word POINTS in \"" + pFilename + "\":" + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            int nPoints = this.toInt(tok2);

            // Konstruktion des TINs:

            mTIN = new GmSimpleTINFeature();
            GmSimpleTINGeometry lTINGeom = (GmSimpleTINGeometry) mTIN.getGeometry();
            lTINGeom.newPointList(nPoints);

            // Belegen der TIN-Punkte:

            double x, y, z;
            //mTIN.setBoundsInvalid(); // Performanz!

            for (int i = 0; i < nPoints; i++) {
                line = pDatRead.readLine();
                lineNumber++;
                tok1 = this.getStrTok(line, 1, " ");
                x = this.toDouble(tok1);
                tok2 = this.getStrTok(line, 2, " ");
                y = this.toDouble(tok2);
                tok3 = this.getStrTok(line, 3, " ");
                z = this.toDouble(tok3);
                lTINGeom.setPoint(i, new GmPoint( x, y, z ));
            }

            line = pDatRead.readLine();
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("TRIANGLES")) 
                throw new T3dException("Expected key-word TRIANGLES in \"" + pFilename + "\":" + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            int nTriangles = this.toInt(tok2);

            lTINGeom.newTriangleList(nTriangles);

            // Dreiecksvermaschung:

            int i1, i2, i3;

            for (int i = 0; i < nTriangles; i++) {
                line = pDatRead.readLine();
                lineNumber++;
                tok1 = this.getStrTok(line, 1, " ");
                i1 = this.toInt(tok1);
                tok2 = this.getStrTok(line, 2, " ");
                i2 = this.toInt(tok2);
                tok3 = this.getStrTok(line, 3, " ");
                i3 = this.toInt(tok3);
                lTINGeom.setTriangle(i, i1, i2, i3);
            }

            line = pDatRead.readLine(); // letzte Zeile
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("END")) 
                throw new T3dException("Expected key-word END in \"" + pFilename + "\":" + lineNumber);

            pDatRead.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
        }
    } // readAcadGeoTIN()

    // private Helfer, die in readAcadGeoTIN() benötigt werden:

    // Extraktion des i-ten Tokens (i >= 1!, i max. = 4) aus einem String ('pSep' als Trenner):
    private String getStrTok(String pStr, int i, String pSep) throws T3dException
    {
        ArrayList lStrArr = new ArrayList(); 
        lStrArr.add(pStr);
        int i0 = 0, i1 = 0, k = 0;
        while (k < 4 && i1 >= 0) {
           i1 = pStr.indexOf(pSep, i0);
           if (i1 >= 0) {
               if (k == 0)
                   lStrArr.set(0, pStr.substring( i0, i1 ));
               else
                   lStrArr.add(pStr.substring( i0, i1 ));
               i0 = i1 + 1;
               k++;
           }
        }
        if (k <= 3)
            lStrArr.add( pStr.substring(i0) );
        if (i < 1 || i > 4)
            throw new T3dException("Logical parser error.");
        return (String) lStrArr.get(i - 1);
    } 

    // Konvertierung String -> Gleitpunktzahl:
    private double toDouble(String pStr) 
    {
        return Double.parseDouble(pStr);
    } 

    // Konvertierung String -> Ganzzahl:
    private int toInt(String pStr)
    {
        return Integer.parseInt(pStr);
    } 

	// Quick-and-dirty-Implementierung für VRML 2-Import: Versuche das erste IndexedFaceSet zu finden und hole es...
	// Das muss natürlich GeoVRML sein... :-]
	private void readVRML2(String pFilename) throws T3dException
    {
		int lineNumber = 0;
		InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename); 
		BufferedReader lDatRead = null;
		
		try { 
//			System.out.println("LoadingStream: "+input);        	
			if(input == null) 
				input =  new FileInputStream(pFilename); 
			lDatRead = new BufferedReader(new InputStreamReader(input));
		} 
		catch( Exception e){ 
			System.out.println("LoadingStreamFailed: "+pFilename);
		} 
		try {
//			FileReader lFileRead = new FileReader(pFilename);
//			BufferedReader lDatRead = new BufferedReader(lFileRead);
			StreamTokenizer lTokRead = new StreamTokenizer(lDatRead);
			lTokRead.lowerCaseMode(true);//alle Tokens in Kleinbuchstaben holen
			lTokRead.commentChar(35);//Hash signalisiert Kommentar-Zeile...
			lTokRead.eolIsSignificant(false);//Zeilenumbruch unwichtig
			lTokRead.wordChars(65,90);//Großbuchstaben
			lTokRead.wordChars(97,122);//Kleinbuchstaben
//			System.out.println("EOF: " + StreamTokenizer.TT_EOF);
//			System.out.println("EOL: " + StreamTokenizer.TT_EOL);
//			System.out.println("NUM: " + StreamTokenizer.TT_NUMBER);
//			System.out.println("CAR: " + StreamTokenizer.TT_WORD);
			int tokType=0;
			boolean geometryFound = false;
			boolean isIndexedFaceSet = false;
			boolean pointsFound = false;
			do {
				tokType = lTokRead.nextToken();
//				if(tokType<0) System.out.println(tokType);
//				else { System.out.write(tokType); System.out.println();} 
				if(tokType==StreamTokenizer.TT_WORD) {
					lineNumber = lTokRead.lineno();
//					System.out.println("gefunden: " + lTokRead.sval);
					if(lTokRead.sval.equals("geometry")) geometryFound=true;
					if(lTokRead.sval.equals("indexedfaceset") && geometryFound) isIndexedFaceSet=true;
					if(lTokRead.sval.equals("point")&& isIndexedFaceSet) {pointsFound=true;break;} 
				}
			} while (tokType!=StreamTokenizer.TT_EOF);
			if(!pointsFound) throw new T3dException("Could not find points.");
			lTokRead.parseNumbers();//Die Doubles lesen wir 'direkt'
			int countO=0;
			double[] ordinates = new double[100000];
			while((tokType=lTokRead.nextToken())!=StreamTokenizer.TT_EOF){
				if(tokType==StreamTokenizer.TT_NUMBER) ordinates[countO++]=lTokRead.nval;
				if(countO==ordinates.length) {
					double[] buf = new double[ordinates.length*2];
					System.arraycopy(ordinates,0,buf,0,ordinates.length);
					ordinates=buf;
				}
				if(tokType==StreamTokenizer.TT_WORD) break;
			}
			if(!lTokRead.sval.equals("coordindex")) throw new T3dException("Could not find indices.");
			int countI=0;
			int[] indices = new int[100000]; //Hier könnten auch Facetten kommen - fehlt noch!
			while((tokType=lTokRead.nextToken())!=StreamTokenizer.TT_EOF){
				if(tokType==StreamTokenizer.TT_NUMBER){
					if(lTokRead.nval>=0) indices[countI++]=(int)lTokRead.nval;
				}
				if(countI==indices.length){
					int[] buf = new int[indices.length*2];
					System.arraycopy(indices,0,buf,0,indices.length);
					indices=buf;
				}
				if(tokType==StreamTokenizer.TT_WORD) break;
			}
			if((countO % 3 != 0)||(countI % 3 != 0)) throw new T3dException("Enexpected error." + countO + " " + countI);
			
			mTIN = new GmSimpleTINFeature();
			GmSimpleTINGeometry lTINGeom = (GmSimpleTINGeometry) mTIN.getGeometry();
			lTINGeom.newPointList(countO/3);
			
			//mTIN.setBoundsInvalid(); // Performanz!
			
			for (int i=0; i<countO/3; i++) {
				lTINGeom.setPoint(i, new GmPoint(ordinates[i*3],ordinates[i*3+1],ordinates[i*3+2]));
			}
			
			lTINGeom.newTriangleList(countI/3);
			for (int i=0; i<countI/3; i++) {
				lTINGeom.setTriangle(i,indices[i*3],indices[i*3+1],indices[i*3+2]);
			}
			
		}
        catch (FileNotFoundException e) {
		    throw new T3dException("Could not access file \"" + pFilename + "\".");
		}
		catch (IOException e) {
		    throw new T3dException(e.getMessage());
		}
		catch (T3dException e) {
		    throw new T3dException(e.getMessage());
		}
        catch (T3dNotYetImplException e) {
            System.out.println("Warning: Caught T3dNotYetImplException...");
                ;  // todo: das ist noch unsauber!!"
        }
		catch (Exception e) {
		    e.printStackTrace();
			throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
		}
	}
	
	private void readARCTin(String pFilename)
    {
		mTIN = new GmSimpleTINFeature();
//		mTIN.setBoundsInvalid(); // Performanz!  // todo: versuchen, wieder reinzunehmem!
		GmSimpleTINGeometry lTINGeom = (GmSimpleTINGeometry) mTIN.getGeometry();
		int vertexCount=0;
		int triangleCount=0;
		double xmin=0, ymin=0, zmin=0, xmax=0, ymax=0, zmax=0;

		FileReader lFileRead;
		try {
			lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);
			StreamTokenizer lTokRead = new StreamTokenizer(lDatRead);
			lTokRead.lowerCaseMode(true); //alle Tokens in Kleinbuchstaben holen

			String line="";
			//read first three lines
			line=lDatRead.readLine();
			line=lDatRead.readLine();
			line=lDatRead.readLine();

			StringTokenizer token=new StringTokenizer(line);
			token.nextToken();
			vertexCount = toInt(token.nextToken());
//			System.out.println("number of vertizes: " + vertexCount);

			lTINGeom.newPointList(vertexCount);
			for (int i=0; i<vertexCount; i++) {
				line=lDatRead.readLine();
				if (line==null || line.startsWith("TRI"))
					break;
				token=new StringTokenizer(line);
				GmPoint point = new GmPoint(
					toDouble(token.nextToken()),
					toDouble(token.nextToken()),
					toDouble(token.nextToken())
				);

				lTINGeom.setPoint(i, point);
				
//				if (i==0) {
//					xmin = xmax = point.getX();
//					ymin = ymax = point.getY();
//					zmin = zmax = point.getZ();
//				}
//				else {
//					if (point.getX() < xmin) xmin = point.getX();
//					if (point.getX() > xmax) xmax = point.getX();
//					if (point.getY() < ymin) ymin = point.getY();
//					if (point.getY() > ymax) ymax = point.getY();
//					if (point.getZ() < zmin) zmin = point.getZ();
//					if (point.getZ() > zmax) zmax = point.getZ();
//				}

			}

			//now the triangles
			line=lDatRead.readLine();
			token=new StringTokenizer(line);

			token.nextToken();
			triangleCount = toInt(token.nextToken());
//			System.out.println("number of triangles: " + triangleCount);

			lTINGeom.newTriangleList(triangleCount);
			for (int i=0; i<triangleCount; i++) {
				line=lDatRead.readLine();
				if (line.endsWith("ENDT") )
					break;
				token=new StringTokenizer(line);
				lTINGeom.setTriangle(
					i,
					toInt(token.nextToken())-1,
					toInt(token.nextToken())-1,
					toInt(token.nextToken())-1
				);
			}

//		System.out.println("Finished reading file : " + pFilename
//			+ "\nPoints:"+ lTINGeom.numberOfPoints()
//			+ "\nTriangles: " + lTINGeom.numberOfTriangles());
		lDatRead.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// reads gmt xyz / tin file; not optimized
	private void readGMTTin(String xyzFilename, String tinFilename) {
		mTIN = new GmSimpleTINFeature();
		mTIN.setBoundsInvalid(); // Performanz!
		GmSimpleTINGeometry lTINGeom = (GmSimpleTINGeometry) mTIN.getGeometry();
		Vector pointVec = new Vector(2000);
		Vector indexVec = new Vector(2000);
		
		FileReader lFileRead;
		try {
			lFileRead = new FileReader(xyzFilename);
			BufferedReader lDatRead = new BufferedReader(lFileRead);
			StreamTokenizer lTokRead = new StreamTokenizer(lDatRead);
			lTokRead.lowerCaseMode(true);//alle Tokens in Kleinbuchstaben holen
			lTokRead.eolIsSignificant(false);
			
			while (lTokRead.ttype != StreamTokenizer.TT_EOF) {		
				double x,y,z;
				lTokRead.nextToken();
				x = lTokRead.nval;
				lTokRead.nextToken();
				y = lTokRead.nval;
				lTokRead.nextToken();
				z = lTokRead.nval;
				pointVec.add(new GmPoint(x,y,z));
			}
						
			lDatRead.close();
		}
        catch (IOException e) {
			System.out.println("Error reading GMT xyz file.");
			e.printStackTrace();
		}
		
		try {
			lFileRead = new FileReader(tinFilename);
			BufferedReader lDatRead = new BufferedReader(lFileRead);
			StreamTokenizer lTokRead = new StreamTokenizer(lDatRead);
			lTokRead.lowerCaseMode(true);//alle Tokens in Kleinbuchstaben holen
			lTokRead.eolIsSignificant(false);
	
			while (lTokRead.ttype != StreamTokenizer.TT_EOF) {		
				int xIndex,yIndex,zIndex;
				lTokRead.nextToken();
				xIndex = (int) lTokRead.nval;
				lTokRead.nextToken();
				yIndex = (int) lTokRead.nval;
				lTokRead.nextToken();
				zIndex = (int) lTokRead.nval;
				indexVec.add(new int[]{xIndex,yIndex,zIndex});
			}
	
			
			lDatRead.close();
		}
        catch (IOException e) {
			System.out.println("Error reading GMT tin file.");
			e.printStackTrace();
		}

		System.out.println("Points: " + pointVec.size() + " / " + indexVec.size());
		lTINGeom.newPointList(pointVec.size());
		for (int i=0; i<pointVec.size();i++) {
			lTINGeom.setPoint(i, (GmPoint)pointVec.get(i));
		}
		
		lTINGeom.newTriangleList(indexVec.size());
		for (int i=0; i<indexVec.size();i++) {
			int[] triangle = (int[]) indexVec.get(i);
			lTINGeom.setTriangle(i, triangle[0],triangle[1],triangle[2]);
		}				
	}
}
