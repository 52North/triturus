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
package org.n52.v3d.triturus.vgis;

/**
 * Framework-specific exception, to be thrown if incompatible coordinate reference systems do not allow further
 * processing.
 * <br /><br />
 * <i>German: </i> Rahmenwerk-spezifische Ausnahme, die beim Auftreten inkompatibler r&auml;umlicher Bezugssysteme
 * geworfen wird.
 * @author Benno Schmidt
 */
public class T3dSRSException extends RuntimeException 
{
	/**
	 * Constructor
	 * @param pMsg Error message
	 */
	public T3dSRSException(String pMsg) {
		super(pMsg);
	}

    /**
     * Constructor
     */
	public T3dSRSException() {
		super();
	}
}