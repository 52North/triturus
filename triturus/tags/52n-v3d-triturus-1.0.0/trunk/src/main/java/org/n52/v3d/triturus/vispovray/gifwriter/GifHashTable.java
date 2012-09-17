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
 * Helper class to implement the <tt>GifEncodedRaster</tt>.
 * @author Benno Schmidt
 * @see GifEncodedRaster
 */
public class GifHashTable
{
    private GifCodeEntry[] mTable;
    static final int sTableSize = 3851; // here a prime number

    /**
     * Constructor.
     */
    public GifHashTable()
    {
        mTable = new GifCodeEntry[sTableSize];
        for (int i = 0; i < sTableSize; i++) {
            mTable[i] = new GifCodeEntry();
        }
    }

    public void reset()
    {
        for (int i = 0; i < sTableSize; i++) {
            mTable[i].reset();
        }
    }

    /**
     * looks for a prefix/char combination in the code-entry hash-table.
     * @param pPrefix Prefix value to look for in the string hash-table
     * @param pChar Char value to look for
     * @return entry index, if found, first available index else
     * @see GifCodeEntry
     */
    public int findMatch(short pPrefix, byte pChar)
    {
        int i = (pChar << 5 ^ pPrefix) % sTableSize;
        if (i < 0)
            i += sTableSize;
        int j = 0;
        while (true)
        {
            if (mTable[i].isMatch(pPrefix, pChar))
               return i;
            j++;
            j++;
            if (j >= sTableSize)
                return -1;
            i = (i + j) % sTableSize;
        }
    }

    public GifCodeEntry getCodeEntry(int pIndex)
    {
        return mTable[pIndex];
    }
}
