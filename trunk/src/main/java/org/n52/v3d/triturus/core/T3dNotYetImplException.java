package org.n52.v3d.triturus.core;

/**
 * Rahmenwerk-spezifische Ausnahme für (noch) nicht implementierte Funktionalität.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dNotYetImplException extends RuntimeException 
{
	/**
	 * Konstruktor.<p>
	 * @param pMsg Fehlermeldung
	 */
	public T3dNotYetImplException(String pMsg) {
		super("Sorry, the requested functionality isn't implemented yet - " + pMsg);
	}

	/** Konstruktor. */
	public T3dNotYetImplException() {
		super("Sorry, the requested functionality isn't implemented yet...");
	}
}