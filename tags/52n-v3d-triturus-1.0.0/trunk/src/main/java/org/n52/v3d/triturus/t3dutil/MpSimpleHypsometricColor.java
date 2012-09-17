package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Klasse zur Ermittlung hypsometrischer Farbwerte. Die einfach gehaltene Implementierung verwendet
 * voreinstellungsgem�� den klassischen Farbkeil f�r die Bundesrepublik Deutschland, durch den tiefe Lagen
 * dunkelgr�n, mittlere Lagen gelb und hohe Lagen braun dargestellt werden. Die St�tzwerte der Farbpalette k�nnen
 * �ber die Methode <tt>MpSimpleHypsometricColor#setPalette</tt> abge�ndert werden, wobei die Farben zwischen den
 * St�tzpunkten entweder im HSV-Farbraum linear interpoliert werden oder ein eintheitlicher Farbwert je definierter
 * H�henklasse verwendet wird.<p>
 * Beispiel zur Verwendung:
 * <pre>
 * double elevs[] = {70., 120., 500., 900.};
 * T3dColor cols[] = {
 *     new T3dColor(0.0f, 0.8f, 0.0f), // Gr�n
 *     new T3dColor(1.0f, 1.0f, 0.5f), // Blassgelb
 *     new T3dColor(0.78f, 0.27f, 0.0f), // Braun
 *     new T3dColor(0.82f, 0.2f, 0.0f)}; // R�tlichbraun
 * colMapper.setPalette(elev, cols, true);
 * </pre>
 * @see org.n52.v3d.triturus.t3dutil.MpGMTHypsometricColor
 * @author Benno Schmidt
 */
public class MpSimpleHypsometricColor extends MpHypsometricColor
{
    private ArrayList lHeights = new ArrayList();
    private ArrayList lColors = new ArrayList();
    private boolean mInterpolMode = true;

    /** Konstruktor. */
    public MpSimpleHypsometricColor() {
        this.setClassicalPalette();
    }

    /**
     * liefert die einem H�henwert zugeordnete Farbe.<p>
     * @param pElevation H�henwert
     * @return der H�he zugewiesener Farbwert
     */
    public T3dColor transform(double pElevation) throws T3dException
    {
        if (pElevation <= ((Double) lHeights.get(0)).doubleValue())
            return (T3dColor) lColors.get(0);
        for (int i = 1; i < lHeights.size(); i++) {
            double hi = ((Double) lHeights.get(i)).doubleValue();
            if (pElevation <= hi) {
                if (!this.getInterpolMode())
                    return (T3dColor) lColors.get(i - 1);
                else {
                    // Farbe interpolieren und zur�ckgeben:
                    double lo = ((Double) lHeights.get(i - 1)).doubleValue();
                    float factor = (float)((pElevation - lo) / (hi - lo));
                    return this.interpolateHSV((T3dColor) lColors.get(i - 1), (T3dColor) lColors.get(i), factor);
                }
            }
        }
        return (T3dColor) lColors.get(lHeights.size() - 1);
    }

    /**
     * setzt eine Palette f�r die hypsometrischen Einf�rbung. Die H�he <i>pElevations[i]</i> wird dabei auf die
     * Farbe <i>pColors[i]</i> abgebildet. Abh�ngig vom angegebenen Modus wird zwischen den Farben im HSV-Farbraum
     * linear interpoliert oder der gesamten Klasse <i>pElevations[i] &lt; h &lt;= pElevations[i + 1]</tt> einheitlich
     * der Farbwert <i>pColors[i]><tt> zugeordnet.<p>
     * @param pElevations Feld mit H�henwerten
     * @param pColors Feld mit zugeh�rigen Farbwerten
     * @param pInterpolMode <i>true</i> f�r Interpolation im HSV-Farbraum, <i>false</i> f�r einheitliche Klassen.
     */
    public void setPalette(double[] pElevations, T3dColor[] pColors, boolean pInterpolMode)
    {
        if (pElevations.length != pColors.length)
            throw new T3dException(
                "Illegal hypsometric color map specification ("+ pElevations.length + " != " + pColors.length + ".");

        lHeights.clear();
        lColors.clear();

        for (int i = 0; i < pElevations.length; i++) {
            lHeights.add(new Double(pElevations[i]));
            lColors.add(pColors[i]);
        }

        this.setInterpolMode(pInterpolMode);
    }

    public boolean getInterpolMode() {
        return mInterpolMode;
    }

    public void setInterpolMode(boolean pInterpolMode) {
        mInterpolMode = pInterpolMode;
    }

    private void setClassicalPalette()
    {
        double elevs[] = {-20., 0., 400., 1500., 3000.};
        T3dColor cols[] = {
            new T3dColor(0.0f, 0.4f, 0.0f), // Dunkelgr�n
            new T3dColor(0.0f, 0.8f, 0.0f), // Gr�n
            new T3dColor(1.0f, 1.0f, 0.5f), // Blassgelb
            new T3dColor(0.78f, 0.27f, 0.0f), // Braun
            new T3dColor(0.82f, 0.2f, 0.0f)}; // R�tlichbraun

        this.setPalette(elevs, cols, true);
    }

    private T3dColor interpolateHSV(T3dColor pColFrom, T3dColor pColTo, float pFactor)
    {
        float hue = pColFrom.getHue() + pFactor * (pColTo.getHue() - pColFrom.getHue());
        float sat = pColFrom.getSaturation() + pFactor * (pColTo.getSaturation() - pColFrom.getSaturation());
        float val = pColFrom.getValue() + pFactor * (pColTo.getValue() - pColFrom.getValue());
        float alf = pColFrom.getAlpha() + pFactor * (pColTo.getAlpha() - pColFrom.getAlpha());
        return new T3dColor("HSV", hue, sat, val, alf);
    }
}