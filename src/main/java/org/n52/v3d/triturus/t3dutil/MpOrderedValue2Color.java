package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Zuordnung von Farbwerten zu ordinal skalierten thematischen Attributen.<p>
 * @author Benno Schmidt<br>
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
public class MpOrderedValue2Color extends MpValue2Color
{
    private String mLogString = "";

    /** Konstruktor. */
    public MpOrderedValue2Color() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgeführte Operation. */
    public String log() {
        return mLogString;
    }

    /** 
     * <b>TODO</b> 
     */
    public T3dColor transform(Object pValue) throws T3dException
    {
        return null;
    }
}