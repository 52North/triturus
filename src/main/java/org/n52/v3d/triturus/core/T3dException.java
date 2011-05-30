package org.n52.v3d.triturus.core;

/**
 * Rahmenwerk-spezifische Ausnahme.<p>
 * <tt>T3dException</tt> ist eine Spezialisierung der Klassen <tt>RuntimeException</tt> und <tt>Throwable</tt>.<p>
 * @see T3dExceptionMessage
 * @author Benno Schmidt<br>
 * (c) 2003-2005, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dException extends RuntimeException
{
    private int mId = -1;

    /**
     * Konstruktor.<p>
     * Bem.: Die Verwendung der Fehler-IDs erfolgt in der Klasse <tt>T3dExceptionMessage</tt>.<p> 
     * @param pMsg Fehlermeldung
     * @param pErrId eindeutige ID
     */
    public T3dException(String pMsg, int pErrId) {
        super(pMsg);
        mId = pErrId;
    }

	/**
	 * Konstruktor.<p>
	 * @param pMsg Fehlermeldung
	 */
	public T3dException(String pMsg) {
		super(pMsg);
	}

	/**
     * @deprecated
     * Konstruktor.
     */
	public T3dException() {
		super();
	}

    /**
     * liefert die ID der <tt>T3dException</tt>. Falls keine ID gesetzt ist, wird der Wert -1 zurückgegeben.<p>
     * @return ID
     */
    public int getId() {
        return mId;
    }
}