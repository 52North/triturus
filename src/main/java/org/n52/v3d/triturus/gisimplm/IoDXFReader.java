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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.t3dutil.T3dColor;
import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.VgGeomObject;

import java.io.*;
import java.util.ArrayList;

/**
 * @deprecated
 * <i>German:</i> Einlesen von Geometrien &uuml;ber DXF. Die CAD-Objekte werden in <tt>VgAttrFeature</tt>-Objekte
 * umgesetzt, in denen die Information &uuml;ber AutoCAD-Layer und -Farbe in thematischen Attributen abgelegt ist.<br />
 * Bem.: Weitere AutoCAD-Attribute wie Linientypen, Textstile, Extrudierungsh&ouml;hen etc. werden (noch!) nicht
 * ber&uuml;cksichtigt. Die <tt>IoDXFReader</tt>-Klasse ist nur rudiment&auml;r implementiert; es werden <b>nicht alle
 * AutoCAD-Entity-Typen unterst&uuml;tzt</b>, sondern nur ausgew&auml;hlte einfache Geometrien wie z. B. POLYLINE
 * (ausgenommen Netz-&auml;hnliche Strukturen) und 3DFACE. Bl&ouml;cke (INSERTs) werden in attributierte Punktgeometrien
 * transformiert. Die nachfolgende Tabelle gibt einen &Uuml;berblick, auf welche Art und Weise jeweils die konkrete
 * Umsetzung erfolgt. Diese Umsetzungsregeln sind beim DXF-Import aus CAD-Systemen zu beachten.<br />
 * <table border="1">
 * <tr><td><i>AutoCAD-Entity-Typ</i></td><td><i>Terra3d VGis-Geometrie</i></td><td><tt>VgAttrFeature</tt>-Attribute</d></tr>
 * <tr><td>f&uuml;r alle untengenannten Typen:</td><td><tt>(VgGeomObject)</tt></td><td>Layer, Farbe (falls ungleich VONLAYER), eindeutige Referenz (falls vorhanden), AutoCAD-Entity-Typ</td></tr>
 * <tr><td>POINT</td><td><tt>VgPoint</tt></td><td>-</td></tr>
 * <tr><td>LINE</td><td><tt>VgLineSegment</tt></td><td>-</td></tr>
 * <tr><td>POLYLINE</td><td><tt>VgLineString</tt> (offene Polylinien), <i>VgPolygon</i> (geschlossene Polylinien)</td><td>Polylinien-Typinformation (Gruppencode 70)</td></tr>
 * <tr><td>VERTEX und SEQEND</td><td>siehe POLYLINE</td><td>-</td></tr>
 * <tr><td>CIRCLE</td><td><tt>VgPoint</tt></td><td>Radius</td></tr>
 * <tr><td>INSERT</td><td><tt>VgPoint</tt></td><td>Blockname</td></tr>
 * <tr><td>3DFACE</td><td><tt>VgTriangle</tt></td><td>Information �ber unsichtbare Kanten</td></tr>
 * <tr><td>TEXT</td><td><tt>VgPoint</tt></td><td>Textinhalt, Texth�he in Zeichnungseinheiten</td></tr>
 * <tr><td>nicht aufgef&uuml;hrte Typen:</td><td><i>-</i></td><td>-</td></tr>
 * </table>
 * @author Benno Schmidt
 * @see IoDXFReader.DxfEntity
 * @see IoDXFReader.DxfColor
 */
public class IoDXFReader extends IoObject
{
    private String mLogString = "";

    public IoDXFReader() {
        mLogString = this.getClass().getName();
    }

    public String log() {
        return mLogString;
    }

