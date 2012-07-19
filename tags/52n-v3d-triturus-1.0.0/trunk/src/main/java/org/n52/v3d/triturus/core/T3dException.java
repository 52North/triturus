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
package org.n52.v3d.triturus.core;

/**
 * Framework-specific exception.<br /><br />
 * <i>German:</i> </i>Rahmenwerk-spezifische Ausnahme. <tt>T3dException</tt> ist eine Spezialisierung der Klassen
 * <tt>RuntimeException</tt> und <tt>Throwable</tt>.
 * @see T3dExceptionMessage
 * @author Benno Schmidt
 */
public class T3dException extends RuntimeException
{
    private int mId = -1;

    /**
     * Constructor.<br />
     * Note: The error IDs will be used by the class <tt>T3dExceptionMessage</tt>.<br /><br />
     * <i>German:</i> Bem.: Die Verwendung der Fehler-IDs erfolgt in der Klasse <tt>T3dExceptionMessage</tt>.
     * @param pMsg Error message
     * @param pErrId unique ID
     */
    public T3dException(String pMsg, int pErrId) {
        super(pMsg);
        mId = pErrId;
    }

	/**
	 * Constructor.<p>
	 * @param pMsg Error message
	 */
	public T3dException(String pMsg) {
		super(pMsg);
	}

	/**
     * @deprecated
     */
	public T3dException() {
		super();
	}

    /**
     * returns the <tt>T3dException</tt>'s ID. If not ID is set, the method will return -1.<br /><br />
     * <i>German:</i> liefert die ID der <tt>T3dException</tt>. Falls keine ID gesetzt ist, wird der Wert -1
     * zur&uuml;ckgegeben.
     * @return ID
     */
    public int getId() {
        return mId;
    }
}