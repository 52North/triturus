package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;

import java.io.*;
import java.util.ArrayList;

/** 
 * Einlesen punkthafter Geoobjekte. In den Eingabedateien stehen zeilenweise x-, y- und z-Koordinaten und Attributwerte
 * durch Leerzeichen voneinander getrennt. Die Namen der Attribute werden aus einer Metadaten-Datei eingelesen.<p>
 * Beispiel:
 * <pre>
 * IoPointFeatureReader reader = new IoPointFeatureReader();
 * try {
 *     reader.readFromMetaFile( "example.metadata" );
 *     reader.readFromFile( "example.data" );
 * }
 * catch (T3dException e) {
 *     ...
 * }
 * </pre><p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoPointFeatureReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private ArrayList mFeatureList = null;

    private int mNumberOfAttributes = 0;

    private ArrayList mAttrNames = null;
    private ArrayList mAttrTypes = null;
        
    /** Konstruktor. */
    public IoPointFeatureReader() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgeführte Transformation. */
    public String log() {
        return mLogString;
    }

    /**
     * liest eine Menge attributierter 3D-Punkte aus einer ASCII-Datei ein.<p>
     * @param pFilename Pfad, unter dem die Datei abgelegt ist
     * @return <tt>ArrayList</tt> von <tt>VgAttrFeature</tt>-Objekten mit <tt>VgPoint</tt>-Geometrien
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public ArrayList readFromFile(String pFilename) throws T3dException
    {
    	String line = "";
        int lineNumber = 0;

        mFeatureList = new ArrayList();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String[] tok = new String[3 /* x, y und z */ + mNumberOfAttributes];
            double x, y, z;
  
            line = lDatRead.readLine();
            while (line != null) {
                lineNumber++;

                x = this.toDouble( this.getStrTok(line, 0, " " ));
                y = this.toDouble( this.getStrTok(line, 1, " " ));
                z = this.toDouble( this.getStrTok(line, 2, " " ));
 
                GmAttrFeature lFeat = new GmAttrFeature();
                lFeat.setGeometry(new GmPoint( x, y, z ));
               
                for (int i = 0; i < mNumberOfAttributes; i++) 
                {
                    String lType = (String) mAttrTypes.get(i);
                    String lAttrName = (String) mAttrNames.get(i);
                    String lVal = this.getStrTok(line, 3 + i, " ");
                    
                    if (lType.equals("int") || lType.equals("long"))
                        lFeat.addAttribute(lAttrName, lType, new Integer(this.toInt(lVal)));
                    else {
                    if (lType.equals("float") || lType.equals("double"))
                        lFeat.addAttribute(lAttrName, lType, new Double( this.toDouble(lVal)));
                    else
                        lFeat.addAttribute(lAttrName, lType, lVal);
                    }
                }
                mFeatureList.add(lFeat);
                line = lDatRead.readLine();
            }
            lDatRead.close();
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

        return mFeatureList;
    }

    /**
     * liest die Namen und Typen der verwendeten Attribute aus einer Metadaten-Datei im ASCII-Format ein.<p>
     * @param pFilename Pfad, unter dem die Datei abgelegt ist
     * @throws T3dException
     */
    public void readFromMetaFile(String pFilename) throws T3dException
    {
    	String line = "";
        int lineNumber = 0;

        mAttrNames = new ArrayList();
        mAttrTypes = new ArrayList();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String tok1, tok2; // Attributname und -typ
  
            line = lDatRead.readLine();
            while (line != null) 
            {
                mAttrNames.add(this.getStrTok(line, 0, " "));
                mAttrTypes.add(this.getStrTok(line, 1, " "));

                lineNumber++;
                line = lDatRead.readLine();
            }
            lDatRead.close();
            
            mNumberOfAttributes = mAttrNames.size();
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
    }

    // Extraktion des i-ten Tokens (i >= 0, i max. = this.cMaxNumberOfTokensPerLine - 1) aus einem String 
    // ('pSep" als Trenner):
    private String getStrTok(String pStr, int i, String pSep) throws T3dException
    {
        ArrayList lStrArr = new ArrayList(); 
        lStrArr.add( pStr );
        int i0 = 0, i1 = 0, k = 0;
        while (i1 >= 0) {
           i1 = pStr.indexOf(pSep, i0);
           if (i1 >= 0) {
               if (k == 0)
                   lStrArr.set(0, pStr.substring(i0, i1));
               else
                   lStrArr.add(pStr.substring( i0, i1 ));
               i0 = i1 + 1;
               k++;
           }
        }
        lStrArr.add(pStr.substring(i0));
        if (i < 0 || i >= lStrArr.size())
            return ""; 
        return (String) lStrArr.get(i);
    } 

    // Konvertierung String -> Gleitpunktzahl:
    private double toDouble(String pStr) 
    {
        return Double.parseDouble(pStr);
    } 

    // Konvertierung String -> Ganzzahl:
    private int toInt(String pStr)
    {
        return Integer.parseInt( pStr );
    } 
}