    /**
     * reads DXF-entities fron a file and delivers (some) corresponding <tt>VgAttrFeature</tt>-objects.<br /><br />
     * <i>German:</i> liest DXF-Objekte aus einer Datei ein und liefert entsprechende <tt>VgAttrFeature</tt>-Objekte.
     * @param pFilename Pfad, unter dem die Datei abgelegt ist
     * @return Ergebnisliste mit angeh&auml;ngten <tt>VgAttrFeature</tt>-Objekten
     * @throws T3dException
     */
    public ArrayList readEntitiesFromFile(String pFilename) throws T3dException
    {
        final boolean lDebugMode = false;
    	
        String line = "";
        int lineNumber = 0;

        ArrayList lFeatList = new ArrayList();
        
        DxfEntity lEnt = new DxfEntity();
        lEnt.clear();
        
        VgGeomObject lGeom = null;

        try {
            FileReader lFileRead = new FileReader(pFilename);
            BufferedReader lDatRead = new BufferedReader(lFileRead);

            StringBuffer gcBuf = new StringBuffer("");
            int gc;
            StringBuffer rawToken = new StringBuffer("");
            StringBuffer purgedToken = new StringBuffer("");
            
            boolean eof = getGCAndVal(lDatRead, gcBuf, rawToken);
            boolean lFoundEntSect = false;
            
            while (!eof) 
            {
                lineNumber += 2;                
                gcBuf = purgeStringBuf(gcBuf);
                purgedToken = purgeStringBuf(rawToken);
                gc = Integer.parseInt(gcBuf.toString(), 10);
                if (lDebugMode) // Ausgabe des gelesenen GC/Wert-Paar 
                    System.out.println("[" + gc + ", \"" + rawToken.toString() + "\"]"); 
                
                if (!lFoundEntSect) {
                    if (!(gc == 2 && purgedToken.toString().equalsIgnoreCase("ENTITIES"))) {
                        eof = getGCAndVal(lDatRead, gcBuf, rawToken);
                        lineNumber += 2;                
                        continue; // bis zur ENTITIES-Sektion �berlesen
                    } else
                        lFoundEntSect = true;
                }
                    
                if (gc == 0)
                {
                    if (!lEnt.isEmpty())
                    {
                        if (purgedToken.toString().compareTo("VERTEX") == 0) {
                            lEnt.addVertex();
                        }
                        else {
                            if (purgedToken.toString().compareTo("SEQEND") == 0)
                                lEnt.completePolyline();
                    
                            if (lDebugMode) // Ausgabe der Werte der zuletzt gelesenen DXF-Entity
                                System.out.println(lEnt.toString());
                            GmAttrFeature lFeat = lEnt.generateGmAttrFeature();
                            if (lFeat != null)
                                lFeatList.add(lFeat);
                            lEnt.clear();
                        }
                    }
                }
                
                if (gc != 1)
                   lEnt.set(gc, purgedToken.toString());
                else
                   lEnt.set(1, rawToken.toString());
 
                eof = getGCAndVal(lDatRead, gcBuf, rawToken);
            }
            lDatRead.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }
        catch (T3dException e) {
            throw new T3dException(e.getMessage());
        }
        catch (Exception e) {
            throw new T3dException("Parser error in \"" + pFilename + "\":" + lineNumber);
        }
        
        return lFeatList;
    }

    private boolean getGCAndVal(BufferedReader pDatRead, StringBuffer pOutGC, StringBuffer pOutToken)
        throws T3dException
        // gibt Gruppencode und aus DXF-Datei gelesenen Wert (Token) zur�ck
    {
        String lStr;

        try {
            lStr = pDatRead.readLine();
            if (lStr == null) return true; // = EOF
            pOutGC.setLength(0);
            pOutGC.append(lStr);

            lStr = pDatRead.readLine();
            if (lStr == null) return true; // = EOF
            pOutToken.setLength(0);
            pOutToken.append(lStr);
        }
        catch (Exception e) {
            throw new T3dException(e.getMessage());
        }
        return false; // = !EOF
    }

    // Helfer zur Bereinigung um Leerzeichen:
    private StringBuffer purgeStringBuf(StringBuffer pStr)
    {
    	StringBuffer lRes = new StringBuffer();
    	lRes.append(pStr);
    	
        int i = 0;
        while (i < lRes.length()) {
            if (lRes.charAt(i) == 32 || lRes.charAt(i) == 0)
                lRes = lRes.deleteCharAt(i);
            else
                i++;
        }
        return lRes;
    }
    
    /** 
     * @deprecated
     * Test-Routine 
     * <pre>
     * java IoDXFReader.java &lt;DXF-Dateiname&gt;
     * </pre>
     */
    public static void main( String args[] )
    {
    	if (args.length != 1) {
            System.out.println("Usage: java IoDXFReader <input-file>");
            return;
        }
        
        String filename = args[0];

        IoDXFReader dxfr = new IoDXFReader();
        ArrayList lFeatList = null;
        lFeatList = dxfr.readEntitiesFromFile(filename);

        for (int i = 0; i < lFeatList.size(); i++)
            System.out.println(lFeatList.get(i)); // Objektinformation ausgeben
        System.out.println("Read " + lFeatList.size() + " objects.");
    }

