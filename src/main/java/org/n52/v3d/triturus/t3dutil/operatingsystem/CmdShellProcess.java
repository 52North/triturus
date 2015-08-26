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
package org.n52.v3d.triturus.t3dutil.operatingsystem;

import org.n52.v3d.triturus.core.T3dException;

import java.io.IOException;

/**
 * Helper class to perform command calls in the shell (command-line).<br /><br />
 * <i>German:</i> Hilfsklasse zur Aktivierung eines Befehlsaufrufs in der Kommandozeile. Die Befehlsabarbeitung erfolgt
 * in einem eigenen Thread und ist &uuml;ber den Aufruf der Methode <tt>CmdShellProcess#start</tt> anzusto&szlig;en.
 * Seitens der aufrufenden Anwendung l&auml;sst sich mittels <tt>CmdShellProcess#hasTerminated</tt> pr&uuml;fen, ob die
 * Ausf&uuml;hrung terminierte.
 * @author Benno Schmidt
 */
public class CmdShellProcess extends Thread
{
    private boolean mLocalDebug = false; // kann f�r Debug-Zwecke gesetzt werden

    private String mCmd;
    private boolean mTerminated;
    private boolean mImmediateTermination = false;

    private Process mP; // p ist global deklariert

    /**
     * Constructor.
     * @param pCommand Command to be executed
     */
    public CmdShellProcess(String pCommand) {
        mCmd = pCommand;
        mTerminated = false;
    }

    /**
     * starts command execution.<br /><br />
     * <i>German:</i> startet die Ausf&uuml;hrung der Kommandozeile. Nach Abarbeitung des Befehls wird das im
     * Konstruktor gesetzte Objekt &uuml;ber die Beendigung der Befehlsausf&uuml;hrung mittels <tt>Object#notify</tt>)
     * benachrichtigt.<br />
     * Bem.: F&uuml;r den Fall, dass die Befehlsausf&uuml;hrung nicht terminiert, empfiehlt es sich, in der aufrufenden
     * Anwendung eine Timeout-Dauer zu setzen.
     * @throws T3dException
     */
    public void run() throws T3dException
    {
        mTerminated = false;

        try {
            if (mLocalDebug)
                System.out.println("> Try to execute command \"" + mCmd + "\"...");

            mP = Runtime.getRuntime().exec(mCmd); // Befehl ausf�hren

            if (mImmediateTermination) {
                // Dieser Schalter wurde eingebaut, weil mP.waitFor() im Weiteren unter Linux (RedHat AS4) nicht lief;
                // es empfiehlt sich daher, den Schalter unter UNIX generell von au�en zu setzen... (Falls das auf
                // Dauer nicht greifen sollte, kann man noch einmal pr�fen, au�erhalb der Methode mit Thread.yield()
                // zu arbeiten!  
                mTerminated = true;
                return;
            }

            mP.waitFor(); // warten, bis Prozess beendet!

            if (mLocalDebug)
                System.out.println("> Termination of command execution (" + mCmd + ").");
            mTerminated = true;
        }
        catch (IOException e) {
            throw new T3dException("IOException while executing \"" + mCmd + "\":" + e.getMessage());
        }
        catch (InterruptedException e) {
            throw new T3dException("IOException while executing \"" + mCmd + "\":" + e.getMessage());
        }
    }

    /**
     * provides the information whether command-execution has finished.<br /><br />
     * <i>German:</i> liefert die Information, ob die Ausf�hrung der Kommandozeile beendet wurde.
     */
    public boolean hasTerminated() {
        return mTerminated;
    }

    /**
     * interrupts the running process.<br /><br />
     * <i>German:</i> unterbricht den laufenden Prozess.<br />
     * Bem.: Nach Aufruf dieses Befehls ist der Prozess m&ouml;glicherweise weiterhin aktiv, so dass er im Bedarfsfall
     * explizit &uuml;ber die Prozess-ID des Betriebssystems zu beenden ist.
     */
    public void interrupt() {
        if (mLocalDebug)
            System.out.println("> Try to interrupt command execution (" + mCmd + ").");
        mP.destroy();
    }

    /**
     * @deprecated
     * deaktiviert die <tt>Process#waitFor</tt>-Anweisung in der run()-Methode, falls als Argument der Wert <i>true</i>
     * angegeben wird. Dieser Schalter ist nur in Ausnahmef�llen zu setzen (z. B. f�r spezielle UNIX-Systeme). F�r die
     * Windows-Plattform sollte dieser Befehl <b>nicht</b> verwendet werden.<p>
     * @param pFlag <i>true</i> zur waitFor()-Deaktivierung (voreinstellungsgem�� ist <i>false</i> gesetzt)
     */
    public void setImmediateTermination(boolean pFlag) {
        mImmediateTermination = pFlag;
    }
}
