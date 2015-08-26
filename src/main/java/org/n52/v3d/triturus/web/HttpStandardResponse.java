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
package org.n52.v3d.triturus.web;

import org.n52.v3d.triturus.core.T3dException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.StringTokenizer;

import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;

/**
 * todo engl. JavaDoc
 * Hilfsklasse f�r die Versendung von Standard-Antworten durch Servlets. Anwendungsf�lle sind z. B. das Senden von
 * vorbereiteter Antworten auf GetCapabilities-Anfragen und die R�ckgabe von Service-Exceptions.<p>
 * @author Benno Schmidt
 */
public class HttpStandardResponse
{
    static public boolean sExplicitFlushAndClose = true;

    private static final short sPNGOutput = 1;
    private static final short sJPEGOutput = 2;
    private static final short sBMPOutput = 3;

    /**
     * sendet die angegebene XML-Datei. Diese Methode bietet sich z. B. an, um die vorbereitete Antwort auf
     * GetCapability-Anfragen zu senden.<p>
     * Bem.: Seitens der Web-Anwendung d�rfen nicht gleichzeitig <tt>PrintWriter</tt> und <tt>ServletOutputStream</tt>
     * offen sein, da seitens der <tt>HttpServletResponse</tt> sonst eine <tt>IllegalStateException</tt> geworfen wird;
     * siehe dazu Dokumentation der Servlet-API. Diese Exception wird von der vorliegenden Methode, die stets den
     * <tt>ServletOutputStream</tt> verwendet, ggf. als <tt>T3dException</tt> weitergegeben (siehe Quellcode).<p>
     * @param pFilename Name der XML-Datei mit vollst�ndiger Pfadangabe (auf dem Server)
     * @param pResponse Antwort-Objekt
     * @throws T3dException
     */
    public void sendXMLFile(String pFilename, HttpServletResponse pResponse) throws T3dException
    {
        boolean usePrintWriter = false;

        if (pFilename == null || pFilename.length() <= 0) {
            this.sendException("The file \"" + pFilename + "\" is not available.", pResponse);
            return;
        }

        BufferedReader lDatRead = null;
        try {
            lDatRead = new BufferedReader(new FileReader(pFilename));
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Internal error while reading \"" + pFilename + "\".");
        }

