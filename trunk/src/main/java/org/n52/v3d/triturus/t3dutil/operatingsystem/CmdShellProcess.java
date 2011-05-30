package org.n52.v3d.triturus.t3dutil.operatingsystem;

import org.n52.v3d.triturus.core.T3dException;

import java.io.IOException;

/**
 * Hilfsklasse zur Aktivierung eines Befehlsaufrufs in der Kommandozeile. Die Befehlsabarbeitung erfolgt in
 * einem eigenen Thread und ist �ber den Aufruf der Methode <tt>CmdShellProcess#start</tt> anzusto�en. Seitens
 * der aufrufenden Anwendung l�sst sich mittels <tt>CmdShellProcess#hasTerminated</tt> pr�fen, ob die Ausf�hrung
 * terminierte.<p>
 * @author Benno Schmidt<br>
 * (c) 2004-2005, con terra GmbH<br>
 */
public class CmdShellProcess extends Thread
{
    private boolean mLocalDebug = false; // kann f�r Debug-Zwecke gesetzt werden

    private String mCmd;
    private boolean mTerminated;
    private boolean mImmediateTermination = false;

    private Process mP; // p ist global deklariert

    /**
     * Konstruktor.<p>
     * @param pCommand auszuf�hrender Befehl
     */
    public CmdShellProcess(String pCommand) {
        mCmd = pCommand;
        mTerminated = false;
    }

    /**
     * startet die Ausf�hrung der Kommandozeile. Nach Abarbeitung des Befehls wird das im Konstruktor gesetzte Objekt
     * �ber die Beendigung der Befehlsausf�hrung mittels <tt>Object#notify</tt>) benachrichtigt.<p>
     * Bem.: F�r den Fall, dass die Befehlsausf�hrung nicht terminiert, empfiehlt es sich, in der aufrufenden Anwendung
     * eine Timeout-Dauer zu setzen.<p>
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
     * liefert die Information, ob die Ausf�hrung der Kommandozeile beendet wurorg.n52.v3d.<p>
     */
    public boolean hasTerminated() {
        return mTerminated;
    }

    /**
     * unterbricht den laufenden Prozess.<p>
     * Bem.: Nach Aufruf dieses Befehls ist der Prozess m�glicherweise weiterhin aktiv, so dass er im Bedarfsfall
     * explizit �ber die Prozess-ID des Betriebssystems zu beenden ist<p>
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
