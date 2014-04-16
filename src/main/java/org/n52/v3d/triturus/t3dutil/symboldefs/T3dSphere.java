/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.t3dutil.symboldefs;

import org.n52.v3d.triturus.t3dutil.T3dSymbolDef;

/**
 * Definition of a sphere to be used as a (cartographic) symbol. To instantiate concrete symbols, the class
 * <tt>T3dSymbolInstance</tt> has to be used.
 *
 * @see org.n52.v3d.triturus.t3dutil.T3dSymbolInstance
 * @author Benno Schmidt
 */
public class T3dSphere extends T3dSymbolDef
{
	private double mRadius = 1.;

    /**
	 * Constructor. The radius of the sphere symbol will be set to 1.
	 */
    public T3dSphere() {
    }

    /**
	 * Constructor.
     *
	 * @param pRadius Radius
	 */
    public T3dSphere(double pRadius) {
        mRadius = mRadius;
    }

    /**
	 * sets the sphere's radius.
     *
	 * @param pRadius Radius
	 */
	public void setRadius(double pRadius) {
		mRadius = pRadius;
	}

	/**
	 * gets the sphere's radius.
     *
	 * @return Radius
	 */
	public double getRadius() {
		return mRadius;
	}
}