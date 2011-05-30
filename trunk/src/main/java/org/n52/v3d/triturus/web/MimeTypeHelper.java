package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Diese Hilfsklasse enthält verschiedene Methoden, die den Umgang mit MIME-Typen erleichtern.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class MimeTypeHelper
{
    /**
     * liefert die für Dateien des angegebenen Typs üblicherweise verwendete Dateiextension.<p>
     * @param pMimeType MIME-Typ, z. B. "image/png"
     * @return Extension (ohne Punkt), z. B. "png"
     */
    static public String getFileExtension(String pMimeType)
    {
        if (pMimeType.equalsIgnoreCase("model/vrml")) return "wrl";
        if (pMimeType.equalsIgnoreCase("model/x3d")) return "x3d";
        if (pMimeType.equalsIgnoreCase("image/bmp")) return "bmp";
        if (pMimeType.equalsIgnoreCase("image/gif")) return "gif";
        if (pMimeType.equalsIgnoreCase("image/jpeg")) return "jpg";
        if (pMimeType.equalsIgnoreCase("image/png")) return "png";
        if (pMimeType.equalsIgnoreCase("image/ppm")) return "ppm";
        if (pMimeType.equalsIgnoreCase("image/rle")) return "rle";
        if (pMimeType.equalsIgnoreCase("image/tga")) return "tga";
        if (pMimeType.equalsIgnoreCase("image/tiff")) return "tif";
        if (pMimeType.equalsIgnoreCase("text/html")) return "htm";
        if (pMimeType.equalsIgnoreCase("text/plain")) return "txt";
        if (pMimeType.equalsIgnoreCase("text/xml")) return "xml";

        throw new T3dException("Unsupported MIME type: " + pMimeType);
    }

    /**
     * liefert den MIME-Typ, dem Dateien mit der angegebenen Extension üblicherweise entsprechen.<p>
     * @param pFileExt Extension (ohne Punkt), z. B. "png"
     * @return MIME-Typ, z. B. "image/png"
     */
    static public String getMimeType(String pFileExt)
    {
        if (pFileExt.equalsIgnoreCase("asc")) return "text/plain";
        if (pFileExt.equalsIgnoreCase("bmp")) return "image/bmp";
        if (pFileExt.equalsIgnoreCase("jpeg")) return "image/jpeg";
        if (pFileExt.equalsIgnoreCase("jpg")) return "image/jpeg";
        if (pFileExt.equalsIgnoreCase("gif")) return "image/gif";
        if (pFileExt.equalsIgnoreCase("grd")) return "text/plain";
        if (pFileExt.equalsIgnoreCase("htm")) return "text/html";
        if (pFileExt.equalsIgnoreCase("html")) return "text/html";
        if (pFileExt.equalsIgnoreCase("png")) return "image/png";
        if (pFileExt.equalsIgnoreCase("ppm")) return "image/ppm";
        if (pFileExt.equalsIgnoreCase("rle")) return "image/rle";
        if (pFileExt.equalsIgnoreCase("tga")) return "image/tga";
        if (pFileExt.equalsIgnoreCase("tif")) return "image/tiff";
        if (pFileExt.equalsIgnoreCase("tiff")) return "image/tiff";
        if (pFileExt.equalsIgnoreCase("tin")) return "text/plain";
        if (pFileExt.equalsIgnoreCase("txt")) return "text/plain";
        if (pFileExt.equalsIgnoreCase("wrl")) return "model/vrml";
        if (pFileExt.equalsIgnoreCase("x3d")) return "model/x3d";
        if (pFileExt.equalsIgnoreCase("xml")) return "text/xml";
        if (pFileExt.equalsIgnoreCase("xyz")) return "text/plain";

        throw new T3dException("Unsupported file extension: " + pFileExt);
    }
}
