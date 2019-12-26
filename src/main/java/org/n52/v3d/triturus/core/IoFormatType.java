/**
 * Copyright (C) 2019 52 North Initiative for Geospatial Open Source 
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.core;

/**
 * I/O format type constants. Within this framework, these constants are 
 * used in I/O implementations of various readers and writers. Note that 
 * not all formats supported by the 52N Triturus framework have been 
 * &quot;registered&quot; as <tt>IoFormatType</tt> here; for rare formats, 
 * often implementation specific type constants or character strings are 
 * used!
 *
 * @author Benno Schmidt
 */
public class IoFormatType 
{
    /**
     * File-format type identifier to be used for encodings based on VTK 
     * &quot;dataset format&quot;.
     */
    public static final String VTK_DATASET = "VTKDataset";

    /**
     * Type identifier to be used for X3DOM-encoded encodings. Usually, X3DOM
     * scenes will be embedded into HTML5 code here.
     */
    public static final String X3DOM = "X3DOM";
    
    /**
     * Type identifier to be used to process X3D-encoded objects.
     */
    public static final String X3D = "X3D";
    
    /**
     * File-format type identifier to be used for (old-fashioned) VRML2 
     * encodings.
     */
    public static final String VRML2 = "VRML2";

    /**
     * File-format type identifier to be used for Wavefront OBJ format.
     */
    public static final String OBJ = "WavefrontOBJ";

    /**
     * File-format type identifier to be used to process objects in ACADGEO 
     * format.
     */
    public static final String ACGEO = "AcGeo";

    /**
     * File-format type identifier to be used to process elevation-grids in 
     * ArcInfo ASCII grid format.
     */
    public static final String ARCINFO_ASCII_GRID = "ArcIGrd";
}