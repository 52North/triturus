package org.n52.v3d.triturus.t3dutil;

import org.n52.v3d.triturus.core.T3dException;

import java.util.ArrayList;

/**
 * Implementation for hypsometric color mappings.
 * <br /><br />
 * <i>German:</i> Klasse zur Ermittlung hypsometrischer Farbwerte. Die einfach gehaltene Implementierung verwendet
 * voreinstellungsgem�� den klassischen Farbkeil f�r die Bundesrepublik Deutschland, durch den tiefe Lagen
 * dunkelgr�n, mittlere Lagen gelb und hohe Lagen braun dargestellt werden. Die St�tzwerte der Farbpalette k�nnen
 * �ber die Methode <tt>MpSimpleHypsometricColor#setPalette</tt> abge�ndert werden, wobei die Farben zwischen den
 * St�tzpunkten entweder im HSV-Farbraum linear interpoliert werden oder ein eintheitlicher Farbwert je definierter
 * H�henklasse verwendet wird.<br />
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
 *
 * @see org.n52.v3d.triturus.t3dutil.MpGMTHypsometricColor
 * @author Benno Schmidt
 */
public class MpSimpleHypsometricColor extends MpHypsometricColor
{
    private ArrayList lHeights = new ArrayList();
    private ArrayList lColors = new ArrayList();
    private boolean mInterpolMode = true;

    public MpSimpleHypsometricColor() {
        this.setClassicalPalette();
    }

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

    public T3dColor transform(Object pValue) throws T3dException
    {
        if (pValue instanceof Double)
            return this.transform(((Double)pValue).doubleValue());
        else
            throw new T3dException("Could not map object value to hypsometric color.");
    }

    /**
     * sets the hypsometric color palette. Here, the elevation value <i>pElevations[i]</i> will be mapped to the color
     * <i>pColors[i]</i>. Dependent on the given interpolation mode, the mapper will interpolate between colors in
     * HSV color space (hue/saturation/value), or for the overall interval
     * <i>pElevations[i] &lt; h &lt;= pElevations[i + 1]</tt> a uniform color value <i>pColors[i]><tt> will be assigned.
     *
     * @param pElevations Array which holds elevation values
     * @param pColors Array whichs holds the corresponding color values
     * @param pInterpolMode <i>true</i> for interpolation in HSV color space, <i>false</i> for uniform classes.
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