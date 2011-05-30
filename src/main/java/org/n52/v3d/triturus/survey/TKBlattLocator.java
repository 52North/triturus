package org.n52.v3d.triturus.survey;

import java.util.HashMap;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.core.T3dException;

/** 
 * Klasse zur Ermittlung der Blattnummern der Topografischen Karten TK 25, TK 50, TK 100 der bundesdeutschen
 * Landesvermessungsämter.<p>
 * Bem.: Dem Blattschnitt der TK 25 liegt dabei eine Gradabteilungskarte mit Linien geografischer Koordinaten in
 * Abständen von 10 Längenminuten (10' entsprechend 1/6 Grad) und 6 Breitenminuten (6' entsprechend 1/10 Grad)
 * zugrunorg.n52.v3d.<p>
 * @author Benno Schmidt<br>
 * (c) 2003-2005, con terra GmbH<br>
 */
public class TKBlattLocator
{
	private HashMap mBlattnamen;
	
	/** Konstruktor. */
	public TKBlattLocator() {
		this.initBlattnamen();
	}
	 
	/**
	 * liefert die Nummer des TK-Blattes für den angegebenen Punkt.<p>
	 * @param pTKBez TK-Werk, z. B. <tt>"TK 25"</tt>, <tt>"TK 50"</tt> oder <tt>"TK 100"</tt>
	 * @param pt Position gegeben in geografischen Koordinaten
	 * @return Blattnummer
	 * @throws org.n52.v3d.triturus.core.T3dException
	 * @throws org.n52.v3d.triturus.vgis.T3dSRSException
	 */
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
     * formatiert die TK-Blattnummer für die angegebene Nummern-Kombination.<p>
     * Beispiel: <tt>blattnummer(47,9)</tt> liefert &quot;4709&quot; als Resultat.<p>
     * @param i ersten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @param j letzten beiden Ziffern der Blattnummer-Angabe als Ganzzahl
     * @return Blattnummer
     */
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
		mBlattnamen = new HashMap();
        
		mBlattnamen.put("4210", "Lüdinghausen");
		mBlattnamen.put("4211", "Ascheberg");
		mBlattnamen.put("4212", "Drensteinfurt");
		mBlattnamen.put("4310", "Datteln");
		mBlattnamen.put("4311", "Lünen");
		mBlattnamen.put("4312", "Hamm");
		mBlattnamen.put("4410", "Dortmund");
		mBlattnamen.put("4411", "Kamen");
		mBlattnamen.put("4412", "Unna");
		mBlattnamen.put("L4111", "Münster");
		mBlattnamen.put("L4310", "Lünen");
		mBlattnamen.put("C4310", "Münster");
		mBlattnamen.put("C4710", "Dortmund");
        // Liste kann bei Bedarf erweitert werden...
	}

	/** 
	 * liefert den Namen des TK-Blatts mit der angegebenen Blattnummer.<p>
	 * Bem.: Diese Methode ist nur für Teilgebiete Nordrhein-Westfalens realisiert; siehe private Methode
     * TkBlattLocator#initBlattnamen im Quellcoorg.n52.v3d.<p>
	 * @param pBlattnummer Blattnummer, z. B. aus <tt>this.blattnummer()</tt>.
	 * @return Blattname oder Leerstring, falls nicht vorliegend.
	 */
	public String blattname(String pBlattnummer) 
	{
		String ret = (String)(mBlattnamen.get(pBlattnummer));
		if (ret == null) 
			return "";
		return ret;
	}
}