    /** 
     * Inner class to hold DXF entities.
     */
    public class DxfEntity
    {
        final private int mCluArrLen = 2000;
        private int mClu[]; // Look-Up-Tabelle (GC -> interner Array-Index)
        private int mGCArrLen;
        private String mGC[]; // DXF-Werte-Array (unter Verwendung interner Array-Indizes)
        private boolean mIsEmpty = true;
        private boolean mPolylineIsComplete;

        private boolean mHasVertices = false;
        ArrayList mVertices = new ArrayList();
        
        public DxfEntity() {
            this.initCodeNoLookUp();
        }

        private void initCodeNoLookUp() // Objekt-Initialisierung
        {
            mClu = new int[mCluArrLen];
            for (int i = 0; i < mCluArrLen; i++)
                mClu[i] = -1; // = "ist nicht gesetzt"
            int j = 0;
            mClu[0] = j++; // Entity-Typ
            mClu[1] = j++; // Textinhalt
            mClu[2] = j++; // Blockname (INSERT)
            mClu[5] = j++; // Handle (eindeutige Referenz)
            mClu[8] = j++; // Layername
            mClu[10] = j++; // x-Koord. POINT/VERTEX, Startpunkt (LINE), erster Eckpunkt (3DFACE)
            mClu[11] = j++; // x-Koord. Linienendpunkt (LINE), zweiter Eckpunkt (3DFACE)
            mClu[12] = j++; // x-Koord. dritter Eckpunkt (3DFACE)
            mClu[13] = j++; // x-Koord. vierter Eckpunkt (3DFACE)
            mClu[20] = j++; // y-Koord. POINT/VERTEX, Startpunkt (LINE), erster Eckpunkt (3DFACE)
            mClu[21] = j++; // y-Koord. Linienendpunkt (LINE), zweiter Eckpunkt (3DFACE)
            mClu[22] = j++; // y-Koord. dritter Eckpunkt (3DFACE)
            mClu[23] = j++; // y-Koord. vierter Eckpunkt (3DFACE)
            mClu[30] = j++; // z-Koord. POINT/VERTEX, Startpunkt (LINE), erster Eckpunkt (3DFACE)
            mClu[31] = j++; // z-Koord. Linienendpunkt (LINE), zweiter Eckpunkt (3DFACE)
            mClu[32] = j++; // z-Koord. dritter Eckpunkt (3DFACE)
            mClu[33] = j++; // z-Koord. vierter Eckpunkt (3DFACE)
            mClu[40] = j++; // Radius
            mClu[62] = j++; // Farbnummer
            mClu[66] = j++; // Vertices-follow-Flag (POLYLINE)
            mClu[70] = j++; // Polylinen-Flag (POLYLINE; z. B. ist Bit 0 gesetzt f�r geschlossene Polylinen)
            mGCArrLen = j;
            mGC = new String[mGCArrLen];
            this.clear();
        }

        /** setzt Entity-Daten zur&uuml;ck. */
        public void clear() {
            for (int i = 0; i < mGCArrLen; i++)
                mGC[i] = "";
            mIsEmpty = true;
            
            mHasVertices = false;
            mVertices.clear();
            mPolylineIsComplete = false;
        }

        /** setzt Wert f&uuml;r angegebenen Gruppencode. */
        public void set(int pGC, String pVal) 
        {
            // System.out.println("set: " + pGC + ", " + pVal);
            if (pGC != 0 && !this.isSet(0))
                return;
                
            if (!mHasVertices) {            
                if (mClu[ pGC ] != -1) {
                    mGC[ mClu[ pGC ] ] = pVal;
                    mIsEmpty = false;
                }
            }
            else {
            	if (pGC == 10 || pGC == 20 || pGC == 30) {
                    double coord = Double.valueOf( pVal ).doubleValue();
                    switch (pGC) {
                	case 10: ((GmPoint) mVertices.get( mVertices.size() - 1 )).setX( coord ); break;
                	case 20: ((GmPoint) mVertices.get( mVertices.size() - 1 )).setY( coord ); break;
            	        case 30: ((GmPoint) mVertices.get( mVertices.size() - 1 )).setZ( coord ); break;
            	        default: /* nichts tun */ break; 
                    }
                }
            }
        }

        /** liefert Wert f&uuml;r angegebenen Gruppencoorg.n52.v3d. */
        public String get(int pGC) {
            return mGC[ mClu[ pGC ] ];
        }

