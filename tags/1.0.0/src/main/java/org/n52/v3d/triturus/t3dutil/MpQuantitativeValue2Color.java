package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Klasse zur Zuordnung von Farbwerten zu quantitativ skalierten thematischen Attributen.
 * @author Benno Schmidt
 */
public class MpQuantitativeValue2Color extends MpValue2Color
{
    private String mLogString = "";

    /** Konstruktor. */
    public MpQuantitativeValue2Color() {
        mLogString = this.getClass().getName();
    }

    /** protokolliert die durchgef�hrte Operation. */
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