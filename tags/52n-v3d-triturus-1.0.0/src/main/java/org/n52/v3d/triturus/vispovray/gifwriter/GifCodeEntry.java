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
public class GifCodeEntry
{
    private short mPriorCode = -1;
    private short mCode = -1;
    private byte mChar = 0;

    public GifCodeEntry()
    {
        this.reset();
    }

    public boolean isFree()
    {
        return (mCode == -1);
    }

    public void set(short pPriorCode, short pCode, byte pChar)
    {
        mPriorCode = pPriorCode;
        mCode = pCode;
        mChar = pChar;
    }

    public void reset()
    {
        mPriorCode = -1;
        mCode = -1;
        mChar = 0;
    }

    public boolean isMatch(short pCode, byte pChar)
    {
        return (mCode == -1) || ((mPriorCode == pCode) && (mChar == pChar));
    }

    public short getCode()
    {
        return mCode;
    }
}
