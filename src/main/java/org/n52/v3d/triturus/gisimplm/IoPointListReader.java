package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.util.ArrayList;

/** 
 * Einlesen von Punktdateien. In den Punktdateien stehen zeilenweise x-, y- und z-Koordinaten durch Leerzeichen
 * voneinander getrennt.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class IoPointListReader extends IoObject
{
    private String mLogString = "";

    private String mFormat;
    private ArrayList mPointList = null;

    private VgEnvelope mSpatialFilter = null;

    /**
     * Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterstützt, wird später während
     * des Lesevorgangs eine Ausnahme geworfen.<p>
     * Es werden die folgenden Formate unterstützt:
     * <ul>
     * <li><i>Plain:</i> ASCII-Datei, zeilenweise x, y und z separiert durch Blank</li>
     * <li><b>... weitere Typen insb. Vermessungsformate -> Benno</b></li>
     * </ul><p>
     * @param pFormat Format-String, z. B. "Plain"
     */
    public IoPointListReader(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
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
     * liest eine Menge von 3D-Punkten einer Datei ein. Wird der spezifizierte Formattyp nicht unterstützt, wirft die
     * Methode eine <tt>T3dNotYetImplException</tt>.<p>
     * @param pFilename Pfad, unter dem die Datei abgelegt ist.
     * @return <tt>ArrayList</tt> von <tt>VgPoint</tt>-Objekten
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public ArrayList readFromFile(String pFilename) throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("Plain")) i = 1;
        // --> hier ggf. weitere Typen ergänzen...

        try {
            switch (i) {
                case 1: this.readPlainAscii(pFilename); break;
                // --> hier ggf. weitere Typen ergänzen...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }

        return mPointList;
    }

    private void readPlainAscii(String pFilename) throws T3dException
    {
// TODO: Separator variabel machen ebenso wie Reihenfolge x y z etc.; Kennung?
        String line = "";
        int lineNumber = 0;

        mPointList = new ArrayList();

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            String tok1, tok2, tok3;
            double x, y, z;
            VgPoint pt = null;

            line = lDatRead.readLine();
            while (line != null) {
                lineNumber++;

                tok1 = this.getStrTok(line, 1, " ");
                tok2 = this.getStrTok(line, 2, " ");
                tok3 = this.getStrTok(line, 3, " ");

                x = this.toDouble(tok1);
                y = this.toDouble(tok2);
                z = this.toDouble(tok3);

                pt = new GmPoint(x, y, z);

                if (mSpatialFilter != null) {
                    if (mSpatialFilter.contains(pt))
                        mPointList.add(pt);
                } else
                    mPointList.add(pt);

                line = lDatRead.readLine();
                //if (lineNumber % 1000000 == 0) System.out.println("lineNumber = " + lineNumber);
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
System.out.println("lineNumber = " + lineNumber);
    } // readPlainAscii()

    /**
     * setzt einen räumlichen Filter für die einzulesenden Punkte. Punkte, die außerhalb der durch <tt>pFilter</tt>
     * gegebenen Bounding-Box liegen, werden nicht berücksichtigt.<p>
     * Soll keine räumliche Filterung erfolgen, ist der Wert <i>null</i> als Parameter zu setzen (Voreinstellung).<p>
     * Bem.: Die z-Werte der Bounding-Box sind auf hinreichend kleine/große Werte zu setzen.<p>
     * @param pFilter Bounding-Box
     */
    public void setSpatialFilter(VgEnvelope pFilter) {
        mSpatialFilter = pFilter;
    }

    /**
     * liefert den gesetzten räumlichen Filter. Punkte, die außerhalb der Bounding-Box des Filters liegen, werden beim
     * Einlesen nicht berücksichtigt.<p>
     * Falls kein räumlicher Filter gesetzt ist, wird der Wert <i>null</i> zurückgegeben.<p>
     * @return 3D-Bounding-Box für räumliche Filterung oder <i>null</i>
     */
    public VgEnvelope getSpatialFilter() {
        return mSpatialFilter;
    }

    // private Helfer, benötigt in readPlainAscii():

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
pStr = pStr.replaceAll(",", "."); // todo: falls, als Dezimalpunkt
        return Double.parseDouble(pStr);
    } 

    // Konvertierung String -> Ganzzahl:
    private int toInt(String pStr)
    {
        return Integer.parseInt(pStr);
    } 
}