        /** 
         * f&uuml;gt der Entity einen Vertex hinzu. Sobald der Entity ein Vertex hinzugef�gt worden ist, werden
         * nachfolgende <tt>this.set()</tt>-Aufrufe au&szlig;er f&uuml;r die Gruppencodes 10, 20 und 30
         * (Vertex-Koordinaten) bis zum n&auml;chsten <tt>this.clear()</tt> ignoriert.
         */
        public void addVertex() {
            mHasVertices = true;
            mPolylineIsComplete = false;
            mVertices.add(new GmPoint(0., 0., 0.));
        }

        /** schlie&szlig;t die Vertex-Eingabe f&uuml;r POLYLINE-Entitys ab. */
        public void completePolyline() {
            if (mHasVertices)
                mPolylineIsComplete = true;
        }

        /** liefert Information, ob f&uuml;r Entity bereits Daten gesetzt. */
        public boolean isEmpty() {
            return mIsEmpty;
        }

        /** liefert Information, ob Wert f&uuml;r Gruppencode bereits gesetzt. */
        public boolean isSet(int pGC) {
            if (mGC[ mClu[ pGC ] ].length() == 0)
                return false;
            return true;
        }

        public void registerLayer(String pLayer) {
            // todo: noch nicht implementiert 
        }

        public String[] getLayers() {
            // todo: noch nicht implementiert 
            return null;
        }
    
