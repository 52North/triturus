package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstract base class for mapper objects that assign color values to quantitative thematic attribute-values.
 * <br /><br />
 * <i>German:</i> Abstrakte Basisklasse für Mapper-Objekte, welche eine Zuordnung von Farbwerten zu quantitativ
 * skalierten thematischen Attributwerten ermöglichen.
 *
 * @author Benno Schmidt
 */
abstract public class MpQuantitativeValue2Color extends MpValue2Color
{
    private String mLogString = "";

    public String log() {
        return mLogString;
    }

    public MpQuantitativeValue2Color() {
        mLogString = this.getClass().getName();
    }

    /**
     * provides the color that is assigned to an attribute-value.
     *
     * @param pValue thematic attribute-value
     * @return Color assigned to the given value
     */
    abstract public T3dColor transform(Object pValue) throws T3dException;
}