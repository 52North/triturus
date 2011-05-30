package org.n52.v3d.triturus.vispovray;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Hilfsklasse zur Ermittlung der BITMAP_TYPE-Bezeichner für die Spezifikation von Image-Maps in POV-Ray.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class BitmapTypeHelper
{
    /**
     * liefert den für Bilddateien des angegebenen Typs verwendeten BITMAP_TYPE-Bezeichner.<p>
     * @param pExt Extension (ohne Punkt), z. B. "png"
     * @return BITMAP_TYPE-Bezeichner
     */
    static public String getFileExtension(String pExt)
    {
        if (pExt.equalsIgnoreCase("bmp")) return "sys"; // todo: prüfen, ob das auich auf Unix-Plattform klappt
        if (pExt.equalsIgnoreCase("gif")) return "gif";
        if (pExt.equalsIgnoreCase("iff")) return "iff";
        if (pExt.equalsIgnoreCase("jpeg")) return "jpeg";
        if (pExt.equalsIgnoreCase("jpg")) return "jpeg";
        if (pExt.equalsIgnoreCase("pgm")) return "pgm";
        if (pExt.equalsIgnoreCase("png")) return "png";
        if (pExt.equalsIgnoreCase("ppm")) return "ppm";
        if (pExt.equalsIgnoreCase("sys")) return "sys";
        if (pExt.equalsIgnoreCase("tga")) return "tga";
        if (pExt.equalsIgnoreCase("tif")) return "tiff";
        if (pExt.equalsIgnoreCase("tiff")) return "tiff";

        throw new T3dException("Unsupported BITMAP_TYPE: " + pExt);
    }
}