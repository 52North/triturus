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
     * Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterst�tzt, wird sp�ter w�hrend
     * des Lesevorgangs eine Ausnahme geworfen.<p>
     * Es werden die folgenden Formate unterst�tzt:
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

    /** protokolliert die durchgef�hrte Transformation. */
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
     * liest eine Menge von 3D-Punkten einer Datei ein. Wird der spezifizierte Formattyp nicht unterst�tzt, wirft die
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
        // --> hier ggf. weitere Typen erg�nzen...

        try {
            switch (i) {
                case 1: this.readPlainAscii(pFilename); break;
                // --> hier ggf. weitere Typen erg�nzen...

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
     * setzt einen r�umlichen Filter f�r die einzulesenden Punkte. Punkte, die au�erhalb der durch <tt>pFilter</tt>
     * gegebenen Bounding-Box liegen, werden nicht ber�cksichtigt.<p>
     * Soll keine r�umliche Filterung erfolgen, ist der Wert <i>null</i> als Parameter zu setzen (Voreinstellung).<p>
     * Bem.: Die z-Werte der Bounding-Box sind auf hinreichend kleine/gro�e Werte zu setzen.<p>
     * @param pFilter Bounding-Box
     */
    public void setSpatialFilter(VgEnvelope pFilter) {
        mSpatialFilter = pFilter;
    }

    /**
     * liefert den gesetzten r�umlichen Filter. Punkte, die au�erhalb der Bounding-Box des Filters liegen, werden beim
     * Einlesen nicht ber�cksichtigt.<p>
     * Falls kein r�umlicher Filter gesetzt ist, wird der Wert <i>null</i> zur�ckgegeben.<p>
     * @return 3D-Bounding-Box f�r r�umliche Filterung oder <i>null</i>
     */
    public VgEnvelope getSpatialFilter() {
        return mSpatialFilter;
    }

    // private Helfer, ben�tigt in readPlainAscii():

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
