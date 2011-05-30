package org.n52.v3d.triturus.t3dutil.operatingsystem;

import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;

import javax.swing.ImageIcon;

/**
 * Klasse mit verschiedenen Hilfsmethoden für die Arbeit mit Bilddateien. Aktuell enthält die Klasse verschiedene
 * Image-I/O-Methoden, zukünftige Erweiterungen sind vorgesehen.<p>
 * @author Torsten Heinen<p>
 * (c) 2003-2004 con terra GmbH<br>
 */
public class ImageTools
{
    /**
     * konvertiert ein awt.Image in ein BufferedImage
     *
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

	/**
	 * produziert ein BufferedImage aus den übergebenen Werten.<p>
	 * Das Array speichert für jeden Pixel die RGB(A) Komponenten in eigenen Speicherbänken:
     * <pre>
	 * [r0][p0][p1]...
	 * [g0][p0][p1]...
	 * [b0][p0][p1]...
	 * [a0][p0][p1]...
     * </pre>
	 * <tt>imageData.length</tt> muss die Anzahl der Komponenten zurückgeben.<p>
     * <i>Bem.: Die Generierung klappt meistens, aber nicht immer -> noch ein wenig buggy.</i><p>
	 * @return Repräsentation des Daten-Arrays
	 */
    public static BufferedImage createBufferedImage(int width, int height, boolean withAlpha, byte[] imageData) {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel colorModel = new ComponentColorModel(
            colorSpace, withAlpha, false,
            Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

        int scanlineStride, pixelStride, dataElements;
        int[] bandoffsets;
        if (withAlpha) {
            scanlineStride = width * 4;
            dataElements = pixelStride = 4;
            bandoffsets = new int[]{0,1,2,3};
        }
        else {
            scanlineStride = width * 3;
            dataElements = pixelStride = 3;
            bandoffsets = new int[]{0,1,2};
        }

        return new BufferedImage(
                colorModel,
                Raster.createWritableRaster(
                        new PixelInterleavedSampleModel(
                                DataBuffer.TYPE_BYTE, //data type
                                width, height,
                                pixelStride,
                                scanlineStride,
                                bandoffsets),
                        new DataBufferByte(
                                imageData,
                                dataElements),
                        null), // Location default 0,0
                false, // isRasterPremultiplied
                new Hashtable()); // hashtable properties ?
    }

    /**
     * @param i
     * @param j
     * @param b
     * @param buffer
     * @return
     */
    public static BufferedImage createBufferedImage(int i, int j, boolean b, ByteBuffer buffer) {
        return createBufferedImage(i, j, b, buffer.array());
    }

    public static BufferedImage createBufferedImage(int width, int height, boolean withAlpha, byte[][] imageData)
    {
        throw new T3dNotYetImplException("wird das benötigt?");
    }

    /**
     * todo: Dokumentation
     * @param imageLocation
     * @return
     */
    public static final BufferedImage loadImage(String imageLocation)
    {
        BufferedImage bufferedImage = null;
        ClassLoader  fileLoader  = ClassLoader.getSystemClassLoader();
        InputStream  input  = fileLoader.getResourceAsStream(imageLocation);

        try {
            if(input == null)
                input =  new FileInputStream(imageLocation);
            bufferedImage = javax.imageio.ImageIO.read(input);
        }
        catch( Exception e){
            try{
                bufferedImage = javax.imageio.ImageIO.read(new java.io.File(imageLocation));
            }catch( Exception newE){}
        }
        return bufferedImage;
	}
}
