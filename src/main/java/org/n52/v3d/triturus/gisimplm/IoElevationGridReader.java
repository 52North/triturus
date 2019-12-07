/**
 * Copyright (C) 2007-2018 52 North Initiative for Geospatial Open Source
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
 * - Apache License, version 2.0 
 * - Apache Software License, version 1.0 
 * - GNU Lesser General Public License, version 3 
 * - Mozilla Public License, versions 1.0, 1.1 and 2.0 
 * - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders 
 * if the distribution is compliant with both the GNU General Public license 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.gisimplm;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;
import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Reader to import elevation grids from various file formats or URL 
 * locations. The result will be <tt>GmSimpleElevationGrid</tt> objects.
 *
 * @author Benno Schmidt, Martin May, Adhitya Kamakshidasan
 */
public class IoElevationGridReader extends IoObject 
{
    private String logString = "";

    private String format;
    private GmSimpleElevationGrid elevGrid = null;

    /**
     * Identifier to be used to process elevation-grids in ArcInfo ASCII 
     * grid format.
     */
    public static final String ARCINFO_ASCII_GRID = "ArcIGrd";
    /**
     * Identifier to be used to process X3DOM-encoded elevation-grids.
     */
    public static final String X3DOM = "X3DOM";

    /**
     * Constructor. As parameter, a format type identifier has to be set.
     * In case, the given format type is not supported, an exception will be
     * thrown. Currently, these formats are supported:
     * <ul>
     * <li><i>ArcIGrd:</i> ArcInfo ASCII grids</li>
     * <li><i>AcGeo:</i> ACADGEO format (lattice without color-information</li>
     * <li><i>BSQ:</i> Byte-sequential ESRI-format</li>
     * <li><i>X3D:</i> X3D-encoded elevation grids</li>
     * </ul>
     * 
     * @param format Format-string, e.g. <tt></tt>&quot;ArcIGrd&quot;</tt>
     * @see IoElevationGridReader#ARCINFO_ASCII_GRID
     */
    public IoElevationGridReader(String format) {
        logString = this.getClass().getName();
        this.setFormatType(format);
    }

    public String log() {
        return logString;
    }

    /**
     * sets the format type.
     *
     * @param format Format-string (e.g. <tt></tt>&quot;ArcIGrd&quot;</tt>)
     * @see IoElevationGridReader#ARCINFO_ASCII_GRID
     */
    public void setFormatType(String format) {
        this.format = format;
    }

    /**
     * @deprecated
     * reads an elevation-grid from a file or URL location.
     *
     * @param location File path or valid URL
     * @return Elevation-grid, or <i>null</i> if an error occurs
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public GmSimpleElevationGrid readFromFile(String location) 
    	throws T3dException 
    {
        return this.read(location);
    }

    /**
     * reads an elevation-grid from a file or URL location.
     * <p>
     * Note: URL strings shall start with <tt>"http"</tt>, file specifications
     * that hold absolute paths with "file" (todo: this remains to be tested).
     * Relative paths are supported (e.g.: <tt>"testdata/..."</tt>), too.
     *
     * @param location File path or valid URL
     * @return Elevation-grid, or <i>null</i> if an error occurs
     * @throws org.n52.v3d.triturus.core.T3dNotYetImplException
     * @throws org.n52.v3d.triturus.core.T3dException
     */
    public GmSimpleElevationGrid read(String location) throws T3dException {
        InputStream is;
        try {
            if (location.startsWith("http")) {
                is = this.createInputStream(new URL(location));
            }
            else {
                is = this.createInputStream(location);
            }
        }
        catch (MalformedURLException e) {
            throw new T3dException("Couldn't read location \"" + location + "\" (malformed URL).");
        }
        catch (IOException e) {
            throw new T3dException("Couldn't read location \"" + location + "\" (IO error).");
        }

        int i = 0;
        if (format.equalsIgnoreCase(ARCINFO_ASCII_GRID)) i = 1;
        if (format.equalsIgnoreCase("AcGeo")) i = 2;
        if (format.equalsIgnoreCase("BSQ")) i = 3;
        if (format.equalsIgnoreCase(X3DOM)) i = 4;
        // --> add more types here...

        try {
            switch (i) {
                case 1:
                    this.readArcInfoAsciiGrid(this.createBufferedReader(is));
                    break;
                case 2:
                    this.readAcadGeoGrid(this.createBufferedReader(is));
                    break;
                case 3:
                    this.readEsriBandSequential(location);
                    break;
                case 4:
                    this.readX3D(location);
                    break;
                // --> add more types here...

                default:
                    throw new T3dNotYetImplException("Unsupported file format");
            }
        }
        catch (T3dException e) {
            throw e;
        }

        return elevGrid;
    }