        /**
         * erzeugt ein <tt>GmAttrFeature</tt>, in dem die Information der DXF-Entity abgelegt ist.
         * @return <tt>GmAttrFeature-Objekt</tt> oder <i>null</i>
         */
        public GmAttrFeature generateGmAttrFeature() 
        {
            // System.out.println( "generateGmAttrFeature: " + this );
    	    GmAttrFeature lFeat = null;
            VgGeomObject lGeom = null;
            
            if (mIsEmpty || (mHasVertices && !mPolylineIsComplete)) 
                return null;	

            lFeat = new GmAttrFeature();
            if (this.isSet(0))
                lFeat.addAttribute("ENTITY_TYPE", "java.lang.String", this.get(0));
            if (this.isSet(5))
                lFeat.addAttribute("HANDLE", "java.lang.String", this.get(5).toLowerCase());
            if (this.isSet(8))
                lFeat.addAttribute("LAYER", "java.lang.String", this.get(8));
            if (this.isSet(62))
                lFeat.addAttribute("COLOR", "java.lang.Integer", Integer.valueOf( this.get(62) ));
            else
                lFeat.addAttribute("COLOR", "java.lang.Integer", new Integer(256));

            if (this.get(0).compareTo("POINT") == 0) 
            {
                double x, y, z = 0.;
                x = Double.valueOf( this.get(10) ).doubleValue();
                y = Double.valueOf( this.get(20) ).doubleValue();
                if (this.isSet(30))
                    z = Double.valueOf( this.get(30) ).doubleValue();
                lGeom = new GmPoint(x, y, z);

                lFeat.setGeometry(lGeom);

                lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPoint");
                
                return lFeat;
            }
            
    	    if (this.get(0).compareTo("LINE") == 0) 
            {
                double x1, y1, z1 = 0., x2, y2, z2 = 0.;
                x1 = Double.valueOf( this.get(10) ).doubleValue();
                y1 = Double.valueOf( this.get(20) ).doubleValue();
                if (this.isSet(30))
                    z1 = Double.valueOf( this.get(30) ).doubleValue();
                x2 = Double.valueOf( this.get(11) ).doubleValue();
                y2 = Double.valueOf( this.get(21) ).doubleValue();
                if (this.isSet(31))
                    z2 = Double.valueOf( this.get(31) ).doubleValue();
                lGeom = new GmLineSegment( new GmPoint(x1, y1, z1), new GmPoint(x2, y2, z2) );

                lFeat.setGeometry(lGeom);
                
                return lFeat;
            }

            if (this.get(0).compareTo("INSERT") == 0) 
            {
                double x, y, z = 0.;
                x = Double.valueOf( this.get(10) ).doubleValue();
                y = Double.valueOf( this.get(20) ).doubleValue();
                if (this.isSet(30))
                    z = Double.valueOf( this.get(30) ).doubleValue();
                lGeom = new GmPoint(x, y, z);

                lFeat.setGeometry(lGeom);

                if (this.isSet(2))
                    lFeat.addAttribute("BLOCKNAME", "java.lang.String", this.get( 2 ));
                lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPoint");
                
                return lFeat;
            }
            
            
            if (this.get(0).compareTo("POLYLINE") == 0) 
            {
            	boolean lEntIsPolygon = false;
                int lPlFlag = 0;
                if (this.isSet(70))
                    lPlFlag = Integer.parseInt( this.get(70), 10 );
                if (this.get(70).compareTo( "" ) != 0 && lPlFlag % 2 == 1)
                    lEntIsPolygon = true; // da 'Polylinie ist geschlosse'-Flag gesetzt (Gruppencode 70)
                if (lEntIsPolygon) 
                    lGeom = new GmLinearRing();
                else
                    lGeom = new GmLineString();
                for (int i = 0; i < mVertices.size(); i++) {
                    if (lEntIsPolygon)
                        ((GmLinearRing)lGeom).addVertex( (GmPoint) mVertices.get( i ) );
                    else
                        ((GmLineString) lGeom).addVertex( (GmPoint) mVertices.get( i ) );
                }

                // if the entity is a polygon, then create a GmPolygon from the 
                // previously created linearRing
                if (lEntIsPolygon) 
                    lGeom = new GmPolygon((GmLinearRing)lGeom);
                
                lFeat.setGeometry(lGeom);

                if (lPlFlag > 1)
                    lFeat.addAttribute("POLYLINE_MODE_FLAG", "java.lang.Integer", new Integer( lPlFlag ));
                if (lEntIsPolygon)
                    lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPolygon");
                else
                    lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgLineString");

                return lFeat;
            }

            if (this.get(0).compareTo("CIRCLE") == 0) 
            {
                double x, y, z = 0.;
                x = Double.valueOf( this.get(10) ).doubleValue();
                y = Double.valueOf( this.get(20) ).doubleValue();
                if (this.isSet(30))
                    z = Double.valueOf( this.get(30) ).doubleValue();
                lGeom = new GmPoint(x, y, z);

                lFeat.setGeometry(lGeom);

                if (this.isSet(40))
                    lFeat.addAttribute("RADIUS", "java.lang.Double", Double.valueOf( this.get( 40 ) ));
                lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPoint");

                return lFeat;
            }

            if (this.get(0).compareTo("TEXT") == 0) 
            {
                double x, y, z = 0.;
                x = Double.valueOf( this.get(10) ).doubleValue();
                y = Double.valueOf( this.get(20) ).doubleValue();
                if (this.isSet(30))
                    z = Double.valueOf( this.get(30) ).doubleValue();
                lGeom = new GmPoint(x, y, z);

                lFeat.setGeometry(lGeom);

                if (this.isSet(1))
                    lFeat.addAttribute("TEXT_VALUE", "java.lang.String", this.get( 1 ));
                if (this.isSet(40))
                    lFeat.addAttribute("TEXT_HEIGHT", "java.lang.Double", Double.valueOf( this.get( 40 ) ));
                lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPoint");
                
                return lFeat;
            }
             
            if (this.get(0).compareTo("3DFACE") == 0) 
            {
                double[] x = new double[4], y = new double[4], z = new double[4];
                z[0] = z[1] = z[2] = z[3] = 0.;

                x[0] = Double.valueOf( this.get( 10 ) ).doubleValue();
                y[0] = Double.valueOf( this.get( 20 ) ).doubleValue();
                if (this.isSet(30))
                    z[0] = Double.valueOf( this.get(30) ).doubleValue();
                x[1] = Double.valueOf( this.get(11) ).doubleValue();
                y[1] = Double.valueOf( this.get(21) ).doubleValue();
                if (this.isSet(31))
                    z[1] = Double.valueOf( this.get(31) ).doubleValue();
                x[2] = Double.valueOf( this.get(12) ).doubleValue();
                y[2] = Double.valueOf( this.get(22) ).doubleValue();
                if (this.isSet(32))
                    z[2] = Double.valueOf( this.get(32) ).doubleValue();
                x[3] = Double.valueOf( this.get(13) ).doubleValue();
                y[3] = Double.valueOf( this.get(23) ).doubleValue();
                if (this.isSet(33))
                    z[3] = Double.valueOf( this.get(33) ).doubleValue();

                double lEps = 0.0000015; // AutoCAD-spezifische Setzung
                if (Math.abs( x[2] - x[3] ) < lEps && Math.abs( y[2] - y[3] ) < lEps && Math.abs( z[2] - z[3] ) < lEps) {
                    lGeom = new GmTriangle( 
                        new GmPoint(x[0], y[0], z[0]), 
                        new GmPoint(x[1], y[1], z[1]),
                        new GmPoint(x[2], y[2], z[2]) );
                    lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgTriangle");
                }
                else {
                    lGeom = new GmLinearRing();
                    for (int i = 0; i < 4; i++)
                        ((GmLinearRing) lGeom).addVertex(new GmPoint( x[i], y[i], z[i] ));
                    
                    // create a GmPolygon from the previously created linearRing
                    lGeom = new GmPolygon((GmLinearRing)lGeom);
                    lFeat.addAttribute("VG_GEOMETRY", "java.lang.String", "org.n52.v3d.triturus.vgis.VgPolygon");
                }

                lFeat.setGeometry(lGeom);

                int lInvEdges = 0;
                if (this.isSet(70))
                    lInvEdges = Integer.valueOf( this.get(70) ).intValue();
                if (lInvEdges > 0)
                    lFeat.addAttribute("INVISIBLE_EDGES_FLAG", "java.lang.Integer", new Integer( lInvEdges ));

                return lFeat;
            }
            
            //else:
            return null;
        }

