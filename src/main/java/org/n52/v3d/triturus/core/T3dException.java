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
package org.n52.v3d.triturus.core;

/**
 * Framework-specific exception.
 *
 * @see T3dExceptionMessage
 * @author Benno Schmidt
 */
public class T3dException extends RuntimeException
{
    private int mId = -1;

    /**
     * Constructor.<br />
     * Note: The error IDs will be used by the class <tt>T3dExceptionMessage</tt>.
     *
     * @param pMsg Error message
     * @param pErrId unique ID
     */
    public T3dException(String pMsg, int pErrId) {
        super(pMsg);
        mId = pErrId;
    }

	/**
	 * Constructor.
     *
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
     * returns the <tt>T3dException</tt>'s ID. If no ID is set, the method will return -1.
     *
     * @return Error ID
     */
    public int getId() {
        return mId;
    }
}