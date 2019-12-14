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
package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/**
 * Class to hold vectors in 3-D Euclidian space.<br /><br />
 * <i>German:</i> Klasse zur Verwaltung von Vektoren im dreidimensionalen euklidischen Raum.<br />
 * Bem.: Die Vektoren werden nicht als VGis-Geometrien behandelt. Die Metrik ist durch die Semantik der aufrufenden
 * Anwendung festzulegen.
 * @author Benno Schmidt
 */
public class T3dVector
{
    private double mX, mY, mZ;

    /**
     * Constructor
     */
    public T3dVector(double pX, double pY, double pZ) {
        mX = pX; mY = pY; mZ = pZ;
    }

    /**
     * Zero vector constructor.
     */
    public T3dVector() {
        mX = 0.; mY = 0.; mZ = 0.;
    }

    /**
     * Constructor
     */
    public T3dVector(T3dVector pVec) {
    	this.assign(pVec);
    }

    /**
     * Constructor
     */
    public T3dVector(VgPoint pPnt) {
    	this.assign(pPnt);
    }

    /**
     * sets the vector's x-component.
     */
    public void setX(double pX) { 
        mX = pX; 
    }

    /**
     * returns the vector's x-component.
     */
    public double getX() {
        return mX;
    }

    /**
     * sets the vector's y-component.
     */
    public void setY(double pY) { 
        mY = pY; 
    }

    /**
     * returns the vector's y-component.
     */
    public double getY() {
        return mY;
    }

    /**
     * sets the vector's z-component.
     */
    public void setZ(double pZ) {
        mZ = pZ;
    }

    /**
     * returns the vector's z-component.
     */
    public double getZ() {
        return mZ;
    }

    /**
     * assigns the values of the position vector <tt>pPnt</tt> to the current vector object.
     */
    public void assign(VgPoint pPnt) {
    	mX = pPnt.getX();
    	mY = pPnt.getY();
    	mZ = pPnt.getZ();
    }

    /**
     * assigns the values of the (direction) vector <tt>pVec</tt> to the current vector object.
     */
    public void assign(T3dVector pVec) {
    	mX = pVec.getX();
    	mY = pVec.getY();
    	mZ = pVec.getZ();
    }

    /**
     * assigns the values of the difference vector <tt>pPnt2 - pPnt1</tt> to the current vector object.
     */
    public void assignDiff(VgPoint pPnt2, VgPoint pPnt1) {
    	mX = pPnt2.getX() - pPnt1.getX();
    	mY = pPnt2.getY() - pPnt1.getY();
    	mZ = pPnt2.getZ() - pPnt1.getZ();
    }

    /**
     * assigns the values of the difference vector <tt>pVec2 - pVec1</tt> to the current vector object.
     */
    public void assignDiff(T3dVector pVec2, T3dVector pVec1) {
    	mX = pVec2.getX() - pVec1.getX();
    	mY = pVec2.getY() - pVec1.getY();
    	mZ = pVec2.getZ() - pVec1.getZ();
    }

    /**
     * assigns the values of the vector sum <tt>pPnt1 + pPnt2</tt> to the current vector object.
     */
    public void assignSum(VgPoint pPnt1, VgPoint pPnt2) {
    	mX = pPnt1.getX() + pPnt2.getX();
    	mY = pPnt1.getY() + pPnt2.getY();
    	mZ = pPnt1.getZ() + pPnt2.getZ();
    }

    /**
     * assigns the values of the vector sum <tt>pVec1 + pVec2</tt> to the current vector object.
     */
    public void assignSum(T3dVector pVec1, T3dVector pVec2) {
    	mX = pVec1.getX() + pVec2.getX();
    	mY = pVec1.getY() + pVec2.getY();
    	mZ = pVec1.getZ() + pVec2.getZ();
    }

    /**
     * returns the scalar product of the vectors <tt>this</tt> and <tt>pVec</tt>.
     */
	public double scalarProd(T3dVector pVec) {
   		return mX*pVec.getX() + mY*pVec.getY() + mZ*pVec.getZ();   
	}

    /**
     * assigns the cross product <tt>pVec1 x pVec2</tt> to the current vector object.
     */
    public void assignCrossProd(T3dVector pVec1, T3dVector pVec2) {
    	mX = pVec1.getY() * pVec2.getZ() - pVec1.getZ() * pVec2.getY();
    	mY = pVec1.getZ() * pVec2.getX() - pVec1.getX() * pVec2.getZ();
    	mZ = pVec1.getX() * pVec2.getY() - pVec1.getY() * pVec2.getX();
	}
  
    /**
     * returns the absolute value (length) of the vector.
     */
    public double length() {
       return Math.sqrt(mX*mX + mY*mY + mZ*mZ);  
    }
    
