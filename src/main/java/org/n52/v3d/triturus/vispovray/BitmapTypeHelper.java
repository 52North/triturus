/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
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