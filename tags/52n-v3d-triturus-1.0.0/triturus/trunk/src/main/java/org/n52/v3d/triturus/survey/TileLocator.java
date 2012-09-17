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
package org.n52.v3d.triturus.survey;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.t3dutil.operatingsystem.PropertyLoader;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tile identifier locator.<br /><br />
 * <i>German: </i>Klasse zur Ermittlung allgemeiner Kachelnummern ("Blattnummern"). Den Kacheln liegen stets Linien
 * geografischer Koordinaten in festen AbstAauml;nden zugrunde. Die Kachelnummern sind stets vierstellig in der Form
 * &quot;B1B2B3B4&quot; (wobei B1, B2, B3, B4 Zeichen im Bereich '0'...'9').<br />
 * Bem.: Ein Beispiel einer derartigen Kachelung ist der Blattschnitt der bundedeutschen TK 25.<br />
 * Die Zuordnungsvorschrift einer Blattnummer zu einer Kachel ergibt sich wie folgt:<p>
 * <tt>(B12, B34)T = A * (lambda, phi)T + b</tt>, wobei sich der Blattnummernteil BiBj aus Math.ceil(Bij) ergibt und
 * gegebenenfalls um eine f&uuml;hrende '0' erg�nzt wird (Bi = '0', falls Bij < 10). A ist eine 2x2-Matrix, b ein
 * 2x1-Vektor, transponierte Vektoren sind durch ein nachgestelltes T gekennzeichnet. Eine Kachelung ist hier somit
 * durch die 6 Parameter a11, a12, b1, a21, a22, b2 eindeutig festgelegt.
 * @see TKBlattLocator
 * @author Benno Schmidt
 */
public class TileLocator
{
    private Log sLogger = LogFactory.getLog(TileLocator.class);

    private ArrayList mTileLocatorEntries = new ArrayList();

    /**
     * Constructor.
     */
    public TileLocator() {
        this.readProperties("tiledef.properties"); // Properties-Datei einlesen
    }

    /**
     * @deprecated
     * Constructor for debug purposes only.
     */
    public TileLocator(String lParam)
    {
        if (lParam.equalsIgnoreCase("test"))
        {
            TileLocatorEntry lEntry1 = new TileLocatorEntry(
                "TK25",
                "German TK 25 tiles (Blattnummern der Topografischen Karten TK 25)",
                new double[] {0.,-10.,560.,6.,0.,-34.});
            mTileLocatorEntries.add(lEntry1);

            TileLocatorEntry lEntry2 = new TileLocatorEntry(
                "earth-10deg",
                "Worldwide 10 degree lat/lon-tiles, \"0000\" (S90 W180) to \"3517\" (N90 E180)",
                new double[] {0.1,0.,18.,0.,0.1,9.});
            mTileLocatorEntries.add(lEntry2);

            TileLocatorEntry lEntry3 = new TileLocatorEntry(
                "earth-5deg",
                "Worldwide 5 degree lat/lon-tiles, \"0000\" (S90 W180) to \"7135\" (N90 E180)",
                new double[] {0.2,0.,36.,0.,0.2,18.});
            mTileLocatorEntries.add(lEntry3);

            TileLocatorEntry lEntry4 = new TileLocatorEntry(
                "europe-1deg",
                "Europe-wide 1 degree lat/lon-tiles, \"0000\" (N30 W30) to \"9959\" (N90 E70)",
                new double[] {1.,0.,30.,0.,1.,-30.});
            mTileLocatorEntries.add(lEntry4);

            TileLocatorEntry lEntry5 = new TileLocatorEntry(
                "europe-0.5deg",
                "Europe-internal 0.5 degree lat/lon-tiles, \"0000\" (N31 W11) to \"9999\" (N81 E39)",
                new double[] {2.,0.,22.,0.,2.,-62.});
            mTileLocatorEntries.add(lEntry5);
        }
        else
            this.readProperties("tiledef.properties"); // Properties-Datei einlesen
    }

    private void readProperties(String pFileName) {
        PropertyLoader lPropLoader = PropertyLoader.getInstance();
        try {
            lPropLoader.loadPropertiesWithClassLoader(pFileName);
        } catch (IOException e) {
            sLogger.error("IO-Exception while reading file \"" + pFileName + "\".");
            return;
        }

        String lProp = "";
        try {
            lProp = "tile-identifiers";
            String str = lPropLoader.getProperty(lProp);
            if (str == null || str.length() <= 0) {
                sLogger.error("Could not access property \"" + lProp + "\" in in properties file \"" + pFileName + "\"!");
                throw new T3dException("Congfiguration error (\"" + pFileName + "\").");
            }
            String[] lTileIDs = str.split(",");

            if (lTileIDs == null || lTileIDs.length <= 0)
                sLogger.warn("Missing tile information in properties file \"" + pFileName + "\"!");
            else {
                for (int i = 0; i < lTileIDs.length; i++) {
                    String lTileID = lTileIDs[i];

                    try {
                        lProp = "tile." + lTileID + ".directory";
                        String lDir = lPropLoader.getProperty(lProp);
                        lProp = "tile." + lTileID + ".description";
                        String lDescr = lPropLoader.getProperty(lProp);
                        lProp = "tile." + lTileID + ".parameters";
                        str = lPropLoader.getProperty(lProp);
                        if (str != null && str.length() > 0) {
                            String[] lParams = str.split(",");
                            if (lParams != null && lParams.length > 0) {
                                if (lParams.length != 6) {
                                    sLogger.error("Illegal number of tile locator parameters in properties file \"" + pFileName + "\"!");
                                    throw new T3dException("Congfiguration error (\"" + pFileName + "\").");
                                }
                                double lParamArr[] = new double[6];
                                for (int j = 0; j < lParams.length; j++)
                                    lParamArr[j] = Double.parseDouble(lParams[j]);
                                TileLocatorEntry lEntry = new TileLocatorEntry(lDir, lDescr, lParamArr);
                                if (lEntry.isValid())
                                    mTileLocatorEntries.add(lEntry);
                             }
                        }
                    }
                    catch (T3dException e) {
                        sLogger.error("Could not access property \"" + lProp + "\" in in properties file \"" + pFileName + "\"!");
                    }
                }
            }
        } catch (T3dException e) {
            sLogger.error("Could not access property \"" + lProp + "\" in in properties file \"" + pFileName + "\"!");
        }
    }

