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

import org.n52.v3d.triturus.core.T3dProcFilter;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.core.T3dNotYetImplException;

/** 
 * Filter to transform an <tt>GmSimpleTINFeature</tt>-objects to a <tt>GmSimpleElevationGrid</tt>.
 * @author Benno Schmidt
 */
public class FltTIN2ElevationGrid extends T3dProcFilter
{
    private String mLogString = "";

    /**
     * Constructor.
     */
    public FltTIN2ElevationGrid() {
        mLogString = this.getClass().getName();
    }

    public String log() {
        return mLogString;
    }

    /**
     * performs the described filter operation.<br />
     * <i>Sorry, this method has not been implemented yet...</i>
     */
    public GmSimpleElevationGrid transform(GmSimpleTINFeature pTIN) throws T3dException
    {
    	throw new T3dNotYetImplException();
    }
}
