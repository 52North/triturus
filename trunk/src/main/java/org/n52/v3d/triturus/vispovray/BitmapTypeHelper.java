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
package org.n52.v3d.triturus.vispovray;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Helper class to determine the BITMAP_TYPE-designator for image-map specifications in POV-Ray.
 * @author Benno Schmidt
 */
public class BitmapTypeHelper
{
    /**
     * returns the BITMAP_TYPE-designator used for image files of a given type.
     * @param pExt Image type, given by the file extension (without dot), e.g. &quot;png&quot;
     * @return BITMAP_TYPE-designator
     */
    static public String getFileExtension(String pExt)
    {
        if (pExt.equalsIgnoreCase("bmp")) return "sys"; // todo: pr�fen, ob das auch auf Unix-Plattform klappt
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