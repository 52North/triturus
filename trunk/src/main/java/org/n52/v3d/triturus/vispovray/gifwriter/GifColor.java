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

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Helper class to hold color-values used in <tt>GifEncodedRaster</tt> objects.
 * @author Benno Schmidt
 * @see GifEncodedRaster
 */
public final class GifColor
{
    private byte mRed;
    private byte mGreen;
    private byte mBlue;

    /**
     * Constructor.
     * @param pIndexColorModel AWT index color model object
     * @param pPixel the specified pixel
     */
    GifColor(IndexColorModel pIndexColorModel, int pPixel)
    {
        mRed = (byte) pIndexColorModel.getRed(pPixel);
        mGreen = (byte) pIndexColorModel.getGreen(pPixel);
        mBlue = (byte) pIndexColorModel.getBlue(pPixel);
    }

    /**
     * returns the AWT color object.
     * @return AWT color representation
     */
    public Color getColor()
    {
        return new Color(mRed & 0xFF, mGreen & 0xFF, mBlue & 0xFF);
    }

    public void write(DataOutputStream pDataOutputStream) throws IOException
    {
        pDataOutputStream.writeByte(mRed);
        pDataOutputStream.writeByte(mGreen);
        pDataOutputStream.writeByte(mBlue);
    }
}
