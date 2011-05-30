package org.n52.v3d.triturus.survey.conterra;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Overview :
 * Abstract :
 * @author  :   spanier
 * Date     :   19.08.2004
 * Time     :   17:32:24
 * Copyright:   Copyright (c) con terra GmbH
 * @link    :   www.conterra.de
 * @version :   0.1
 *
 * Revision :
 * @author  :   spanier
 * Date     :
 *
 */

// import ...

/**
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
