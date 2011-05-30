package org.n52.v3d.triturus.t3dutil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import org.n52.v3d.triturus.core.T3dException;

/**
 * Abstrakte Basisklasse zur Ermittlung hypsometrischer Farbwerte.<p>
 * @see org.n52.v3d.triturus.t3dutil.MpSimpleHypsometricColor
 * @see org.n52.v3d.triturus.t3dutil.MpGMTHypsometricColor
 * @author Benno Schmidt
 * (c) 2004, con terra GmbH & Institute for Geoinformatics<br>
 */
abstract public class MpHypsometricColor extends MpQuantitativeValue2Color
{
    /**
     * liefert die einem Höhenwert zugeordnete Farbe.<p>
     * @param pElevation Höhenwert
     * @return der Höhe zugewiesener Farbwert
     */
    abstract public T3dColor transform(double pElevation);
}