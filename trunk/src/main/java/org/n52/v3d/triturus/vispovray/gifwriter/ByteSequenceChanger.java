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