        public String toString() {
            String str = "";
            for (int i = 0; i < mCluArrLen; i++) {
                if (mClu[ i ] != -1 && mGC[ mClu[ i ] ] != "") {
                   if (i >= 10 && i <= 100)
                       str = str + "(" + i + ", " + mGC[ mClu[ i ] ] + ")";
                   else
                       str = str + "(" + i + ", \"" + mGC[ mClu[ i ] ] + "\")";
                }
            }
            if (mHasVertices)  {
            	str = str + " {";
            	for (int i = 0; i < mVertices.size(); i++)
            	    str = str + ((GmPoint) mVertices.get( i ) );
                str = str + "}";
            }
            return str;
        }
    }
    
    /** 
     * Inner class to handle AutoCAD standard colors.>
     */
    public class DxfColor
    {
    	private int mCol = 7;
    	
    	public DxfColor() {
    	}
    	
    	public DxfColor( int pAcadColorNumber ) {
            mCol = pAcadColorNumber;
    	}
    	
    	/**
    	 * setzt die f&uuml;r das aktuelle <tt>DxfColor</tt>-Objekt gesetzte Farbe in einen sprechenden Text um.
    	 * @return Farbbezeichnung
    	 */
    	public String colorAsString() 
    	{
    	    switch (mCol) {
    	    	case 0: return "BYBLOCK";
    	    	case 256: return "BYLAYER";
    	    	case 1: return "red";
    	    	case 2: return "yellow";
    	    	case 3: return "green";
    	    	case 4: return "cyan";
    	    	case 5: return "blue";
    	    	case 6: return "magenta";
    	    	case 7: return "black";
    	    	case 8: return "dark red";
    	    	case 9: return "dark yellow";
    	    	case 10: return "dark green";
    	    	case 11: return "dark cyan";
    	    	case 12: return "dark blue";
    	    	case 13: return "dark magenta";
    	    	default: return "" + mCol;
    	    }
    	}

    	/**
    	 * setzt die f&uuml;r das aktuelle <tt>DxfColor</tt>-Objekt gesetzte Farbe in eine <tt>VuColor</tt> um.
    	 * @return <tt>VuColor</tt>-Objekt
    	 */
    	public T3dColor toT3dColor()
    	{
    	    float lDarkFact = 0.8f;
    	    switch (mCol) 
    	    {
    	    	case 1: return new T3dColor(1.f, 0.f, 0.f);
    	    	case 2: return new T3dColor(1.f, 1.f, 0.f);
    	    	case 3: return new T3dColor(0.f, 1.f, 0.f);
    	    	case 4: return new T3dColor(0.f, 1.f, 1.f);
    	    	case 5: return new T3dColor(0.f, 0.f, 1.f);
    	    	case 6: return new T3dColor(1.f, 0.f, 1.f);
    	    	case 7: return new T3dColor(1.f, 1.f, 1.f);
    	    	case 8: return new T3dColor(lDarkFact, 0.f, 0.f);
    	    	case 9: return new T3dColor(lDarkFact, lDarkFact, 0.f);
    	    	case 10: return new T3dColor( 0.f, lDarkFact, 0.f);
    	    	case 11: return new T3dColor( 0.f, lDarkFact, lDarkFact);
    	    	case 12: return new T3dColor( 0.f, 0.f, lDarkFact);
    	    	case 13: return new T3dColor( lDarkFact, 0.f, lDarkFact);
    	    	default: 
    	    	    float gray = (float) mCol / 256;
    	    	    return new T3dColor(gray, gray, gray);
    	    }
    	}
    }
}