    /** 
     * normalizes the vector.<br /><br />
     * <i>German:</i> normiert den Vektor.<br />
     * Nach Aufruf der Methode sind die Komponenten des Objekts so ge&auml;ndert, dass der Vektor die L&auml;nge 1
     * besitzt; vgl. Methode <tt>this.norm()</tt>.<br />
     * Wird die Methode f&uuml;r den Nullvektor aufgerufen, bleibt der Methodenaufruf ohne Wirkung.
     * @see T3dVector#norm
     */
	public void doNorm() {
		double len = this.length();
		if (len == 0.)
			return;
		mX /= len; mY /= len; mZ /= len;
	}

	/**
	 * returns a vector with length 1 with same direction as <tt>this</tt>.<br /><<br />
	 * <i>German:</i> liefert einen zu dem Objekt geh&ouml;rigen Vektor mit der L&auml;nge 1.<br />
	 * Wird die Methode f&uuml;r den Nullvektor aufgerufen, wird eine T3dException geworfen.
	 * @see T3dVector#doNorm
	 */
	public T3dVector norm() throws T3dException {
		double len = this.length();
		if (len == 0.)
			throw new T3dException("Tried to normalize vector with length 0.");
		return new T3dVector(mX/len, mY/len, mZ/len);
	}

	/**
	 * assigns an orthogonal normalized vector to the current vector object.<br /><br />
	 * <i>German:</i> weist dem Objekt einen zu <tt>pVec1</tt> und <tt>pVec2</tt> orthogonalen, normierten Vektor zu.
	 * <br />
	 * Bem.: Der Ergebnisvektor ist eindeutig bestimmt (Rechte-Hand-Regel: <tt>pVec1</tt> = Daumen, <tt>pVec2</tt> =
	 * Zeigefinger, Ergebnisvektor = Daumen).<br />
	 * Kann kein Vektor berechnet werden, wird eine T3dException geworfen.
	 * @throws T3dException
	 */
    public void ortho(T3dVector pVec1, T3dVector pVec2) {
    	this.assignCrossProd(pVec1, pVec2);
    	this.doNorm();
	}

    /**
     * returns a rotated vector (rotation in xy-plane, origin as rotation center.<br /><br />
     * <i>German:</i> liefert einen innerhalb der xy-Ebene um den Ursprung gedrehten Vektor.
     * todo engl. JavaDoc für Parameter
     * @param pAzimuth Drehwinkel im Bogenma� (im Uhrzeigersinn)
     * @return gedrehte Bounding-Box als Polygon
     */
    public T3dVector rotateXY(double pAzimuth) {
        return new T3dVector(
            Math.cos(pAzimuth) * this.getX() - Math.sin(pAzimuth) * this.getY(),
            Math.sin(pAzimuth) * this.getX() + Math.cos(pAzimuth) * this.getY(),
            this.getZ());
    }

    /**
     * returns a rotated vector (rotation against xy-plane, origin as rotation center.<br /><br />
     * <i>German:</i> liefert einen gegen�ber der xy-Ebene um den Ursprung gedrehten Vektor.<p>
     * todo engl. JavaDoc für Parameter
     * @param pInclination Drehwinkel im Bogenma�
     * @return gedrehte Bounding-Box als Polygon
     */
    public T3dVector rotateZ(double pInclination) {
        throw new T3dNotYetImplException("Bounding-box rotation against z-axis");
    }

	/**
	 * returns the angle between the position vectors <tt>pVec1</tt>, 
	 * <tt>this</tt> (as apex), and <tt>pVec2</tt>. 
	 * <br />
	 * Note: <tt>pVec1</tt>, <tt>pVec2</tt> and <tt>this</tt> must be different
	 * from one another, otherwise the result will be 0 here.
	 * 
	 * @param pos1 Position 
	 * @param pos2 Position 
	 * @return Angle given in radians (in the range 0 ... <tt>Math.PI</tt>)
	 */
	public double angle(T3dVector pos1, T3dVector pos2)
	{   
   		T3dVector 
   			v10 = new T3dVector(),
   			v20 = new T3dVector(),
   			v21 = new T3dVector();
   		v10.assignDiff(pos1, this);
   		v20.assignDiff(pos2, this);
   		v21.assignDiff(pos2, pos1);
   		double 
   			l10 = v10.length(),
   			l20 = v20.length(),
   			l21 = v21.length();
   		if (l10 == 0. || l20 == 0. || l21 == 0.)
      		return 0.;
   		double cosPhi =
   				(l10 * l10 + l21 * l21 - l20 * l20) / (2. * l10 * l21);
   		return Math.acos(cosPhi);
	}
	
	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ")";
	}
}
