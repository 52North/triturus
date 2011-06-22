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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A <tt>GifEncodedRaster</tt> represents an index-coded GIF image (GIF89a) holding elevation values. Usage example:
 * <br /><br />
 * <tt>
 * GmSimpleElevationGrid myElevationGrid;<br />
 * GmSimple2dGridGeometry myGeom = (GmSimple2dGridGeometry) myElevationGrid.getGeometry();<br />
 * GifEncodedRaster img = new GifEncodedRaster(myGeom.numberOfColumns(), myGeom.numberOfRows());<br />
 * // Write GIF file<br />
 * try {<br />
 * &nbsp;&nbsp;FileOutputStream fos = new FileOutputStream(pFilename);<br />
 * &nbsp;&nbsp;img.encode(fos);<br />
 * &nbsp;&nbsp;fos.close();<br />
 * }<br />
 * catch { ... };<br />
 * </tt>
 * <br />
 * Note: For further information about GIF file generation, it is recommended to study the open-source package
 * <tt>ranab.img.gif</tt> by Rana Bhattacharyya, which is available via Koders.com; see http://corp.koders.com.
 * @author Benno Schmidt
 */
public class GifEncodedRaster
{
    private BufferedImage mImageData;
    private short mImageWidth;
    private short mImageHeight;
    private Graphics2D mGraphics = null;
    private byte[] mCodeBuffer;
    GifColor[] mGlobalColorTable;

    private GifHashTable mGifHashTable;

    private short mBitOffset;
    private short mClearCode = 256;
    private short mEofCode;
    private short mCodeSize;
    private short mMaxCode;
    private short mFreeCode;

    /**
     * Constructor.
     * @param pImageWidth Image width in pixels
     * @param pImageHeight Image height in pixels
     */
    public GifEncodedRaster(int pImageWidth, int pImageHeight)
    {
        mCodeBuffer = new byte[259];
        mImageData = new BufferedImage(pImageWidth, pImageHeight, 13);
        mGraphics = mImageData.createGraphics();

        mImageWidth = (short) pImageWidth;
        mImageHeight = (short) pImageHeight;

        mGifHashTable = new GifHashTable();

        mGlobalColorTable = new GifColor[256];
        IndexColorModel lIndexColorModel = (IndexColorModel) mImageData.getColorModel();
        for (int i = 0; i < mGlobalColorTable.length; i = (short)(i + 1)) {
            mGlobalColorTable[i] = new GifColor(lIndexColorModel, i);
        }
    }

    public Graphics2D getGraphics()
    {
        return mGraphics;
    }

    /**
     * gets the palette color for a given index.
     * @param pIndex index, here in the range 0...255
     * @return AWT color
     */
    public Color getPalColor(int pIndex)
    {
        return mGlobalColorTable[pIndex].getColor();
    }

    /**
     * writes the image to an output stream.
     * @param pOutputStream Output stream
     * @throws IOException if an I/O error occurs
     */
    public void encode(OutputStream pOutputStream) throws IOException
    {
        DataOutputStream lDataOutputStream = new DataOutputStream(pOutputStream);
        String str = "GIF89a";
        for (int i = 0; i < str.length(); i++) {
            lDataOutputStream.writeByte(str.charAt(i));
        }
        this.writeScreenDesc(lDataOutputStream);
        for (int j = 0; j < mGlobalColorTable.length; j++) {
            mGlobalColorTable[j].write(lDataOutputStream);
        }
        this.writeApplBlock(lDataOutputStream);
        this.writeCommentBlock(lDataOutputStream);
        this.writeImage(lDataOutputStream);
        lDataOutputStream.writeByte(59);
        lDataOutputStream.close();
    }

    private void writeImage(DataOutputStream pDataOutputStream) throws IOException
    {
        this.writeGraphicsBlock(pDataOutputStream);
        this.writeImageDesc(pDataOutputStream);

        mBitOffset = 0;
        this.reset();
        pDataOutputStream.writeByte(8);
        this.writeCode(pDataOutputStream, mClearCode);
        Raster lRaster = mImageData.getData();
        int[] lIntArr = new int[1];
        lRaster.getPixel(0, 0, lIntArr);

        int lImageHeight = (short) mImageData.getHeight();
        int lImageWidth = (short) mImageData.getWidth();
        short s = (short) lIntArr[0];
        for (int i = 0; i < lImageHeight; i++)
        {
            int j = 0;
            if (i == 0) j++;
            for (/*int j*/; j < lImageWidth; j++)
            {
                lRaster.getPixel(j, i, lIntArr);
                int k1 = (byte) lIntArr[0];
                int k2 = mGifHashTable.findMatch(s, (byte) k1);
                if (k2 < -1)
                    throw new IOException();
                GifCodeEntry lCodeEntry = mGifHashTable.getCodeEntry(k2);
                if (!lCodeEntry.isFree()) {
                    s = lCodeEntry.getCode();
                }
                else {
                    this.writeCode(pDataOutputStream, s);
                    int k3 = mFreeCode;
                    if (mFreeCode <= 4095) {
                        lCodeEntry.set(s, mFreeCode, (byte) k1);
                        mFreeCode = (short) (mFreeCode + 1);
                    }
                    if (k3 == mMaxCode) {
                        if (mCodeSize < 12) {
                            mCodeSize = (short) (mCodeSize + 1);
                            mMaxCode = (short) (mMaxCode * 2);
                        }
                        else {
                            this.writeCode(pDataOutputStream, mClearCode);
                            this.reset();
                        }
                    }
                    s = (short)(k1 & 0xFF);
                }
            }
        }

        this.writeCode(pDataOutputStream, s);
        this.writeCode(pDataOutputStream, mEofCode);
        if (mBitOffset > 0) {
            this.flush(pDataOutputStream, (mBitOffset + 7) / 8);
        }
        this.flush(pDataOutputStream, 0);
    }