    /**
     * returns the tile identifier for a given position.<br /><br />
     * <i>German:</i> liefert die vierstellige Kachelnummer f&uuml;r den angegebenen Punkt.<br />
     * @param pTileId Bezeichner der verwendeten Kachelung, z. B. <tt>"TK25"</tt> oder <tt>"earth"</tt>
     * @param pt Position gegeben in geografischen Koordinaten
     * @return Kachelnummer
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.vgis.T3dSRSException
     */
    // todo engl. javadoc der Parameter
    public String tileNumber(String pTileId, VgPoint pt) throws T3dException, T3dSRSException
    {
        pt.setSRS("EPSG:4326");// todo muss eigentlich raus, l�uft dann aber noch nicht... :-(
        if (! (pt.getSRS().equalsIgnoreCase("EPSG:4326")))
            throw new T3dSRSException( "TileLocator can not process SRS \"" + pt.getSRS() + "\"." );

        // zum �bergebenen Bezeichner geh�rigen TileLocatorEntry suchen:
        TileLocatorEntry lEntry = this.getTileLocatorEntry(pTileId);
        if (lEntry == null)
            throw new T3dException("TileLocatorEntry \"" + pTileId + "\" is not available!");

        this.setTile(pt, lEntry);
        return this.generateNumber();
    }

    /**
     * formats the tile identifier.<br /><br />
     * <i>German:</i> formatiert die Kachelnummer f&uuml;r die angegebene Nummern-Kombination.<br />
     * Beispiel: <tt>blattnummer(47,9)</tt> liefert &quot;4709&quot; als Resultat.<p>
     * @param i ersten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @param j letzten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @return Blattnummer
     */
    // todo engl. javadoc der Parameter
    static public String blattnummer(int i, int j) {
        String ret = "";
        if (i < 10) ret += "0";
        ret += "" + i;
        if (j < 10) ret += "0";
        ret += "" + j;
        return ret;         // todo methode kann in basisklasse oder in TKBlattLocator diese methode nutzen um redundanz zu vermeiden
    }

    private int mB12, mB34;

    private void setTile(VgPoint pt, TileLocatorEntry pLocEntry)
    {
        double[] par = pLocEntry.getParameter();

        mB12 = (int)(par[0] * pt.getX() + par[1] * pt.getY() + par[2]); // todo: im HBU dokumentieren!
        mB34 = (int)(par[3] * pt.getX() + par[4] * pt.getY() + par[5]);
    }

    public VgEnvelope envelope(int B12, int B34, String pFileLoc)
    {
        double[] par = this.getTileLocatorEntry(pFileLoc).getParameter();

        // Invertierung der Parametermatrix hier einfach, da 2x2-Matrix:
        double det = 1./(par[0] * par[4] - par[1] * par[3]);
        double inv11 = par[4] * det;
        double inv12 = -par[1] * det;
        double inv21 = -par[3] * det;
        double inv22 = par[0] * det;

        double lambda_min = inv11 * (B12 - par[2]) + inv12 * (B34 - par[5]);
        double phi_min = inv21 * (B12 - par[2]) + inv22 * (B34 - par[5]);
        double lambda_max = inv11 * ((B12 + 1) - par[2]) + inv12 * ((B34 + 1) - par[5]);
        double phi_max = inv21 * ((B12 + 1) - par[2]) + inv22 * ((B34 + 1) - par[5]);
        // todo: stimmt so nur f�r kacheln, f�r die B12, B34 gegen�ber lambda, phi monoton steigend

        return new GmEnvelope(lambda_min, lambda_max, phi_min, phi_max, 0., 0.);
    }

    private String generateNumber()
    {
        if (mB34 < 10)
            return "" + mB12 + "0" + mB34;
        else
            return "" + mB12 + mB34;
    }

    private TileLocatorEntry getTileLocatorEntry(String pTileId) {
        if (mTileLocatorEntries == null)
            return null;
        for (int i = 0; i < mTileLocatorEntries.size(); i++) {
            Object obj = mTileLocatorEntries.get(i);
            if (obj != null && obj instanceof TileLocatorEntry) {
                TileLocatorEntry lEntry = (TileLocatorEntry) obj;
                if (pTileId.equals(lEntry.getDirectory()))
                    return lEntry;
            }
        }
        return null;
    }

    public class TileLocatorEntry
    {
        private String mDirectory = null;
        private String mDescription = null;
        private double[] mParameters = null;

        public TileLocatorEntry(String pDirectory, String pDescription, double[] pParameters) {
            mDirectory = pDirectory;
            mDescription = pDescription;
            mParameters = pParameters;
        }

        public boolean isValid() {
            if (mDirectory == null || mDirectory.length() <= 0)
                return false;
            if (mDescription == null || mDescription.length() <= 0)
                return false;
            if (mParameters == null || mParameters.length != 6)
                return false;
            return true;
        }

        public String getDirectory() {
            return mDirectory;
        }

        public double[] getParameter() {
            return mParameters;
        }
    }
}
