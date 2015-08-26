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
package org.n52.v3d.triturus.vispovray;

import org.n52.v3d.triturus.vscene.*;
import org.n52.v3d.triturus.gisimplm.*;
import org.n52.v3d.triturus.t3dutil.T3dVector;
import org.n52.v3d.triturus.t3dutil.operatingsystem.CmdShellProcess;
import org.n52.v3d.triturus.t3dutil.operatingsystem.FileTools;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.io.*;
import java.lang.Process;
import java.lang.Runtime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Specification of a POV-Ray scene description.<br />
 * Note: We are sorry, this class is documented in German language only :-(<br /><br />
 * <i>German:</i> Spezifikation f&uuml;r eine POV-Ray-Szenenbeschreibung. Unterst&uuml;tzt wird das Rendering f&uuml;r
 * die POV-Ray-Engine in denVersionen 3.5 und 3.6.<br />
 * Bem.: Bislang wurden nur Windows und Linux-Implementierungen der POV-Ray-Engine getestet (siehe dazu
 * Installationshandbuch des 52N terrainServers).
 * @author Benno Schmidt
 */
public class PovrayScene extends VsSimpleScene
{
    private boolean mLocalDebug = false; // kann f�r Debug-Zwecke gesetzt werden

	private String mPovrayPath;
    private String mPovrayExec = "bin/pvengine.exe" /* Windows-Vorgabe */;
    private boolean mPovrayWin = true /* Windows-Plattform als Voreinstellung */;
	private ArrayList mSceneDescription = null;
	private String mWrkDir = ".";
    private String mTmpName = "tmp";
	private String mShell = "cmd.exe";
    private String mShellParams = "";
    private boolean mShellCommandQuot = true;
    private boolean mSmoothRelief = true;
    private String mImageFormat = "";
    private int mImageWidth = 640;
    private int mImageHeight = 480;
    private boolean mDisplay = true;
    private short mQuality = 9;
    private double mLightIntensity = 2.0;
    private String mGifEncodedDEM = "";
    private long mRendererTimeout = 20000;
    private boolean mImmediateTermination = false;

	/**
	 * generiert die zu den gesetzten Werten geh&ouml;rige Szenen-Beschreibung f&uuml;r POV-Ray. Das Resultat ist eine
	 * Szenenbeschreibung in der "POV-Ray Scene Language".
	 * @return Szenen-Beschreibung als <tt>ArrayList</tt> mit Strings
	 */
	public Object generateScene()
	{
		this.generatePovSceneDescription();
		return mSceneDescription;
	}

	/**
	 * ruft POV-Ray auf und f�hrt das Rendering durch. Der Pfad der POV-Ray-Installation ist zuvor &uuml;ber die
	 * Methode <tt>setPovrayInstallationPath</tt> zu setzen.<br />
	 * Falls die POV-Ray-Rendering-Engine nicht ausf&uuml;hrbar ist oder die gesetzte Timeout-Zeit &uuml;berschritten
     * wird, wird eine <tt>T3dException</tt> geworfen.<br />
	 * Bem.: Die Methode <tt>this.generateScene()</tt> ist vor dem Rendering nicht explizit aufzurufen.
	 * @throws T3dException
     * @see PovrayScene#setRendererTimeout(long)
     */
	public void render()
	{
		this.writeElevationModelToGIFFile(mWrkDir + "/" + mTmpName + ".gif");
        mGifEncodedDEM = mWrkDir + "/" + mTmpName + ".gif";
        this.performRendering();
	}

    /**
     * ruft POV-Ray auf und f&uuml;hrt das Rendering durch. Im Gegensatz zur Methode PovrayScene#render wird das
     * GIF-kodierte H&ouml;henmodell nicht generiert, sondern aus der angegebenen Datei gelesen.<br />
     * Bem.: Siehe auch Hinweise zu PovrayScene#render.
     * @param pFilename Dateiname (mit Extension <tt>&quot;.gif&quot;</tt>)
     * @throws T3dException
     * @see PovrayScene#render()
     */
    public void renderCachedDEM(String pFilename)
    {
        mGifEncodedDEM = pFilename;
        this.performRendering();
    }

    private void performRendering()
    {
        this.writePovFile(mWrkDir + "/" + mTmpName + ".pov");

        String lBatFile = mWrkDir + "/" + mTmpName + ".bat";
        String batFileContent = "";
        if (mPovrayWin)
            this.writeBatFile(lBatFile);
        else
            batFileContent = this.writeBatFile(lBatFile);

        if (mLocalDebug)
            System.out.println("> Preparing POV-Ray to run in " + mWrkDir + "...");

        String cmd = mShell;
        if (mShellParams != null && mShellParams.length() > 0)
            cmd = cmd + " " + mShellParams;
        if (mShellCommandQuot)
            cmd = cmd + " \"" + lBatFile + "\"";
        else
            cmd = cmd + " " + lBatFile;

        boolean lDirectExec = false; // f�r UNIX-Plattform erstmal nur Setzung 'false' unterst�tzt

        try {
            if (lDirectExec) {
                // einfache L�sung: Prozess instanziieren und auf Beendigung warten.
                // Nachteil: Gefahr nicht-terminierenderer Prozesse, dadurch Renderer-Instanz "verbraucht"!
                Process p; // p ist global deklariert
                if (mLocalDebug)
                    System.out.println("> Try to execute renderer command: " + cmd);
                p = Runtime.getRuntime().exec(cmd);
                p.waitFor(); // warten, bis Prozess beendet!
            }
            else {
                // Alternativl�sung: Prozess als kontrollierten Thread intanzieren und auf Terminierung pr�fen;
                // Die Time-out-Zeit ergibt sich zu lMaxIntervalChecks * lChkInterval (z. B. 40 x 500 ms = 20 s).
                // Nachteil: Rechenzeit durchschnittl. um 0.5 * lChkInterval h�her.

                CmdShellProcess p = null;
                if (mPovrayWin)
                    p = new CmdShellProcess(cmd);
                else
                    p = new CmdShellProcess(batFileContent);
                p.setImmediateTermination(mImmediateTermination);
                if (p == null)
                    throw new T3dException("Could not instantiate CmdShellProcess object.");

                p.start();

                if (mLocalDebug)
                    System.out.println("> Continue control thread...");
                //Thread t = new Thread();
                //t.start();
                int lMaxIntervalChecks = 40;
                int lCheckInterval = (int) (mRendererTimeout / lMaxIntervalChecks);
                int ct = 0;
                while (ct < lMaxIntervalChecks) {
                    if (p.hasTerminated())
                        break;
                    Thread.sleep(lCheckInterval);
                    ct++;
                    if (mLocalDebug)
                        System.out.println("> lChkCounter = " + ct + ", terminated = " + p.hasTerminated());
                }
                if (! p.hasTerminated() && (ct >= lMaxIntervalChecks)) {
                    p.interrupt();
                    // Prozess ist m�glicherweise noch aktiv -> ggf. �ber Prozess-ID des Betriebssystems zu beenden!
                }
            }
        }
        catch (IOException e) {
            throw new T3dException("Could not execute POV-Ray rendering engine: " + e.getMessage());
        }
        catch (InterruptedException e) {
            throw new T3dException("Rendering process has been interrupted: " + e.getMessage());
        }
        catch (Throwable e) {
            throw new T3dException("An error occured: " + e.getMessage());
        }
    }

	/**
	 * setzt den Pfad, unter dem POV-Ray installiert ist.<br />
	 * Bsp.: <tt>this.setPovrayInstallationPath("C:/Programme/Povray");</tt>
	 * @param pPath vollst&auml;ndige Pfadangabe (ohne Name des Executables)
	 */
	public void setPovrayInstallationPath(String pPath) {
		mPovrayPath = pPath;
	}

    /**
     * legt die auszuf&uuml;hrende Datei (&quot;Executable&quot;) innerhalb des POV-Ray-Installationsverzeichnisses
     * fest.<br />
     * Bsp.: <tt>this.setPovrayExecutable("bin/pvengine.exe");</tt><br />
     * Voreinstellungsge&auml;&szlig; ist das in den Windows-Installationen verwendete Executable "bin/pvengine.exe"
     * gesetzt.
     * @param pExec relative Pfadangabe innerhalb des POV-Ray-Installationsverzeichnisses
     * @see org.n52.v3d.triturus.vispovray.PovrayScene#setPovrayInstallationPath(java.lang.String)
     */
    public void setPovrayExecutable(String pExec) {
        mPovrayExec = pExec;
    }

    /**
     * legt fest, ob POV-Ray unter Windows oder auf einem UNIX-System (z. B. LINUX) ausgef&uuml;hrt wird.<br />
     * Voreinstellungsgem&auml;&szlig; ist die Windows-Plattform gesetzt.
     * @param pVal <i>false</i> f&uuml;r UNIX-Plattform
     */
    public void setPovrayWin(boolean pVal) {
        mPovrayWin = pVal;
    }

	/**
	 * setzt den Pfad des Arbeitsverzeichnisses, aus dem heraus der Aufruf von POV-Ray erfolgen soll.<br />
	 * Bsp.: <tt>this.setWorkingDirectory(".");</tt>
	 * @param pPath Verzeichnisname
	 */
	public void setWorkingDirectory(String pPath) {
		mWrkDir = pPath;
	}

    /**
     * liefert den f&uuml;r das Arbeitsverzeichnisses gesetzten Pfad.
     * @return pPath Verzeichnisname
     * @see PovrayScene#setWorkingDirectory
     */
    public String getWorkingDirectory() {
        return mWrkDir;
    }

    /**
     * setzt den Namensrumpf der generierten Tempor�rdateien. Um das Bild rendern zu k&ouml;nnen, werden im Verzeichnis
     * <tt>this.getWorkingDirectory()</tt> stets die drei folgenden Tempor&auml;rdateien generiert: Tempor&auml;rdatei
     * f&uuml;r das H&ouml;henmodell (GIF), POV-Ray-Szenenbeschreibung (.pov-Datei), Batch-Datei f&uuml;r POV-Ray-Start
     * (.bat-Datei).<br />
     * Voreinstellungsagem&auml;&szlig; ist der Namensrumpf "tmp" gesetzt. Werden simultan im gleichen Verzeichnis
     * mehrere POV-Ray-Prozesse durchgef&uuml;hrt (z. B. bei Verwendung innerhalb eines Servlets), ist daf&uuml;r zu
     * sorgen, dass der gesetzte Namensrumpf eindeutig ist!
     * @param pName Namensrumpf
     */
    public void setTempName(String pName) {
        mTmpName = pName;
    }

    /**
     * liefert den f&uuml;r die Tempor&auml;rdateien gesetzten Namensrumpf.
     * @return Namensrumpf
     * @see PovrayScene#setTempName
     */
    public String getTempName() {
        return mTmpName;
    }

	/**
	 * setzt den Namen der ausf&uuml;hrbaren Shell.<br />
	 * Bsp.: <tt>this.setShellCommand("cmd.exe");</tt>
	 * @param pApp Name der Shell, z. B. "cmd.exe" oder "command.com"
	 */
	public void setShellCommand(String pApp) {
		mShell = pApp;
	}

    /**
     * setzt Aufrufparameter f&uuml;r die auszuf&uuml;hrbare Shell.<br />
     * Bsp.: <tt>this.setShellCommandParams("/E:1900 /C");</tt>
     * @param pParams Aufrufparameter
     */
    public void setShellCommandParams(String pParams) {
        mShellParams = pParams;
    }

    /**
     * legt fest, ob die Angabe der auszuf&uuml;hrenden POV-Ray-Batch-Datei in Anf&uuml;hrungszeichen gesetzt wird
     * (unter Windows sinnvoll). Voreinstellungsgem&auml;&szlig; ist der Wert <i>true</i> gesetzt (z. B. unter
     * UNIX-Systemen nicht verwendbar!).
     * @param pVal <i>true</i>, falls &quot;doppeltes&quot; Anf&uuml;hrungszeichen verwendet werden soll
     */
    public void setShellCommandQuot(boolean pVal) {
        mShellCommandQuot = pVal;
    }

	// Einige private Helfer:

    private void writeElevationModelToGIFFile(String pFilename)
    {
		try {
            IoElevationGridGIFWriter lWriter = new IoElevationGridGIFWriter("GIFPalOrder");
            lWriter.writeToFile((GmSimpleElevationGrid) this.getTerrain(), pFilename);
		}
		catch (T3dException e) {
			e.printStackTrace();
		}
    }

    private void generatePovSceneDescription()
    {
        boolean orthographicView = false;
        String projType = this.getCurrentCamera().getProjectionType();
        if (projType.equalsIgnoreCase(VsCamera.OrthographicView))
            orthographicView = true;

        T3dVector lookFrom = this.norm(this.getCurrentViewpoint().getLookFrom());
        T3dVector lookAt = this.norm(this.getCurrentViewpoint().getLookAt());
        T3dVector lookUp = this.getCurrentViewpoint().getLookUp();
        double fovy = this.getCurrentCamera().getFovy();

    	mSceneDescription = new ArrayList(); // ggf. anzupassen!

    	mSceneDescription.add("// Persistence Of Vision Ray Tracer Scene Description File");
    	mSceneDescription.add("// This file was generated automatically by the M�nsterian Triturus framework!");
    	mSceneDescription.add("#version 3.5;");
    	mSceneDescription.add("global_settings {");
    	mSceneDescription.add("  assumed_gamma 1");
    	mSceneDescription.add("  max_trace_level 25");
    	mSceneDescription.add("}");
    	mSceneDescription.add("camera {");

        if (orthographicView) {
            mSceneDescription.add("  orthographic");
            fovy = 0.;
        }
        else {
            if (projType.equalsIgnoreCase(VsCamera.PerspectiveView))
                mSceneDescription.add("  perspective");
            else
                throw new T3dNotYetImplException("Unknown camera projection \"" + projType + "\"");
        }

        double cx = 0.5 * (lookFrom.getX() + 1.);
        double cy = 0.5 * (lookFrom.getY() + 1.);
        double cz = lookFrom.getZ() * 0.5 * this.getDefaultExaggeration();
        mSceneDescription.add("  location <" + cx + ", " + cz + ", " + cy + ">");
        mSceneDescription.add("  sky <" +
            lookUp.getX() + ", " +
            lookUp.getZ() + ", " +
            lookUp.getY() + ">");
        mSceneDescription.add("  right <" + ((double) mImageWidth)/((double) mImageHeight) + ", 0.0 ,0.0>");
        if (orthographicView)
            mSceneDescription.add("  // Note: Omitting angle specification.");
        else
            mSceneDescription.add("  angle " + fovy);
        mSceneDescription.add("  look_at <" +
            0.5 * (lookAt.getX() + 1.) + ", " +
            lookAt.getZ() * 0.5 * this.getDefaultExaggeration() + ", " +
            0.5 * (lookAt.getY() + 1.) + ">");

    	mSceneDescription.add("}");
    	mSceneDescription.add("light_source {");
    	mSceneDescription.add("  <" + cx + ", " + cz + ", " + cy + ">"); // Punktlicht in Kameraposition
    	mSceneDescription.add("  color rgb <1.0, 1.0, 1.0>*" + mLightIntensity); // helles wei�es Licht
    	mSceneDescription.add("}");
        mSceneDescription.add("background { color rgb <"
            + this.getBackgroundColor().getRed() + ","
            + this.getBackgroundColor().getGreen() + ","
            + this.getBackgroundColor().getBlue() + "> }");

        if (this.getDrape() !=  null && this.getDrape().length() > 0) {
            mSceneDescription.add("#declare T_Terrain =");
            mSceneDescription.add("  texture {");
            mSceneDescription.add("    pigment {");
    	    mSceneDescription.add("      image_map {");
            String drpFileExt = FileTools.getExtension(this.getDrape());
            mSceneDescription.add("      "
                + BitmapTypeHelper.getFileExtension(drpFileExt) + " \"" + this.getDrape() + "\""
                + " map_type 0 interpolate 2 once transmit all 0.0 }");
    	    mSceneDescription.add("    }");
            mSceneDescription.add("    rotate x*90");
            mSceneDescription.add("  }");
        }
        else
            mSceneDescription.add("  // Note: No drape texture specified.");

    	mSceneDescription.add("#declare Terrain_Obj =");
    	mSceneDescription.add("  height_field {");
    	mSceneDescription.add("    gif \"" + mGifEncodedDEM + "\"");

        if (this.getReliefSmoothingMode())
            mSceneDescription.add("    smooth");
        else
            mSceneDescription.add("    //smooth");
        if (this.getDrape() !=  null && this.getDrape().length() > 0)
    	    mSceneDescription.add("    texture { T_Terrain }");
        else
            mSceneDescription.add("    pigment { color rgb <0.2,0.7,0.2> }");
            // TODO: Farbe evtl. von au�en konfigurierbar machen

        // height_field wird f�r x, y und z auf Bereich 0..1 abgebildet; daher ist inverse Transf. nachzuschalten:
        double sx = 1., sy = 1.;
        double tx = 0., ty = 0.;
        if (this.getAspect() > 1.) {
            sx = 1./this.getAspect();
            tx = 0.5 * (1. - sx);
        }
        else {
            sy = this.getAspect();
            ty = 0.5 * (1. - sy);
        }
        double sz = 256./220. * (this.normZMax() - this.normZMin()) * 0.5 * this.getDefaultExaggeration();
        // todo: 256./220.-Problem dokumentieren (durchg�ngig!) -> bs
        double tz = this.normZMin() * 0.5 * this.getDefaultExaggeration();
    	mSceneDescription.add("    scale <" + sx + ", " + sz + ", " + sy + ">");
        mSceneDescription.add("    translate <" + tx + ", " + tz + ", " + ty + ">");

    	mSceneDescription.add("  }");
    	mSceneDescription.add("object {");
    	mSceneDescription.add("  Terrain_Obj");
    	mSceneDescription.add("}");

    	if (this.drawBBox()) {
     		double zMin = this.normZMin() * 0.5 * this.getDefaultExaggeration();
  		   	double zMax = this.normZMax() * 0.5 * this.getDefaultExaggeration();
    		double _x, x_, _y, y_, _z, z_;
    		double delta = 0.002;
    		for (int x = 0; x <= 1; x++) {
	    		for (int y = 0; y <= 1; y++) {
	    			_x = ((double) x) - delta;
	    			x_ = ((double) x) + delta;
	    			_y = ((double) y) - delta;
	    			y_ = ((double) y) + delta;
	    			mSceneDescription.add("box {");
	    			mSceneDescription.add("  <" + _x + "," + zMin + "," + _y + "><" + x_ + "," + zMax + "," + y_ + ">");
			    	mSceneDescription.add("  texture { pigment { color rgb <1,0,0> } }");
			    	mSceneDescription.add("}");
    			}
    		}
    		for (int x = 0; x <= 1; x++) {
	    		for (int z = 0; z <= 1; z++) {
	    			_x = ((double) x) - delta;
	    			x_ = ((double) x) + delta;
	    			if (z == 0) {
	    				_z = zMin - delta;
		    			z_ = zMin + delta;
		    		}
		    		else {
	    				_z = zMax - delta;
		    			z_ = zMax + delta;
		    		}
	    			mSceneDescription.add("box {");
     				mSceneDescription.add("  <" + _x + "," + _z + "," + 0 + "><" + x_ + "," + z_ + "," + 1 + ">");
			    	mSceneDescription.add("  texture { pigment { color rgb <0,1,0> } }");
			    	mSceneDescription.add("}");
    			}
    		}
    		for (int y = 0; y <= 1; y++) {
	    		for (int z = 0; z <= 1; z++) {
	    			_y = ((double) y) - delta;
	    			y_ = ((double) y) + delta;
	    			if (z == 0) {
	    				_z = zMin - delta;
		    			z_ = zMin + delta;
		    		}
		    		else {
	    				_z = zMax - delta;
		    			z_ = zMax + delta;
		    		}
	    			mSceneDescription.add("box {");
    				mSceneDescription.add("  <0," + _z + "," + _y + "><1," + z_ + "," + y_ + ">");
			    	mSceneDescription.add("  texture { pigment { color rgb <0,0,1> } }");
			    	mSceneDescription.add("}");
    			}
    		}
    	}
        else
            mSceneDescription.add("// Note: No directive to draw bounding-box.");

        if (this.drawTerrainPedestal()) {
            mSceneDescription.add("mesh2 {");
            mSceneDescription.add("  vertex_vectors {");
            mSceneDescription.add("    " + this.getNumberOfVerticesPedestal() + ",");
            mSceneDescription.addAll(this.getVertexListPedestal());
            mSceneDescription.add("  }");
            mSceneDescription.add("  face_indices {");
            mSceneDescription.add("    " + this.getNumberOfFacesPedestal() + ",");
            mSceneDescription.addAll(this.getFaceListPedestal());
            mSceneDescription.add("  }");
            mSceneDescription.add("  pigment { color"
                + " red " + this.getPedestalColor().getRed()
                + " green " + this.getPedestalColor().getGreen()
                + " blue " +  this.getPedestalColor().getBlue() + " }");
            mSceneDescription.add("  finish { ambient 0.2 diffuse 0.8 }");
            mSceneDescription.add("}");
        }
	}

    private void writePovFile(String pFilename)
    {
    	this.generatePovSceneDescription();

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));
			for (int i = 0; i < mSceneDescription.size(); i++) {
            	lDat.write((String) mSceneDescription.get(i));
                lDat.newLine();
            }
            lDat.close();
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
    }

    private String writeBatFile(String pFilename)
    {
        String cmd = ""; // Inhalt der Batch-Datei (entspr. R�ckgabe-Ergebnis)

        String executable = mPovrayPath + mPovrayExec;

        String imageFormatSwitch = "";
        String ext = "";
        if (mImageFormat.equalsIgnoreCase("RLE-TGA")) { imageFormatSwitch = "+FC"; ext = "rle"; }
        if (mImageFormat.equalsIgnoreCase("PNG")) { imageFormatSwitch = "+FN"; ext = "png"; }
        if (mImageFormat.equalsIgnoreCase("PPM")) { imageFormatSwitch = "+FP"; ext = "ppm"; }
        if (mImageFormat.equalsIgnoreCase("SYS")) { imageFormatSwitch = "+FS"; ext = "bmp"; }
        if (mImageFormat.equalsIgnoreCase("TGA")) { imageFormatSwitch = "+FT"; ext = "tga"; }

        try {
            BufferedWriter lDat = new BufferedWriter(new FileWriter(pFilename));

            String lDisplayStr = "+D";
            if (!mDisplay) lDisplayStr = "-D";

            cmd = executable
                + " Input_File_Name=\"" + mWrkDir + "/" + mTmpName + ".pov\""
                + " +Q" + mQuality
                + " -V"
                + " " + lDisplayStr;

            if (mPovrayWin)
                cmd = cmd + " Output_File_Name=\"" + mWrkDir + "\\" + mTmpName + "." + ext + "\" /exit";
            else
                cmd = cmd + " Output_File_Name=\"" + mWrkDir + "/" + mTmpName + "." + ext + "\"";

            cmd = cmd + " +W" + mImageWidth + " +H" + mImageHeight + " " + imageFormatSwitch;

            if (mLocalDebug)
                System.out.println("> Writing batch command: " + cmd);
            lDat.write(cmd);
            lDat.close();
        }
        catch (FileNotFoundException e) {
            throw new T3dException("Could not access file \"" + pFilename + "\".");
        }
        catch (IOException e) {
            throw new T3dException(e.getMessage());
        }

        return cmd;
        /*
        if (! mPovrayWin) {
            FilePermission fp = new FilePermission(pFilename, "read,write,execute");
            PermissionCollection pc = fp.newPermissionCollection();
        }
        */
    }

    /**
     * setzt den Modus f&uuml;r die gegl&auml;ttete Relief-Darstellung auf den angegebenen Wert.
     * Voreinstellungsgem&auml;&szlig; ist dieser Modus gesetzt.
     * @param pVal <i>true</i>, f&uuml;r eine gegl&auml;ttete Relief-Darstellung, sonst <i>false</i>.
     */
    public void setReliefSmoothingMode(boolean pVal) {
        mSmoothRelief = pVal;
    }

    /**
     * liefert den gesetzten Modus f&uuml;r die Gl&auml;ttung der Relief-Darstellung.
     * @return <i>true</i>, falls der Gl&auml;ttungsmodus gesetzt ist, sonst <i>false</i>.
     */
    public boolean getReliefSmoothingMode() {
        return mSmoothRelief;
    }

    /**
     * setzt das Bildformat f&uuml;r die POV-Ray-Rendering-Engine. Detaillierte Information zu den Formaten ist der
     * POV-Ray-Dokumentation zu entnehmen.<br />
     * Bem.: Die Einstellung "SYS" setzt das System-spezifische Format; unter Windows ist dies "BMP".
     * @param pImageFormat "RLE-TGA", "PNG", "PPM", "SYS", "TGA" oder Leerstring f&uuml;r POV-Ray-Default
     * @see PovrayScene#setImageFormat
     */
    public void setPovrayImageFormat(String pImageFormat) {
        mImageFormat = pImageFormat.toLowerCase();
    }

    /**
     * liefert das f&uuml;r die POV-Ray-Rendering-Engine eingestellte Bildformat.
     * @return K&uuml;rzel f&uuml;r das eingestellte Bildformat
     * @see PovrayScene#setPovrayImageFormat
     */
    public String getPovrayImageFormat() {
        return mImageFormat;
    }

    /**
     * setzt das Bildformat f&uuml;r die POV-Ray-Rendering-Engine entsprechend dem angegebenen MIME-Typ. Falls f&uuml;r
     * das angegebene Format keine Unterst�tzung vorhanden ist, wird eine <tt>T3dException</tt> geworfen.<br />
     * Bem.: Bez&uuml;glich der Unterst&uuml;tzung des BMP-Formats sind die Hinweise unter
     * PovrayScene#setPovrayImageFormat zu beachten!
     * @param pMimeType MIME-Typ, z. B. "image/png"
     * @throws T3dException
     * @see PovrayScene#setPovrayImageFormat
     */
    public void setImageFormat(String pMimeType) throws T3dException
    {
         if (pMimeType.equalsIgnoreCase("image/png")) {
            mImageFormat = "PNG";
            return;
        }
        if (pMimeType.equalsIgnoreCase("image/bmp")) {
            mImageFormat = "SYS";
            return;
        }
        if (pMimeType.equalsIgnoreCase("image/tga")) {
            mImageFormat = "TGA";
            return;
        }
        if (pMimeType.equalsIgnoreCase("image/ppm")) {
            mImageFormat = "PPM";
            return;
        }
        if (pMimeType.equalsIgnoreCase("image/rle")) {
            mImageFormat = "RLE-TGA";
            return;
        }
        if (pMimeType.equalsIgnoreCase("image/gif"))
            throw new T3dException("Sorry, GIF output is not supported...");
        if (pMimeType.equalsIgnoreCase("image/jpeg"))
            throw new T3dException("Sorry, JPEG output is not supported...");
        if (pMimeType.equalsIgnoreCase("image/jpg")) // eigentl. "image/jpeg"
            throw new T3dException("Sorry, JPEG output is not supported...");
        if (pMimeType.equalsIgnoreCase("image/tiff"))
            throw new T3dException("Sorry, TIFF output is not supported...");
        if (pMimeType.equalsIgnoreCase("image/tif")) // eigentl. "image/tiff"
            throw new T3dException("Sorry, TIFF output is not supported...");

        throw new T3dException("Sorry, " + pMimeType + " output is not supported...");
    }

    /**
     * liefert den MIME-Typ des konfigurierten Formats f&uuml;r die Bildausgabe.<br />
     * Bem.: Bez&uuml;glich der Unterst&uuml;tzung des BMP-Formats sind die Hinweise unter
     * PovrayScene#setPovrayImageFormat zu beachten!
     * @return MIME-Typ oder "", falls unbekannt
     */
    public String getImageFormat()
    {
        // Bem.: Dieser Methode nicht unter Verwendung von org.n52.v3d.terra3d.web.MimeTypeHelper implementieren, um keine
        // Abh�ngigkeiten zwischen Java-Paketen zu schaffen!

        if (mImageFormat.equalsIgnoreCase("PNG"))
            return "image/png";
        if (mImageFormat.equalsIgnoreCase("SYS"))
            return "image/bmp";
        if (mImageFormat.equalsIgnoreCase("TGA"))
            return "image/tga";
        if (mImageFormat.equalsIgnoreCase("PPM"))
            return "image/ppm";
        if (mImageFormat.equalsIgnoreCase("RLE-TGA"))
            return "image/rle";
        return "";
    }

    /**
     * setzt die Bildbreite f&uuml;r die POV-Ray-Rendering-Engine.<br />
     * Bem.: Voreingestellt ist der Wert 640.
     * @param pWidth Bildbreite in Pixeln
     */
    public void setImageWidth(int pWidth) {
        mImageWidth = pWidth;
    }

    /**
     * liefert die f&uuml;r die POV-Ray-Rendering-Engine eingestellte Bildbreite.
     * @return Bildbreite in Pixeln
     */
    public int getImageWidth() {
        return mImageWidth;
    }

    /**
     * setzt die Bildh&ouml;he f&uuml;r die POV-Ray-Rendering-Engine.<br />
     * Bem.: Voreingestellt ist der Wert 480.
     * @param pHeight Bildh&ouml;he in Pixeln
     */
    public void setImageHeight(int pHeight) {
        mImageHeight = pHeight;
    }

    /**
     * liefert die f&uuml;r die POV-Ray-Rendering-Engine eingestellte Bildh&ouml;he.<p>
     * @return Bildh&ouml;he in Pixeln
     */
    public int getImageHeight() {
        return mImageHeight;
    }

    /**
     * setzt die Bildh&ouml;he und -breite f&uuml;r die POV-Ray-Rendering-Engine.<br />
     * Bem.: Voreingestellt ist eine Bildgr&ouml;&szlig;e von 640x480 Pixeln.
     * @param pWidth Bildbreite in Pixeln
     * @param pHeight Bildh�he in Pixeln
     */
    public void setImageSize(int pWidth, int pHeight) {
        this.setImageHeight(pHeight);
        this.setImageWidth(pWidth);
    }

    /**
     * setzt den Quality-Parameter f&uuml;r die POV-Ray-Engine. Voreingestellt ist der Wert 9.
     * @param pQuality Wert zwischen 0 und 9
     */
    public void setQuality(short pQuality) {
        mQuality = pQuality;
    }

    /**
     * liefert den f&uuml;r die POV-Ray-Engine eingestellten Quality-Parameter.
     * @return Qualit&auml;tsangabe
     */
    public short getQuality() {
        return mQuality;
    }

    /**
     * setzt die Lichtintensit&auml;t f&uuml;r die POV-Ray-Szene. Voreingestellt ist der Wert 2.<br />
     * Bem.: In der vorliegenden Implementierung ist die Lichtquelle stets an der Kameraposition positioniert.
     * @param pLightIntensity Wert > 0
     */
    public void setLightIntensity(double pLightIntensity) {
        mLightIntensity = pLightIntensity;
    }

    /**
     * liefert die eingestellte Lichtintensit&auml;t.
     * @return Wert > 0
     */
    public double getLightIntensity() {
        return mLightIntensity;
    }

    /**
     * setzt den Display-Parameter f&uuml;r die POV-Ray-Engine. Voreingestellt ist der (f&uuml;r Debug-Zwecke sinnvolle)
     * Wert <i>true</i>.
     * @param pFlag <i>true</i>, um Rendering-Fenster anzuzeigen, sonst <i>false</i>
     */
    public void setDisplayVisible(boolean pFlag) {
        mDisplay = pFlag;
    }

    /**
     * liefert den f&uuml;r POV-Ray-Engine eingestellten Display-Parameter.
     * @return <i>true</i>, falls Rendering-Fenster angezeigt wird, sonst <i>false</i>
     */
    public boolean isDisplayVisible() {
        return mDisplay;
    }

    /**
     * liefert den Dateinamen des ben&ouml;tigten GIF-kodierten H&ouml;henmodells.
     * @return Dateiname (inkl. Pfad und Extension ".gif")
     */
    public String getGifEncodedDEM() {
        return mGifEncodedDEM;
    }

    // Private Helfer f�r Sockel-Generierung:

    // # Vertizes f�r mesh2-Objekt
    private int getNumberOfVerticesPedestal()
    {
        return 4 * (this.getTerrain().numberOfRows() + this.getTerrain().numberOfColumns() - 2);
    }

    // Liste der Vertizes f�r mesh2-Objekt
    private Collection getVertexListPedestal()
    {
        ArrayList list = new ArrayList();
        int nRows = this.getTerrain().numberOfRows();
        int nCols = this.getTerrain().numberOfColumns();
        double zRef = /*1.33 **/ this.getTerrain().minimalElevation() /*- 0.33 * this.getTerrain().maximalElevation()*/;
        // TODO: Was ist mit lattice/grid / vertex/cell-based?????
        for (int j = 0; j < nCols - 1; j++) {
            VgPoint pt1 = ((GmSimple2dGridGeometry) this.getTerrain().getGeometry()).getVertexCoordinate(0, j);
            VgPoint pt0 = new GmPoint(pt1);
            pt1.setZ(this.getTerrain().getValue(0, j));
            pt0.setZ(zRef);
            list.add(this.vertexHelperPedestal(pt1, pt0));
        }
        for (int i = 0; i < nRows - 1; i++) {
            VgPoint pt1 = ((GmSimple2dGridGeometry) this.getTerrain().getGeometry()).getVertexCoordinate(i, nCols - 1);
            VgPoint pt0 = new GmPoint(pt1);
            pt1.setZ(this.getTerrain().getValue(i, nCols - 1));
            pt0.setZ(zRef);
            list.add(this.vertexHelperPedestal(pt1, pt0));
        }
        for (int j = nCols - 1 ; j >= 1; j--) {
            VgPoint pt1 = ((GmSimple2dGridGeometry) this.getTerrain().getGeometry()).getVertexCoordinate(nRows - 1, j);
            VgPoint pt0 = new GmPoint(pt1);
            pt1.setZ(this.getTerrain().getValue(nRows - 1, j));
            pt0.setZ(zRef);
            list.add(this.vertexHelperPedestal(pt1, pt0));
        }
        for (int i = nRows - 1; i >= 1; i--) {
            VgPoint pt1 = ((GmSimple2dGridGeometry) this.getTerrain().getGeometry()).getVertexCoordinate(i, 0);
            VgPoint pt0 = new GmPoint(pt1);
            pt1.setZ(this.getTerrain().getValue(i, 0));
            pt0.setZ(zRef);
            list.add(this.vertexHelperPedestal(pt1, pt0));
        }
        return list;
    }

    // String f�r Vertex-Angaben der Punkte am Modellrand (variable H�he pt1.getZ() und Bezugsh�he pt0.getZ()):
    private String vertexHelperPedestal(VgPoint pt1, VgPoint pt0) {
        T3dVector v1 = this.norm(pt1);
        T3dVector w1 = new T3dVector(0.5 * (v1.getX() + 1.), v1.getZ() * 0.5 * this.getDefaultExaggeration(), 0.5 * (v1.getY() + 1.));
        T3dVector v0 = this.norm(pt0);
        T3dVector w0 = new T3dVector(0.5 * (v0.getX() + 1.), v0.getZ() * 0.5 * this.getDefaultExaggeration(), 0.5 * (v0.getY() + 1.));
        return "    <" + w1.getX() + "," + w1.getY() + "," + w1.getZ() + ">, <" + w0.getX() + "," + w0.getY() + "," + w0.getZ() + ">, ";
    }

    // # Faces f�r mesh2-Objekt
    private int getNumberOfFacesPedestal()
    {
        return 4 * (this.getTerrain().numberOfRows() + this.getTerrain().numberOfColumns() - 2);
    }

    // Liste der Faces f�r mesh2-Objekt
    private Collection getFaceListPedestal()
    {
        ArrayList list = new ArrayList();
        int nRows = this.getTerrain().numberOfRows();
        int nCols = this.getTerrain().numberOfColumns();
        // TODO: Was ist mit lattice/grid / vertex/cell-based?????
        for (int i = 0; i < 2 * (nRows + nCols - 2) - 1; i++)
            list.add("    <" + (2*i) + "," + (2*i + 1) + "," + (2*i + 3) + ">, <" + (2*i) + "," + (2*i + 3) + "," + (2*i + 2) + ">, ");
        int i = 2 * (nRows + nCols - 2) - 1;
        list.add("    <" + (2*i) + "," + (2*i + 1) + ",1>, <" + (2*i) + ",0,1>");
        // System.out.println("size faces = " + (2 * list.size()));
        return list;
    }

    /**
     * setzt die Timeout-Zeit f&uuml;r den Aufruf der POV-Ray-Rendering-Engine. Falls diese Zeit in den Methoden
     * <tt>this.render()</tt> oder <tt>this.renderCachedDEM()</tt> &uuml;berschritten wird, wird der Rendering-Vorgang
     * abgebrochen und liefert eine <tt>T3dException</tt>. Voreingestellt ist der Wert 20000 (entspr. 20 msec).
     * @param pVal Zeit in Millisekunden
     * @see T3dException
     */
    public void setRendererTimeout(long pVal) {
        mRendererTimeout = pVal;
    }

    /**
     * @deprecated
     * deaktiviert die <tt>Process#waitFor</tt>-Anweisung in der <tt>run()</tt>-Methode der f&uuml;r die Realisierung
     * genutzten Klasse <tt>CmdShellProcess</tt>, falls als Argument der Wert <i>true</i> angegeben wird. Dieser
     * Schalter ist nur in Ausnahmef&auml;llen zu setzen (z. B. f&uuml;r spezielle UNIX-Systeme). F&uuml;r die
     * Windows-Plattform sollte dieser Befehl <b>nicht</b> verwendet werden.
     * @param pFlag <i>true</i> zur waitFor()-Deaktivierung (voreinstellungsgem&auml;&szlig; ist <i>false</i> gesetzt)
     * @see org.n52.v3d.triturus.t3dutil.operatingsystem.CmdShellProcess
     */
    public void setImmediateTermination(boolean pFlag) {
        mImmediateTermination = pFlag;
    }

    /**
     * setzt des Modus f&uuml;r die Konsolen-Ausgabe von Kontrollausgaben.
     * @param pVal <i>true</i>, falls Ausgabe erfolgen soll, sonst <i>false</i> (Voreinstellung)
     */
    public void setLocalDebug(boolean pVal) {
        mLocalDebug = pVal;
    }
}