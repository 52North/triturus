package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Klasse zur Ermittlung hypsometrischer Farbwerte. Die einfach gehaltene Implementierung verwendet
 * voreinstellungsgemäß den klassischen Farbkeil für die Bundesrepublik Deutschland, durch den tiefe Lagen
 * dunkelgrün, mittlere Lagen gelb und hohe Lagen braun dargestellt werden. Die Stützwerte der Farbpalette können
 * über die Methode <tt>MpSimpleHypsometricColor#setPalette</tt> abgeändert werden, wobei die Farben zwischen den
 * Stützpunkten entweder im HSV-Farbraum linear interpoliert werden oder ein eintheitlicher Farbwert je definierter
 * Höhenklasse verwendet wird.<p>
 * Beispiel zur Verwendung:
 * <pre>
 * double elevs[] = {70., 120., 500., 900.};
 * T3dColor cols[] = {
 *     new T3dColor(0.0f, 0.8f, 0.0f), // Grün
 *     new T3dColor(1.0f, 1.0f, 0.5f), // Blassgelb
 *     new T3dColor(0.78f, 0.27f, 0.0f), // Braun
 *     new T3dColor(0.82f, 0.2f, 0.0f)}; // Rötlichbraun
 * colMapper.setPalette(elev, cols, true);
 * </pre>
 * @see org.n52.v3d.triturus.t3dutil.MpGMTHypsometricColor
 * @author Benno Schmidt
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
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
     * liefert die einem Höhenwert zugeordnete Farbe.<p>
     * @param pElevation Höhenwert
     * @return der Höhe zugewiesener Farbwert
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
                    // Farbe interpolieren und zurückgeben:
                    double lo = ((Double) lHeights.get(i - 1)).doubleValue();
                    float factor = (float)((pElevation - lo) / (hi - lo));
                    return this.interpolateHSV((T3dColor) lColors.get(i - 1), (T3dColor) lColors.get(i), factor);
                }
            }
        }
        return (T3dColor) lColors.get(lHeights.size() - 1);
    }

    /**
     * setzt eine Palette für die hypsometrischen Einfärbung. Die Höhe <i>pElevations[i]</i> wird dabei auf die
     * Farbe <i>pColors[i]</i> abgebildet. Abhängig vom angegebenen Modus wird zwischen den Farben im HSV-Farbraum
     * linear interpoliert oder der gesamten Klasse <i>pElevations[i] &lt; h &lt;= pElevations[i + 1]</tt> einheitlich
     * der Farbwert <i>pColors[i]><tt> zugeordnet.<p>
     * @param pElevations Feld mit Höhenwerten
     * @param pColors Feld mit zugehörigen Farbwerten
     * @param pInterpolMode <i>true</i> für Interpolation im HSV-Farbraum, <i>false</i> für einheitliche Klassen.
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
            new T3dColor(0.0f, 0.4f, 0.0f), // Dunkelgrün
            new T3dColor(0.0f, 0.8f, 0.0f), // Grün
            new T3dColor(1.0f, 1.0f, 0.5f), // Blassgelb
            new T3dColor(0.78f, 0.27f, 0.0f), // Braun
            new T3dColor(0.82f, 0.2f, 0.0f)}; // Rötlichbraun

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