    private void writeCode(DataOutputStream pDataOutputStream, short pCode) throws IOException
    {
        int i = (short) (mBitOffset >>> 3);
        short s = (short) (mBitOffset & 0x7);
        if (i >= 254) {
            this.flush(pDataOutputStream, i);
            mCodeBuffer[0] = mCodeBuffer[i];
            mBitOffset = s;
            i = 0;
        }
        if (s > 0) {
            int j = pCode << s | mCodeBuffer[i];
            mCodeBuffer[i] = (byte) j;
            mCodeBuffer[(i + 1)] = (byte) (j >> 8);
            mCodeBuffer[(i + 2)] = (byte) (j >> 16);
        }
        else {
            mCodeBuffer[i] = (byte) pCode;
            mCodeBuffer[(i + 1)] = (byte) (pCode >> 8);
        }
        mBitOffset = (short)(mBitOffset + mCodeSize);
    }

    private void writeApplBlock(DataOutputStream pDataOutputStream) throws IOException
    {
        byte[] mApplicationId = new byte[8];
        byte[] mAppAuth = new byte[3];

        String str = "NETSCAPE";
        for (int i = 0; i < mApplicationId.length; i++)
            mApplicationId[i] = (byte) str.charAt(i);
        str = "2.0";
        for (int j = 0; j < mAppAuth.length; j++)
            mAppAuth[j] = (byte) str.charAt(j);

        pDataOutputStream.writeByte(33);
        pDataOutputStream.writeByte(-1);
        pDataOutputStream.writeByte(11);
        for (int i = 0; i < mApplicationId.length; i++) {
            pDataOutputStream.writeByte(mApplicationId[i]);
        }
        for (int j = 0; j < mAppAuth.length; j++) {
            pDataOutputStream.writeByte(mAppAuth[j]);
        }
        pDataOutputStream.writeByte(3);
        pDataOutputStream.writeByte(1);
        pDataOutputStream.writeShort(0);
        pDataOutputStream.writeByte(0);
    }

    private void writeCommentBlock(DataOutputStream pDataOutputStream) throws IOException
    {
        pDataOutputStream.writeByte(33);
        pDataOutputStream.writeByte(-2);

        String str = "Triturus DEM-GIF encoder prototype";
        int i = str.length();
        pDataOutputStream.writeByte(i);
        for (int j = 0; j < i; j++)
            pDataOutputStream.writeByte(str.charAt(j));

        pDataOutputStream.writeByte(0);
    }

    private void writeGraphicsBlock(DataOutputStream pDataOutputStream) throws IOException
    {
        pDataOutputStream.writeByte(33);
        pDataOutputStream.writeByte(-7);
        pDataOutputStream.writeByte(4);
        pDataOutputStream.writeByte(0);
        pDataOutputStream.writeShort(0);
        pDataOutputStream.writeByte(0);
        pDataOutputStream.writeByte(0);
    }

    private void flush(DataOutputStream pDataOutputStream, int pVal) throws IOException
    {
        pDataOutputStream.writeByte(pVal);
        for (int i = 0; i < pVal; i++) {
            pDataOutputStream.writeByte(mCodeBuffer[i]);
        }
    }

    private void reset()
    {
        mClearCode = 256;
        mEofCode = (short) (mClearCode + 1);
        mFreeCode = (short) (mClearCode + 2);
        mCodeSize = 9;
        mMaxCode = (short) (1 << mCodeSize);
        mGifHashTable.reset();
    }

    private void writeScreenDesc(DataOutputStream pDataOutputStream) throws IOException
    {
        pDataOutputStream.writeShort(ByteSequenceChanger.perform(mImageWidth));
        pDataOutputStream.writeShort(ByteSequenceChanger.perform(mImageHeight));
        pDataOutputStream.writeByte(-9);
        pDataOutputStream.writeByte(20); // background index
        pDataOutputStream.writeByte(0);
    }

    private void writeImageDesc(DataOutputStream pDataOutputStream) throws IOException
    {
        pDataOutputStream.writeByte(44); // 2C hex. (',') as separator
        pDataOutputStream.writeShort(0); // left
        pDataOutputStream.writeShort(0); // top
        pDataOutputStream.writeShort(ByteSequenceChanger.perform(mImageWidth));
        pDataOutputStream.writeShort(ByteSequenceChanger.perform(mImageHeight));
        pDataOutputStream.writeByte(0);
    }
}