    private InputStream createInputStream(URL url) throws IOException {
        return url.openConnection().getInputStream();
    }

    private InputStream createInputStream(String filename) 
    	throws FileNotFoundException 
    {
        InputStream input = 
        	this.getClass().getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            input = new FileInputStream(filename);
        }
        return input;
    }

    private BufferedReader createBufferedReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    /**
     * reads a 2-D float array from an input stream and generates a grid from 
     * it.
     *
     * @param inputStream Stream to read data from
     * @param env Bounding-box of target-grid
     * @param width Number of grid-cells in x-direction (columns)
     * @param height Number of grid-cells in y-direction (rows)
     * @throws IOException
     */
    public GmSimpleElevationGrid readRawFloats(
    	InputStream inputStream, VgEnvelope env, int width, int height) 
    	throws IOException 
    {
        int bufferSize = (width + 1) * (height + 1) * 4;
        ByteBuffer bb = ByteBuffer.allocate(bufferSize);
        int k = 0;
        byte[] bytes = new byte[50000]; // buffer
        while ((k = inputStream.read(bytes)) != -1) {
            bb.put(bytes, 0, k);
            //System.out.println("read bytes:"+k+"/pos="+bb.position()+"/size="+bufferSize);
        }

        // Elevation-grid construction:
        elevGrid = new GmSimpleElevationGrid(
                width, height,
                new GmPoint(env.getXMin(), env.getYMin(), 0), // Origin
                env.getExtentX() / width, // Cell-size x-direction
                env.getExtentY() / height); // Cell-size y-direction
        //System.out.println("Input: width: "+width+ " height: "+height+" env: "+env+"\nElevationGrid: "+elevationGrid);

        bb.rewind();
        FloatBuffer fb = bb.asFloatBuffer();
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                elevGrid.setValue(i, j, fb.get());
            }
            //System.out.println("Height: "+i+" heights="+fb.position());
        }
        return elevGrid;
    }

    private void readArcInfoAsciiGrid(BufferedReader datRead) 
    	throws T3dException 
    {
        try {
            // Read header:
            int nCols = this.parseInt("ncols", datRead.readLine());
            int nRows = this.parseInt("nrows", datRead.readLine());
            float xllcorner = this.parseFloat("xllcorner", datRead.readLine());
            float yllcorner = this.parseFloat("yllcorner", datRead.readLine());
            float cellSize = this.parseFloat("cellsize", datRead.readLine());
            float NODATA_value = this.parseFloat("NODATA_value", datRead.readLine());

            // Determine bounding-box:
            double xFrom = xllcorner + cellSize / 2.; 
            double yFrom = yllcorner + cellSize / 2.; 
            // Since it is a lattice (not a grid), half cell-sizes have to be added. 

            // Elevation-grid construction:
            elevGrid = new GmSimpleElevationGrid(
                    nCols, nRows,
                    new GmPoint(xFrom, yFrom, 0.), // Origin
                    cellSize, // Cell-size x-direction
                    cellSize); // Cell-size y-direction
            elevGrid.setLatticeInterpretation(); // todo: okay? Consequences?

            // Read elevation values and populate target-grid:
            float z = 0.f;
            String line = null;

            for (int i = nRows - 1; i >= 0; i--) {
                line = datRead.readLine();

                if (line != null) {
                    StringTokenizer st = new StringTokenizer(line);
                    for (int j = 0; j < nCols; j++) {
                        try {
                            z = Float.parseFloat(st.nextToken());
                        }
                        catch (NumberFormatException nfe) {
                            z = 0.f;
                        }
                        if (z != NODATA_value) {
                            elevGrid.setValue(i, j, z);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw e;
        }
    } // readArcInfoAsciiGrid()

    // Helpers for readArcInfoAsciiGrid():
    
    private int parseInt(String check, String line) throws T3dException {
        StringTokenizer st = new StringTokenizer(line);
        String[] tokens = {"", ""};
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i] = st.nextToken();
            if (tokens[i].length() > 0) {
                i++;
            }
            if (i > 1) {
                break;
            }
        }
        if (i == 2 && tokens[0].toLowerCase().equals(check.toLowerCase())) {
            return Integer.parseInt(tokens[1]);
        }
        else {
            throw new T3dException("Header-value \"" + check + "\" is missing in input file.");
        }
    }

    private float parseFloat(String check, String line) throws T3dException {
        StringTokenizer st = new StringTokenizer(line);
        String[] tokens = {"", ""};
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens[i] = st.nextToken();
            if (tokens[i].length() > 0) {
                i++;
            }
            if (i > 1) {
                break;
            }
        }
        if (i == 2 && tokens[0].toLowerCase().equals(check.toLowerCase())) {
            return Float.parseFloat(tokens[1]);
        }
        else {
            throw new T3dException("Header-value \"" + check + "\" is missing in input file.");
        }
    }

    private void readAcadGeoGrid(BufferedReader datRead) throws T3dException {
        String line = "";
        int lineNumber = 0;

        try {
            String tok1, tok2, tok3, tok4;

            line = datRead.readLine(); // line 1
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("GRID:")) {
                throw new T3dException("Expected key-word GRID: in line " + lineNumber);
            }

            line = datRead.readLine(); // line 2
            lineNumber++;
            ; // "C=[ON|OFF]"; will be ignored

            line = datRead.readLine(); // line 3
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("FROM")) {
                throw new T3dException("Expected key-word FROM in line " + lineNumber);
            }
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            double xFrom = this.toDouble(tok2);
            double yFrom = this.toDouble(tok3);

            line = datRead.readLine(); // line 4
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("TO")) {
                throw new T3dException("Expected key-word TO in line " + lineNumber);
            }
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            double xTo = this.toDouble(tok2);
            double yTo = this.toDouble(tok3);

            line = datRead.readLine(); // line 5
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("SIZE")) {
                throw new T3dException("Expected key-word SIZE in line " + lineNumber);
            }
            tok2 = this.getStrTok(line, 2, " ");
            tok3 = this.getStrTok(line, 3, " ");
            if (!tok3.equalsIgnoreCase("x")) {
                throw new T3dException("Expected token 'x' in line " + lineNumber);
            }
            tok4 = this.getStrTok(line, 4, " ");
            int nCols = this.toInt(tok2);
            int nRows = this.toInt(tok4);

            // Elevation-grid construction:
            elevGrid = new GmSimpleElevationGrid(
                    nCols, nRows,
                    new GmPoint(xFrom, yFrom, 0.), // Ursprungspunkt
                    (xTo - xFrom) / ((double) nCols - 1.), // Gitterweite x-Richtung
                    (yTo - yFrom) / ((double) nRows - 1.)); // Gitterweite y-Richtung
            elevGrid.setLatticeInterpretation();

            // Populate grid with elevation values:
            double z;
            elevGrid.setZBoundsInvalid(); // Performance!

            for (int j = 0; j < nCols; j++) {
                for (int i = 0; i < nRows; i++) {
                    line = datRead.readLine();
                    lineNumber++;
                    tok1 = this.getStrTok(line, 1, " ");
                    z = this.toDouble(tok1);
                    elevGrid.setValue(i, j, z);
                }
            }

            line = datRead.readLine(); // last row
            lineNumber++;
            tok1 = this.getStrTok(line, 1, " ");
            if (!tok1.equalsIgnoreCase("END")) {
                throw new T3dException("Expected key-word END in line " + lineNumber);
            }

            datRead.close();
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in line " + lineNumber);
        }
    } // readAcadGeoGrid()

    // Helpers for readAcadGeoGrid():
    
    // Extraction of i-th token (i >= 1!, i max. = 4) from a string ('pSep' as separator):
    private String getStrTok(String str, int i, String sep) throws T3dException {
        ArrayList<String> strArr = new ArrayList<String>();
        strArr.add(str);
        int i0 = 0, i1 = 0, k = 0;
        while (k < 4 && i1 >= 0) {
            i1 = str.indexOf(sep, i0);
            if (i1 >= 0) {
                if (k == 0) {
                    strArr.set(0, str.substring(i0, i1));
                }
                else {
                    strArr.add(str.substring(i0, i1));
                }
                i0 = i1 + 1;
                k++;
            }
        }
        if (k <= 3) {
            strArr.add(str.substring(i0));
        }
        if (i < 1 || i > 4) {
            throw new T3dException("Logical parser error.");
        }
        return (String) strArr.get(i - 1);
    }

    private double toDouble(String str) {
        return Double.parseDouble(str);
    }

    private int toInt(String str) {
        return Integer.parseInt(str);
    }

    private void readEsriBandSequential(String filename) throws T3dException 
    {
        try {
            String bsqName = filename, hdrName = null, bqwName = null;
            boolean isFloat;
            
            if (bsqName.toLowerCase().endsWith("bsq")) {
                hdrName = bsqName.substring(0, bsqName.length() - 3) + "hdr";
                bqwName = bsqName.substring(0, bsqName.length() - 3) + "bqw";
            }
            else {
                throw new T3dException("File-name not correct: " + filename);
            }

            // Read header:
            FileReader hdrFileRead = new FileReader(hdrName);
            BufferedReader hdrDatRead = new BufferedReader(hdrFileRead);
            StreamTokenizer hdrTokRead = new StreamTokenizer(hdrDatRead);
            hdrTokRead.lowerCaseMode(true); // fetch all tokens (in lower case)
            hdrTokRead.commentChar(35); // Hashes mark comment lines...
            hdrTokRead.eolIsSignificant(false); // line feeds are irrelevant
            hdrTokRead.wordChars(65, 90); // upper-case characters
            hdrTokRead.wordChars(97, 122); // lower-case characters
            int tokType = 0;
            hdrTokRead.parseNumbers(); // The numbers are read 'directly'
            String valueName = "";
            Hashtable<String, Serializable> 
            	header = new Hashtable<String, Serializable>();
            // traverse everything and put it to the hash-table pairwise
            do {
                tokType = hdrTokRead.nextToken();
                if (tokType == StreamTokenizer.TT_WORD) {
                    valueName = hdrTokRead.sval;
                }
                tokType = hdrTokRead.nextToken();
                if (tokType == StreamTokenizer.TT_WORD) {
                    header.put(valueName, hdrTokRead.sval);
                }
                else if (tokType == StreamTokenizer.TT_NUMBER) {
                    Integer value = new Integer((int) hdrTokRead.nval);
                    header.put(valueName, value);
                }
            } while (tokType != StreamTokenizer.TT_EOF);

            // Check some values:
            if (header.get("pixeltype").equals("float")) {
                isFloat = true;
            }
            else if (header.get("pixeltype").equals("int") || header.get("pixeltype").equals("integer")) {
                isFloat = false;
            }
            else {
                throw new T3dException("Pixeltype not supported: " + header.get("pixeltype"));
            }
            if (((Integer) header.get("nbands")).intValue() != 1) {
                throw new T3dException("Number of bands not supported: " + header.get("nbands"));
            }

            // Read lower-left:
            double LLx = 0;
            double LLy = 0;
            double Dx = 10;
            double Dy = 10;
            int colOrder = 1; // Values will be read to the left
            int rowOrder = 1; // and to top
            FileReader bqwFileRead = new FileReader(bqwName);
            BufferedReader bqwDatRead = new BufferedReader(bqwFileRead);
            StreamTokenizer bqwTokRead = new StreamTokenizer(bqwDatRead);
            bqwTokRead.lowerCaseMode(true); // fetch all tokens (in lower case)
            bqwTokRead.commentChar(35); // Hashes mark comment-lines...
            bqwTokRead.eolIsSignificant(false); // line feeds are irrelevant
            bqwTokRead.wordChars(65, 90); // upper-case characters
            bqwTokRead.wordChars(97, 122); // lower-case characters
            tokType = 0;
            bqwTokRead.parseNumbers(); // The numbers are read 'directly'
            tokType = bqwTokRead.nextToken();
            if (tokType == StreamTokenizer.TT_NUMBER) {
                Dx = bqwTokRead.nval;
            }
            tokType = bqwTokRead.nextToken();
            tokType = bqwTokRead.nextToken();
            tokType = bqwTokRead.nextToken();
            if (tokType == StreamTokenizer.TT_NUMBER) {
                Dy = bqwTokRead.nval;
            }
            tokType = bqwTokRead.nextToken();
            if (tokType == StreamTokenizer.TT_NUMBER) {
                LLx = bqwTokRead.nval;
            }
            tokType = bqwTokRead.nextToken();
            if (tokType == StreamTokenizer.TT_NUMBER) {
                LLy = bqwTokRead.nval;
            }

            // Origin correction to LL (if necessary)
            if (Dx < 0) {
                LLx = LLx + Dx * ((Integer) header.get("ncols")).intValue();
                Dx = 0 - Dx;
                colOrder = -1;
            }
            if (Dy < 0) {
                LLy = LLy + Dy * ((Integer) header.get("nrows")).intValue();
                Dy = 0 - Dy;
                rowOrder = -1;
            }

            // Elevation-grid construction:
            elevGrid = new GmSimpleElevationGrid(
                    ((Integer) header.get("nrows")).intValue(), ((Integer) header.get("ncols")).intValue(),
                    new GmPoint(LLx, LLy, 0.), // Origin
                    Dx, // Cell-size x-direction
                    Dy); // Cell-size y-direction
            elevGrid.setLatticeInterpretation();
            elevGrid.setZBoundsInvalid(); // Performance!

            FileInputStream bsqFS = new FileInputStream(bsqName);

            // Populate grid with elevation-values:
            int startrow, startcol;
            if (rowOrder == -1) {
                startrow = elevGrid.numberOfRows() - 1;
            }
            else {
                startrow = 0;
            }
            if (colOrder == -1) {
                startcol = elevGrid.numberOfColumns() - 1;
            }
            else {
                startcol = 0;
            }

            System.out.println("i(rows): " + startrow + " j(cols): " + startcol + " rowOrder: " + rowOrder + " colOrder: " + colOrder);
            ByteBuffer lBBuf = ByteBuffer.allocateDirect(elevGrid.numberOfColumns() * 4);
            FloatBuffer lBuf = lBBuf.asFloatBuffer();
            FileChannel fc = bsqFS.getChannel();

            for (int i = startrow; i >= 0 && i < elevGrid.numberOfRows(); i = i + rowOrder) { // Reihenfolge?
                fc.read(lBBuf);
                for (int j = startcol; j >= 0 && j < elevGrid.numberOfColumns(); j = j + colOrder) {
//					int zRaw = 0;
//					zRaw = bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
//					zRaw = zRaw<<8;
//					zRaw = zRaw|bsqFS.read();
                    if (isFloat) {
//						float z = Float.intBitsToFloat(zRaw);
                        float z = lBuf.get(j);
                        if (z > -8000) {
                            elevGrid.setValue(i, j, z);
                            // Here, one could check for NaN, but...
                        }
                    }
                    else {
                        elevGrid.setValue(i, j, lBuf.get(j));
                    }
                }
                System.out.println("Reihe: " + i);
                lBBuf.clear();
            }

            bsqFS.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + filename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Read error in \"" + filename + "\".");
        }
    } // readEsriBandSequential()

    private void readX3D(String filename) throws T3dException {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(filename);

            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList X3DNodes = doc.getElementsByTagName("X3D");
            Node X3DNode = X3DNodes.item(0);
            Element element = (Element) X3DNode;

            NodeList elevationGridNodes = element.getElementsByTagName("ElevationGrid");
            NodeList metadataNodes = element.getElementsByTagName("MetadataDouble");

            String origin = "";
            boolean originPresent = false;
            double xllcorner = 0.0, yllcorner = 0.0;

            for (int temp = 0; temp < metadataNodes.getLength() && !originPresent; temp++) {
                Node metadataNode = metadataNodes.item(temp);
                Element metadataElement = (Element) metadataNode;
                if ("origin".equals(metadataElement.getAttribute("DEF"))) {
                    origin = metadataElement.getAttribute("value");
                    originPresent = true;
                }
            }
            
            if (originPresent) {
                String[] originCoordinates = origin.split(",");
                xllcorner = Double.parseDouble(originCoordinates[0]);
                yllcorner = Double.parseDouble(originCoordinates[1]);
            }

            Node elevationGridNode = elevationGridNodes.item(0);
            element = (Element) elevationGridNode;

            int nCols = Integer.parseInt(element.getAttribute("xDimension"));
            int nRows = Integer.parseInt(element.getAttribute("zDimension"));
            double xSpacing = Double.parseDouble(element.getAttribute("xSpacing"));
            double zSpacing = Double.parseDouble(element.getAttribute("zSpacing"));
            String[] height = element.getAttribute("height").split(" ");
            
            System.out.println(nCols + " " + nRows + " " + xSpacing + " " + zSpacing + " " + xllcorner + " " + yllcorner);
            
            double xFrom = xllcorner + xSpacing / 2.; 
            double yFrom = yllcorner + zSpacing / 2.; 
            // Since it is a lattice (not a grid), half cell-sizes have to be added. 

            elevGrid = new GmSimpleElevationGrid(
                    nCols, nRows,
                    new GmPoint(xFrom, yFrom, 0.), // Origin point
                    xSpacing, // Cell-size x-direction
                    zSpacing); // Cell-size y-direction
            
            elevGrid.setLatticeInterpretation();
            elevGrid.setZBoundsInvalid(); 

            float z = 0.f;
            int k = 0;

            for (int i = nRows - 1; i >= 0; i--) {
                for (int j = 0; j < nCols; j++) {
                    z = Float.parseFloat(height[k]);
                    elevGrid.setValue(i, j, z);
                    k++;
                }
            }
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    } // readX3D()
}
