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

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgIndexedTIN;

import java.io.*;
import java.text.DecimalFormat;

/** 
 * Writing TINs to files or streams.<br /><br />
 * <i>German:</i> Schreiben von TINs (Typ <tt>GmSimpleTINFeature</tt>) in Dateien oder Str&ouml;me.
 * @author Benno Schmidt
 */
public class IoTINWriter extends IoAbstractWriter
{
    private String mLogString = "";
    private String mFormat;

    /**
     * Constructor.<br /><br />
     * <i>German:</i> Konstruktor. Als Parameter ist der Dateiformattyp zu setzen. Wird dieser nicht unterst&uuml;tzt,
     * wird sp&auml;ter w&auml;hrend des Schreibvorgangs eine Ausnahme geworfen.<br />
     * Es werden die folgenden Formate unterst&uuml;tzt:<br />
     * <ul>
     * <li><i>AcGeo:</i> ACADGEO format</li>
     * <li><i>Vrml1:</i> VRML 1.0 scene (as triangle mesh)</li>
     * </ul>
     * Bem.:<p>
     * <b>Bislang ist keiner der Exporter getestet! F&uuml;r den VRML-Export ist die Orientierung der Dreiecke noch
     * unber&uuml;cksichtigt!</b>
     * @param pFormat Format-string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
     */
    public IoTINWriter(String pFormat) {
        mLogString = this.getClass().getName();
        this.setFormatType(pFormat);
    }

    public String log() {
        return mLogString;
    }

    /** 
     * sets the format type.
     * @param pFormat Format-string (e.g. <tt></tt>&quot;AcGeo&quot;</tt>)
     */
    public void setFormatType(String pFormat)
    {
        mFormat = pFormat;
    }

    /**
     * writes a TIN to a file.<br /><br />
     * <i>German:</i> schreibt ein TIN in eine Datei. Wird der spezifizierte Formattyp nicht unterst&uuml;tzt, wirft die
     * Methode eine <tt>T3dNotYetImplException</tt>.
     * @param pTIN TIN to be written
     * @param pFilename File path
     * @throws org.n52.v3d.triturus.core.T3dException
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     */
    public void writeToFile(GmSimpleTINFeature pTIN, String pFilename) 
    	throws T3dException, T3dNotYetImplException
    {
        int i = 0;
        if (mFormat.equalsIgnoreCase("AcGeo")) i = 1;
        if (mFormat.equalsIgnoreCase("Vrml1")) i = 2;
        // --> hier ggf. weitere Typen erg�nzen...

        try {
            switch (i) {
                case 1: this.writeAcadGeoTIN(pTIN, pFilename); break;
                case 2: this.writeSimpleVrml(pTIN, pFilename); break;
                // --> hier ggf. weitere Typen erg�nzen...

                default: throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }
    }  

	private void writeAcadGeoTIN(GmSimpleTINFeature pTIN, String pFilename) throws T3dException
	{
		try {
			FileWriter lFileWrite = new FileWriter(pFilename);
			BufferedWriter lDat = new BufferedWriter(lFileWrite);

			//GmSimpleTINGeometry lGeom = (GmSimpleTINGeometry) pTIN.getGeometry();
			VgIndexedTIN lGeom = (VgIndexedTIN) pTIN.getGeometry();

			lDat.write("TINBEGIN");
			lDat.newLine();
			lDat.write("FORMAT R=OFF C=OFF");
			lDat.newLine();
			lDat.write("TIN:");
			lDat.newLine();

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

			lDat.write("POINTS "+lGeom.numberOfPoints()+"\n");
			for (int i = 0; i < lGeom.numberOfPoints(); i++) {
				lDat.write(dfXY.format(lGeom.getPoint(i).getX()) + " ");
				lDat.write(dfXY.format(lGeom.getPoint(i).getY()) + " ");
				lDat.write(dfZ.format(lGeom.getPoint(i).getZ()) + "\n");
			}
			
			lDat.write("TRIANGLES "+lGeom.numberOfTriangles()+"\n");
			for (int i = 0; i < lGeom.numberOfTriangles(); i++) {
				lDat.write(lGeom.getTriangleVertexIndices(i)[0] + " ");
				lDat.write(lGeom.getTriangleVertexIndices(i)[1] + " ");
				lDat.write(lGeom.getTriangleVertexIndices(i)[2] + "\n");
			}
			lDat.write("END");
			lDat.newLine();
			
			lDat.close();
			lFileWrite.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    
    private void writeSimpleVrml(GmSimpleTINFeature pTIN, String pFilename) throws T3dException
    {
        try {
            FileWriter lFileWrite = new FileWriter(pFilename);
            BufferedWriter lDat = new BufferedWriter(lFileWrite);

            GmSimpleTINGeometry lGeom = (GmSimpleTINGeometry) pTIN.getGeometry();

            lDat.write("#VRML V1.0 ascii"); lDat.newLine();
            lDat.newLine();
            lDat.write("Separator {"); lDat.newLine();
            lDat.newLine();
            lDat.write("  DEF SceneInfo Info {\n"); lDat.newLine();
            lDat.write("    string \"Generated by ACAD-GEO DGM v3.1\""); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.write("  ShapeHints {"); lDat.newLine();
            lDat.write("    vertexOrdering CLOCKWISE"); lDat.newLine();
            lDat.write("    shapeType SOLID"); lDat.newLine();
            lDat.write("    faceType CONVEX"); lDat.newLine();
            lDat.write("    creaseAngle 0.0"); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.newLine();
            lDat.write("  DEF Green_DEM Separator {"); lDat.newLine();
            lDat.write("    Material {\n");
            lDat.write("      diffuseColor 0.0 1.0 0.0"); lDat.newLine();
            lDat.write("      ambientColor 0.0 0.1 0.0"); lDat.newLine();
            lDat.write("      specularColor 0.8 0.8 0.8"); lDat.newLine();
            lDat.write("      shininess 0.1"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.write("    Coordinate3 {"); lDat.newLine();
            lDat.write("      point ["); lDat.newLine();

            // VRML Teil 1 (Angabe der St�tzpunkte):

            DecimalFormat dfXY = this.getDecimalFormatZ();
            DecimalFormat dfZ = this.getDecimalFormatZ();

            for (int i = 0; i < lGeom.numberOfPoints(); i++) {
                GmPoint pt = new GmPoint(lGeom.getPoint(i));
                lDat.write("        " + dfXY.format(pt.getX()) + " " + dfXY.format(pt.getY()) + " " + dfZ.format(pt.getZ()));
                lDat.newLine();
            }

            lDat.write("      ]"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.newLine();

            // VRML Teil 2 (Vermaschung):

            lDat.write("    IndexedFaceSet {"); lDat.newLine();
            lDat.write("      coordIndex ["); lDat.newLine();

            int crn[];
            for (int i = 0; i <= lGeom.numberOfTriangles(); i++) {
            	crn = lGeom.getTriangleVertexIndices( i );
                lDat.write("        " + crn[0] + ", " + crn[1] + ", " + crn[3] + ", -1,"); lDat.newLine();
            }

            lDat.write("      ]"); lDat.newLine();
            lDat.write("    }"); lDat.newLine();
            lDat.write("  }"); lDat.newLine();
            lDat.write("}"); lDat.newLine();

            lDat.close();
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
    } // writeSimpleVrml()
}