        String enc = "text/xml"; // zu verwendendes Encoding
        pResponse.setContentType(enc); // MIME-Typ f�r Antwort setzen
        if (usePrintWriter) {
            PrintWriter out = null;
            try {
                out = pResponse.getWriter(); // PrintWriter auf die Antwort aufsetzen

                // Ergebnisdatei zeilenweise als Antwort senden:
                String line = lDatRead.readLine();
                while (line != null) {
                    out.println(line);
                    line = lDatRead.readLine();
                }
                lDatRead.close();
            }
            catch (UnsupportedEncodingException e) {
                // falls sich der in setContentType spezifizierte Zeichensatz nicht verwenden l�sst
                throw new T3dException("Could not send exception (encoding \"" + enc + "\" not supported): "
                    + e.getMessage());
            }
            catch (IllegalStateException e) {
                // getOutputStream() wurde f�r Response-Objekt bereits aufgerufen
                throw new T3dException("Could not send exception (IllegalStateException): " + e.getMessage());
            }
            catch (IOException e) {
                throw new T3dException("Could not send exception (I/O error): " + e.getMessage());
            }
            // Die beiden folgenden Operationen erledigt eigentlich die Servlet-Engine:
            if (sExplicitFlushAndClose) {
                out.flush();
                out.close();
            }
        }
        else {
            ServletOutputStream out = null;
            try {
                out = pResponse.getOutputStream(); // OutputStream holen

                // Ergebnisdatei zeilenweise als Antwort senden:
                String line = lDatRead.readLine();
                while (line != null) {
                    out.println(line);
                    line = lDatRead.readLine();
                }
                lDatRead.close();
                // Die beiden folgenden Operationen erledigt eigentlich die Servlet-Engine:
                if (sExplicitFlushAndClose) {
                    out.flush();
                    out.close();
                }
            }
            catch (IllegalStateException e) {
                // getOutputStream() wurde f�r Response-Objekt bereits aufgerufen
            }
            catch (IOException e) {
                throw new T3dException("Could not send exception (I/O error): " + e.getMessage());
            }
        }
    }

    /**
     * sendet eine Service-Exception mit der angegebenen Fehlermeldung.<p>
     * Bem.: Die Struktur der XML-Antwort ist kompatibel zu der in Anhang A.3 der WMS-Spezifikation 1.1.1 vorgegebenen
     * Struktur (EXCEPTIONS-Wert "application/vnd.ogc.se_xml").<p>
     * Bem.: Seitens der Web-Anwendung d�rfen nicht gleichzeitig <tt>PrintWriter</tt> und <tt>ServletOutputStream</tt>
     * offen sein, da seitens der <tt>HttpServletResponse</tt> sonst eine <tt>IllegalStateException</tt> geworfen wird;
     * siehe dazu Dokumentation der Servlet-API. Diese Exception wird von der vorliegenden Methode, die stets den
     * <tt>ServletOutputStream</tt> verwendet, ggf. als <tt>T3dException</tt> weitergegeben (siehe Quellcode).<p>
     * @param pMessage Fehlermeldung
     * @param pResponse Antwort-Objekt
     * @throws T3dException
     */
    public void sendException(String pMessage, HttpServletResponse pResponse) throws T3dException
    {
        boolean usePrintWriter = false;

        String enc = "text/xml"; // zu verwendendes Encoding
        pResponse.setContentType(enc); // MIME-Typ f�r Antwort setzen

        if (usePrintWriter) {
            PrintWriter out = null;
            try {
                out = pResponse.getWriter(); // PrintWriter auf die Antwort aufsetzen
            }
            catch (UnsupportedEncodingException e) {
                // falls sich der in setContentType spezifizierte Zeichensatz nicht verwenden l�sst
                throw new T3dException("Could not send exception (encoding \"" + enc + "\" not supported): "
                    + e.getMessage());
            }
            catch (IllegalStateException e) {
                // getOutputStream() wurde f�r Response-Objekt bereits aufgerufen
                throw new T3dException("Could not send exception (IllegalStateException): " + e.getMessage());                
            }
            catch (IOException e) {
                throw new T3dException("Could not send exception (I/O error): " + e.getMessage());
            }

            out.println("<?xml version='1.0' encoding=\"UTF-8\" standalone=\"no\" ?>");
            out.println("<!DOCTYPE ServiceExceptionReport SYSTEM \"http://www.digitalearth.gov/wmt/xml/exception_1_1_0.dtd\">");
            out.println("<ServiceExceptionReport version=\"1.1.0\">");
            out.println("  <ServiceException>");
            out.println("    Request rejected due to errors.");
            out.println("	   Reason: " + pMessage);
            out.println("  </ServiceException>");
            out.println("</ServiceExceptionReport>");
            out.println();

            // Die beiden folgenden Operationen erledigt eigentlich die Servlet-Engine:
            if (sExplicitFlushAndClose) {
                out.flush();
                out.close();
            }
        }
        else {
            ServletOutputStream out = null;
            try {
                out = pResponse.getOutputStream(); // OutputStream holen

                out.println("<?xml version='1.0' encoding=\"UTF-8\" standalone=\"no\" ?>");
                out.println("<!DOCTYPE ServiceExceptionReport SYSTEM \"http://www.digitalearth.gov/wmt/xml/exception_1_1_0.dtd\">");
                out.println("<ServiceExceptionReport version=\"1.1.0\">");
                out.println("  <ServiceException>");
                out.println("    Request rejected due to errors.");
                out.println("	   Reason: " + pMessage);
                out.println("  </ServiceException>");
                out.println("</ServiceExceptionReport>");
                out.println();

                // Die beiden folgenden Operationen erledigt eigentlich die Servlet-Engine:
                if (sExplicitFlushAndClose) {
                    out.flush();
                    out.close();
                }
            }
            catch (IllegalStateException e) {
                // getOutputStream() wurde f�r Response-Objekt bereits aufgerufen
            }
            catch (IOException e) {
                throw new T3dException("Could not send exception (I/O error): " + e.getMessage());
            }
        }
    }

    /**
     * sendet eine Service-Exception mit der angegebenen Fehlermeldung als Grafik.<p>
     * Unterst�tzt werden die Bildformate mit den MIME-Typen "image/png", "image/jpeg" und "image/bmp".<p>
     * Bem.: Vgl. WMS-Spezifikation 1.1.1, EXCEPTIONS-Wert "application/vnd.ogc.se_inimage".<p>
     * @param pMessage Fehlermeldung
     * @param pResponse Antwort-Objekt
     * @param pFormat MIME-Typ zu verwendenden Bildformats, z. B. "image/png"
     * @param pWidth Bildbreite in Pixeln
     * @param pHeight Bildh�he in Pixeln
     * @throws T3dException
     */
    public void sendException(String pMessage, HttpServletResponse pResponse, String pFormat, int pWidth, int pHeight)
        throws T3dException
    {
        final int lTextSize = 12;

        try {
            BufferedImage lImage = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
            String resExt = MimeTypeHelper.getFileExtension(pFormat);

            Graphics2D g = lImage.createGraphics();
            g.drawImage(lImage, 0, 0, null);
            g.setColor(new java.awt.Color(1.0f, 1.0f, 1.0f));
            g.fillRect(0, 0, pWidth, pHeight);
            g.setColor(new java.awt.Color(0.0f, 0.0f, 0.0f));
            Font font = new Font("SansSerif", 0 /* Style als int, siehe ggf. API-Dok.*/, lTextSize);
            g.setFont(font);
            StringTokenizer st = new StringTokenizer(pMessage, "|");
            int i = 0;
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                i++;
                g.drawString(tok, 5, (int)(1.3 /*-zeilig*/ * i * lTextSize));
            }
            g.dispose();

            OutputStream out = pResponse.getOutputStream();
            short lOutputFormat = -1;
            if (pFormat.equalsIgnoreCase("image/png")) lOutputFormat = sPNGOutput;
            if (pFormat.equalsIgnoreCase("image/jpeg")) lOutputFormat = sJPEGOutput;
            if (pFormat.equalsIgnoreCase("image/bmp")) lOutputFormat = sBMPOutput;
            if (pFormat == null || pFormat.length() <= 0)
                throw new T3dException("No exception output format specified.");
            if (lOutputFormat <= 0)
                throw new T3dException("Unsupported exception output format \"" + pFormat + "\". ");

            switch (lOutputFormat) {
                case sPNGOutput:
                    ImageIO.setUseCache(false); // wichtig!
                    pResponse.setContentType("image/png");
                    try {
                        ImageIO.write(lImage, resExt, out); // resExt ist informaler Formatname...
                    }
                    catch (Exception e) {
                        //e.printStackTrace();
                        throw new T3dException("Could not send PNG image. " + e.getMessage());
                    }
                    break;
                case sJPEGOutput:
                    try {
                        //ImageIO.setUseCache(false); // wichtig!
                        pResponse.setContentType("image/jpeg");
                        JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out); // JPEG-Encoder instanziieren
                        JPEGEncodeParam prm = enc.getDefaultJPEGEncodeParam(lImage);
                        prm.setQuality(1.0f, false); // Qualit�t auf 100% setzen
                        enc.setJPEGEncodeParam(prm);
                        enc.encode(lImage); // Bild als JPG encoden und an Client senden
                    } catch (Exception e) {
                        //e.printStackTrace();
                        throw new T3dException("Could not send JPEG image. " + e.getMessage());
                    }
                    break;
                case sBMPOutput:
                    try {
                        pResponse.setContentType("image/bmp");
                        // Merkw�rdig, dass nachstehender Code praktisch das korrekte Resultat liefert... (todo)
                        JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out); // JPEG-Encoder instanziieren
                        JPEGEncodeParam prm = enc.getDefaultJPEGEncodeParam(lImage);
                        prm.setQuality(1.0f, false); // Qualit�t auf 100% setzen
                        enc.setJPEGEncodeParam(prm);
                        enc.encode(lImage); // Bild als JPG encoden und an Client senden
                        ImageIO.write(lImage, "jpg", out); // !
                    } catch (Exception e) {
                        //e.printStackTrace();
                        throw new T3dException("Could not send BMP image. " + e.getMessage());
                    }
                    break;
            }
            out.close();
        }
        catch (IOException e) {
            throw new T3dException("An I/O exception occured. The application could not send an image reponse.");
        }
    }

    /**
     * sendet eine XML-kodierte Systemmeldung.<p>
     * @param pMessage Meldung
     * @param pResponse Antwort-Objekt
     * @throws T3dException
     */
    public void sendMessage(String pMessage, HttpServletResponse pResponse) throws T3dException
    {
        //System.out.println("sendMessage(" + pMessage + ")");

        pResponse.setContentType("text/plain"); // MIME-Typ f�r Antwort setzen
        PrintWriter out = null;
        try {
            out = pResponse.getWriter(); // PrintWriter auf die Antwort aufsetzen
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }

        out.println(pMessage);
        out.println();

        // Die beiden folgenden Operationen erledigt eigentlich die Servlet-Engine:
        if (sExplicitFlushAndClose) {
            out.flush();
            out.close();
        }
    }
}
