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
package org.n52.v3d.triturus.gisimplm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgEnvelope;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Reading elevation grids (of type <tt>GmSimpleElevationGrid</tt>) from a file or URL location.
 *
 * @author Benno Schmidt, Martin May
 */
public class IoElevationGridReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private GmSimpleElevationGrid mElevationGrid = null;

    /**
     * Identifier to be used to process elevation-grids in ArcInfo ASCII grid format.
     */
    public static final String ARCINFO_ASCII_GRID = "ArcIGrd";

    /**
     * Constructor. As parameter, a format type identifier has to be set.
     * <p>
     * In case, the given format type is not supported, an exception will be thrown. Currently, these formats are
     * supported:
     * <ul>
     * <li><i>ArcIGrd:</i> ArcInfo ASCII grids</li>
     * <li><i>AcGeo:</i> ACADGEO format (lattice without color-information</li>
     * <li><i>BSQ:</i> Byte-sequential ESRI-format</li>
     * </ul>
     * <p>
     * @param pFormat Format-string, e.g. <tt></tt>&quot;ArcIGrd&quot;</tt>
     * @see IoElevationGridReader#ARCINFO_ASCII_GRID
     */
    public IoElevationGridReader(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * sets the format type.
     *
     * @param pFormat Format-string (e.g. <tt></tt>&quot;ArcIGrd&quot;</tt>)
     * @see IoElevationGridReader#ARCINFO_ASCII_GRID
     */
    public void setFormatType(String pFormat)
    {
        mFormat = pFormat;
    }

    /**
     * reads an elevation-grid from a file or URL location.<br /><br />
     *
     * @param pLocation File path or valid URL
     * @return Elevation-grid, or <i>null</i> if an error occurs
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public GmSimpleElevationGrid readFromFile(String pLocation) throws T3dException
    {
    	return this.read(pLocation);
    }

    /**
     * reads an elevation-grid from a file or URL location.
     * <p>
     * Note: URL strings shall start with <tt>"http"</tt>, file specifications that hold absolute paths with "file"
     * (todo: this remains to be tested).
     * Relative paths are supported (e.g.: <tt>"testdata/..."</tt>), too.
     *
     * @param pLocation File path or valid URL
     * @return Elevation-gridm, or <i>null</i> if an error occurs
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public GmSimpleElevationGrid read(String pLocation) throws T3dException
    {
    	InputStream is;
		try {
			if (pLocation.startsWith("http"))
	    		is = this.createInputStream(new URL(pLocation));
			else
				is = this.createInputStream(pLocation);
        }
        catch (MalformedURLException e) {
			throw new T3dException("Couldn't read location \"" + pLocation + "\" (malformed URL)." );
		} catch (IOException e) {
			throw new T3dException("Couldn't read location \"" + pLocation + "\" (IO error)." );
		}

    	int i = 0;
        if (mFormat.equalsIgnoreCase(ARCINFO_ASCII_GRID)) i = 1;
        if (mFormat.equalsIgnoreCase("AcGeo")) i = 2;
        if (mFormat.equalsIgnoreCase("BSQ")) i = 3;
        // --> add more types here...

        try {
            switch (i) {
                case 1: this.readArcInfoAsciiGrid(this.createBufferedReader(is)); break;
                case 2: this.readAcadGeoGrid(this.createBufferedReader(is)); break;
                case 3: this.readEsriBandSequential(pLocation); break;
                // --> add more types here...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }

        return mElevationGrid;
    }

    private InputStream createInputStream(URL url) throws IOException {
    	return url.openConnection().getInputStream();
    }

    private InputStream createInputStream(String pFilename) throws FileNotFoundException {
        InputStream  input  = this.getClass().getClassLoader().getResourceAsStream(pFilename);
    	if(input == null)
    		input =  new FileInputStream(pFilename);
    	return input;
    }

    private BufferedReader createBufferedReader(InputStream pInputStream) {
        return new BufferedReader( new InputStreamReader(pInputStream) );
    }

    /**
     * reads a 2-D float array from a stream and generates a grid from it.
     *
	 * @param inputStream
     * @throws IOException
	 */
	public GmSimpleElevationGrid readRawFloats(InputStream inputStream, VgEnvelope pEnv, int pWidth, int pHeight)
        throws IOException

    {
		int bufferSize = (pWidth+1)*(pHeight+1)*4;
		ByteBuffer bb = ByteBuffer.allocate(bufferSize);
		int k=0;
		byte[] bytes = new byte[50000]; //buffer
		while ( (k = inputStream.read(bytes))  != -1 ) {
			bb.put(bytes, 0, k);
			//System.out.println("read bytes:"+k+"/pos="+bb.position()+"/size="+bufferSize);
		}

        // Konstruktion des Elevation-Grids:
        mElevationGrid = new GmSimpleElevationGrid(
        		pWidth, pHeight,
				new GmPoint(pEnv.getXMin(),pEnv.getYMin(),0 ), // Ursprungspunkt
				pEnv.getExtentX() / pWidth , // Gitterweite x-Richtung
				pEnv.getExtentY() / pHeight); // Gitterweite y-Richtung
        //System.out.println("Input: width: "+pWidth+ " height: "+pHeight+" env: "+pEnv+"\nElevationGrid: "+mElevationGrid);

        bb.rewind();
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i=pHeight-1; i>=0; i--) {
			for (int j=0; j< pWidth; j++) {
				mElevationGrid.setValue(i,j, fb.get() );
			}
	        //System.out.println("Height: "+i+" heights="+fb.position());
		}
        return mElevationGrid;
	}

    private void readArcInfoAsciiGrid(BufferedReader pDatRead) throws T3dException
    {
        try {
            // Header lesen:
            int nCols = this.parseInt("ncols", pDatRead.readLine());
            int nRows = this.parseInt("nrows", pDatRead.readLine());
            float xllcorner = this.parseFloat("xllcorner", pDatRead.readLine());
            float yllcorner = this.parseFloat("yllcorner", pDatRead.readLine());
            float cellSize = this.parseFloat("cellsize", pDatRead.readLine());
            float NODATA_value = this.parseFloat("NODATA_value", pDatRead.readLine());

            // Bounding-Box bestimmen:
            double xFrom = xllcorner + cellSize/2.; // da Lattice, nicht Grid!
            double yFrom = yllcorner + cellSize/2.; // da Lattice, nicht Grid!

            // Konstruktion des Elevation-Grids:
            mElevationGrid = new GmSimpleElevationGrid(
                nCols, nRows,
                new GmPoint(xFrom, yFrom, 0.), // Ursprungspunkt
                cellSize, // Gitterweite x-Richtung
                cellSize); // Gitterweite y-Richtung
            mElevationGrid.setLatticeInterpretation(); // todo: okay? Konsequenzen?

            // H�henwerte lesen und Belegen des Zielgitters:
            float z = 0.f;
            String line = null;

            for (int i = nRows - 1; i >= 0; i--)
            {
                line = pDatRead.readLine();

                if (line != null) {
                    StringTokenizer st = new StringTokenizer(line);
                    for (int j = 0; j < nCols; j++) {
                        try {
                            z = Float.parseFloat(st.nextToken());
                        }
                        catch (NumberFormatException nfe) {
                            z = 0.f;
                        }
                        if (z != NODATA_value)
                            mElevationGrid.setValue(i, j, z);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw e;
        }
    } // readArcInfoAsciiGrid()

    // private Helfer, ben�tigt in readArcInfoAsciiGrid():

    private int parseInt(String check, String line) throws T3dException
    {
        StringTokenizer st = new StringTokenizer(line);
        String[] tokens = {"",""};
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i] = st.nextToken();
            if (tokens[i].length() > 0) i++;
            if (i > 1) break;
        }
        if (i == 2 && tokens[0].toLowerCase().equals(check.toLowerCase()))
            return Integer.parseInt(tokens[1]);
        else
            throw new T3dException("Header-value \"" + check + "\" is missing in input file.");
    }

    private float parseFloat(String check, String line) throws T3dException
    {
        StringTokenizer st = new StringTokenizer(line);
        String[] tokens = {"",""};
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i] = st.nextToken();
            if (tokens[i].length() > 0) i++;
            if (i > 1) break;
        }
        if (i == 2 && tokens[0].toLowerCase().equals(check.toLowerCase()))
            return Float.parseFloat(tokens[1]);
        else
            throw new T3dException("Header-value \"" + check + "\" is missing in input file.");
    }

    private void readAcadGeoGrid(BufferedReader pDatRead) throws T3dException
    {
        String line = "";
        int lineNumber = 0;

        try {
            String tok1, tok2, tok3, tok4;

            line = pDatRead.readLine(); // line 1
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("GRID:")) 
                throw new T3dException("Expected key-word GRID: in line " + lineNumber);

            line = pDatRead.readLine(); // line 2
            lineNumber++;
            ; // "C=[ON|OFF]"; Anweisung wird ignoriert

            line = pDatRead.readLine(); // line 3
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("FROM")) 
                throw new T3dException("Expected key-word FROM in line " + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            double xFrom = this.toDouble(tok2);
            double yFrom = this.toDouble(tok3);

            line = pDatRead.readLine(); // line 4
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("TO")) 
                throw new T3dException("Expected key-word TO in line " + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            double xTo = this.toDouble(tok2);
            double yTo = this.toDouble(tok3);

            line = pDatRead.readLine(); // line 5
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("SIZE")) 
                throw new T3dException("Expected key-word SIZE in line " + lineNumber);
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            if (!tok3.equalsIgnoreCase("x")) 
                throw new T3dException("Expected token 'x' in line " + lineNumber);
            tok4 = this.getStrTok(line, 4, " ");
            int nCols = this.toInt(tok2);
            int nRows = this.toInt(tok4);

            // Konstruktion des Elevation-Grids:

            mElevationGrid = new GmSimpleElevationGrid( 
                nCols, nRows,
                new GmPoint(xFrom, yFrom, 0.), // Ursprungspunkt
                (xTo - xFrom) / ((double)nCols - 1.), // Gitterweite x-Richtung
                (yTo - yFrom) / ((double)nRows - 1.)); // Gitterweite y-Richtung
            mElevationGrid.setLatticeInterpretation();

            // Belegen der Gitterpunkte mit H�henwerten:

            double z;
            mElevationGrid.setZBoundsInvalid(); // Performanz!


            for (int j = 0; j < nCols; j++) {
                for (int i = 0; i < nRows; i++) {
                    line = pDatRead.readLine();
                    lineNumber++;
                    tok1 = this.getStrTok(line, 1, " ");
                    z = this.toDouble(tok1);
                    mElevationGrid.setValue(i, j, z);
                }
            }

            line = pDatRead.readLine(); // letzte Zeile
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("END")) 
                throw new T3dException("Expected key-word END in line " + lineNumber);

            pDatRead.close();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in line " + lineNumber);
        }
    } // readAcadGeoGrid()

    // private Helfer, ben�tigt in readAcadGeoGrid():

    // Extraktion des i-ten Tokens (i >= 1!, i max. = 4) aus einem String ('pSep" als Trenner):
    private String getStrTok(String pStr, int i, String pSep) throws T3dException
    {
        ArrayList lStrArr = new ArrayList(); 
        lStrArr.add(pStr);
        int i0 = 0, i1 = 0, k = 0;
        while (k < 4 && i1 >= 0) {
           i1 = pStr.indexOf(pSep, i0);
           if (i1 >= 0) {
               if (k == 0)
                   lStrArr.set(0, pStr.substring(i0, i1));
               else
                   lStrArr.add(pStr.substring(i0, i1));
               i0 = i1 + 1;
               k++;
           }
        }
        if (k <= 3)
            lStrArr.add(pStr.substring(i0));
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
    
    private void readEsriBandSequential(String pFilename) throws T3dException {
		try {
			String bsqName = pFilename;
			String hdrName = null;
			String bqwName = null;
			boolean isFloat;
			if(bsqName.toLowerCase().endsWith("bsq")) {
				hdrName = bsqName.substring(0,bsqName.length()-3) + "hdr";
				bqwName = bsqName.substring(0,bsqName.length()-3) + "bqw";
			} else throw new T3dException("File-Name not correct: " + pFilename);

			//Header einlesen:
			FileReader hdrFileRead = new FileReader(hdrName);
			BufferedReader hdrDatRead = new BufferedReader(hdrFileRead);
			StreamTokenizer hdrTokRead = new StreamTokenizer(hdrDatRead);
			hdrTokRead.lowerCaseMode(true);//alle Tokens in Kleinbuchstaben holen
			hdrTokRead.commentChar(35);//Hash signalisiert Kommentar-Zeile...
			hdrTokRead.eolIsSignificant(false);//Zeilenumbruch unwichtig
			hdrTokRead.wordChars(65,90);//Gro�buchstaben
			hdrTokRead.wordChars(97,122);//Kleinbuchstaben
			int tokType=0;
			hdrTokRead.parseNumbers();//Die Nummern lesen wir 'direkt'
			String valueName="";
			Hashtable header = new Hashtable();
			//alles durchgehen und paarweise in Hashtable ablegen
			do {
				tokType = hdrTokRead.nextToken();
				if(tokType==StreamTokenizer.TT_WORD) {
					valueName = hdrTokRead.sval;
				}
				tokType = hdrTokRead.nextToken();
				if(tokType==StreamTokenizer.TT_WORD) {
					header.put(valueName,hdrTokRead.sval);
				} else if(tokType==StreamTokenizer.TT_NUMBER) {
					Integer value = new Integer((int)hdrTokRead.nval);
					header.put(valueName,value);
				}
			} while (tokType!=StreamTokenizer.TT_EOF);
			
			//Einige Werte pr�fen:
			if(header.get("pixeltype").equals("float")) isFloat=true;
			else if(header.get("pixeltype").equals("int") || header.get("pixeltype").equals("integer")) isFloat=false;
			else throw new T3dException("Pixeltype not supported: " + header.get("pixeltype"));
			if(((Integer)header.get("nbands")).intValue()!=1) throw new T3dException("Number of bands not supported: " + header.get("nbands"));
			
			//LowerLeft einlesen:
			double LLx = 0;
			double LLy = 0;
			double Dx = 10;
			double Dy = 10;
			int colOrder = 1;//Werte werden nach rechts
			int rowOrder = 1;// und nach oben eingelesen
			FileReader bqwFileRead = new FileReader(bqwName);
			BufferedReader bqwDatRead = new BufferedReader(bqwFileRead);
			StreamTokenizer bqwTokRead = new StreamTokenizer(bqwDatRead);
			bqwTokRead.lowerCaseMode(true);//alle Tokens in Kleinbuchstaben holen
			bqwTokRead.commentChar(35);//Hash signalisiert Kommentar-Zeile...
			bqwTokRead.eolIsSignificant(false);//Zeilenumbruch unwichtig
			bqwTokRead.wordChars(65,90);//Gro�buchstaben
			bqwTokRead.wordChars(97,122);//Kleinbuchstaben
			tokType=0;
			bqwTokRead.parseNumbers();//Die Nummern lesen wir 'direkt'
			tokType = bqwTokRead.nextToken();
			if(tokType==StreamTokenizer.TT_NUMBER) Dx = bqwTokRead.nval;
			tokType = bqwTokRead.nextToken();
			tokType = bqwTokRead.nextToken();
			tokType = bqwTokRead.nextToken();
			if(tokType==StreamTokenizer.TT_NUMBER) Dy = bqwTokRead.nval;
			tokType = bqwTokRead.nextToken();
			if(tokType==StreamTokenizer.TT_NUMBER) LLx = bqwTokRead.nval;
			tokType = bqwTokRead.nextToken();
			if(tokType==StreamTokenizer.TT_NUMBER) LLy = bqwTokRead.nval;
			
			//Korrektur des Ursprungs nach LL (falls n�tig)
			if(Dx<0){
				LLx = LLx + Dx * ((Integer)header.get("ncols")).intValue();
				Dx = 0-Dx;
				colOrder = -1;
			}
			if(Dy<0){
				LLy = LLy + Dy * ((Integer)header.get("nrows")).intValue();
				Dy = 0-Dy;
				rowOrder = -1;
			}
			
			// Konstruktion des Elevation-Grids:
			mElevationGrid = new GmSimpleElevationGrid(
				((Integer)header.get("nrows")).intValue(), ((Integer)header.get("ncols")).intValue(),
				new GmPoint(LLx, LLy, 0.), // Ursprungspunkt
				Dx, // Gitterweite x-Richtung
				Dy); // Gitterweite y-Richtung
			mElevationGrid.setLatticeInterpretation();
			mElevationGrid.setZBoundsInvalid(); // Performanz!

			FileInputStream bsqFS = new FileInputStream(bsqName);
			
			// Belegen der Gitterpunkte mit H�henwerten:
			int startrow,startcol;
			if(rowOrder==-1) startrow=mElevationGrid.numberOfRows()-1;
			else startrow=0;
			if(colOrder==-1) startcol=mElevationGrid.numberOfColumns()-1;
			else startcol=0;

			System.out.println("i(rows): " + startrow + " j(cols): " + startcol + " rowOrder: " + rowOrder + " colOrder: " + colOrder);
			ByteBuffer lBBuf = ByteBuffer.allocateDirect(mElevationGrid.numberOfColumns() * 4);
			FloatBuffer lBuf = lBBuf.asFloatBuffer();
			FileChannel fc = bsqFS.getChannel();
			
			for (int i = startrow; i >= 0 && i < mElevationGrid.numberOfRows(); i = i + rowOrder) { // Reihenfolge?
				fc.read(lBBuf);
				for (int j = startcol; j >= 0 && j < mElevationGrid.numberOfColumns(); j = j + colOrder) {
//					int zRaw = 0;
//					zRaw = bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
					if(isFloat) {
//						float z = Float.intBitsToFloat(zRaw);
						float z = lBuf.get(j);
						if(z>-8000) mElevationGrid.setValue(i, j, z);//Man k�nnte auf NaN pr�fen, aber...
					} else mElevationGrid.setValue(i, j, lBuf.get(j));
				}
				System.out.println("Reihe: " + i);
				lBBuf.clear();
			}
			
			bsqFS.close();
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
			throw new T3dException("Read error in \"" + pFilename + "\".");
		}
	} // readEsriBandSequential()
}