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
package org.n52.v3d.triturus.examples.srtm90import;

import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.survey.TileLocator;
import org.n52.v3d.triturus.vgis.VgEnvelope;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/**
 * Triturus example application: Reads SRTM90 data and transforms them into tiles that can be processed by the 52N
 * terrainServer.<br /><br />
 * <i>German:</i> Beispielanwendung: Einlesen von SRTM 90 m-Daten und Aufbereitung zu einer seitens des 52N
 * terrainServers verwendbaren Kachelung.
 * @author Benno Schmidt
 */
public class Srtm90Import
{
    int mHiFrom, mHiTo, mLoFrom, mLoTo;

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("Usage: java Srtm90Import <input-file> <tile-locator ID> <cellsize>");
            return;
        }

        Srtm90Import app = new Srtm90Import();
        app.run(args[0], args[1], Double.parseDouble(args[2]));
    }

    private void run(String pInputFile, String pFileLoc, double pCellSize)
    {
        GmSimpleElevationGrid lGrid = this.readArcInfoAsciiGrid(pInputFile);
        lGrid.getGeometry().setSRS("EPSG:4326");
        lGrid.envelope().setSRS("EPSG:4326");

        this.determineTiles(lGrid.envelope(), pFileLoc);

        if (mHiFrom >= 0 && mHiTo >= 0 && mLoFrom >= 0 && mLoTo >= 0) {
            FltElevationGrid2PointSet lFlt = new FltElevationGrid2PointSet();
            for (int hi = mHiFrom; hi <= mHiTo; hi++) {
                for (int lo = mLoFrom; lo <= mLoTo; lo++) {
                    System.out.println("pCellSize = " + pCellSize + ", inverse bbox = " + new TileLocator("test").envelope(hi, lo, pFileLoc));
                    GmSimple2dGridGeometry lGrdGeom = this.constructDestinationGrid(new TileLocator("test").envelope(hi, lo, pFileLoc), pCellSize);
                    String filename = this.constructDEMFilename(hi, lo, "c:/tmp", lGrid.envelope().getSRS(), pFileLoc);
                    System.out.println("Destination grid: " + lGrdGeom.toString() + " will be written to file " + filename + "...");

                    lFlt.setSpatialFilter(lGrdGeom.envelope());
                    ArrayList lPointList = lFlt.transform(lGrid);

                    // Zielgitter mit Werten belegen:
                    System.out.println("Starting gridding process...");
                    double lSearchRadius = 0.003;
                    GmSimpleElevationGrid lResGrid = this.gridding(lPointList, lGrdGeom, lSearchRadius);
                    System.out.println("Finished gridding process... " + lGrdGeom);

                    if (lResGrid != null) {
                        this.purgeNoDataValues(lResGrid);

                        // TODO: Hier Schleife einbauen -> unset grid cells (NO_DATA_VAL) -> 0.0 setzen!

                        IoElevationGridWriter lGridWriter = new IoElevationGridWriter(filename);
                        lGridWriter.setFormatType("ArcIGrd");
                        lGridWriter.setPrecisionXY(0);
                        lGridWriter.setPrecisionZ(0);
                        lGridWriter.writeToFile(lResGrid, filename);

                        System.out.println("Tidying up...");
                        lGrdGeom = null;
                        lPointList.clear();
                        lPointList = null;
                    }
                }
            }
        }
        else
            System.out.println("Warning: Ignoring invalid tile...");
    }

    private GmSimpleElevationGrid readArcInfoAsciiGrid(String pFileName) {
        IoElevationGridReader lReader = new IoElevationGridReader("ArcIGrd");
        try {
             return lReader.readFromFile(pFileName);
        }
        catch (T3dException e) {
            throw new T3dException("Error reading file \"" + pFileName + "\"!");
        }
    }

    private void determineTiles(VgEnvelope pBBox, String pFileLoc) {
        GmPoint lPnt1 = new GmPoint(pBBox.getXMin(), pBBox.getYMin(), 0.);
        GmPoint lPnt2 = new GmPoint(pBBox.getXMin(), pBBox.getYMax(), 0.);
        GmPoint lPnt3 = new GmPoint(pBBox.getXMax(), pBBox.getYMax(), 0.);
        GmPoint lPnt4 = new GmPoint(pBBox.getXMax(), pBBox.getYMin(), 0.);
        lPnt1.setSRS(pBBox.getSRS());
        lPnt2.setSRS(pBBox.getSRS());
        lPnt3.setSRS(pBBox.getSRS());
        lPnt4.setSRS(pBBox.getSRS());

        int[] blatt1 = this.getTileNumber(lPnt1, pFileLoc);
        int[] blatt2 = this.getTileNumber(lPnt2, pFileLoc);
        int[] blatt3 = this.getTileNumber(lPnt3, pFileLoc);
        int[] blatt4 = this.getTileNumber(lPnt4, pFileLoc);
        if (blatt1 == null) blatt1 = this.firstNonNull(blatt2, blatt3, blatt4);
        if (blatt2 == null) blatt2 = this.firstNonNull(blatt1, blatt3, blatt4);
        if (blatt3 == null) blatt3 = this.firstNonNull(blatt1, blatt2, blatt4);
        if (blatt4 == null) blatt4 = this.firstNonNull(blatt1, blatt2, blatt3);

        if (blatt1 == null) { // d. h. alle null
            mHiFrom = -1;
            return;
        }

        mHiFrom = this.min(blatt1[0], blatt2[0], blatt3[0], blatt4[0]);
        mHiTo = this.max(blatt1[0], blatt2[0], blatt3[0], blatt4[0]);
        mLoFrom = this.min(blatt1[1], blatt2[1], blatt3[1], blatt4[1]);
        mLoTo = this.max(blatt1[1], blatt2[1], blatt3[1], blatt4[1]);
        System.out.println("mHi: " + mHiFrom + "..." + mHiTo);
        System.out.println("mLo: " + mLoFrom + "..." + mLoTo);
        
        if (mHiFrom < 0 && mHiTo >= 0) mHiFrom = 0;
        if (mLoFrom < 0 && mLoTo >= 0) mLoFrom = 0;
        if (mHiTo > 99 && mHiFrom < 100) mHiTo = 99;
        if (mLoTo > 99 && mLoFrom < 100) mLoTo = 99;
    }

    private int[] firstNonNull(int[] pBlattA, int[] pBlattB, int[] pBlattC) {
        if (pBlattA != null) return pBlattA;
        if (pBlattB != null) return pBlattB;
        if (pBlattC != null) return pBlattC;
        return null;
    }

    private int[] getTileNumber(VgPoint pt, String pTileLocator) // Ermittlung der Blattnummer-Indizes als zweielementiges Feld
    {
        TileLocator loc = new TileLocator("test");
        String tileNo = loc.tileNumber(pTileLocator, pt);
        System.out.println("Tile number: " + tileNo);
        if (tileNo.indexOf("-") >= 0) {
            tileNo = "6000"; 
            System.out.println("Warning: Illegal tile number! -> " + tileNo);
        }
        if (tileNo.length() > 4) {
            tileNo = "99" + tileNo.substring(tileNo.length() - 2);
            System.out.println("Warning: Illegal tile number! -> " + tileNo);
        }

        int[] ret = new int[2];
        ret[0] = Integer.parseInt(tileNo.substring(0,2));
        ret[1] = Integer.parseInt(tileNo.substring(2,4));
        return ret;
    }

    private int min(int a, int b, int c, int d) {
        int res = a;
        if (b < res) res = b;
        if (c < res) res = c;
        if (d < res) res = d;
        return res;
    }

    private int max(int a, int b, int c, int d) {
        int res = a;
        if (b > res) res = b;
        if (c > res) res = c;
        if (d > res) res = d;
        return res;
    }

    private GmSimple2dGridGeometry constructDestinationGrid(VgEnvelope pBBox, double pCellSize)
    {
        int nx = ((int) Math.floor(pBBox.getExtentX() / pCellSize)) + 1;
        int ny = ((int) Math.floor(pBBox.getExtentY() / pCellSize)) + 1;
        System.out.println("Lattice of size " + nx + " x " + ny + " will be built...");

        return new GmSimple2dGridGeometry(
            nx, ny,
            new GmPoint(pBBox.getXMin(), pBBox.getYMin(), 0.), // untere linke Ecke
            pCellSize, pCellSize); // Gitterweiten in x- und y-Richtung
    }

    private String constructDEMFilename(int i, int j, String pSrcGrdPath, String pSRS, String pTileLocatorName)
    {
        String subdir = pSRS.toLowerCase();
        subdir = subdir.replaceAll(":", "_");
        String filename = pSrcGrdPath + "/" + pTileLocatorName + "/" + subdir + "/dgm";
        if (i < 10) filename += "0";
        filename += "" + i;
        if (j < 10) filename += "0";
        filename += "" + j + ".asc";
        return filename;
    }

    public GmSimpleElevationGrid gridding(ArrayList pPointList, GmSimple2dGridGeometry pGrdGeom, double pSearchRadius)
    {
        GmSimpleElevationGrid lResGrid = null;
        try {
            int N = pPointList.size();
            if (N > 0) {
                FltPointSet2ElevationGrid lGridder =
                    new FltPointSet2ElevationGrid(pGrdGeom, FltPointSet2ElevationGrid.cInverseDist, pSearchRadius);
                System.out.println("Required heap memory for destination grid: " +
                    (lGridder.estimateMemoryConsumption() / 1000) + " KBytes");
                System.out.println("Starting gridding for " + pPointList.size() + " input points...");

                lResGrid = lGridder.transform(pPointList);
                if (! lResGrid.isSet())
                    System.out.println("Did not assign values to all grid cells!");
            }
        }
        catch (T3dException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return lResGrid;
    }

    private void purgeNoDataValues(GmSimpleElevationGrid lGrid) {
        for (int i = 0; i < lGrid.numberOfRows(); i++) {
            for (int j = 0; j < lGrid.numberOfColumns(); j++) {
                if (! lGrid.isSet(i, j))
                    lGrid.setValue(i, j, 0.);
            }
        }
    }
}
