package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * Klasse zur Verwaltung von Vektoren im dreidimensionalen euklidischen Raum.<p>
 * Bem.: Die Vektoren werden nicht als VGis-Geometrien behandelt. Die Metrik ist durch die Semantik der aufrufenden
 * Anwendung festzulegen.<p>
 * @author Benno Schmidt<br>
 * (c) 2003-2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dVector
{
    private double mX, mY, mZ;

    /** Konstruktor.<p> */
    public T3dVector(double pX, double pY, double pZ) {
        mX = pX; mY = pY; mZ = pZ;
    }

    /** Konstruktor für Nullvektor.<p> */
    public T3dVector() {
        mX = 0.; mY = 0.; mZ = 0.;
    }

    /** Konstruktor.<p> */
    public T3dVector(T3dVector pVec) {
    	this.assign(pVec);
    }

    /** Konstruktor.<p> */
    public T3dVector(VgPoint pPnt) {
    	this.assign(pPnt);
    }

	/** setzt den x-Wert des Vektors.<p> */
    public void setX(double pX) { 
        mX = pX; 
    }
    /** liefert den x-Wert des Vektors.<p> */
    public double getX() {
        return mX;
    }

	/** setzt den y-Wert des Vektors.<p> */
    public void setY(double pY) { 
        mY = pY; 
    }
    /** liefert den y-Wert der Vektors.<p> */
    public double getY() {
        return mY;
    }

    /** setzt den z-Wert des Vektors.<p> */
    public void setZ(double pZ) {
        mZ = pZ;
    }
    /** liefert den z-Wert des Vektors.<p> */
    public double getZ() {
        return mZ;
    }

    /** weist dem Objekt den Wert des Ortsvektors <tt>pPnt</tt> zu.<p> */
    public void assign(VgPoint pPnt) {
    	mX = pPnt.getX();
    	mY = pPnt.getY();
    	mZ = pPnt.getZ();
    }

    /** weist dem Objekt den Wert des Vektors <tt>pVec</tt> zu.<p> */
    public void assign(T3dVector pVec) {
    	mX = pVec.getX();
    	mY = pVec.getY();
    	mZ = pVec.getZ();
    }
    
    /** weist dem Objekt den Differenzvektor <tt>pPnt2 - pPnt1</tt> zu.<p> */
    public void assignDiff(VgPoint pPnt2, VgPoint pPnt1) {
    	mX = pPnt2.getX() - pPnt1.getX();
    	mY = pPnt2.getY() - pPnt1.getY();
    	mZ = pPnt2.getZ() - pPnt1.getZ();
    }

    /** weist dem Objekt den Differenzvektor <tt>pVec2 - pVec1</tt> zu.<p> */
    public void assignDiff(T3dVector pVec2, T3dVector pVec1) {
    	mX = pVec2.getX() - pVec1.getX();
    	mY = pVec2.getY() - pVec1.getY();
    	mZ = pVec2.getZ() - pVec1.getZ();
    }

    /** weist dem Objekt den Summenvektor <tt>pPnt1 + pPnt2</tt> zu.<p> */
    public void assignSum(VgPoint pPnt1, VgPoint pPnt2) {
    	mX = pPnt1.getX() + pPnt2.getX();
    	mY = pPnt1.getY() + pPnt2.getY();
    	mZ = pPnt1.getZ() + pPnt2.getZ();
    }

    /** weist dem Objekt den Summenvektor <tt>pVec1 + pVec2</tt> zu.<p> */
    public void assignSum(T3dVector pVec1, T3dVector pVec2) {
    	mX = pVec1.getX() + pVec2.getX();
    	mY = pVec1.getY() + pVec2.getY();
    	mZ = pVec1.getZ() + pVec2.getZ();
    }

	/** liefert das Skalarprodukt der Vektoren <tt>this</tt> und <tt>pVec</tt>.<p> */
	public double scalarProd(T3dVector pVec) {
   		return mX*pVec.getX() + mY*pVec.getY() + mZ*pVec.getZ();   
	}

    /** weist dem Objekt das Kreuzprodukt <tt>pVec1 x pVec2</tt> zu.<p> */
    public void assignCrossProd(T3dVector pVec1, T3dVector pVec2) {
    	mX = pVec1.getY() * pVec2.getZ() - pVec1.getZ() * pVec2.getY();
    	mY = pVec1.getZ() * pVec2.getX() - pVec1.getX() * pVec2.getZ();
    	mZ = pVec1.getX() * pVec2.getY() - pVec1.getY() * pVec2.getX();
	}
  
  	/** liefert den Betrag (Länge) des Vektors.<p> */
    public double length() {
       return Math.sqrt(mX*mX + mY*mY + mZ*mZ);  
    }
    
    /** 
     * normiert den Vektor.<p> 
     * Nach Aufruf der Methode sind die Komponenten des Objekts so geändert, dass der 
     * Vektor die Länge 1 besitzt; vgl. Methode <tt>this.norm()</tt>.<p>
     * Wird die Methode für den Nullvektor aufgerufen, bleibt der Methodenaufruf ohne
     * Wirkung.<p>
     * @see T3dVector#norm
     */
	public void doNorm() {
		double len = this.length();
		if (len == 0.)
			return;
		mX /= len; mY /= len; mZ /= len;
	}

	/**
	 * liefert einen zu dem Objekt gehörigen Vektor mit der Länge 1.<p>
     * Wird die Methode für den Nullvektor aufgerufen, wird eine T3dException geworfen.<p>
     * @see T3dVector#doNorm
     */
	public T3dVector norm() throws T3dException {
		double len = this.length();
		if (len == 0.)
			throw new T3dException("Tried to normalize vector with length 0.");
		return new T3dVector(mX/len, mY/len, mZ/len);
	}

	/** 
	 * weist dem Objekt einen zu <tt>pVec1</tt> und <tt>pVec2</tt> orthogonalen,
	 * normierten Vektor zu.<p> 
	 * Bem.: Der Ergebnisvektor ist eindeutig bestimmt (Rechte-Hand-Regel: <tt>pVec1</tt>
	 * = Daumen, <tt>pVec2</tt> = Zeigefinger, Ergebnisvektor = Daumen).<p>
     * Kann kein Vektor berechnet werden, wird eine T3dException geworfen.<p>
     * @throws T3dException
     */
    public void ortho(T3dVector pVec1, T3dVector pVec2) {
    	this.assignCrossProd(pVec1, pVec2);
    	this.doNorm();
	}

    /**
     * liefert einen innerhalb der xy-Ebene um den Ursprung gedrehten Vektor.<p>
     * @param pAzimuth Drehwinkel im Bogenmaß (im Uhrzeigersinn)
     * @return gedrehte Bounding-Box als Polygon
     */
    public T3dVector rotateXY(double pAzimuth) {
        return new T3dVector(
            Math.cos(pAzimuth) * this.getX() - Math.sin(pAzimuth) * this.getY(),
            Math.sin(pAzimuth) * this.getX() + Math.cos(pAzimuth) * this.getY(),
            this.getZ());
    }

    /**
     * liefert einen gegenüber der xy-Ebene um den Ursprung gedrehten Vektor.<p>
     * @param pInclination Drehwinkel im Bogenmaß
     * @return gedrehte Bounding-Box als Polygon
     */
    public T3dVector rotateZ(double pInclination) {
        throw new T3dNotYetImplException("Bounding-box rotation against z-axis");
    }

	/**
	 * berechnet den durch die Ortsvektoren <tt>pVec1</tt>, <tt>this</tt> und <tt>pVec2</tt> 
	 * festgelegten Winkel. Das aktuelle Objekt enthält den Scheitelpunkt des Winkels.<p>
	 * Bem.: <tt>pVec1</tt>, <tt>pVec2</tt> und <tt>this</tt> müssen voneinander verschieden 
	 * sein! Anderenfalls wird stets der Wert 0 zurückgegeben.<p>
	 * <b>TODO: Funktion ist nmoch nicht getestet.</b>
	 * @return Winkel im Bogenmaß (im Bereich 0 ... <tt>this.PI</tt>)
	 */
	public double angle(T3dVector pVec1, T3dVector pVec2)
	{   
   		T3dVector v01 = new T3dVector();
   		v01.assignDiff(pVec1, this);
   		T3dVector v02 = new T3dVector();
   		v01.assignDiff(pVec2, this);
   		T3dVector v12 = new T3dVector();
   		v01.assignDiff(pVec2, pVec1);
   		
   		double l01 = v01.length();
   		double l02 = v02.length();
   		double l12 = v12.length();
   		if (l01 == 0. || l02 == 0. || l02 == 0.)
      		return 0.;
   		
   		double cosPhi = (l01*l01 + l12*l12 - l02*l02) / (2. * l01 * l12);  
   		return Math.acos(cosPhi);
	}
	
	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")";
	}
}
