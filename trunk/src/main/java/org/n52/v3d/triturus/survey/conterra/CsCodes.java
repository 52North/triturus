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
package org.n52.v3d.triturus.survey.conterra;

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
