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
package org.n52.v3d.triturus.vispovray.gifwriter;

/**
 * Helper to change the byte-order of 2-byte values.
 * @author Benno Schmidt
 */
public class ByteSequenceChanger
{
    /**
     * changes a <tt>short</tt> value's byte order. E.g., for the decimal value 262 (which corresponds to
     * 00000001 00000110 in 2-byte binary representation) the method will return the decimal value 1537 (which
     * corresponds to 00000110 00000001), and vice versa.
     * @param pVal 2-byte value
     * @return Value according to changed byte-order representation
     */
    public static short perform(short pVal)
    {
        int j = (short) (pVal & 0xFF);
        int i = (short) (pVal >>> 8);
        return (short) (j << 8 | i);
    }
}
