/**
 * Copyright (C) 2007-2016 52 North Initiative for Geospatial Open Source
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
 * if the distribution is compliant with both the GNU General Public License 
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * Contact: Benno Schmidt and Martin May, 52 North Initiative for Geospatial 
 * Open Source Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, 
 * Germany, info@52north.org
 */
package org.n52.v3d.triturus.survey;

import java.util.HashMap;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.core.T3dException;

/**
 * Determination of German TK-Blattnummer identifers.
 * <br /><br />
 * <i>German:</i> Klasse zur Ermittlung der Blattnummern der Topografischen 
 * Karten TK 25, TK 50, TK 100 der bundesdeutschen Landesvermessungs&auml;mter.
 * <br />
 * Bem.: Dem Blattschnitt der TK 25 liegt dabei eine Gradabteilungskarte mit 
 * Linien geografischer Koordinaten in Abst&auml;nden von 10 L&auml;ngenminuten 
 * (10' entsprechend 1/6 Grad) und 6 Breitenminuten (6' entsprechend 1/10 Grad)
 * zugrunde.
 * 
 * @author Benno Schmidt
 */
public class TKBlattLocator
{
	private HashMap<String, String> mBlattnamen;
	
	
	/**
     * Constructor.
     */
	public TKBlattLocator() {
		this.initBlattnamen();
	}
	 
	/**
     * returns the TK-identifier for a given point.
     * <br /><br />
	 * <i>German:</i> liefert die Nummer des TK-Blattes f&uuml;r den 
	 * angegebenen Punkt.
	 * 
	 * @param pTKBez TK-Werk, z. B. <tt>"TK 25"</tt>, <tt>"TK 50"</tt> oder <tt>"TK 100"</tt>
	 * @param pt Position gegeben in geografischen Koordinaten
	 * @return Blattnummer
	 * @throws org.n52.v3d.triturus.core.T3dException
	 * @throws org.n52.v3d.triturus.vgis.T3dSRSException
	 */
    // todo engl. javadoc der Parameter
    public String blattnummer(String pTKBez, VgPoint pt) throws T3dException, T3dSRSException
	{
  		if (! (pt.getSRS().equalsIgnoreCase("EPSG:4326")))
            throw new T3dSRSException( "TKBlattLocator can not process SRS \"" + pt.getSRS() + "\"." );

		if (pTKBez.equalsIgnoreCase("TK 25") || pTKBez.equalsIgnoreCase("TK25"))
			return this.tk25(pt);
		if (pTKBez.equalsIgnoreCase("TK 50") || pTKBez.equalsIgnoreCase("TK50"))
			return this.tk50(pt);
		if (pTKBez.equalsIgnoreCase("TK 100") || pTKBez.equalsIgnoreCase("TK100"))
			return this.tk100(pt);
		// else:
		throw new T3dException( "Illegal TK name: \"" + pTKBez + "\"." );
	}

    /**
     * @see TileLocator#tileNumber(String, org.n52.v3d.triturus.vgis.VgPoint)
     */
    public String tileNumber(String pTileId, VgPoint pt) throws T3dException, T3dSRSException {
        return this.blattnummer(pTileId, pt);
    }

    /**
     * formats the TK-Blattnummer.
     * <br /><br />
     * <i>German:</i> formatiert die TK-Blattnummer f&uuml;r die angegebene 
     * Nummern-Kombination.<br />
     * Beispiel: <tt>blattnummer(47,9)</tt> liefert &quot;4709&quot; als 
     * Resultat.
     * 
     * @param i ersten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @param j letzten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @return Blattnummer
     */
    // todo engl. javadoc der Parameter
    static public String blattnummer(int i, int j) {
        String ret = "";
        if (i < 10) ret += "0";
        ret += "" + i;
        if (j < 10) ret += "0";
        ret += "" + j;
        return ret;
    }

	private int mB12, mB34;
	
	private void setTk25(VgPoint pt) 
	{
		mB34 = (int)(6. * pt.getX() - 34.);
		mB12 = (int)(560. - 10. * pt.getY());
	}

	private String generateNumber() 
	{
		if (mB34 < 10)
			return "" + mB12 + "0" + mB34;
		else
			return "" + mB12 + mB34;
	}

	private String tk25(VgPoint pt) 
	{
		this.setTk25(pt);
		return this.generateNumber();
	}
	
	private String tk50(VgPoint pt) 
	{
		this.setTk25(pt);
		if (mB34 % 2 == 1) mB34 -= 1;
		if (mB12 % 2 == 0) mB12 += 1;
		return "L" + this.generateNumber();
	}

	private String tk100(VgPoint pt) 
	{
		this.setTk25(pt);
		if (mB34 % 4 != 2) mB34 -= ((mB34 + 2) % 4);
		if (mB12 % 4 != 3) mB12 += (3 - (mB12 % 4));
		return "C" + this.generateNumber();
	}
	
	private void initBlattnamen() 
	{
		mBlattnamen = new HashMap<String, String>();
        
		mBlattnamen.put("4210", "L�dinghausen");
		mBlattnamen.put("4211", "Ascheberg");
		mBlattnamen.put("4212", "Drensteinfurt");
		mBlattnamen.put("4310", "Datteln");
		mBlattnamen.put("4311", "L�nen");
		mBlattnamen.put("4312", "Hamm");
		mBlattnamen.put("4410", "Dortmund");
		mBlattnamen.put("4411", "Kamen");
		mBlattnamen.put("4412", "Unna");
		mBlattnamen.put("L4111", "M�nster");
		mBlattnamen.put("L4310", "L�nen");
		mBlattnamen.put("C4310", "M�nster");
		mBlattnamen.put("C4710", "Dortmund");
        // Liste kann bei Bedarf erweitert werden...
	}

	/**
     * returns the TK-Blatt name for a given TK-Blatt identifiert.
     * <br /><br />
	 * <i>German:</i> liefert den Namen des TK-Blatts mit der angegebenen Blattnummer.<br />
	 * Bem.: Diese Methode ist nur f&uuml;r Teilgebiete Nordrhein-Westfalens realisiert; siehe private Methode
     * TkBlattLocator#initBlattnamen im Quellcoorg.n52.v3d.
     * 
	 * @param pBlattnummer Blattnummer, z. B. aus <tt>this.blattnummer()</tt>.
	 * @return Blattname oder Leerstring, falls nicht vorliegend.
	 */
    // todo engl. javadoc der Parameter
	public String blattname(String pBlattnummer) 
	{
		String ret = (String)(mBlattnamen.get(pBlattnummer));
		if (ret == null) 
			return "";
		return ret;
	}
}
