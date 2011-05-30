package org.n52.v3d.triturus.vgis;

/**
 * Rahmenwerk-spezifische Ausnahme, die beim Auftreten inkompatibler räumlicher Bezugssysteme geworfen wird.<p>
 * @author Benno Schmidt<br>
 * (c) 2003, con terra GmbH & Institute for Geoinformatics<br>
 */
public class T3dSRSException extends RuntimeException 
{
	/**
	 * Konstruktor.<p>
	 * @param pMsg Fehlermeldung
	 */
	public T3dSRSException(String pMsg) {
		super(pMsg);
	}

	/** Konstruktor. */
	public T3dSRSException() {
		super();
	}
}