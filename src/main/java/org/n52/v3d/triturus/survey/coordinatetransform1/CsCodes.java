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
package org.n52.v3d.triturus.survey.coordinatetransform1;

import org.n52.v3d.triturus.core.T3dException;

/**
 * @author Udo Einspanier
 */
public abstract class CsCodes {
    public final static String AUTHORITY_EPSG = "epsg";

    public final static String AUTHORITY_SEP = ":";

    public final static int EPSG_UNKNOWN = -1;

    public final static int EPSG_GCS_WGS84 = 4326;
    public final static int EPSG_PCS_UTM32 = 32632;
    public final static int EPSG_PCS_DHDN_GK1 = 31465;
    public final static int EPSG_PCS_DHDN_GK2 = 31466;
    public final static int EPSG_PCS_DHDN_GK3 = 31467;
    public final static int EPSG_PCS_DHDN_GK4 = 31468;
    public final static int EPSG_PCS_DHDN_GK5 = 31469;

    public final static String createId(int code) {
        return createId(AUTHORITY_EPSG, code);
    }

    public final static String createId(String authority, int code) {
        return authority + AUTHORITY_SEP + code;
    }

    public final static String extractAuthority(String srsId) {
        if (srsId == null) {
            return null;
        }
        int pos = srsId.indexOf(AUTHORITY_SEP);
        if (pos < 0) {
            return srsId;
        }
        return srsId.substring(0, pos);
    }

    public final static int extractCode(String srsId) {
        if (srsId == null) {
            return EPSG_UNKNOWN;
        }
        int pos = srsId.indexOf(AUTHORITY_SEP);
        if (pos > -1) {
            srsId = srsId.substring(pos + 1);
        }
        int code = EPSG_UNKNOWN;
        try {
            code = Integer.parseInt(srsId);
        }
        catch (Exception x) {
            throw new T3dException("Error extracting SRS code");
        }
        return code;
    }

}
